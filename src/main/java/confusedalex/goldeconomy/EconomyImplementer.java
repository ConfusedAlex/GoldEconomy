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
    public EconomyResponse withdrawPlayer(String s, double v) {
        EconomyResponse response;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(s);
        Player player;
        int oldBalance = 0;

        // if < 0 return
        if (v < 0) return new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        if (offlinePlayer != null){
           String uuid = offlinePlayer.getUniqueId().toString();
           if (offlinePlayer.isOnline()){
               player = offlinePlayer.getPlayer();

               int oldBankBalance = bank.getPlayerBank().get(uuid);
               int oldInventoryBalance = 0;
               if (player != null) {
                   oldInventoryBalance = converter.getInventoryValue(player);
               }
               if (v > oldBankBalance + oldInventoryBalance) return new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
               if (oldBankBalance - v > 0){
                   response = new EconomyResponse(v, (oldBankBalance - v), EconomyResponse.ResponseType.SUCCESS, "");
                   bank.setBalance(uuid, (int) (oldBankBalance - v));
               } else {
                   int diff = (int) (v - oldBankBalance);
                   bank.setBalance(uuid, 0);
                   if (player != null) {
                       converter.remove(player, diff);
                   }
                   return new EconomyResponse(v, oldInventoryBalance - v, EconomyResponse.ResponseType.SUCCESS, "");
               }
           } else {
               oldBalance = bank.getPlayerBank().get(uuid);
               int newBalance = (int) (oldBalance - v);
               response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
               bank.setBalance(uuid, newBalance);
           }
        } else {
           oldBalance = bank.getBalance(s);
           int newBalance = (int) (oldBalance - v);

           if (v > 0) {
               response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
               bank.getFakeAccounts().put(s, newBalance);
           } else {
               response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
           }
        }
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        String uuid = offlinePlayer.getUniqueId().toString();
        Player player;
        int oldBalance = 0;

        if (v < 0){
            return new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
        }

        if (offlinePlayer.isOnline()) {
            player = offlinePlayer.getPlayer();

            int oldBankBalance = bank.getBalance(uuid);
            int oldInventoryBalance = 0;
            if (player != null) {
                oldInventoryBalance = converter.getInventoryValue(player);
            }
            if (v > oldBankBalance + oldInventoryBalance)
                return new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
            if (oldBankBalance - v > 0) {
                bank.setBalance(uuid, (int) (oldBankBalance - v));
                return new EconomyResponse(v, (oldBankBalance - v), EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                int diff = (int) (v - oldBankBalance);
                bank.setBalance(uuid, 0);
                if (player != null) {
                    converter.remove(player, diff);
                }
                return new EconomyResponse(v, oldInventoryBalance - v, EconomyResponse.ResponseType.SUCCESS, "");
            }
        } else {
            oldBalance = bank.getBalance(uuid);
            int newBalance = (int) (oldBalance - v);
            bank.setBalance(uuid, newBalance);
            return new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        EconomyResponse response;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(s);
        Player player;
        int oldBalance = 0;

        // if < 0 return
        if (v < 0) return new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");

        if (offlinePlayer != null){
            String uuid = offlinePlayer.getUniqueId().toString();
            if (offlinePlayer.isOnline()){
                player = offlinePlayer.getPlayer();

                int oldBankBalance = bank.getPlayerBank().get(uuid);
                int oldInventoryBalance = 0;
                if (player != null) {
                    oldInventoryBalance = converter.getInventoryValue(player);
                }
                if (v > oldBankBalance + oldInventoryBalance) return new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
                if (oldBankBalance - v > 0){
                    response = new EconomyResponse(v, (oldBankBalance - v), EconomyResponse.ResponseType.SUCCESS, "");
                    bank.setBalance(uuid, (int) (oldBankBalance - v));
                } else {
                    int diff = (int) (v - oldBankBalance);
                    bank.setBalance(uuid, 0);
                    assert player != null;
                    converter.remove(player, diff);
                    return new EconomyResponse(v, oldInventoryBalance - v, EconomyResponse.ResponseType.SUCCESS, "");
                }
            } else {
                oldBalance = bank.getPlayerBank().get(uuid);
                int newBalance = (int) (oldBalance - v);
                response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
                bank.setBalance(uuid, newBalance);
            }
        } else {
            oldBalance = bank.getBalance(s);
            int newBalance = (int) (oldBalance - v);

            if (v > 0) {
                response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
                bank.getFakeAccounts().put(s, newBalance);
            } else {
                response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
            }
        }
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v){
        String uuid = offlinePlayer.getUniqueId().toString();
            Player player;
            int oldBalance = 0;

        if (offlinePlayer.isOnline()) {
            player = offlinePlayer.getPlayer();

            int oldBankBalance = bank.getPlayerBank().get(uuid);
            int oldInventoryBalance = 0;
            if (player != null) {
                oldInventoryBalance = converter.getInventoryValue(player);
            }
            if (v > oldBankBalance + oldInventoryBalance)
                return new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
            if (oldBankBalance - v > 0) {
                bank.setBalance(uuid, (int) (oldBankBalance - v));
                return new EconomyResponse(v, (oldBankBalance - v), EconomyResponse.ResponseType.SUCCESS, "");
            } else {
                int diff = (int) (v - oldBankBalance);
                bank.setBalance(uuid, 0);
                if (player != null) {
                    converter.remove(player, diff);
                }
                return new EconomyResponse(v, oldInventoryBalance - v, EconomyResponse.ResponseType.SUCCESS, "");
            }
        } else {
            oldBalance = bank.getPlayerBank().get(uuid);
            int newBalance = (int) (oldBalance - v);
            bank.setBalance(uuid, newBalance);
            return new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        EconomyResponse response;
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(s);
        if (player != null){
            String uuid = player.getUniqueId().toString();

            int oldBalance = bank.getPlayerBank().get(uuid);
            int newBalance = (int) (oldBalance + v);

            if (v < 0){
                response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
            } else {
                response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
                bank.getPlayerBank().put(uuid, newBalance);
            }
        } else {
            int oldBalance = bank.getBalance(s);
            int newBalance = (int) (oldBalance + v);

            if (v > 0) {
                response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
                bank.getFakeAccounts().put(s, newBalance);
            } else {
                response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
            }
        }
        return response;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        EconomyResponse response;
        String uuid = offlinePlayer.getUniqueId().toString();
        int oldBalance = bank.getBalance(uuid);
        int newBalance = (int) (v + oldBalance);

        if (v < 0){
            response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
        } else {
            response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
            bank.setBalance(uuid, newBalance);
        }

        return response;
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        EconomyResponse response;
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(s);
        if (player != null){
            String uuid = player.getUniqueId().toString();

            int oldBalance = bank.getBalance(uuid);
            int newBalance = (int) (oldBalance + v);

            if (v < 0){
                response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
            } else {
                response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
                bank.setBalance(uuid, newBalance);
            }
        } else {
            int oldBalance = bank.getBalance(s);
            int newBalance = (int) (oldBalance + v);

            if (v > 0) {
                response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
                bank.setBalance(s, newBalance);
            } else {
                response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
            }
        }
        return response;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        EconomyResponse response;
        String uuid = offlinePlayer.getUniqueId().toString();
        int oldBalance = bank.getPlayerBank().get(uuid);
        int newBalance = (int) (v + oldBalance);

        if (v < 0){
            response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
        } else {
            response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
            bank.setBalance(uuid, newBalance);
        }

        return response;
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
