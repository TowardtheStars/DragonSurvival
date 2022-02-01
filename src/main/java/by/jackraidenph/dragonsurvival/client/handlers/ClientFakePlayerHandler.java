package by.jackraidenph.dragonsurvival.client.handlers;

import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayer;
import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayerUtils;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.TimeUnit;

public class ClientFakePlayerHandler
{
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event){
        FakeClientPlayerUtils. fakePlayers.forEach((i, v) -> {
            if (System.currentTimeMillis() - ((FakeClientPlayer)v).lastAccessed >= TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)) {
                ((FakeClientPlayer)v).remove();
                FakeClientPlayerUtils.fakeDragons.get(i).remove();

                FakeClientPlayerUtils.fakeDragons.remove(i);
                FakeClientPlayerUtils.fakePlayers.remove(i);
            }
        });
    }
}
