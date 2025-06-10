package org.tsob.MCLang.FileIO;

import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.DataBase.DataBase;

public class FileMessage extends FileIO{
  public FileMessage() {
    super("message", "en.yml");
  }
  
  @Override
  public boolean reloadcmd() {
    try {
      this.setFileName(tools.getLang() + ".yml");
    } catch (IllegalArgumentException e) {
      printCmd("§cError: File not found: §e%fileName%, §cusing default file: §e%defaultFileName%"
        .replace("%fileName%", "message/"+ tools.getLang() + ".yml")
        .replace("%defaultFileName%", "message/en.yml"));
      this.setFileName("en.yml");
    }
    return true;
  }

  private static void printCmd(String msg) {
    String title = AnsiColor.minecraftToAnsiColor("§b[LoadMessage] ");
    msg = title + AnsiColor.WHITE + AnsiColor.minecraftToAnsiColor(msg);
    DataBase.Print(msg);
  }
  
}
