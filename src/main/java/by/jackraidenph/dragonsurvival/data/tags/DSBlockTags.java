package by.jackraidenph.dragonsurvival.data.tags;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import org.lwjgl.system.NonnullDefault;

import java.util.Map;

@NonnullDefault
public class DSBlockTags
{
    public static final String CAVE_DRAGON_BLOCKS;
    public static final String FOREST_DRAGON_BLOCKS;
    public static final String SEA_DRAGON_BLOCKS;

    private static final String _SPEED_UP_BLOCKS = "speed_up_blocks";
    public static final Map<DragonType, Tags.IOptionalNamedTag<Block>> SPEED_UP_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> CAVE_SPEED_UP_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> FOREST_SPEED_UP_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> SEA_SPEED_UP_BLOCKS;

    public static final Tags.IOptionalNamedTag<Block> SEA_HYDRATION_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> FOREST_BREATH_INEFFECTIVE_BLOCKS;

    private static final String _MANA_BLOCKS = "mana_blocks";
    public static final Map<DragonType, Tags.IOptionalNamedTag<Block>> MANA_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> SEA_MANA_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> CAVE_MANA_BLOCKS;
    public static final Tags.IOptionalNamedTag<Block> FOREST_MANA_BLOCKS;

    private static final String _BREATH_BREAKABLE = "breath_breakable";
    public static final Map<DragonType, Tags.IOptionalNamedTag<Block>> BREATH_BREAKABLE;
    public static final Tags.IOptionalNamedTag<Block> FIRE_BREATH_BREAKABLE;
    public static final Tags.IOptionalNamedTag<Block> STORM_BREATH_BREAKABLE;
    public static final Tags.IOptionalNamedTag<Block> FOREST_BREATH_BREAKABLE;

    static
    {
        CAVE_DRAGON_BLOCKS = ("cave_dragon");
        FOREST_DRAGON_BLOCKS = ("forest_dragon");
        SEA_DRAGON_BLOCKS = ("sea_dragon");

        CAVE_MANA_BLOCKS = tag(CAVE_DRAGON_BLOCKS, _MANA_BLOCKS);
        CAVE_SPEED_UP_BLOCKS = tag(CAVE_DRAGON_BLOCKS , _SPEED_UP_BLOCKS);
        FIRE_BREATH_BREAKABLE = tag(CAVE_DRAGON_BLOCKS , _BREATH_BREAKABLE);

        FOREST_BREATH_BREAKABLE = tag(FOREST_DRAGON_BLOCKS , _BREATH_BREAKABLE);
        FOREST_MANA_BLOCKS = tag(FOREST_DRAGON_BLOCKS , _MANA_BLOCKS);
        FOREST_SPEED_UP_BLOCKS = tag(FOREST_DRAGON_BLOCKS , _SPEED_UP_BLOCKS);

        SEA_MANA_BLOCKS = tag(SEA_DRAGON_BLOCKS , _MANA_BLOCKS);
        SEA_HYDRATION_BLOCKS = tag(SEA_DRAGON_BLOCKS , "hydration_blocks");
        SEA_SPEED_UP_BLOCKS = tag(SEA_DRAGON_BLOCKS , _SPEED_UP_BLOCKS);
        STORM_BREATH_BREAKABLE = tag(SEA_DRAGON_BLOCKS , _BREATH_BREAKABLE);

        FOREST_BREATH_INEFFECTIVE_BLOCKS = tag("forest_breath_ineffective");

        SPEED_UP_BLOCKS = ImmutableMap.of(
                DragonType.CAVE, CAVE_SPEED_UP_BLOCKS,
                DragonType.FOREST, FOREST_SPEED_UP_BLOCKS,
                DragonType.SEA, SEA_SPEED_UP_BLOCKS
        );

        MANA_BLOCKS = ImmutableMap.of(
                DragonType.CAVE, CAVE_MANA_BLOCKS,
                DragonType.FOREST, FOREST_MANA_BLOCKS,
                DragonType.SEA, SEA_MANA_BLOCKS
        );

        BREATH_BREAKABLE = ImmutableMap.of(
                DragonType.CAVE, FIRE_BREATH_BREAKABLE,
                DragonType.FOREST, FOREST_BREATH_BREAKABLE,
                DragonType.SEA, STORM_BREATH_BREAKABLE
        );
    }

    private static Tags.IOptionalNamedTag<Block> tag(String... name)
    {
        return BlockTags.createOptional(new ResourceLocation(DragonSurvivalMod.MODID, String.join("/", name)));
    }

}
