//import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class TheGoldEconomy: JavaPlugin() {
    private val util: Util = Util()
    private val converter: Converter = Converter()
    private val bank: Bank = Bank(util, converter)
    private lateinit var language: Language

    override fun onEnable() {
        createPlayerFolder()
        util.createFakeAccountsFile()

        // Config
        saveDefaultConfig()
        reloadConfig()
        config.options().copyDefaults(true)

        language = Language(this, config.getString("language") ?: "en-US")

        Bukkit.getPluginManager().registerEvents(Events(bank, util), this)
        this.getCommand("bank")?.setExecutor(Commands(bank, this, language))

        val vaultHook = VaultHook(this, EconomyImplementer(util, bank, converter))
        vaultHook.hook()

        if (config.getBoolean("removeGoldDrops")) {
            Bukkit.getPluginManager().registerEvents(RemoveGoldDrops(), this)
        }

//        val metrics = Metrics(this, 15402)
    }

    override fun onDisable() {
        for ((key, value) in bank.fakeAccounts) {
            util.fakeAccountsFileSet(key, value)
        }

        for (player: Player in Bukkit.getOnlinePlayers()) {
            val uuid = player.uniqueId
            val balance = bank.getAccountBalance(uuid)
            util.playerFileSet(uuid, "balance", balance)
        }
    }

    private fun createPlayerFolder() {
        val playerFolder = File("plugins/TheGoldEconomy/players")
        playerFolder.mkdirs()
    }
}