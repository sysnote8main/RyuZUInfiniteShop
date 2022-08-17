package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editors;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopEditorMainPage;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.EquipmentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

//ショップのNPCの装備を変更する
public class ChangeEquipmentListener implements Listener {
    @EventHandler
    public void changeEquipment(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (!(gui instanceof ShopEditorMainPage)) return;
        if (!ShopUtil.isEditMode(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = shopholder.getShop();
        int slot = event.getSlot();

        if (!EquipmentUtil.getEquipmentsSlot().containsKey(slot)) return;

        //装備を変更
        if ((type.isRightClick() || type.isLeftClick()) && !type.isShiftClick()) {
            if (ItemUtil.isAir(event.getCursor())) {
                if (ItemUtil.isAir(shop.getEquipmentItem(EquipmentUtil.getEquipmentSlotNumber(EquipmentUtil.getEquipmentSlot(slot)))))
                    return;
                 else
                    event.setCurrentItem(EquipmentUtil.getEquipmentDisplayItem(EquipmentUtil.getEquipmentSlot(slot)));
            } else
                event.setCurrentItem(ItemUtil.getOneItemStack(event.getCursor()));

            shop.setEquipmentItem(ItemUtil.getOneItemStack(event.getCursor()), EquipmentUtil.getEquipmentSlotNumber(EquipmentUtil.getEquipmentSlot(slot)));

            //音を出す
            SoundUtil.playClickShopSound(p);
        }
    }
}
