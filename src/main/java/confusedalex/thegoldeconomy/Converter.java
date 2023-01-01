package confusedalex.thegoldeconomy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.ResourceBundle;

public class Converter {

    EconomyImplementer eco;
    ResourceBundle bundle;

    public Converter(EconomyImplementer economyImplementer, ResourceBundle bundle) {
        this.eco = economyImplementer;
        this.bundle = bundle;
    }

    public int getValue(Material material) {
        if (material.equals(Material.GOLD_NUGGET)) return 0;
        if (material.equals(Material.GOLD_INGOT)) return 1;
        if (material.equals(Material.GOLD_BLOCK)) return 9;

        return 0;
    }

    public boolean isNotGold(Material material) {
        switch(material) {
            case GOLD_BLOCK:
            case GOLD_INGOT:
            case GOLD_NUGGET:
                return false;
            default:
                return true;
        }
    }

    public int getInventoryValue(Player player){
        int value = 0;

        // calculating the value of all the gold in the inventory to nuggets
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            Material material = item.getType();

            if (isNotGold(material)) continue;

            value += (getValue(material) * item.getAmount());

        }
        return value;
    }

    public void remove(Player player, int amount){
        int value = 0;

        // calculating the value of all the gold in the inventory to nuggets
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            Material material = item.getType();

            if (isNotGold(material)) continue;

            value += (getValue(material) * item.getAmount());
        }

        // Checks if the Value of the items is greater than the amount to deposit
        if (value < amount) return;

        // Deletes all gold items
        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            if (isNotGold(item.getType())) continue;

            item.setAmount(0);
            item.setType(Material.AIR);
        }

        int newBalance = value - amount;
        give(player, newBalance);
    }

    public void give(Player player, int value){
        boolean warning = false;

        HashMap<Integer, ItemStack> blocks = player.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, value/9));
        for (ItemStack item : blocks.values()) {
            if (item != null && item.getType() == Material.GOLD_BLOCK && item.getAmount() > 0) {
                player.getWorld().dropItem(player.getLocation(), item);
                warning = true;
            }
        }

        value -= (value/9)*9;

        HashMap<Integer, ItemStack> ingots = player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, value));
        for (ItemStack item : ingots.values()) {
            if (item != null && item.getType() == Material.GOLD_INGOT && item.getAmount() > 0) {
                player.getWorld().dropItem(player.getLocation(), item);
                warning = true;
            }
        }

        if (warning) Util.sendMessageToPlayer(String.format(bundle.getString("warning.drops")), player);
    }


    public void withdrawAll(Player player){
        String uuid = player.getUniqueId().toString();

        // searches in the Hashmap for the balance, so that a player can't withdraw gold from his Inventory
        int value = eco.bank.getAccountBalance(player.getUniqueId().toString());
        eco.bank.setBalance(uuid, (0));

        give(player, value);
    }

    public void withdraw(Player player, int blocks){
        String uuid = player.getUniqueId().toString();
        int oldbalance = eco.bank.getAccountBalance(player.getUniqueId().toString());

        // Checks balance in HashMap
        if (blocks > eco.bank.getPlayerBank().get(player.getUniqueId().toString())) {
            Util.sendMessageToPlayer(bundle.getString("error.notenoughmoneywithdraw"), player);
            return;
        }
        eco.bank.setBalance(uuid, (oldbalance - blocks));

        give(player, blocks);

    }

    public void depositAll(Player player){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        int value = 0;

        for (ItemStack item : player.getInventory()) {
            if (item == null) continue;
            Material material = item.getType();

            if (isNotGold(material)) continue;

            value = value + (getValue(material) * item.getAmount());
            item.setAmount(0);
            item.setType(Material.AIR);
        }

        eco.depositPlayer(op, value);

    }

    public void deposit(Player player, int blocks){
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());

        remove(player, blocks);
        eco.depositPlayer(op, blocks);
    }
}
