package org.tsob.MCLang.API;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * IMCLang Interface
 * Provides Minecraft language-related functions.
 * Minecraft 語言相關功能介面
 * @author brian
 */
public interface IMCLang {
  /**
   * Get or set the language.
   * 取得或設定語言
   * @return Language code, e.g., "en_us", "zh_tw"
   *         語言代碼，例如 "en_us"、"zh_tw" 等
   */
  String getLang();

  /**
   * Set the language.
   * 設定語言
   * @param lang Language code, e.g., "en_us", "zh_tw"
   *             語言代碼，例如 "en_us"、"zh_tw" 等
   */
  void setLang(String lang);

  /**
   * Get the translation data for the specified Minecraft Lang path.
   * 取得對應 Minecraft Lang 的資料
   * @return Translation string
   *         翻譯資料
   */
  String getString(String path);

  /**
   * Get the translation of an item.
   * 取得物品的翻譯
   * @param item The item to translate
   *             要翻譯的物品
   * @return The translated item string
   *         物品的翻譯字串
   */
  String getItemTranslate(ItemStack item);

  /**
   * Get the translation of an item name.
   * 取得物品名稱的翻譯
   * @param itemName The item name to translate
   *                 要翻譯的物品名稱
   * @return The translated item name string
   *         物品名稱的翻譯字串
   */
  String getItemTranslate(String itemName);

  /**
   * Get the translation of an entity (boat, primed TNT, mobs, etc.) by EntityType.
   * 取得實體(船,觸發的tnt等,怪物)的翻譯
   * @param entityType The entity type to translate
   *                   要翻譯的實體類型
   * @return The translated entity string
   *         實體的翻譯字串
   * @deprecated It is recommended to use {@link #getEntityTranslate(Entity)} for more complete entity information.
   *             建議改用 {@link #getEntityTranslate(Entity)} 以取得更完整的實體資訊
   */
  @Deprecated
  String getEntityTranslate(EntityType entityType);

  /**
   * Get the translation of an entity (boat, primed TNT, mobs, etc.).
   * 取得實體的翻譯 (船,觸發的tnt等,怪物)
   * @param entity The entity to translate
   *               要翻譯的實體
   * @return The translated entity string
   *         實體的翻譯字串
   */
  String getEntityTranslate(Entity entity);

  /**
   * Get the translation of an entity name.
   * 取得實體名稱的翻譯
   * @param entityName The entity name to translate
   *                   要翻譯的實體名稱
   * @return The translated entity name string
   *         實體名稱的翻譯字串
   */
  String getEntityTranslate(String entityName);

  /**
   * Get the translation of an enchantment by name.
   * 取得附魔的翻譯
   * @param enchantmentName The enchantment name to translate
   *                        要翻譯的附魔名稱
   * @return The translated enchantment name string
   *         附魔名稱的翻譯字串
   */
  String getEnchantmentTranslate(String enchantmentName);

  /**
   * Get the translation of an enchantment.
   * 取得附魔的翻譯
   * @param enchantment The enchantment to translate
   *                   要翻譯的附魔
   * @return The translated enchantment string
   *         附魔的翻譯字串
   */
  String getEnchantmentTranslate(Enchantment enchantment);

  /**
   * Reload the Minecraft Lang configuration file.
   * 重新讀取 Minecraft Lang 的設定檔
   */
  void reload();
}
