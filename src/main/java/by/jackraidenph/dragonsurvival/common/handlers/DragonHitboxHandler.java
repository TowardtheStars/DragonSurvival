package by.jackraidenph.dragonsurvival.common.handlers;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.DSEntities;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber
public class DragonHitboxHandler
{
	public static ConcurrentHashMap<Integer, Integer> dragonHitboxes = new ConcurrentHashMap<>();
	
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent playerTickEvent){
		PlayerEntity player = playerTickEvent.player;
		
		if(!player.level.isClientSide) {
			if (DragonStateProvider.isDragon(player)) {
				if (dragonHitboxes.containsKey(player.getId())) {
					int id = dragonHitboxes.get(player.getId());
					Entity ent = player.level.getEntity(id);
					
					if (ent == null || !(ent instanceof DragonHitBox) || !ent.isAlive() || ent.removed) {
						dragonHitboxes.remove(player.getId());
						addPlayerHitbox(player);
					}
				}else{
					dragonHitboxes.remove(player.getId());
					addPlayerHitbox(player);
				}
			}
		}
	}
	
	private static void addPlayerHitbox(PlayerEntity player){
		if(player.level != null) {
			DragonHitBox hitbox = DSEntities.DRAGON_HITBOX.create(player.level);
			hitbox.copyPosition(player);
			hitbox.player = player;
			hitbox.setPlayerId(player.getId());
			player.level.addFreshEntity(hitbox);
			dragonHitboxes.put(player.getId(), hitbox.getId());
		}
	}

	public static boolean isThisPlayer(@Nullable Entity entity, @Nullable Entity player)
	{
		return entity != null && player != null && ((entity instanceof DragonHitBox) ?
				((DragonHitBox) entity).getPlayerId() == player.getId() :
				entity instanceof DragonHitboxPart
						&& ((DragonHitboxPart) entity).parentMob.getPlayerId() == player.getId());
	}

	@SubscribeEvent
	public static void cancelSelfHurting(LivingHurtEvent event)
	{
		event.setCanceled(isThisPlayer(event.getEntity(), event.getSource().getEntity()));
	}

}
