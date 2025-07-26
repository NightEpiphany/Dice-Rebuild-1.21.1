package com.moigferdsrte.util;

public class ColorUtils {

    // RGB转HSL
    public static float[] rgbToHsl(float r, float g, float b) {
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float h, s, l;
        l = (max + min) / 2.0f;

        if (max == min) {
            h = s = 0.0f; // 灰色，没有色调
        } else {
            float delta = max - min;
            s = l > 0.5f ? delta / (2.0f - max - min) : delta / (max + min);

            if (max == r) {
                h = (g - b) / delta + (g < b ? 6.0f : 0.0f);
            } else if (max == g) {
                h = (b - r) / delta + 2.0f;
            } else {
                h = (r - g) / delta + 4.0f;
            }
            h /= 6.0f;
        }
        return new float[] {h, s, l};
    }

    // HSL转RGB
    public static float[] hslToRgb(float h, float s, float l) {
        float r, g, b;

        if (s == 0) {
            r = g = b = l; // 灰色
        } else {
            float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1.0f/3.0f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0f/3.0f);
        }
        return new float[] {r, g, b};
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0f/6.0f) return p + (q - p) * 6.0f * t;
        if (t < 1.0f/2.0f) return q;
        if (t < 2.0f/3.0f) return p + (q - p) * (2.0f/3.0f - t) * 6.0f;
        return p;
    }

    public static float[] brightenColor(float r, float g, float b, float factor) {
        float[] hsl = rgbToHsl(r, g, b);
        // 增加亮度，但不超过1.0
        hsl[2] = Math.min(hsl[2] * factor, 1.0f);
        return hslToRgb(hsl[0], hsl[1], hsl[2]);
    }

}
