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

  @Override
  public void onEnable() {
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
  }

  @Override
  public void onReload() {
    // 重新加載 API 相關資源
    boolean debug = Main.plugin.getConfig().getBoolean("debug", false);
    if (debug) {
      DataBase.Print("Debug 模式啟用中，正在重新加載 API 相關 指令...");
      RegistryCommandSystem.registerCommands(new Commandapitest());
      DataBase.Print("Debug 模式啟用完成");
    }
  }
}