package confusedalex.goldeconomy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;

public class VaultHook {
    private final GoldEconomy plugin;
    private Economy provider;

    public VaultHook(GoldEconomy plugin, Economy provider) {
        this.plugin = plugin;
        this.provider = provider;
    }

    public void hook(){
        provider = plugin.getEconomyImplementer();
        Bukkit.getServicesManager().register(Economy.class, this.provider, plugin, ServicePriority.Normal);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "VaultAPI hooked into " + ChatColor.AQUA + plugin.getName());
    }

    public void unhook(){
        Bukkit.getServicesManager().unregister(Economy.class, this.provider);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "VaultAPI unhooked from " + ChatColor.AQUA + plugin.getName());
    }
}
