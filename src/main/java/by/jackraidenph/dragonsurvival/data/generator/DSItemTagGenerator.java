package by.jackraidenph.dragonsurvival.data.generator;

import by.jackraidenph.dragonsurvival.common.items.DSItems;
import com.google.common.collect.ImmutableList;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

import static by.jackraidenph.dragonsurvival.data.tags.DSItemTags.*;

public class DSItemTagGenerator extends ForgeItemTagsProvider
{
    public DSItemTagGenerator(DataGenerator gen, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper)
    {
        super(gen, blockTagProvider, existingFileHelper);
    }

    @Override
    public void addTags()
    {
        TagsProvider.Builder<Item> builder = tag(UNAVAILABLE_FOR_DRAGON)
                .add(
                        Items.BOW, Items.SHIELD, Items.TRIDENT, Items.CROSSBOW
                );
        ImmutableList.of(   // Copied from ServerConfig.java
                        "item:spartanshields:shield_basic_nickel",
                        "item:spartanshields:shield_basic_invar",
                        "item:spartanshields:shield_basic_constantan",
                        "item:spartanshields:shield_basic_platinum",
                        "item:spartanshields:shield_mekanism_refined_glowstone",
                        "item:spartanshields:shield_tower_wood",
                        "item:spartanshields:shield_tower_stone",
                        "item:spartanshields:shield_tower_iron",
                        "item:spartanshields:shield_tower_gold",
                        "item:spartanshields:shield_tower_diamond",
                        "item:spartanshields:shield_tower_netherite",
                        "item:spartanshields:shield_tower_obsidian",
                        "item:spartanshields:shield_tower_copper",
                        "item:spartanshields:shield_tower_tin",
                        "item:spartanshields:shield_tower_bronze",
                        "item:spartanshields:shield_tower_steel",
                        "item:spartanshields:shield_tower_silver",
                        "item:spartanshields:shield_tower_lead",
                        "item:spartanshields:shield_tower_nickel",
                        "item:spartanshields:shield_tower_constantan",
                        "item:spartanshields:shield_tower_invar",
                        "item:spartanshields:shield_tower_platinum",
                        "item:spartanshields:shield_tower_electrum",
                        "item:spartanshields:shield_mekanism_powered_ultimate",
                        "item:quark:flamerang", "item:quark:pickarang",
                        "item:spartanshields:shield_botania_manasteel",
                        "item:spartanshields:shield_botania_elementium",
                        "item:spartanshields:shield_mekanism_osmium",
                        "item:spartanshields:shield_mekanism_lapis_lazuli",
                        "item:spartanshields:shield_basic_electrum",
                        "item:spartanshields:shield_mekanism_refined_obsidian",
                        "item:spartanshields:shield_mekanism_powered_basic",
                        "item:spartanshields:shield_mekanism_powered_advanced",
                        "item:spartanshields:shield_mekanism_powered_elite",
                        "item:spartanweaponry:boomerang_steel",
                        "item:spartanweaponry:boomerang_invar",
                        "item:spartanweaponry:boomerang_platinum",
                        "item:spartanweaponry:boomerang_electrum",
                        "item:spartanshields:shield_basic_bronze",
                        "item:spartanshields:shield_basic_tin",
                        "item:spartanshields:shield_basic_copper",
                        "item:spartanshields:shield_basic_obsidian",
                        "item:spartanshields:shield_basic_netherite",
                        "item:spartanshields:shield_basic_diamond",
                        "item:spartanshields:shield_basic_gold",
                        "item:spartanshields:shield_basic_iron",
                        "item:spartanshields:shield_basic_stone",
                        "item:spartanshields:shield_basic_wood",
                        "item:spartanweaponry:boomerang_lead",
                        "item:spartanweaponry:boomerang_nickel",
                        "item:spartanshields:shield_basic_steel",
                        "item:spartanshields:shield_basic_silver",
                        "item:spartanshields:shield_basic_lead",
                        "item:spartanweaponry:boomerang_bronze",
                        "item:spartanweaponry:boomerang_tin",
                        "item:spartanweaponry:boomerang_copper",
                        "item:spartanweaponry:boomerang_netherite",
                        "item:spartanweaponry:boomerang_gold",
                        "item:spartanweaponry:boomerang_iron",
                        "item:spartanweaponry:boomerang_stone",
                        "item:spartanweaponry:heavy_crossbow_bronze",
                        "item:spartanshields:shield_botania_terrasteel",
                        "item:spartanweaponry:heavy_crossbow_leather",
                        "item:spartanweaponry:heavy_crossbow_iron",
                        "item:spartanweaponry:heavy_crossbow_gold",
                        "item:spartanweaponry:heavy_crossbow_diamond",
                        "item:spartanweaponry:heavy_crossbow_netherite",
                        "item:spartanweaponry:heavy_crossbow_copper",
                        "item:spartanweaponry:heavy_crossbow_tin",
                        "item:spartanweaponry:boomerang_wood",
                        "item:nethers_exoticism:rambutan_shield",
                        "item:spartanweaponry:heavy_crossbow_lead",
                        "item:spartanweaponry:heavy_crossbow_nickel",
                        "item:spartanweaponry:heavy_crossbow_electrum",
                        "item:spartanweaponry:heavy_crossbow_platinum",
                        "item:spartanweaponry:heavy_crossbow_invar",
                        "item:spartanweaponry:heavy_crossbow_silver",
                        "item:spartanweaponry:heavy_crossbow_steel",
                        "item:spartanweaponry:boomerang_diamond",
                        "item:spartanweaponry:heavy_crossbow_wood",
                        "item:aquaculture:neptunium_bow",
                        "item:spartanweaponry:longbow_electrum",
                        "item:spartanweaponry:longbow_invar",
                        "item:infernalexp:glowsilk_bow",
                        "item:spartanweaponry:longbow_wood",
                        "item:spartanweaponry:longbow_leather",
                        "item:spartanweaponry:longbow_silver",
                        "item:spartanweaponry:longbow_steel",
                        "item:spartanweaponry:longbow_bronze",
                        "item:spartanweaponry:longbow_tin",
                        "item:spartanweaponry:longbow_copper",
                        "item:spartanweaponry:longbow_netherite",
                        "item:spartanweaponry:longbow_diamond",
                        "item:spartanweaponry:longbow_gold",
                        "item:spartanweaponry:longbow_iron",
                        "item:spartanweaponry:boomerang_diamond",
                        "item:spartanweaponry:boomerang_iron",
                        "item:spartanweaponry:boomerang_wood",
                        "item:spartanweaponry:boomerang_gold",
                        "item:spartanweaponry:boomerang_netherite",
                        "item:spartanweaponry:boomerang_copper",
                        "item:spartanweaponry:boomerang_tin",
                        "item:spartanweaponry:boomerang_bronze",
                        "item:spartanweaponry:boomerang_stone",
                        "item:spartanweaponry:boomerang_platinum",
                        "item:spartanweaponry:boomerang_electrum",
                        "item:spartanweaponry:boomerang_steel",
                        "item:spartanweaponry:boomerang_lead",
                        "item:spartanweaponry:boomerang_invar",
                        "item:spartanweaponry:boomerang_nickel"
                )
                .stream()
                .map(str->str.split(":", 2)[1])
                .map(ResourceLocation::new)
                .forEach(builder::addOptional);

        tag(HYDRATION_USABLES)
                .add(Items.ENCHANTED_GOLDEN_APPLE);

        subTag(CAVE_DRAGON_HURTFUL_ITEMS, "damage_2")
                .add(Items.POTION, Items.MILK_BUCKET)
        ;

        addParameterTag(CAVE_DRAGON_HURTFUL_ITEMS);

        tag(GROW_NEWBORN)
                .add(
                        DSItems.dragonHeartShard
                ).addTags(GROW_YOUNG, GROW_ADULT);

        tag(GROW_YOUNG)
                .add(
                        DSItems.weakDragonHeart
                ).addTags(GROW_ADULT);

        tag(GROW_ADULT)
                .add(
                        DSItems.elderDragonHeart
                );


    }

    private void addParameterTag(Tags.IOptionalNamedTag<Item> pTag)
    {
        ItemTags.getAllTags().getAllTags().entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof ITag.INamedTag)
                .filter(entry -> entry.getKey().toString().startsWith(pTag.getName().toString()))
                .forEach(
                        (entry) ->
                            tag(pTag).addTag((ITag.INamedTag<Item>)entry.getValue())
                );
    }

    private Tags.IOptionalNamedTag<Item> getSubTag(ITag.INamedTag<Item> pTag, String... path)
    {
        return ItemTags.createOptional(new ResourceLocation(pTag.getName().toString() + "/" + String.join("/", path)));
    }

    private TagsProvider.Builder<Item> subTag(ITag.INamedTag<Item> pTag, String... path)
    {
        return tag(getSubTag(pTag, path));
    }
}
