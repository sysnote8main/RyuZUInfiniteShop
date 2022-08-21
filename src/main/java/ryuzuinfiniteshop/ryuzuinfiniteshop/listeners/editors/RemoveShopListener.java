package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ConfirmRemoveShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopEditorMainPage;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

//ショップのNPCの装備を変更する
public class RemoveShopListener implements Listener {
    @EventHandler
    public void removeShop(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorMainPage)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 3 * 9 + 6) return;

        //ショップを削除
        shop.removeShop();

        //音を出す
        SoundUtil.playCautionSound(p);

        //確認画面を開く
        p.openInventory(new ConfirmRemoveShop(holder.getShop(), holder.getGui().getPage()).getInventory(holder.getShopMode()));
    }

    @EventHandler
    public void confirmRemoveShop(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ConfirmRemoveShop)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 0 && slot != 9) return;

        if(slot == 0) {
            //音を出す
            SoundUtil.playClickShopSound(p);

            //エディター画面に戻る
            p.openInventory(shop.getEditor(holder.getGui().getPage()).getInventory(holder.getShopMode()));
        } else {
            //ショップを削除
            shop.removeShop();

            //音を出し、メッセージを送信する
            SoundUtil.playSuccessSound(p);
            p.sendMessage(RyuZUInfiniteShop.prefix + ChatColor.RED + "ショップを削除しました");

            //インベントリを閉じる
            p.closeInventory();
        }
    }
}
