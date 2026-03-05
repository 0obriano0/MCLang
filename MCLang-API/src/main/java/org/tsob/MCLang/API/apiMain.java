package org.tsob.MCLang.API;

import org.tsob.MCLang.Main;
import org.tsob.MCLang.Command.Commandapitest;
import org.tsob.MCLang.Command.RegistryCommandSystem;
import org.tsob.MCLang.DataBase.DataBase;
/**
 * API 主類實現
 * 負責註冊 API 相關的指令和功能
 */
public class apiMain extends ApiMainBase {
  private final Object webApiLock = new Object();
  private WebApiServer webApiServer;

  @Override
  public void onEnable() {
    startWebApiServer();
    // 註冊 API 相關的指令
    boolean debug = Main.plugin.getConfig().getBoolean("debug", false);
    if (debug) {
      DataBase.Print("Debug 模式啟用中，正在註冊 相關 指令...");
      RegistryCommandSystem.registerCommands(new Commandapitest());
      DataBase.Print("Debug 模式啟用完成");
    }
  }

  @Override
  public void onDisable() {
    // 清理 API 相關資源
    stopWebApiServer();
  }

  @Override
  public void onReload() {
    stopWebApiServer();
    startWebApiServer();
    // 重新加載 API 相關資源
    boolean debug = Main.plugin.getConfig().getBoolean("debug", false);
    if (debug) {
      DataBase.Print("Debug 模式啟用中，正在重新加載 API 相關 指令...");
      RegistryCommandSystem.registerCommands(new Commandapitest());
      DataBase.Print("Debug 模式啟用完成");
    }
  }

  private void startWebApiServer() {
    synchronized (webApiLock) {
      boolean enabled = Main.plugin.getConfig().getBoolean("web.enabled", false);
      if (!enabled) {
        return;
      }

      String host = Main.plugin.getConfig().getString("web.host", "127.0.0.1");
      int port = Main.plugin.getConfig().getInt("web.port", 8765);
      boolean corsEnabled = Main.plugin.getConfig().getBoolean("web.cors", true);
      int maxEntries = Main.plugin.getConfig().getInt("web.maxEntriesPerRequest", 300);
      if (maxEntries < 50) {
        maxEntries = 50;
      }

      try {
        webApiServer = new WebApiServer(host, port, corsEnabled, maxEntries);
        webApiServer.start();
        DataBase.Print("MCLang Web API started at http://" + host + ":" + port + "/");
      } catch (Exception e) {
        DataBase.Print("MCLang Web API start failed: " + e.getMessage());
      }
    }
  }

  private void stopWebApiServer() {
    synchronized (webApiLock) {
      if (webApiServer != null) {
        webApiServer.stop();
        webApiServer = null;
        DataBase.Print("MCLang Web API stopped.");
      }
    }
  }
}
