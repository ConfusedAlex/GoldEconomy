package confusedalex.thegoldeconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Util {

    public static OfflinePlayer isOfflinePlayer(String playerName) {
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            String name = p.getName();
            if (name != null) {
                if (name.equals(playerName)) return p;
            }
        }
        return null;
    }

    public static void sendMessageToPlayer(String message, Player player){
        if (player == null) return;
        player.sendMessage(ChatColor.GOLD + "[TheGoldEconomy] " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message));
    }

}
