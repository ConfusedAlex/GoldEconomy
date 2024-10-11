package confusedalex.thegoldeconomy

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import co.aikar.commands.annotation.Optional
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.util.*

@Suppress("DEPRECATION")
@CommandAlias("bank")
class BankCommand(plugin: TheGoldEconomy) : BaseCommand() {
    private var bundle: ResourceBundle = plugin.eco.bundle
    private var eco: EconomyImplementer = plugin.eco
    var util: Util = plugin.util
    var config: FileConfiguration = plugin.config

    @HelpCommand
    fun help(help: CommandHelp) {
        help.showHelp()
    }

    @Subcommand("balance")
    @CommandAlias("balance")
    @Description("{@@command.info.balance}")
    @CommandPermission("thegoldeconomy.balance")
    fun balance(commandSender: CommandSender) {
        val player = commandSender as Player
        val uuid = player.uniqueId
        util.sendMessageToPlayer(
            String.format(
                bundle.getString("info.balance"),
                eco.getBalance(uuid.toString()).toInt(),
                eco.bank.getAccountBalance(uuid),
                eco.converter.getInventoryValue(player)
            ), player
        )
    }

    @Subcommand("pay")
    @Description("{@@command.info.pay}")
    @CommandPermission("thegoldeconomy.pay")
    fun pay(commandSender: CommandSender, target: OfflinePlayer, amount: Int) {
        val sender = commandSender as Player
        val senderuuid = sender.uniqueId
        val targetuuid = target.uniqueId

        when {
            util.isBankingRestrictedToPlot(sender) -> return
            amount == 0 -> {
                util.sendMessageToPlayer(bundle.getString("error.zero"), sender)
                return
            }
            amount < 0 -> {
                util.sendMessageToPlayer(bundle.getString("error.negative"), sender)
                return
            }
            amount > eco.bank.getTotalPlayerBalance(senderuuid) -> {
                util.sendMessageToPlayer(bundle.getString("error.notenough"), sender)
                return
            }
            senderuuid == targetuuid -> {
                util.sendMessageToPlayer(bundle.getString("error.payyourself"), sender)
                return
            }
            Util.isOfflinePlayer(target.name).isEmpty -> {
                util.sendMessageToPlayer(bundle.getString("error.noplayer"), sender)
                return
            }
            else -> {
                eco.withdrawPlayer(sender, amount.toDouble())
                util.sendMessageToPlayer(
                    String.format(bundle.getString("info.sendmoneyto"), amount, target.name), sender)
                if (target.isOnline) {
                    util.sendMessageToPlayer(
                        String.format(bundle.getString("info.moneyreceived"),
                            amount, sender.name), Bukkit.getPlayer(target.uniqueId))
                    eco.bank.setAccountBalance(target.uniqueId, eco.bank.getTotalPlayerBalance(targetuuid) + amount)
                } else {
                    eco.depositPlayer(target, amount.toDouble())
                }
            }
        }
    }

    @Subcommand("deposit")
    @Description("{@@command.info.deposit}")
    @CommandPermission("thegoldeconomy.deposit")
    fun deposit(commandSender: CommandSender, @Optional nuggets: String?) {
        val player = commandSender as Player
        val inventoryValue = eco.converter.getInventoryValue(commandSender)

        if (util.isBankingRestrictedToPlot(player)) {
            return
        }
        if (nuggets == null || nuggets == "all") {
            if (inventoryValue <= 0) {
                util.sendMessageToPlayer(bundle.getString("error.zero"), player)
                return
            }
            util.sendMessageToPlayer(String.format(bundle.getString("info.deposit"), inventoryValue), player)
            eco.converter.depositAll(commandSender)
            return
        }

        val amount: Int
        try {
            amount = nuggets.toInt()
        } catch (e: NumberFormatException) {
            commandHelp.showHelp()
            return
        }

        if (amount == 0) {
            util.sendMessageToPlayer(bundle.getString("error.zero"), player)
        } else if (amount < 0) {
            util.sendMessageToPlayer(bundle.getString("error.negative"), player)
        } else if (amount > inventoryValue) {
            util.sendMessageToPlayer(bundle.getString("error.notenough"), player)
        } else {
            util.sendMessageToPlayer(String.format(bundle.getString("info.deposit"), amount), player)
            eco.converter.deposit(commandSender, nuggets.toInt())
        }
    }

    @Subcommand("withdraw")
    @Description("{@@command.info.withdraw}")
    @CommandPermission("thegoldeconomy.withdraw")
    fun withdraw(commandSender: CommandSender, @Optional nuggets: String?) {
        val player = commandSender as Player

        if (util.isBankingRestrictedToPlot(player)) return
        if (nuggets == null || nuggets == "all") {
            val accountBalance = eco.bank.getAccountBalance(player.uniqueId)
            util.sendMessageToPlayer(String.format(bundle.getString("info.withdraw"), accountBalance), player)
            eco.converter.withdrawAll(player)
            return
        }

        val amount: Int
        try {
            amount = nuggets.toInt()
        } catch (e: NumberFormatException) {
            commandHelp.showHelp()
            return
        }

        if (amount == 0) {
            util.sendMessageToPlayer(bundle.getString("error.zero"), player)
        } else if (amount < 0) {
            util.sendMessageToPlayer(bundle.getString("error.negative"), player)
        } else if (amount > eco.bank.getAccountBalance(player.uniqueId)) {
            util.sendMessageToPlayer(bundle.getString("error.notenough"), player)
        } else {
            util.sendMessageToPlayer(String.format(bundle.getString("info.withdraw"), amount), player)
            eco.converter.withdraw(player, amount)
        }
    }


    @Subcommand("set")
    @CommandPermission("thegoldeconomy.set")
    @Description("{@@command.info.set}")
    fun set(commandSender: CommandSender?, target: OfflinePlayer, gold: Int) {
        if (commandSender is Player) {
            util.sendMessageToPlayer(
                String.format(bundle.getString("info.sender.moneyset"), target.name, gold),
                commandSender
            )
        }

        eco.bank.setAccountBalance(target.uniqueId, gold)
        util.sendMessageToPlayer(
            String.format(bundle.getString("info.target.moneyset"), gold),
            Bukkit.getPlayer(target.uniqueId)
        )
    }

    @Subcommand("add")
    @CommandPermission("thegoldeconomy.add")
    @Description("{@@command.info.add}")
    fun add(commandSender: CommandSender?, target: OfflinePlayer, gold: Int) {
        if (commandSender is Player) {
            util.sendMessageToPlayer(
                String.format(bundle.getString("info.sender.addmoney"), gold, target.name),
                commandSender
            )
        }

        eco.depositPlayer(target, gold.toDouble())
        util.sendMessageToPlayer(
            String.format(bundle.getString("info.target.addmoney"), gold),
            Bukkit.getPlayer(target.uniqueId)
        )
    }

    @Subcommand("remove")
    @CommandPermission("thegoldeconomy.remove")
    @Description("{@@command.info.remove}")
    fun remove(commandSender: CommandSender?, target: OfflinePlayer, gold: Int) {
        if (commandSender is Player) {
            util.sendMessageToPlayer(
                String.format(bundle.getString("info.sender.remove"), gold, target.name),
                commandSender
            )
        }

        eco.withdrawPlayer(target, gold.toDouble())
        util.sendMessageToPlayer(
            String.format(bundle.getString("info.target.remove"), gold),
            Bukkit.getPlayer(target.uniqueId)
        )
    }
}