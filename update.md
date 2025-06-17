## 1.1.0
* Added: Translation for `/mclang help` explanations.
  * If you cannot see the translation, please delete `.\message\XX.json` and a new file will be generated automatically.
* Improved: `String getItemTranslate(ItemStack item)`
  * Now accurately detects potions, splash potions, lingering potions, tipped arrows, and music discs.
* Added: `String getEntityTranslate(Entity entity)`
  * Now correctly detects villager professions.
* Add [Example](https://github.com/0obriano0/MCLang-example) 
* Move the file download to asynchronous execution, and temporarily use en_us until the download is complete.

## 1.0.8
* Change: Each version now corresponds to its own local language pack. Please download the appropriate pack from GitHub. Language packs are no longer bundled with the plugin.
* Added: Easy translation retrieval methods.
  * `String getEnchantmentTranslate(String enchantmentName)` – Retrieves the translated name of an enchantment.
  * `String getEnchantmentTranslate(Enchantment enchantment)` – Retrieves the translated name of an enchantment.

## 1.0.7
* Added: bStats integration.
* Refactored: Maven `pom.xml`.

## 1.0.6
* Changed: MCLang system language setting. If the specified language is not found, defaults to 'en'.
* Updated: Maven repositories.

## 1.0.5
* Changed: Default language.
* Added: Lego.

## 1.0.4
* Added: Easy translation retrieval methods.
  * `String getItemTranslate(String itemName)` – Retrieves the translated name of an item name.
  * `String getEntityTranslate(String entityName)` – Retrieves the translated name of an entity name.

## 1.0.3
* Added: Easy translation retrieval methods.
  * `String getItemTranslate(ItemStack item)` – Retrieves the translated name of an item.
  * `String getEntityTranslate(EntityType entityType)` – Retrieves the translated name of an entity.