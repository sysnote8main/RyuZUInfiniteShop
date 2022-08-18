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
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.guis.ShopGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.AgeableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.PoweredableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.Shop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.VillagerableShop;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.ShopUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.utils.SoundUtil;

//NPCのエンティティタイプごとの固有のNBTを変更
public class ChangeIndividualSettingsListener implements Listener {

    //年齢の変更
    @EventHandler
    public void changeAgeLook(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (!(gui instanceof ShopEditorMainPage)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = shopholder.getShop();
        int slot = event.getSlot();
        if (!((ShopEditorMainPage) gui).getSettingsMap().containsKey(slot)) return;
        if (!(shop instanceof AgeableShop)) return;

        //年齢の変更
        ((AgeableShop) shop).setAgeLook(!((AgeableShop) shop).isAdult());

        //音を出す
        SoundUtil.playClickShopSound(p);

        //GUIのアイテムを更新
        ((ShopEditorMainPage) gui).setSettings(event.getView().getTopInventory());
    }

    //クリーパーを帯電させるか変更
    @EventHandler
    public void changePowered(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (!(gui instanceof ShopEditorMainPage)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = shopholder.getShop();
        int slot = event.getSlot();
        if (!((ShopEditorMainPage) gui).getSettingsMap().containsKey(slot)) return;
        if (!(shop instanceof PoweredableShop)) return;

        //帯電させるか変更
        ((PoweredableShop) shop).setPowered(!((PoweredableShop) shop).isPowered());

        //音を出す
        SoundUtil.playClickShopSound(p);

        //GUIのアイテムを更新
        ((ShopEditorMainPage) gui).setSettings(event.getView().getTopInventory());
    }

    //村人の職業を変更
    @EventHandler
    public void changeProfession(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (!(gui instanceof ShopEditorMainPage)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = shopholder.getShop();
        int slot = event.getSlot();
        if (!((ShopEditorMainPage) gui).getSettingsMap().containsKey(slot)) return;
        if (!(shop instanceof VillagerableShop)) return;

        //帯電させるか変更
        ((VillagerableShop) shop).setProfession(((VillagerableShop) shop).getNextProfession());

        //音を出す
        SoundUtil.playClickShopSound(p);

        //GUIのアイテムを更新
        ((ShopEditorMainPage) gui).setSettings(event.getView().getTopInventory());
    }

    //村人の職業を変更
    @EventHandler
    public void changeBiome(InventoryClickEvent event) {
        //インベントリがショップなのかチェック
        ShopGui gui = ShopUtil.getShopGui(event);
        if (gui == null) return;
        if (!(gui instanceof ShopEditorMainPage)) return;
        if (!ShopUtil.isEditMode(event)) return;
        if (event.getClickedInventory() == null) return;

        //必要なデータを取得
        Player p = (Player) event.getWhoClicked();
        ShopHolder shopholder = (ShopHolder) event.getView().getTopInventory().getHolder();
        Shop shop = shopholder.getShop();
        int slot = event.getSlot();
        if (!((ShopEditorMainPage) gui).getSettingsMap().containsKey(slot)) return;
        if (!(shop instanceof VillagerableShop)) return;

        //帯電させるか変更
        ((VillagerableShop) shop).setBiome(((VillagerableShop) shop).getNextBiome());

        //音を出す
        SoundUtil.playClickShopSound(p);

        //GUIのアイテムを更新
        ((ShopEditorMainPage) gui).setSettings(event.getView().getTopInventory());
    }
}
