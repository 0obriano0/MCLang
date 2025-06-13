package org.tsob.MCLang.API;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.tsob.MCLang.DataBase.DataBase;
import org.tsob.MCLang.FileIO.JsonFileIOMinecraftLang;

public class MCLang implements IMCLang {
  private String Lang;
  
  /**
   * 建構子
   * @param lang 語言代碼，例如 "en_us"、"zh_tw" 等
   */
  public MCLang(String lang) {
    this.Lang = lang;
  }

  @Override
  public String getLang() {
    return Lang;
  }

  @Override
  public void setLang(String lang) {
    this.Lang = lang;
  }

  /**
   * 取得 Minecraft Lang 的 JsonFileIO
   * @return JsonFileIOMinecraftLang
   */
  private JsonFileIOMinecraftLang getJsonFileIOMinecraftLang() {
    JsonFileIOMinecraftLang _minecraftLangMap = null;
    
    if (DataBase.minecraftLangMap.containsKey(this.Lang)) {
      _minecraftLangMap = DataBase.minecraftLangMap.get(this.Lang);
    } else {
      _minecraftLangMap = new JsonFileIOMinecraftLang(this.Lang);
      DataBase.minecraftLangMap.put(this.Lang, _minecraftLangMap);
    }

    return _minecraftLangMap;
  }

  @Override
  public String getString(String path) {
    return _getString(path, true);
  }

  private String _getString(String path, boolean fullback) {
    JsonFileIOMinecraftLang minecraftLang = getJsonFileIOMinecraftLang();
    String translation = minecraftLang.getString(path);
    
    if (translation == null || translation.isEmpty()) {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return fullback ? path : "";
    }
    
    return translation; 

  }

  @Override
  public String getItemTranslate(ItemStack item) {
    if (item == null || item.getType() == null) {
      return "unknown_item";
    }
    String path = "item.minecraft." + item.getType().name().toLowerCase();
    String translation = "";

    // 特殊道具區
    // 1.13 以上
    //   藥水
    if (item.getType() == Material.POTION) {
      PotionMeta potionMeta = (PotionMeta) item.getItemMeta();  
      PotionType potionType = potionMeta.getBasePotionData().getType();
      path = "item.minecraft.potion.effect." + potionType.name().toLowerCase();
    }

    //   滯留藥水
    if (item.getType() == Material.LINGERING_POTION) {
      PotionMeta potionMeta = (PotionMeta) item.getItemMeta();  
      PotionType potionType = potionMeta.getBasePotionData().getType();
      path = "item.minecraft.lingering_potion.effect." + potionType.name().toLowerCase();
    }

    //   飛濺藥水
    if (item.getType() == Material.SPLASH_POTION) {
      PotionMeta potionMeta = (PotionMeta) item.getItemMeta();  
      PotionType potionType = potionMeta.getBasePotionData().getType();
      path = "item.minecraft.splash_potion.effect." + potionType.name().toLowerCase();
    }

    //   藥水箭
    if (item.getType() == Material.TIPPED_ARROW) {
      PotionMeta potionMeta = (PotionMeta) item.getItemMeta();  
      PotionType potionType = potionMeta.getBasePotionData().getType();
      path = "item.minecraft.tipped_arrow.effect." + potionType.name().toLowerCase();
    }

    //   唱片
    if (item.getType().name().toLowerCase().contains("music_disc_")) {
      path = "item.minecraft." + item.getType().name().toLowerCase() + ".desc";
    }


    translation = _getString(path,false);
    if (translation == null || translation.isEmpty() || translation == "") {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return item.getType().name().toLowerCase();
    }

    return translation;
  }

  @Override
  public String getItemTranslate(String itemName) {
    if (itemName == null || itemName.isEmpty()) {
      return "unknown_item";
    }
    String path = "item.minecraft." + itemName.trim().toLowerCase();
    String translation = "";

    translation = _getString(path,false);
    if (translation == null || translation.isEmpty() || translation == "") {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return itemName.toLowerCase();
    }

    return translation;
  }

  @Override
  public String getEntityTranslate(EntityType entityType) {
    if (entityType == null) {
      return "unknown_entity";
    }
    String path = "entity.minecraft." + entityType.name().toLowerCase();
    String translation = "";

    translation = _getString(path,false);
    if (translation == null || translation.isEmpty() || translation == "") {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return entityType.name().toLowerCase();
    }

    return translation;
  }

  @Override
  public String getEntityTranslate(Entity entity) {
    if (entity == null || entity.getType() == null) {
      return "unknown_entity";
    }
    String path = "entity.minecraft." + entity.getType().name().toLowerCase();
    String translation = "";

    // 特殊實體區
    // 1.13 以上
    //   村民職業
    if (entity instanceof Villager) {
      Villager villager = (Villager) entity;
      path = "entity.minecraft.villager." + villager.getProfession().name().toLowerCase();
    }

    translation = _getString(path,false);
    if (translation == null || translation.isEmpty() || translation == "") {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return entity.getType().name().toLowerCase();
    }

    return translation;
  }

  @Override
  public String getEntityTranslate(String entityName) {
    if (entityName == null || entityName.isEmpty()) {
      return "unknown_entity";
    }
    String path = "entity.minecraft." + entityName.trim().toLowerCase();
    String translation = "";

    translation = _getString(path,false);
    if (translation == null || translation.isEmpty() || translation == "") {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return entityName.toLowerCase();
    }

    return translation;
  }

  @Override
  public String getEnchantmentTranslate(String enchantmentName) {
    if (enchantmentName == null || enchantmentName.isEmpty()) {
      return "unknown_enchantment";
    }
    String path = "enchantment.minecraft." + enchantmentName.trim().toLowerCase();
    String translation = "";

    translation = _getString(path,false);
    if (translation == null || translation.isEmpty() || translation == "") {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return enchantmentName.toLowerCase();
    }

    return translation;
  }

  @Override
  public String getEnchantmentTranslate(Enchantment enchantment) {
    if (enchantment == null) {
      return "unknown_enchantment";
    }
    String path = "enchantment.minecraft." + enchantment.getKey().getKey().trim().toLowerCase();
    String translation = "";

    translation = _getString(path,false);
    if (translation == null || translation.isEmpty() || translation == "") {
      // 如果沒有找到對應的翻譯，返回原始路徑
      return enchantment.getKey().getKey();
    }

    return translation;
  }

  @Override
  public void reload() {
    JsonFileIOMinecraftLang minecraftLang = getJsonFileIOMinecraftLang();
    minecraftLang.reloadWithLangAndVersion();
  }
}