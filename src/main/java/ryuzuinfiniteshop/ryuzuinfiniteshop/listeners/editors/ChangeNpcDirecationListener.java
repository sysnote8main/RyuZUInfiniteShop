package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editors;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

//NPCの方向を変更する
public class ChangeNpcDirecationListener implements Listener {
    @EventHandler
    public void changeNPCDirecation(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if(!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 5 * 9 + 6) return;

        //NPCの向きを45度回す
        shop.changeNPCDirecation();

        //音を出す
        SoundUtil.playClickShopSound(p);
    }
}
