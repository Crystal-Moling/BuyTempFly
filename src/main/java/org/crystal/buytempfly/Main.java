package org.crystal.buytempfly;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private net.milkbowl.vault.economy.Economy vault;
    private double prize = 100.0;

    public void onEnable() {
        if(!initVault()){
            getLogger().info("vault插件挂钩失败，请检查是否安装了vault插件");
        } else {
            getLogger().info("BuyTempFly插件加载成功!");
        }
    }

    private boolean initVault(){
        RegisteredServiceProvider<Economy> economyProvider
                = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if(economyProvider == null) return false;
        else return true;
    }

    private void command(String Command){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Command);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家能够使用该命令!");
        } else {
            if (cmd.getName().equalsIgnoreCase("btf")) {
                if(initVault()){
                    double bal = vault.getBalance(player);
                    if(vault.has(player, prize)){
                        command("eco take " + player.getName() + " " + prize);
                        command("tf give " + player.getName() + " -h 1");
                    }else{
                        player.sendMessage("余额不足,你现在有$" + bal + ",需要$" + prize);
                    }
                } else {
                    player.sendMessage("vault插件挂钩失败，请联系管理员解决问题");
                }
                return true;
            }
            return false;
        }
        return false;
    }
}