package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import lombok.EqualsAndHashCode;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.Config;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.DisplayPanelConfig;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.ShopType;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item.ObjectItems;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.FileUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.TradeUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@EqualsAndHashCode
public class ShopTrade {
    public static final BiMap<ShopTrade, UUID> tradeUUID = HashBiMap.create();
    public static final Table<UUID, UUID, Integer> tradeCounts = HashBasedTable.create();
    public static final HashMap<UUID, TradeOption> tradeLimits = new HashMap<>();

    public enum TradeResult {NotAfford, Full, Success, Lack, Limited, Locked, Error, Normal}

    private ObjectItems giveData;
    private ObjectItems takeData;

    public ConfigurationSection serialize() {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("give", giveData.getObjects());
        config.set("take", takeData.getObjects());
        if (tradeUUID.containsKey(this))
            config.set("uuid", tradeUUID.get(this).toString());
        return config;
    }

    public ShopTrade(HashMap<String, Object> config) {
        this.giveData = new ObjectItems(config.get("give"));
        this.takeData = new ObjectItems(config.get("take"));
        if (config.containsKey("uuid"))
            tradeUUID.put(this, UUID.fromString((String) config.get("uuid")));
    }

    public ShopTrade(ItemStack[] give, ItemStack[] take) {
        this.giveData = new ObjectItems(give);
        this.takeData = new ObjectItems(take);
    }

    public ShopTrade(Inventory inv, int slot, ShopType type, int limit) {
        setTrade(inv, slot, type);
        setTradeLimits(limit, limit != 0);
    }

    public ItemStack[] getGiveItems() {
        return giveData.toItemStacks();
    }

    public ItemStack[] getTakeItems() {
        return takeData.toItemStacks();
    }

    public int getLimit() {
        if (!tradeUUID.containsKey(this)) return 0;
        return tradeLimits.getOrDefault(tradeUUID.get(this), new TradeOption()).getLimit();
    }

    public Integer getTradeCount(Player player) {
        if (!tradeUUID.containsKey(this)) return 0;
        return tradeCounts.contains(player.getUniqueId(), tradeUUID.get(this)) ? tradeCounts.get(player.getUniqueId(), tradeUUID.get(this)) : 0;
    }

    public int getCounts(Player p) {
        if (!tradeUUID.containsKey(this)) return 0;
        return tradeCounts.contains(p.getUniqueId(), tradeUUID.get(this)) ? tradeCounts.get(p.getUniqueId(), tradeUUID.get(this)) : 0;
    }

    public void addTradeCount(Player player) {
        if (!tradeUUID.containsKey(this)) return;
        tradeCounts.put(player.getUniqueId(), tradeUUID.get(this), getTradeCount(player) + 1);
    }

    public void setTradeCount(Player player, int count) {
        if (!tradeUUID.containsKey(this)) return;
        tradeCounts.put(player.getUniqueId(), tradeUUID.get(this), count);
    }

