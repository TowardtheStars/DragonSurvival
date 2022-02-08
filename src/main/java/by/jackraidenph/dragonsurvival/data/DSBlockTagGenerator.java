package by.jackraidenph.dragonsurvival.data;

import by.jackraidenph.dragonsurvival.common.blocks.DSBlocks;
import by.jackraidenph.dragonsurvival.common.tags.DSBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;

import static by.jackraidenph.dragonsurvival.common.tags.DSBlockTags.*;
import static net.minecraft.tags.BlockTags.*;
import static net.minecraftforge.common.Tags.Blocks.*;

public class DSBlockTagGenerator extends ForgeBlockTagsProvider
{
    public DSBlockTagGenerator(DataGenerator gen, ExistingFileHelper existingFileHelper)
    {
        super(gen, existingFileHelper);
    }

    @Override
    public void addTags()
    {
        tag(CAVE_SPEED_UP_BLOCKS)
                .addTags(
                        BASE_STONE_NETHER,
                        BASE_STONE_OVERWORLD,
                        STONE_BRICKS,
                        BEACON_BASE_BLOCKS,
                        COBBLESTONE,
                        SANDSTONE,
                        STONE,
                        ORES
                )
                .addOptional(
                        new ResourceLocation("quark:deepslate")
                )
                .addOptional(
                        new ResourceLocation("quark:deepslate_bricks")
                )
                .addOptional(
                        new ResourceLocation("quark:cobbled_deepslate")
                )
        ;

        tag(FOREST_SPEED_UP_BLOCKS)
                .addTags(
                        LOGS, LEAVES, PLANKS, DIRT
                );

        tag(SEA_SPEED_UP_BLOCKS)
                .addTags(
                        ICE, IMPERMEABLE,
                        net.minecraft.tags.BlockTags.SAND,
                        net.minecraftforge.common.Tags.Blocks.SAND,
                        SANDSTONE
                )
                .add(
                        Blocks.GRASS_PATH, Blocks.SANDSTONE, Blocks.WATER
                );
        tag(SEA_HYDRATION_BLOCKS).addTag(ICE)
                .add(Blocks.SNOW, Blocks.SNOW_BLOCK, DSBlocks.seaSourceOfMagic);

        tag(SEA_MANA_BLOCKS)
                .addTags(ICE)
                .add(
                        DSBlocks.seaSourceOfMagic,
                        Blocks.SNOW_BLOCK,
                        Blocks.SNOW,
                        Blocks.WATER,
                        Blocks.WET_SPONGE,
                        Blocks.CAULDRON
                );
        tag(FOREST_MANA_BLOCKS)
                .add(DSBlocks.forestSourceOfMagic)
                .addTags(SMALL_FLOWERS, FLOWERS, TALL_FLOWERS);

        tag(CAVE_MANA_BLOCKS)
                .add(
                        DSBlocks.caveSourceOfMagic,
                        Blocks.LAVA,
                        Blocks.SMOKER,
                        Blocks.FURNACE,
                        Blocks.MAGMA_BLOCK,
                        Blocks.BLAST_FURNACE
                )
                .addTags(CAMPFIRES);

        tag(FOREST_BREATH_BREAKABLE)
                .addTags(BANNERS);

        tag(STORM_BREATH_BREAKABLE)
                .addTags(
                        IMPERMEABLE,
                        CROPS,
                        FLOWERS,
                        BANNERS,
                        LEAVES
                )
                .add(Blocks.SNOW);

        tag(FIRE_BREATH_BREAKABLE)
                .addTags(
                        ICE,
                        CROPS,
                        LEAVES,
                        FLOWERS,
                        BANNERS,
                        WOODEN_FENCES,
                        LOGS_THAT_BURN,
                        WOODEN_STAIRS,
                        WOODEN_DOORS,
                        WOOL,
                        SAPLINGS,
                        IMPERMEABLE
                )
                .add(
                        Blocks.SNOW,
                        Blocks.COBWEB,
                        Blocks.LARGE_FERN,
                        Blocks.SUGAR_CANE,
                        Blocks.SNOW_BLOCK
                );
    }
}
