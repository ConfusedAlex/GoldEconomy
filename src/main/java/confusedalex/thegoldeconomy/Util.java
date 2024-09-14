package confusedalex.thegoldeconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Util {
  TheGoldEconomy plugin;

  public Util(TheGoldEconomy plugin) {
    this.plugin = plugin;
  }

  public static Optional<OfflinePlayer> isOfflinePlayer(String playerName) {
    for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
      String name = p.getName();
      if (name != null) {
        if (name.equals(playerName)) return Optional.of(p);
      }
    }
    return Optional.empty();
  }

  public void sendMessageToPlayer(String message, Player player) {
    String prefix = plugin.configFile.getString("prefix");
    if (player != null) {
      player.sendMessage(String.format(ChatColor.GOLD + "[%s] " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message), prefix));
    }
  }
}
