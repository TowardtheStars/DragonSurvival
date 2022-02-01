package by.jackraidenph.dragonsurvival.client.util;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.DragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class FakeClientPlayerUtils
{
	public static final ConcurrentHashMap<Integer, Object> fakePlayers = new ConcurrentHashMap<>();
	public static final ConcurrentHashMap<Integer, DragonEntity> fakeDragons = new ConcurrentHashMap<>();

	@OnlyIn(Dist.CLIENT)
	public static FakeClientPlayer getFakePlayer(int num, DragonStateHandler handler){
		fakePlayers.computeIfAbsent(num, FakeClientPlayer::new);
		((FakeClientPlayer)fakePlayers.get(num)).handler = handler;
		((FakeClientPlayer)fakePlayers.get(num)).lastAccessed = System.currentTimeMillis();
		return (FakeClientPlayer) fakePlayers.get(num);
	}

	@OnlyIn(Dist.CLIENT)
	public static DragonEntity getFakeDragon(int num, DragonStateHandler handler){
		FakeClientPlayer clientPlayer = getFakePlayer(num, handler);
		
		fakeDragons.computeIfAbsent(num, (n) -> new DragonEntity(DSEntities.DRAGON, clientPlayer.level){
			@Override
			public PlayerEntity getPlayer()
			{
				return clientPlayer;
			}
			
			@Override
			public void registerControllers(AnimationData animationData) {
				animationData.shouldPlayWhilePaused = true;
				animationData.addAnimationController(new AnimationController<DragonEntity>(this, "fake_player_controller", 2, (event) -> {
					
					if(getPlayer() instanceof FakeClientPlayer) {
						AnimationBuilder builder = new AnimationBuilder();
						
						if (clientPlayer.animationSupplier != null) {
							builder.addAnimation(clientPlayer.animationSupplier.get(), true);
						}
						
						event.getController().setAnimation(builder);
						return PlayState.CONTINUE;
					}else {
						return PlayState.STOP;
					}
				}));
			}
		});
		
		return fakeDragons.get(num);
	}


}
