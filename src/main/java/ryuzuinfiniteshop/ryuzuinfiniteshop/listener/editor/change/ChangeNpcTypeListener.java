package ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.change;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.listener.editor.system.SchedulerListener;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.effect.SoundUtil;

//ショップのNPCの名前を変更する
public class ChangeNpcTypeListener implements Listener {
    @EventHandler
    public void changeNpcType(InventoryClickEvent event) {
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
        if (slot != 4 * 9 + 4) return;
        //チャット入力待機
        SchedulerListener.setSchedulers(p, shop.getID(), (message) -> {
            //成功時の処理
            //NPCを再構築する
            try {
                shop.setNpcType(EntityType.valueOf(message.toUpperCase()));
                shop.respawnNPC();
//                ShopUtil.overwriteShop(shop.getLocation(), shop.convertShopToString(), shop.convertTradesToMap(), EntityType.valueOf(message.toUpperCase()));
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "エンティティタイプを変更しました");
                SoundUtil.playSuccessSound(p);
            } catch (IllegalArgumentException e) {
                p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "有効なエンティティタイプを入力して下さい");
                SoundUtil.playFailSound(p);
            }
        });
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "エンティティIDを入力してください");
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + "20秒待つか'Cancel'と入力することでキャンセルことができます");

        SoundUtil.playClickShopSound(p);
        holder.getShop().setEditting(false);
    }
}
