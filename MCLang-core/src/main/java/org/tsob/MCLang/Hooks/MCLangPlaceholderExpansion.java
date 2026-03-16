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
 * Examples 範例:
 *   %mclang_en_us_item.minecraft.diamond_sword%  → Diamond Sword
 *   %mclang_zh_tw_item.minecraft.diamond_sword%  → 鑽石劍
 *   %mclang_ja_jp_entity.minecraft.creeper%       → クリーパー
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
    // Language codes follow the pattern xx_xx (e.g., en_us, zh_tw, ja_jp)
    // So we expect at least 6 characters: 5 for lang code + 1 for separator underscore
    // 語言代碼格式為 xx_xx（如 en_us、zh_tw、ja_jp）
    // 因此至少需要 6 個字元：5 個語言代碼 + 1 個底線分隔符
    if (params.length() < 7) {
      return null;
    }

    // Extract language code (first 5 chars: xx_xx)
    String lang = params.substring(0, 5);

    // Validate language code separator
    if (params.charAt(5) != '_') {
      return null;
    }

    // Extract translation key (everything after the separator)
    String key = params.substring(6);
    if (key.isEmpty()) {
      return null;
    }

    // Get or create the language file handler from cache
    JsonFileIOMinecraftLang langFile = DataBase.minecraftLangMap.computeIfAbsent(lang, JsonFileIOMinecraftLang::new);

    String translation = langFile.getString(key);
    if (translation == null || translation.isEmpty()) {
      return key;
    }

    return translation;
  }
}
