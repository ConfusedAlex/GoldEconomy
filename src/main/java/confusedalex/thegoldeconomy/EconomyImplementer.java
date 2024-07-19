package confusedalex.thegoldeconomy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class EconomyImplementer implements Economy {
  TheGoldEconomy plugin;
  Bank bank;
  Converter converter;
  ResourceBundle bundle;
  Util util;

  public EconomyImplementer(TheGoldEconomy plugin, ResourceBundle bundle, Util util) {
    this.plugin = plugin;
    this.bundle = bundle;
    this.util = util;
    bank = new Bank(plugin, this);
    converter = new Converter(this, bundle);
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String getName() {
    return "TheGoldEconomy";
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
    return v + " Gold";
  }

  @Override
  public String currencyNamePlural() {
    return "Gold";
  }

  @Override
  public String currencyNameSingular() {
    return "Gold";
  }

  @Override
  public boolean hasAccount(String s) {
    if (Util.isOfflinePlayer(s) != null) return true;
    if (bank.getFakeAccounts().containsKey(s)) return true;
    else if (bank.fakeAccountsFile.contains(s)) return true;

    bank.fakeAccountsFile.set(s, 0);
    bank.getFakeAccounts().put(s, 0);
    return true;
  }

  @Override
  public boolean hasAccount(OfflinePlayer offlinePlayer) {
    return true;
  }

  @Override
  public boolean hasAccount(String s, String s1) {
    if (Util.isOfflinePlayer(s) != null) return true;
    if (bank.getFakeAccounts().containsKey(s)) return true;
    else if (bank.fakeAccountsFile.contains(s)) return true;

    bank.fakeAccountsFile.set(s, 0);
    bank.getFakeAccounts().put(s, 0);
    return true;
  }

  @Override
  public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
    return true;
  }

  @Override
  public double getBalance(String s) {
    try {
      if (Bukkit.getPlayer(UUID.fromString(s)) != null) return bank.getTotalPlayerBalance(s);
    } catch (IllegalArgumentException e) {
      // String is not UUID
    }
    if (Util.isOfflinePlayer(s) != null)
      return bank.getTotalPlayerBalance(Bukkit.getOfflinePlayer(s).getUniqueId().toString());
    return bank.getFakeBalance(s);
  }

  @Override
  public double getBalance(OfflinePlayer offlinePlayer) {
    if (offlinePlayer != null) return bank.getTotalPlayerBalance(offlinePlayer.getUniqueId().toString());
    return 0;
  }

  @Override
  public double getBalance(String s, String s1) {
    try {
      if (Bukkit.getPlayer(UUID.fromString(s)) != null) return bank.getTotalPlayerBalance(s);
    } catch (IllegalArgumentException e) {
      // String is not UUID
    }
    if (Util.isOfflinePlayer(s) != null)
      return bank.getTotalPlayerBalance(Bukkit.getOfflinePlayer(s).getUniqueId().toString());
    return bank.getFakeBalance(s);
  }

  @Override
  public double getBalance(OfflinePlayer offlinePlayer, String s) {
    if (offlinePlayer != null) return bank.getTotalPlayerBalance(offlinePlayer.getUniqueId().toString());
    return 0;
  }

  @Override
  public boolean has(String s, double v) {
    if (Util.isOfflinePlayer(s) != null)
      return v < bank.getTotalPlayerBalance(Bukkit.getOfflinePlayer(s).getUniqueId().toString());
    else return v < bank.getFakeBalance(s);
  }

  @Override
  public boolean has(OfflinePlayer offlinePlayer, double v) {
    return v < bank.getTotalPlayerBalance(offlinePlayer.getUniqueId().toString());
  }

  @Override
  public boolean has(String s, String s1, double v) {
    if (Util.isOfflinePlayer(s) != null)
      return v < bank.getTotalPlayerBalance(Bukkit.getOfflinePlayer(s).getUniqueId().toString());
    else return v < bank.getFakeBalance(s);
  }

  @Override
  public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
    return v < bank.getTotalPlayerBalance(offlinePlayer.getUniqueId().toString());
  }

  @Override
  public EconomyResponse withdrawPlayer(String s, double amount) {
    int oldBalance = 0;

    // if amount is negative return
    if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

    if (Util.isOfflinePlayer(s) != null) {
      OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
      String uuid = offlinePlayer.getUniqueId().toString();

      // if player is online
      if (offlinePlayer.isOnline()) {
        Player player = offlinePlayer.getPlayer();

        if (player == null)
          return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        // get balance and InventoryValue from Player
        int oldBankBalance = bank.getAccountBalance(uuid);
        int oldInventoryBalance = converter.getInventoryValue(player);


        // If balance + InventoryValue is < amount, return
        if (amount > oldBankBalance + oldInventoryBalance)
          return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "Not enough Money!");
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
        // When player is offline
        oldBalance = bank.getTotalPlayerBalance(uuid);
        int newBalance = (int) (oldBalance - amount);
        bank.setBalance(uuid, newBalance);
        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
      }
    } else {
      oldBalance = bank.getFakeBalance(s);
      int newBalance = (int) (oldBalance - amount);
      bank.setBalance(s, newBalance);
      return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
    }
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

      if (player == null)
        return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

      // get Balance and InventoryValue
      int oldBankBalance = bank.getAccountBalance(uuid);
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
      // if offline or fakeAccount
      oldBalance = bank.getTotalPlayerBalance(uuid);
      int newBalance = (int) (oldBalance - amount);
      bank.setBalance(uuid, newBalance);

      return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
    }
  }

  @Override
  public EconomyResponse withdrawPlayer(String s, String s1, double amount) {
    int oldBalance = 0;

    // if amount is negative return
    if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

    if (Util.isOfflinePlayer(s) != null) {
      OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
      String uuid = offlinePlayer.getUniqueId().toString();

      // if player is online
      if (offlinePlayer.isOnline()) {
        Player player = offlinePlayer.getPlayer();

        if (player == null)
          return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        // get balance and InventoryValue from Player
        int oldBankBalance = bank.getAccountBalance(uuid);
        int oldInventoryBalance = converter.getInventoryValue(player);


        // If balance + InventoryValue is < amount, return
        if (amount > oldBankBalance + oldInventoryBalance)
          return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "Not enough Money!");
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
        // When player is offline
        oldBalance = bank.getTotalPlayerBalance(uuid);
        int newBalance = (int) (oldBalance - amount);
        bank.setBalance(uuid, newBalance);
        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
      }
    } else {
      oldBalance = bank.getFakeBalance(s);
      int newBalance = (int) (oldBalance - amount);
      bank.setBalance(s, newBalance);
      return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
    }
  }

  @Override
  public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double amount) {
    String uuid = offlinePlayer.getUniqueId().toString();
    Player player;
    int oldBalance = 0;

    // if amount is negative return
    if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

    // if player is online
    if (offlinePlayer.isOnline()) {
      player = offlinePlayer.getPlayer();

      if (player == null)
        return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

      // get Balance and InventoryValue
      int oldBankBalance = bank.getAccountBalance(uuid);
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
      // if offline
      oldBalance = bank.getTotalPlayerBalance(uuid);
      int newBalance = (int) (oldBalance - amount);
      bank.setBalance(uuid, newBalance);

      return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
    }
  }

  @Override
  public EconomyResponse depositPlayer(String s, double amount) {
    int oldBalance = 0;

    // If amount is negative -> return
    if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

    if (Util.isOfflinePlayer(s) != null) {
      OfflinePlayer player = Bukkit.getOfflinePlayer(s);
      String uuid = player.getUniqueId().toString();

      // Getting balance and calculating new Balance
      oldBalance = bank.getAccountBalance(uuid);
      int newBalance = (int) (oldBalance + amount);
      bank.setBalance(uuid, newBalance);
      return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
    } else {
      oldBalance = bank.getFakeBalance(s);
      int newBalance = (int) (oldBalance + amount);
      bank.setBalance(s, newBalance);
      return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
    }
  }

  @Override
  public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
    String uuid = offlinePlayer.getUniqueId().toString();
    int oldBalance = bank.getAccountBalance(uuid);
    int newBalance = (int) (amount + oldBalance);

    // If amount is negative -> return
    if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

    bank.setBalance(uuid, newBalance);
    return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
  }

  @Override
  public EconomyResponse depositPlayer(String s, String s1, double amount) {
    int oldBalance = 0;

    // If amount is negative -> return
    if (amount < 0) return new EconomyResponse(amount, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

    if (Util.isOfflinePlayer(s) != null) {
      OfflinePlayer player = Bukkit.getOfflinePlayer(s);
      String uuid = player.getUniqueId().toString();

      // Getting balance and calculating new Balance
      oldBalance = bank.getAccountBalance(uuid);
      int newBalance = (int) (oldBalance + amount);
      bank.setBalance(uuid, newBalance);
      return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
    } else {
      oldBalance = bank.getFakeBalance(s);
      int newBalance = (int) (oldBalance + amount);
      bank.setBalance(s, newBalance);
      return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
    }
  }

  @Override
  public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double amount) {
    String uuid = offlinePlayer.getUniqueId().toString();
    int oldBalance = bank.getAccountBalance(uuid);
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
