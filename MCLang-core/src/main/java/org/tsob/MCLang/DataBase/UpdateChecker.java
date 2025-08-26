package org.tsob.MCLang.DataBase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.tsob.MCLang.Platform.SchedulerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class UpdateChecker {
  private static final String API_URL = "https://api.spiget.org/v2/resources/125883/versions/latest";
  private final JavaPlugin plugin;
  private String latestVersion = null;
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final HttpClient httpClient;

  public UpdateChecker(JavaPlugin plugin) {
    this.plugin = plugin;
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();
  }

  public void start() {
    SchedulerFactory.get().runAsyncRepeating(plugin, this::checkForUpdate, 0L, 432000L);
    Bukkit.getPluginManager().registerEvents(new Listener() {
      @EventHandler
      public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("mclang.admin.update.notify") && latestVersion != null && isUpdateAvailable()) {
          SchedulerFactory.get().runEntityTask(plugin, player, () ->
            player.sendMessage("§fA new version of §bMCLang §fis available: §a" + latestVersion + "§f. Please update on SpigotMC!"));
        }
      }
    }, plugin);
  }

  private void checkForUpdate() {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(API_URL))
          .header("User-Agent", "Mozilla/5.0")
          .timeout(Duration.ofSeconds(5))
          .GET()
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      
      String responseBody = response.body();

      JsonNode json = objectMapper.readTree(responseBody);
      latestVersion = json.get("name").asText();
      
      if (isUpdateAvailable()) {
        notifyAdmins();
      }
    } catch (Exception e) {
      plugin.getLogger().warning("[UpdateChecker] Could not check for updates: " + e.getMessage());
    }
  }

  private boolean isUpdateAvailable() {
    String currentVersion = plugin.getDescription().getVersion();
    if (latestVersion == null || currentVersion == null) return false;
    // Compare version numbers (assume format: x[.y[.z]])
    String[] currentParts = currentVersion.split("\\.");
    String[] latestParts = latestVersion.split("\\.");
    int length = Math.max(currentParts.length, latestParts.length);
    for (int i = 0; i < length; i++) {
      int current = i < currentParts.length ? parseIntSafe(currentParts[i]) : 0;
      int latest = i < latestParts.length ? parseIntSafe(latestParts[i]) : 0;
      if (current < latest) return true;
      if (current > latest) return false;
    }
    return false;
  }

  private int parseIntSafe(String s) {
    try {
      return Integer.parseInt(s.replaceAll("[^0-9]", ""));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private void notifyAdmins() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.hasPermission("mclang.admin.update.notify")) {
        SchedulerFactory.get().runEntityTask(plugin, player, () ->
          player.sendMessage("§fA new version of §bMCLang §fis available: §a" + latestVersion + "§f. Please update on SpigotMC!"));
      }
    }

    // Also notify in the console
    DataBase.Print("§eA new version of §bMCLang §eis available: §a" + latestVersion + "§e. Please update on SpigotMC!");
  }
}
