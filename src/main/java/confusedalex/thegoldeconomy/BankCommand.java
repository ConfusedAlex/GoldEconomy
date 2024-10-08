package confusedalex.thegoldeconomy;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

@CommandAlias("bank")
public class BankCommand extends BaseCommand {
  ResourceBundle bundle;
  EconomyImplementer eco;
  Util util;
  FileConfiguration config;

  public BankCommand(TheGoldEconomy plugin) {
    this.bundle = plugin.eco.bundle;
    this.eco = plugin.eco;
    this.util = plugin.util;
    this.config = plugin.getConfig();
  }

  @HelpCommand
  public void help(CommandHelp help) {
    help.showHelp();
  }

  @Subcommand("balance")
  @CommandAlias("balance")
  @Description("{@@command.info.balance}")
  @CommandPermission("thegoldeconomy.balance")
  public void balance(CommandSender commandSender) {
    Player player = (Player) commandSender;
    UUID uuid = player.getUniqueId();
    util.sendMessageToPlayer(String.format(bundle.getString("info.balance"), (int) eco.getBalance(uuid.toString()), eco.bank.getAccountBalance(uuid), eco.converter.getInventoryValue(player)), player);
  }

  @Subcommand("pay")
  @Description("{@@command.info.pay}")
  @CommandPermission("thegoldeconomy.pay")
  public void pay(CommandSender commandSender, OfflinePlayer target, int amount) {
    Player sender = (Player) commandSender;
    UUID senderuuid = sender.getUniqueId();
    UUID targetuuid = target.getUniqueId();

    if (util.isBankingRestrictedToPlot(sender)) return;
    if (amount == 0) {
      util.sendMessageToPlayer(bundle.getString("error.zero"), sender);
      return;
    }
    if (amount < 0) {
      util.sendMessageToPlayer(bundle.getString("error.negative"), sender);
      return;
    }
    if (amount > eco.bank.getTotalPlayerBalance(senderuuid)) {
      util.sendMessageToPlayer(bundle.getString("error.notenough"), sender);
      return;
    } else if (senderuuid.equals(targetuuid)) {
      util.sendMessageToPlayer(bundle.getString("error.payyourself"), sender);
      return;
    } else if (Util.isOfflinePlayer(target.getName()).isEmpty()) {
      util.sendMessageToPlayer(bundle.getString("error.noplayer"), sender);
      return;
    }

    eco.withdrawPlayer(sender, amount);
    util.sendMessageToPlayer(String.format(bundle.getString("info.sendmoneyto"), amount, target.getName()), sender);
    if (target.isOnline()) {
      util.sendMessageToPlayer(String.format(bundle.getString("info.moneyreceived"), amount, sender.getName()), Objects.requireNonNull(Bukkit.getPlayer(target.getUniqueId())));
      eco.bank.setAccountBalance(target.getUniqueId(), eco.bank.getTotalPlayerBalance(targetuuid) + amount);
    } else {
      eco.depositPlayer(target, eco.bank.getTotalPlayerBalance(targetuuid) + amount);
    }
  }

  @Subcommand("deposit")
  @Description("{@@command.info.deposit}")
  @CommandPermission("thegoldeconomy.deposit")
  public void deposit(CommandSender commandSender, @Optional String nuggets) {
    Player player = (Player) commandSender;
    int inventoryValue = eco.converter.getInventoryValue((Player) commandSender);

    if (util.isBankingRestrictedToPlot(player)) return;
    if (nuggets == null || nuggets.equals("all")) {
      if (inventoryValue <= 0) {
        util.sendMessageToPlayer(bundle.getString("error.zero"), player);
        return;
      }
      util.sendMessageToPlayer(String.format(bundle.getString("info.deposit"), inventoryValue), player);
      eco.converter.depositAll((Player) commandSender);
      return;
    }

    int amount = 0;
    try {
      amount = Integer.parseInt(nuggets);
    } catch (NumberFormatException e) {
      getCommandHelp().showHelp();
      return;
    }

    if (amount == 0) {
      util.sendMessageToPlayer(bundle.getString("error.zero"), player);
    } else if (amount < 0) {
      util.sendMessageToPlayer(bundle.getString("error.negative"), player);
    } else if (amount > inventoryValue) {
      util.sendMessageToPlayer(bundle.getString("error.notenough"), player);
    } else {
      util.sendMessageToPlayer(String.format(bundle.getString("info.deposit"), amount), player);
      eco.converter.deposit((Player) commandSender, Integer.parseInt(nuggets));
    }
  }

  @Subcommand("withdraw")
  @Description("{@@command.info.withdraw}")
  @CommandPermission("thegoldeconomy.withdraw")
  public void withdraw(CommandSender commandSender, @Optional String nuggets) {
    Player player = (Player) commandSender;

    if (util.isBankingRestrictedToPlot(player)) return;
    if (nuggets == null || nuggets.equals("all")) {
      int accountBalance = eco.bank.getAccountBalance(player.getUniqueId());
      util.sendMessageToPlayer(String.format(bundle.getString("info.withdraw"), accountBalance), player);
      eco.converter.withdrawAll(player);
      return;
    }

    int amount = 0;
    try {
      amount = Integer.parseInt(nuggets);
    } catch (NumberFormatException e) {
      getCommandHelp().showHelp();
      return;
    }

    if (amount == 0) {
      util.sendMessageToPlayer(bundle.getString("error.zero"), player);
    } else if (amount < 0) {
      util.sendMessageToPlayer(bundle.getString("error.negative"), player);
    } else if (amount > eco.bank.getAccountBalance(player.getUniqueId())) {
      util.sendMessageToPlayer(bundle.getString("error.notenough"), player);
    } else {
      util.sendMessageToPlayer(String.format(bundle.getString("info.withdraw"), amount), player);
      eco.converter.withdraw(player, amount);
    }
  }


  @Subcommand("set")
  @CommandPermission("thegoldeconomy.set")
  @Description("{@@command.info.set}")
  public void set(CommandSender commandSender, OfflinePlayer target, int gold) {
    if (commandSender instanceof Player player) {
        util.sendMessageToPlayer(String.format(bundle.getString("info.sender.moneyset"), target.getName(), gold), player);
    }

    eco.bank.setAccountBalance(target.getUniqueId(), gold);
    util.sendMessageToPlayer(String.format(bundle.getString("info.target.moneyset"), gold), Bukkit.getPlayer(target.getUniqueId()));
  }

  @Subcommand("add")
  @CommandPermission("thegoldeconomy.add")
  @Description("{@@command.info.add}")
  public void add(CommandSender commandSender, OfflinePlayer target, int gold) {
    if (commandSender instanceof Player player) {
        util.sendMessageToPlayer(String.format(bundle.getString("info.sender.addmoney"), gold, target.getName()), player);
    }

    eco.depositPlayer(target, gold);
    util.sendMessageToPlayer(String.format(bundle.getString("info.target.addmoney"), gold), Bukkit.getPlayer(target.getUniqueId()));
  }

  @Subcommand("remove")
  @CommandPermission("thegoldeconomy.remove")
  @Description("{@@command.info.remove}")
  public void remove(CommandSender commandSender, OfflinePlayer target, int gold) {
    if (commandSender instanceof Player player) {
        util.sendMessageToPlayer(String.format(bundle.getString("info.sender.remove"), gold, target.getName()), player);
    }

    eco.withdrawPlayer(target, gold);
    util.sendMessageToPlayer(String.format(bundle.getString("info.target.remove"), gold), Bukkit.getPlayer(target.getUniqueId()));
  }
}


