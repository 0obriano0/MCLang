# MCLang [![MCLang Build Status](https://img.shields.io/github/actions/workflow/status/0obriano0/MCLang/build.yml)](https://github.com/0obriano0/MCLang/actions) [![GitHub Release](https://img.shields.io/github/v/release/0obriano0/MCLang)](https://github.com/0obriano0/MCLang/releases)

Official Language Pack API for your Minecraft Bukkit plugins.

*Tested Minecraft versions: 1.13, 1.14, 1.15, 1.16, 1.17, 1.18, 1.19, 1.20, 1.21*

## Features
* Easy-to-use language pack management for Bukkit plugins
* Includes preloaded language packs for each Minecraft version (download from [GitHub](https://github.com/0obriano0/MCLang/releases))
* Automatically downloads language packs from the official Minecraft resources if not present
* Hot-reload language files without server restart
* API for developers to integrate custom messages

## Docs

## Usage

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
  compileOnly group: 'org.tsob', name: 'MCLang-API', version: '1.0.5'
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
  <version>1.0.5</version>
  <scope>provided</scope>
</dependency>
```

### Basic Example
```java
import org.tsob.MCLang.API.MCLang;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ExamplePlugin extends JavaPlugin {
  @Override
  public void onEnable() {
    MCLang lang = new MCLang("en");

    // Get translation for an ItemStack
    ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
    String itemTranslation = lang.getItemTranslate(item);

    // Get translation for an item name
    String itemTranslationByName = lang.getItemTranslate("DIAMOND_SWORD");

    // Get translation for an EntityType
    String entityTranslation = lang.getEntityTranslate (EntityType.CREEPER);

    // Get translation for an entity name
    String entityTranslationByName = lang.getEntityTranslate("CREEPER");
  }
}
```

## TODO
* Improve API documentation
* Add more usage examples
* Move the file download to asynchronous execution, and temporarily use en_us until the download is complete.