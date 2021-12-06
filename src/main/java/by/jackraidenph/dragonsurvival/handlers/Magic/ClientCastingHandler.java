package by.jackraidenph.dragonsurvival.handlers.Magic;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.handlers.ClientSide.KeyInputHandler;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.magic.common.ActiveDragonAbility;
import by.jackraidenph.dragonsurvival.network.magic.ActivateAbilityServerSide;
import by.jackraidenph.dragonsurvival.network.magic.SyncAbilityCastingToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
@Mod.EventBusSubscriber( Dist.CLIENT)
public class ClientCastingHandler
{
	private static byte timer = 0;
	private static byte abilityHoldTimer = 0;
	
	@SubscribeEvent
	public static void abilityKeyBindingChecks(TickEvent.ClientTickEvent clientTickEvent) {
	    if ((Minecraft.getInstance().player == null) ||
	        (Minecraft.getInstance().level == null) ||
	        (clientTickEvent.phase != TickEvent.Phase.END) ||
	        (!DragonStateProvider.isDragon(Minecraft.getInstance().player)))
	        return;
	    
	    PlayerEntity playerEntity = Minecraft.getInstance().player;
	    
	    abilityHoldTimer = (byte) (KeyInputHandler.USE_ABILITY.isDown() ? abilityHoldTimer < 3 ? abilityHoldTimer + 1 : abilityHoldTimer : 0);
	    byte modeAbility;
	    if (KeyInputHandler.USE_ABILITY.isDown() && abilityHoldTimer > 1)
	        modeAbility = GLFW.GLFW_REPEAT;
	    else if (KeyInputHandler.USE_ABILITY.isDown() && abilityHoldTimer == 1)
	        modeAbility = GLFW.GLFW_PRESS;
	    else
	        modeAbility = GLFW.GLFW_RELEASE;
	    
	    int slot = DragonStateProvider.getCap(playerEntity).map((i) -> i.getMagic().getSelectedAbilitySlot()).orElse(0);
	    timer = (byte) ((modeAbility == GLFW.GLFW_RELEASE) ? timer < 3 ? timer + 1 : timer : 0);
	    
	    if (timer > 1)
	        return;
	    
	    DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
	        ActiveDragonAbility ability = dragonStateHandler.getMagic().getAbilityFromSlot(slot);
	        if(ability.getLevel() > 0) {
	            if(ability.canRun(playerEntity, modeAbility) && !ability.isDisabled()) {
		            ability.errorTicks = 0;
		            ability.errorMessage = null;
					
	                if (ability.getCurrentCastTimer() < ability.getCastingTime() && modeAbility == GLFW.GLFW_REPEAT) {
		                ability.tickCasting();
						
						if(dragonStateHandler.getMagic().getCurrentlyCasting() != ability) {
							dragonStateHandler.getMagic().setCurrentlyCasting(ability);
							NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCastingToServer(playerEntity.getId(), ability));
						}
						
	                } else if (modeAbility == GLFW.GLFW_RELEASE) {
	                    ability.stopCasting();
						
						if(dragonStateHandler.getMagic().getCurrentlyCasting() != null) {
							NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCastingToServer(playerEntity.getId(), null));
							dragonStateHandler.getMagic().setCurrentlyCasting(null);
						}
		
	                } else if (ability.getCastingTime() <= 0 || ability.getCurrentCastTimer() >= ability.getCastingTime()){
	                    ability.onKeyPressed(playerEntity);
	                    NetworkHandler.CHANNEL.sendToServer(new ActivateAbilityServerSide(slot));
	                }
	            }else{
		            if(dragonStateHandler.getMagic().getCurrentlyCasting() != null) {
			            NetworkHandler.CHANNEL.sendToServer(new SyncAbilityCastingToServer(playerEntity.getId(), null));
			            dragonStateHandler.getMagic().setCurrentlyCasting(null);
		            }
	            }
	        }
	    });
	}
}
