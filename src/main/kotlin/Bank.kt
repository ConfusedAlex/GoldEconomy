import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class Bank(private val util: Util, val converter: Converter, val plugin : TheGoldEconomy) {
    init {
        util.createPlayersFile()
        util.createFakeAccountsFile()
    }
    val playerAccounts : HashMap<String, Int> = Json.decodeFromString(plugin.playersFile.readText())
    val fakeAccounts : HashMap<String, Int> = Json.decodeFromString(plugin.fakeAccounts.readText())

    fun getTotalPlayerBalance(uuid: UUID): Int {
        val player: Player? = Bukkit.getPlayer(uuid)

        if (player != null) {
            if (playerAccounts.contains(uuid.toString())) {
                return playerAccounts.getValue(uuid.toString()) + converter.getInventoryValue(player)
            }
        }
        return playerAccounts.getValue(uuid.toString())
    }

    fun getAccountBalance(uuid: UUID): Int {
        return playerAccounts.getValue(uuid.toString())
    }

    fun setAccountBalance(uuid: UUID, amount: Int) {
        playerAccounts[uuid.toString()] = amount
    }

    fun setFakeAccountBalance(s: String, amount: Int) {
        fakeAccounts[s] = amount
    }

    fun getFakeBalance(s: String): Int {
        if (fakeAccounts.containsKey(s)) return fakeAccounts.getValue(s)

        fakeAccounts[s] = 0
        return 0
    }
}