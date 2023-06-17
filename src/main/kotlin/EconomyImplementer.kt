package thegoldeconomy

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

class EconomyImplementer(private val util: Util, private val bank: Bank, private val converter: Converter): Economy {

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getName(): String {
        return "TheGoldEconomy"
    }

    override fun hasBankSupport(): Boolean {
        return false
    }

    override fun fractionalDigits(): Int {
        return 0
    }

    override fun format(v: Double): String {
        return "$v Gold"
    }

    override fun currencyNamePlural(): String {
        return "Gold"
    }

    override fun currencyNameSingular(): String {
        return "Gold"
    }

    override fun hasAccount(s: String): Boolean {
        if (util.isOfflinePlayer(s)) return true

        if (bank.fakeAccounts.containsKey(s)) return true
        else if (util.getFakeAccountsFile().contains(s)) return true

        util.getFakeAccountsFile().set(s, 0)
        bank.fakeAccounts[s] = 0
        return true
    }

    override fun hasAccount(offlinePlayer: OfflinePlayer?): Boolean {
        return true
    }

    override fun hasAccount(s: String, s1: String?): Boolean {
        if (util.isOfflinePlayer(s)) return true

        if (bank.fakeAccounts.containsKey(s)) return true
        else if (util.getFakeAccountsFile().contains(s)) return true

        util.getFakeAccountsFile().set(s, 0)
        bank.fakeAccounts[s] = 0
        return true
    }

    override fun hasAccount(offlinePlayer: OfflinePlayer, s: String): Boolean {
        return true
    }

    override fun getBalance(playerName: String): Double {
        val player = Bukkit.getPlayer(playerName)
        if (player != null) {
            return bank.getTotalPlayerBalance(player.uniqueId).toDouble()
        }

        if (util.isOfflinePlayer(playerName)) {
            return bank.getTotalPlayerBalance(Bukkit.getOfflinePlayer(playerName).uniqueId).toDouble()
        }

        return bank.getFakeBalance(playerName).toDouble()
    }

    override fun getBalance(offlinePlayer: OfflinePlayer): Double {
        return bank.getTotalPlayerBalance(offlinePlayer.uniqueId).toDouble()
    }

    override fun getBalance(playerName: String, world: String?): Double {
        val player = Bukkit.getPlayer(playerName)
        if (player != null) {
            return bank.getTotalPlayerBalance(player.uniqueId).toDouble()
        }

        if (util.isOfflinePlayer(playerName)) {
            return bank.getTotalPlayerBalance(Bukkit.getOfflinePlayer(playerName).uniqueId).toDouble()
        }

        val fb = bank.getFakeBalance(playerName)
        return fb.toDouble()
    }

    override fun getBalance(offlinePlayer: OfflinePlayer, s: String?): Double {
        return bank.getTotalPlayerBalance(offlinePlayer.uniqueId).toDouble()
    }

    override fun has(playerName: String, v: Double): Boolean {
        return if (util.isOfflinePlayer(playerName)) {
            v < bank.getTotalPlayerBalance(Bukkit.getOfflinePlayer(playerName).uniqueId)
        } else v < bank.getFakeBalance(playerName)
    }

    override fun has(offlinePlayer: OfflinePlayer, v: Double): Boolean {
        return v < bank.getTotalPlayerBalance(offlinePlayer.uniqueId)
    }

    override fun has(playerName: String, s1: String?, v: Double): Boolean {
        return if (util.isOfflinePlayer(playerName)) {
            v < bank.getTotalPlayerBalance(Bukkit.getOfflinePlayer(playerName).uniqueId)
        } else v < bank.getFakeBalance(playerName)
    }

    override fun has(offlinePlayer: OfflinePlayer, s: String?, v: Double): Boolean {
        return v < bank.getTotalPlayerBalance(offlinePlayer.uniqueId)
    }

