package org.tsob.MCLang.FileIO;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.DataBase.DataBase;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonFileIOMinecraftLang extends JsonFileIO {

  public Map<String,String> Minecraft_Items = new HashMap<String,String>();

  /**
   * @param langFromConfig 語言設定，抓不到自動用 en_us
   */
  public JsonFileIOMinecraftLang(String langFromConfig) {
    super(getVersionPath(getMinecraftVersion()), getLangFileName(resolveLang(langFromConfig)));
    reloadNode();
  }

  /**
   * 取得 Minecraft 版本號（主.次.修）
   */
  private static String getMinecraftVersion() {
    String bukkitVer = Bukkit.getBukkitVersion(); // 例如 1.21.5-R0.1-SNAPSHOT
    if (bukkitVer == null || bukkitVer.isEmpty())
      return "unknown";
    String[] arr = bukkitVer.split("-")[0].split("\\.");
    if (arr.length >= 3)
      return arr[0] + "." + arr[1] + "." + arr[2];
    if (arr.length == 2)
      return arr[0] + "." + arr[1] + ".0";
    return bukkitVer.split("-")[0];
  }

  /**
   * 判斷語言，抓不到就回傳 en
   */
  private static String resolveLang(String langFromConfig) {
    if (langFromConfig == null || langFromConfig.isEmpty())
      return "en";
    return langFromConfig;
  }

  /**
   * 取得完整路徑
   */
  private static String getVersionPath(String version) {
    if (version == null || version.isEmpty())
      version = "1.21.5"; // 預設版本號

    String path = String.format("mc_lang/%s", version);
    DataBase.Print(AnsiColor.YELLOW + "[LoadMinecraftLang] Minecraft Version Path: " + AnsiColor.GREEN + path);
    return path;
  }

  private static String getLangFileName(String lang) {
    if (lang == null || lang.isEmpty())
      lang = "en_us"; // 預設語言

    if (lang == "en") {
      lang = "en_us"; // Minecraft 默認語言
    }

    lang = lang.toLowerCase(); // 確保語言是小寫

    String fileName = String.format("%s.json", lang);
    DataBase.Print(AnsiColor.YELLOW + "[LoadMinecraftLang] Minecraft Lang FileName: " + AnsiColor.GREEN + fileName);
    return fileName;
  }

  /**
   * 如果需要重新指定語言和版本，可用這方法。
   */
  public void reloadWithLangAndVersion(String lang) {
    setFileName(getLangFileName(resolveLang(lang)));
    reloadFile();
    reloadNode();
  }


  // 讀取Minecraft 文件很特殊 沒有階層
  @Override
  protected JsonNode getNodeByPath(String path) {
    if (data == null)
      return null;
    String[] parts = new String[] { path };
    JsonNode node = data;
    for (String part : parts) {
      node = node.get(part);
      if (node == null)
        break;
    }
    return node;
  }

  // 將 JsonNode 轉成我要的資料
  private void reloadNode() {
    if (data == null)
      return;
    int count = 0;
    java.util.Iterator<Map.Entry<String, JsonNode>> iterator = data.fields();
    while (iterator.hasNext()) {
      Map.Entry<String, JsonNode> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue().asText();

      if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
        continue; // 跳過空鍵或值
      }
      count++;

      String minecrat_name = "";
      if (key.contains("item.minecraft")) {
        minecrat_name = key.replace("item.minecraft.", "");
      } else if (key.contains("block.minecraft")) {
        minecrat_name = key.replace("block.minecraft.", "");
      } else {
        continue; // 如果不是 item 或 block 的鍵，則跳過
      }

      if (minecrat_name.isEmpty()) {
        continue; // 如果處理後的名稱是空的，則跳過
      }

      // 如果 minecrat_name 有 . 就跳過
      if (minecrat_name.contains(".")) {
        // DataBase.Print("MinecraftLang 鍵包含點: " + minecrat_name);
        continue; // 如果名稱包含點，則跳過
      }

      if (Minecraft_Items.containsKey(minecrat_name)) {
        // DataBase.Print("MinecraftLang 重複鍵: " + minecrat_name);
        continue; // 如果已經存在這個鍵，則跳過
      }
      Minecraft_Items.put(minecrat_name, value);
    }

    DataBase.Print(AnsiColor.YELLOW + "[LoadMinecraftLang] Minecraft Lang Loads: " + AnsiColor.GREEN + count);
    DataBase.Print(AnsiColor.YELLOW + "[LoadMinecraftLang] Minecraft Origin Items: " + AnsiColor.GREEN + Minecraft_Items.size());
  }
}