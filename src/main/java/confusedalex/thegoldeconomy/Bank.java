package confusedalex.thegoldeconomy;

import de.leonhard.storage.Json;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Bank {
    EconomyImplementer eco;
    private final Json balanceFile;
    private final HashMap<String, Integer> playerBank;
    final Json fakeAccountsFile;
    private final HashMap<String, Integer> fakeAccounts;

    public Bank(TheGoldEconomy theGoldEconomy, EconomyImplementer eco) {
        this.eco = eco;
        balanceFile = new Json("balance.json", theGoldEconomy.getDataFolder() + "/data/");
        playerBank = new HashMap<>();
        fakeAccountsFile = new Json("fakeAccounts.json", theGoldEconomy.getDataFolder() + "/data/");
        fakeAccounts = new HashMap<>();
    }

    public int getTotalPlayerBalance(String uuid){
        if (playerBank.containsKey(uuid)) return playerBank.get(uuid) + eco.converter.getInventoryValue(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(uuid))));
        else return balanceFile.getInt(uuid);

    }
    
    public int getFakeBalance(String s){
        if (fakeAccounts.containsKey(s)) return fakeAccounts.get(s);
        else if (fakeAccountsFile.contains(s)) return fakeAccountsFile.getInt(s);

        fakeAccountsFile.set(s, 0);
        fakeAccounts.put(s, 0);
        return getFakeBalance(s);
    }

    public int getAccountBalance(String uuid){
        if (playerBank.containsKey(uuid)) return playerBank.get(uuid);
        else return balanceFile.getInt(uuid);
    }

    public void setBalance(String uuid, int balance){
        if (playerBank.containsKey(uuid)) playerBank.put(uuid, balance);
        else if (balanceFile.contains(uuid)) balanceFile.set(uuid, balance);
        else if (fakeAccounts.containsKey(uuid))  fakeAccounts.put(uuid, balance);
        else if (fakeAccountsFile.contains(uuid))  fakeAccountsFile.set(uuid, balance);

    }

    public Json getBalanceFile() {
        return balanceFile;
    }

    public HashMap<String, Integer> getPlayerBank() {
        return playerBank;
    }

    public HashMap<String, Integer> getFakeAccounts() {
        return fakeAccounts;
    }
}
