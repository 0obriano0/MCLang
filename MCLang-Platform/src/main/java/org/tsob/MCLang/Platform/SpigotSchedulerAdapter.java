package org.tsob.MCLang.Platform;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * 傳統 (Spigot/Paper) 調度：全部回主執行緒 (同步) / 已有 async API。
 */
public class SpigotSchedulerAdapter implements SchedulerAdapter {

    @SuppressWarnings("deprecation")
    @Override
    public void runGlobalTask(Plugin plugin, Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void runGlobalDelayed(Plugin plugin, Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void runAsync(Plugin plugin, Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void runAsyncRepeating(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
    }

    @Override
    public void runRegionTask(Plugin plugin, Location location, Runnable task) {
        // Spigot 無 Region 概念，直接主執行緒
        runGlobalTask(plugin, task);
    }

    @Override
    public void runRegionDelayed(Plugin plugin, Location location, Runnable task, long delayTicks) {
        runGlobalDelayed(plugin, task, delayTicks);
    }

    @Override
    public void runEntityTask(Plugin plugin, Entity entity, Runnable task) {
        runGlobalTask(plugin, task);
    }

    @Override
    public void runEntityDelayed(Plugin plugin, Entity entity, Runnable task, long delayTicks) {
        runGlobalDelayed(plugin, task, delayTicks);
    }
}