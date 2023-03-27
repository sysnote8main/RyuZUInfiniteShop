package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.admin.MythicListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.TradeUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShopTrade {
    public static final HashMap<ShopTrade, UUID> tradeUUID = new HashMap<>();
    public static final Table<UUID, UUID, Integer> tradeCounts = HashBasedTable.create();
    public static final HashMap<UUID, Integer> tradeLimits = new HashMap<>();

    public enum TradeResult {notAfford, Full, Success, Lack, Limited}

    public List<Object> giveData;
    public List<Object> takeData;

    public ConfigurationSection getConfig() {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("give", giveData);
        config.set("take", takeData);
        if (tradeUUID.containsKey(this))
            config.set("uuid", tradeUUID.get(this));
        return config;
    }

    public ShopTrade(HashMap<String, Object> config) {
        this.giveData = (List<Object>) config.get("give");
        this.takeData = (List<Object>) config.get("take");
        if (config.containsKey("limit"))
            tradeUUID.put(this, UUID.fromString((String) config.get("uuid")));
    }

    public ShopTrade(ItemStack[] give, ItemStack[] take) {
        this.giveData = getItemsConfiguration(give);
        this.takeData = getItemsConfiguration(take);
    }

    public ShopTrade(Inventory inv, int slot, Shop.ShopType type, int limit) {
        setTrade(inv, slot, type);
        setTradeLimits(limit);
    }

    private List<Object> getItemsConfiguration(ItemStack[] items) {
        return Arrays.stream(items).map(item -> {
            if (MythicListener.getID(item) != null)
                return new MythicItem(MythicListener.getID(item), item.getAmount());
            else
                return item;
        }).collect(Collectors.toList());
    }

    public ItemStack[] getGiveItems() {
        return getTradeItems(giveData);
    }

    public ItemStack[] getTakeItems() {
        return getTradeItems(takeData);
    }

    public int getLimit() {
        return tradeLimits.getOrDefault(tradeUUID.get(this), 0);
    }

    public Integer getTradeCount(Player player) {
        if(!tradeUUID.containsKey(this)) return 0;
        return tradeCounts.contains(player.getUniqueId(), tradeUUID.get(this)) ? tradeCounts.get(player.getUniqueId(), tradeUUID.get(this)) : 0;
    }

    public int getTradeLimit() {
        if(!tradeUUID.containsKey(this)) return 0;
        return tradeLimits.getOrDefault(tradeUUID.get(this), 0);
    }

    public int getCounts(Player p) {
        if(!tradeUUID.containsKey(this)) return 0;
        return tradeCounts.contains(p.getUniqueId(), tradeUUID.get(this)) ? tradeCounts.get(p.getUniqueId(), tradeUUID.get(this)) : 0;
    }

    public void addTradeCount(Player player) {
        if(!tradeUUID.containsKey(this)) return;
        tradeCounts.put(player.getUniqueId(), tradeUUID.get(this), getTradeCount(player) + 1);
    }

    public void setTradeCount(Player player, int count) {
        tradeCounts.put(player.getUniqueId(), tradeUUID.get(this), count);
    }

    public void setTradeLimits(int count) {
        if(count == 0) {
            tradeLimits.remove(tradeUUID.get(this));
            tradeUUID.remove(this);
        }
        else {
            if(!tradeLimits.containsKey(tradeUUID.get(this))) tradeUUID.put(this, UUID.randomUUID());
            tradeLimits.put(tradeUUID.get(this), count);
        }
    }

    private ItemStack getFilter() {
        return ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.BLACK + "");
    }

    private ItemStack getFilter(ShopHolder.ShopMode mode) {
        return mode.equals(ShopHolder.ShopMode.Edit) ? getSettingsFilter() : getFilter();
    }

    private ItemStack getSettingsFilter() {
        return getSettingsFilter(getLimit());
    }

    private ItemStack getSettingsFilter(int value) {
        return PersistentUtil.setNMSTag(ItemUtil.getNamedItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "取引上限設定と取引のアイテム化",
                                                              ChatColor.GREEN + "クリック: 取引上限設定" + ChatColor.YELLOW + " 取引上限: " + value,
                                                              ChatColor.GREEN + "シフトクリック: 取引のアイテム化"
        ), "TradeLimit", String.valueOf(value));
    }

    public ItemStack changeLimit(int variation) {
        int value = Math.max(getLimit() + variation, 0);
        tradeLimits.put(tradeUUID.get(this), value);
        return getSettingsFilter(value);
    }

    public ItemStack[] getTradeItems(Shop.ShopType type, ShopHolder.ShopMode mode) {
        ItemStack[] items;
        ItemStack filter = getFilter(mode);
        switch (type) {
            case TwotoOne:
                items = new ItemStack[4];
                items[2] = filter;
                for (int i = 0; i < getTakeItems().length; i++) {
                    items[i] = getTakeItems()[i];
                }
                for (int i = 0; i < getGiveItems().length; i++) {
                    items[i + 3] = getGiveItems()[i];
                }
                break;
            case FourtoFour:
                items = new ItemStack[9];
                items[4] = filter;
                for (int i = 0; i < getTakeItems().length; i++) {
                    items[i] = getTakeItems()[i];
                }
                for (int i = 0; i < getGiveItems().length; i++) {
                    items[i + 5] = getGiveItems()[i];
                }
                break;
            case SixtoTwo:
                items = new ItemStack[9];
                items[6] = filter;
                for (int i = 0; i < getTakeItems().length; i++) {
                    items[i] = getTakeItems()[i];
                }
                for (int i = 0; i < getGiveItems().length; i++) {
                    items[i + 7] = getGiveItems()[i];
                }
                break;
            default:
                items = new ItemStack[0];
                break;
        }
        return items;
    }

    private ItemStack[] getTradeItems(List<Object> data) {
        return data.stream().map(item -> {
            if (item instanceof MythicItem)
                return ((MythicItem) item).convertItemStack();
            else
                return item;
        }).toArray(ItemStack[]::new);
    }

    public void setTrade(Inventory inv, int slot, Shop.ShopType type) {
        ShopTrade trade = TradeUtil.getTrade(inv, slot, type);
        if (trade == null) return;
        this.takeData = trade.takeData;
        this.giveData = trade.giveData;
    }

    public TradeResult getResult(Player p) {
        TradeResult result = TradeResult.Success;

        if (!affordTrade(p)) result = TradeResult.notAfford;
        else if (isLimited(p)) result = TradeResult.Limited;
        else if (!hasEnoughSpace(p)) result = TradeResult.Full;
        return result;
    }

    public TradeResult trade(Player p) {
        Inventory inv = p.getInventory();
        TradeResult result = getResult(p);

        //アイテムを追加する
        if (result == TradeResult.Success) {
            inv.removeItem(getTakeItems());
            inv.addItem(getGiveItems());
            addTradeCount(p);
        }
        return result;
    }

    public TradeResult trade(Player p, int times) {
        TradeResult result = TradeResult.Success;
        for (int time = 0; time < times; time++) {
            result = trade(p);
            if (!result.equals(TradeResult.Success)) {
                if (time != 0)
                    result = TradeResult.Lack;
                else
                    result = TradeResult.notAfford;
                break;
            }
        }
        //結果に対するエフェクトを表示
        playResultEffect(p, result);
        return result;
    }

    //アイテムを追加できるかチェックする
    private boolean hasEnoughSpace(Player p) {
        return ItemUtil.ableGive(p.getInventory(), getGiveItems());
    }

    //アイテムを所持しているか確認する
    private boolean affordTrade(Player p) {
        return ItemUtil.contains(p.getInventory(), getTakeItems());
    }

    private boolean isLimited(Player p) {
        return tradeUUID.containsKey(this) && getCounts(p) == tradeLimits.get(tradeUUID.get(this));
    }

    public void playResultEffect(Player p, TradeResult result) {
        switch (result) {
            case notAfford:
                p.sendMessage(ChatColor.RED + "アイテムが足りません");
                SoundUtil.playFailSound(p);
                break;
            case Limited:
                p.sendMessage(ChatColor.RED + "取引上限です");
                SoundUtil.playFailSound(p);
                break;
            case Lack:
                p.sendMessage(ChatColor.RED + "すべてを購入できませんでした");
                SoundUtil.playCautionSound(p);
                break;
            case Full:
                p.sendMessage(ChatColor.RED + "インベントリに十分な空きがありません");
                SoundUtil.playCautionSound(p);
                break;
            case Success:
                SoundUtil.playClickShopSound(p);
                break;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ShopTrade) {
            ShopTrade trade = (ShopTrade) obj;
            return trade.giveData.equals(this.giveData) && trade.takeData.equals(this.takeData);
        }
        return false;
    }

}
