package org.tsob.MCLang.Platform;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * 抽象層：封裝 Folia 與 Spigot/Paper 的差異。
 */
public interface SchedulerAdapter {
    void runGlobalTask(Plugin plugin, Runnable task);
    void runGlobalDelayed(Plugin plugin, Runnable task, long delayTicks);
    void runAsync(Plugin plugin, Runnable task);
    void runAsyncRepeating(Plugin plugin, Runnable task, long delayTicks, long periodTicks);

    void runRegionTask(Plugin plugin, Location location, Runnable task);
    void runRegionDelayed(Plugin plugin, Location location, Runnable task, long delayTicks);

    void runEntityTask(Plugin plugin, Entity entity, Runnable task);
    void runEntityDelayed(Plugin plugin, Entity entity, Runnable task, long delayTicks);
}