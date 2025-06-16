package org.tsob.MCLangExample;

public class AnsiColor {
  //public static final String  = "\u001B[m";設定顏色
  public static final String RESET = "\u001B[0m";
  public static final String BLACK = "\u001B[30m";
  public static final String RED = "\u001B[31m";
  public static final String GREEN = "\u001B[32m";
  public static final String YELLOW = "\u001B[33m";
  public static final String BLUE = "\u001B[34m";
  public static final String PURPLE = "\u001B[35m";
  public static final String CYAN = "\u001B[36m";
  public static final String WHITE = "\u001B[37m";

  public static final String DARK_GRAY = "\u001B[90m";

  public static final String LIGHT_BLUE = "\u001B[94m";
  public static final String LIGHT_GREEN = "\u001B[92m";
  public static final String LIGHT_CYAN = "\u001B[96m";
  public static final String LIGHT_RED = "\u001B[91m";
  public static final String LIGHT_PURPLE = "\u001B[95m";
  public static final String LIGHT_YELLOW = "\u001B[93m";
  public static final String BOLD = "\u001B[1m";
  public static final String UNDERLINE = "\u001B[4m";

  public static String minecraftToAnsiColor(String str) {
    return str.replace("§0", BLACK)
              .replace("§1", BLUE)
              .replace("§2", GREEN)
              .replace("§3", CYAN)
              .replace("§4", RED)
              .replace("§5", PURPLE)
              .replace("§6", YELLOW)
              .replace("§7", BLACK)
              .replace("§8", BLACK)
              .replace("§9", BLUE)
              .replace("§a", LIGHT_GREEN)
              .replace("§b", LIGHT_CYAN)
              .replace("§c", LIGHT_RED)
              .replace("§d", LIGHT_PURPLE)
              .replace("§e", LIGHT_YELLOW)
              .replace("§f", RESET);
  }
}
