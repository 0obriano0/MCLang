package org.tsob.MCLang.Command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tsob.MCLang.Main;
import org.tsob.MCLang.DataBase.DataBase;

public class Commandreload extends mainCommandSystem{
  public Commandreload() {
    super(  "reload",
        DataBase.fileMessage.getString("Command.Help.reload"),
        new ArrayList<String>(Arrays.asList("mclang.admin.reload")));
  }
  
  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args) throws Exception {
    DataBase.Print(DataBase.fileMessage.getString("Message.Data_Re_read"));
    Main.reload();
    DataBase.Print(DataBase.fileMessage.getString("Message.Data_Re_read_Complete"));
  }
  
  @Override
  public void run(Player player, String commandLabel, Command command, String[] args) throws Exception {
    DataBase.sendMessage(player,DataBase.fileMessage.getString("Message.Data_Re_read"));
    Main.reload();
    DataBase.sendMessage(player,DataBase.fileMessage.getString("Message.Data_Re_read_Complete"));
  }
}
