package confusedalex.goldeconomy;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlockType;
import de.leonhard.storage.Yaml;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

import java.util.Objects;
import java.util.ResourceBundle;

public class Commands {
    GoldEconomy plugin;
    ResourceBundle bundle;
    EconomyImplementer eco;
    Yaml configFile;

    public Commands(GoldEconomy plugin, ResourceBundle bundle, EconomyImplementer eco, Yaml configFile) {
        this.plugin = plugin;
        this.bundle = bundle;
        this.eco = eco;
        this.configFile = configFile;
    }

    public boolean isBankingRestrictedToPlot(Player player){
        if (configFile.getBoolean("restrictToBankPlot")) {
            if (TownyAPI.getInstance().getTownBlock(player.getLocation()) == null || !Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(player.getLocation())).getType().equals(TownBlockType.BANK)){
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
        plugin.sendMessage(String.format(bundle.getString("info.balance"), (int) eco.getBalance(uuid), eco.bank.getPlayerBank().get(uuid), eco.converter.getInventoryValue(player)), player);
    }

    @CommandHook("pay")
    public void pay(CommandSender commandSender, OfflinePlayer target, int amount) {
        Player sender = (Player) commandSender;
        String senderuuid = sender.getUniqueId().toString();
        String targetuuid = target.getUniqueId().toString();

        if (isBankingRestrictedToPlot(sender)) return;

        if (amount > eco.bank.getTotalBalance(senderuuid)) {
            return;
        } else if (senderuuid.equals(targetuuid)){
            plugin.sendMessage(bundle.getString("error.payyourself"), sender);
            return;
        }

        eco.withdrawPlayer(sender, amount);
        plugin.sendMessage(String.format(bundle.getString("info.sendmoneyto"), amount, target.getName()), sender);
        if (target.isOnline()) {
            plugin.sendMessage(String.format(bundle.getString("info.moneyreceived"), amount, sender.getName()), Objects.requireNonNull(Bukkit.getPlayer(target.getUniqueId())));
            eco.bank.setBalance(target.getUniqueId().toString(), eco.bank.getTotalBalance(senderuuid) + amount);
        } else {
            eco.depositPlayer(target, eco.bank.getTotalBalance(senderuuid) + amount);
        }
    }

    @CommandHook("deposit")
    public void deposit(CommandSender commandSender, String nuggets){
        Player player = (Player) commandSender;

        if (isBankingRestrictedToPlot(player)) return;

        if (nuggets == null){
            eco.converter.depositAll((Player) commandSender);
            return;
        }
        eco.converter.deposit((Player) commandSender, Integer.parseInt(nuggets));
    }

    @CommandHook("withdraw")
    public void withdraw(CommandSender commandSender, String nuggets){
        Player player = (Player) commandSender;

        if (isBankingRestrictedToPlot(player)) {
            return;
        }

        if (nuggets == null) {
            plugin.sendMessage(bundle.getString("conformation.withdrawall"), player);
            plugin.sendMessage(bundle.getString("warning.golddropped"), player);
            plugin.sendMessage(bundle.getString("confirm.withdrawall"), player);
        } else if (nuggets.equals("confirm")) {
            eco.converter.withdrawAll((Player) commandSender);
        } else if (!NumberUtils.isNumber(nuggets)) {
        } else {
            eco.converter.withdraw((Player) commandSender, Integer.parseInt(nuggets));
        }


    }

    @CommandHook("set")
    public void set(CommandSender commandSender, OfflinePlayer target, int gold){
        eco.bank.setBalance(target.getUniqueId().toString(), gold);
    }

    @CommandHook("add")
    public void add(CommandSender commandSender, OfflinePlayer target, int gold){
     eco.depositPlayer(target, gold);
    }

    @CommandHook("remove")
    public void remove(CommandSender commandSender, OfflinePlayer target, int gold) {
        eco.withdrawPlayer(target, gold);
    }
}


