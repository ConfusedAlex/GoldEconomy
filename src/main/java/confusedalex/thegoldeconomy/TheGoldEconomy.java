package confusedalex.thegoldeconomy;

import de.leonhard.storage.Yaml;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandParser;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public final class TheGoldEconomy extends JavaPlugin {

    EconomyImplementer eco;
    private VaultHook vaultHook;
    Util util;
    Yaml configFile;

    @Override
    public void onEnable() {
        // Config
         configFile = new Yaml("config.yaml", getDataFolder().toString(), getResource("config.yaml"));

        if (!configFile.getString("base").equals("nuggets") && !configFile.getString("base").equals("ingots")) {
             getLogger().severe("The base is not correctly defined in the config file! Only the values 'nuggets' and 'ingots' are allowed! \n"
                     + "If you don't have the base option in your config file, add: base: \"nuggets\" or \"ingots\"");
             getServer().shutdown();
         }

        // Language
        ResourceBundle bundle;
        if ("de_DE".equals(configFile.getString("language"))) {
            bundle = ResourceBundle.getBundle("messages", Locale.GERMANY);
        } else if ("en_US".equals(configFile.getString("language"))) {
            bundle = ResourceBundle.getBundle("messages", Locale.US);
        } else if ("zh_CN".equals(configFile.getString("language"))) {
            bundle = ResourceBundle.getBundle("messages", Locale.SIMPLIFIED_CHINESE);
        } else if ("es_ES".equals(configFile.getString("language"))) {
            Locale locale = new Locale("es", "ES");
            bundle = ResourceBundle.getBundle("messages", locale);
        } else if ("tr_TR".equals(configFile.getString("language"))) {
            Locale locale = new Locale("tr", "TR");
            bundle = ResourceBundle.getBundle("messages", locale);
        } else if ("pt_BR".equals(configFile.getString("language"))) {
            Locale locale = new Locale("pt", "BR");
            bundle = ResourceBundle.getBundle("messages", locale);
        } else {
            bundle = ResourceBundle.getBundle("messages", Locale.US);
            getLogger().warning("Invalid language in config. Defaulting to English.");
        }

        // bStats
        int pluginId = 15402;
        new Metrics(this, pluginId);

        // Vault shit
        util = new Util(this);
        eco = new EconomyImplementer(this, bundle, util);
        vaultHook = new VaultHook(this, eco);
        vaultHook.hook();

        // Commands from RedLib
        ArgType<OfflinePlayer> offlinePlayer = new ArgType<>("offlinePlayer", Bukkit::getOfflinePlayer)
                .tabStream(c -> Bukkit.getOnlinePlayers().stream().map(Player::getName));
        new CommandParser(this.getResource("commands.rdcml"))
                .setArgTypes(offlinePlayer)
                .parse()
                .register("TheGoldEconomy",
                new Commands(bundle, eco, configFile, util));

        // Event class registering
        Bukkit.getPluginManager().registerEvents(new Events (this, eco.bank), this);
        // If removeGoldDrop is true, register Listener
        if (configFile.getBoolean("removeGoldDrop")) Bukkit.getPluginManager().registerEvents(new RemoveGoldDrops(), this);

        // Update Checker
        if (configFile.getBoolean("updateCheck")) {
            new UpdateChecker(this, 102242).getVersion(version -> {
                if (!this.getDescription().getVersion().equals(version)) {
                    getLogger().info(bundle.getString("warning.update"));
                }
            });
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Placeholders(this).register();
        }
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

        getLogger().info("TheGoldEconomy disabled.");
    }
}
