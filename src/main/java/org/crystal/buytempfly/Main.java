package org.crystal.buytempfly;

import com.moneybags.tempfly.TempFly;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

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
            getLogger().info("§b vault插件挂钩失败，请检查是否安装了vault插件及EssentialsX插件");
        } else if (!setupEconomy()){
            getLogger().info("§b vault插件加载失败，请检查是否安装了vault插件及EssentialsX插件");
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
    }  //初始化配置
    private boolean initVault(){
        RegisteredServiceProvider<Economy> economyProvider
                = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if(economyProvider == null) return false;
        else return true;
    }  //初始化Vault插件
    private UUID getPlayerUUID(Player player){
        String fullPlayer = "OfflinePlayer:" + player.getName();
        return UUID.nameUUIDFromBytes(fullPlayer.getBytes());
    }  //初始化TempFly插件
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

    private boolean command(String Command){
        return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Command);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (!(sender instanceof Player)) {  //判断执行者是否为玩家
            sender.sendMessage("只有玩家能够使用该命令!");
        } else {
            if (cmd.getName().equalsIgnoreCase("btf")) {
                if(initVault()){
                    double bal = vault.getBalance(player);  //获取玩家余额
                    if(vault.has(player, prize)){  //判断玩家是否有足够金钱
                        if (TempFly.getAPI().addFlightTime(getPlayerUUID(player), FlyS) == true){
                            vault.withdrawPlayer(player, prize);  //扣钱
                        } else {
                            player.sendMessage("§a 余额不足,你现在有$" + bal + ",需要$" + prize);
                        }
                    }else{
                        player.sendMessage("§a 飞行时间添加失败!");
                    }
                } else {
                    getLogger().info("§b vault插件挂钩失败，请联系管理员解决问题");
                }
                return true;
            }
            return false;
        }
        return false;
    }
}