package thegoldeconomy

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority

class VaultHook(private val theGoldEconomy: TheGoldEconomy, private val provider: Economy) {

    fun hook() {
        Bukkit.getServicesManager().register(Economy::class.java, provider, theGoldEconomy, ServicePriority.Normal)
    }

    fun unhook() {
        Bukkit.getServicesManager().unregister(Economy::class.java, provider)
    }
}