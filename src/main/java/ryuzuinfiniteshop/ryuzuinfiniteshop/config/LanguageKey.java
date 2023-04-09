package ryuzuinfiniteshop.ryuzuinfiniteshop.config;

import org.bukkit.ChatColor;

public enum LanguageKey {
    ITEM_NAME_SEARCH_BY_VALUE("対価で検索", "Search by value"),
    ITEM_SEARCH_BY_PRODUCT("商品で検索", "Search by product"),
    ITEM_LORE_SEARCH_BY_NAME("シフトクリック: アイテムの名前で検索", "Shift click: Search by item name"),
    ITEM_SEARCH_BY_CLICK("検索するアイテムを持ってクリック", "Search by clicking on an item"),
    ITEM_LORE_SEARCH_BY_NPC("シフトクリック: NPCの名前で検索", "Shift click: Search by NPC name"),
    TRADE_SEARCH("トレード サーチ", "Trade Search"),
    PAGE_NAVIGATION_MESSAGE("GUIの画面外を右クリック: 次のページに移動、左クリック: 前のページに移動できます", "Right click outside the GUI to move to the next page, left click to move to the previous page"),
    UNDERSTAND_BUTTON("分かった！", "I understand!"),
    DUPLICATE_TRADE_WARNING("重複している取引がありました", "There are duplicate trades"),
    UNDERSTAND_BUTTON_TOOLTIP("これ以降メッセージを表示しない", "Don't show messages anymore"),
    ERROR_SAVING_SHOP("ShopID: {0} の保存中にエラーが発生しました", "Error saving ShopID: {0}"),
    ERROR_LOADING_FILE("ShopID: {0} の読み込み中にエラーが発生しました", "Error loading ShopID: {0}"),
    ERROR_CONVERTING_SHOPKEEPERS("ShopkeepersID: {0} SISID: {1},{2},{3},{4} のShopkeepersからのコンバート中にエラーが発生しました", "An error occurred while converting ShopkeepersID: {0} SISID: {1},{2},{3},{4} from Shopkeepers"),
    COMMAND_LIST_SHOPS("ショップの一覧を表示します", "Show list of shops"),
    COMMAND_SEARCH("ショップや取引を検索します", "Search shops or trades"),
    COMMAND_SPAWN("ショップの作成または更新をします", "Create or update a shop"),
    COMMAND_OPEN("ショップの取引画面を開きます", "Open shop trade screen"),
    COMMAND_RELOAD("全てのデータをリロードします", "Reload all data"),
    COMMAND_LOAD("全てのデータをファイルから読み取ります", "Load all data from files"),
    COMMAND_SAVE("全てのデータをファイルに保存します", "Save all data to files"),
    COMMAND_LIMIT("取引回数を変更します", "Change trade limit"),
    COMMAND_ARGUMENT("必須", "Required"),
    COMMAND_PLAYER_ONLY("§c§lError! §4§lプレイヤーのみ実行可能です。", "§c§lError! §4§lOnly players can execute this command."),
    SHOP_CREATED(ChatColor.GREEN + "ショップを設置しました", ChatColor.GREEN + "Shop created!"),
    SHOP_UPDATED(ChatColor.RED + "ショップを更新しました", ChatColor.RED + "Shop updated!"),
    SHOP_CREATE("§a§lSuccess! §2§lショップを設置しました。", "§a§lSuccess! §2§lShop created."),
    SHOP_UPDATE("§a§lSuccess! §2§lショップを更新しました。", "§a§lSuccess! §2§lShop updated."),
    ENTITY_INVALID("§c§lError! §4§l有効なエンティティタイプまたはMythicMobIDを入力して下さい。", "§c§lError! §4§lPlease input a valid entity type or MythicMob ID."),
    TRADE_SEARCH_PAGE("トレード サーチ ページ", "Trade Search Page"),
    SEARCH_TRADE_PAGE_TITLE("トレード サーチ ページ {0}", "Trade Search Page {0}"),
    SHOP_LIST_PAGE_TITLE("ショップ一覧 ページ{0}", "Shop list page {0}"),
    COORDINATES("座標: {0}", "Coordinates: {0}"),
    SEARCHABILITY("検索可否: {0}", "Searchability: {0}"),
    LOCK_STATUS("ロック: {0}", "Lock status: {0}"),
    CLICK_OPEN_TRADE("クリック: 取引画面を開く", "Click: Open trade"),
    SHIFT_CLICK_EDIT("シフトクリック: 編集画面を開く", "Shift click: Open edit"),
    PAGE_TITLE("ショップ一覧 ページ", "Shop List Page"),
    POSITION("座標: ", "Position: "),
    IS_SEARCHABLE("検索可否: ", "Searchable: "),
    SEARCHABLE("可能", "Searchable"),
    UNSEARCHABLE("不可 ", "Unsearchable"),
    LOCKED("ロック", "Locked"),
    IS_LOCKED("ロック: ", "Locked: "),
    UNLOCKED("アンロック", "Unlocked"),
    CLICK_TO_OPEN("クリック: 取引画面を開く", "Click: Open trade window"),
    SHIFT_CLICK_TO_EDIT("シフトクリック: 編集画面を開く", "Shift-click: Open edit window"),
    DELETE_SHOP_TITLE("ショップ削除確認：", "Shop Deletion Confirmation: "),
    CANCEL_BUTTON(ChatColor.RED + "キャンセル", ChatColor.RED + "Cancel"),
    DELETE_BUTTON(ChatColor.GREEN + "削除する", ChatColor.GREEN + "Delete"),
    EDITOR_PAGE_TITLE("ショップエディター", "Shop Editor"),
    EDITOR_PAGE_NUMBER("ページ {0}", "Page {0}"),

    TRADER_PAGE_NUMBER(ChatColor.GREEN + "ページ {0}", ChatColor.GREEN + "Page {0}"),
    DISPLAY_NAME_CHANGE(ChatColor.GREEN + "名前を変更する", ChatColor.GREEN + "Change name"),
    CURRENT_NAME(ChatColor.YELLOW + "現在の名前: {0}", ChatColor.YELLOW + "Current name: {0}"),
    TELEPORT(ChatColor.GREEN + "NPCにテレポートする", ChatColor.GREEN + "Teleport to NPC"),
    NEW_PAGE(ChatColor.YELLOW + "新規ページ", ChatColor.YELLOW + "New page"),


    COMMAND_ARGUMENT_OPTIONAL("任意", "Optional");


    private final String japanese;
    private final String english;

    LanguageKey(String japanese, String english) {
        this.japanese = japanese;
        this.english = english;
    }

//    public String getMessage() {
//        if (LanguageConfig.getLanguage().equals("Japanese")) {
//            return japanese;
//        } else {
//            return english;
//        }
//    }

//    public String getMessage(String... args) {
//        if (LanguageConfig.getLanguage().equals("Japanese")) {
//            return japanese;
//        } else {
//            return english;
//        }
//    }

    public String getMessage(Object... args) {
        String message = LanguageConfig.getLanguage().equals("English") ? english : japanese;
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return message;
    }
}