    public void saveTradeLimit() {
        if (getLimit() <= 0) return;
        File file = FileUtil.initializeFile("limits.yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        saveTradeLimit(config);

        try {
            config.save(file);
        } catch (IOException e) {
            if (!Config.readOnlyIgnoreIOException) e.printStackTrace();
        }
    }

    public void saveTradeLimit(YamlConfiguration config) {
        if (getLimit() <= 0) return;
        UUID tradeID = tradeUUID.get(this);
        config.set(tradeID.toString() + ".limit", getLimit());
        ShopTrade.tradeCounts.rowKeySet().forEach(playerID -> config.set(tradeID + ".counts." + playerID.toString(), ShopTrade.tradeCounts.get(playerID, tradeID)));
    }

    public static ItemStack getFilter() {
        return ItemUtil.getNamedItem(ItemUtil.getColoredItem("BLACK_STAINED_GLASS_PANE"), ChatColor.BLACK + "");
    }

    private ItemStack getFilter(ShopMode mode) {
        return mode.equals(ShopMode.EDIT) ? getSettingsFilter() : getFilter();
    }

    public static ItemStack getFilter(ShopMode mode, int value) {
        return mode.equals(ShopMode.EDIT) ? getSettingsFilter(value) : getFilter();
    }

    public ItemStack getFilter(String id, Player player) {
        Shop shop = ShopUtil.getShop(id);
        String page = String.valueOf(shop.getPage(this));
        TradeResult result = getResult(player, shop);
        boolean isAdmin = player.hasPermission("sis.op");
        return NBTUtil.setNMSTag(
                NBTUtil.setNMSTag(
                        ItemUtil.getNamedItem(
                                DisplayPanelConfig.getPanel(result).getItemStack(getLimit(), getTradeCount(player)),
                                ChatColor.GREEN + LanguageKey.ITEM_TRADE_WITH.getMessage(ShopUtil.getShop(id).getDisplayNameOrElseNone()),
                                false,
                                ChatColor.YELLOW + LanguageKey.INVENTORY_PAGE.getMessage(page),
                                isAdmin ? (ChatColor.YELLOW + LanguageKey.ITEM_EDITOR_IS_SEARCHABLE.getMessage(shop.isSearchable() ? ChatColor.GREEN + LanguageKey.ITEM_EDITOR_SEARCH_ENABLED.getMessage() : ChatColor.RED + LanguageKey.ITEM_EDITOR_SEARCH_DISABLED.getMessage())) : null,
                                isAdmin ? (ChatColor.YELLOW + LanguageKey.ITEM_IS_LOCKED.getMessage(shop.isLock() ? ChatColor.RED + LanguageKey.ITEM_EDITOR_LOCKED.getMessage() : ChatColor.GREEN + LanguageKey.ITEM_EDITOR_UNLOCKED.getMessage())) : null,
                                ChatColor.GREEN + LanguageKey.ITEM_TRADE_WINDOW_OPEN.getMessage(),
                                isAdmin ? null : ChatColor.GREEN + LanguageKey.ITEM_SEARCH_BY_VALUEORPRODUCT.getMessage(),
                                isAdmin ? ChatColor.GREEN + LanguageKey.ITEM_EDIT_WINDOW_OPEN.getMessage() : null
                        ),
                        "Shop", id
                ), "Page", page
        );
    }

    private ItemStack getSettingsFilter() {
        return getSettingsFilter(getLimit());
    }

    private static ItemStack getSettingsFilter(int value) {
        return NBTUtil.setNMSTag(ItemUtil.withLore(
                DisplayPanelConfig.getPanel(TradeResult.Normal).getItemStack(),
                ChatColor.GREEN + LanguageKey.ITEM_SETTINGS_TRADE_SET_LIMIT.getMessage() + ChatColor.YELLOW + " " + LanguageKey.ITEM_SETTINGS_TRADE_LIMIT.getMessage() + ": " + value,
                ChatColor.GREEN + LanguageKey.ITEM_SETTINGS_TRADE_TO_ITEM.getMessage()
        ), "TradeLimit", String.valueOf(value));
    }

    public void setTradeLimits(int limit, boolean force) {
        TradeOption data = tradeLimits.computeIfAbsent(tradeUUID.computeIfAbsent(this, key -> UUID.randomUUID()), key -> new TradeOption());
        if (force)
            data.setLimit(limit);
        else
            data.setLimit(Math.max(data.getLimit(), limit));
        if (data.isNoData()) {
            tradeLimits.remove(tradeUUID.get(this));
            tradeUUID.remove(this);
        }
    }

    public ItemStack changeLimit(int variation) {
        int value = Math.max(getLimit() + variation, 0);
        return getSettingsFilter(value);
    }

    public ItemStack[] getTradeItems(ShopType type, ShopMode mode) {
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

    public ItemStack[] getTradeItems(ShopType type, String id, Player player) {
        ItemStack[] items;
        ItemStack filter = getFilter(id, player);
        switch (type) {
            case TwotoOne:
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

    public void setTrade(Inventory inv, int slot, ShopType type) {
        ShopTrade trade = TradeUtil.getTrade(inv, slot, type);
        if (trade == null) return;
        this.takeData = trade.takeData;
        this.giveData = trade.giveData;
    }

    public TradeResult getResult(Player p) {
        TradeResult result = TradeResult.Success;

        if (!affordTrade(p)) result = TradeResult.NotAfford;
        else if (isError()) result = TradeResult.Error;
        else if (isLimited(p)) result = TradeResult.Limited;
        else if (!hasEnoughSpace(p)) result = TradeResult.Full;
        return result;
    }

    public TradeResult getResult(Player p, Shop shop) {
        TradeResult result = getResult(p);
        if (shop.isLock()) result = TradeResult.Locked;

        return result;
    }

    private TradeResult trade(Player p) {
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

    public int trade(Player p, int times) {
        TradeResult result = getResult(p);
        int resultTime = times;
        for (int time = 0; time < times; time++) {
            result = trade(p);
            if (!result.equals(TradeResult.Success)) {
                if (time != 0)
                    result = TradeResult.Lack;
                resultTime = time;
                break;
            }
        }
        //結果に対するエフェクトを表示
        playResultEffect(p, result);
        saveTradeLimit();
        return resultTime;
    }

    //アイテムを追加できるかチェックする
    private boolean hasEnoughSpace(Player p) {
        return ItemUtil.ableGive(p.getInventory(), getGiveItems());
    }

    //アイテムを所持しているか確認する
    private boolean affordTrade(Player p) {
        return ItemUtil.contains(p.getInventory(), getTakeItems());
    }

    private boolean isError() {
        return Arrays.stream(getGiveItems()).anyMatch(item -> NBTUtil.getNMSStringTag(item, "Error") != null) ||
                Arrays.stream(getTakeItems()).anyMatch(item -> NBTUtil.getNMSStringTag(item, "Error") != null);
    }

    private boolean isLimited(Player p) {
        if (getLimit() == 0) return false;
        return getCounts(p) >= getLimit();
    }

    public static void playResultEffect(Player p, TradeResult result) {
        switch (result) {
            case NotAfford:
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_ERROR_NOT_ENOUGH_ITEMS.getMessage());
                SoundUtil.playFailSound(p);
                break;
            case Locked:
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_SHOP_LOCKED.getMessage());
                SoundUtil.playFailSound(p);
                break;
            case Error:
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.COMMAND_INVALID_TRADE.getMessage());
                SoundUtil.playFailSound(p);
                break;
            case Limited:
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_ERROR_TRADE_LIMITED.getMessage());
                SoundUtil.playFailSound(p);
                break;
            case Lack:
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_ERROR_NOT_ENOUGH_SPACE.getMessage());
                SoundUtil.playCautionSound(p);
                break;
            case Full:
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + LanguageKey.MESSAGE_ERROR_INVENTORY_FULL.getMessage());
                SoundUtil.playCautionSound(p);
                break;
            case Success:
                SoundUtil.playClickShopSound(p);
                break;
        }
    }

    @Override
    public String toString() {
        return "ShopTrade{" +
                "takeData=" + takeData +
                ", giveData=" + giveData +
                '}';
    }
}
