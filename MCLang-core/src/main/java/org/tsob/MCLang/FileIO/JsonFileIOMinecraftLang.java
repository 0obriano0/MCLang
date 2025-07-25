package org.tsob.MCLang.FileIO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.Main;
import org.tsob.MCLang.DataBase.DataBase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFileIOMinecraftLang extends JsonFileIO {
  private String lang; // 預設語言

  /**
   * default.json fallback support
   */
  private volatile boolean usingDefaultData = false;
  private volatile JsonNode defaultData = null;
  private static final ExecutorService langDownloadExecutor = Executors.newCachedThreadPool();
  private volatile boolean downloading = false;

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

  public void reloadWithLangAndVersion() {
    reloadFile();
    reloadNode();
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

  /**
   * 取得所有path 用 List String 顯示
   */
  public List<String> getAllPaths() {
    List<String> paths = new ArrayList<String>();
    if (data == null)
      return paths;
    Iterator<Map.Entry<String, JsonNode>> iterator = data.fields();
    while (iterator.hasNext()) {
      Map.Entry<String, JsonNode> entry = iterator.next();
      String key = entry.getKey();
      if (key != null && !key.isEmpty()) {
        paths.add(key);
      }
    }
    return paths;
  }

  // 將 JsonNode 轉成我要的資料
  private void reloadNode() {
    if (data == null)
      return;
    int count = 0;
    Iterator<Map.Entry<String, JsonNode>> iterator = data.fields();
    while (iterator.hasNext()) {
      Map.Entry<String, JsonNode> entry = iterator.next();
      String key = entry.getKey();
      String value = entry.getValue().asText();

      if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
        continue; // 跳過空鍵或值
      }
      count++;
    }

    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Load_LangItems")
      .replace("%lang%", this.lang)
      .replace("%count%", String.valueOf(count))); // 顯示重新載入節點訊息
  }

  /**
   * 嘗試建立語言檔案：
   * 1. 先檢查 jar 內是否有資源，有的話直接存檔。
   * 2. 若無則從 Mojang 官方下載語言檔，並驗證 SHA1。
   * 
   * ★ 新增：如果找不到語言檔，先暫時讀取 default.json，然後用多執行緒下載語言檔，下載完畢自動 reload，期間所有 getString/getStringList 都會回傳 default.json 的資料。
   * 
   * @param fullUrl 語言檔案相對路徑（如 mc_lang/1.21.5/en_us.json）
   */
  @Override
  protected void createFile(String fullUrl) {
    if (downloading) {
        // Already downloading, do not start another download
        return;
    }
    downloading = true;
    // 先檢查 jar 內是否有資源
    try {
      InputStream in = Main.plugin.getResource(fullUrl);
      if (in != null) {
        // jar 內有，直接存檔
        Main.plugin.saveResource(fullUrl, true);
        in.close();
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 2. jar 內沒有，從網路下載前，先 fallback default.json
    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Error_LangNotFound")
      .replace("%fileName%", this.getFileName()));
    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Internet_TryToDownload")
      .replace("%fileName%", this.getFileName()));

    try {
      File dataFolder = Main.plugin.getDataFolder();
      Path defaultPath = Paths.get(dataFolder.toString(), "mc_lang", "default.json");
      if (Files.exists(defaultPath)) {
        this.defaultData = objectMapper.readTree(defaultPath.toFile());
        this.usingDefaultData = true;
        printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Temp_UseDefaultJson")
          .replace("%fileName%", this.getFileName()));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 非同步下載語言檔
    langDownloadExecutor.submit(() -> {
      try {
        downloadLangFile(fullUrl);
        // 下載完成後 reload，切回正式語言檔
        Bukkit.getScheduler().runTask(Main.plugin, () -> {
          this.reloadFile();
          this.usingDefaultData = false;
          printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Internet_DownloadSuccess_Reload")
          .replace("%fileName%", this.getFileName()));
          downloading = false;
        });
      } catch (ConnectException e) {
        printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Internet_DownloadFail")
          .replace("%fileName%", this.getFileName()));
        downloading = false;
      } catch (Exception e) {
        e.printStackTrace();
        printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Internet_DownloadFail")
          .replace("%fileName%", this.getFileName()));
        downloading = false;
      }
    });
  }

  /**
   * 將原本 createFile 裡的下載邏輯移到這裡
   */
  private void downloadLangFile(String fullUrl) throws Exception {
    String version = getMinecraftVersion();

    if (this.lang == null || this.lang.isEmpty())
      this.lang = "en_us"; // 預設語言
    if (this.lang.equals("en")) {
      this.lang = "en_us"; // Minecraft 默認語言
    }
    this.lang = this.lang.trim().toLowerCase(); // 確保語言是小寫
    // 嘗試從 Mojang 的資源下載語言檔
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
    
    if (this.lang.equals("en_us")) {
      // 下載 client.jar，準備解壓 en_us.json
      String clientJarUrl = clientManifest.get("downloads").get("client").get("url").asText();
      String clientSha1 = clientManifest.get("downloads").get("client").get("sha1").asText();
      Path clientJarPath = Paths.get(Main.plugin.getDataFolder().toString(), "client.jar");
      downloadFile(clientJarUrl, clientJarPath);
      // 驗證 client.jar 的 SHA1
      if (!sha1(clientJarPath).equalsIgnoreCase(clientSha1)) {
        Files.deleteIfExists(clientJarPath);
        throw new RuntimeException("client.jar SHA1 mismatch!" + version);
      } 

      // 從 client.jar 解壓 en_us.json 語言檔
      try (ZipFile zip = new ZipFile(clientJarPath.toFile())) {
        ZipEntry entry = zip.getEntry("assets/minecraft/lang/en_us.json");
        File dataFolder = Main.plugin.getDataFolder();
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
      // 下載 asset index
      String assetIndexUrl = clientManifest.get("assetIndex").get("url").asText();
      resp = client.send(HttpRequest.newBuilder().uri(URI.create(assetIndexUrl)).build(), HttpResponse.BodyHandlers.ofString());
      JsonNode assetIndex = mapper.readTree(resp.body()).get("objects");

      // 查找語言檔 hash
      String key = "minecraft/lang/" + this.lang + ".json";
      if (!assetIndex.has(key)) throw new RuntimeException("找不到語言檔: " + key);
      String hash = assetIndex.get(key).get("hash").asText();
      String url = "https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash;
      // 下載到 plugins/MCLang/mc_lang/{version}/{lang}.json
      File dataFolder = Main.plugin.getDataFolder();
      Path outPath = Paths.get(dataFolder.toString(), "mc_lang", version, this.lang + ".json");
      Files.createDirectories(outPath.getParent());
      downloadFile(url, outPath);

      // 驗證 hash
      String fileHash = sha1(outPath);
      if (!fileHash.equalsIgnoreCase(hash)) {
        throw new RuntimeException("語言檔 SHA1 不符: " + this.lang + ".json");
      }
    }

    printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Internet_DownloadSuccess")
      .replace("%fileName%", this.getFileName())); // 顯示下載成功訊息
  }

  // 下載檔案到指定路徑
  static void downloadFile(String url, Path out) throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
    HttpResponse<InputStream> resp = client.send(req, HttpResponse.BodyHandlers.ofInputStream());

    long contentLength = 0;
    if (resp.headers().firstValue("Content-Length").isPresent()) {
      contentLength = Long.parseLong(resp.headers().firstValue("Content-Length").get());
    }

    try (InputStream is = resp.body()) {
      Files.createDirectories(out.getParent());
      try (OutputStream os = Files.newOutputStream(out)) {
        byte[] buffer = new byte[8192];
        long totalRead = 0;
        int read;
        long lastPrint = 0;
        while ((read = is.read(buffer)) != -1) {
          os.write(buffer, 0, read);
          totalRead += read;
          // 每 1 秒或最後一次才顯示進度
          long now = System.currentTimeMillis();
          if (contentLength > 0 && (now - lastPrint > 1000 || totalRead == contentLength)) {
            double percent = (totalRead * 100.0) / contentLength;
            double mbDone = totalRead / 1024.0 / 1024.0;
            double mbTotal = contentLength / 1024.0 / 1024.0;
            // DataBase.Print("下載進度：" + String.format("%.2f MB / %.2f MB (%.1f%%)", mbDone, mbTotal, percent));
            printCmd(DataBase.fileMessage.getString("LoadMinecraftLang.Internet_DownloadProgress")
              .replace("%fileName%", out.getFileName().toString())
              .replace("%done%", String.format("%.2f", mbDone))
              .replace("%total%", String.format("%.2f", mbTotal))
              .replace("%percent%", String.format("%.1f", percent)));
            lastPrint = now;
          }
        }
      }
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

  @Override
  protected void readFile() {
    if (downloading) {
        // Already downloading, do not start another download
        return;
    }
    File fileLoad;
    String fullUrl = super.getFileName();
    if (super.getUrl() == null || super.getUrl().isEmpty())
      fileLoad = new File(Main.plugin.getDataFolder(), super.getFileName());
    else {
      fileLoad = new File("./" + Main.plugin.getDataFolder().toString() + "/" + super.getUrl() + "/" + super.getFileName());
      fullUrl = super.getUrl() + "\\" + super.getFileName();
    }
    
    if (!fileLoad.exists()) createFile(fullUrl);
      
    try {
      data = objectMapper.readTree(fileLoad);
    } catch (IOException e) {
      // e.printStackTrace();
      DataBase.Print("Failed to read JSON file: " + fullUrl);
      data = null;
    }
  }


  // ★ Fallback: 下載期間所有 getString/getStringList 都讀 default.json
  @Override
  public String getString(String path) {
    if(usingDefaultData && defaultData != null) {
      JsonNode node = getNodeByPath(defaultData, path);
      return node != null && node.isTextual() ? node.asText() : null;
    }
    return super.getString(path);
  }

  @Override
  public List<String> getStringList(String path) {
    if(usingDefaultData && defaultData != null) {
      JsonNode node = getNodeByPath(defaultData, path);
      List<String> result = new ArrayList<>();
      if (node != null && node.isArray()) {
        for (JsonNode n : node) {
          result.add(n.asText());
        }
      }
      return result;
    }
    return super.getStringList(path);
  }

  // 給 fallback 用的 JsonNode 路徑查詢
  private JsonNode getNodeByPath(JsonNode root, String path) {
    String[] parts = path.split("\\.");
    JsonNode node = root;
    for (String part : parts) {
      node = node.get(part);
      if (node == null) break;
    }
    return node;
  }
}