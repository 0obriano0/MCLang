package org.tsob.MCLang.Command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.tsob.MCLang.API.MCLang;
public class Commandapitest extends mainCommandSystem{
  public Commandapitest() {
    super(  "apitest",
        "/mclang apitest <語言> <模式>",
        new ArrayList<String>(Arrays.asList("mclang.admin.apitest")));
  }
  
  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args){
    String mode = "";
    String lang = "zh_tw"; // 預設語言
    if (args.length >= 2) {
      lang = args[0];
      mode = args[1];
    } else {
      sender.sendMessage("用法: /mclang apitest <語言> <模式>");
      sender.sendMessage("模式: item 或 entity");
      return;
    }

    String translation = "";

    MCLang mclang = new MCLang(lang);
    
    if (mode.equals("item")) {
      ItemStack item = new ItemStack(Material.STONE_PICKAXE);
      translation = mclang.getItemTranslate(item);
    } else if (mode.equals("entity")) {
      translation = mclang.getEntityTranslate(EntityType.CREEPER);
    } else {
      translation = "未知模式";
    }

    sender.sendMessage("翻譯結果: " + translation);
  }
}
  
