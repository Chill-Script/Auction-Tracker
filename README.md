# 📊 Auction Tracker (v1.1)

![Version](https://img.shields.io/badge/version-1.1-blue.svg)
![Platform](https://img.shields.io/badge/platform-Fabric-lightgrey.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.21.x-green.svg)

**Never be in the dark about an item's value.** Auction Tracker is a lightweight utility mod for Minecraft Fabric that acts as a historical record for your server's economy. By automatically recording market data as you browse, it provides a deep history of every item's worth, allowing you to make informed decisions based on real data rather than memory.

---

## ● Key Features

* **💎 Price Transparency:** Instantly see what an item is worth by viewing its historical low and high prices directly on your item tooltips.
* **💾 Persistent Database:** Your market knowledge stays with you. All recorded prices are saved to `config/auction_trends.json` and persist across game restarts.
* **🛡️ Stealth Scanning:** Uses a **per-tick** iteration method (scanning 1 slot every 1/20th of a second) with randomized page delays to remain server-friendly and stable.
* **🧠 Intelligent Tooltips:** Item tooltips update dynamically to show:
    * **Lowest & Highest** prices ever recorded for that specific item.
    * **Quantity** available during your most recent scan.
    * **Timestamps** for exactly when that item type was last seen on the market.
* **🧹 Data Management:** Maintain a clean database with the `/at reset` command to wipe history whenever the economy shifts.

---

## ● Installation

1.  **Requirements:**
    * Minecraft **1.21.x**
    * Fabric Loader **>=0.15.11**
    * Fabric API
2.  **Setup:**
    * Download the latest release `.jar`.
    * Place the file into your Minecraft `mods` folder.
    * Launch the game; the mod will automatically generate the necessary configuration files.

---

## ● Quick Start Guide

### 1. Initiating Your First Scan
Open the Auction House menu (usually `/ah`) and navigate to the "All Items" category. The bot will automatically begin scanning each slot. **Do not close the menu** until you see the completion message in chat.

### 2. Reading Tooltips
Hover over any item in your inventory. You will see:
* **Market History:** The floor and ceiling prices recorded for that item.
* **Live Status:** How many of that item were found in the last full scan.
* **Timestamps:** When the market was last updated globally and when that specific item was last seen.

### 3. Maintenance
If the server economy resets or changes drastically, use the command below to clear your data:
* `/at reset` — Wipes all historical data and clears the JSON storage file.

---

## ● Technical Overview

| Module | Responsibility |
| :--- | :--- |
| **DataStorage** | Handles JSON serialization and historical price calculations. |
| **AuctionBot** | Manages the automated page-turning and per-tick scanning logic. |
| **ItemStackMixin** | Injects the data display into the vanilla Minecraft tooltip system. |
| **AuctionScanner** | Parses item lore and fingerprints items via translation keys. |

---

## ● License

Distributed under the MIT License. See `LICENSE` for more information.

---
*Note: This mod is intended for educational purposes and personal data tracking. Always check your server's rules regarding automation tools.*
