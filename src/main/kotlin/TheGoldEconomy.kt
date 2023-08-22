import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class TheGoldEconomy: JavaPlugin() {
    val playersFile = File("plugins/TheGoldEconomy/players.json")
    val fakeAccounts = File("plugins/TheGoldEconomy/fakeAccounts.json")
    private val util: Util = Util()
    private val converter: Converter = Converter()
    private val bank = Bank(util, converter, this)
    private lateinit var language: Language

    override fun onEnable() {
        // Config
        saveDefaultConfig()
        reloadConfig()
        config.options().copyDefaults(true)

        language = Language(this, config.getString("language") ?: "en-US")

        Bukkit.getPluginManager().registerEvents(Events(bank), this)
        this.getCommand("bank")?.setExecutor(Commands(bank, this, language))

        val vaultHook = VaultHook(this, EconomyImplementer(util, bank, converter))
        vaultHook.hook()

        if (config.getBoolean("removeGoldDrops")) {
            Bukkit.getPluginManager().registerEvents(RemoveGoldDrops(), this)
        }

        val metrics = Metrics(this, 15402)
    }

    override fun onDisable() {
        playersFile.writeText(Json.encodeToString(bank.playerAccounts))
        fakeAccounts.writeText(Json.encodeToString(bank.fakeAccounts))
    }
}