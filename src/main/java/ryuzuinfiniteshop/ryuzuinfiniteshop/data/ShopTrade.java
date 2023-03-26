package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.admin.MythicListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.TradeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ShopTrade {
    public enum Result {notAfford, Full, Success, Lack}
    public List<Object> giveData;
    public List<Object> takeData;

    public ConfigurationSection getConfig() {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("give", giveData);
        config.set("take", takeData);

        return config;
    }

    public ShopTrade(HashMap<String, List<Object>> config) {
        this.giveData = config.get("give");
        this.takeData = config.get("take");
    }

    public ShopTrade(ItemStack[] give, ItemStack[] take) {
        this.giveData = getItemsConfiguration(give);
        this.takeData = getItemsConfiguration(take);
    }

    public ShopTrade(Inventory inv, int slot, Shop.ShopType type) {
        setTrade(inv, slot, type);
    }

    private List<Object> getItemsConfiguration(ItemStack[] items) {
        return Arrays.stream(items).map(item -> {
            if(MythicListener.getID(item) != null)
                return new MythicItem(MythicListener.getID(item) , item.getAmount());
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

    private ItemStack[] getTradeItems(List<Object> data) {
        return data.stream().map(item -> {
            if(item instanceof MythicItem)
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

    public Result getResult(Player p) {
        Result result = Result.Success;

        if (!affordTrade(p)) result = Result.notAfford;
        else if (!hasEnoughSpace(p)) result = Result.Full;
        return result;
    }

    public Result trade(Player p) {
        Inventory inv = p.getInventory();
        Result result = getResult(p);

        //アイテムを追加する
        if (result == Result.Success) {
            inv.removeItem(getTakeItems());
            inv.addItem(getGiveItems());
        }
        return result;
    }

    public Result trade(Player p, int times) {
        Result result = Result.Success;
        for (int time = 0; time < times; time++) {
            result = trade(p);
            if (!result.equals(Result.Success)) {
                if (time != 0)
                    result = Result.Lack;
                else
                    result = Result.notAfford;
                break;
            }
        }
        //結果に対するエフェクトを表示
        playResultEffect(p, result);
        return result;
    }

    //アイテムを追加できるかチェックする
    public boolean hasEnoughSpace(Player p) {
        return ItemUtil.ableGive(p.getInventory(), getGiveItems());
    }

    //アイテムを所持しているか確認する
    public boolean affordTrade(Player p) {
        return ItemUtil.contains(p.getInventory(), getTakeItems());
    }

    public void playResultEffect(Player p, Result result) {
        switch (result) {
            case notAfford:
                p.sendMessage(ChatColor.RED + "アイテムが足りません");
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
}
