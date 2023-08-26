import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.function.Consumer

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
class UpdateChecker(private val plugin: JavaPlugin) {
    fun getVersion(consumer: Consumer<String?>) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                URL("https://api.spigotmc.org/legacy/update.php?resource=" + 102242 + "/~")
                    .openStream().use { `is` ->
                        Scanner(`is`).use { scann ->
                            if (scann.hasNext()) {
                                consumer.accept(scann.next())
                            }
                        }
                    }
            } catch (e: IOException) {
                plugin.logger.info("Unable to check for updates: " + e.message)
            }
        })
    }
}