package ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.editor;

import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopMode;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.trade.ShopTradeGui;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.shops.*;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.gui.holder.ShopHolder;
import ryuzuinfiniteshop.ryuzuinfiniteshop.data.system.ShopTrade;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.EquipmentUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.inventory.ItemUtil;
import ryuzuinfiniteshop.ryuzuinfiniteshop.util.configuration.JavaUtil;

import java.util.HashMap;
import java.util.List;

//ショップエディターのメインページ
public class ShopEditorGui extends ShopGui {
    public enum ShopSettings {Age, Power, Profession, Biome, Visible, ParrotColor, DyeColor, HorseColor, HorseStyle}

    private final HashMap<Integer, ShopSettings> SettingsMap = new HashMap<>();

    public ShopEditorGui(Shop shop, int page) {
        super(shop, page);
    }

    @Override
    public Inventory getInventory(ShopMode mode) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(mode, getShop(), this), 9 * 6, JavaUtil.getOrDefault(getShop().getNPC().getCustomName(), "ショップ") + " エディター ページ" + getPage());

        //アイテムを設置
        setDisplay(inv);

        return inv;
    }

    public List<ShopTrade> getTrades() {
        return this.trades;
    }

    public ShopTradeGui getTradeGui(int slot) {
        if (slot < 0 || 17 < slot) return null;
        if (getShop().getPageCount() > slot + 1) return null;
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
        if (newslot == 18)
            getShop().createEditorNewPage();
        else if (newslot != -1)
            inv.setItem(newslot, ItemUtil.getNamedItem(Material.WHITE_STAINED_GLASS_PANE, ChatColor.YELLOW + "新規ページ"));
    }

    private void setDisplayName(Inventory inv) {
        inv.setItem(5 * 9 + 8, ItemUtil.getNamedItem(Material.NAME_TAG, ChatColor.GREEN + "名前を変更する" , ChatColor.YELLOW + "現在の名前: " + JavaUtil.getOrDefault(getShop().getNPC().getCustomName() , "<none>")));
    }

    private void setEntityType(Inventory inv) {
        inv.setItem(5 * 9 + 4, ItemUtil.getNamedItem(Material.EGG, ChatColor.GREEN + "エンティティタイプを変更する"));
    }

    private void setMythicMobType(Inventory inv) {
        inv.setItem(5 * 9 + 3, ItemUtil.getNamedItem(Material.TURTLE_EGG, ChatColor.GREEN + "MythicMobIDを設定する"));
    }

    private void setShopType(Inventory inv) {
        inv.setItem(5 * 9 + 7, ItemUtil.getNamedItem(Material.MAGENTA_GLAZED_TERRACOTTA, getShop().getShopTypeDisplay()));
    }

    private void setNPCDirecation(Inventory inv) {
        inv.setItem(5 * 9 + 6, ItemUtil.getNamedItem(Material.ARROW, ChatColor.GREEN + "方向切り替え"));
    }

    private void setLock(Inventory inv) {
        ItemStack item = getShop().isLock() ?
                ItemUtil.getNamedEnchantedItem(Material.TRIPWIRE_HOOK, ChatColor.GREEN + "ロック") :
                ItemUtil.getNamedItem(Material.TRIPWIRE_HOOK, ChatColor.GREEN + "アンロック");
        inv.setItem(5 * 9 + 5, item);
    }

    private void setConvertTrades(Inventory inv) {
        inv.setItem(3 * 9 + 7, ItemUtil.getNamedItem(Material.EMERALD, ChatColor.GREEN + "トレード内容をアイテム化する"));
        inv.setItem(2 * 9 + 8, ItemUtil.getNamedEnchantedItem(Material.BLACK_STAINED_GLASS_PANE, ChatColor.GREEN + "トレードを読み込む"));
    }

    private void setConvertShop(Inventory inv) {
        inv.setItem(3 * 9 + 8, ItemUtil.getNamedItem(Material.DIAMOND, ChatColor.GREEN + "ショップをアイテム化する"));
    }

    private void setRemoveShop(Inventory inv) {
        inv.setItem(3 * 9 + 5, ItemUtil.getNamedItem(Material.BARRIER, ChatColor.RED + "ショップを削除する"));
    }

    private void setReloadShop(Inventory inv) {
        inv.setItem(3 * 9 + 6, ItemUtil.getNamedItem(Material.NAUTILUS_SHELL, ChatColor.RED + "ショップを更新する"));
    }

    public void setDisplay(Inventory inv) {
        setLock(inv);
        setTradesPage(inv);
        setShopType(inv);
        setNPCDirecation(inv);
        setConvertTrades(inv);
        setConvertShop(inv);
        setRemoveShop(inv);
        setReloadShop(inv);
        setMythicMobType(inv);
        setEntityType(inv);
        if(!MythicMobs.inst().getAPIHelper().isMythicMob(getShop().getNPC())) {
            setEquipment(inv);
            setDisplayName(inv);
            setSettings(inv);
        }
        setCover(inv);
    }

    public void setCover(Inventory inv) {
        ItemStack item = ItemUtil.getNamedItem(Material.WHITE_STAINED_GLASS_PANE, "");
        for (int i = 2 * 9 ; i < 6 * 9 ; i++)
            if(ItemUtil.isAir(inv.getItem(i))) inv.setItem(i, item);
    }

    public void setSettings(Inventory inv) {
        SettingsMap.clear();
        setVisible(inv);
        setAge(inv);
        setPower(inv);
        setProfession(inv);
        setBiome(inv);
        setParrotColor(inv);
        setDyeColor(inv);
        setHorseColor(inv);
        setHorseStyle(inv);
    }

    private void setAge(Inventory inv) {
        if (!(getShop() instanceof AgeableShop)) return;
        ItemStack item = ((AgeableShop) getShop()).isAdult() ?
                ItemUtil.getNamedItem(Material.STONE, ChatColor.GREEN + "大人") :
                ItemUtil.getNamedItem(Material.STONE_BUTTON, ChatColor.GREEN + "子供");
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, item);
        SettingsMap.put(slot, ShopSettings.Age);
    }

    private void setPower(Inventory inv) {
        if (!(getShop() instanceof PoweredableShop)) return;
        ItemStack item = ((PoweredableShop) getShop()).isPowered() ?
                ItemUtil.getNamedEnchantedItem(Material.CREEPER_SPAWN_EGG, ChatColor.GREEN + "帯電") :
                ItemUtil.getNamedItem(Material.CREEPER_SPAWN_EGG, ChatColor.GREEN + "通常");
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, item);
        SettingsMap.put(slot, ShopSettings.Power);
    }

    private void setParrotColor(Inventory inv) {
        if (!(getShop() instanceof ParrotShop)) return;
        ItemStack item = ItemUtil.getNamedItem(((ParrotShop) getShop()).getColorMaterial(), ChatColor.GREEN + "色の変更");
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, item);
        SettingsMap.put(slot, ShopSettings.ParrotColor);
    }

    private void setDyeColor(Inventory inv) {
        if (!(getShop() instanceof DyeableShop)) return;
        ItemStack item = ItemUtil.getNamedItem(((DyeableShop) getShop()).getColorMaterial(), ChatColor.GREEN + "色の変更");
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, item);
        SettingsMap.put(slot, ShopSettings.DyeColor);
    }

    private void setHorseColor(Inventory inv) {
        if (!(getShop() instanceof HorseShop)) return;
        ItemStack item = ItemUtil.getNamedItem(((HorseShop) getShop()).getColorMaterial(), ChatColor.GREEN + "色の変更");
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, item);
        SettingsMap.put(slot, ShopSettings.HorseColor);
    }

    private void setHorseStyle(Inventory inv) {
        if (!(getShop() instanceof HorseShop)) return;
        ItemStack item = ItemUtil.getNamedItem(Material.TERRACOTTA, ChatColor.GREEN + "模様の変更");
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, item);
        SettingsMap.put(slot, ShopSettings.HorseStyle);
    }

    private void setProfession(Inventory inv) {
        if (!(getShop() instanceof VillagerableShop)) return;
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, ItemUtil.getNamedItem(((VillagerableShop) getShop()).getJobBlockMaterial(), ChatColor.GREEN + "ジョブチェンジ"));
        SettingsMap.put(slot, ShopSettings.Profession);
    }

    private void setBiome(Inventory inv) {
        if (!(getShop() instanceof VillagerableShop)) return;
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, ItemUtil.getNamedItem(((VillagerableShop) getShop()).getBiomeImageMaterial(), ChatColor.GREEN + "バイオームチェンジ"));
        SettingsMap.put(slot, ShopSettings.Biome);
    }

    private void setVisible(Inventory inv) {
        if (!(getShop().getNPC() instanceof LivingEntity)) return;
        ItemStack item = ((LivingEntity) getShop().getNPC()).isInvisible() ?
                ItemUtil.getNamedItem(Material.GLASS, ChatColor.GREEN + "透明") :
                ItemUtil.getNamedItem(Material.POLISHED_ANDESITE, ChatColor.GREEN + "不透明");
        int slot = 4 * 9 + 8 - SettingsMap.size();
        inv.setItem(slot, item);
        SettingsMap.put(slot, ShopSettings.Visible);
    }

    public int getTradePageRawNumber(int slot) {
        return slot + 1 + (getPage() - 1) * 18;
    }

    public int getTradePageNumber(int slot) {
        int page = getTradePageRawNumber(slot);
        return page <= getShop().getPageCount() ? page : 0;
    }

    public int getTradeLastSlotNumber() {
        return Math.min(17, getShop().getPageCount() - 1 - (getPage() - 1) * 18);
    }

    public int getTradeNewSlotNumber() {
        int last = getTradeLastSlotNumber();
        if (last == -1) return 0;
        if (getShop().isLimitPage(getTradePageNumber(last)))
            return last + 1;
        else
            return -1;
    }

    public HashMap<Integer, ShopSettings> getSettingsMap() {
        return SettingsMap;
    }
}
