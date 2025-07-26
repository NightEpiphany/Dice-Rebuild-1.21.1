package com.moigferdsrte.util;

import net.minecraft.block.MapColor;
import net.minecraft.util.DyeColor;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;

public enum ColorIndexIndicator {
    WHITE(0, "white", 16383998, MapColor.WHITE, 15790320, 16777215),
    ORANGE(1, "orange", 16351261, MapColor.ORANGE, 15435844, 16738335),
    MAGENTA(2, "magenta", 13061821, MapColor.MAGENTA, 12801229, 16711935),
    LIGHT_BLUE(3, "light_blue", 3847130, MapColor.LIGHT_BLUE, 6719955, 10141901),
    YELLOW(4, "yellow", 16701501, MapColor.YELLOW, 14602026, 16776960),
    LIME(5, "lime", 8439583, MapColor.LIME, 4312372, 12582656),
    PINK(6, "pink", 15961002, MapColor.PINK, 14188952, 16738740),
    GRAY(7, "gray", 4673362, MapColor.GRAY, 4408131, 8421504),
    LIGHT_GRAY(8, "light_gray", 10329495, MapColor.LIGHT_GRAY, 11250603, 13882323),
    CYAN(9, "cyan", 1481884, MapColor.CYAN, 2651799, 65535),
    PURPLE(10, "purple", 8991416, MapColor.PURPLE, 8073150, 10494192),
    BLUE(11, "blue", 3949738, MapColor.BLUE, 2437522, 255),
    BROWN(12, "brown", 8606770, MapColor.BROWN, 5320730, 9127187),
    GREEN(13, "green", 6192150, MapColor.GREEN, 3887386, 65280),
    RED(14, "red", 11546150, MapColor.RED, 11743532, 16711680),
    BLACK(15, "black", 1908001, MapColor.BLACK, 1973019, 0);

    private static final IntFunction<ColorIndexIndicator> BY_ID = ValueLists.createIdToValueFunction(ColorIndexIndicator::getId, values(), ValueLists.OutOfBoundsHandling.ZERO);

    private int getId() {
        return id;
    }

    private final int id;
    private final int color;


    ColorIndexIndicator(int id, String name, int color, MapColor mapColor, int fireworkColor, int signColor) {
        this.id = id;
        this.color = color;
        int j = (color & 0xFF0000) >> 16;
        int k = (color & 0xFF00) >> 8;
        int l = (color & 0xFF);
        float[] colorComponents = new float[]{j / 255.0F, k / 255.0F, l / 255.0F};
    }

    public int getColor() {
        return color;
    }

    public static ColorIndexIndicator byId(int id) {
        return BY_ID.apply(id);
    }
}
