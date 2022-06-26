package confusedalex.thegoldeconomy;

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
    ResourceBundle bundle;
    EconomyImplementer eco;
    Yaml configFile;

    public Commands(ResourceBundle bundle, EconomyImplementer eco, Yaml configFile) {
        this.bundle = bundle;
        this.eco = eco;
        this.configFile = configFile;
    }

    public boolean isBankingRestrictedToPlot(Player player){
        if (configFile.getBoolean("restrictToBankPlot")) {
            if (TownyAPI.getInstance().getTownBlock(player.getLocation()) == null || !Objects.requireNonNull(TownyAPI.getInstance().getTownBlock(player.getLocation())).getType().equals(TownBlockType.BANK)){
                Util.sendMessageToPlayer(bundle.getString("error.bankplot"), player);
                return true;
            }
        }
        return false;
    }

    @CommandHook("balance")
    public void balance(CommandSender commandSender) {
        Player player = (Player) commandSender;
        String uuid = player.getUniqueId().toString();
        Util.sendMessageToPlayer(String.format(bundle.getString("info.balance"), (int) eco.getBalance(uuid), eco.bank.getPlayerBank().get(uuid), eco.converter.getInventoryValue(player)), player);
    }

    @CommandHook("pay")
    public void pay(CommandSender commandSender, OfflinePlayer target, int amount) {
        Player sender = (Player) commandSender;
        String senderuuid = sender.getUniqueId().toString();
        String targetuuid = target.getUniqueId().toString();

        if (isBankingRestrictedToPlot(sender)) return;

        if (amount > eco.bank.getTotalPlayerBalance(senderuuid)) {
            Util.sendMessageToPlayer(bundle.getString("error.notenough"), sender);
            return;
        } else if (senderuuid.equals(targetuuid)){
            Util.sendMessageToPlayer(bundle.getString("error.payyourself"), sender);
            return;
        } else if (Util.isOfflinePlayer(target.getName()) == null) {
            Util.sendMessageToPlayer(bundle.getString("error.noplayer"), sender);
            return;
        }

        eco.withdrawPlayer(sender, amount);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.sendmoneyto"), amount, target.getName()), sender);
        if (target.isOnline()) {
            Util.sendMessageToPlayer(String.format(bundle.getString("info.moneyreceived"), amount, sender.getName()), Objects.requireNonNull(Bukkit.getPlayer(target.getUniqueId())));
            eco.bank.setBalance(target.getUniqueId().toString(), eco.bank.getTotalPlayerBalance(targetuuid) + amount);
        } else {
            eco.depositPlayer(target, eco.bank.getTotalPlayerBalance(targetuuid) + amount);
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

        Util.sendMessageToPlayer(String.format(bundle.getString("info.deposit"), eco.converter.getInventoryValue((Player) commandSender)), player);
    }

    @CommandHook("withdraw")
    public void withdraw(CommandSender commandSender, String nuggets){
        Player player = (Player) commandSender;

        if (isBankingRestrictedToPlot(player)) {
            return;
        }

        if (nuggets == null) {
            Util.sendMessageToPlayer(bundle.getString("conformation.withdrawall"), player);
            Util.sendMessageToPlayer(bundle.getString("warning.golddropped"), player);
            Util.sendMessageToPlayer(bundle.getString("confirm.withdrawall"), player);
        } else if (nuggets.equals("confirm")) {
            eco.converter.withdrawAll((Player) commandSender);
        } else if (!NumberUtils.isNumber(nuggets)) {
        } else {
            eco.converter.withdraw((Player) commandSender, Integer.parseInt(nuggets));
        }

        Util.sendMessageToPlayer(String.format(bundle.getString("info.withdraw"), eco.converter.getInventoryValue((Player) commandSender)), player);
    }

    @CommandHook("set")
    public void set(CommandSender commandSender, OfflinePlayer target, int gold){
        Player player = (Player) commandSender;

        eco.bank.setBalance(target.getUniqueId().toString(), gold);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.sender.moneyset"), target.getName(), gold), player);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.target.moneyset"), gold), Bukkit.getPlayer(target.getUniqueId()));

    }

    @CommandHook("add")
    public void add(CommandSender commandSender, OfflinePlayer target, int gold){
        Player player = (Player) commandSender;

        eco.depositPlayer(target, gold);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.sender.addmoney"), gold, target.getName()), player);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.target.addmoney"), gold), Bukkit.getPlayer(target.getUniqueId()));
    }

    @CommandHook("remove")
    public void remove(CommandSender commandSender, OfflinePlayer target, int gold) {
        Player player = (Player) commandSender;

        eco.withdrawPlayer(target, gold);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.sender.remove"), gold, target.getName()), player);
        Util.sendMessageToPlayer(String.format(bundle.getString("info.target.remove"), gold), Bukkit.getPlayer(target.getUniqueId()));
    }
}


