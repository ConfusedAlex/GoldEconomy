package confusedalex.goldeconomy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class EconomyImplementer implements Economy {
    GoldEconomy plugin;
    Bank bank;
    Converter converter;

    public EconomyImplementer(GoldEconomy plugin) {
        this.plugin = plugin;
        bank = new Bank(plugin, this);
        converter = new Converter(this);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return "Gold";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return null;
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    @Override
    public double getBalance(String s) {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(s);

        if (player == null) {
              return bank.getBalance(s);
        } else {
            return bank.getBalance(player.getUniqueId().toString());
        }
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        if (offlinePlayer != null) {
            return bank.getBalance(offlinePlayer.getUniqueId().toString());
        }
        return 0;
    }

    @Override
    public double getBalance(String s, String s1) {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(s);

        if (player == null) {
            return bank.getBalance(s);
        } else {
            return bank.getBalance(player.getUniqueId().toString());
        }
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        if (offlinePlayer != null) {
            return bank.getBalance(offlinePlayer.getUniqueId().toString());
        }
        return 0;
    }

    @Override
    public boolean has(String s, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return false;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double amount) {
        EconomyResponse response;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(s);
        Player player;
        int oldBalance = 0;

        // if amount is negative return
        if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        // if player exists
        if (offlinePlayer != null){
           String uuid = offlinePlayer.getUniqueId().toString();
           // if player is online
           if (offlinePlayer.isOnline()){
               player = offlinePlayer.getPlayer();

               if (player == null) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

               // get balance and InventoryValue from Player
               int oldBankBalance = bank.getBalance(uuid);
               int oldInventoryBalance = converter.getInventoryValue(player);


               // If balance + InventoryValue is < amount, return
               if (amount > oldBankBalance + oldInventoryBalance) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "Not enough Money!");
               // If bank balances is enough to cover amount
               if (oldBankBalance - amount > 0){
                   response = new EconomyResponse(amount, (oldBankBalance - amount), EconomyResponse.ResponseType.SUCCESS, "");
                   bank.setBalance(uuid, (int) (oldBankBalance - amount));
               } else {
                   // Set balance to 0 and cover rest of the costs with Inventory Funds
                   int diff = (int) (amount - oldBankBalance);
                   bank.setBalance(uuid, 0);
                   converter.remove(player, diff);

                   return new EconomyResponse(amount, oldInventoryBalance - amount, EconomyResponse.ResponseType.SUCCESS, "");
               }
           } else {
               // When player is offline
               oldBalance = bank.getBalance(uuid);
               int newBalance = (int) (oldBalance - amount);
               response = new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
               bank.setBalance(uuid, newBalance);
           }
        } else {
            // if fakeAccount
           oldBalance = bank.getBalance(s);
           int newBalance = (int) (oldBalance - amount);

           if (amount > 0) {
               response = new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
               bank.setBalance(s, newBalance);
           } else {
               response = new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
           }
        }
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        String uuid = offlinePlayer.getUniqueId().toString();
        Player player;
        int oldBalance = 0;

        // if amount is negative return
        if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        // if player is online
        if (offlinePlayer.isOnline()) {
            player = offlinePlayer.getPlayer();

            if (player == null) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

            // get Balance and InventoryValue
            int oldBankBalance = bank.getBalance(uuid);
            int oldInventoryBalance = converter.getInventoryValue(player);

            // If balance + InventoryValue is < amount, return
            if (amount > oldBankBalance + oldInventoryBalance)
                return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
            // If bank balances is enough to cover amount
            if (oldBankBalance - amount > 0) {
                bank.setBalance(uuid, (int) (oldBankBalance - amount));
                return new EconomyResponse(amount, (oldBankBalance - amount), EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                // Set balance to 0 and cover rest of the costs with Inventory Funds
                int diff = (int) (amount - oldBankBalance);
                bank.setBalance(uuid, 0);
                converter.remove(player, diff);
                return new EconomyResponse(amount, oldInventoryBalance - amount, EconomyResponse.ResponseType.SUCCESS, "");
            }
        } else {
            // if FakeAccount
            oldBalance = bank.getBalance(uuid);
            int newBalance = (int) (oldBalance - amount);
            bank.setBalance(uuid, newBalance);

            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double amount) {
        EconomyResponse response;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(s);
        Player player;
        int oldBalance = 0;

        // if amount is negative return
        if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        // if player exists
        if (offlinePlayer != null){
            String uuid = offlinePlayer.getUniqueId().toString();
            // if player is online
            if (offlinePlayer.isOnline()){
                player = offlinePlayer.getPlayer();

                if (player == null) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

                // get balance and inventoryvalue from Player
                int oldBankBalance = bank.getBalance(uuid);
                int oldInventoryBalance = converter.getInventoryValue(player);


                // If balance + InventoryValue is < amount, return
                if (amount > oldBankBalance + oldInventoryBalance) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "Not enough Money!");
                // If bank balances is enough to cover amount
                if (oldBankBalance - amount > 0){
                    response = new EconomyResponse(amount, (oldBankBalance - amount), EconomyResponse.ResponseType.SUCCESS, "");
                    bank.setBalance(uuid, (int) (oldBankBalance - amount));
                } else {
                    // Set balance to 0 and cover rest of the costs with Inventory Funds
                    int diff = (int) (amount - oldBankBalance);
                    bank.setBalance(uuid, 0);
                    converter.remove(player, diff);

                    return new EconomyResponse(amount, oldInventoryBalance - amount, EconomyResponse.ResponseType.SUCCESS, "");
                }
            } else {
                // When player is offline
                oldBalance = bank.getBalance(uuid);
                int newBalance = (int) (oldBalance - amount);
                response = new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
                bank.setBalance(uuid, newBalance);
            }
        } else {
            // if fakeAccount
            oldBalance = bank.getBalance(s);
            int newBalance = (int) (oldBalance - amount);

            if (amount > 0) {
                response = new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
                bank.setBalance(s, newBalance);
            } else {
                response = new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
            }
        }
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double amount){
        String uuid = offlinePlayer.getUniqueId().toString();
        Player player;
        int oldBalance = 0;

        // if amount is negative return
        if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        // if player is online
        if (offlinePlayer.isOnline()) {
            player = offlinePlayer.getPlayer();

            if (player == null) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

            // get Balance and InventoryValue
            int oldBankBalance = bank.getBalance(uuid);
            int oldInventoryBalance = converter.getInventoryValue(player);

            // If balance + InventoryValue is < amount, return
            if (amount > oldBankBalance + oldInventoryBalance)
                return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
            // If bank balances is enough to cover amount
            if (oldBankBalance - amount > 0) {
                bank.setBalance(uuid, (int) (oldBankBalance - amount));
                return new EconomyResponse(amount, (oldBankBalance - amount), EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                // Set balance to 0 and cover rest of the costs with Inventory Funds
                int diff = (int) (amount - oldBankBalance);
                bank.setBalance(uuid, 0);
                converter.remove(player, diff);

                return new EconomyResponse(amount, oldInventoryBalance - amount, EconomyResponse.ResponseType.SUCCESS, "");
            }
        } else {
            // if FakeAccount
            oldBalance = bank.getBalance(uuid);
            int newBalance = (int) (oldBalance - amount);
            bank.setBalance(uuid, newBalance);

            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String s, double amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(s);
        int oldBalance = 0;

        // If amount is negative -> return
        if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        // If player not null
        if (player != null){
            String uuid = player.getUniqueId().toString();

            // Getting balance and calulating new Balance
            oldBalance = bank.getBalance(uuid);
            int newBalance = (int) (oldBalance + amount);

            bank.setBalance(uuid, newBalance);
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            // if fakeAccount
            // Getting balance and calulating new Balance
            oldBalance = bank.getBalance(s);
            int newBalance = (int) (oldBalance + amount);

            bank.getFakeAccounts().put(s, newBalance);
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        String uuid = offlinePlayer.getUniqueId().toString();
        int oldBalance = bank.getBalance(uuid);
        int newBalance = (int) (amount + oldBalance);

        // If amount is negative -> return
        if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        bank.setBalance(uuid, newBalance);
        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double amount) {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(s);
        int oldBalance = 0;

        // If amount is negative -> return
        if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        // If player not null
        if (player != null){
            String uuid = player.getUniqueId().toString();

            // Getting balance and calulating new Balance
            oldBalance = bank.getBalance(uuid);
            int newBalance = (int) (oldBalance + amount);

            bank.setBalance(uuid, newBalance);
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        } else {
            // if fakeAccount
            // Getting balance and calulating new Balance
            oldBalance = bank.getBalance(s);
            int newBalance = (int) (oldBalance + amount);

            bank.getFakeAccounts().put(s, newBalance);
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double amount) {
        String uuid = offlinePlayer.getUniqueId().toString();
        int oldBalance = bank.getBalance(uuid);
        int newBalance = (int) (amount + oldBalance);

        // If amount is negative -> return
        if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        bank.setBalance(uuid, newBalance);
        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }
}
