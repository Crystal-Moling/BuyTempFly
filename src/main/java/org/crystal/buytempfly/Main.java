package org.crystal.buytempfly;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private Economy economy = null;
    @Override
    public void onEnable() {
        if(!initVault()){
            getLogger().info("vault插件挂钩失败，请检查是否安装了vault插件");
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("btf")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("这个指令只能让玩家使用。");
            } else {
                Player player = (Player) sender;
                if(economy.has(player, 100.0)) {
                    economy.withdrawPlayer(player, 100.0);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/tf give "+ player +" -h 1");
                } else {
                    player.sendMessage("金钱不足,赚钱去吧");
                }
            }
            return true;
        }
        return false;
    }
    private boolean initVault(){
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if(economyProvider == null) return false;
        else return true;
    }
}
