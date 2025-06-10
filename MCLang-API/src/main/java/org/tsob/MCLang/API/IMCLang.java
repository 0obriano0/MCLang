package org.tsob.MCLang.API;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * IMCLang 介面
 * 提供 Minecraft 語言相關的功能
 * @author brian
 *
 */
public interface IMCLang {
  /**
   * 取得或設定語言
   * @return 語言代碼，例如 "en_us"、"zh_tw" 等
   */
  String getLang();

  /**
   * 設定語言
   * @param lang 語言代碼，例如 "en_us"、"zh_tw" 等
   */
  void setLang(String lang);

  /**
   * 取得對應 Minecraft Lang 的資料
   * @return translation 資料
   */
  String getString(String path);

  /**
   * 取得物品的翻譯
   * @param item 要翻譯的物品
   * @return 物品的翻譯字串
   */
  String getItemTranslate(ItemStack item);

  /**
   * 取得物品名稱的翻譯
   * @param itemName 要翻譯的物品名稱
   * @return 物品名稱的翻譯字串
   */
  String getItemTranslate(String itemName);

  /**
   * 取得實體(船,觸發的tnt等,怪物)的翻譯
   * @param entry 要翻譯的實體類型
   * @return 實體的翻譯字串
   */
  String getEntityTranslate(EntityType entry);

  /**
   * 取得實體名稱的翻譯
   * @param entityName 要翻譯的實體名稱
   * @return 實體名稱的翻譯字串
   */
  String getEntityTranslate(String entityName);

  /**
   * 重新讀取 Minecraft Lang 的設定檔
   */
  void reload();
}
