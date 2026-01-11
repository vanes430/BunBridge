<div align="center">

# ⚡ BunBridge 🥟

![Java](https://img.shields.io/badge/Java-17%2B-orange?style=for-the-badge&logo=openjdk)
![Platform](https://img.shields.io/badge/Platform-Spigot%20|%20Folia%20|%20Velocity-blue?style=for-the-badge&logo=minecraft)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**Run high-performance JavaScript & TypeScript apps natively within your Minecraft environment.**
<br>
*The speed of Bun, the flexibility of Minecraft.*

</div>

---

## 🚀 What is BunBridge?

**BunBridge** is a bridge plugin that brings the [Bun Runtime](https://bun.sh/)—the fast all-in-one JavaScript runtime—to your Minecraft server. It automates the installation, verification, and execution of Bun, allowing you to run modern scripts, web servers, and automation directly alongside your server.

### ✨ Key Features

*   **⚡ Blazing Fast Setup**: Run `/bunsetup` and the plugin will automatically fetch the correct binary for your OS (Linux, Windows, macOS) and architecture (x86_64, aarch64).
*   **🛡️ Secure Verification**: Every download is cross-referenced against official SHA256 checksums from the Bun repository.
*   **🌀 Folia & Velocity Ready**: Fully asynchronous execution. Running heavy scripts or web servers won't block your server's main thread or regions.
*   **📂 Rooted Environment**: Bun runs inside a dedicated `bun/` folder in your server root, keeping your file system organized.

---

## 🖥️ Commands

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/bunsetup` | `bunbridge.admin` | **Initializes** the Bun environment. <br>• Fetches latest release.<br>• Verifies integrity.<br>• Installs binary. |
| `/bun <args>` | `bunbridge.admin` | **Runs** any Bun command. <br>• Example: `/bun run index.ts`<br>• Example: `/bun -v` |

---

## 💡 Use Cases

*   **🌐 High-Performance Web APIs**: Run a backend for your server website or map using Bun's ultra-fast HTTP server.
*   **🤖 Integrated Discord Bots**: Host your Discord.js or Elysia-based bots directly within your Minecraft server process.
*   **📊 Data Processing**: Use TypeScript to process logs or player data with much higher performance than traditional script plugins.
*   **🔌 Cross-Platform Automation**: Trigger shell scripts or system tasks using JavaScript.

---

## 📦 Installation

1.  **Download** the plugin jar:
    *   `bunbridge-spigot-1.0.0.jar` (Spigot, Paper, Folia)
    *   `bunbridge-velocity-1.0.0.jar` (Velocity Proxy)
2.  **Drop** it into your `plugins` folder.
3.  **Start** the server.
4.  Run `/bunsetup` from the console or as an op.
5.  Start coding in your new `bun/` folder!

---

## 🏗️ Building from Source

```bash
git clone https://github.com/vanes430/BunBridge.git
cd BunBridge
mvn clean install
```

---

<div align="center">
Made with 🥟 for the modern Minecraft Admin
</div>
