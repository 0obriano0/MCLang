package org.tsob.MCLang.Command;

import java.util.*;

/**
 * 子指令註冊系統
 * 用於管理指令的子指令
 */
public class RegistrySubCommandSystem {
  // 格式: 父指令名稱 -> 子指令列表
  private static final Map<String, Map<String, ImainCommandSystem>> subCommands = new HashMap<>();

  /**
   * 為指令註冊子指令
   * 
   * @param parentCommand 父指令名稱
   * @param subCommand    子指令實例
   */
  public static void registerSubCommand(String parentCommand, ImainCommandSystem subCommand) {
    subCommands.putIfAbsent(parentCommand, new LinkedHashMap<>());
    subCommands.get(parentCommand).put(subCommand.getName(), subCommand);
  }

  /**
   * 批量註冊子指令
   * 
   * @param parentCommand 父指令名稱
   * @param commands      子指令實例陣列
   */
  public static void registerSubCommands(String parentCommand, ImainCommandSystem... commands) {
    for (ImainCommandSystem command : commands) {
      registerSubCommand(parentCommand, command);
    }
  }

  /**
   * 取得子指令
   * 
   * @param parentCommand  父指令名稱
   * @param subCommandName 子指令名稱
   * @return 子指令實例
   */
  public static ImainCommandSystem getSubCommand(String parentCommand, String subCommandName) {
    Map<String, ImainCommandSystem> subs = subCommands.get(parentCommand);
    return subs != null ? subs.get(subCommandName) : null;
  }

  /**
   * 檢查子指令是否存在
   * 
   * @param parentCommand  父指令名稱
   * @param subCommandName 子指令名稱
   * @return true 如果存在
   */
  public static boolean hasSubCommand(String parentCommand, String subCommandName) {
    Map<String, ImainCommandSystem> subs = subCommands.get(parentCommand);
    return subs != null && subs.containsKey(subCommandName);
  }

  /**
   * 取得所有子指令名稱
   * 
   * @param parentCommand 父指令名稱
   * @return 子指令名稱列表
   */
  public static List<String> getSubCommandNames(String parentCommand) {
    Map<String, ImainCommandSystem> subs = subCommands.get(parentCommand);
    return subs != null ? new ArrayList<>(subs.keySet()) : Collections.emptyList();
  }

  /**
   * 取得所有子指令
   * 
   * @param parentCommand 父指令名稱
   * @return 子指令集合
   */
  public static Collection<ImainCommandSystem> getSubCommands(String parentCommand) {
    Map<String, ImainCommandSystem> subs = subCommands.get(parentCommand);
    return subs != null ? subs.values() : Collections.emptyList();
  }

  /**
   * 清除所有子指令
   */
  public static void clearAll() {
    // 在清除子指令前先呼叫 onDisable 以確保資源釋放
    for (Map<String, ImainCommandSystem> subs : subCommands.values()) {
      for (ImainCommandSystem cmd : subs.values()) {
        cmd.onDisable();
      }
    }
    subCommands.clear();
  }

  /**
   * 清除特定父指令的子指令
   */
  public static void clearSubCommandsForParent(String parentCommand) {
    // 在清除子指令前先呼叫 onDisable 以確保資源釋放
    Map<String, ImainCommandSystem> subs = subCommands.get(parentCommand);
    if (subs != null) {
      for (ImainCommandSystem cmd : subs.values()) {
        cmd.onDisable();
      }
    }
    subCommands.remove(parentCommand);
  }

  /**
   * 清除特定指令的子指令
   * 
   * @param parentCommand 父指令名稱
   */
  public static void clearSubCommands(String parentCommand) {
    subCommands.remove(parentCommand);
  }
}