package org.tsob.MCLang.Platform;

/**
 * 懶漢式偵測是否為 Folia。
 */
public final class SchedulerFactory {
    private static SchedulerAdapter ADAPTER;
    private static Boolean FOLIA;

    private SchedulerFactory() {}

    public static SchedulerAdapter get() {
        if (ADAPTER != null) return ADAPTER;
        if (isFolia()) {
            ADAPTER = new FoliaSchedulerAdapter();
        } else {
            ADAPTER = new SpigotSchedulerAdapter();
        }
        return ADAPTER;
    }

    public static boolean isFolia() {
        if (FOLIA != null) return FOLIA;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            FOLIA = true;
        } catch (ClassNotFoundException e) {
            FOLIA = false;
        }
        return FOLIA;
    }
}