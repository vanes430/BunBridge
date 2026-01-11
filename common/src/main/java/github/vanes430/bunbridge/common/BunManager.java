package github.vanes430.bunbridge.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BunManager {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/oven-sh/bun/releases/latest";
    private static final String SHASUMS_FILE = "SHASUMS256.txt";
    
    private final Path rootDir;
    private final BridgeLogger logger;

    public BunManager(Path rootDir, BridgeLogger logger) {
        this.rootDir = rootDir;
        this.logger = logger;
    }

    public void init() {
        try {
            if (!Files.exists(rootDir)) {
                Files.createDirectories(rootDir);
            }
        } catch (IOException e) {
            logger.severe("Failed to create root directory: " + e.getMessage());
        }
    }

    public void install() {
        logger.info("Starting Bun setup...");
        try {
            checkForUpdatesAndInstall();
        } catch (IOException e) {
            logger.severe("Installation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void execute(String[] args) {
        Path binaryPath = getBinaryPath();
        if (!Files.exists(binaryPath)) {
            logger.severe("Bun binary not found. Please run /bunsetup first.");
            return;
        }

        // Ensure executable on Unix
        if (PlatformUtils.getOS() != PlatformUtils.OS.WINDOWS) {
            binaryPath.toFile().setExecutable(true);
        }

        try {
            List<String> command = new ArrayList<>();
            command.add(binaryPath.toAbsolutePath().toString());
            // Add user arguments
            for (String arg : args) {
                command.add(arg);
            }

            logger.info("Executing: bun " + String.join(" ", args));
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(rootDir.toFile());
            
            Process p = pb.start();
            
            // Output handling
            inheritIO(p.getInputStream(), "INFO");
            inheritIO(p.getErrorStream(), "ERROR");
            
        } catch (IOException e) {
            logger.severe("Failed to execute bun: " + e.getMessage());
        }
    }

    private void checkForUpdatesAndInstall() throws IOException {
        logger.info("Checking for Bun updates...");
        String jsonResponse = BridgeUtils.fetchUrl(GITHUB_API_URL);
        JsonObject release = JsonParser.parseString(jsonResponse).getAsJsonObject();
        
        String assetName = getAssetName();
        Path zipPath = rootDir.resolve(assetName);
        Path binaryPath = getBinaryPath();
        
        // 1. Fetch SHA256 sums
        String shasumsUrl = null;
        for (JsonElement el : release.getAsJsonArray("assets")) {
            JsonObject asset = el.getAsJsonObject();
            if (asset.get("name").getAsString().equals(SHASUMS_FILE)) {
                shasumsUrl = asset.get("browser_download_url").getAsString();
                break;
            }
        }

        if (shasumsUrl == null) {
            throw new IOException("Could not find " + SHASUMS_FILE + " in release assets.");
        }

        String shasumsContent = BridgeUtils.fetchUrl(shasumsUrl);
        String expectedHash = extractHash(shasumsContent, assetName);
        
        if (expectedHash == null) {
            logger.warning("Could not find hash for " + assetName + ". Skipping verification.");
        }

        boolean downloadNeeded = true;

        // 2. Check existing zip or binary
        // Ideally we check the zip if we keep it, but we extract it.
        // So let's just check if binary exists. If verification is strict, we might want to re-download zip to verify.
        // For 'setup', let's force re-verify flow: download zip -> verify -> extract.
        
        // If zip exists, check its hash
        if (Files.exists(zipPath) && expectedHash != null) {
            try {
                String localHash = BridgeUtils.calculateSha256(zipPath);
                if (localHash.equalsIgnoreCase(expectedHash)) {
                    downloadNeeded = false;
                    logger.info("Valid local archive found.");
                } else {
                    logger.warning("Local archive hash mismatch. Re-downloading.");
                    Files.delete(zipPath);
                }
            } catch (Exception e) {
                Files.deleteIfExists(zipPath);
            }
        }

        if (downloadNeeded) {
            // Find asset URL
            String downloadUrl = null;
            for (JsonElement el : release.getAsJsonArray("assets")) {
                JsonObject asset = el.getAsJsonObject();
                if (asset.get("name").getAsString().equals(assetName)) {
                    downloadUrl = asset.get("browser_download_url").getAsString();
                    break;
                }
            }

            if (downloadUrl == null) {
                throw new IOException("Asset " + assetName + " not found.");
            }

            logger.info("Downloading " + assetName + "...");
            BridgeUtils.downloadFile(downloadUrl, zipPath);
            
            // Verify again
            if (expectedHash != null) {
                try {
                    String newHash = BridgeUtils.calculateSha256(zipPath);
                    if (!newHash.equalsIgnoreCase(expectedHash)) {
                        Files.delete(zipPath);
                        throw new IOException("Downloaded file hash mismatch!");
                    }
                    logger.info("Hash verified.");
                } catch (Exception e) {
                    throw new IOException("Hash check failed: " + e.getMessage());
                }
            }
        }

        // 3. Extract
        logger.info("Extracting...");
        extractZip(zipPath);
        
        if (Files.exists(binaryPath)) {
            logger.info("Bun setup complete! (" + binaryPath.getFileName() + ")");
            // Cleanup zip
            Files.deleteIfExists(zipPath);
        } else {
            throw new IOException("Extraction failed: binary not found at expected path.");
        }
    }

    private String extractHash(String content, String filename) {
        // Format: HASH  filename
        Pattern pattern = Pattern.compile("^([a-fA-F0-9]{64})\s+" + Pattern.quote(filename), Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private Path getBinaryPath() {
        String name = "bun";
        if (PlatformUtils.getOS() == PlatformUtils.OS.WINDOWS) {
            name += ".exe";
        }
        // Bun zips usually extract to a folder named like 'bun-linux-x64/bun'
        // We want to flatten it to rootDir/bun
        return rootDir.resolve(name);
    }

    private String getAssetName() {
        PlatformUtils.OS os = PlatformUtils.getOS();
        PlatformUtils.Arch arch = PlatformUtils.getArch();

        if (os == PlatformUtils.OS.WINDOWS && arch == PlatformUtils.Arch.AMD64) {
            return "bun-windows-x64.zip";
        } else if (os == PlatformUtils.OS.LINUX) {
            if (arch == PlatformUtils.Arch.AMD64) return "bun-linux-x64.zip";
            if (arch == PlatformUtils.Arch.ARM64) return "bun-linux-aarch64.zip";
        } else if (os == PlatformUtils.OS.MACOS) {
            if (arch == PlatformUtils.Arch.AMD64) return "bun-darwin-x64.zip";
            if (arch == PlatformUtils.Arch.ARM64) return "bun-darwin-aarch64.zip";
        }
        
        return "bun-linux-x64.zip"; // Fallback
    }

    private void extractZip(Path zipFile) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile.toFile())) {
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                String name = entry.getName();
                
                // We only care about the binary 'bun' or 'bun.exe'
                // Entries are often "bun-linux-x64/bun"
                File file = new File(name);
                String fileName = file.getName(); // gets 'bun' from 'dir/bun'
                
                if (fileName.equals("bun") || fileName.equals("bun.exe")) {
                    Path targetPath = rootDir.resolve(fileName);
                    try (InputStream in = zip.getInputStream(entry)) {
                        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    if (PlatformUtils.getOS() != PlatformUtils.OS.WINDOWS) {
                        targetPath.toFile().setExecutable(true);
                    }
                }
            }
        }
    }

    private void inheritIO(InputStream src, String level) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(src))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("§7[Bun] " + line);
                }
            } catch (IOException e) {
                // Ignore
            }
        }).start();
    }
}
