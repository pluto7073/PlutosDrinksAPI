package ml.pluto7073.pdapi.tag;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class PDTags {

    public static final TagKey<Item> WORKSTATION_DRINKS = TagKey.of(RegistryKeys.ITEM, PDAPI.asId("workstation_drinks"));
    public static final TagKey<Item> MILK_BOTTLES = TagKey.of(RegistryKeys.ITEM, new Identifier("c:milk/milk_bottle"));

}
