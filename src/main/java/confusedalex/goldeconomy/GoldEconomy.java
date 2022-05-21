package confusedalex.goldeconomy;

import de.leonhard.storage.Json;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.CommandParser;

import java.util.HashMap;
import java.util.Map;

public final class GoldEconomy extends JavaPlugin {
    private final Json balanceFile = new Json("balance.json", getDataFolder().toString());
    private final HashMap<String, Double> playerBank = new HashMap<>();
    private EconomyImplementer economyImplementer;
    private VaultHook vaultHook;

    @Override
    public void onEnable() {

        // Vault shit
        economyImplementer = new EconomyImplementer(this);
        vaultHook = new VaultHook(this, economyImplementer);
        vaultHook.hook();

        // Commands from RedLib
        new CommandParser(this.getResource("commands.rdcml")).parse().register("GoldEconomy", new Commands(this));

        // Event class registering
        Bukkit.getPluginManager().registerEvents(new Events (this), this);
    }

    @Override
    public void onDisable() {
        for(Map.Entry<String, Double> entry : playerBank.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            balanceFile.getFileData().insert(key, value);
        }

        balanceFile.write();

        vaultHook.unhook();
    }

    public Json getBalanceFile() {
        return balanceFile;
    }

    public void setBalances(String uuid, double aDouble) {
        playerBank.put(uuid, aDouble);
    }

    public HashMap<String, Double> getPlayerBank() {
        return playerBank;
    }

    public EconomyImplementer getEconomyImplementer() {
        return economyImplementer;
    }
}
