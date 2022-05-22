package confusedalex.goldeconomy;

import de.leonhard.storage.Json;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class Events implements org.bukkit.event.Listener {
    GoldEconomy goldEconomy;

    public Events(GoldEconomy goldEconomy) {
        this.goldEconomy = goldEconomy;
    }

    @EventHandler
    public void onPlayerJoining(PlayerJoinEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();
        Json balanceFile = goldEconomy.getBalanceFile();

        if (!balanceFile.contains(uuid)) {
            balanceFile.set(uuid, 0);
        }

        goldEconomy.setBalances(uuid, balanceFile.getDouble(uuid));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String uuid = e.getPlayer().getUniqueId().toString();

        goldEconomy.getBalanceFile().set(uuid, goldEconomy.getPlayerBank().get(uuid));
        goldEconomy.getPlayerBank().remove(uuid);
    }

}
