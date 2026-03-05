# MCLang 

### Github Status
[![MCLang Build Status](https://img.shields.io/github/actions/workflow/status/0obriano0/MCLang/build.yml)](https://github.com/0obriano0/MCLang/actions) [![GitHub Release](https://img.shields.io/github/v/release/0obriano0/MCLang)](https://github.com/0obriano0/MCLang/releases)

### SpigotMC Status
[![Spiget Version](https://img.shields.io/spiget/version/125883?color=yellow)](https://www.spigotmc.org/resources/mclang.125883/) [![Spiget Downloads](https://img.shields.io/spiget/downloads/125883?color=yellow)](https://www.spigotmc.org/resources/mclang.125883/)

### bStats Status
[![bStats Servers](https://img.shields.io/bstats/servers/26149.svg?color=green&label=OnlineServers&style=plastic)](https://bstats.org/plugin/bukkit/MCLang) [![bStats Players](https://img.shields.io/bstats/players/26149.svg?color=green&label=OnlinePlayers&style=plastic)](https://bstats.org/plugin/bukkit/MCLang)

Lightweight API to load and use official Minecraft language pack translations.

*Tested Minecraft versions: 1.13, 1.14, 1.15, 1.16, 1.17, 1.18, 1.19, 1.20, 1.21*
*Folia 1.19.4+ support (version 1.1.3 add)

## Features
* Easy-to-use language pack management for Bukkit plugins
* Language packs for different Minecraft versions can be downloaded from [GitHub](https://github.com/0obriano0/MCLang/tree/main/Local%20Language%20Pack)
* Automatically downloads language packs from the official Minecraft resources if not present
* Hot-reload language files without server restart
* API for developers to integrate custom messages
* Optional Web API + built-in web page to inspect API docs and language-pack keys/values

## Docs [JDOC](https://0obriano0.github.io/MCLang/)

## Web API (for admin / frontend developers)
Enable in `/plugins/MCLang/config.yml`:
```yml
web:
  enabled: true
  host: 127.0.0.1
  port: 8765
  cors: true
  maxEntriesPerRequest: 300
```

Then open:
* `http://127.0.0.1:8765/` → built-in web page (API docs + language pack viewer)
* `GET /api/docs` → API usage
* `GET /api/languages` → available language codes + versions
* `GET /api/languages/{lang}` → language pack keys/values (supports `version`, `prefix`, `limit`, `offset`)
* `GET /api/translate?lang=zh_tw&key=item.minecraft.diamond_sword`
* `POST /api/translate` with JSON body: `{"lang":"zh_tw","key":"item.minecraft.diamond_sword"}`

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
  compileOnly group: 'org.tsob', name: 'MCLang-API', version: '1.1.3'
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
  <version>1.1.3</version>
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

## Project Growth Suggestions (to increase adoption)
If you want this project to create more value and reach more users, these are the highest-impact next steps:

1. **Publish complete copy-paste examples**
   * Add 3 small example plugins (Item / Entity / Enchantment translation) under a single repo or folder.
   * Include "expected output" screenshots/GIFs so plugin authors can verify behavior quickly.
2. **Lower first-use friction**
   * Add a "Quick Start in 5 minutes" section (dependencies + one command + one API call).
   * Keep version numbers in README aligned with latest release to avoid setup failures.
3. **Improve discoverability**
   * Add bilingual docs (English + Traditional Chinese) for README and command usage.
   * Add keywords/topics on GitHub and keep SpigotMC page examples synced with README.
4. **Encourage community contributions**
   * Label starter tasks as `good first issue` and document contribution steps.
   * Prioritize requests for new language packs and popular plugin integration examples.
5. **Track what users actually need**
   * Use bStats + GitHub issues to decide roadmap priorities (most requested API helpers first).
   * Keep changelog focused on practical plugin-developer outcomes, not only internal refactors.

### 中文建議（提升專案價值與使用人數）
1. **先補齊可直接複製的範例**：提供最小可運作範例，並附上預期輸出結果。  
2. **降低新手門檻**：加入 5 分鐘快速開始，確保 README 版本號與最新 release 一致。  
3. **提升曝光**：維護中英文文件、更新 GitHub Topics、同步 SpigotMC 頁面內容。  
4. **強化社群參與**：建立 `good first issue` 工作流，明確寫出貢獻步驟。  
5. **用數據排優先級**：結合 bStats 與 issue 回饋，先做最多人需要的 API 能力。  
