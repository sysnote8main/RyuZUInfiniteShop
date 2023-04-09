package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.config.LanguageKey;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.JavaUtil;

//ショップエディターのメインページ
public class ConfirmRemoveGui extends ShopGui {

    public ConfirmRemoveGui(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getShop(), this), 9, LanguageKey.DELETE_SHOP_TITLE.getMessage() + getShop().getDisplayNameOrElseShop() + LanguageKey.DELETE_SHOP_TITLE.getMessage());

        inv.setItem(0, ItemUtil.getNamedItem(ItemUtil.getColoredItem("RED_STAINED_GLASS_PANE"), LanguageKey.CANCEL_BUTTON.getMessage()));
        inv.setItem(8, ItemUtil.getNamedItem(ItemUtil.getColoredItem("GREEN_STAINED_GLASS_PANE"), LanguageKey.DELETE_BUTTON.getMessage()));

        return inv;
    }


}
