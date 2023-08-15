import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class RemoveGoldDrops: Listener {
    
    @EventHandler
    fun entityDeathEvent(e: EntityDeathEvent) {
        if (e.entity is Player) return

        e.drops.removeIf {
                it.type == Material.GOLD_INGOT ||
                it.type == Material.GOLD_NUGGET ||
                it.type == Material.GOLD_BLOCK ||
                it.type == Material.GOLDEN_SWORD ||
                it.type == Material.GOLDEN_HELMET ||
                it.type == Material.GOLDEN_CHESTPLATE ||
                it.type == Material.GOLDEN_LEGGINGS ||
                it.type == Material.GOLDEN_BOOTS
        }
    }

}
