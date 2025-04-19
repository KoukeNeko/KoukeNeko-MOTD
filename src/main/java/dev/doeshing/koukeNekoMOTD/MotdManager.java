package dev.doeshing.koukeNekoMOTD;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * MOTD 系統管理器
 *
 * 此類用於區分「伺服器列表 MOTD」與「玩家加入 MOTD」，並支援 PlaceholderAPI 顯示動態資料（例如玩家數量、TPS、玩家名稱等）。
 * 採用 Paper 內建的 PaperServerListPingEvent 處理伺服器列表 MOTD，不依賴 ProtocolLib。
 */
public class MotdManager implements Listener {

    // HEX 顏色代碼 Pattern (匹配 &#RRGGBB 格式)
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private final Map<InetAddress, String> lastPlayerNameByIp = new ConcurrentHashMap<>();

    private String serverListMotdNew;       // Default 的 Description (新玩家)
    private String serverListMotdReturning; // Personalized 的 Description (回歸玩家)

    private List<String> serverListHoverNew;       // Default 的 Hover
    private List<String> serverListHoverReturning; // Personalized 的 Hover

    private String joinMotd;

    private boolean serverListEnabled = true;
    private boolean playerJoinEnabled = true;

    private final KoukeNekoMOTD plugin;

    public MotdManager(KoukeNekoMOTD plugin) {
        this.plugin = plugin;
        loadConfigValues(plugin.getConfig());
    }

    public void onDisable() {
        lastPlayerNameByIp.clear();
    }

