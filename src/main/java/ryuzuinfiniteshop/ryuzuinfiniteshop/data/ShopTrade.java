package ryuzuinfiniteshop.ryuzuinfiniteshop.data;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopTrade {
    public enum Result {notAfford, Full, Success, Lack}

    public ItemStack[] give;
    public ItemStack[] take;

    public ConfigurationSection getConfig() {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("give", give);
        config.set("take", take);

        return config;
    }

    public ShopTrade(HashMap<String, ArrayList<ItemStack>> config) {
        this.give = config.get("give").toArray(new ItemStack[0]);
        this.take = config.get("take").toArray(new ItemStack[0]);
    }

    public ShopTrade(ConfigurationSection config) {
        this.give = (ItemStack[]) config.get("give");
        this.take = (ItemStack[]) config.get("take");
    }

    public ShopTrade(Inventory inv, int slot, Shop.ShopType type) {
        setTrade(inv, slot, type);
    }

    public void setTrade(Inventory inv, int slot, Shop.ShopType type) {
        if (type.equals(Shop.ShopType.TwotoOne)) {
            this.take = ItemUtil.getItemSet(inv, slot, 2);
            this.give = new ItemStack[]{inv.getItem(slot + 3)};
        } else {
            this.take = ItemUtil.getItemSet(inv, slot, 4);
            this.give = ItemUtil.getItemSet(inv, slot + 4, 4);
        }
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
            inv.removeItem(take);
            inv.addItem(give);
        }
        return result;
    }

    public Result trade(Player p, int times) {
        Result result = Result.Success;
        for (int time = 0; time < times; time++) {
            result = trade(p);
            if (!result.equals(Result.Success)) {
                if (time != 1)
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
        return ItemUtil.ableGive(p.getInventory(), give);
    }

    //アイテムを所持しているか確認する
    public boolean affordTrade(Player p) {
        return ItemUtil.contains(p.getInventory(), take);
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
