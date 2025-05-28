package net.deepacat.stackcrafting.workbench.recipebook;

import vectorwing.farmersdelight.client.recipebook.CookingPotRecipeBookTab;

public enum SWRecipeBookTab {
    EQUIPMENT("equipment"),
    BUILDING("building"),
    REDSTONE("redstone"),
    MISC("misc");

    public final String name;

    SWRecipeBookTab(String name) {
        this.name = name;
    }

    public static SWRecipeBookTab findByName(String name) {
        for (SWRecipeBookTab value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
