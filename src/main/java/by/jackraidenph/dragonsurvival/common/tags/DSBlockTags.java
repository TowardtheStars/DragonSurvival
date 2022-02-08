package by.jackraidenph.dragonsurvival.common.tags;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class DSBlockTags
{
    public static final String CAVE_DRAGON_BLOCKS;
    public static final String FOREST_DRAGON_BLOCKS;
    public static final String SEA_DRAGON_BLOCKS;

    public static final String SPEED_UP_BLOCKS = "speed_up_blocks";
    public static final Tags.IOptionalNamedTag<Block> CAVE_SPEED_UP_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> FOREST_SPEED_UP_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> SEA_SPEED_UP_BLOCKS;

    public static final Tags.IOptionalNamedTag<Block> SEA_HYDRATION_BLOCKS;


    public static final String MANA_BLOCKS = "mana_blocks";
    public static final Tags.IOptionalNamedTag<Block> SEA_MANA_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> CAVE_MANA_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> FOREST_MANA_BLOCKS;

    public static final String BREATH_BREAKABLE = "breath_breakable";
    public static final Tags.IOptionalNamedTag<Block> FIRE_BREATH_BREAKABLE;
    public static final Tags.IOptionalNamedTag<Block> STORM_BREATH_BREAKABLE;
    public static final Tags.IOptionalNamedTag<Block> FOREST_BREATH_BREAKABLE;

    static
    {
        CAVE_DRAGON_BLOCKS = ("cave_dragon");
        FOREST_DRAGON_BLOCKS = ("forest_dragon");
        SEA_DRAGON_BLOCKS = ("sea_dragon");

        CAVE_MANA_BLOCKS = tag(CAVE_DRAGON_BLOCKS, MANA_BLOCKS);
        CAVE_SPEED_UP_BLOCKS = tag(CAVE_DRAGON_BLOCKS , SPEED_UP_BLOCKS);
        FIRE_BREATH_BREAKABLE = tag(CAVE_DRAGON_BLOCKS , BREATH_BREAKABLE);

        FOREST_BREATH_BREAKABLE = tag(FOREST_DRAGON_BLOCKS , BREATH_BREAKABLE);
        FOREST_MANA_BLOCKS = tag(FOREST_DRAGON_BLOCKS , MANA_BLOCKS);
        FOREST_SPEED_UP_BLOCKS = tag(FOREST_DRAGON_BLOCKS , SPEED_UP_BLOCKS);

        SEA_MANA_BLOCKS = tag(SEA_DRAGON_BLOCKS , MANA_BLOCKS);
        SEA_HYDRATION_BLOCKS = tag(SEA_DRAGON_BLOCKS , "hydration_blocks");
        SEA_SPEED_UP_BLOCKS = tag(SEA_DRAGON_BLOCKS , SPEED_UP_BLOCKS);
        STORM_BREATH_BREAKABLE = tag(SEA_DRAGON_BLOCKS , BREATH_BREAKABLE);
    }

    private static Tags.IOptionalNamedTag<Block> tag(String... name)
    {
        return BlockTags.createOptional(new ResourceLocation(DragonSurvivalMod.MODID, String.join("/", name)));
    }

}
