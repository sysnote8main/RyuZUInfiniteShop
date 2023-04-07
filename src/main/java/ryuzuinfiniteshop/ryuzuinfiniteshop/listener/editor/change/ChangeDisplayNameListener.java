package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.change;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system.SchedulerListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.ColorUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;

//ショップのNPCの名前を変更する
public class ChangeDisplayNameListener implements Listener {

    @EventHandler
    public void changeDisplay(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!holder.getMode().equals(ShopMode.Edit)) return;
        if (event.getClickedInventory() == null) return;
        if (ItemUtil.getNamedItem(ItemUtil.getColoredItem(Material.WHITE_STAINED_GLASS_PANE.name()), "").equals(event.getCurrentItem())) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 5 * 9 + 3) return;
        //チャット入力待機
        SchedulerListener.setSchedulers(p, holder.getShop().getID(), (message) -> {
            //成功時の処理
            shop.setDisplayName(ColorUtil.color(message));
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "名前が設定されました");
            SoundUtil.playSuccessSound(p);
        });
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "NPCの名前をチャットに入力してください");
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "20秒待つか'Cancel'と入力することでキャンセルことができます");
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "カラーコードを使う際は'&'を使用してください");

        SoundUtil.playClickShopSound(p);
        holder.getShop().setEditting(false);
    }
}
