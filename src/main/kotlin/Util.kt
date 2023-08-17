import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class Util {

    fun createPlayersFile() {
        val file = File("plugins/TheGoldEconomy/players.json")
        if (file.exists()) return
        file.createNewFile()
        file.writeText("{}")
    }

    fun createFakeAccountsFile() {
        val file = File("plugins/TheGoldEconomy/fakeAccounts.json")
        if (file.exists()) return
        file.createNewFile()
        file.writeText("{}")
    }

    fun isOfflinePlayer(name: String?): Boolean {
        for (p: OfflinePlayer in Bukkit.getOfflinePlayers()) {
            if (name.equals(p.name)) return true
        }
        return false
    }

}