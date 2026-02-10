package org.tsob.MCLang.Command;

import java.util.*;

public class RegistryCommandSystem {
  private static final Map<String, ImainCommandSystem> registeredCommands = new LinkedHashMap<>();

  /**
   * 註冊一個指令
   * 
   * @param command 指令實例
   */
  public static void registerCommand(ImainCommandSystem command) {
    String commandName = command.getName();
    if (registeredCommands.containsKey(commandName)) {
      throw new IllegalArgumentException("指令已經被註冊: " + commandName);
    }
    registeredCommands.put(commandName, command);
  }

  /**
   * 批量註冊指令
   * 
   * @param commands 指令實例陣列
   */
  public static void registerCommands(ImainCommandSystem... commands) {
    for (ImainCommandSystem command : commands) {
      registerCommand(command);
    }
  }

  /**
   * 取得指令實例
   * 
   * @param commandName 指令名稱
   * @return 指令實例，如果不存在則返回 null
   */
  public static ImainCommandSystem getCommand(String commandName) {
    return registeredCommands.get(commandName);
  }

  /**
   * 檢查指令是否存在
   * 
   * @param commandName 指令名稱
   * @return true 如果存在
   */
  public static boolean hasCommand(String commandName) {
    return registeredCommands.containsKey(commandName);
  }

  /**
   * 取得所有註冊的指令名稱
   * 
   * @return 指令名稱列表
   */
  public static List<String> getCommandNames() {
    return new ArrayList<>(registeredCommands.keySet());
  }

  /**
   * 取得所有註冊的指令實例
   * 
   * @return 指令實例集合
   */
  public static Collection<ImainCommandSystem> getAllCommands() {
    return registeredCommands.values();
  }

  /**
   * 清除所有已註冊的指令（用於重載）
   */
  public static void clearAll() {
    // 在清除指令前先呼叫 onDisable 以確保資源釋放
    for (ImainCommandSystem command : registeredCommands.values()) {
      command.onDisable();
    }
    registeredCommands.clear();
  }

  /**
   * 移除特定指令
   * 
   * @param commandName 指令名稱
   * @return true 如果成功移除
   */
  public static boolean unregisterCommand(String commandName) {
    return registeredCommands.remove(commandName) != null;
  }
}