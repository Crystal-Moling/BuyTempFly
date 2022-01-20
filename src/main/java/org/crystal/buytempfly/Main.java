package org.crystal.buytempfly;

import com.moneybags.tempfly.TempFly;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Main extends JavaPlugin {
    public int FlyMainSec;
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
    public void onDisable(){
        this.saveConfig();
    }

    public int GetIntConfig(String ConfigName){
        return this.getConfig().getInt(ConfigName);
    }//获取配置文件子程序
    private UUID getPlayerUUID(Player player){
        String fullPlayer = "OfflinePlayer:" + player.getName();
        return UUID.nameUUIDFromBytes(fullPlayer.getBytes());
    }  //计算UUID子程序

    public void InitConfig(){
        FlyMainSec = GetIntConfig("flyTime.sec") 
                    + (GetIntConfig("flyTime.min") * 60) 
                    + (GetIntConfig("flyTime.hour") * 60 * 60)
                    + (GetIntConfig("flyTime.day") * 24 * 60 * 60);
        prize = GetIntConfig("flyPrize");
    }  //初始化配置
    private boolean initVault(){
        RegisteredServiceProvider<Economy> economyProvider
                = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        return economyProvider != null;
    }  //初始化Vault插件
    private boolean setupEconomy() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider;
        try {
            economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        } catch (Exception e) {
            return false;
        }
        if (economyProvider != null) {
            vault = economyProvider.getProvider();
        }
        if (vault == null) {
            return false;
        }
        if (vault.getName() == null || vault.getName().isEmpty()) {

                    getLogger()
                    .warning(
                            "Current economy plugin not correct process all request, this usually cause by irregular code, you should report this issue to your economy plugin author or use other economy plugin.");

                    getLogger()
                    .warning(
                            "This is technical information, please send this to economy plugin author: "
                                    + "VaultEconomyProvider.getName() return a null or empty.");
        } else {
            getLogger().info("Using economy system: " + vault.getName());
        }
        return true;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        UUID PUUID = getPlayerUUID(player);
        if (!(sender instanceof Player)) {  //判断执行者是否为玩家
            sender.sendMessage("只有玩家能够使用该命令!");
        } else {
            if (cmd.getName().equalsIgnoreCase("btf")) {
                if(initVault()){
                    double bal = vault.getBalance(player);  //获取玩家余额
                    if(vault.has(player, prize)){  //判断玩家是否有足够金钱
                        double OriginalFlytime = TempFly.getAPI().getFlightTime(PUUID);//获取玩家当前飞行时间
                        double TargetFlyTime = OriginalFlytime + FlyMainSec;//计算玩家应有的飞行时间
                        TempFly.getAPI().addFlightTime(PUUID, FlyMainSec);//尝试添加飞行时间
                        if(TempFly.getAPI().getFlightTime(PUUID) == TargetFlyTime){  //判断实际飞行时间是否等于计算值
                            vault.withdrawPlayer(player, prize);  //扣除金钱
                        } else {  //如果不等于
                            TempFly.getAPI().setFlightTime(PUUID,OriginalFlytime);//将玩家的飞行时间还原
                            player.sendMessage("§a 飞行时间添加失败!");
                        }
                    }else{
                        player.sendMessage("§a 余额不足,你现在有$" + bal + ",需要$" + prize);
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