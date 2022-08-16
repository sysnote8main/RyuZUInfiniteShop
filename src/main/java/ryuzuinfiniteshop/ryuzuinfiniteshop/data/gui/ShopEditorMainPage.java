package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.ShopUtil;

import java.util.List;

//ショップエディターのメインページ
public class ShopEditorMainPage extends ShopGui {

    public ShopEditorMainPage(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getShop(), getPage() , ShopEditorMainPage.class.getName()), 9 * 6, "ショップエディター");

        setEquipment(inv);
        setTradesPage(inv);
        setDisplayName(inv);

        return inv;
    }

    @Override
    public boolean existTrade(int page) {
        return getTrades().size() > (page - 1) * 12;
    }

    public List<ShopTrade> getTrades() {
        return this.trades;
    }

    public ShopTradeGui getTradeGui(int slot) {
        if (slot < 0 || 17 < slot) return null;
        return getShop().getPage(slot + (getPage() - 1) * 18);
    }

    //エディターに装備を置く
    private void setEquipment(Inventory inv) {
        if (getShop().getNPC() instanceof LivingEntity) {
            for (Integer slot : ShopUtil.getEquipmentsSlot().keySet()) {
                inv.setItem(slot, getShop().getEquipmentDisplayItem(ShopUtil.getEquipmentsSlot().get(slot)));
            }
        }
    }

    private void setTradesPage(Inventory inv) {
        int max = Math.min(17, getShop().getTradePageCount() - (getPage() - 1) * 18);
        for (int i = 0; i < max; i++) {
            inv.setItem(i, ItemUtil.getNamedItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "ページ" + i));
        }
        if (max != 17 && getShop().isLimitPage(max))
            inv.setItem(max, ItemUtil.getNamedItem(Material.WHITE_STAINED_GLASS_PANE, ChatColor.WHITE + "新規ページ"));
    }

    private void setDisplayName(Inventory inv) {
        String diplayname = getShop().getNPC().getCustomName() == null ? ChatColor.WHITE + "名前" : getShop().getNPC().getCustomName();
        inv.setItem(5 * 9 + 8, ItemUtil.getNamedItem(Material.NAME_TAG, diplayname));
    }
}
