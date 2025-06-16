package org.tsob.MCLangExample.Command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tsob.MCLangExample.DataBase.DataBase;
public class Commandtest extends mainCommandSystem{
  public Commandtest() {
    super(  "test",
        DataBase.fileMessage.getString("Command.Help.test"),
        new ArrayList<String>(Arrays.asList("mclang.admin.test")));
  }
  
  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args){
    
  }
}
  
