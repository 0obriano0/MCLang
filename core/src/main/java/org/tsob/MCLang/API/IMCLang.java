package org.tsob.MCLang.API;

interface IMCLang {
  /**
   * 取得或設定語言
   * @return 語言代碼，例如 "en_us"、"zh_TW" 等
   */
  String getLang();

  /**
   * 設定語言
   * @param lang 語言代碼，例如 "en_us"、"zh_TW" 等
   */
  void setLang(String lang);

  /**
   * 取得對應 Minecraft Lang 的資料
   * @return translation 資料
   */
  String getString(String path);

  /**
   * 重新讀取 Minecraft Lang 的設定檔
   */
  void reload();
}
