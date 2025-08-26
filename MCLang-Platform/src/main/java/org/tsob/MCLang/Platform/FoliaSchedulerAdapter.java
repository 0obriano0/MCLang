package org.tsob.MCLang.Platform;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

/**
 * Folia 調度：使用 GlobalRegionScheduler / RegionScheduler / Entity.scheduler。
 * 注意：沒有 runTaskTimer 同型，需要自行重複排程或改為 async + 單次遞迴。
 */
public class FoliaSchedulerAdapter implements SchedulerAdapter {

    @Override
    public void runGlobalTask(Plugin plugin, Runnable task) {
        Bukkit.getGlobalRegionScheduler().run(plugin, (ignored) -> task.run());
    }

    @Override
    public void runGlobalDelayed(Plugin plugin, Runnable task, long delayTicks) {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (ignored) -> task.run(), delayTicks);
    }

    @Override
    public void runAsync(Plugin plugin, Runnable task) {
        // Folia async：可用 AsyncScheduler
        Bukkit.getAsyncScheduler().runNow(plugin, (ignored) -> task.run());
    }

    @Override
    public void runAsyncRepeating(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        // 自行模擬：第一次延遲 delayTicks，再遞迴 periodTicks
        Bukkit.getAsyncScheduler().runDelayed(plugin, scheduledTask -> {
            try {
                task.run();
            } finally {
                // 再排下一次
                runAsyncRepeating(plugin, task, periodTicks, periodTicks);
            }
        }, delayTicks, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runRegionTask(Plugin plugin, Location location, Runnable task) {
        Bukkit.getRegionScheduler().run(plugin, location, (ignored) -> task.run());
    }

    @Override
    public void runRegionDelayed(Plugin plugin, Location location, Runnable task, long delayTicks) {
        Bukkit.getRegionScheduler().runDelayed(plugin, location, (ignored) -> task.run(), delayTicks);
    }

    @Override
    public void runEntityTask(Plugin plugin, Entity entity, Runnable task) {
        entity.getScheduler().run(plugin, (ignored) -> task.run(), null);
    }

    @Override
    public void runEntityDelayed(Plugin plugin, Entity entity, Runnable task, long delayTicks) {
        entity.getScheduler().runDelayed(plugin, (ignored) -> task.run(), null, delayTicks);
    }
}