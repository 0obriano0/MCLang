package org.tsob.MCLang.API;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.tsob.MCLang.DataBase.DataBase;
import org.tsob.MCLang.FileIO.JsonFileIOMinecraftLang;

public class MCLang implements IMCLang {
  private String Lang;
  
  /**
   * 建構子
   * @param lang 語言代碼，例如 "en_us"、"zh_TW" 等
   */
  public MCLang(String lang) {
    this.Lang = lang;
  }

  @Override
  public String getLang() {
    return Lang;
  }

  @Override
  public void setLang(String lang) {
    this.Lang = lang;
  }

  /**
   * 取得 Minecraft Lang 的 JsonFileIO
   * @return JsonFileIOMinecraftLang
   */
  private JsonFileIOMinecraftLang getJsonFileIOMinecraftLang() {
    JsonFileIOMinecraftLang _minecraftLangMap = null;
    
    if (DataBase.minecraftLangMap.containsKey(this.Lang)) {
      _minecraftLangMap = DataBase.minecraftLangMap.get(this.Lang);
    } else {
      _minecraftLangMap = new JsonFileIOMinecraftLang(this.Lang);
      DataBase.minecraftLangMap.put(this.Lang, _minecraftLangMap);
    }

    return _minecraftLangMap;
  }

  @Override
  public String getString(String path) {
    return _getString(path, true);
  }

  private String _getString(String path, boolean fullback) {
    JsonFileIOMinecraftLang minecraftLang = getJsonFileIOMinecraftLang();
    String translation = minecraftLang.getString(path);
    
    if (translation == null || translation.isEmpty()) {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return fullback ? path : "";
    }
    
    return translation; 

  }

  @Override
  public String getItemTranslate(ItemStack item) {
    if (item == null || item.getType() == null) {
      return "unknown_item";
    }
    String path = "item.minecraft." + item.getType().name().toLowerCase();
    String translation = "";

    // 特殊道具區

    translation = _getString(path,false);
    if (translation == null || translation.isEmpty() || translation == "") {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return item.getType().name().toLowerCase();
    }

    return translation;
  }

  @Override
  public String getEntityTranslate(EntityType entry) {
    if (entry == null) {
      return "unknown_entity";
    }
    String path = "entity.minecraft." + entry.name().toLowerCase();
    String translation = "";

    // 特殊實體區

    translation = _getString(path,false);
    if (translation == null || translation.isEmpty() || translation == "") {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return entry.name().toLowerCase();
    }

    return translation;
  }

  @Override
  public void reload() {
    JsonFileIOMinecraftLang minecraftLang = getJsonFileIOMinecraftLang();
    minecraftLang.reloadWithLangAndVersion();
  }
}