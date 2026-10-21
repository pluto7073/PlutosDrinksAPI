package ml.pluto7073.pdapi.util;

import ml.pluto7073.pdapi.addition.DrinkAddition;
import ml.pluto7073.pdapi.item.PDItems;
import ml.pluto7073.pdapi.specialty.SpecialtyDrink;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class ColorUtil {

    public static int subtractiveMix(Collection<Integer> colors) {
        double key = 0;
        double cyan = 0;
        double magenta = 0;
        double yellow = 0;
        int count = 0;
        for (CMYK cmyk : colors.stream().map(CMYK::fromRGBInt).toList()) {
            key += cmyk.key;
            cyan += cmyk.cyan;
            magenta += cmyk.magenta;
            yellow += cmyk.yellow;
            count++;
        }
        return new CMYK(key / count, cyan / count, magenta / count, yellow / count).toRGB();
    }

    @Deprecated
    public static int averageColors(Collection<Integer> colors) {
        if (colors.isEmpty()) return 0xFFFFFF;
        int r = 0;
        int g = 0;
        int b = 0;
        for (int color : colors) {
            r += (color >> 16 & 255);
            g += (color >> 8 & 255);
            b += (color & 255);
        }
        r /= colors.size();
        g /= colors.size();
        b /= colors.size();
        return r << 16 | g << 8 | b;
    }

    /**
     * Computes the color of a drink subtractively based on the additions and base color of the drink
     * @param drink The stack to compute
     * @param base The starting color of the drink
     * @param multiplicity How much influence the starting color has on the overall result. Effectively
     *                     how many times is the base color counted before the addition colors
     * @param level The current level
     * @return An RGB int with the resulting color
     */
    public static int getColorForDrinkWithDefault(ItemStack drink, int base, int multiplicity, Level level) {
        DrinkAddition[] additions = DrinkUtil.getAdditionsFromStack(drink, level);
        List<Integer> colors = Arrays.stream(additions).filter(DrinkAddition::changesColor)
                .map(DrinkAddition::getColor).collect(Collectors.toCollection(ArrayList::new));
        for (int i = 0; i < multiplicity; i++) {
            colors.addFirst(base);
        }
        return subtractiveMix(colors);
    }

    public static int getDrinkColor(ItemStack stack, Level level) {
        if (!stack.is(PDItems.SPECIALTY_DRINK)) return -1;
        try {
            SpecialtyDrink drink = DrinkUtil.getSpecialDrink(stack, level);
            return drink.color();
        } catch (IllegalArgumentException e) {
            return 0xf918c5;
        }
    }

    public record CMYK(double key, double cyan, double magenta, double yellow) {

        public static CMYK fromRGBInt(int color) {
            double red = (color >> 16 & 255) / 255.0;
            double green = (color >> 8 & 255) / 255.0;
            double blue = (color & 255) / 255.0;
            double key = 1 - Math.max(red, Math.max(green, blue));
            double cyan = (1 - red - key) / (1 - key);
            double magenta = (1 - green - key) / (1 - key);
            double yellow = (1 - blue - key) / (1 - key);
            return new CMYK(key, cyan, magenta, yellow);
        }

        public static CMYK mix(CMYK first, CMYK second) {
            double key = (first.key + second.key) / 2.0;
            double cyan = (first.cyan + second.cyan) / 2.0;
            double magenta = (first.magenta + second.magenta) / 2.0;
            double yellow = (first.yellow + second.yellow) / 2.0;
            return new CMYK(key, cyan, magenta, yellow);
        }

        public int toRGB() {
            int red = (int) (255 * (1 - cyan) * (1 - key));
            int green = (int) (255 * (1 - magenta) * (1 - key));
            int blue = (int) (255 * (1 - yellow) * (1 - key));
            return red << 16 | green << 8 | blue;
        }

    }

}
