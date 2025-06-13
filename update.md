## 1.0.8
* Modification method: Each version corresponds to the local language pack. Please download it from my GitHub yourself. It will no longer be packaged together when separating versions during packaging.
* Add a function to easily retrieve translations.
  * String getEnchantmentTranslate(String enchantmentName); – Retrieves the translated name of an enchantment.
  * String getEnchantmentTranslate(Enchantment enchantment); – Retrieves the translated name of an enchantment.

## 1.0.7
* add bstats 
* Refactor Maven's pom.xml

## 1.0.6
* Move the MCLang system language setting, and set 'en' as the default when the specified language cannot be found.
* change maven repositories

## 1.0.5
* change default lang
* add lego

## 1.0.4
* Add a function to easily retrieve translations
  * String getItemTranslate(String itemName); – Retrieves the translated name of an item name.
  * String getEntityTranslate(String entryName); – Retrieves the translated name of an entity name.
  
## 1.0.3
* Add a function to easily retrieve translations
  * String getItemTranslate(ItemStack item); – Retrieves the translated name of an item.
  * String getEntityTranslate(EntityType entry); – Retrieves the translated name of an entity.