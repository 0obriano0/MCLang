package org.tsob.MCLang.Web;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.Main;
import org.tsob.MCLang.DataBase.DataBase;
/**
 * API 主類實現
 * 負責註冊 API 相關的指令和功能
 */
public class WebMain extends WebMainBase {
  private final Object webServerLock = new Object();
  private WebApiServer backendApiServer;
  private StaticFrontendServer frontendServer;

  @Override
  public void onEnable() {
    startWebServers();
  }

  @Override
  public void onDisable() {
    // 清理 API 相關資源
    stopWebServers();
  }

  @Override
  public void onReload() {
    stopWebServers();
    startWebServers();
  }

  private void startWebServers() {
    synchronized (webServerLock) {
      boolean backendEnabled = Main.plugin.getConfig().getBoolean("web.enabled", false);

      String backendHost = Main.plugin.getConfig().getString("web.api.host",
          Main.plugin.getConfig().getString("web.host", "127.0.0.1"));
      int backendPort = Main.plugin.getConfig().getInt("web.api.port",
          Main.plugin.getConfig().getInt("web.port", 8765));
      boolean apiCorsEnabled = Main.plugin.getConfig().getBoolean("web.api.cors",
          Main.plugin.getConfig().getBoolean("web.cors", true));
      int maxEntries = Main.plugin.getConfig().getInt("web.api.maxEntriesPerRequest",
          Main.plugin.getConfig().getInt("web.maxEntriesPerRequest", 300));
      if (maxEntries < 50) {
        maxEntries = 50;
      }

      if (backendEnabled) {
        try {
          backendApiServer = new WebApiServer(backendHost, backendPort, apiCorsEnabled, maxEntries);
          backendApiServer.start();
          printCmd(DataBase.fileMessage.getString("Web.Backend_API_server_start")
            .replace("%host%", backendHost)
            .replace("%port%", String.valueOf(backendPort))
            .replace("%router%", "api/"));
        } catch (Exception e) {
          printCmd(DataBase.fileMessage.getString("Web.Backend_API_server_start_failed")
            .replace("%error%", e.getMessage()));
        }
      } else {
        return;
      }

      boolean frontendEnabled = Main.plugin.getConfig().getBoolean("web.frontend.enabled", backendEnabled);
      if (!frontendEnabled) {
        return;
      }

      String frontendHost = Main.plugin.getConfig().getString("web.frontend.host", "127.0.0.1");
      int frontendPort = Main.plugin.getConfig().getInt("web.frontend.port", 8766);
      boolean frontendCorsEnabled = Main.plugin.getConfig().getBoolean("web.frontend.cors", apiCorsEnabled);
      String staticDir = Main.plugin.getConfig().getString("web.frontend.staticDir", "web");
      Path frontendRoot = resolveFrontendRoot(staticDir);

      if ("web".equals(staticDir)) {
        ensureDefaultFrontendFiles();
      }

      try {
        frontendServer = new StaticFrontendServer(frontendHost, frontendPort, frontendRoot, frontendCorsEnabled);
        frontendServer.start();

        printCmd(DataBase.fileMessage.getString("Web.Frontend_server_start")
            .replace("%host%", frontendHost)
            .replace("%port%", String.valueOf(frontendPort)));
        printCmd(DataBase.fileMessage.getString("Web.Frontend_frontendRoot")
            .replace("%frontendRoot%", frontendRoot.toString()));
      } catch (Exception e) {
        printCmd(DataBase.fileMessage.getString("Web.Frontend_server_start_failed")
            .replace("%error%", e.getMessage()));
      }
    }
  }

  private void stopWebServers() {
    synchronized (webServerLock) {
      if (backendApiServer != null) {
        backendApiServer.stop();
        backendApiServer = null;
        printCmd(DataBase.fileMessage.getString("Web.Backend_API_server_stop"));
      }
      if (frontendServer != null) {
        frontendServer.stop();
        frontendServer = null;
        printCmd(DataBase.fileMessage.getString("Web.Frontend_server_stop"));
      }
    }
  }

  private Path resolveFrontendRoot(String staticDir) {
    if (staticDir == null || staticDir.isBlank()) {
      return Main.plugin.getDataFolder().toPath().resolve("web").toAbsolutePath().normalize();
    }
    Path configured = Paths.get(staticDir);
    if (configured.isAbsolute()) {
      return configured.toAbsolutePath().normalize();
    }
    return Main.plugin.getDataFolder().toPath().resolve(configured).toAbsolutePath().normalize();
  }

  private void ensureDefaultFrontendFiles() {
    trySaveFrontendResource("web/index.html");
    trySaveFrontendResource("web/style.css");
    trySaveFrontendResource("web/app.js");
  }

  private void trySaveFrontendResource(String resourcePath) {
    Path dest = Main.plugin.getDataFolder().toPath().resolve(resourcePath);
    if (dest.toFile().exists()) {
      return;
    }
    try {
      Main.plugin.saveResource(resourcePath, false);
    } catch (Exception e) {
      if (DataBase.getDebug()) {
        printCmd(DataBase.fileMessage.getString("Web.Frontend_server_resource_save_failed")
            .replace("%error%", e.getMessage())
            .replace("%resourcePath%", resourcePath));
      }
    }
  }

  private static void printCmd(String msg) {
    String title = AnsiColor.minecraftToAnsiColor(DataBase.fileMessage.getString("Web.Title"));
    msg = title + AnsiColor.WHITE + AnsiColor.minecraftToAnsiColor(msg);;
    DataBase.Print(msg);
  }
}
