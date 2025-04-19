package dev.doeshing.koukeNekoMOTD;

import dev.doeshing.koukeNekoMOTD.commands.ReloadCommand;
import dev.doeshing.koukeNekoMOTD.core.CommandSystem;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class KoukeNekoMOTD extends JavaPlugin {

    private MotdManager motdManager;
    private CommandSystem commandSystem;

    @Override
    public void onEnable() {
        // 保存默認配置（如果不存在）
        saveDefaultConfig();
        
        // 初始化 MOTD 管理器
        initMotdManager();
        
        // 註冊指令
        commandSystem = new CommandSystem(this);
        commandSystem.registerCommand(
            "koukemotdreload", new ReloadCommand(
                this
                ),
            "重載插件配置",
            "koukenekomctd.reload",
                "knmotdreload"
        );

        // 顯示插件啟用訊息和支援的顏色格式
        getLogger().info("KoukeNekoMOTD 已啟用！");
        getLogger().info("支援傳統顏色代碼（&a, &b, &c 等）以及 HEX 顏色格式（&#RRGGBB）");
        
        // 檢查伺服器版本是否支援 HEX 顏色
        String version = getServer().getVersion();
        if (version.contains("1.16") || version.contains("1.17") || version.contains("1.18") || 
            version.contains("1.19") || version.contains("1.20") || version.contains("1.21")) {
            getLogger().info("已檢測到支援 HEX 顏色的伺服器版本: " + version);
        } else {
            getLogger().warning("當前伺服器版本可能不支援 HEX 顏色格式（需要 1.16+）: " + version);
        }
    }

    @Override
    public void onDisable() {
        if (motdManager != null) {
            motdManager.onDisable();
        }
        getLogger().info("KoukeNekoMOTD 已停用！");
    }
    
    /**
     * 初始化 MOTD 管理器
     */
    private void initMotdManager() {
        // 創建 MOTD 管理器
        motdManager = new MotdManager(this);
        
        // 註冊事件
        Bukkit.getPluginManager().registerEvents(motdManager, this);
    }
    
    /**
     * 重新載入 MOTD 管理器
     */
    public void reloadMotdManager() {
        // 停用當前管理器
        if (motdManager != null) {
            motdManager.onDisable();
            // 取消註冊事件（通過禁用和啟用插件）
            Bukkit.getPluginManager().disablePlugin(this);
            Bukkit.getPluginManager().enablePlugin(this);
        }
    }
}
