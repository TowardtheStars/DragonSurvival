package by.jackraidenph.dragonsurvival.common.tags;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class DSItemTags
{
    public static final String CAVE_DRAGON = "cave_dragon";
    public static final String FOREST_DRAGON = "forest_dragon";
    public static final String SEA_DRAGON = "sea_dragon";

    public static final Tags.IOptionalNamedTag<Item> GROW_NEWBORN;
    public static final Tags.IOptionalNamedTag<Item> GROW_YOUNG;
    public static final Tags.IOptionalNamedTag<Item> GROW_ADULT;

    public static final Tags.IOptionalNamedTag<Item> HYDRATION_USABLES;

    public static final String HURTFUL_ITEMS = "hurtful_items";
    public static final Tags.IOptionalNamedTag<Item> SEA_DRAGON_HURTFUL_ITEMS;
    public static final Tags.IOptionalNamedTag<Item> FOREST_DRAGON_HURTFUL_ITEMS;
    public static final Tags.IOptionalNamedTag<Item> CAVE_DRAGON_HURTFUL_ITEMS;

    public static final String DRAGON_FOOD = "dragon_food";
    public static final Tags.IOptionalNamedTag<Item> CAVE_DRAGON_FOOD;
    public static final Tags.IOptionalNamedTag<Item> FOREST_DRAGON_FOOD;
    public static final Tags.IOptionalNamedTag<Item> SEA_DRAGON_FOOD;

    static {
        GROW_ADULT = tag("grow_adult");
        GROW_NEWBORN = tag("grow_newborn");
        GROW_YOUNG = tag("grow_young");

        HYDRATION_USABLES = tag(SEA_DRAGON, "hydration_usable");
        SEA_DRAGON_HURTFUL_ITEMS = tag(SEA_DRAGON, HURTFUL_ITEMS);
        FOREST_DRAGON_HURTFUL_ITEMS = tag(FOREST_DRAGON, HURTFUL_ITEMS);
        CAVE_DRAGON_HURTFUL_ITEMS = tag(CAVE_DRAGON, HURTFUL_ITEMS);

        CAVE_DRAGON_FOOD = tag(CAVE_DRAGON, DRAGON_FOOD);
        FOREST_DRAGON_FOOD = tag(FOREST_DRAGON, DRAGON_FOOD);
        SEA_DRAGON_FOOD= tag(SEA_DRAGON, DRAGON_FOOD);

    }

    private static Tags.IOptionalNamedTag<Item> tag(String... name)
    {
        return ItemTags.createOptional(new ResourceLocation(DragonSurvivalMod.MODID, String.join("/", name)));
    }


}
