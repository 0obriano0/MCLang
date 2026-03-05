package org.tsob.MCLang.API;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.tsob.MCLang.Main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * 提供簡易 Web API 與內建前端頁面，方便管理員/開發者查看 API 與語言包內容。
 */
public class WebApiServer {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private HttpServer server;
  private final String host;
  private final int port;
  private final boolean corsEnabled;
  private final int maxEntriesPerRequest;

  public WebApiServer(String host, int port, boolean corsEnabled, int maxEntriesPerRequest) {
    this.host = host;
    this.port = port;
    this.corsEnabled = corsEnabled;
    this.maxEntriesPerRequest = maxEntriesPerRequest;
  }

  public void start() throws IOException {
    if (server != null) {
      return;
    }

    server = HttpServer.create(new InetSocketAddress(host, port), 0);
    server.createContext("/", new RootHandler());
    server.createContext("/api/docs", new DocsHandler());
    server.createContext("/api/languages", new LanguagesHandler());
    server.createContext("/api/translate", new TranslateHandler());
    server.setExecutor(null);
    server.start();
  }

  public void stop() {
    if (server != null) {
      server.stop(1);
      server = null;
    }
  }

  private class RootHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      String path = exchange.getRequestURI().getPath();
      if (!"/".equals(path)) {
        sendJson(exchange, 404, Map.of("error", "Not found"));
        return;
      }
      if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        sendMethodNotAllowed(exchange);
        return;
      }
      sendHtml(exchange, 200, buildHtmlPage());
    }
  }

  private class DocsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if (handleOptions(exchange)) {
        return;
      }
      if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
        sendMethodNotAllowed(exchange);
        return;
      }

      Map<String, Object> docs = new LinkedHashMap<>();
      docs.put("service", "MCLang Web API");
      docs.put("version", Main.plugin.getDescription().getVersion());
      docs.put("baseUrl", "http://" + host + ":" + port);
      docs.put("endpoints", List.of(
          Map.of("method", "GET", "path", "/api/docs", "description", "API 使用說明"),
          Map.of("method", "GET", "path", "/api/languages", "description", "取得語言清單（含版本）"),
          Map.of("method", "GET", "path", "/api/languages/{lang}?version=1.21.5&prefix=item.minecraft.&limit=50&offset=0",
              "description", "取得語言包代碼與翻譯內容"),
          Map.of("method", "GET", "path", "/api/translate?lang=zh_tw&key=item.minecraft.diamond_sword",
              "description", "以 GET 查詢單一翻譯"),
          Map.of("method", "POST", "path", "/api/translate",
              "description", "以 JSON Body 查詢單一翻譯", "body", Map.of("lang", "zh_tw", "key", "item.minecraft.diamond_sword"))));
      sendJson(exchange, 200, docs);
    }
  }

  private class LanguagesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if (handleOptions(exchange)) {
        return;
      }
      String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
      if (!"GET".equals(method)) {
        sendMethodNotAllowed(exchange);
        return;
      }

      String path = exchange.getRequestURI().getPath();
      if ("/api/languages".equals(path)) {
        sendJson(exchange, 200, Map.of("languages", listLanguages()));
        return;
      }

      String prefix = "/api/languages/";
      if (path.startsWith(prefix) && path.length() > prefix.length()) {
        String lang = path.substring(prefix.length()).toLowerCase(Locale.ROOT);
        if (!isSafeToken(lang)) {
          sendJson(exchange, 400, Map.of("error", "Invalid lang"));
          return;
        }

        Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
        String version = query.getOrDefault("version", "").trim();
        if (!version.isEmpty() && !isSafeVersion(version)) {
          sendJson(exchange, 400, Map.of("error", "Invalid version"));
          return;
        }

        int limit = parseInt(query.get("limit"), 200);
        int offset = parseInt(query.get("offset"), 0);
        if (limit < 1) {
          limit = 1;
        }
        if (offset < 0) {
          offset = 0;
        }
        limit = Math.min(limit, maxEntriesPerRequest);

        String keyPrefix = query.getOrDefault("prefix", "");
        Path file = findLangFile(lang, version);
        if (file == null || !Files.exists(file)) {
          sendJson(exchange, 404, Map.of("error", "Language pack not found", "lang", lang, "version", version));
          return;
        }

        JsonNode root = objectMapper.readTree(file.toFile());
        Map<String, String> entries = new LinkedHashMap<>();
        int skipped = 0;
        Iterator<Map.Entry<String, JsonNode>> it = root.fields();
        while (it.hasNext()) {
          Map.Entry<String, JsonNode> entry = it.next();
          String key = entry.getKey();
          if (!keyPrefix.isEmpty() && !key.startsWith(keyPrefix)) {
            continue;
          }
          if (skipped < offset) {
            skipped++;
            continue;
          }
          if (entries.size() >= limit) {
            break;
          }
          entries.put(key, entry.getValue().asText());
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("lang", lang);
        response.put("version", extractVersionFromPath(file));
        response.put("file", file.getFileName().toString());
        response.put("limit", limit);
        response.put("offset", offset);
        response.put("prefix", keyPrefix);
        response.put("entries", entries);
        sendJson(exchange, 200, response);
        return;
      }

      sendJson(exchange, 404, Map.of("error", "Not found"));
    }
  }

  private class TranslateHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if (handleOptions(exchange)) {
        return;
      }

      String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
      String lang;
      String key;
      if ("GET".equals(method)) {
        Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
        lang = query.getOrDefault("lang", "").toLowerCase(Locale.ROOT);
        key = query.getOrDefault("key", "");
      } else if ("POST".equals(method)) {
        try {
          JsonNode body = objectMapper.readTree(exchange.getRequestBody());
          lang = body.path("lang").asText("").toLowerCase(Locale.ROOT);
          key = body.path("key").asText("");
        } catch (Exception e) {
          sendJson(exchange, 400, Map.of("error", "Invalid JSON body"));
          return;
        }
      } else {
        sendMethodNotAllowed(exchange);
        return;
      }

      if (!isSafeToken(lang) || key == null || key.isBlank()) {
        sendJson(exchange, 400, Map.of("error", "lang and key are required"));
        return;
      }

      MCLang mclang = new MCLang(lang);
      String value = mclang.getString(key);
      sendJson(exchange, 200, Map.of("lang", lang, "key", key, "value", value));
    }
  }

  private List<Map<String, Object>> listLanguages() {
    Path root = Main.plugin.getDataFolder().toPath().resolve("mc_lang");
    if (!Files.exists(root)) {
      return List.of();
    }

    Map<String, TreeSet<String>> langVersions = new HashMap<>();
    try (Stream<Path> stream = Files.walk(root, 3)) {
      stream
          .filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().endsWith(".json"))
          .filter(p -> !"default.json".equalsIgnoreCase(p.getFileName().toString()))
          .forEach(file -> {
            String lang = file.getFileName().toString().replace(".json", "").toLowerCase(Locale.ROOT);
            Path parent = file.getParent();
            String version = parent != null ? parent.getFileName().toString() : "unknown";
            langVersions.computeIfAbsent(lang, k -> new TreeSet<>(Comparator.reverseOrder())).add(version);
          });
    } catch (IOException ignored) {
    }

    List<Map<String, Object>> result = new ArrayList<>();
    langVersions.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .forEach(entry -> result.add(Map.of(
            "lang", entry.getKey(),
            "versions", new ArrayList<>(entry.getValue()))));
    return result;
  }

  private Path findLangFile(String lang, String version) {
    Path root = Main.plugin.getDataFolder().toPath().resolve("mc_lang");
    if (!version.isEmpty()) {
      Path fixed = root.resolve(version).resolve(lang + ".json");
      if (Files.exists(fixed)) {
        return fixed;
      }
    }

    String currentVersion = Bukkit.getBukkitVersion().split("-")[0];
    Path current = root.resolve(currentVersion).resolve(lang + ".json");
    if (Files.exists(current)) {
      return current;
    }

    try (Stream<Path> stream = Files.walk(root, 3)) {
      return stream
          .filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().equalsIgnoreCase(lang + ".json"))
          .findFirst()
          .orElse(null);
    } catch (IOException e) {
      return null;
    }
  }

  private String extractVersionFromPath(Path file) {
    Path parent = file.getParent();
    return parent == null ? "unknown" : parent.getFileName().toString();
  }

  private boolean handleOptions(HttpExchange exchange) throws IOException {
    if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
      addCommonHeaders(exchange);
      exchange.sendResponseHeaders(204, -1);
      return true;
    }
    return false;
  }

  private void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
    sendJson(exchange, 405, Map.of("error", "Method not allowed"));
  }

  private void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
    byte[] data = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(body);
    addCommonHeaders(exchange);
    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
    exchange.sendResponseHeaders(status, data.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(data);
    }
  }

  private void sendHtml(HttpExchange exchange, int status, String html) throws IOException {
    byte[] data = html.getBytes(StandardCharsets.UTF_8);
    addCommonHeaders(exchange);
    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
    exchange.sendResponseHeaders(status, data.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(data);
    }
  }

  private void addCommonHeaders(HttpExchange exchange) {
    if (corsEnabled) {
      exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
      exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
      exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }
  }

  private int parseInt(String value, int fallback) {
    if (value == null || value.isBlank()) {
      return fallback;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return fallback;
    }
  }

  private boolean isSafeToken(String token) {
    return token != null && token.matches("[a-z0-9_]+");
  }

  private boolean isSafeVersion(String version) {
    return version != null && version.matches("[0-9.]+");
  }

  private Map<String, String> parseQuery(String rawQuery) {
    Map<String, String> query = new HashMap<>();
    if (rawQuery == null || rawQuery.isBlank()) {
      return query;
    }
    String[] pairs = rawQuery.split("&");
    for (String pair : pairs) {
      int idx = pair.indexOf('=');
      if (idx > 0) {
        String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
        String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
        query.put(key, value);
      }
    }
    return query;
  }

  private String buildHtmlPage() {
    return """
        <!doctype html>
        <html lang="zh-Hant">
        <head>
          <meta charset="utf-8">
          <meta name="viewport" content="width=device-width,initial-scale=1">
          <title>MCLang Web API</title>
          <style>
            body { font-family: sans-serif; margin: 24px; background: #0f172a; color: #e2e8f0; }
            h1,h2 { margin-bottom: 8px; }
            .card { background:#1e293b; border-radius:10px; padding:16px; margin-bottom:16px; }
            input,select,button { padding:8px; margin:4px 0; border-radius:6px; border:1px solid #475569; background:#0f172a; color:#e2e8f0; }
            button { cursor:pointer; }
            pre { background:#020617; color:#cbd5e1; padding:12px; border-radius:8px; max-height:360px; overflow:auto; }
            .row { display:flex; gap:12px; flex-wrap: wrap; align-items:center; }
          </style>
        </head>
        <body>
          <h1>MCLang Web API</h1>
          <div class="card">
            <h2>1) API 使用說明</h2>
            <button onclick="loadDocs()">載入 /api/docs</button>
            <pre id="docs"></pre>
          </div>

          <div class="card">
            <h2>2) 查詢語言包與代碼</h2>
            <button onclick="loadLanguages()">載入語言清單</button>
            <div class="row">
              <label>語言代碼: <input id="lang" value="en_us"></label>
              <label>版本(可空白): <input id="version" placeholder="1.21.5"></label>
              <label>前綴過濾: <input id="prefix" value="item.minecraft."></label>
              <button onclick="loadLangData()">查詢語言包</button>
            </div>
            <pre id="langs"></pre>
          </div>

          <div class="card">
            <h2>3) 前後端分離測試 (GET / POST)</h2>
            <div class="row">
              <label>lang: <input id="tlang" value="zh_tw"></label>
              <label>key: <input id="tkey" value="item.minecraft.diamond_sword"></label>
              <button onclick="translateGet()">GET</button>
              <button onclick="translatePost()">POST</button>
            </div>
            <pre id="translate"></pre>
          </div>

          <script>
            async function loadDocs() {
              const res = await fetch('/api/docs');
              document.getElementById('docs').textContent = JSON.stringify(await res.json(), null, 2);
            }
            async function loadLanguages() {
              const res = await fetch('/api/languages');
              document.getElementById('langs').textContent = JSON.stringify(await res.json(), null, 2);
            }
            async function loadLangData() {
              const lang = document.getElementById('lang').value.trim();
              const version = document.getElementById('version').value.trim();
              const prefix = document.getElementById('prefix').value.trim();
              const q = new URLSearchParams({ limit: '80', offset: '0' });
              if (version) q.set('version', version);
              if (prefix) q.set('prefix', prefix);
              const res = await fetch('/api/languages/' + encodeURIComponent(lang) + '?' + q.toString());
              document.getElementById('langs').textContent = JSON.stringify(await res.json(), null, 2);
            }
            async function translateGet() {
              const lang = document.getElementById('tlang').value.trim();
              const key = document.getElementById('tkey').value.trim();
              const q = new URLSearchParams({ lang, key });
              const res = await fetch('/api/translate?' + q.toString());
              document.getElementById('translate').textContent = JSON.stringify(await res.json(), null, 2);
            }
            async function translatePost() {
              const lang = document.getElementById('tlang').value.trim();
              const key = document.getElementById('tkey').value.trim();
              const res = await fetch('/api/translate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ lang, key })
              });
              document.getElementById('translate').textContent = JSON.stringify(await res.json(), null, 2);
            }
          </script>
        </body>
        </html>
        """;
  }
}
