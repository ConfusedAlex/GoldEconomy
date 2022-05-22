package confusedalex.goldeconomy;

import de.leonhard.storage.Json;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.CommandParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class GoldEconomy extends JavaPlugin {
    private final Json balanceFile = new Json("balance.json", getDataFolder().toString());
    private final HashMap<String, Integer> playerBank = new HashMap<>();
    private final Json fakeAccountsFile = new Json("fakeAccounts.json", getDataFolder().toString());
    private final HashMap<String, Integer> fakeAccounts = new HashMap<>();
    private EconomyImplementer economyImplementer;
    private VaultHook vaultHook;
    private Converter converter;

    @Override
    public void onEnable() {

        // Vault shit
        economyImplementer = new EconomyImplementer(this);
        vaultHook = new VaultHook(this, economyImplementer);
        vaultHook.hook();

        converter = new Converter(this);

        // Commands from RedLib
        new CommandParser(this.getResource("commands.rdcml")).parse().register("GoldEconomy", new Commands(this));

        // Event class registering
        Bukkit.getPluginManager().registerEvents(new Events (this), this);
    }

    @Override
    public void onDisable() {
        for(Map.Entry<String, Integer> entry : playerBank.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            balanceFile.getFileData().insert(key, value);
        }
        balanceFile.write();

        for(Map.Entry<String, Integer> entry : fakeAccounts.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            fakeAccountsFile.getFileData().insert(key, value);
        }
        fakeAccountsFile.write();

        vaultHook.unhook();
    }

    public int getBalance(String uuid){
        if (playerBank.containsKey(uuid)) return playerBank.get(uuid) + converter.getInventoryValue(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(uuid))));
        if (balanceFile.contains(uuid)) return balanceFile.getInt(uuid);
        if (fakeAccounts.containsKey(uuid)) return fakeAccounts.get(uuid);
        if (fakeAccountsFile.contains(uuid)) return fakeAccountsFile.getInt(uuid);

        fakeAccountsFile.set(uuid, 0);
        fakeAccounts.put(uuid, 0);
        return  getBalance(uuid);
    }

    public void setBalance(String uuid, int balance){
        if (playerBank.containsKey(uuid)) playerBank.put(uuid, balance);
        if (balanceFile.contains(uuid))  balanceFile.set(uuid, balance);
        if (fakeAccounts.containsKey(uuid))  fakeAccounts.put(uuid, balance);
        if (fakeAccountsFile.contains(uuid))  fakeAccountsFile.set(uuid, balance);
    }

    public Json getBalanceFile() {
        return balanceFile;
    }

    public Json getFakeAccountsFile(){
        return fakeAccountsFile;
    }

    public void setBalances(String uuid, int aInt) {
        playerBank.put(uuid, aInt);
    }

    public void setFakeBalances(String uuid, int aInt) {
        fakeAccounts.put(uuid, aInt);
    }

    public HashMap<String, Integer> getPlayerBank() {
        return playerBank;
    }

    public HashMap<String, Integer> getFakeAccounts() {
        return fakeAccounts;
    }

    public EconomyImplementer getEconomyImplementer() {
        return economyImplementer;
    }

    public Converter getConverter(){
        return converter;
    }
}
