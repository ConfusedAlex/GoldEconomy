import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class Util {

    fun createPlayerFile(uuid: UUID) {
        val file = File("plugins/TheGoldEconomy/players/$uuid.yml")
        if (!file.exists()) {
            file.createNewFile()
            val config = YamlConfiguration.loadConfiguration(file)
            config.set("balance", 0)
            config.save(file)
        }
    }

    fun createFakeAccountsFile() {
        val file = File("plugins/TheGoldEconomy/fakeaccounts.yml")
        if (!file.exists()) {
            file.createNewFile()
            val config = YamlConfiguration.loadConfiguration(file)
            config.save(file)
        }
    }

    fun getPlayerFile(uuid: UUID): YamlConfiguration {
        val file = File("plugins/TheGoldEconomy/players/$uuid.yml")
        return YamlConfiguration.loadConfiguration(file)
    }

    fun playerFileSet(uuid: UUID, path: String, v: Any) {
        val file = File("plugins/TheGoldEconomy/players/$uuid.yml")
        val config = YamlConfiguration.loadConfiguration(file)

        config.set(path, v)
        config.save(file)
    }

    fun fakeAccountsFileSet(path: String, v: Any) {
        val file = File("plugins/TheGoldEconomy/players/fakeaccounts.yml")
        val config = YamlConfiguration.loadConfiguration(file)

        config.set(path, v)
        config.save(file)
    }

    fun getPlayerBalanceFromFile(uuid: UUID): Int {
        return getPlayerFile(uuid).get("balance") as Int
    }

    fun getFakeAccountsFile(): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(File ("plugins/TheGoldEconomy/fakeaccounts.yml"))
    }

    fun isOfflinePlayer(name: String?): Boolean {
        for (p: OfflinePlayer in Bukkit.getOfflinePlayers()) {
            if (name.equals(p.name)) return true
        }
        return false
    }

}