    @EventHandler
    public void onPaperPing(PaperServerListPingEvent event) {
        if (!serverListEnabled) {
            plugin.getLogger().info("Server list MOTD is disabled.");
            return; // 如果伺服器列表 MOTD 未啟用，則不處理
        }
        InetAddress address = event.getAddress();
        String motdTemplate;
        List<String> hoverLines;
        if (address != null && lastPlayerNameByIp.containsKey(address)) {
            motdTemplate = serverListMotdReturning; // 回歸玩家
            hoverLines = serverListHoverReturning;
        } else {
            motdTemplate = serverListMotdNew;       // 新玩家
            hoverLines = serverListHoverNew;
        }
        if (motdTemplate == null || motdTemplate.isEmpty()) {
            plugin.getLogger().warning("MOTD 樣板為空，無法設定 MOTD 訊息！");
            return;
        }
        // 處理 MOTD
        String motdText = processPlaceholdersAndColor(motdTemplate, address);
        event.setMotd(motdText);

        // 處理 Hover 文字：先合併為一個字串，再用換行符拆分
        if (hoverLines != null && !hoverLines.isEmpty()) {
            String combinedHover = String.join("\n", hoverLines);
            String[] splitHover = combinedHover.split("\\n");
            List<PaperServerListPingEvent.ListedPlayerInfo> listedPlayers = event.getListedPlayers();
            listedPlayers.clear();
            for (String line : splitHover) {
                String hoverText = processPlaceholdersAndColor(line, address);
                listedPlayers.add(new PaperServerListPingEvent.ListedPlayerInfo(hoverText, UUID.randomUUID()));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!playerJoinEnabled) {
            plugin.getLogger().info("Player join MOTD is disabled.");
            return; // 玩家加入 MOTD 未啟用，則不處理
        }
        Player player = event.getPlayer();
        InetAddress address = (player.getAddress() != null) ? player.getAddress().getAddress() : null;
        if (joinMotd != null && !joinMotd.isEmpty()) {
            String joinMessage = processPlaceholdersAndColor(joinMotd, null, player);
            player.sendMessage(joinMessage);
        }
        if (address != null) {
            lastPlayerNameByIp.put(address, player.getName());
        }
    }

    private String processPlaceholdersAndColor(String text, InetAddress address) {
        return processPlaceholdersAndColor(text, address, null);
    }

    private String processPlaceholdersAndColor(String text, InetAddress address, Player player) {
        String result = text;
        
        // 檢查是否安裝 PlaceholderAPI 並處理佔位符
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                
                if (address != null && lastPlayerNameByIp.containsKey(address)) {
                    String pName = lastPlayerNameByIp.get(address);
                    result = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(pName), result);
                } else if (player != null) {
                    result = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, result);
                } else {
                    result = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(null, result);
                }
            } catch (ClassNotFoundException e) {
                plugin.getLogger().warning("PlaceholderAPI found but could not be loaded properly.");
            }
        } else {
            // 基本佔位符處理（無需 PlaceholderAPI）
            if (address != null && lastPlayerNameByIp.containsKey(address)) {
                result = result.replace("%player_name%", lastPlayerNameByIp.get(address));
            }
            result = result.replace("%server_online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
            result = result.replace("%server_max_players%", String.valueOf(Bukkit.getMaxPlayers()));
            
            // 如果有 Player 對象，處理玩家相關佔位符
            if (player != null) {
                result = result.replace("%player%", player.getName());
                result = result.replace("%player_displayname%", player.getDisplayName());
                result = result.replace("%world%", player.getWorld().getName());
                result = result.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
                result = result.replace("%max%", String.valueOf(Bukkit.getMaxPlayers()));
                result = result.replace("%ping%", String.valueOf(player.getPing()));
            }
        }
        
        // 處理 HEX 顏色代碼 (&#RRGGBB 格式)
        result = processHexColors(result);
        
        // 處理傳統顏色代碼
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    /**
     * 處理 HEX 顏色代碼
     * 將 &#RRGGBB 格式轉換為 §x§R§R§G§G§B§B 格式
     *
     * @param text 包含 HEX 顏色代碼的文字
     * @return 轉換後的文字
     */
    private String processHexColors(String text) {
        if (text == null) {
            return "";
        }
        
        // 早期版本可能不支援 HEX 色碼，先檢查伺服器版本
        // 如果伺服器版本低於 1.16，則不進行處理
        try {
            String version = Bukkit.getServer().getVersion();
            if (version.contains("1.8") || version.contains("1.9") || 
                version.contains("1.10") || version.contains("1.11") || 
                version.contains("1.12") || version.contains("1.13") || 
                version.contains("1.14") || version.contains("1.15")) {
                plugin.getLogger().warning("HEX 顏色代碼在 1.16 以下的版本不受支援！");
                return text;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("檢查伺服器版本時發生錯誤: " + e.getMessage());
        }
        
        // 使用 Matcher 搜尋並替換所有 HEX 顏色代碼
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            String replacement = translateHexColorCodes(hexCode);
            matcher.appendReplacement(buffer, replacement);
        }
        
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    
    /**
     * 將 HEX 顏色代碼轉換為 Minecraft 格式
     *
     * @param hexCode HEX 顏色代碼 (不含 # 號)
     * @return Minecraft 格式顏色代碼
     */
    private String translateHexColorCodes(String hexCode) {
        StringBuilder result = new StringBuilder("§x");
        for (char c : hexCode.toCharArray()) {
            result.append("§").append(c);
        }
        return result.toString();
    }
    
    private void loadConfigValues(FileConfiguration config) {
        // 讀取 server_list_motd 設定
        ConfigurationSection slSection = config.getConfigurationSection("server_list_motd");
        if (slSection != null) {
            serverListEnabled = slSection.getBoolean("enabled", true);
            // 讀取 Default (新玩家)
            ConfigurationSection defSection = slSection.getConfigurationSection("Default");
            if (defSection != null) {
                List<String> defDesc = defSection.getStringList("Description");
                if (defDesc != null && !defDesc.isEmpty()) {
                    serverListMotdNew = String.join("\n", defDesc);
                }
                ConfigurationSection defPlayers = defSection.getConfigurationSection("Players");
                if (defPlayers != null) {
                    List<String> defHover = defPlayers.getStringList("Hover");
                    if (defHover != null && !defHover.isEmpty()) {
                        serverListHoverNew = defHover;
                    }
                }
            }
            // 讀取 Personalized (回歸玩家)
            ConfigurationSection perSection = slSection.getConfigurationSection("Personalized");
            if (perSection != null) {
                List<String> perDesc = perSection.getStringList("Description");
                if (perDesc != null && !perDesc.isEmpty()) {
                    serverListMotdReturning = String.join("\n", perDesc);
                }
                ConfigurationSection perPlayers = perSection.getConfigurationSection("Players");
                if (perPlayers != null) {
                    List<String> perHover = perPlayers.getStringList("Hover");
                    if (perHover != null && !perHover.isEmpty()) {
                        serverListHoverReturning = perHover;
                    }
                }
            }
        } else {
            plugin.getLogger().warning("找不到 server_list_motd 設定節點！");
        }

        // 讀取 player_join_motd 設定
        ConfigurationSection pjSection = config.getConfigurationSection("player_join_motd");
        if (pjSection != null) {
            playerJoinEnabled = pjSection.getBoolean("enabled", true);
            if (playerJoinEnabled) {
                List<String> joinList = pjSection.getStringList("motd");
                if (joinList != null && !joinList.isEmpty()) {
                    joinMotd = String.join("\n", joinList);
                }
            }
        }
    }
}
