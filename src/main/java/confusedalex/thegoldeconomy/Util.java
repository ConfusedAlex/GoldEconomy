package confusedalex.thegoldeconomy;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlockType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
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
        if (name.equals(playerName)) {
          return Optional.of(p);
        }
      }
    }
    return Optional.empty();
  }

  public void sendMessageToPlayer(String message, Player player) {
    String prefix = plugin.getConfig().getString("prefix");
    if (player != null) {
      player.sendMessage(String.format(ChatColor.GOLD + "[%s] " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message), prefix));
    }
  }

  public boolean isBankingRestrictedToPlot(Player player) {
    if (plugin.getConfig().getBoolean("restrictToBankPlot")) {
      if (TownyAPI.getInstance().getTownBlock(player.getLocation()) == null || !Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(player.getLocation())).getType().equals(TownBlockType.BANK)) {
        sendMessageToPlayer(plugin.bundle.getString("error.bankplot"), player);
        return true;
      }
    }
    return false;
  }

  public Optional<Player> isPlayer(CommandSender commandSender) {
    if (commandSender instanceof Player) {
      return Optional.of((Player) commandSender);
    } else {
      commandSender.sendMessage(plugin.bundle.getString("error.notaplayer"));
      return Optional.empty();
    }
  }
}
