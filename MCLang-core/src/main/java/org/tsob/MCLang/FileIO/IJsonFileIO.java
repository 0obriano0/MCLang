package org.tsob.MCLang.FileIO;

import java.util.List;

public interface IJsonFileIO {
  /**
   * 取得檔案名稱(含副檔名)
   */
  String getFileName();

  /**
   * 取得檔案相對路徑
   */
  String getPath();

  /**
   * 取得 JSON 物件資料
   */
  Object getJsonObject();

  /**
   * 依路徑取得字串
   */
  String getString(String path);

  /**
   * 依路徑取得字串列表
   */
  List<String> getStringList(String path);

  /**
   * 重新讀取檔案
   */
  boolean reloadFile();

  /**
   * 重新讀取檔案後要做的事
   */
  boolean reloadcmd();
}