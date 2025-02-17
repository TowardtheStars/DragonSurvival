package by.jackraidenph.dragonsurvival.network.status;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.network.IMessage;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class SyncTreasureRestStatus implements IMessage<SyncTreasureRestStatus>
{
	public int playerId;
	public boolean state;
	
	public SyncTreasureRestStatus() {}
	
	public SyncTreasureRestStatus(int playerId, boolean state) {
		this.playerId = playerId;
		this.state = state;
	}
	
	@Override
	public void encode(SyncTreasureRestStatus message, PacketBuffer buffer) {
		buffer.writeInt(message.playerId);
		buffer.writeBoolean(message.state);
	}
	
	@Override
	public SyncTreasureRestStatus decode(PacketBuffer buffer) {
		int playerId = buffer.readInt();
		boolean state = buffer.readBoolean();
		return new SyncTreasureRestStatus(playerId, state);
	}
	
	@Override
	public void handle(SyncTreasureRestStatus message, Supplier<NetworkEvent.Context> supplier) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> runClient(message, supplier));
		
		if(supplier.get().getDirection() == PLAY_TO_SERVER){
			ServerPlayerEntity entity = supplier.get().getSender();
			if(entity != null){
				DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
					if(message.state != dragonStateHandler.treasureResting){
						dragonStateHandler.treasureRestTimer = 0;
						dragonStateHandler.treasureSleepTimer = 0;
					}
					dragonStateHandler.treasureResting = message.state;
				});
				
				NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncTreasureRestStatus(entity.getId(), message.state));
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void runClient(SyncTreasureRestStatus message, Supplier<NetworkEvent.Context> supplier){
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			PlayerEntity thisPlayer = Minecraft.getInstance().player;
			if (thisPlayer != null) {
				World world = thisPlayer.level;
				Entity entity = world.getEntity(message.playerId);
				if (entity instanceof PlayerEntity) {
					DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
						if(message.state != dragonStateHandler.treasureResting){
							dragonStateHandler.treasureRestTimer = 0;
							dragonStateHandler.treasureSleepTimer = 0;
						}
						dragonStateHandler.treasureResting = message.state;
					});
				}
			}
			context.setPacketHandled(true);
		});
	}
}