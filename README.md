# MCLang 

### Github Status
[![MCLang Build Status](https://img.shields.io/github/actions/workflow/status/0obriano0/MCLang/build.yml)](https://github.com/0obriano0/MCLang/actions) [![GitHub Release](https://img.shields.io/github/v/release/0obriano0/MCLang)](https://github.com/0obriano0/MCLang/releases)

### SpigotMC Status
[![Spiget Version](https://img.shields.io/spiget/version/125883?color=yellow)](https://www.spigotmc.org/resources/mclang.125883/) [![Spiget Downloads](https://img.shields.io/spiget/downloads/125883?color=yellow)](https://www.spigotmc.org/resources/mclang.125883/)

### bStats Status
[![bStats Servers](https://img.shields.io/bstats/servers/26149.svg?color=green&label=OnlineServers&style=plastic)](https://bstats.org/plugin/bukkit/MCLang) [![bStats Players](https://img.shields.io/bstats/players/26149.svg?color=green&label=OnlinePlayers&style=plastic)](https://bstats.org/plugin/bukkit/MCLang)

Lightweight API to load and use official Minecraft language pack translations.

*Tested Minecraft versions: 1.13, 1.14, 1.15, 1.16, 1.17, 1.18, 1.19, 1.20, 1.21, 26.1
*Folia 1.19.4+ support (version 1.1.3 add)

## Features
* Easy-to-use language pack management for Bukkit plugins
* Language packs for different Minecraft versions can be downloaded from [GitHub](https://github.com/0obriano0/MCLang/tree/main/Local%20Language%20Pack)
* Automatically downloads language packs from the official Minecraft resources if not present
* Hot-reload language files without server restart
* API for developers to integrate custom messages
* Optional Web API + built-in web page to inspect API docs and language-pack keys/values

## Docs [JDOC](https://0obriano0.github.io/MCLang/)

## Web API & Frontend Portal (version 1.2.0 add)
MCLang includes a built-in modern Web API and an interactive frontend portal, making it easy to query translations programmatically or test them directly via your browser.

Enable and configure in `plugins/MCLang/config.yml`:
```yml
web:
  enabled: true  # Master switch
  ssl:
    enabled: false
    keystorePath: "keystore.jks"
    keystorePassword: "password"
  api:
    host: 127.0.0.1
    port: 8765
    cors: true
    maxEntriesPerRequest: 300
    requireKey: false       # Set to true to require a Bearer token
    key: "YOUR_SECRET_KEY"  # The token clients must provide
  frontend:
    enabled: true
    host: 127.0.0.1
    port: 8766
    cors: true
    staticDir: web          # Automatically extracted on startup
```

### Accessing the Services
* **Interactive Web Portal:** `http://127.0.0.1:8766/` (or `https://` if SSL is enabled)
  * Features a modern, multi-language (i18n) test playground.
  * Dynamically extracts `HTML/CSS/JS/JSON` frontend files to `plugins/MCLang/web/` on server start.
* **Backend API Base URL:** `http://127.0.0.1:8765`
  * *(Access is denied with `401 Unauthorized` if `requireKey` is enabled and no token is passed)*

### Available API Endpoints
* `GET /api/languages`: Returns a list of all currently loaded language codes and their versions.
* `GET /api/languages/{lang}`: Get keys/values for a specific language.
  * Supported query parameters: `version`, `prefix`, `limit`, `offset`.
* `GET /api/translate`: Query a single translation string.
  * Usage: `?lang=zh_tw&key=item.minecraft.diamond_sword`
* `POST /api/translate`: Query a single translation string via POST.
  * Usage: send JSON payload `{"lang":"zh_tw","key":"item.minecraft.diamond_sword"}`

*Note: You can easily test all these API endpoints directly via the interactive web portal without writing any code!*

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
  compileOnly group: 'org.tsob', name: 'MCLang-API', version: '1.2.0'
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
  <version>1.2.0</version>
  <scope>provided</scope>
</dependency>
```
### Full Example: [link](https://github.com/0obriano0/MCLang-example)

![Example](image/Example.gif)

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