package org.tsob.MCLang.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tsob.MCLang.DataBase.DataBase;

public class Commandpath extends mainCommandSystem{
  public Commandpath() {
    super(  "path",
        "/mclang path <path> 取得物品的路徑",
        new ArrayList<String>(Arrays.asList("mobdrop.admin.path")));
  }
  
  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args) {
    String path = "test";
    if (args.length >= 1) {
      path = args[0];
    }
    DataBase.Print(DataBase.fileMinecraftLang.getString(path));
  }
  
  @Override
  public List<String> tabComplete(CommandSender sender, String commandLabel, Command command, String[] args, final ClassLoader classLoader, final String commandPath) {
    if (args.length == 1) {
      List<String> Paths = new ArrayList<String>();
      for (String path : DataBase.fileMinecraftLang.getAllPaths()) {
        // if (path.toLowerCase().contains(args[0].toLowerCase())) {
        if (path.toLowerCase().startsWith(args[0].toLowerCase())) {
          Paths.add(path);
        }
      }
      return Paths;
    }
    return Collections.emptyList();
  }
}
