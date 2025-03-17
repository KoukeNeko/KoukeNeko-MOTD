package dev.doeshing.koukeNekoMOTD;

import dev.doeshing.koukeNekoMOTD.commands.ReloadCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class KoukeNekoMOTD extends JavaPlugin {

    private MotdManager motdManager;

    @Override
    public void onEnable() {
        // 保存默認配置（如果不存在）
        saveDefaultConfig();
        
        // 初始化 MOTD 管理器
        initMotdManager();
        
        // 註冊指令
        getCommand("koukemotd").setExecutor(new ReloadCommand(this));
        getCommand("koukemotd").setTabCompleter(new ReloadCommand(this));
        
        getLogger().info("KoukeNekoMOTD 已啟用！");
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
