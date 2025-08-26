package org.tsob.MCLang.DataBase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.Main;
import org.tsob.MCLang.Command.ImainCommandSystem;
import org.tsob.MCLang.Command.ToolCommandSystem;
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
   * 插件目錄 插件附屬檔案的存放路徑
   */
  public static String pluginMainDir = "./plugins/MCLang/";
  
  /**
   * 此插件名稱
   */
  public static String pluginName = "MCLang";

  /**
   * main.java 的根目錄
   */
  public static String mainJavaPath = "/org/tsob/" + pluginName;

  /**
   * main.java 的根目錄 dot 的版本
   */
  public static String mainJavaPathDot = "org.tsob." + pluginName;
  
  /**
   * 指令列表
   */
  private static List<String> Commands = null;
  
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
   * 抓取指令列表(/MCLang 列表資料)
   * @param plugin 系統資料
   * @return 列表資料
   */
  public static List<String> getCommands(Plugin plugin){
    if(Commands == null) {
      Commands = new ArrayList<String>();
      URL jarURL = plugin.getClass().getResource(mainJavaPath + "/Command");
        URI uri;
      try {
        FileSystem fileSystem = null;
        uri = jarURL.toURI();
        Path myPath;
        if (uri.getScheme().equals("jar")) {
            fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
            myPath = fileSystem.getPath(mainJavaPath +"/Command");
        } else {
            myPath = Paths.get(uri);
        }
        if (fileSystem == null) {
          return Commands;
        }
        for (Iterator<Path> it = Files.walk(myPath, 1).iterator(); it.hasNext();){
          String[] path = it.next().toString().split("/");
          
          String file = path[path.length - 1];
          if(file.matches("(.*)class$")) {
            file = file.split("\\.")[0];
            if(file.matches("^Command.*") && !file.contains("$")) {
              String filename = file.split("Command")[1];
              // 用來判斷 command 是否存在
              ImainCommandSystem cmd = ToolCommandSystem.getCommandClass(filename);
              if (cmd != null) Commands.add(filename);
            }
          }
            //System.out.println(it.next());
          Collections.sort(Commands);
        }
        fileSystem.close();
      } catch (URISyntaxException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return Commands;
  }
  
  /**
   * 取得是否顯示debug 專用訊息
   * @return
   */
  public static boolean getDebug() {
    return (Main.plugin.getConfig().contains("Debug") && Main.plugin.getConfig().getBoolean("Debug"));
  }
}
