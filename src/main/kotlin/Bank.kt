import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class Bank(private val util: Util, val converter: Converter) {
    val playerAccounts = HashMap<UUID, Int>()
    val fakeAccounts = HashMap<String, Int>()

    fun getTotalPlayerBalance(uuid: UUID): Int {
        val player: Player? = Bukkit.getPlayer(uuid)

        if (player != null) {
            if (playerAccounts.contains(uuid)) {
                return playerAccounts.getValue(uuid) + converter.getInventoryValue(player)
            }
        }
        return util.getPlayerBalanceFromFile(uuid)
    }

    fun getAccountBalance(uuid: UUID): Int {
        if (playerAccounts.containsKey(uuid)) return playerAccounts.getValue(uuid)
        return util.getPlayerFile(uuid).get("balance") as Int
    }

    fun setAccountBalance(uuid: UUID, amount: Int) {
        if (playerAccounts.containsKey(uuid)) {
            playerAccounts[uuid] = amount
            return
        }
        util.playerFileSet(uuid, "balance", amount)
    }

    fun setFakeAccountBalance(s: String, amount: Int) {
        if (fakeAccounts.containsKey(s)) {
            fakeAccounts[s] = amount
            return
        }
        util.fakeAccountsFileSet(s, amount)
    }

    fun getFakeBalance(s: String): Int {
        val fakeAccountsFile = util.getFakeAccountsFile()

        if (fakeAccounts.containsKey(s)) return fakeAccounts.getValue(s)
        if (fakeAccountsFile.contains(s)) return fakeAccountsFile.get(s) as Int

        util.getFakeAccountsFile().set(s, 0)
        return 0
    }
}