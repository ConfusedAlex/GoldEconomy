import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Converter {

    fun getValue(material: Material): Int = when (material) {
        Material.GOLD_NUGGET -> 1
        Material.GOLD_INGOT -> 9
        Material.GOLD_BLOCK -> 81
        else -> 0
    }

    fun getInventoryValue (player: Player): Int {
        return player.inventory.filterNotNull().sumOf { getValue(it.type)*it.amount }
    }

    fun give(player: Player, amount: Int) {
        var value = amount

        player.inventory.addItem(ItemStack(Material.GOLD_BLOCK, value/81))
        value -= value/81 * 81
        player.inventory.addItem(ItemStack(Material.GOLD_INGOT, value/9))
        value -= value/9 * 9
        player.inventory.addItem(ItemStack(Material.GOLD_NUGGET, value))
    }

    fun remove(player: Player, amount: Int) {
        val value = getInventoryValue(player)

        if (amount > value) return

        player.inventory.filterNotNull().forEach {
            if (getValue(it.type) > 0) {
                it.amount = 0
                it.type = Material.AIR
            }
        }

        val newInventoryValue = value - amount
        give(player, newInventoryValue)
    }
}