package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
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
    int rate;

    public TradeOption() {
        this.give = false;
        this.money = 0;
        this.limit = 0;
        this.rate = 100;
    }

    public TradeOption(boolean give, double money, int limit, int rate) {
        this.give = give;
        this.money = money;
        this.limit = limit;
        this.rate = rate;
    }

    public boolean isNoData() {
        return money == 0 && limit == 0 && rate == 100;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("give", give);
        result.put("money", money);
        result.put("limit", limit);
        result.put("rate", rate);
        return result;
    }

    public static TradeOption deserialize(Map<String, Object> map) {
        return new TradeOption((boolean) map.getOrDefault("give", "false"), (int) map.getOrDefault("money", 0), (int) map.getOrDefault("limit", 0), (int) map.getOrDefault("rate", 100));
    }

    public ItemStack getOptionsPanel(ItemStack panel) {
        if (money != 0) {
            panel = NBTUtil.setNMSTag(panel, "Give", String.valueOf(give));
            panel = NBTUtil.setNMSTag(panel, "Money", String.valueOf(money));
            ItemUtil.withLore(panel,
                    ChatColor.GREEN + "取引方法: " + ChatColor.YELLOW + (give ? "受け取り" : "支払い"),
                    ChatColor.GREEN + "取引金額: " + ChatColor.YELLOW + VaultHandler.getInstance().format(money)
            );
        }
        if(limit != 0) {
            panel = NBTUtil.setNMSTag(panel, "Limit", String.valueOf(limit));
            ItemUtil.withLore(panel,
                    ChatColor.GREEN + "取引上限: " + ChatColor.YELLOW + limit
            );
        }
        if(rate != 100) {
            panel = NBTUtil.setNMSTag(panel, "Rate", String.valueOf(rate));
            ItemUtil.withLore(panel,
                    ChatColor.GREEN + "取引成功確率: " + ChatColor.YELLOW + rate + "%"
            );
        }
        return panel;
    }

    public static TradeOption getOption(ItemStack item) {
        TradeOption option = new TradeOption();
        option.setGive(Boolean.parseBoolean(JavaUtil.getOrDefault(NBTUtil.getNMSStringTag(item, "Give"), "false")));
        option.setMoney(Double.parseDouble(JavaUtil.getOrDefault(NBTUtil.getNMSStringTag(item, "Money"), "0")));
        option.setLimit(Integer.parseInt(JavaUtil.getOrDefault(NBTUtil.getNMSStringTag(item, "Limit"), "0")));
        option.setRate(Integer.parseInt(JavaUtil.getOrDefault(NBTUtil.getNMSStringTag(item, "Rate"), "100")));
        return option;
    }
}
