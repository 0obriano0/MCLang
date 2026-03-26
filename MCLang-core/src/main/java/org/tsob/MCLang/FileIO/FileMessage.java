package org.tsob.MCLang.FileIO;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.Main;
import org.tsob.MCLang.DataBase.DataBase;

public class FileMessage extends FileIO{
  public FileMessage() {
    super("message", "en.yml");
  }
  
  @Override
  public boolean reloadcmd() {
    String lang = tools.getLang();
    try {
      this.setFileName(lang + ".yml");
    } catch (IllegalArgumentException e) {
      printCmd("§cError: File not found: §e%fileName%, §cusing default file: §e%defaultFileName%"
        .replace("%fileName%", "message/"+ lang + ".yml")
        .replace("%defaultFileName%", "message/en.yml"));
      this.setFileName("en.yml");
      lang = "en";
    }

    // Dynamic fallback logic
    try {
      InputStream in = Main.plugin.getResource("message/" + lang + ".yml");
      if (in == null) {
        in = Main.plugin.getResource("message/en.yml");
      }
      
      if (in != null) {
        this.setDefaultData(YamlConfiguration.loadConfiguration(new InputStreamReader(in)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return true;
  }

  private static void printCmd(String msg) {
    String title = AnsiColor.minecraftToAnsiColor("§b[LoadMessage] ");
    msg = title + AnsiColor.WHITE + AnsiColor.minecraftToAnsiColor(msg);
    DataBase.Print(msg);
  }
  
}