    override fun withdrawPlayer(s: String, amount: Double): EconomyResponse {
        // If amount to withdraw is < 0
        if (amount < 0) return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't withdraw negative amount!")

        if (util.isOfflinePlayer(s)) {
            val offlinePlayer = Bukkit.getOfflinePlayer(s)
            // If players balance is insufficient
            if (!has(offlinePlayer, amount)) return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Not enough money")


            if (offlinePlayer.isOnline) {
                val player: Player? = offlinePlayer.player
                val uuid: UUID? = player?.uniqueId

                val oldBankBalance = bank.getAccountBalance(offlinePlayer.uniqueId)
                val oldInventoryBalance = player?.let { converter.getInventoryValue(it) }

                // If bank balances is sufficient
                if (oldBankBalance > amount) {
                    val newBalance = oldBankBalance - amount
                    uuid?.let { bank.setAccountBalance(it, (newBalance).toInt()) }
                    return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
                } else {
                    val diff = amount - oldBankBalance

                    if (player != null && uuid != null) {
                        bank.setAccountBalance(uuid, 0)
                        converter.remove(player, diff.toInt())
                        return EconomyResponse(amount,
                            oldInventoryBalance?.minus(diff)!!, EconomyResponse.ResponseType.SUCCESS, "")
                    }
                }
            } else {
                val uuid = offlinePlayer.uniqueId
                val oldBalance = bank.getAccountBalance(uuid)
                val newBalance = oldBalance - amount
                bank.setAccountBalance(uuid, newBalance.toInt())
                return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
            }
        } else {
            val oldBalance = bank.getFakeBalance(s)
            val newBalance = bank.getFakeBalance(s) - amount
            if (!has(s, amount)) return EconomyResponse(amount,
                oldBalance.toDouble(), EconomyResponse.ResponseType.FAILURE, "Not enough money")

            bank.setFakeAccountBalance(s, newBalance.toInt())
            return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
        }
        return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "")
    }

    override fun withdrawPlayer(offlinePlayer: OfflinePlayer, amount: Double): EconomyResponse? {
        // If amount to withdraw is < 0
        if (amount < 0) return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't withdraw negative amount!")

        // If players balance is insufficient
        if (!has(offlinePlayer, amount)) return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Not enough money")


        if (offlinePlayer.isOnline) {
            val player: Player? = offlinePlayer.player
            val uuid: UUID? = player?.uniqueId

            val oldBankBalance = bank.getAccountBalance(offlinePlayer.uniqueId)
            val oldInventoryBalance = player?.let { converter.getInventoryValue(it) }

            // If bank balances is sufficient
            if (oldBankBalance > amount) {
                val newBalance = oldBankBalance - amount
                uuid?.let { bank.setAccountBalance(it, (newBalance).toInt()) }
                return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
            } else {
                val diff = amount - oldBankBalance

                if (player != null && uuid != null) {
                    bank.setAccountBalance(uuid, 0)
                    converter.remove(player, diff.toInt())
                    return EconomyResponse(amount,
                        oldInventoryBalance?.minus(diff)!!, EconomyResponse.ResponseType.SUCCESS, "")
                }
            }
        } else {
            val uuid = offlinePlayer.uniqueId
            val oldBalance = bank.getAccountBalance(uuid)
            val newBalance = oldBalance - amount
            bank.setAccountBalance(uuid, newBalance.toInt())
            return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
        }
    return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "")
    }

    override fun withdrawPlayer(s: String, s1: String?, amount: Double): EconomyResponse? {
        // If amount to withdraw is < 0
        if (amount < 0) return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't withdraw negative amount!")

        if (util.isOfflinePlayer(s)) {
            val offlinePlayer = Bukkit.getOfflinePlayer(s)
            // If players balance is insufficient
            if (!has(offlinePlayer, amount)) return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Not enough money")


            if (offlinePlayer.isOnline) {
                val player: Player? = offlinePlayer.player
                val uuid: UUID? = player?.uniqueId

                val oldBankBalance = bank.getAccountBalance(offlinePlayer.uniqueId)
                val oldInventoryBalance = player?.let { converter.getInventoryValue(it) }

                // If bank balances is sufficient
                if (oldBankBalance > amount) {
                    val newBalance = oldBankBalance - amount
                    uuid?.let { bank.setAccountBalance(it, (newBalance).toInt()) }
                    return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
                } else {
                    val diff = amount - oldBankBalance

                    if (player != null && uuid != null) {
                        bank.setAccountBalance(uuid, 0)
                        converter.remove(player, diff.toInt())
                        return EconomyResponse(amount,
                            oldInventoryBalance?.minus(diff)!!, EconomyResponse.ResponseType.SUCCESS, "")
                    }
                }
            } else {
                val uuid = offlinePlayer.uniqueId
                val oldBalance = bank.getAccountBalance(uuid)
                val newBalance = oldBalance - amount
                bank.setAccountBalance(uuid, newBalance.toInt())
                return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
            }
        } else {
            val oldBalance = bank.getFakeBalance(s)
            val newBalance = bank.getFakeBalance(s) - amount
            if (!has(s, amount)) return EconomyResponse(amount,
                oldBalance.toDouble(), EconomyResponse.ResponseType.FAILURE, "Not enough money")

            bank.setFakeAccountBalance(s, newBalance.toInt())
            return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
        }
        return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "")
    }

    override fun withdrawPlayer(offlinePlayer: OfflinePlayer, s: String?, amount: Double): EconomyResponse? {
        // If amount to withdraw is < 0
        if (amount < 0) return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Can't withdraw negative amount!")

        // If players balance is insufficient
        if (!has(offlinePlayer, amount)) return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Not enough money")


        if (offlinePlayer.isOnline) {
            val player: Player? = offlinePlayer.player
            val uuid: UUID? = player?.uniqueId

            val oldBankBalance = bank.getAccountBalance(offlinePlayer.uniqueId)
            val oldInventoryBalance = player?.let { converter.getInventoryValue(it) }

            // If bank balances is sufficient
            if (oldBankBalance > amount) {
                val newBalance = oldBankBalance - amount
                uuid?.let { bank.setAccountBalance(it, (newBalance).toInt()) }
                return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
            } else {
                val diff = amount - oldBankBalance

                if (player != null && uuid != null) {
                    bank.setAccountBalance(uuid, 0)
                    converter.remove(player, diff.toInt())
                    return EconomyResponse(amount,
                        oldInventoryBalance?.minus(diff)!!, EconomyResponse.ResponseType.SUCCESS, "")
                }
            }
        } else {
            val uuid = offlinePlayer.uniqueId
            val oldBalance = bank.getAccountBalance(uuid)
            val newBalance = oldBalance - amount
            bank.setAccountBalance(uuid, newBalance.toInt())
            return EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "")
        }
        return EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "")
    }

    override fun depositPlayer(s: String, amount: Double): EconomyResponse? {
        var oldBalance = 0

        // If amount is negative -> return
        if (amount < 0) return EconomyResponse(
            amount,
            oldBalance.toDouble(),
            EconomyResponse.ResponseType.FAILURE,
            "error"
        )
        return if (util.isOfflinePlayer(s)) {
            val player = Bukkit.getOfflinePlayer(s)
            val uuid = player.uniqueId

            // Getting balance and calculating new Balance
            oldBalance = bank.getAccountBalance(uuid)
            val newBalance = (oldBalance + amount).toInt()
            bank.setAccountBalance(uuid, newBalance)
            EconomyResponse(amount, newBalance.toDouble(), EconomyResponse.ResponseType.SUCCESS, "")
        } else {
            oldBalance = bank.getFakeBalance(s)
            val newBalance = (oldBalance + amount).toInt()
            bank.setFakeAccountBalance(s, newBalance)
            EconomyResponse(amount, newBalance.toDouble(), EconomyResponse.ResponseType.SUCCESS, "")
        }
    }

    override fun depositPlayer(offlinePlayer: OfflinePlayer, amount: Double): EconomyResponse? {
        val uuid = offlinePlayer.uniqueId
        val oldBalance: Int = bank.getAccountBalance(uuid)
        val newBalance = (amount + oldBalance).toInt()

        // If amount is negative -> return
        if (amount < 0) return EconomyResponse(
            amount,
            oldBalance.toDouble(),
            EconomyResponse.ResponseType.FAILURE,
            "error"
        )
        bank.setAccountBalance(uuid, newBalance)
        return EconomyResponse(amount, newBalance.toDouble(), EconomyResponse.ResponseType.SUCCESS, "")
    }

    override fun depositPlayer(s: String, s1: String?, amount: Double): EconomyResponse? {
        var oldBalance = 0

        // If amount is negative -> return
        if (amount < 0) return EconomyResponse(
            amount,
            oldBalance.toDouble(),
            EconomyResponse.ResponseType.FAILURE,
            "error"
        )
        return if (util.isOfflinePlayer(s)) {
            val player = Bukkit.getOfflinePlayer(s)
            val uuid = player.uniqueId

            // Getting balance and calculating new Balance
            oldBalance = bank.getAccountBalance(uuid)
            val newBalance = (oldBalance + amount).toInt()
            bank.setAccountBalance(uuid, newBalance)
            EconomyResponse(amount, newBalance.toDouble(), EconomyResponse.ResponseType.SUCCESS, "")
        } else {
            oldBalance = bank.getFakeBalance(s)
            val newBalance = (oldBalance + amount).toInt()
            bank.setFakeAccountBalance(s, newBalance)
            EconomyResponse(amount, newBalance.toDouble(), EconomyResponse.ResponseType.SUCCESS, "")
        }
    }

    override fun depositPlayer(offlinePlayer: OfflinePlayer, s: String?, amount: Double): EconomyResponse? {
        val uuid = offlinePlayer.uniqueId
        val oldBalance: Int = bank.getAccountBalance(uuid)
        val newBalance = (amount + oldBalance).toInt()

        // If amount is negative -> return
        if (amount < 0) return EconomyResponse(
            amount,
            oldBalance.toDouble(),
            EconomyResponse.ResponseType.FAILURE,
            "error"
        )
        bank.setAccountBalance(uuid, newBalance)
        return EconomyResponse(amount, newBalance.toDouble(), EconomyResponse.ResponseType.SUCCESS, "")
    }

    override fun createBank(s: String?, s1: String?): EconomyResponse? {
        return null
    }

    override fun createBank(s: String?, offlinePlayer: OfflinePlayer?): EconomyResponse? {
        return null
    }

    override fun deleteBank(s: String?): EconomyResponse? {
        return null
    }

    override fun bankBalance(s: String?): EconomyResponse? {
        return null
    }

    override fun bankHas(s: String?, v: Double): EconomyResponse? {
        return null
    }

    override fun bankWithdraw(s: String?, v: Double): EconomyResponse? {
        return null
    }

    override fun bankDeposit(s: String?, v: Double): EconomyResponse? {
        return null
    }

    override fun isBankOwner(s: String?, s1: String?): EconomyResponse? {
        return null
    }

    override fun isBankOwner(s: String?, offlinePlayer: OfflinePlayer?): EconomyResponse? {
        return null
    }

    override fun isBankMember(s: String?, s1: String?): EconomyResponse? {
        return null
    }

    override fun isBankMember(s: String?, offlinePlayer: OfflinePlayer?): EconomyResponse? {
        return null
    }

    override fun getBanks(): List<String?>? {
        return null
    }

    override fun createPlayerAccount(s: String): Boolean {
        if (util.isOfflinePlayer(s)) {
            util.createPlayerFile(Bukkit.getOfflinePlayer(s).uniqueId)
        } else {
            if (!util.getFakeAccountsFile().contains("s")) util.getFakeAccountsFile().set(s, 0)
        }
        return true
    }

    override fun createPlayerAccount(offlinePlayer: OfflinePlayer): Boolean {
        util.createPlayerFile(offlinePlayer.uniqueId)
        return true
    }

    override fun createPlayerAccount(s: String, s1: String?): Boolean {
        if (util.isOfflinePlayer(s)) {
            util.createPlayerFile(Bukkit.getOfflinePlayer(s).uniqueId)
        } else {
            if (!util.getFakeAccountsFile().contains("s")) util.getFakeAccountsFile().set(s, 0)
        }
        return true
    }

    override fun createPlayerAccount(offlinePlayer: OfflinePlayer, s: String?): Boolean {
        util.createPlayerFile(offlinePlayer.uniqueId)
        return true
    }
}