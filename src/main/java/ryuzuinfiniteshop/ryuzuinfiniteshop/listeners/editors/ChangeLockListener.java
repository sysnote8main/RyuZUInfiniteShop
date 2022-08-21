package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopEditorMainPage;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

import java.util.HashMap;
import java.util.UUID;

//ショップのロック状態を変更する
public class ChangeLockListener implements Listener {
    @EventHandler
    public void changeLock(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if(!(holder.getGui() instanceof ShopEditorMainPage)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 5 * 9 + 5) return;

        //音を出す
        SoundUtil.playClickShopSound(p);

        //ロック状態を切り替え
        shop.setLock(!shop.isLock());

        //インベントリを更新する
        shop.getEditor(holder.getGui().getPage()).setDisplay(event.getClickedInventory());
    }
}
