package org.tsob.MCLang.Compat;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class PotionCompat_v1_17 implements IPotionCompat {
    @Override
    public String getPotionPath(ItemStack item) {
        if (!(item.getItemMeta() instanceof PotionMeta)) return null;
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        
        // 1.20.4 以前的版本使用 getBasePotionData()
        PotionType potionType = potionMeta.getBasePotionData().getType();
        
        String prefix = "item.minecraft.potion.effect.";
        if (item.getType() == Material.LINGERING_POTION) prefix = "item.minecraft.lingering_potion.effect.";
        if (item.getType() == Material.SPLASH_POTION) prefix = "item.minecraft.splash_potion.effect.";
        if (item.getType() == Material.TIPPED_ARROW) prefix = "item.minecraft.tipped_arrow.effect.";
        
        return prefix + potionType.name().toLowerCase();
    }
}
