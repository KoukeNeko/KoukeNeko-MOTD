# KoukeNeko-MOTD Plugin

## 🌟 Introduction

KoukeNeko-MOTD is a powerful Minecraft plugin that enhances your server's Message of The Day (MOTD) system. It provides customized server list display messages and player welcome messages with dynamic content using PlaceholderAPI.

## 🔧 Features

* ✅ Customizable server list MOTD with different messages for new and returning players
* ✅ Hover information when players view your server in the server list
* ✅ Personalized join messages for players when they connect to the server
* ✅ Full PlaceholderAPI support for dynamic content
* ✅ Simple and clean configuration system
* ✅ In-game reload command for easy updates
* ✅ **NEW!** HEX color code support (&#RRGGBB format) for Minecraft 1.16+

## 📂 Installation

1. Place the plugin (KoukeNeko-MOTD.jar) into your server's plugins folder.
2. Restart your server to activate the plugin.
3. The plugin will generate a default configuration file.

## ⚙️ Configuration

Example configuration (config.yml):

```yaml
# 插件前綴，用於顯示訊息
prefix: "&7[&#42c8f5&l🕹️&7]&f"

# 伺服器列表 MOTD 設定
server_list_motd:
  enabled: true
  # 預設 MOTD (新玩家)
  Default:
    Description:
      - "&#ffa500⚒ &f麻糬咕嚕嚕呼嚕嚕 &#ffa500⚒ &8| &#ffdd00現實生存伺服器"
      - "&7▸ &f季節: &#00cc44%rs_season_world% &8• &f挑戰自然環境 &8[&#00ff55%server_online%&7/&#ff5555%server_max_players%&8]"
    Players:
      Hover:
        - "&#ffa500伺服器狀態:"
        - "&7線上玩家: &#00ff55%server_online% &7/ &#ff5555%server_max_players%"
        - "&7TPS: &#00ff55%server_tps_1%"
  # 回歸玩家 MOTD (曾經連線過的 IP)
  Personalized:
    Description:
      - "&#ffa500⚒ &f麻糬咕嚕嚕呼嚕嚕 &#ffa500⚒ &8| &#ffdd00現實生存伺服器"
      - "&7▸ &f歡迎回來 &#ffcc00%player_displayname% &8• &f現實生存等待挑戰 &8[&#00ff55%server_online%&7/&#ff5555%server_max_players%&8]"
    Players:
      Hover:
        - "&#ffa500伺服器狀態:"
        - "&7線上玩家: &#00ff55%server_online% &7/ &#ff5555%server_max_players%"
        - "&7目前季節: &#00ff55%rs_season_world%"

# 玩家加入 MOTD 設定
player_join_motd:
  enabled: true
  motd:
    - ""
    - "&#ffa500⚒ &f麻糬咕嚕嚕呼嚕嚕現實生存 &#ffa500⚒"
    - ""
    - "&7• &f線上人數: &#ffdd00%online%&7/&#ffdd00%max% &7| &fTPS: &#00cc44%tps% &7| &fPing: &#00cc44%ping%ms"
    - "&7• &f目前世界: &#ffdd00%world% &7| &f目前季節: &#00cc44%rs_season%"
    - ""
```

## 🎨 Color Code Support

The plugin now supports both traditional color codes and HEX color codes:

* **Traditional color codes**: `&a`, `&b`, `&c`, etc.
* **HEX color codes**: `&#RRGGBB` format (e.g., `&#FF5733` for orange-red, `&#4287f5` for light blue)

HEX color codes require Minecraft 1.16 or higher. The plugin will automatically detect server version and display appropriate warnings if HEX colors aren't supported.

## 🚀 Commands

* `/koukemotd reload` (Permission: koukenekomctd.reload)
  + Reloads the plugin's configuration.

## 🔑 Permissions

| Permission | Description |
| --- | --- |
| koukenekomctd.reload | Allows use of the reload command |

## 🎮 How It Works

### Server List MOTD

1. When a player views your server in the server list, the plugin shows a customized MOTD.
2. New players (never connected before) see the "Default" MOTD.
3. Returning players (previously connected) see the "Personalized" MOTD.
4. Hovering over the player count shows custom hover text with server information.

### Player Join MOTD

1. When a player joins the server, they receive a custom welcome message.
2. The message can include dynamic content like player stats, server info, etc.
3. All messages support PlaceholderAPI for maximum customization.

## 🔌 Dependencies

* **Required**: Paper/Spigot 1.16.5+ (for full HEX color support)
* **Optional**: PlaceholderAPI (for expanded placeholder support)

## 🌟 簡介

KoukeNeko-MOTD 是一款功能強大的 Minecraft 插件，可增強你伺服器的每日訊息 (MOTD) 系統。它提供自訂的伺服器列表顯示訊息和玩家歡迎訊息，並使用 PlaceholderAPI 來顯示動態內容。

## 🔧 功能特色

* ✅ 可自訂伺服器列表 MOTD，為新玩家和回歸玩家顯示不同訊息
* ✅ 當玩家在伺服器列表中查看你的伺服器時，提供懸停資訊
* ✅ 當玩家連接到伺服器時，發送個人化的加入訊息
* ✅ 完整支援 PlaceholderAPI 實現動態內容
* ✅ 簡潔明了的配置系統
* ✅ 遊戲內重載指令，方便更新設定
* ✅ **新功能!** 支援 HEX 顏色代碼 (&#RRGGBB 格式)，適用於 Minecraft 1.16+

## 📂 安裝方法

1. 將插件 (KoukeNeko-MOTD.jar) 放入伺服器的 plugins 資料夾。
2. 重新啟動伺服器以啟用插件。
3. 插件將會產生預設設定檔。

## 🎨 顏色代碼支援

插件現在支援傳統顏色代碼和 HEX 顏色代碼：

* **傳統顏色代碼**：`&a`、`&b`、`&c` 等
* **HEX 顏色代碼**：`&#RRGGBB` 格式（例如，`&#FF5733` 為橙紅色，`&#4287f5` 為淺藍色）

HEX 顏色代碼需要 Minecraft 1.16 或更高版本。插件會自動檢測伺服器版本，如果不支援 HEX 顏色，將顯示適當的警告。

## 🚀 指令

* `/koukemotd reload` (權限: koukenekomctd.reload)
  + 重新載入插件的設定檔。

## 🔑 許可權

| 權限 | 說明 |
| --- | --- |
| koukenekomctd.reload | 允許使用重新載入指令 |

## 🎮 運作方式

### 伺服器列表 MOTD

1. 當玩家在伺服器列表中查看你的伺服器時，插件會顯示自訂的 MOTD。
2. 新玩家（從未連接過）會看到「Default」MOTD。
3. 回歸玩家（之前已連接過）會看到「Personalized」MOTD。
4. 懸停在玩家數量上會顯示包含伺服器資訊的自訂懸停文字。

### 玩家加入 MOTD

1. 當玩家加入伺服器時，他們會收到自訂的歡迎訊息。
2. 訊息可以包含動態內容，如玩家統計資料、伺服器資訊等。
3. 所有訊息都支援 PlaceholderAPI 以實現最大程度的自訂。

## 🔌 相依性

* **必要**: Paper/Spigot 1.16.5+ (完整 HEX 顏色支援)
* **可選**: PlaceholderAPI (用於擴充佔位符支援)

🚀 Enjoy your game! 祝你遊戲愉快!
