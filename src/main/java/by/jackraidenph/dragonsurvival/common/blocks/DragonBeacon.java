package by.jackraidenph.dragonsurvival.common.blocks;

import by.jackraidenph.dragonsurvival.client.sounds.SoundRegistry;
import by.jackraidenph.dragonsurvival.common.EffectInstance2;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.server.tileentity.DSTileEntities;
import by.jackraidenph.dragonsurvival.server.tileentity.DragonBeaconTileEntity;
import by.jackraidenph.dragonsurvival.util.Functions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DragonBeacon extends Block implements IWaterLoggable {
    public static BooleanProperty LIT = BlockStateProperties.LIT;
    public static BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public DragonBeacon(Properties p_i48440_1_) {
        super(p_i48440_1_);
        registerDefaultState(getStateDefinition().any().setValue(LIT, false).setValue(WATERLOGGED, false));
    }

    private static final Map<Item, DragonBeaconTileEntity.Type> UPGRADE_BEACON_MAP = ImmutableMap.of(
            Items.GOLD_BLOCK, DragonBeaconTileEntity.Type.PEACE,
            Items.DIAMOND_BLOCK, DragonBeaconTileEntity.Type.MAGIC,
            Items.NETHERITE_INGOT, DragonBeaconTileEntity.Type.FIRE
    );

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ActionResultType use(BlockState blockState, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult p_225533_6_) {
        ItemStack itemStack = playerEntity.getItemInHand(hand);
        Item item = itemStack.getItem();
        //upgrading
        if (this == DSBlocks.dragonBeacon) {
            DragonBeaconTileEntity old = (DragonBeaconTileEntity) world.getBlockEntity(pos);

            if (UPGRADE_BEACON_MAP.containsKey(item))
            {
                world.setBlockAndUpdate(pos, DSBlocks.peaceDragonBeacon.defaultBlockState());
                DragonBeaconTileEntity dragonBeaconEntity = (DragonBeaconTileEntity) world.getBlockEntity(pos);
                if (dragonBeaconEntity != null)
                {
                    dragonBeaconEntity.type = UPGRADE_BEACON_MAP.getOrDefault(item, DragonBeaconTileEntity.Type.NONE);
                    if (old != null)
                    {
                        dragonBeaconEntity.tick = old.tick;
                    }
                }
                itemStack.shrink(1);
                world.playSound(playerEntity, pos, SoundRegistry.upgradeBeacon, SoundCategory.BLOCKS, 1, 1);
                return ActionResultType.SUCCESS;
            }

        }
        //apply temporary benefits
        if (DragonStateProvider.isDragon(playerEntity) && itemStack.isEmpty() && (playerEntity.totalExperience >= 60 || playerEntity.isCreative()))
        {
            if (!world.isClientSide)
            {
                if (this == DSBlocks.peaceDragonBeacon)
                {
                    applyTemporaryEffectFromConfig(playerEntity, ConfigHandler.COMMON.peaceBeaconEffects);
                } else if (this == DSBlocks.magicDragonBeacon)
                {
                    applyTemporaryEffectFromConfig(playerEntity, ConfigHandler.COMMON.magicBeaconEffects);
                } else if (this == DSBlocks.fireDragonBeacon)
                {
                    applyTemporaryEffectFromConfig(playerEntity, ConfigHandler.COMMON.fireBeaconEffects);
                }
            }
            playerEntity.giveExperiencePoints(-60);
            world.playSound(playerEntity, pos, SoundRegistry.applyEffect, SoundCategory.PLAYERS, 1, 1);
            return ActionResultType.SUCCESS;
        }
        playerEntity.hurt(DamageSource.GENERIC, 1);
        return ActionResultType.SUCCESS;
    }

    private static void applyTemporaryEffectFromConfig(PlayerEntity playerEntity, ForgeConfigSpec.ConfigValue<List<? extends String>> resourcelocationList)
    {
        resourcelocationList.get().stream()
                .map(ResourceLocation::new)
                .map(ForgeRegistries.POTIONS::getValue)
                .filter(Objects::nonNull)
                .forEach(effect ->
                        playerEntity.addEffect(
                                new EffectInstance2(
                                        effect,
                                        Functions.minutesToTicks(
                                                ConfigHandler.COMMON.secondsOfBeaconEffect.get()
                                        )
                                )
                        )
                );
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return DSTileEntities.dragonBeacon.create();
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateContainer.Builder<Block, BlockState> p_206840_1_) {
        super.createBlockStateDefinition(p_206840_1_);
        p_206840_1_.add(LIT, WATERLOGGED);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState p_149645_1_) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    //methods below are required for waterlogged property to work

    @Nonnull
    @ParametersAreNonnullByDefault
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState1, IWorld world, BlockPos blockPos, BlockPos blockPos1) {
        if (blockState.getValue(WATERLOGGED)) {
            world.getLiquidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(blockState, direction, blockState1, world, blockPos, blockPos1);
    }

    @Nonnull
    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }
}
