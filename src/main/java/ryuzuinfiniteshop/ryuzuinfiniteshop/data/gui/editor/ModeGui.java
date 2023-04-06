package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ModeHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;

import java.util.Arrays;


public abstract class ModeGui {

    public abstract Inventory getInventory(ShopMode mode);

    public Inventory getInventory(ShopMode mode, ModeHolder before) {
        Inventory inv = getInventory(mode);
        ModeHolder holder = ShopUtil.getModeHolder(inv);
        holder.setBefore(before);
        return inv;
    }

    public void reloadInventory(Inventory target) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(target);
        if (holder == null) return;

        //必要なデータの取得
        Inventory inv = getInventory(holder.getMode());
        if(!holder.equals(ShopUtil.getShopHolder(inv))) return;
        ItemStack[] clear = target.getContents();
        Arrays.fill(clear, new ItemStack(Material.AIR));
        target.setContents(clear);
        target.setContents(inv.getContents());
    }
}
