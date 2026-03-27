package org.tsob.MCLang.Compat;

import org.bukkit.Bukkit;
import org.tsob.MCLang.AnsiColor;
import org.tsob.MCLang.DataBase.DataBase;

public class CompatManager {
  private static IPotionCompat potionCompat;

  public static void init() {
    String version = Bukkit.getBukkitVersion();
    String successMsg = DataBase.fileMessage.getString("Compat.Success").replace("%compat_name%", "PotionCompat");

    try {
      if (isVersionAtLeast(version, 1, 20, 5)) {
        potionCompat = (IPotionCompat) Class.forName("org.tsob.MCLang.Compat.PotionCompat_v1_21")
            .getDeclaredConstructor().newInstance();
        printCmd(successMsg.replace("%version%", "1.20.5+"));
      } else {
        potionCompat = (IPotionCompat) Class.forName("org.tsob.MCLang.Compat.PotionCompat_v1_17")
            .getDeclaredConstructor().newInstance();
        printCmd(successMsg.replace("%version%", "1.17-1.20.4"));
      }
    } catch (Exception e) {
      printCmd(DataBase.fileMessage.getString("Compat.Error")
          .replace("%compat_name%", "PotionCompat")
          .replace("%error%", e.getMessage()));
      e.printStackTrace();
    }
  }

  public static IPotionCompat getPotionCompat() {
    return potionCompat;
  }

  private static void printCmd(String msg) {
    String title = AnsiColor.minecraftToAnsiColor(DataBase.fileMessage.getString("Compat.Title"));
    msg = title + AnsiColor.WHITE + AnsiColor.minecraftToAnsiColor(msg);
    DataBase.Print(msg);
  }

  private static boolean isVersionAtLeast(String bukkitVersion, int major, int minor, int patch) {
    try {
      String versionStr = bukkitVersion.split("-")[0];
      String[] parts = versionStr.split("\\.");
      int currentMajor = Integer.parseInt(parts[0]);
      int currentMinor = Integer.parseInt(parts[1]);
      int currentPatch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

      if (currentMajor > major)
        return true;
      if (currentMajor < major)
        return false;

      if (currentMinor > minor)
        return true;
      if (currentMinor < minor)
        return false;

      return currentPatch >= patch;
    } catch (Exception e) {
      // Handle versions like "26.1"
      try {
        String versionStr = bukkitVersion.split("-")[0];
        int currentVersion = Integer.parseInt(versionStr.split("\\.")[0]);
        return currentVersion >= 26; // 假設 26+ 都是新版
      } catch (Exception e2) {
        return false;
      }
    }
  }
}
