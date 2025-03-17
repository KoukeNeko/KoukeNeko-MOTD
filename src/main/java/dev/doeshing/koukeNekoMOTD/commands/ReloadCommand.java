package dev.doeshing.koukeNekoMOTD.commands;

import dev.doeshing.koukeNekoMOTD.KoukeNekoMOTD;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    private final KoukeNekoMOTD plugin;

    public ReloadCommand(KoukeNekoMOTD plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("koukenekomctd.reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getConfig().getString("prefix") + " &c你沒有權限重載插件！"));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            // 重載配置文件
            plugin.reloadConfig();
            // 重新初始化 MOTD 管理器
            plugin.reloadMotdManager();
            
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                    plugin.getConfig().getString("prefix") + " &a設定已重新載入！"));
            return true;
        }

        // 顯示幫助訊息
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', 
                plugin.getConfig().getString("prefix") + " &e指令用法: &f/koukemotd reload"));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
        }
        
        return completions;
    }
}
