package ml.pluto7073.pdapi.tag;

import ml.pluto7073.pdapi.PDAPI;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class PDTags {

    public static final TagKey<Item> WORKSTATION_DRINKS = TagKey.of(RegistryKeys.ITEM, PDAPI.asId("workstation_drinks"));

}
