package org.crystal.buytempfly;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public boolean def;
    public int FlyD;
    public int FlyH;
    public int FlyM;
    public int FlyS;
    public int prize;

    private static net.milkbowl.vault.economy.Economy vault = null;

    public void onEnable() {
        if(!initVault()){
            getLogger().info("§b vault插件挂钩失败，请检查是否安装了vault插件");
        } else if (!setupEconomy()){
            getLogger().info("§b vault插件加载失败，请检查是否安装了vault插件");
        } else {
            InitConfig();
            getLogger().info("§b BuyTempFly插件加载成功!");
        }
    }

    public void InitConfig(){
        def = this.getConfig().getBoolean("default");
        FlyD = this.getConfig().getInt("flyTime.day");
        FlyH = this.getConfig().getInt("flyTime.hour");
        FlyM = this.getConfig().getInt("flyTime.min");
        FlyS = this.getConfig().getInt("flyTime.sec");
        prize = this.getConfig().getInt("flyPrize");
        if(def){
            this.saveDefaultConfig();
            getLogger().info("§b 初次启动，请修改config.yml!");
        }
    }
    private boolean initVault(){
        RegisteredServiceProvider<Economy> economyProvider
                = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if(economyProvider == null) return false;
        else return true;
    }
    private boolean setupEconomy() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider;
        try {
            economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        } catch (Exception e) {
            return false;
        }

        if (economyProvider != null) {
            this.vault = economyProvider.getProvider();
        }

        if (this.vault == null) {
            return false;
        }

        if (this.vault.getName() == null || this.vault.getName().isEmpty()) {

                    getLogger()
                    .warning(
                            "Current economy plugin not correct process all request, this usually cause by irregular code, you should report this issue to your economy plugin author or use other economy plugin.");

                    getLogger()
                    .warning(
                            "This is technical information, please send this to economy plugin author: "
                                    + "VaultEconomyProvider.getName() return a null or empty.");
        } else {
            getLogger().info("Using economy system: " + this.vault.getName());
        }
        return true;
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
                        command("tf give " + player.getName() + " -d " + FlyD + " -h " + FlyH + " -m " + FlyM + " -s " + FlyS);
                    }else{
                        player.sendMessage("§a 余额不足,你现在有$" + bal + ",需要$" + prize);
                    }
                } else {
                    player.sendMessage("§b vault插件挂钩失败，请联系管理员解决问题");
                }
                return true;
            }
            return false;
        }
        return false;
    }
}