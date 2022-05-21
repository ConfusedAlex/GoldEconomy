package confusedserver.goldeconomy;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.UUID;


public class Events implements org.bukkit.event.Listener {
   GoldEconomy goldEconomy;

    public Events(GoldEconomy goldEconomy) {
        this.goldEconomy = goldEconomy;
    }

    @EventHandler
   public void onPlayerJoining(PlayerJoinEvent e){
       String uuid = e.getPlayer().getUniqueId().toString();

       if (!goldEconomy.getBalanceFile().contains(uuid)){
           goldEconomy.getBalanceFile().set(uuid, 0);
       }
   }

}
