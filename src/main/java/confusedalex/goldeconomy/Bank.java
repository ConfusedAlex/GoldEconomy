package confusedalex.goldeconomy;

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

    public Bank(GoldEconomy goldEconomy, EconomyImplementer eco) {
        this.eco = eco;
        balanceFile = new Json("balance.json", goldEconomy.getDataFolder() + "/data/");
        playerBank = new HashMap<>();
        fakeAccountsFile = new Json("fakeAccounts.json", goldEconomy.getDataFolder() + "/data/");
        fakeAccounts = new HashMap<>();
    }

    public int getBalance(String uuid){
        if (playerBank.containsKey(uuid)) return playerBank.get(uuid) + eco.converter.getInventoryValue(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(uuid))));
        else if (balanceFile.contains(uuid)) return balanceFile.getInt(uuid);
        else if (fakeAccounts.containsKey(uuid)) return fakeAccounts.get(uuid);
        else if (fakeAccountsFile.contains(uuid)) return fakeAccountsFile.getInt(uuid);

        fakeAccountsFile.set(uuid, 0);
        fakeAccounts.put(uuid, 0);
        return getBalance(uuid);
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
