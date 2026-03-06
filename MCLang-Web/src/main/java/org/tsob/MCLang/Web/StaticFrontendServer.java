package org.tsob.MCLang.Web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.tsob.MCLang.DataBase.DataBase;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class StaticFrontendServer {
  private HttpServer server;
  private final String host;
  private final int port;
  private final Path frontendRoot;
  private final boolean corsEnabled;

  public StaticFrontendServer(String host, int port, Path frontendRoot, boolean corsEnabled) {
    this.host = host;
    this.port = port;
    this.frontendRoot = frontendRoot.toAbsolutePath().normalize();
    this.corsEnabled = corsEnabled;
  }

  public boolean start() throws IOException {
    if (server != null) {
      return false;
    }

    Files.createDirectories(frontendRoot);
    String bindHost = normalizeBindHost(host);
    if (bindHost == null) {
      server = HttpServer.create(new InetSocketAddress(port), 0);
    } else {
      server = HttpServer.create(new InetSocketAddress(bindHost, port), 0);
    }

    server.createContext("/", new StaticFileHandler());
    server.setExecutor(null);
    server.start();

    return true;
  }

  public void stop() {
    if (server != null) {
      server.stop(1);
      server = null;
    }
  }

  private class StaticFileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      if (handleOptions(exchange)) {
        return;
      }

      String method = exchange.getRequestMethod().toUpperCase(Locale.ROOT);
      if (!"GET".equals(method) && !"HEAD".equals(method)) {
        sendText(exchange, 405, "Method not allowed");
        return;
      }

      String requestPath = exchange.getRequestURI().getPath();
      if (requestPath == null || requestPath.isBlank() || "/".equals(requestPath)) {
        requestPath = "/index.html";
      }

      Path file = frontendRoot.resolve(requestPath.substring(1)).normalize();
      if (!file.startsWith(frontendRoot)) {
        sendText(exchange, 403, "Forbidden");
        return;
      }

      if (!Files.exists(file) || !Files.isRegularFile(file)) {
        sendText(exchange, 404, "Not found");
        return;
      }

      String contentType = guessContentType(file);
      byte[] data = Files.readAllBytes(file);
      addCommonHeaders(exchange);
      exchange.getResponseHeaders().set("Content-Type", contentType);
      exchange.sendResponseHeaders(200, data.length);
      if ("HEAD".equals(method)) {
        exchange.close();
        return;
      }
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(data);
      }
    }
  }

  private boolean handleOptions(HttpExchange exchange) throws IOException {
    if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
      addCommonHeaders(exchange);
      exchange.sendResponseHeaders(204, -1);
      return true;
    }
    return false;
  }

  private void sendText(HttpExchange exchange, int status, String message) throws IOException {
    byte[] data = message.getBytes();
    addCommonHeaders(exchange);
    exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
    exchange.sendResponseHeaders(status, data.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(data);
    }
  }

  private void addCommonHeaders(HttpExchange exchange) {
    if (corsEnabled) {
      exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
      exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS");
      exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }
  }

  private String guessContentType(Path file) {
    try {
      String guessed = Files.probeContentType(file);
      if (guessed != null && !guessed.isBlank()) {
        return guessed;
      }
    } catch (Exception e) {
      logDebug("Failed to probe content type: " + e.getMessage());
    }

    String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
    if (name.endsWith(".html") || name.endsWith(".htm")) {
      return "text/html; charset=utf-8";
    }
    if (name.endsWith(".css")) {
      return "text/css; charset=utf-8";
    }
    if (name.endsWith(".js")) {
      return "application/javascript; charset=utf-8";
    }
    if (name.endsWith(".json")) {
      return "application/json; charset=utf-8";
    }
    if (name.endsWith(".svg")) {
      return "image/svg+xml";
    }
    return "application/octet-stream";
  }

  private void logDebug(String message) {
    if (DataBase.getDebug()) {
      DataBase.Print("[StaticFrontendServer] " + message);
    }
  }

  private String normalizeBindHost(String host) {
    if (host == null) {
      return null;
    }
    String h = host.trim();
    if (h.isEmpty() || "*".equals(h) || "0.0.0.0".equals(h)) {
      return null;
    }
    return h;
  }
}
