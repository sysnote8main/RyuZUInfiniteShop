package ryuzuinfiniteshop.ryuzuinfiniteshop.listeners.editor.convert;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.RyuZUInfiniteShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.editor.ShopEditorGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.effect.SoundUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.PersistentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.inventory.ShopUtil;

//ショップのNPCの装備を変更する
public class ConvartListener implements Listener {
    @EventHandler
    public void convert(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!ShopUtil.isEditMode(event)) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        Shop shop = holder.getShop();
        int slot = event.getSlot();
        if (slot != 3 * 9 + 7 && slot != 3 * 9 + 8) return;

        //コンバート
        ItemStack item = slot == 3 * 9 + 7 ? shop.convertTrades() : shop.convertShopToItemStack();
        if (ItemUtil.ableGive(p.getInventory(), item)) {
            p.getInventory().addItem(item);
            SoundUtil.playSuccessSound(p);
        } else
            SoundUtil.playFailSound(p);
    }

    //トレードを読み込む
    @EventHandler
    public void loadTrades(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopHolder holder = ShopUtil.getShopHolder(event);
        if (holder == null) return;
        if (!(holder.getGui() instanceof ShopEditorGui)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        Shop shop = holder.getShop();
        ItemStack item = event.getCursor();
        String tag = PersistentUtil.getNMSStringTag(item, "ShopType");
        int slot = event.getSlot();

        if (slot != 2 * 9 + 8) return;
        if (tag == null) {
            SoundUtil.playFailSound(p);
            return;
        }
        if (!(Shop.ShopType.valueOf(tag).equals(Shop.ShopType.TwotoOne) || Shop.ShopType.valueOf(tag).equals(shop.getShopType()))) {
            SoundUtil.playFailSound(p);
            p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.RED + "ショップタイプが違います");
            return;
        }

        //トレードを読み込む
        if ((type.isRightClick() || type.isLeftClick()) && !type.isShiftClick()) {
            shop.loadTrades(item);

            //音を出す
            SoundUtil.playSuccessSound(p);

            //インベントリを更新する
            holder.getGui().reloadInventory(event.getClickedInventory());
        }
    }

    //ショップを読み込む
    @EventHandler
    public void loadShop(PlayerInteractEvent event) {
        //ショップ召喚用アイテムなのかチェック
        if (event.getHand() == null) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        //必要なデータを取得
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();

        if (!p.hasPermission("ris.op")) return;
        if (!p.isSneaking()) return;
        if (item == null || PersistentUtil.getNMSStringTag(item, "Shop") == null) return;
        if (block == null) return;

        //ショップを読み込む
        Shop shop = ShopUtil.createShop(block.getLocation().add(0, 1, 0), PersistentUtil.getNMSStringTag(item, "Shop"));

        //音を出し、メッセージを送信
        SoundUtil.playSuccessSound(p);
        p.sendMessage(RyuZUInfiniteShop.prefixCommand + ChatColor.GREEN + shop.getDisplayName() + "を召喚しました");
    }
}
