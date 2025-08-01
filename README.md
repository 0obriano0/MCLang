# MCLang 

### Github Status
[![MCLang Build Status](https://img.shields.io/github/actions/workflow/status/0obriano0/MCLang/build.yml)](https://github.com/0obriano0/MCLang/actions) [![GitHub Release](https://img.shields.io/github/v/release/0obriano0/MCLang)](https://github.com/0obriano0/MCLang/releases)

### SpigotMC Status
[![Spiget Version](https://img.shields.io/spiget/version/125883?color=yellow)](https://www.spigotmc.org/resources/mclang.125883/) [![Spiget Downloads](https://img.shields.io/spiget/downloads/125883?color=yellow)](https://www.spigotmc.org/resources/mclang.125883/)

### bStats Status
[![bStats Servers](https://img.shields.io/bstats/servers/26149.svg?color=green&label=OnlineServers&style=plastic)](https://bstats.org/plugin/bukkit/MCLang) [![bStats Players](https://img.shields.io/bstats/players/26149.svg?color=green&label=OnlinePlayers&style=plastic)](https://bstats.org/plugin/bukkit/MCLang)

Official Language Pack API for your Minecraft Bukkit plugins.

*Tested Minecraft versions: 1.13, 1.14, 1.15, 1.16, 1.17, 1.18, 1.19, 1.20, 1.21*

## Features
* Easy-to-use language pack management for Bukkit plugins
* Language packs for different Minecraft versions can be downloaded from [GitHub](https://github.com/0obriano0/MCLang/tree/main/Local%20Language%20Pack)
* Automatically downloads language packs from the official Minecraft resources if not present
* Hot-reload language files without server restart
* API for developers to integrate custom messages

## Docs [JDOC](https://0obriano0.github.io/MCLang/)

### Gradle
```gradle
repositories {
  mavenCentral()
  maven {
    url 'https://repo1.maven.org/maven2/'
  }
}

dependencies {
  // Please check the latest version
  compileOnly group: 'org.tsob', name: 'MCLang-API', version: '1.1.2'
}
```

### Maven
```xml
<repositories>
  <repository>
    <id>maven</id>
    <url>https://repo1.maven.org/maven2/</url>
  </repository>
</repositories>

<dependency>
  <groupId>org.tsob</groupId>
  <artifactId>MCLang-API</artifactId>
  <version>1.1.2</version>
  <scope>provided</scope>
</dependency>
```
### Full Example: [link](https://github.com/0obriano0/MCLang-example)
### Basic Example: 
```java
import org.tsob.MCLang.API.MCLang;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ExamplePlugin extends JavaPlugin {
  @Override
  public void onEnable() {
    MCLang lang = new MCLang("en");

    // Get translation for an ItemStack
    ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
    String itemTranslation = lang.getItemTranslate(item);

    // Directly read the translation from the language pack.
    String translation = getString("item.minecraft.diamond_sword");

    // Get translation for an item name
    String itemTranslationByName = lang.getItemTranslate("DIAMOND_SWORD");

    // Get translation for an EntityType
    String entityTranslation = lang.getEntityTranslate(EntityType.CREEPER);

    // Get translation by entity instance (version 1.1.0 add)
    Entity entity = ...; // your Entity instance
    String entityTranslationFromEntity = lang.getEntityTranslate(entity);

    // Get translation for an entity name
    String entityTranslationByName = lang.getEntityTranslate("CREEPER");

    // Get translation for an enchantment (version 1.0.8 add)
    String enchantmentTranslationByName = lang.getEnchantmentTranslate(Enchantment.SILK_TOUCH);

    // Get translation for an enchantment name (version 1.0.8 add)
    String enchantmentTranslationByName = lang.getEnchantmentTranslate("SILK_TOUCH");
  }
}
```

*[View updates and changelog here.](https://github.com/0obriano0/MCLang/tree/main/update.md)*

![bstats](https://bstats.org/signatures/bukkit/MCLang.svg)

## TODO
* Improve API documentation
* Add more usage examples

