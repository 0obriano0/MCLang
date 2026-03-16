package org.tsob.MCLang.Hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.tsob.MCLang.DataBase.DataBase;
import org.tsob.MCLang.FileIO.JsonFileIOMinecraftLang;

/**
 * PlaceholderAPI Expansion for MCLang
 * PlaceholderAPI 擴展，用於支援 MCLang 的多國語言翻譯
 *
 * Placeholder format 佔位符格式:
 *   %mclang_<lang>_<key>%
 *
 * Language codes 語言代碼 (variable length, may or may not contain underscores):
 *   - Simple / 簡單: tok, lzh, bar, isv ...
 *   - Standard / 標準: en_us, zh_tw, ja_jp ...
 *   - Extended / 延伸: zlm_arab, be_latn, sah_sah ...
 *
 * Examples 範例:
 *   %mclang_en_us_item.minecraft.diamond_sword%  → Diamond Sword
 *   %mclang_zh_tw_item.minecraft.diamond_sword%  → 鑽石劍
 *   %mclang_ja_jp_entity.minecraft.creeper%       → クリーパー
 *   %mclang_tok_item.minecraft.stone%             → (Toki Pona)
 *   %mclang_zlm_arab_item.minecraft.stone%        → (Malay Arabic)
 */
public class MCLangPlaceholderExpansion extends PlaceholderExpansion {

  private final Plugin plugin;

  public MCLangPlaceholderExpansion(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public String getIdentifier() {
    return "mclang";
  }

  @Override
  public String getAuthor() {
    return "0obriano0";
  }

  @Override
  public String getVersion() {
    return plugin.getDescription().getVersion();
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String onRequest(OfflinePlayer player, String params) {
    if (params == null || params.isEmpty()) {
      return null;
    }

    // Format: <lang>_<key>
    // Language codes are variable-length and may or may not contain underscores.
    // 語言代碼長度不固定，且不一定包含底線（例如 tok、zlm_arab、en_us）。
    //
    // Strategy: Minecraft translation keys always contain dots (e.g. item.minecraft.stone).
    // The separator between the lang code and the key is the last '_' that appears
    // before the first '.' in params.
    // 策略：Minecraft 翻譯鍵一定含有「.」，因此 lang 與 key 的分隔點
    // 就是第一個「.」之前的最後一個「_」。
    int firstDot = params.indexOf('.');
    if (firstDot <= 0) {
      return null;
    }

    int separatorIndex = params.lastIndexOf('_', firstDot - 1);
    if (separatorIndex <= 0) {
      return null;
    }

    String lang = params.substring(0, separatorIndex);
    String key = params.substring(separatorIndex + 1);
    if (lang.isEmpty() || key.isEmpty()) {
      return null;
    }

    // Get or create the language file handler from cache
    JsonFileIOMinecraftLang langFile;
    if (DataBase.minecraftLangMap.containsKey(lang)) {
      langFile = DataBase.minecraftLangMap.get(lang);
    } else {
      langFile = new JsonFileIOMinecraftLang(lang);
      DataBase.minecraftLangMap.put(lang, langFile);
    }

    String translation = langFile.getString(key);
    if (translation == null || translation.isEmpty()) {
      return key;
    }

    return translation;
  }
}
