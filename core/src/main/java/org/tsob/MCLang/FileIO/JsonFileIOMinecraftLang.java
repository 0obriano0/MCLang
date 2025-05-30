package org.tsob.MCLang.FileIO;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.MCLang;
import org.tsob.MCLang.DataBase.DataBase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFileIOMinecraftLang extends JsonFileIO {

  public Map<String,String> Minecraft_Items = new HashMap<String,String>();

  private String lang; // 預設語言
  /**
   * @param langFromConfig 語言設定，抓不到自動用 en_us
   */
  public JsonFileIOMinecraftLang(String langFromConfig) {
    super(getVersionPath(getMinecraftVersion()),ini_getLangFileName(langFromConfig));
    this.lang = resolveLang(langFromConfig); // 設定語言
    reloadNode();
  }

  private static String ini_getLangFileName(String langFromConfig) {
    String lang = "";
    if (langFromConfig == null || langFromConfig.isEmpty())
      lang = "en_us"; // 預設語言
    else 
      lang = langFromConfig.toLowerCase(); // 確保語言是小寫
      
    if (lang == null || lang.isEmpty())
      lang = "en_us"; // 預設語言
    if (lang.equals("en"))
      lang = "en_us"; // Minecraft 默認語言
    return String.format("%s.json", lang.toLowerCase());
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
  private String resolveLang(String langFromConfig) {
    String _lang = langFromConfig;
    if (langFromConfig == null || langFromConfig.isEmpty()) {
      if (this.lang != null && !this.lang.isEmpty())
        _lang = this.lang.toLowerCase(); // 如果已經設定了語言，直接使用
      else {
        _lang = "en_us"; // 預設語言
      }
    } else {
      _lang = langFromConfig.toLowerCase(); // 確保語言是小寫
      if (_lang.equals("en")) {
        _lang = "en_us"; // Minecraft 默認語言
      }
    }

    setLang(_lang);

    return _lang;
  }

  /**
   * 取得完整路徑
   */
  private static String getVersionPath(String version) {
    if (version == null || version.isEmpty())
      version = "1.21.5"; // 預設版本號

    String path = String.format("mc_lang/%s", version);
    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.SetVersionPath")
      .replace("%version%", version)
      .replace("%versionPath%", path)); // 顯示版本路徑訊息
    return path;
  }

  private void setLang(String _lang) {
    String oldLang = this.lang; // 保存舊語言
    oldLang = oldLang == null ? "" : oldLang.toLowerCase(); // 確保舊語言是小寫
    oldLang = oldLang.trim(); // 去除前後空格
    if (_lang == null || _lang.isEmpty()) {
      this.lang = "en_us"; // 預設語言
    } else {
      this.lang = _lang.toLowerCase(); // 確保語言是小寫
      if (this.lang.equals("en")) {
        this.lang = "en_us"; // Minecraft 默認語言
      }
    }
    // trim
    this.lang = this.lang.trim(); // 去除前後空格
    if (this.lang.toLowerCase() == oldLang.toLowerCase()) {
      return; // 如果語言沒有變化，則不需要重新設定
    }

    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.SetLang")
      .replace("%lang%", this.lang)); // 顯示設定語言訊息
  }

  private String getLangFileName(String _lang) {
    setLang(_lang); // 設定語言

    if (this.lang == null || this.lang.isEmpty())
      this.lang = "en_us"; // 預設語言

    if (this.lang == "en") {
      this.lang = "en_us"; // Minecraft 默認語言
    }

    this.lang = this.lang.toLowerCase(); // 確保語言是小寫

    String fileName = String.format("%s.json", this.lang);
    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.GetLangFileName")
      .replace("%lang%", this.lang)
      .replace("%fileName%", fileName)); // 顯示設定檔案名稱訊息
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

    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Load_LangItems")
      .replace("%lang%", this.lang)
      .replace("%count%", String.valueOf(count))); // 顯示重新載入節點訊息
    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Load_MinecraftOriginItem")
      .replace("%count%", String.valueOf(Minecraft_Items.size()))); // 顯示載入 Minecraft 原始物品訊息
  }

  /**
   * 嘗試建立語言檔案：
   * 1. 先檢查 jar 內是否有資源，有的話直接存檔。
   * 2. 若無則從 Mojang 官方下載語言檔，並驗證 SHA1。
   * @param fullUrl 語言檔案相對路徑（如 mc_lang/1.21.5/en_us.json）
   */
  @Override
  protected void createFile(String fullUrl) {
    // 先檢查 jar 內是否有資源
    try {
      // 1. 檢查 jar 內資源
      InputStream in = MCLang.plugin.getResource(fullUrl);
      if (in != null) {
        // jar 內有，直接存檔
        MCLang.plugin.saveResource(fullUrl, true);
        in.close();
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 2. jar 內沒有，從網路下載
    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Error_LangNotFound")
      .replace("%fileName%", this.getFileName()));
    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Internet_TryToDownload")
      .replace("%fileName%", this.getFileName()));

    String version = getMinecraftVersion();

    if (this.lang == null || this.lang.isEmpty())
      this.lang = "en_us"; // 預設語言
    if (this.lang == "en") {
      this.lang = "en_us"; // Minecraft 默認語言
    }
    this.lang = this.lang.toLowerCase(); // 確保語言是小寫
    // 嘗試從 Mojang 的資源下載語言檔
    try {
      // 下載 Minecraft 版本資訊清單
      ObjectMapper mapper = new ObjectMapper();
      HttpClient client = HttpClient.newHttpClient();
      String manifestUrl = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
      HttpRequest req = HttpRequest.newBuilder().uri(URI.create(manifestUrl)).build();
      HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
      JsonNode manifest = mapper.readTree(resp.body());
      JsonNode versions = manifest.get("versions");
      String clientManifestUrl = null;
      // 取得對應版本的 manifest 下載網址
      for (JsonNode v : versions) {
        if (v.get("id").asText().equals(version)) {
          clientManifestUrl = v.get("url").asText();
          break;
        }
      }
      if (clientManifestUrl == null) {
        throw new RuntimeException("找不到對應版本 manifest: " + version);
      }
      // 下載 client manifest
      resp = client.send(HttpRequest.newBuilder().uri(URI.create(clientManifestUrl)).build(), HttpResponse.BodyHandlers.ofString());
      JsonNode clientManifest = mapper.readTree(resp.body());
      // 下載 asset index
      String assetIndexUrl = clientManifest.get("assetIndex").get("url").asText();
      resp = client.send(HttpRequest.newBuilder().uri(URI.create(assetIndexUrl)).build(), HttpResponse.BodyHandlers.ofString());
      JsonNode assetIndex = mapper.readTree(resp.body()).get("objects");
      
      if (this.lang == "en_us") {
        // 下載 client.jar，準備解壓 en_us.json
        String clientJarUrl = clientManifest.get("downloads").get("client").get("url").asText();
        String clientSha1 = clientManifest.get("downloads").get("client").get("sha1").asText();
        Path clientJarPath = Paths.get(MCLang.plugin.getDataFolder().toString(), "client.jar");
        downloadFile(clientJarUrl, clientJarPath);
        // 驗證 client.jar 的 SHA1
        if (!sha1(clientJarPath).equalsIgnoreCase(clientSha1)) {
          Files.deleteIfExists(clientJarPath);
          throw new RuntimeException("client.jar SHA1 mismatch!" + version);
        } 

        // 從 client.jar 解壓 en_us.json 語言檔
        try (ZipFile zip = new ZipFile(clientJarPath.toFile())) {
          ZipEntry entry = zip.getEntry("assets/minecraft/lang/en_us.json");
          File dataFolder = MCLang.plugin.getDataFolder();
          Path outPath = Paths.get(dataFolder.toString(), "mc_lang", version, this.lang + ".json");
          Files.createDirectories(outPath.getParent());
          if (entry != null) {
            try (InputStream is = zip.getInputStream(entry)) {
              Files.copy(is, outPath, StandardCopyOption.REPLACE_EXISTING);
            }
          }
        }
        // 刪除 client.jar，釋放空間
        Files.delete(clientJarPath);

      } else {
        // 查找語言檔 hash
        String key = "minecraft/lang/" + this.lang + ".json";
        if (!assetIndex.has(key)) throw new RuntimeException("找不到語言檔: " + key);
        String hash = assetIndex.get(key).get("hash").asText();
        String url = "https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash;
        // 下載到 plugins/MCLang/mc_lang/{version}/{lang}.json
        File dataFolder = MCLang.plugin.getDataFolder();
        Path outPath = Paths.get(dataFolder.toString(), "mc_lang", version, this.lang + ".json");
        Files.createDirectories(outPath.getParent());
        HttpRequest langReq = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<InputStream> langResp = client.send(langReq, HttpResponse.BodyHandlers.ofInputStream());
        try (InputStream is = langResp.body()) {
          Files.copy(is, outPath, StandardCopyOption.REPLACE_EXISTING);
        }
        // 驗證 hash
        String fileHash = sha1(outPath);
        if (!fileHash.equalsIgnoreCase(hash)) {
          throw new RuntimeException("語言檔 SHA1 不符: " + this.lang + ".json");
        }
      }

      printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Internet_DownloadSuccess")
        .replace("%fileName%", this.getFileName())); // 顯示下載成功訊息
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 下載檔案到指定路徑
  static void downloadFile(String url, Path out) throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
    HttpResponse<InputStream> resp = client.send(req, HttpResponse.BodyHandlers.ofInputStream());
    try (InputStream is = resp.body()) {
      Files.copy(is, out, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  // 計算檔案 SHA1
  private String sha1(Path file) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA-1");
    try (InputStream is = Files.newInputStream(file)) {
      byte[] buf = new byte[8192];
      int n;
      while ((n = is.read(buf)) > 0)
        md.update(buf, 0, n);
    }
    StringBuilder sb = new StringBuilder();
    for (byte b : md.digest())
      sb.append(String.format("%02x", b));
    return sb.toString();
  }

  private static void printCmd(String msg) {
    String title = AnsiColor.minecraftToAnsiColor(DataBase.fileMessage.getString("LoadMinecraftLang.Title"));
    msg = title + AnsiColor.WHITE + AnsiColor.minecraftToAnsiColor(msg);;
    DataBase.Print(msg);
  }
}