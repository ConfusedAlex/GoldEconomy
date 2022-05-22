package confusedalex.goldeconomy;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlockType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

import java.util.Objects;
import java.util.ResourceBundle;

public class Commands {
    GoldEconomy plugin;
    Converter converter;
    ResourceBundle bundle;


    public Commands(GoldEconomy plugin) {
        this.plugin = plugin;
        converter = plugin.getConverter();
        bundle = plugin.getBundle();
    }

    public boolean isBankingRestrictedToPlot(Player player){
        if (plugin.getConfigFile().getBoolean("restrictToBankPlot")){
            if (TownyAPI.getInstance().getTownBlock(player.getLocation()) == null || !TownyAPI.getInstance().getTownBlock(player.getLocation()).getType().equals(TownBlockType.BANK)){
                plugin.sendMessage(bundle.getString("error.bankplot"), player);
                return true;
            }
        }
        return false;
    }

    @CommandHook("balance")
    public void balance(CommandSender commandSender) {
        Player player = (Player) commandSender;
        String uuid = player.getUniqueId().toString();
        plugin.sendMessage(String.format(bundle.getString("info.balance"), plugin.getBalance(uuid), plugin.getPlayerBank().get(uuid), converter.getInventoryValue(player)), player);
    }

    @CommandHook("pay")
    public void pay(CommandSender commandSender, String sTarget, int amount) {
        Player sender = (Player) commandSender;
        String senderuuid = sender.getUniqueId().toString();
        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(sTarget);

        if (isBankingRestrictedToPlot(sender)) return;

        if (target == null){
        plugin.sendMessage(bundle.getString("error.noplayer"), sender);
            return;
        } else if (amount > plugin.getBalance(senderuuid)) {
            return;
        } else if (senderuuid.equals(target.getUniqueId().toString())){
            plugin.sendMessage(bundle.getString("error.payyourself"), sender);
            return;
        }

        plugin.getEconomyImplementer().withdrawPlayer(sender, amount);
        plugin.sendMessage(String.format(bundle.getString("info.sendmoneyto"), amount, target.getName()), sender);
        if (target.isOnline()) {
            plugin.sendMessage(String.format(bundle.getString("info.moneyreceived"), amount, sender.getName()), Objects.requireNonNull(Bukkit.getPlayer(target.getUniqueId())));
            plugin.setBalances(target.getUniqueId().toString(), plugin.getBalance(senderuuid) + amount);
        } else {
            plugin.getEconomyImplementer().depositPlayer(target, plugin.getBalance(senderuuid) + amount);
        }
    }

    @CommandHook("deposit")
    public void deposit(CommandSender commandSender, String nuggets){
        Player player = (Player) commandSender;

        if (isBankingRestrictedToPlot(player)) return;

        if (nuggets == null){
            converter.depositAll((Player) commandSender);
            return;
        }
        converter.deposit((Player) commandSender, Integer.parseInt(nuggets));
    }

    @CommandHook("withdraw")
    public void withdraw(CommandSender commandSender, String nuggets){
        Player player = (Player) commandSender;

        if (isBankingRestrictedToPlot(player)) return;

        if (nuggets == null){
            plugin.sendMessage(bundle.getString("conformation.withdrawall"), player);
            plugin.sendMessage(bundle.getString("warning.golddropped"), player);
            plugin.sendMessage(bundle.getString("confirm.withdrawall"), player);
            return;
        } else if (nuggets.equals("confirm")) {
            converter.withdrawAll((Player) commandSender);
            return;
        }
        converter.withdraw((Player) commandSender, Integer.parseInt(nuggets));
    }
}


