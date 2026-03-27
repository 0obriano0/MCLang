package org.tsob.MCLang.API;

import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.Main;
import org.tsob.MCLang.Command.Commandapitest;
import org.tsob.MCLang.Command.RegistryCommandSystem;
import org.tsob.MCLang.DataBase.DataBase;

/**
 * API 主類實現
 * 負責註冊 API 相關的指令和功能
 */
public class apiMain extends ApiMainBase {

  @Override
  public void onEnable() {
    // 註冊 API 相關的指令
    boolean debug = Main.plugin.getConfig().getBoolean("debug", false);
    if (debug) {
      printDebug(DataBase.fileMessage.getString("Debug.Register_Commands"));
      RegistryCommandSystem.registerCommands(new Commandapitest());
      printDebug(DataBase.fileMessage.getString("Debug.Complete"));
    }
  }

  @Override
  public void onDisable() {
    // 清理 API 相關資源
  }

  @Override
  public void onReload() {
    // 重新加載 API 相關資源
    boolean debug = Main.plugin.getConfig().getBoolean("debug", false);
    if (debug) {
      printDebug(DataBase.fileMessage.getString("Debug.Reload_Commands"));
      RegistryCommandSystem.registerCommands(new Commandapitest());
      printDebug(DataBase.fileMessage.getString("Debug.Complete"));
    }
  }

  private static void printDebug(String msg) {
    String title = AnsiColor.minecraftToAnsiColor(DataBase.fileMessage.getString("Debug.Title"));
    msg = title + AnsiColor.WHITE + AnsiColor.minecraftToAnsiColor(msg);
    DataBase.Print(msg);
  }
}
