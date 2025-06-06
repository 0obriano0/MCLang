package org.tsob.MCLang.Command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tsob.MCLang.DataBase.DataBase;
public class Commandtest extends mainCommandSystem{
  public Commandtest() {
    super(  "test",
        "/mclang test 取得指令說明",
        new ArrayList<String>(Arrays.asList("mclang.admin.test")));
  }
  
  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args){
    String path = "test";
    if (args.length >= 1) {
      path = args[0];
    }
    // DataBase.Print(DataBase.fileMinecraftLang.getString(path));
    DataBase.Print(DataBase.fileMinecraftLang.Minecraft_Items.get(path.toLowerCase()));
  }
}
  
