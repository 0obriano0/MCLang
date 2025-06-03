package org.tsob.MCLang.API;

import org.tsob.MCLang.DataBase.DataBase;
import org.tsob.MCLang.FileIO.JsonFileIOMinecraftLang;

public class MCLang implements IMCLang {
  private String Lang;
  
  MCLang(String lang) {
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
    JsonFileIOMinecraftLang minecraftLang = getJsonFileIOMinecraftLang();
    String translation = minecraftLang.getString(path);
    
    if (translation == null || translation.isEmpty()) {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return path;
    }
    
    return translation;
  }

  @Override
  public void reload() {
    JsonFileIOMinecraftLang minecraftLang = getJsonFileIOMinecraftLang();
    minecraftLang.reloadWithLangAndVersion();
  }
}