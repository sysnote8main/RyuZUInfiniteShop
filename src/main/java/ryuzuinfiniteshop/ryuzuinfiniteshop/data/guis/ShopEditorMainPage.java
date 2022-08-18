package ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.EquipmentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.JavaUtil;

import java.util.List;

//ショップエディターのメインページ
public class ShopEditorMainPage extends ShopGui {

    public ShopEditorMainPage(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopHolder.ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getShop(), getPage(), ShopEditorMainPage.class.getName()), 9 * 6, "ショップエディター ページ" + getPage());

        setEquipment(inv);
        setTradesPage(inv);
        setDisplayName(inv);

        return inv;
    }

    public List<ShopTrade> getTrades() {
        return this.trades;
    }

    public ShopTradeGui getTradeGui(int slot) {
        if (slot < 0 || 17 < slot) return null;
        if (getShop().getTradePageCount() > slot + 1) return null;
        return getShop().getPage(slot + (getPage() - 1) * 18);
    }

    //エディターに装備を置く
    private void setEquipment(Inventory inv) {
        if (getShop().getNPC() instanceof LivingEntity) {
            for (Integer slot : EquipmentUtil.getEquipmentsSlot().keySet()) {
                inv.setItem(slot, getShop().getEquipmentDisplayItem(EquipmentUtil.getEquipmentSlot(slot)));
            }
        }
    }

    private void setTradesPage(Inventory inv) {
        int lastslot = getTradeLastSlotNumber();
        for (int i = 0; i <= lastslot; i++) {
            inv.setItem(i, ItemUtil.getNamedItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "ページ" + getTradePageNumber(i)));
        }
        int newslot = getTradeNewSlotNumber();
        if (newslot != -1)
            inv.setItem(newslot, ItemUtil.getNamedItem(Material.WHITE_STAINED_GLASS_PANE, ChatColor.WHITE + "新規ページ"));
    }

    private void setDisplayName(Inventory inv) {
        String diplayname = JavaUtil.getOrDefault(getShop().getNPC().getCustomName(), ChatColor.WHITE + "名前");
        inv.setItem(5 * 9 + 8, ItemUtil.getNamedItem(Material.NAME_TAG, diplayname));
    }

    public int getTradePageRawNumber(int slot) {
        return slot + 1 + (getPage() - 1) * 18;
    }

    public int getTradePageNumber(int slot) {
        int page = getTradePageRawNumber(slot);
        return page <= getShop().getTradePageCount() ? page : 0;
    }

    public int getTradeLastSlotNumber() {
        return Math.min(17, getShop().getTradePageCount() - 1 - (getPage() - 1) * 18);
    }

    public int getTradeNewSlotNumber() {
        int last = getTradeLastSlotNumber();
        if(last == -1) return 0;
        if (last != 17 && getShop().isLimitPage(getTradePageNumber(last)))
            return last + 1;
        else
            return -1;
    }
}
