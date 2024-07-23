package ml.pluto7073.pdapi.tag;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class PDTags {

    public static final TagKey<Item> WORKSTATION_DRINKS = TagKey.create(Registries.ITEM, PDAPI.asId("workstation_drinks"));
    public static final TagKey<Item> MILK_BOTTLES = TagKey.create(Registries.ITEM, new ResourceLocation("c:milk/milk_bottle"));

}
