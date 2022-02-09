package by.jackraidenph.dragonsurvival.common.handlers;

import by.jackraidenph.dragonsurvival.common.DamageSources;
import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.data.DSTags;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.entity.player.SyncCapabilityDebuff;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
@Mod.EventBusSubscriber
public class DragonPenaltyHandler
{
	@SubscribeEvent
	public static void hitByPotion(ProjectileImpactEvent.Throwable potionEvent) {
		if (!ConfigHandler.SERVER.penalties.get() || ConfigHandler.SERVER.caveSplashDamage.get() == 0.0)
			return;

		if(potionEvent.getThrowable() instanceof PotionEntity){
			PotionEntity potionEntity = (PotionEntity)potionEvent.getThrowable();
			if(potionEntity.getItem().getItem() != net.minecraft.item.Items.SPLASH_POTION) return;
			if(PotionUtils.getPotion(potionEntity.getItem()).getEffects().size() > 0) return; //Remove this line if you want potions with effects to also damage rather then just water ones.
			
			Vector3d pos = potionEvent.getRayTraceResult().getLocation();
			List<PlayerEntity> entities = potionEntity.level.getEntities(EntityType.PLAYER, new AxisAlignedBB(pos.x - 5, pos.y - 1, pos.z - 5, pos.x + 5, pos.y + 1, pos.z + 5), (entity) -> entity.position().distanceTo(pos) <= 4);
			
			for(PlayerEntity player : entities){
				if(player.hasEffect(DragonEffects.FIRE)) continue;
				
				DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
					if (dragonStateHandler.isDragon()) {
						if(dragonStateHandler.getType() != DragonType.CAVE) return;
						player.hurt(DamageSources.WATER_BURN, ConfigHandler.SERVER.caveSplashDamage.get().floatValue());
					}
				});
			}
		}
	}
	
	@SubscribeEvent
		public static void consumeHurtfulItem(LivingEntityUseItemEvent.Finish destroyItemEvent) {
			if (!ConfigHandler.SERVER.penalties.get())
				return;
			
			if(!(destroyItemEvent.getEntityLiving() instanceof PlayerEntity)) return;
			
			PlayerEntity playerEntity = (PlayerEntity)destroyItemEvent.getEntityLiving();
			ItemStack itemStack = destroyItemEvent.getItem();
			
			DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
				if (dragonStateHandler.isDragon()) {
					Tags.IOptionalNamedTag<Item> hurtfulItems = (
							dragonStateHandler.getType() == DragonType.FOREST ? DSTags.Items.FOREST_DRAGON_HURTFUL_ITEMS:
							dragonStateHandler.getType() == DragonType.CAVE ? DSTags.Items.CAVE_DRAGON_HURTFUL_ITEMS:
							dragonStateHandler.getType() == DragonType.SEA ? DSTags.Items.SEA_DRAGON_HURTFUL_ITEMS: null);
					if (hurtfulItems != null)
					{
						itemStack.getItem().getTags().stream()
								.filter(res -> res.toString().startsWith(hurtfulItems.getName().toString()))
								.findAny()
								.ifPresent(
										res -> {
											String[] paths = res.toString().split("/");
											float damage = Float.parseFloat(paths[paths.length - 1].split("damage_")[1]);
											playerEntity.hurt(DamageSource.GENERIC, damage);
										}
								);
					}
//					if(hurtfulItems.size() > 0){
//						ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
//
//						if(itemId != null){
//							for(String item : hurtfulItems){
//								boolean match = item.startsWith("item:" + itemId + ":");
//
//								if(!match){
//									for(ResourceLocation tag : itemStack.getItem().getTags()){
//										if(item.startsWith("tag:" + tag + ":")){
//											match = true;
//											break;
//										}
//									}
//								}
//
//								if(match){
//									String damage = item.substring(item.lastIndexOf(":") + 1);
//									playerEntity.hurt(DamageSource.GENERIC, Float.parseFloat(damage));
//									break;
//								}
//							}
//						}
//					}
				}
			});
		}
	
	@SubscribeEvent
	public static void onWaterConsumed(LivingEntityUseItemEvent.Finish destroyItemEvent) {
		if (!ConfigHandler.SERVER.penalties.get() || ConfigHandler.SERVER.seaTicksWithoutWater.get() == 0)
			return;
	    ItemStack itemStack = destroyItemEvent.getItem();
	    DragonStateProvider.getCap(destroyItemEvent.getEntityLiving()).ifPresent(dragonStateHandler -> {
	        if (dragonStateHandler.isDragon()) {
	            PlayerEntity playerEntity = (PlayerEntity)destroyItemEvent.getEntityLiving();
	            if (ConfigHandler.SERVER.seaAllowWaterBottles.get() && itemStack.getItem() instanceof PotionItem) {
						if (PotionUtils.getPotion(itemStack) == Potions.WATER && dragonStateHandler.getType() == DragonType.SEA && !playerEntity.level.isClientSide) {
							dragonStateHandler.getDebuffData().timeWithoutWater = Math.max(dragonStateHandler.getDebuffData().timeWithoutWater - ConfigHandler.SERVER.seaTicksWithoutWaterRestored.get(), 0);
							NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new SyncCapabilityDebuff(playerEntity.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
						}
	            }
	            if (DSTags.Items.HYDRATION_USABLES.contains(itemStack.getItem()) && !playerEntity.level.isClientSide) {
		            dragonStateHandler.getDebuffData().timeWithoutWater = Math.max(dragonStateHandler.getDebuffData().timeWithoutWater - ConfigHandler.SERVER.seaTicksWithoutWaterRestored.get(), 0);
		            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new SyncCapabilityDebuff(playerEntity.getId(), dragonStateHandler.getDebuffData().timeWithoutWater, dragonStateHandler.getDebuffData().timeInDarkness, dragonStateHandler.getDebuffData().timeInRain));
	            }
	        }
	    });
	}
}
