package ryuzuinfiniteshop.ryuzuinfiniteshop.config;

import org.bukkit.ChatColor;

public enum LanguageKey {
    ITEM_NAME_SEARCH_BY_VALUE("対価で検索", "Search by value"),
    ITEM_SEARCH_BY_PRODUCT("商品で検索", "Search by product"),
    ITEM_LORE_SEARCH_BY_NAME("シフトクリック: アイテムの名前で検索", "Shift click: Search by item name"),
    ITEM_SEARCH_BY_CLICK("検索するアイテムを持ってクリック", "Search by clicking on an item"),
    ITEM_LORE_SEARCH_BY_NPC("シフトクリック: NPCの名前で検索", "Shift click: Search by NPC name"),
    INVENTORY_TRADE_SEARCH("トレード サーチ", "Trade Search"),
    MESSAGE_PAGE_NAVIGATION("GUIの画面外を右クリック: 次のページに移動、左クリック: 前のページに移動できます", "Right click outside the GUI to move to the next page, left click to move to the previous page"),
    MESSAGE_UNDERSTAND_BUTTON("分かった！", "I understand!"),
    MESSAGE_DUPLICATE_TRADE_WARNING("重複している取引がありました", "There are duplicate trades"),
    MESSAGE_UNDERSTAND_BUTTON_TOOLTIP("これ以降メッセージを表示しない", "Don't show messages anymore"),
    ERROR_SAVING_SHOP("ShopID: {0} の保存中にエラーが発生しました", "Error saving ShopID: {0}"),
    ERROR_LOADING_FILE("ShopID: {0} の読み込み中にエラーが発生しました", "Error loading ShopID: {0}"),
    ERROR_CONVERTING_SHOPKEEPERS("ShopkeepersID: {0} SISID: {1},{2},{3},{4} のShopkeepersからのコンバート中にエラーが発生しました", "An error occurred while converting ShopkeepersID: {0} SISID: {1},{2},{3},{4} from Shopkeepers"),
    COMMAND_LIST_SHOPS("ショップの一覧を表示します", "Show a list of shops"),
    COMMAND_SEARCH_TRADES("ショップや取引を検索します", "Search for shops and trades"),
    COMMAND_SPAWN_SHOP("ショップの作成または更新をします", "Create or update a shop"),
    COMMAND_OPEN_TRADE_GUI("ショップの取引画面を開きます", "Open the trade GUI of a shop"),
    COMMAND_RELOAD_ALL_DATA("全てのデータをリロードします", "Reload all data"),
    COMMAND_LOAD_ALL_DATA("全てのデータをファイルから読み取り、ショップをロードします", "Load all data from files"),
    COMMAND_UNLOAD_ALL_DATA("全てのデータをファイルに保存し、ショップをアンロードします", "Unload all data to files"),
    COMMAND_CHANGE_TRADE_LIMIT("取引回数を変更します", "Change trade limits"),
    COMMAND_ARGUMENT_REQUIRED("[arg]", "Required argument"),
    COMMAND_ARGUMENT_OPTIONAL("<arg>", "Optional argument"),
    COMMAND_PLAYER_ONLY("プレイヤーのみ実行可能です。", "Only players can execute this command."),
    MESSAGE_SHOP_CREATED("ショップを設置しました", "Shop created!"),
    MESSAGE_SHOP_UPDATED( "ショップを更新しました", "Shop updated!"),
    MESSAGE_ENTITY_INVALID("有効なエンティティタイプまたはMythicMobIDを入力して下さい。", "Please input a valid entity type or MythicMob ID."),
    INVENTORY_SEARCH_TRADE_PAGE_TITLE("トレード サーチ ", "Trade Search "),
    INVENTORY_SHOP_LIST_PAGE_TITLE("ショップ一覧", "Shop list"),
    ITEM_POSITION("座標: {0}", "Coordinates: {0}"),
    ITEM_IS_SEARCHABLE("検索可否: {0}", "Searchability: {0}"),
    ITEM_SEARCHABLE("可能", "Searchable"),
    ITEM_UNSEARCHABLE("不可 ", "Unsearchable"),
    ITEM_LOCKED("ロック", "Locked"),
    ITEM_IS_LOCKED("ロック: ", "Locked: "),
    ITEM_UNLOCKED("アンロック", "Unlocked"),
    ITEM_CLICK_TO_OPEN("クリック: 取引画面を開く", "Click: Open trade window"),
    ITEM_SHIFT_CLICK_TO_EDIT("シフトクリック: 編集画面を開く", "Shift-click: Open edit window"),
    INVENTORY_DELETE_SHOP_TITLE("ショップ削除確認：", "Shop Deletion Confirmation: "),
    ITEM_CANCEL_BUTTON( "キャンセル", "Cancel"),
    ITEM_DELETE_BUTTON("削除する", "Delete"),
    INVENTORY_EDITOR_PAGE_TITLE("ショップエディター", "Shop Editor"),
    INVENTORY_PAGE_NUMBER("ページ {0}", "Page {0}"),
    INVENTORY_SET_NAME("名前を変更する", "Set display name"),
    INVENTORY_CURRENT_NAME("現在の名前: {0}", "Current name: {0}"),
    INVENTORY_TELEPORT_TO_NPC("NPCにテレポートする", "Teleport to NPC"),
    INVENTORY_SET_MYTHIC_MOB_ID("MythicMobIDを設定する", "Set MythicMob ID"),
    INVENTORY_CHANGE_ENTITY_TYPE("エンティティタイプを変更する", "Change entity type"),
    INVENTORY_SEARCH_SELECT("検索可能", "Searchable"),
    INVENTORY_SEARCH_NOT_SELECTABLE("検索不可", "Not searchable"),
    INVENTORY_SHOP_LOCKED("ロック", "Locked"),
    INVENTORY_SHOP_UNLOCKED("アンロック", "Unlocked"),
    INVENTORY_SWITCH_DIRECTION("方向切り替え", "Switch direction"),
    INVENTORY_DELETE_SHOP("ショップを削除する", "Delete shop"),
    INVENTORY_UPDATE_SHOP("ショップを更新する", "Update shop"),
    INVENTORY_CONVERT_TRADE_TO_ITEMS("トレード内容をアイテム化する", "Convert trades to items"),
    INVENTORY_CONVERT_SHOP_TO_ITEMS("ショップをアイテム化する", "Convert shop to items"),
    INVENTORY_LOAD_TRADES("トレードを読み込む", "Load trades"),
    INVENTORY_SHOP_POWERED("帯電", "Powered"),
    INVENTORY_SHOP_NOT_POWERED("通常", "Not powered"),
    INVENTORY_INCREASE_SLIME_SIZE("サイズを大きくする", "Increase slime size"),
    INVENTORY_DECREASE_SLIME_SIZE("サイズを小さくする", "Decrease slime size"),
    INVENTORY_CHANGE_BODY_COLOR("体の色を変更する", "Change body color"),
    INVENTORY_CHANGE_PATTERN_COLOR("模様の色を変更する", "Change pattern color"),
    INVENTORY_CHANGE_PATTERN("模様を変更する", "Change pattern"),
    INVENTORY_CHANGE_PARROT_COLOR("色の変更", "Change parrot color"),
    INVENTORY_CHANGE_DYE_COLOR("色の変更", "Change dye color"),
    INVENTORY_CHANGE_OPTIONAL_INFO("追加情報の変更", "Change optional info"),
    INVENTORY_CHANGE_HORSE_COLOR("色の変更", "Change horse color"),
    INVENTORY_CHANGE_HORSE_STYLE("模様の変更", "Change horse style"),
    SETTINGS_JOB_CHANGE("ジョブチェンジ", "Job change"),
    SETTINGS_BIOME_CHANGE("バイオームチェンジ", "Biome change"),
    SETTINGS_LEVEL_CHANGE("レベルチェンジ", "Level change"),
    SETTINGS_VISIBILITY_INVISIBLE("透明", "Invisible"),
    SETTINGS_VISIBILITY_VISIBLE("不透明", "Visible"),
    MESSAGE_SHOP_LOCKED("現在このショップはロックされています", "This shop is currently locked"),
    MESSAGE_SHOP_EDITING("現在このショップは編集中です", "This shop is currently being edited"),
    MESSAGE_SHOP_NO_TRADES("現在このショップには取引が存在しません", "There are currently no trades available in this shop"),
    ERROR_SAVING_FILE("ShopID: {0} の保存中にエラーが発生しました", "An error occurred while saving ShopID: {0}"),
    MESSAGE_PLAYER_NOT_EXIST("そのプレイヤーは存在しません", "That player does not exist"),
    MESSAGE_TRADE_COMPRESSION_GEM_REQUIRED("トレード圧縮宝石を持った状態で実行してください", "Please execute while holding a Trade Compression Gem"),
    ERROR_INVALID_INTEGER("0以上の整数を入力してください", "Please input a non-negative integer"),
    MESSAGE_LIMIT_CHANGE("{0}の取引上限に変更を加えました", "Changed the trade limit for {0}"),
    ITEM_SHOP_COMPRESSION_GEM("ショップ圧縮宝石:", "Shop compression gem: "),
    ITEM_SHOP_COMPRESSION_GEM_CLICK("ショップに向かって右クリック:", "Right-click on a shop: "),
    ITEM_SHOP_COMPRESSION_GEM_CLICK_SUB("ショップの取引の取り込み", "Collect shop trades"),
    ITEM_SHOP_COMPRESSION_GEM_SHIFT_CLICK("地面に向かってシフト右クリック:", "Shift right-click on the ground: "),
    ITEM_SHOP_COMPRESSION_GEM_SHIFT_CLICK_SUB("ショップの設置", "Place the shop"),
    ITEM_SHOP_COMPRESSION_GEM_TYPE("ショップタイプ: ", "Shop type: "),
    ITEM_TRADE_COMPRESSION_GEM("トレード圧縮宝石", "Trade compression gem"),
    ITEM_TRADE_WITH("との取引", "Trade with {0}"),
    INVENTORY_PAGE("ページ目", "Page {0}"),
    INVENTORY_SEARCH("検索可否", "Searchable"),
    INVENTORY_SEARCH_ENABLED("可", "Enabled"),
    INVENTORY_SEARCH_DISABLED("不可", "Disabled"),
    INVENTORY_LOCK("ロック", "Lock"),
    INVENTORY_LOCKED("ロック", "Locked"),
    INVENTORY_UNLOCKED("アンロック", "Unlocked"),
    ITEM_TRADE_WINDOW_OPEN("クリック: 取引画面を開く", "Click: Open trade window"),
    ITEM_SEARCH_BY_VALUE_OR_ITEM("対価、商品をクリック: 商品、対価で検索", "Click on value or item to search"),
    ITEM_EDIT_WINDOW_OPEN("シフトクリック: 編集画面を開く", "Shift click: Open edit window"),
    ERROR_NOT_ENOUGH_ITEMS("アイテムが足りません", "Not enough items"),
    ERROR_INVALID_TRADE("エラーが発生しました。無効な取引です", "An error occurred. Invalid trade"),
    ERROR_TRADE_LIMITED("取引上限です", "Trade limit reached"),
    ERROR_NOT_ENOUGH_SPACE("すべてを購入できませんでした", "Not enough space"),
    ERROR_INVENTORY_FULL("インベントリに十分な空きがありません", "Inventory is full"),
    ITEM_SETTINGS_TRADE_LIMIT_CLICK("クリック: 取引上限設定", "Click: Set trade limit"),
    ITEM_SETTINGS_TRADE_LIMIT_VALUE("取引上限", "Trade limit"),
    ITEM_SETTINGS_TRADE_TO_ITEM("シフトクリック: 取引のアイテム化", "Shift click: Trade to item"),
    MESSAGE_SUCCESS_SET_DISPLAY_NAME("名前が設定されました", "Display name set successfully"),
    MESSAGE_ENTER_NPC_NAME("NPCの名前をチャットに入力してください", "Enter NPC name in chat"),
    MESSAGE_ENTER_NPC_NAME_CANCEL("20秒待つか'Cancel'と入力することでキャンセルことができます", "You can cancel by waiting for 20 seconds or typing 'Cancel'"),
    MESSAGE_ENTER_NPC_NAME_COLOR_CODE("カラーコードを使う際は'&'を使用してください", "Use '&' to use color codes"),
    ERROR_INVALID_MYTHIC_MOB_ID("有効なMythicMobIDを入力して下さい", "Please enter a valid MythicMobID"),
    MESSAGE_SUCCESS_SET_MYTHIC_MOB_ID("MythicMobIDを設定しました", "MythicMobID set successfully"),
    MESSAGE_ENTER_MYTHIC_MOB_ID("MythicMobIDを入力してください", "Enter MythicMobID"),
    MESSAGE_ENTER_MYTHIC_MOB_ID_CANCEL("20秒待つか'Cancel'と入力することでキャンセルことができます", "You can cancel by waiting for 20 seconds or typing 'Cancel')"),
    MESSAGE_SHOP_NOT_FOUND("ショップが見つかりませんでした。", "Shop not found."),
    MESSAGE_NO_TRADES_AVAILABLE("ショップに取引がありません。", "No trades available in the shop."),
    ERROR_SHOP_TYPE_MISMATCH("ショップタイプが違います", "Shop type mismatch"),
    WARNING_DUPLICATE_TRADE("重複している取引がありました", "Duplicate trade found"),
    MESSAGE_SUCCESS_CREATE_SHOP("{0}を作成しました", "Successfully created {0}"),
    MESSAGE_SUCCESS_DELETE_SHOP("{0}を削除しました", "Successfully deleted {0}"),
    MESSAGE_CANCELLED("キャンセルしました", "Cancelled"),
    ERROR_SHOP_NOT_FOUND("ショップが見つかりませんでした", "Shop not found"),
    ITEM_SEARCH_BY_ITEM_CLICK("検索するアイテムを持ってクリック", "Click with item to search"),
    ITEM_SEARCH_BY_NPC_NAME("シフトクリック: NPCの名前で検索", "Shift click: Search by NPC name"),
    MESSAGE_SEARCH_NPC_NAME_INPUT("検索するNPCの名前をチャットに入力してください", "Input the name of NPC you want to search in chat"),
    MESSAGE_CANCEL_INPUT("20秒待つか'Cancel'と入力することでキャンセルことができます", "You can cancel the operation by waiting for 20 seconds or inputting 'Cancel'"),
    MESSAGE_SEARCH_NO_RESULTS("検索結果がありませんでした", "No results found for search"),
    MESSAGE_SEARCH_INPUT_PROMPT("検索するアイテムの名前をチャットに入力してください", "Please enter the name of the item to search for in chat"),
    MESSAGE_SEARCH_CANCEL_PROMPT("20秒待つか'Cancel'と入力することでキャンセルことができます", "You can cancel the search by waiting 20 seconds or typing 'Cancel'"),
    MESSAGE_RELOADING_FILES("全てのファイルをリロード中です。しばらくお待ちください。", "Reloading all files. Please wait a moment."),
    MESSAGE_LOADING_COMPLETE("全てのファイルのロードが完了しました", "All files have been loaded successfully."),
    MESSAGE_SAVING_COMPLETE("全てのファイルの保存が完了しました", "All files have been saved successfully."),
    MESSAGE_RELOADING_COMPLETE("全てのファイルのリロードが完了しました", "All files have been reloaded successfully."),
    MESSAGE_RELOADING_BLOCKED("現在リロード処理中のため、すべての処理をブロックしています。", "All processes are currently blocked due to reloading."),
    ERROR_WORLD_NOT_FOUND("ワールドが存在しません: {0}", "World not found: {0}"),
    MESSAGE_SHOP_NOT_EXIST("そのショップは存在しません", "That shop does not exist"),
    ITEM_EQUIP_MAIN_HAND("メインハンド", "Main hand"),
    ITEM_EQUIP_HELMET("ヘルメット", "Helmet"),
    ITEM_EQUIP_CHESTPLATE("チェストプレート", "Chestplate"),
    ITEM_EQUIP_LEGGINGS("レギンス", "Leggings"),
    ITEM_EQUIP_BOOTS("ブーツ", "Boots"),
    ITEM_EQUIP_OFF_HAND("オフハンド", "Off hand"),
    MESSAGE_SUCCESS_MERGE_SHOP("{0}の取引を宝石のショップにマージしました", "Successfully merged trades of {0} into gemstone shop");


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
        String message = LanguageConfig.getText(this);
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return message;
    }

    public String getLanguage(String language) {
        return language.equalsIgnoreCase("Japanese") ? japanese : english;
    }
}
