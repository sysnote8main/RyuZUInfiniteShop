package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.JavaUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.VaultHandler;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class TradeOption implements ConfigurationSerializable {
    boolean give;
    double money;
    int limit;
    boolean hide;
    int rate;

    public TradeOption() {
        this.give = false;
        this.money = 0;
        this.limit = 0;
        this.hide = false;
        this.rate = 100;
    }

    public TradeOption(boolean give, double money, int limit, boolean hide, int rate) {
        this.give = give;
        this.money = money;
        this.limit = limit;
        this.hide = hide;
        this.rate = rate;
    }

    public boolean isNoData() {
        return money == 0 && limit == 0 && rate == 100;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        if(money != 0) {
            result.put("give", give);
            result.put("money", money);
        }
        if(limit != 0) result.put("limit", limit);
        if(rate != 100) {
            result.put("hide", hide);
            result.put("rate", rate);
        }
        return result;
    }

    public static TradeOption deserialize(Map<String, Object> map) {
        return new TradeOption((boolean) map.getOrDefault("give", false), (double) map.getOrDefault("money", 0d), (int) map.getOrDefault("limit", 0),(boolean) map.getOrDefault("hide", false), (int) map.getOrDefault("rate", 100));
    }

    public ItemStack getOptionsPanel(ItemStack panel) {
        return getOptionsPanel(panel, null, null);
    }

    public ItemStack getOptionsPanel(ItemStack panel, Player p, ShopTrade trade) {
        if (money != 0) {
            panel = NBTUtil.setNMSTag(panel, "Give", String.valueOf(give));
            panel = NBTUtil.setNMSTag(panel, "Money", String.valueOf(money));
            ItemUtil.withLore(panel,
                    (give ? ChatColor.GREEN + LanguageKey.ITEM_OPTIONS_MONEY_RECEIVE.getMessage() : ChatColor.RED + LanguageKey.ITEM_OPTIONS_MONEY_PAY.getMessage()) +
                            ChatColor.GREEN + " " + LanguageKey.ITEM_OPTIONS_MONEY_AMOUNT.getMessage() + ": " + ChatColor.YELLOW + VaultHandler.getInstance().format(money)
            );
        }
        if(limit != 0) {
            panel = NBTUtil.setNMSTag(panel, "Limit", String.valueOf(limit));
            ItemUtil.withLore(panel,
                    ChatColor.YELLOW + LanguageKey.ITEM_OPTIONS_LIMIT.getMessage(p == null ? "" : trade.getTradeCount(p) , limit)
            );
        }
        if(rate != 100) {
            panel = NBTUtil.setNMSTag(panel, "Rate", String.valueOf(rate));
            panel = NBTUtil.setNMSTag(panel, "Hide", String.valueOf(hide));
            ItemUtil.withLore(panel,
                    ChatColor.YELLOW + LanguageKey.ITEM_OPTIONS_RATE_SUCCESS.getMessage(((p != null && hide) ? "?" : rate)) +
                            (p == null ? "" : (" " + (hide ? ChatColor.RED + LanguageKey.ITEM_OPTIONS_RATE_HIDE.getMessage() : ChatColor.GREEN + LanguageKey.ITEM_OPTIONS_RATE_SHOW.getMessage())))
            );
        }
        return panel;
    }

    public static TradeOption getOption(ItemStack item) {
        TradeOption option = new TradeOption();
        option.setGive(Boolean.parseBoolean(JavaUtil.getOrDefault(NBTUtil.getNMSStringTag(item, "Give"), "false")));
        option.setMoney(Double.parseDouble(JavaUtil.getOrDefault(NBTUtil.getNMSStringTag(item, "Money"), "0")));
        option.setLimit(Integer.parseInt(JavaUtil.getOrDefault(NBTUtil.getNMSStringTag(item, "Limit"), "0")));
        option.setHide(Boolean.parseBoolean(JavaUtil.getOrDefault(NBTUtil.getNMSStringTag(item, "Hide"), "false")));
        option.setRate(Integer.parseInt(JavaUtil.getOrDefault(NBTUtil.getNMSStringTag(item, "Rate"), "100")));
        return option;
    }
}
