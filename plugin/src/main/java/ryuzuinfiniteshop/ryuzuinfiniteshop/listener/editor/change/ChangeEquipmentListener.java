package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.change;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.entity.EquipmentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;

//ショップのNPCの装備を変更する
public class ChangeEquipmentListener implements Listener {
    @EventHandler
    public void changeEquipment(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!holder.getMode().equals(ShopMode.EDIT)) return;
        if (event.getClickedInventory() == null) return;
        if(ItemUtil.getWhitePanel().equals(event.getCurrentItem())) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        Shop shop = holder.getShop();
        int slot = event.getSlot();

        if (!EquipmentUtil.getEquipmentsSlot().containsKey(slot)) return;

        //装備を変更
        if ((type.isRightClick() || type.isLeftClick())) {
            if (ItemUtil.isAir(event.getCursor())) {
                if (ItemUtil.isAir(shop.getEquipmentItem(EquipmentUtil.getEquipmentSlot(slot).ordinal())))
                    return;
                 else
                    event.setCurrentItem(EquipmentUtil.getEquipmentDisplayItem(EquipmentUtil.getEquipmentSlot(slot)));
            } else
                event.setCurrentItem(ItemUtil.getOneItemStack(event.getCursor()));

            shop.setEquipmentItem(ItemUtil.getOneItemStack(event.getCursor()), EquipmentUtil.getEquipmentSlot(slot).ordinal());

            //音を出す
            SoundUtil.playClickShopSound(p);
        }
    }
}
