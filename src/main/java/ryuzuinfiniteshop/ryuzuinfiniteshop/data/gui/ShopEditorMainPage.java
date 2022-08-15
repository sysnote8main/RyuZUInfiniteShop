package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ShopUtil;

import java.util.HashMap;

//ショップエディターのメインページ
public class ShopEditorMainPage {

    private final int page;
    private final Shop shop;

    public ShopEditorMainPage(Shop shop, int page) {
        this.shop = shop;
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public Shop getShop() {
        return shop;
    }

    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getPage()), 9 * 6, "ショップエディター");

        setEquipment(inv);
        setTradesPage(inv);
        setDisplayName(inv);

        return inv;
    }

    //エディターに装備を置く
    private void setEquipment(Inventory inv) {
        if(shop.getNPC() instanceof LivingEntity) {
            for(Integer slot : ShopUtil.getEquipmentsSlot().keySet()) {
                inv.setItem(slot, shop.getEquipmentDisplayItem(ShopUtil.getEquipmentsSlot().get(slot)));
            }
        }
    }

    private void setTradesPage(Inventory inv) {
        int max = Math.min(17 , shop.getPageCount() - (getPage() - 1) * 18);
        for(int i = 0; i < max ; i++) {
            inv.setItem(i, ItemUtil.getNamedItem(Material.LIME_STAINED_GLASS_PANE , ChatColor.GREEN + "ページ" + i));
        }
        if(max != 17 && shop.isLimitPage(max)) inv.setItem(max, ItemUtil.getNamedItem(Material.WHITE_STAINED_GLASS_PANE , ChatColor.WHITE + "新規ページ"));
    }

    private void setDisplayName(Inventory inv) {
        String diplayname = shop.getNPC().getCustomName() == null ? ChatColor.WHITE + "名前" : shop.getNPC().getCustomName();
        inv.setItem(5 * 9 + 8, ItemUtil.getNamedItem(Material.NAME_TAG , diplayname));
    }
}
