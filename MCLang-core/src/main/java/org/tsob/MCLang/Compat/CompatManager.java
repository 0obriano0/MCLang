package org.tsob.MCLang.Compat;

import org.bukkit.Bukkit;
import org.tsob.MCLang.DataBase.DataBase;

public class CompatManager {
    private static IPotionCompat potionCompat;

    public static void init() {
        String version = Bukkit.getBukkitVersion();
        
        try {
            if (isVersionAtLeast(version, 1, 20, 5)) {
                potionCompat = (IPotionCompat) Class.forName("org.tsob.MCLang.Compat.PotionCompat_v1_21").getDeclaredConstructor().newInstance();
                DataBase.Print("Loaded PotionCompat for 1.20.5+");
            } else {
                potionCompat = (IPotionCompat) Class.forName("org.tsob.MCLang.Compat.PotionCompat_v1_17").getDeclaredConstructor().newInstance();
                DataBase.Print("Loaded PotionCompat for 1.17-1.20.4");
            }
        } catch (Exception e) {
            DataBase.Print("Failed to load PotionCompat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static IPotionCompat getPotionCompat() {
        return potionCompat;
    }

    private static boolean isVersionAtLeast(String bukkitVersion, int major, int minor, int patch) {
        try {
            String versionStr = bukkitVersion.split("-")[0];
            String[] parts = versionStr.split("\\.");
            int currentMajor = Integer.parseInt(parts[0]);
            int currentMinor = Integer.parseInt(parts[1]);
            int currentPatch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

            if (currentMajor > major) return true;
            if (currentMajor < major) return false;
            
            if (currentMinor > minor) return true;
            if (currentMinor < minor) return false;
            
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
