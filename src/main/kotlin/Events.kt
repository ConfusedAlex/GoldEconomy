import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class Events(private var bank: Bank) : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val uuid = e.player.uniqueId

       if (!bank.playerAccounts.contains(uuid.toString())) {
           bank.playerAccounts[uuid.toString()] = 0
       }
    }
}