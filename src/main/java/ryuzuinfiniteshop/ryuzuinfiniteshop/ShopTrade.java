package ryuzuinfiniteshop.ryuzuinfiniteshop;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ItemUtil;

public class ShopTrade {
    public enum Result {Lack, Full, Success}

    public ItemStack[] give;
    public ItemStack[] take;

    public ConfigurationSection getConfig() {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("give", give);
        config.set("take", take);

        return config;
    }

    public ShopTrade(ConfigurationSection config) {
        this.give = (ItemStack[]) config.get("give");
        this.take = (ItemStack[]) config.get("take");
    }

    public Result trade(Player p) {
        Inventory inv = p.getInventory();
        Result result = Result.Success;

        for (int i = 0; i < 4; i++) {
            if (take[i] == null) continue;
            inv.remove(take[i]);
        }
        if(affordTrade(p)) result = Result.Lack;
        if(hasEnoughSpace(p)) result = Result.Full;

        playResultEffect(p, result);

        //アイテムを追加する
        if (result == Result.Success) {
            for (int i = 0; i < 4; i++) {
                if (give[i] == null) continue;
                inv.addItem(give[i]);
            }
        }
        return result;
    }

    public Result trade(Player p, int times) {
        Result result = Result.Success;
        for (int time = 0; time < times; time++) {
            result = trade(p);
            if (!result.equals(Result.Success)) {
                break;
            }
        }
        return result;
    }

    //アイテムを追加できるかチェックする
    public boolean affordTrade(Player p) {
        for (int i = 0; i < 4; i++) {
            if (give[i] == null) continue;
            if (!ItemUtil.canGive(p.getInventory(), give[i])) {
                return false;
            }
        }
        return true;
    }

    //アイテムを消費する
    public boolean hasEnoughSpace(Player p) {
        for (int i = 0; i < 4; i++) {
            if (take[i] == null) continue;
            if (!p.getInventory().contains(take[i], take[i].getAmount())) {
                return false;
            }
        }
        return true;
    }

    public void playResultEffect(Player p, Result result) {
        switch (result) {
            case Lack:
                p.sendMessage(ChatColor.RED + "アイテムが足りません");
                break;
            case Full:
                p.sendMessage(ChatColor.RED + "インベントリに十分な空きがありません");
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                break;
            case Success:
                p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 2);
                break;
        }
    }
}
