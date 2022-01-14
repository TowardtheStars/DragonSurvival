package by.jackraidenph.dragonsurvival.common.handlers.magic;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.TieredItem;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@EventBusSubscriber
public class ClawToolHandler
{
    @SubscribeEvent
    public static void experiencePickup(PickupXp event)
    {
        PlayerEntity player = event.getPlayer();

        DragonStateProvider.getCap(player).ifPresent(cap ->
        {
            ArrayList<ItemStack> stacks = new ArrayList<>();

            for (int i = 0; i < 4; i++)
            {
                ItemStack clawStack = cap.getClawInventory().getClawsInventory().getItem(i);
                int mending = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, clawStack);

                if (mending > 0 && clawStack.isDamaged())
                {
                    stacks.add(clawStack);
                }
            }

            if (stacks.size() > 0)
            {
                ItemStack repairTime = stacks.get(player.level.random.nextInt(stacks.size()));
                if (!repairTime.isEmpty() && repairTime.isDamaged())
                {

                    int i = Math.min((int) (event.getOrb().value * repairTime.getXpRepairRatio()), repairTime.getDamageValue());
                    event.getOrb().value -= i * 2;
                    repairTime.setDamageValue(repairTime.getDamageValue() - i);
                }
            }

            event.getOrb().value = Math.max(0, event.getOrb().value);
        });
    }

    @SubscribeEvent
    public static void playerDieEvent(LivingDropsEvent event)
    {
        Entity ent = event.getEntity();

        if (ent instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) ent;

            if (!player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !ConfigHandler.SERVER.keepClawItems.get())
            {
                DragonStateProvider.getCap(player).ifPresent (handler ->
                {
                    for (int i = 0; i < handler.getClawInventory().getClawsInventory().getContainerSize(); i++)
                    {
                        ItemStack stack = handler.getClawInventory().getClawsInventory().getItem(i);

                        if (!stack.isEmpty())
                        {
                            event.getDrops().add(new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), stack));
                            handler.getClawInventory().getClawsInventory().setItem(i, ItemStack.EMPTY);
                        }
                    }
                }
                        );
            }
        }
    }

    @SubscribeEvent
    public static void dropBlocksMinedByPaw(PlayerEvent.HarvestCheck harvestCheck)
    {
        if (!ConfigHandler.SERVER.bonuses.get() || !ConfigHandler.SERVER.clawsAreTools.get())
        {
            return;
        }
        PlayerEntity playerEntity = harvestCheck.getPlayer();
        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler ->
        {
            if (dragonStateHandler.isDragon())
            {
                ItemStack mainHandItem = playerEntity.getMainHandItem();
                BlockState blockState = harvestCheck.getTargetBlock();
                if (!willOverrideClawsAndTeeth(mainHandItem) && !harvestCheck.canHarvest())
                {
                    harvestCheck.setCanHarvest(dragonStateHandler.canHarvestWithPaw(playerEntity, blockState));
                }
            }
        });
    }

    public static ItemStack getDragonTools(PlayerEntity player)
    {
        ItemStack mainStack = player.inventory.getSelected();
        AtomicReference<ItemStack> harvestTool = new AtomicReference<>(mainStack);
        if (!willOverrideClawsAndTeeth(mainStack))
        {
            World world = player.level;
            BlockRayTraceResult rayTraceResult = Item.getPlayerPOVHitResult(world, player, FluidMode.NONE);
            if (rayTraceResult.getType() != RayTraceResult.Type.MISS)
            {
                DragonStateProvider.getCap(player).filter(DragonStateHandler::isDragon).ifPresent(cap ->
                {

                    // Get the fastest claws and teeth tool for the block
                    BlockState state = world.getBlockState(rayTraceResult.getBlockPos());
//                    for (int i = 1; i < 4; i++)
//                    {
//                        ItemStack breakingItem = cap.getClawInventory().getClawsInventory().getItem(i);
//
//                        if (!breakingItem.isEmpty() && breakingItem.getToolTypes().stream().anyMatch(state::isToolEffective))
//                        {
//                            float tempSpeed = breakingItem.getDestroySpeed(state);
//
//                            if (breakingItem.getItem() instanceof ToolItem)
//                            {
//                                ToolItem item = (ToolItem) breakingItem.getItem();
//                                tempSpeed = item.getDestroySpeed(breakingItem, state);
//                            }
//
//                            if (tempSpeed > newSpeed)
//                            {
//                                newSpeed = tempSpeed;
//                                harvestTool.set(breakingItem);
//                            }
//                        }
//                    }
                    harvestTool.set(
                            IntStream.range(1,4)
                                    .mapToObj(
                                            i->cap.getClawInventory().getClawsInventory().getItem(i)
                                    )
                                    .filter(((Predicate<? super ItemStack>) ItemStack::isEmpty).negate())
                                    .max(
                                            Comparator.comparing(
                                                toolStack -> toolStack.getDestroySpeed(state)
                                            )
                                    )
                                    .orElse(mainStack)
                    );
                });
            }
        }

		return harvestTool.get();
	}

    public static boolean willOverrideClawsAndTeeth(@Nonnull ItemStack stack)
    {
        return !stack.isEmpty()
                || !stack.getToolTypes().isEmpty()
                || stack.getItem() instanceof ShearsItem
                || stack.getItem() instanceof TieredItem;
    }

	
	
	@Mod.EventBusSubscriber( modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Event_busHandler{
		@SubscribeEvent
		public void modifyBreakSpeed(PlayerEvent.BreakSpeed breakSpeedEvent) {
			if (!ConfigHandler.SERVER.bonuses.get() || !ConfigHandler.SERVER.clawsAreTools.get())
				return;
			PlayerEntity playerEntity = breakSpeedEvent.getPlayer();
			
			ItemStack mainStack = playerEntity.getMainHandItem();

			if(willOverrideClawsAndTeeth(mainStack))
                return;

			DragonStateProvider.getCap(playerEntity)
                    .filter(DragonStateHandler::isDragon)
                    .ifPresent(dragonStateHandler -> {
                BlockState blockState = breakSpeedEvent.getState();

                float originalSpeed = breakSpeedEvent.getOriginalSpeed();


                // Check if claws and teeth have the proper tool for the block
                        /*
                         * Fix: cobweb and other blocks that return `null` in harvestTool()
                         *      will never receive bonus mining speed if there is any tool inside claws and teeth
                         */
                int idx = -1;
                for (int i = 1; i < 4; i++)
                {
                    if (blockState.getHarvestTool() == DragonStateHandler.CLAW_TOOL_TYPES[i])
                    {
                        idx = i;
                        break;
                    }
                }
                // If claws and teeth have the proper tool, no extra modification
                if (idx >= 1 && !dragonStateHandler.getClawInventory().getClawsInventory().getItem(idx).isEmpty())
                    return;


                // Otherwise, if main hand item is not a tool, a sword or a shear



                // If dragon older than bonusUnlockedAt config value, apply bonus by 2
                float bonus = 1f;
                // If adult dragon, apply species extra bonus
                if (dragonStateHandler.getLevel() == DragonLevel.ADULT)
                {
                    DragonType type = dragonStateHandler.getType();
                    ToolType harvestTool = blockState.getHarvestTool();
                    if (
                            harvestTool == ToolType.AXE && type == DragonType.FOREST ||
                            harvestTool == ToolType.PICKAXE && type == DragonType.CAVE ||
                            harvestTool == ToolType.SHOVEL && type == DragonType.SEA
                    )
                        bonus = 4f;
                }
                else
                {
                     bonus = dragonStateHandler.getLevel().compareTo(
                            ConfigHandler.SERVER.bonusUnlockedAt.get()
                     ) < 0 ? 1f : 2f;
                }
                LogManager.getLogger(Event_busHandler.class).debug(
                        MarkerManager.getMarker(PlayerEvent.BreakSpeed.class.getName()),
                        "bonus=" + bonus
                );
                breakSpeedEvent.setNewSpeed((originalSpeed * bonus));

			});

        }



	}

}
