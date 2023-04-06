package ryuzuinfiniteshop.ryuzuinfiniteshop.data.system;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.EqualsAndHashCode;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.DisplayPanelConfig;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.item.ObjectItems;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.LogUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.NBTUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.TradeUtil;

import java.util.HashMap;
import java.util.UUID;

@EqualsAndHashCode
public class ShopTrade {
    public static final HashMap<ShopTrade, UUID> tradeUUID = new HashMap<>();
    public static final Table<UUID, UUID, Integer> tradeCounts = HashBasedTable.create();
    public static final HashMap<UUID, Integer> tradeLimits = new HashMap<>();

    public enum TradeResult {NotAfford, Full, Success, Lack, Limited, Locked, Normal}

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

    public ShopTrade(Inventory inv, int slot, Shop.ShopType type, int limit) {
        setTrade(inv, slot, type);
        setTradeLimits(limit, false);
    }

    public ItemStack[] getGiveItems() {
        return giveData.toItemStacks();
    }

    public ItemStack[] getTakeItems() {
        return takeData.toItemStacks();
    }

    public int getLimit() {
        if (!tradeUUID.containsKey(this)) return 0;
        return tradeLimits.getOrDefault(tradeUUID.get(this), 0);
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

    public void setTradeLimits(int count, boolean force) {
        if (count == 0) {
            tradeLimits.remove(tradeUUID.get(this));
            tradeUUID.remove(this);
        } else {
            if (!tradeUUID.containsKey(this)) tradeUUID.put(this, UUID.randomUUID());
            if (!(!force && tradeLimits.containsKey(tradeUUID.get(this)))) tradeLimits.put(tradeUUID.get(this), count);
        }
    }

    public static ItemStack getFilter() {
        return ItemUtil.getNamedItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.BLACK + "");
    }

    private ItemStack getFilter(ShopMode mode) {
        return mode.equals(ShopMode.Edit) ? getSettingsFilter() : getFilter();
    }

    public static ItemStack getFilter(ShopMode mode, int value) {
        return mode.equals(ShopMode.Edit) ? getSettingsFilter(value) : getFilter();
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
                                ShopUtil.getShop(id).getDisplayNameOrElseNone() + ChatColor.GREEN + "との取引",
                                false,
                                ChatColor.GREEN + page + ChatColor.YELLOW + "ページ目",
                                isAdmin ? (ChatColor.YELLOW + "検索可否: " + (shop.isSearchable() ? ChatColor.GREEN + "可" : ChatColor.RED + "不可")) : null,
                                isAdmin ? (ChatColor.YELLOW + "ロック: " + (shop.isLock() ? ChatColor.RED + "ロック" : ChatColor.GREEN + "アンロック")) : null,
                                ChatColor.GREEN + "クリック: 取引画面を開く",
                                isAdmin ? null : ChatColor.GREEN + "対価、商品をクリック: 商品、対価で検索",
                                isAdmin ? ChatColor.GREEN + "シフトクリック: 編集画面を開く" : null
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
                ChatColor.GREEN + "クリック: 取引上限設定" + ChatColor.YELLOW + " 取引上限: " + value,
                ChatColor.GREEN + "シフトクリック: 取引のアイテム化"
        ), "TradeLimit", String.valueOf(value));
    }

    public ItemStack changeLimit(int variation) {
        int value = Math.max(getLimit() + variation, 0);
        if (value == 0)
            tradeUUID.remove(this);
        else
            tradeUUID.put(this, UUID.randomUUID());
        tradeLimits.put(tradeUUID.get(this), value);
        return getSettingsFilter(value);
    }

    public ItemStack[] getTradeItems(Shop.ShopType type, ShopMode mode) {
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

    public ItemStack[] getTradeItems(Shop.ShopType type, String id, Player player) {
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

    public void setTrade(Inventory inv, int slot, Shop.ShopType type) {
        ShopTrade trade = TradeUtil.getTrade(inv, slot, type);
        if (trade == null) return;
        this.takeData = trade.takeData;
        this.giveData = trade.giveData;
    }

    public TradeResult getResult(Player p) {
        TradeResult result = TradeResult.Success;

        if (!affordTrade(p)) result = TradeResult.NotAfford;
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
        TradeResult result = TradeResult.Success;
        int resultTime = times;
        for (int time = 0; time < times; time++) {
            result = trade(p);
            if (!result.equals(TradeResult.Success)) {
                if (time != 0)
                    result = TradeResult.Lack;
                else
                    result = TradeResult.NotAfford;
                resultTime = time;
                break;
            }
        }
        //結果に対するエフェクトを表示
        playResultEffect(p, result);
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

    private boolean isLimited(Player p) {
        if (!tradeUUID.containsKey(this)) return false;
        if (tradeLimits.getOrDefault(tradeUUID.get(this), 0) == 0) return false;
        return getCounts(p) >= tradeLimits.getOrDefault(tradeUUID.get(this), 0);
    }

    private void playResultEffect(Player p, TradeResult result) {
        switch (result) {
            case NotAfford:
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "アイテムが足りません");
                SoundUtil.playFailSound(p);
                break;
            case Limited:
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "取引上限です");
                SoundUtil.playFailSound(p);
                break;
            case Lack:
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "すべてを購入できませんでした");
                SoundUtil.playCautionSound(p);
                break;
            case Full:
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "インベントリに十分な空きがありません");
                SoundUtil.playCautionSound(p);
                break;
            case Success:
                SoundUtil.playClickShopSound(p);
                break;
        }
    }
}
