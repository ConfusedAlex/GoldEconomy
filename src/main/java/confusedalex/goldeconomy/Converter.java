package confusedalex.goldeconomy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

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

    public void depositAll(Player player){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        int value = 0;

        for (ItemStack item : player.getInventory()) {
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
        int value = 0;

        // calculating the value of all the gold in the inventory to nuggets
        for (ItemStack item : player.getInventory()) {
            Material material = item.getType();

            if (!isGold(material)) continue;

            value += (getValue(material) * item.getAmount());
        }

        int newBalance = value - nuggets;

        if (newBalance <= 0) {
            player.sendMessage("error");
            return;
        }

        player.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, newBalance/81));
        newBalance -= newBalance/81;
        player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, newBalance/9));
        newBalance -= newBalance/9;
        player.getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, newBalance));

        plugin.getEconomyImplementer().depositPlayer(op, value);
    }
}
