package by.jackraidenph.dragonsurvival.client.handlers.magic;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.client.particles.CaveDragon.SmallFireParticleData;
import by.jackraidenph.dragonsurvival.client.particles.ForestDragon.SmallPoisonParticleData;
import by.jackraidenph.dragonsurvival.client.particles.SeaDragon.LargeLightningParticleData;
import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.misc.DragonType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientMagicHandler
{
	
	@SubscribeEvent
	public static void onFovEvent(FOVUpdateEvent event) {
	    PlayerEntity player = event.getEntity();
	    
	    DragonStateProvider.getCap(player).ifPresent(cap -> {
			if(cap.getEmotes().getCurrentEmote() != null){
				event.setNewfov(1f);
				return;
			}
			
	        ActiveDragonAbility ability = cap.getMagic().getCurrentlyCasting();
	        
	        if(ability != null && ability.getCurrentCastTimer() > 0){
		        double perc = Math.min(((float)ability.getCurrentCastTimer() / (float)ability.getCastingTime()), 1) / 4;
				double c4 = (2 * Math.PI) / 3;
				
				if(perc != 0 && perc != 1){
					perc = Math.pow(2, -10 * perc) * Math.sin((perc * 10 - 0.75) * c4) + 1;
				}
				
				float newFov = (float)MathHelper.clamp(perc, 0.75F, 1.2F);
	            event.setNewfov(newFov);
	        }
	    });
	}
	
	@OnlyIn( Dist.CLIENT)
	@SubscribeEvent
	public static void livingTick(LivingUpdateEvent event)
	{
		LivingEntity entity = event.getEntityLiving();
		
		
		if (!entity.level.isClientSide) {
			return;
		}
		
		if(entity == Minecraft.getInstance().player || DragonStateProvider.isDragon(entity)){
			return;
		}
		if (entity.hasEffect(DragonEffects.BURN)) {
			IParticleData data = new SmallFireParticleData(37F, false);
			for (int i = 0; i < 4; i++) {
				renderEffectParticle(entity, data);
			}
		}
		
		if (entity.hasEffect(DragonEffects.DRAIN)) {
			IParticleData data = new SmallPoisonParticleData(37F, false);
			for (int i = 0; i < 4; i++) {
				renderEffectParticle(entity, data);
			}
		}
		
		if (entity.hasEffect(DragonEffects.CHARGED)) {
			IParticleData data = new LargeLightningParticleData(37F, false);
			for (int i = 0; i < 4; i++) {
				renderEffectParticle(entity, data);
			}
		}
	}
	
	public static void renderEffectParticle(LivingEntity entity, IParticleData data)
	{
		double d0 = (double)(entity.level.random.nextFloat()) * entity.getBbWidth();
		double d1 = (double)(entity.level.random.nextFloat()) * entity.getBbHeight();
		double d2 = (double)(entity.level.random.nextFloat()) * entity.getBbWidth();
		double x = entity.getX() + d0 - (entity.getBbWidth() / 2);
		double y = entity.getY() + d1;
		double z = entity.getZ() + d2 - (entity.getBbWidth() / 2);
		Minecraft.getInstance().player.level.addParticle(data, x, y, z, 0, 0, 0);
	}
	
	@SubscribeEvent
	@OnlyIn( Dist.CLIENT)
	public static void removeLavaAndWaterFog(EntityViewRenderEvent.FogDensity event) {
	    ClientPlayerEntity player = Minecraft.getInstance().player;
	    DragonStateProvider.getCap(player).ifPresent(cap -> {
	        if(!cap.isDragon()) return;
	        
	        if (cap.getType() == DragonType.CAVE && event.getInfo().getFluidInCamera().is(FluidTags.LAVA)) {
	            if(player.hasEffect(DragonEffects.LAVA_VISION)) {
	                event.setDensity(0.02F);
	                event.setCanceled(true);
	            }
	        }else if (cap.getType() == DragonType.SEA && event.getInfo().getFluidInCamera().is(FluidTags.WATER)) {
	            if(player.hasEffect(DragonEffects.WATER_VISION)) {
	                event.setDensity(event.getDensity() / 10);
	                event.setCanceled(true);
	            }
	        }
	    });
	}
}
