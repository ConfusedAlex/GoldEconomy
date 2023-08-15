import net.md_5.bungee.api.ChatColor.*
import org.bukkit.entity.Player
import java.io.File
import java.net.URLClassLoader
import java.text.MessageFormat
import java.util.*

class Language(plugin: TheGoldEconomy, private val language: String) {

    private val resourceBundles: List<ResourceBundle>
    private val locale = Locale.forLanguageTag(language)

    init {
        val languageFolder = File(plugin.dataFolder, "lang")
        languageFolder.mkdirs()
        plugin.saveResource("lang/lang_en_US.properties", false)
        plugin.saveResource("lang/lang_de_DE.properties", false)
        val externalUrls = arrayOf(languageFolder.toURI().toURL())
        val externalClassLoader = URLClassLoader(externalUrls)
        val externalResourceBundle = ResourceBundle.getBundle(
            "lang",
            locale,
            externalClassLoader
        )
        val internalResourceBundle = ResourceBundle.getBundle(
            "lang/lang",
            locale
        )
        resourceBundles = listOf(externalResourceBundle, internalResourceBundle)
    }

    operator fun get(key: String) =
        resourceBundles.firstNotNullOfOrNull { resourceBundle ->
            try {
                translateAlternateColorCodes('&', resourceBundle.getString(key))

            } catch (exception: MissingResourceException) {
                null
            }
        } ?: "Missing translation for ${language}: $key"

    fun send(player: Player, key: String, vararg params: Any) {
        val message: String = MessageFormat.format(get(key), *params)
        player.sendMessage("$GOLD[TheGoldEconomy]$WHITE ${translateAlternateColorCodes('&', message)}")
    }
}