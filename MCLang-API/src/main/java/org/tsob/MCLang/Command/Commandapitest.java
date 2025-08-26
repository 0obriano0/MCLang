package org.tsob.MCLang.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tsob.MCLang.Main;
import org.tsob.MCLang.API.MCLang;
import org.tsob.MCLang.DataBase.DataBase;
import org.tsob.MCLang.Platform.SchedulerFactory;
public class Commandapitest extends mainCommandSystem{
  public Commandapitest() {
    super(  "apitest",
        DataBase.fileMessage.getString("Command.Help.apitest"),
        new ArrayList<String>(Arrays.asList("mclang.admin.apitest")));
  }
  
  @Override
  public void run(CommandSender sender, String commandLabel, Command command, String[] args){
    List<String> results = _run(args);
    for (String msg : results) {
      DataBase.Print(msg);
    }
  }


  @Override
  public void run(Player player, String commandLabel, Command command, String[] args) throws Exception {
    List<String> results = _run(args);
    for (String msg : results) {
      DataBase.sendMessage(player, msg);
    }
  }

  private List<String> _run(String[] args) {
    List<String> results = new ArrayList<>();
    String mode = "";
    String lang = "zh_tw"; // 預設語言
    if (args.length >= 2) {
      lang = args[0];
      mode = args[1];
    } else {
      results.add("用法: /mclang apitest <語言> <模式>");
      results.add("模式: item 或 entity");
      return results;
    }

    String translation = "";

    MCLang mclang = new MCLang(lang);
    
    if (mode.equals("item")) {
      ItemStack item = new ItemStack(Material.STONE_PICKAXE);
      translation = mclang.getItemTranslate(item);
    } else if (mode.equals("entity")) {
      translation = mclang.getEntityTranslate(EntityType.CREEPER);
    } else if (mode.equals("enchantment")) {
      translation = mclang.getEnchantmentTranslate("SILK_TOUCH");
    } else if (mode.equals("enchantment2")) {
      translation = mclang.getEnchantmentTranslate(Enchantment.SILK_TOUCH);
    } else {
      translation = "未知模式";
    }

    results.add("翻譯結果: " + translation);
    return results;
  }
}
  
