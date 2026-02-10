package org.tsob.MCLang.DataBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.Main;
import org.tsob.MCLang.FileIO.FileMessage;
import org.tsob.MCLang.FileIO.JsonFileIOMinecraftLang;
import org.tsob.MCLang.Platform.SchedulerFactory;

/**
 * 基本資料暫存區
 * @author brian
 *
 */
public class DataBase {
  
  /**
   * 此插件名稱
   */
  public static String pluginName = "MCLang";
  
  /**
   * message 設定
   */
  public static FileMessage fileMessage = new FileMessage();

  /**
   * Minecraft Lang 設定檔
   */
  public static JsonFileIOMinecraftLang fileMinecraftLang = new JsonFileIOMinecraftLang(Main.plugin.getConfig().getString("lang"));

  /**
   * 紀錄已經讀取的Minecraft Lang
   */
  public static Map<String, JsonFileIOMinecraftLang> minecraftLangMap = new HashMap<String, JsonFileIOMinecraftLang>();

  /**
   * 傳給玩家的訊息加上 Message.Title
   * @param player 玩家
   * @param msg 文字訊息
   */
  public static void sendMessage(Player player,String msg){
    SchedulerFactory.get().runEntityTask(Main.plugin, player, () ->
      player.sendMessage(DataBase.fileMessage.getString("Message.Title") + "§f" + msg));
  }
  
  /**
   * 顯示訊息 在cmd 裡顯示 "[MCLang] " + msg
   * @param msg 要顯示的文字
   */
  public static void Print(String msg){
      Main.plugin.getLogger().info(AnsiColor.minecraftToAnsiColor(msg) + AnsiColor.RESET);
    //System.out.print("[MCLang] " + msg);
  }
  
  /**
   * 顯示訊息 在cmd 裡顯示 "[MCLang] " + msg
   * @param msg 要顯示的文字
   */
  public static void Print(List<String> msg){
    for(String str : msg) Main.plugin.getLogger().info(AnsiColor.minecraftToAnsiColor(str) + AnsiColor.RESET);
    //System.out.print("[MCLang] " + msg);
  }
  
  /**
   * 取得是否顯示debug 專用訊息
   * @return
   */
  public static boolean getDebug() {
    return (Main.plugin.getConfig().contains("Debug") && Main.plugin.getConfig().getBoolean("Debug"));
  }
}
