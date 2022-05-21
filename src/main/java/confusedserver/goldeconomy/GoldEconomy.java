package confusedserver.goldeconomy;

import de.leonhard.storage.Json;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class GoldEconomy extends JavaPlugin {
    private Json balanceFile = new Json("balance.json", getDataFolder().toString());

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new Events (this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Json getBalanceFile() {
        return balanceFile;
    }
}
