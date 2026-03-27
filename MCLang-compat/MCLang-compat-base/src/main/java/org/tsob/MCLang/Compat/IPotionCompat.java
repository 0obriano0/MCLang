package org.tsob.MCLang.Compat;

import org.bukkit.inventory.ItemStack;

public interface IPotionCompat {
    /**
     * 從 ItemStack 中獲取藥水翻譯路徑
     * @param item 物品
     * @return 翻譯路徑 (例如 item.minecraft.potion.effect.healing)
     */
    String getPotionPath(ItemStack item);
}
