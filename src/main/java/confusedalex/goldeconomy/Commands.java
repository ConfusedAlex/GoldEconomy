package confusedalex.goldeconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

import java.util.Objects;

public class Commands {
    GoldEconomy plugin;
    Converter converter;


    public Commands(GoldEconomy plugin) {
        this.plugin = plugin;
        converter = plugin.getConverter();
    }

    @CommandHook("balance")
    public void balance(CommandSender commandSender) {
        Player player = (Player) commandSender;
        player.sendMessage("Your have " + ChatColor.GOLD + plugin.getBalance(player.getUniqueId().toString()) + ChatColor.WHITE + " Gold on the Bank and " + ChatColor.GOLD + converter.getInventoryValue(player) + ChatColor.WHITE + " on hand.");
    }

    @CommandHook("pay")
    public void pay(CommandSender commandSender, String sTarget, int amount) {
        Player sender = (Player) commandSender;
        String senderuuid = sender.getUniqueId().toString();
        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(sTarget);

        if (target == null){
            commandSender.sendMessage("Player not found!");
            return;
        } else if (amount > plugin.getBalance(senderuuid)) {
            return;
        } else if (senderuuid.equals(target.getUniqueId().toString())){
            sender.sendMessage("You cannot send money to yourself!");
            return;
        }

        plugin.setBalances(senderuuid, (plugin.getBalance(senderuuid) - amount));
        sender.sendMessage("You've sent " + ChatColor.GOLD + amount + " Gold" + ChatColor.WHITE + " to " + target.getName());
        if (target.isOnline()) {
            Objects.requireNonNull(Bukkit.getPlayer(target.getUniqueId())).sendMessage(("You've received " + ChatColor.GOLD + amount + " Gold" + ChatColor.WHITE + " from " + sender.getName()));
            plugin.setBalances(target.getUniqueId().toString(), plugin.getBalance(senderuuid) + amount);
        } else {
            plugin.getBalanceFile().set(target.getUniqueId().toString(), plugin.getBalance(senderuuid) + amount);
        }
    }

    @CommandHook("deposit")
    public void deposit(CommandSender commandSender, String nuggets){

        if (nuggets == null){
            converter.depositAll((Player) commandSender);
            return;
        }
        converter.deposit((Player) commandSender, Integer.parseInt(nuggets));
    }

    @CommandHook("withdraw")
    public void withdraw(CommandSender commandSender, String nuggets){

        if (nuggets == null){
            commandSender.sendMessage("Do you really want to withdraw all your gold from your bank?");
            commandSender.sendMessage("Gold that doesn't fit into your inventory will be dropped!");
            commandSender.sendMessage("Type " + ChatColor.AQUA +  "/bank withdraw confirm" + ChatColor.WHITE + " to withdraw all your money");
            return;
        } else if (nuggets.equals("confirm")) {
            converter.withdrawAll((Player) commandSender);
        }
        converter.withdraw((Player) commandSender, Integer.parseInt(nuggets));
    }
}


