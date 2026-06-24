package ml.pluto7073.pdapi.internal;

import ml.pluto7073.pdapi.addition.DrinkAdditionManager;
import ml.pluto7073.pdapi.specialty.SpecialtyDrinkManager;
import org.jetbrains.annotations.ApiStatus;

public interface PDAPILevelExtensions {

    default DrinkAdditionManager getDrinkAdditionManager() {
        return null;
    }

    default SpecialtyDrinkManager getSpecialtyDrinkManager() {
        return null;
    }

}
