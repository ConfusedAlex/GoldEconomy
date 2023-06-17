package thegoldeconomy

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Commands(private var bank: Bank, val plugin: TheGoldEconomy, val language: Language) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) TODO("Display Help")

        if (sender !is Player) {
            return true
        }

        when (args[0].lowercase()) {
            "balance" -> {
                val totalBalance = bank.getTotalPlayerBalance(sender.uniqueId)
                val inventoryBalance = bank.converter.getInventoryValue(sender)
                val accountBalance = bank.getAccountBalance(sender.uniqueId)
                language.send(sender, "info.balance", totalBalance, accountBalance, inventoryBalance)
                return true
            }
            "withdraw" -> {
                val param = args[1]
                val uuid = sender.uniqueId
                val accountBalance = bank.getAccountBalance(uuid)

                if (param.toIntOrNull() == null) {
                    return if (param == "all") {
                        bank.converter.give(sender, accountBalance)
                        language.send(sender, "info.withdraw", accountBalance)
                        bank.setAccountBalance(uuid, 0)
                        true
                    } else {
                        language.send(sender, "help.withdraw")
                        true
                    }
                }

                val value = param.toInt()
                if (value < 0) {
                    language.send(sender, "error.negative")
                    return true
                } else if (value == 0) {
                    language.send(sender, "error.zero")
                    return true
                } else if (value > accountBalance) {
                    language.send(sender, "error.notenoughmoneywithdraw")
                } else {
                    bank.setAccountBalance(uuid, accountBalance - value)
                    bank.converter.give(sender, value)
                    language.send(sender, "info.withdraw", value)
                }
                return true
            }
            "deposit" -> {
                val param = args[1]
                val uuid = sender.uniqueId
                val accountBalance = bank.getAccountBalance(uuid)
                val inventoryValue = bank.converter.getInventoryValue(sender)

                if (param.toIntOrNull() == null) {
                    return if (param == "all") {
                        bank.converter.remove(sender, inventoryValue)
                        language.send(sender, "info.deposit", inventoryValue)
                        bank.setAccountBalance(uuid, accountBalance + inventoryValue)
                        true
                    } else {
                        language.send(sender, "help.deposit")
                        true
                    }
                }

                val value = param.toInt()

                if (value < 0) {
                    language.send(sender, "error.negative")
                    return true
                } else if (value == 0) {
                    language.send(sender, "error.zero")
                    return true
                } else if (value > inventoryValue) {
                    language.send(sender, "error.notenough")
                } else {
                    bank.setAccountBalance(uuid, accountBalance + value)
                    bank.converter.remove(sender, value)
                    language.send(sender, "info.deposit", value)
                }
                return true
            }
            else -> return true
        }
    }
}