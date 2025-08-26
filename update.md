## 1.1.3
* Declared Jackson Databind dependency in plugin.yml instead of shading.

## 1.1.2
* Fixed the logic for determining the latest version.
* Replaced HttpURLConnection with Java 11's HttpClient API
* This plugin requires Java 11 or higher

## 1.1.1
* Regularly check for updates to MCLang on SpigotMC.

## 1.1.0
* Added translation for the `Download language pack` feature.
* Added: Translation for `/mclang help` explanations.
  * If you cannot see the translation, please delete `.\message\XX.json` and a new file will be generated automatically.
* Improved: `String getItemTranslate(ItemStack item)`
  * Now accurately detects potions, splash potions, lingering potions, tipped arrows, and music discs.
* Added: `String getEntityTranslate(Entity entity)`
  * Now correctly detects villager professions.
* Add [Example](https://github.com/0obriano0/MCLang-example) 
* Move the file download to asynchronous execution, and temporarily use en_us until the download is complete.
* test in 1.21.6

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