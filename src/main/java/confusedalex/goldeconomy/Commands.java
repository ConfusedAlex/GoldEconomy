package confusedalex.goldeconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

public class Commands {
    GoldEconomy plugin;
    Converter converter;


    public Commands(GoldEconomy plugin) {
        this.plugin = plugin;
        converter = plugin.getConverter();
    }

    @CommandHook("balance")
    public void balance(CommandSender commandSender){
        Player player = (Player) commandSender;
        player.sendMessage("Your balance is: " + plugin.getBalance(player.getUniqueId().toString()));
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


