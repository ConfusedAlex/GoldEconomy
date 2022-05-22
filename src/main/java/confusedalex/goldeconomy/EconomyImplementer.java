package confusedalex.goldeconomy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class EconomyImplementer implements Economy {
    private final GoldEconomy plugin;

    public EconomyImplementer(GoldEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return null;
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
        Player player = Bukkit.getPlayer(s);
        String uuid = player.getUniqueId().toString();
        return plugin.getPlayerBank().get(uuid);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        String uuid = offlinePlayer.getUniqueId().toString();
        return plugin.getPlayerBank().get(uuid);
    }

    @Override
    public double getBalance(String s, String s1) {
        Player player = Bukkit.getPlayer(s);
        String uuid = player.getUniqueId().toString();
        return plugin.getPlayerBank().get(uuid);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        String uuid = offlinePlayer.getUniqueId().toString();
        return plugin.getPlayerBank().get(uuid);
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
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        EconomyResponse response;
        Player player = Bukkit.getPlayer(s);
        String uuid = player.getUniqueId().toString();
        double oldBalance = plugin.getPlayerBank().get(uuid);
        double newBalance = v + oldBalance;

        if (v < 0){
            response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
        } else {
            response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
            plugin.getPlayerBank().put(uuid, newBalance);
        }

        return response;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        EconomyResponse response;
        String uuid = offlinePlayer.getUniqueId().toString();
        double oldBalance = plugin.getPlayerBank().get(uuid);
        double newBalance = v + oldBalance;

        if (v < 0){
            response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
        } else {
            response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
            plugin.getPlayerBank().put(uuid, newBalance);
        }

        return response;
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        EconomyResponse response;
        Player player = Bukkit.getPlayer(s);
        String uuid = player.getUniqueId().toString();
        double oldBalance = plugin.getPlayerBank().get(uuid);
        double newBalance = v + oldBalance;

        if (v < 0){
            response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
        } else {
            response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
            plugin.getPlayerBank().put(uuid, newBalance);
        }

        return response;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        EconomyResponse response;
        String uuid = offlinePlayer.getUniqueId().toString();
        double oldBalance = plugin.getPlayerBank().get(uuid);
        double newBalance = v + oldBalance;

        if (v < 0){
            response = new EconomyResponse(v, oldBalance, EconomyResponse.ResponseType.FAILURE, "error");
        } else {
            response = new EconomyResponse(v, newBalance, EconomyResponse.ResponseType.SUCCESS, "");
            plugin.getPlayerBank().put(uuid, newBalance);
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
