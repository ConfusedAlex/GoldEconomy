package confusedalex.goldeconomy;

import de.leonhard.storage.Json;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class Events implements org.bukkit.event.Listener {
    GoldEconomy plugin;
    Bank bank;

    public Events(GoldEconomy plugin, Bank bank) {
        this.plugin = plugin;
        this.bank = bank;
    }

    @EventHandler
    public void onPlayerJoining(PlayerJoinEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        Json balanceFile = bank.getBalanceFile();

        if (!balanceFile.contains(uuid)) {
            balanceFile.set(uuid, 0);
        }

        bank.getPlayerBank().put(uuid, balanceFile.getInt(uuid));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();

        bank.getBalanceFile().set(uuid, bank.getPlayerBank().get(uuid));
        bank.getPlayerBank().remove(uuid);
    }

}
