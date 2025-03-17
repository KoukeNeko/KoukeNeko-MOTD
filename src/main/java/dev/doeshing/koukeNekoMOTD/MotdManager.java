package dev.doeshing.koukeNekoMOTD;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        
        // 處理顏色代碼
        return ChatColor.translateAlternateColorCodes('&', result);
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
