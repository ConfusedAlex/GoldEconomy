package confusedalex.goldeconomy;

import de.leonhard.storage.Yaml;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandParser;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public final class GoldEconomy extends JavaPlugin {

    EconomyImplementer eco;
    private VaultHook vaultHook;


    @Override
    public void onEnable() {
        // Config
        Yaml configFile = new Yaml("config.yaml", getDataFolder().toString(), getResource("config.yaml"));

        // Language
        ResourceBundle bundle;
        if ("de_DE".equals(configFile.getString("language"))) {
             bundle = ResourceBundle.getBundle("messages", Locale.GERMANY);
        } else {
             bundle = ResourceBundle.getBundle("messages", Locale.US);
        }

        // Vault shit
        eco = new EconomyImplementer(this, bundle);
        vaultHook = new VaultHook(this, eco);
        vaultHook.hook();

        // Commands from RedLib
        ArgType<OfflinePlayer> offlinePlayer = new ArgType<>("offlinePlayer", Bukkit::getOfflinePlayerIfCached)
                .tabStream(c -> Bukkit.getOnlinePlayers().stream().map(Player::getName));
        new CommandParser(this.getResource("commands.rdcml"))
                .setArgTypes(offlinePlayer)
                .parse()
                .register("GoldEconomy",
                new Commands(this, bundle, eco, configFile));

        // Event class registering
        Bukkit.getPluginManager().registerEvents(new Events (this, eco.bank), this);
        // If removeGoldDrop is true, register Listener
        if (configFile.getBoolean("removeGoldDrop")) Bukkit.getPluginManager().registerEvents(new RemoveGoldDrops(), this);

    }

    @Override
    public void onDisable() {
        // Save player HashMap to File
        for(Map.Entry<String, Integer> entry : eco.bank.getPlayerBank().entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            eco.bank.getBalanceFile().getFileData().insert(key, value);
        }
        eco.bank.getBalanceFile().write();

        // Save FakeAccount HashMap to File
        for(Map.Entry<String, Integer> entry : eco.bank.getFakeAccounts().entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            eco.bank.fakeAccountsFile.getFileData().insert(key, value);
        }
        eco.bank.fakeAccountsFile.write();

        vaultHook.unhook();
    }

    public void sendMessage(String message, Player player){
        player.sendMessage(ChatColor.GOLD + "[GoldEconomy] " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message));
    }
}
