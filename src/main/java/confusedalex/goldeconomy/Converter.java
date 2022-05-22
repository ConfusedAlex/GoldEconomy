package confusedalex.goldeconomy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Converter {

    GoldEconomy plugin;

    public Converter(GoldEconomy plugin) {
        this.plugin = plugin;
    }

    public int getValue(Material material) {
        if (material.equals(Material.GOLD_NUGGET)) return 1;
        if (material.equals(Material.GOLD_INGOT)) return 9;
        if (material.equals(Material.GOLD_BLOCK)) return 81;

        return 0;
    }

    public boolean isGold(Material material) {
        switch(material) {
            case GOLD_BLOCK:
            case GOLD_INGOT:
            case GOLD_NUGGET:
                return true;
            default:
                return false;
        }
    }

    public int getInventoryValue(Player player){
        int value = 0;

        // calculating the value of all the gold in the inventory to nuggets
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            Material material = item.getType();

            if (!isGold(material)) continue;

            value += (getValue(material) * item.getAmount());

        }
        return value;
    }

    public void remove(Player player, int amount){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        int value = 0;

        // calculating the value of all the gold in the inventory to nuggets
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            Material material = item.getType();

            if (!isGold(material)) continue;

            value += (getValue(material) * item.getAmount());

        }

        // Checks if the Value of the items is greater than the amount to deposit
        if (value < amount) return;

        // Deletes all gold items
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            if (!isGold(item.getType())) continue;

            item.setAmount(0);
            item.setType(Material.AIR);
        }

        int newBalance = value - amount;
        give(player, newBalance);
    }

    public void give(Player player, int value){
        player.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, value/81));
        value -= (value/81)*81;
        player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, value/9));
        value -= (value/9)*9;
        player.getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, value));
    }

    public void withdrawAll(Player player){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        int value = plugin.getPlayerBank().get(player.getUniqueId().toString());
        plugin.getEconomyImplementer().withdrawPlayer(op, value);

        give(player, value);
    }

    public void withdraw(Player player, int nuggets){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        if (nuggets > plugin.getPlayerBank().get(player.getUniqueId().toString())) return;
        plugin.getEconomyImplementer().withdrawPlayer(op, nuggets);

        give(player, nuggets);

    }

    public void depositAll(Player player){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        int value = 0;

        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            Material material = item.getType();

            if (!isGold(material)) continue;

            value = value + (getValue(material) * item.getAmount());
            item.setAmount(0);
            item.setType(Material.AIR);
        }

        plugin.getEconomyImplementer().depositPlayer(op, value);

    }

    public void deposit(Player player, int nuggets){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());

        remove(player, nuggets);
        plugin.getEconomyImplementer().depositPlayer(op, nuggets);
    }
}
