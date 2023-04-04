package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ModeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ModeHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.JavaUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;

public class SelectSearchItemGui extends ModeGui {

    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ModeHolder(ShopMode.Search, this), 9, ChatColor.DARK_BLUE + "トレード サーチ");

        for (int i = 1; i < 8; i++) inv.setItem(i, ShopTrade.getFilter());
        inv.setItem(0, ItemUtil.getNamedItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "対価で検索", ChatColor.GREEN + "シフトクリック: アイテムの名前で検索"));
        inv.setItem(8, ItemUtil.getNamedItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "商品で検索", ChatColor.GREEN + "シフトクリック: アイテムの名前で検索"));
        inv.setItem(4, ItemUtil.getNamedItem(Material.WHITE_STAINED_GLASS_PANE, ChatColor.BLUE + "検索するアイテムを持ってクリック", ChatColor.GREEN + "シフトクリック: NPCの名前で検索"));

        return inv;
    }
}
