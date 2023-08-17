import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class Events(private var bank: Bank, private var util: Util) : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val uuid = e.player.uniqueId
        
       if (!bank.playerAccounts.contains(uuid.toString())) {
           bank.playerAccounts[uuid.toString()] = 0
       }
    }
}