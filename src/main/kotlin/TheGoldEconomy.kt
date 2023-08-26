import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class TheGoldEconomy: JavaPlugin() {
    val playersFile = File("plugins/TheGoldEconomy/players.json")
    val fakeAccounts = File("plugins/TheGoldEconomy/fakeAccounts.json")
    private val util: Util = Util()
    private val converter: Converter = Converter()
    private val bank = Bank(converter, this)
    private lateinit var language: Language

    override fun onEnable() {
        // Config
        saveDefaultConfig()
        reloadConfig()
        config.options().copyDefaults(true)

        language = Language(this, config.getString("language") ?: "en-US")

        UpdateChecker(this).getVersion { version: String? ->
            if (description.version == version) {
                logger.info("There is not a new update available.")
            } else {
                logger.info("There is a new update available.")
            }
        }

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