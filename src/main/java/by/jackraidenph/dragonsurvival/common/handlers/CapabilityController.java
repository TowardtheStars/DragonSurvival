package by.jackraidenph.dragonsurvival.common.handlers;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.Capabilities;
import by.jackraidenph.dragonsurvival.common.capability.Capabilities.GenericCapabilityProvider;
import by.jackraidenph.dragonsurvival.common.capability.Capabilities.VillageRelationshipsProvider;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitBox;
import by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox.DragonHitboxPart;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.status.DiggingStatus;
import by.jackraidenph.dragonsurvival.network.status.RefreshDragons;
import by.jackraidenph.dragonsurvival.network.entity.player.SynchronizeDragonCap;
import by.jackraidenph.dragonsurvival.common.DragonEffects;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import by.jackraidenph.dragonsurvival.common.EffectInstance2;
import by.jackraidenph.dragonsurvival.client.util.FakeClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Optional;

@EventBusSubscriber
public class CapabilityController {
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(new ResourceLocation("dragonsurvival", "generic_capability_data"), new GenericCapabilityProvider());
    
        if (event.getObject() instanceof PlayerEntity ) {
            event.addCapability(new ResourceLocation("dragonsurvival", "playerstatehandler"), new DragonStateProvider());
            event.addCapability(new ResourceLocation("dragonsurvival", "village_relations"), new VillageRelationshipsProvider());
            DragonSurvivalMod.LOGGER.info("Successfully attached capabilities to the " + event.getObject().getClass().getSimpleName());
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e) {
        PlayerEntity player = e.getPlayer();
        PlayerEntity original = e.getOriginal();
        
        DragonStateProvider.getCap(player).ifPresent(capNew ->
                DragonStateProvider.getCap(original).ifPresent(capOld -> {
                    if (capOld.isDragon()) {
                        DragonStateHandler.DragonMovementData movementData = capOld.getMovementData();
                        capNew.setMovementData(movementData.bodyYaw, movementData.headYaw, movementData.headPitch, movementData.bite);
                        
                        capNew.altarCooldown = capOld.altarCooldown;
                        capNew.hasUsedAltar = capOld.hasUsedAltar;
                        
                        capNew.getMovementData().spinCooldown = capOld.getMovementData().spinCooldown;
                        capNew.getMovementData().spinLearned = capOld.getMovementData().spinLearned;
                        
                        capNew.growing = capOld.growing;
                        
                        capNew.forestSize = capOld.forestSize;
                        capNew.caveSize = capOld.caveSize;
                        capNew.seaSize = capOld.seaSize;
    
                        capNew.setHasWings(capOld.hasWings());
                        capNew.setWingsSpread(capOld.isWingsSpread());
                        
                        capNew.forestWings = capOld.forestWings;
                        capNew.caveWings = capOld.caveWings;
                        capNew.seaWings = capOld.seaWings;
                        
                        capNew.getClawInventory().clone(capOld);
                        capNew.getEmotes().clone(capOld);
                        capNew.getMagic().clone(capOld);
                        capNew.getSkin().clone(capOld);
    
                        capNew.setSize(capOld.getSize());
                        capNew.setType(capOld.getType());
                        capNew.setLavaAirSupply(ConfigHandler.SERVER.caveLavaSwimmingTicks.get());
                        
                        DragonStateHandler.updateModifiers(original, player);

                        player.refreshDimensions();
                    }
                }));
        
        Capabilities.getVillageRelationships(player).ifPresent(villageRelationShips -> {
            Capabilities.getVillageRelationships(original).ifPresent(old -> {
                villageRelationShips.evilStatusDuration = old.evilStatusDuration;
                villageRelationShips.crimeLevel = old.crimeLevel;
                villageRelationShips.hunterSpawnDelay = old.hunterSpawnDelay;
                if (ConfigHandler.COMMON.preserveEvilDragonEffectAfterDeath.get() && villageRelationShips.evilStatusDuration > 0) {
                    player.addEffect(new EffectInstance2(DragonEffects.EVIL_DRAGON, villageRelationShips.evilStatusDuration));
                }
            });
        });

    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent playerTickEvent) {
        if (playerTickEvent.phase != TickEvent.Phase.START)
            return;
        PlayerEntity playerEntity = playerTickEvent.player;
        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
            if (dragonStateHandler.isDragon()) {
                if (playerEntity instanceof ServerPlayerEntity) {
                    PlayerInteractionManager interactionManager = ((ServerPlayerEntity) playerEntity).gameMode;
                    boolean isMining = interactionManager.isDestroyingBlock && playerEntity.swinging;
                    
                    if(isMining != dragonStateHandler.getMovementData().dig) {
                        dragonStateHandler.getMovementData().dig = isMining;
                        NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> playerEntity), new DiggingStatus(playerEntity.getId(), isMining));
                    }
                }
            }
        });
    }

    /**
     * Mounting a dragon
     */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        Entity ent = event.getEntity();
        
        if(ent instanceof DragonHitBox){
            ent = ((DragonHitBox)ent).player;
        }else if(ent instanceof DragonHitboxPart){
            ent = (((DragonHitboxPart)ent).parentMob).player;
        }
        
        if (!(ent instanceof PlayerEntity) || event.getHand() != Hand.MAIN_HAND)
            return;
        PlayerEntity target = (PlayerEntity) ent;
        PlayerEntity self = event.getPlayer();
        DragonStateProvider.getCap(target).ifPresent(targetCap -> {
            if (targetCap.isDragon() && target.getPose() == Pose.CROUCHING && targetCap.getSize() >= 40 && !target.isVehicle()) {
                DragonStateProvider.getCap(self).ifPresent(selfCap -> {
                    if (!selfCap.isDragon() || selfCap.getLevel() == DragonLevel.BABY) {
                        if (event.getTarget() instanceof ServerPlayerEntity) {
                            self.startRiding(target);
                            ((ServerPlayerEntity) event.getTarget()).connection.send(new SSetPassengersPacket(target));
                            targetCap.setPassengerId(self.getId());
                            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target), new SynchronizeDragonCap(target.getId(), targetCap.isHiding(), targetCap.getType(), targetCap.getSize(), targetCap.hasWings(), targetCap.getLavaAirSupply(), self.getId()));
                        }
                        event.setCancellationResult(ActionResultType.SUCCESS);
                        event.setCanceled(true);
                    }
                });
            }
        });
    }

    @SubscribeEvent
    public static void onServerPlayerTick(TickEvent.PlayerTickEvent event) { // TODO: Find a better way of doing this.
        if (!(event.player instanceof ServerPlayerEntity))
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.player;
        DragonStateProvider.getCap(player).ifPresent(dragonStateHandler -> {
            int passengerId = dragonStateHandler.getPassengerId();
            Entity passenger = player.level.getEntity(passengerId);
            boolean flag = false;
            if (!dragonStateHandler.isDragon() && player.isVehicle() && player.getPassengers().get(0) instanceof ServerPlayerEntity) {
                flag = true;
                player.getPassengers().get(0).stopRiding();
                player.connection.send(new SSetPassengersPacket(player));
            } else if (player.isSpectator() && passenger != null && player.getPassengers().get(0) instanceof ServerPlayerEntity) {
                flag = true;
                player.getPassengers().get(0).stopRiding();
                player.connection.send(new SSetPassengersPacket(player));
            } else if (dragonStateHandler.isDragon() && dragonStateHandler.getSize() < 40 && player.isVehicle() && player.getPassengers().get(0) instanceof ServerPlayerEntity) {
                flag = true;
                player.getPassengers().get(0).stopRiding();
                player.connection.send(new SSetPassengersPacket(player));
            } else if (player.isSleeping() && player.isVehicle() && player.getPassengers().get(0) instanceof ServerPlayerEntity) {
                flag = true;
                player.getPassengers().get(0).stopRiding();
                player.connection.send(new SSetPassengersPacket(player));
            }
            if (passenger instanceof ServerPlayerEntity) {
                Optional<DragonStateHandler> passengerCap = DragonStateProvider.getCap(passenger)
                        .filter(DragonStateHandler::isDragon)
                        .filter(cap->cap.getLevel() != DragonLevel.BABY);
                if (passengerCap.isPresent()) {
                    flag = true;
                    passenger.stopRiding();
                    player.connection.send(new SSetPassengersPacket(player));
                } else if (passenger.getRootVehicle() != player.getRootVehicle()) {
                    flag = true;
                    passenger.stopRiding();
                    player.connection.send(new SSetPassengersPacket(player));
                }
            }
            if (flag || passenger == null || !player.hasPassenger(passenger) || passenger.isSpectator() || player.isSpectator()) {
                dragonStateHandler.setPassengerId(0);
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SynchronizeDragonCap(player.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
            }

        });
    }

    @SubscribeEvent
    public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        if (player.getVehicle() == null || !(player.getVehicle() instanceof ServerPlayerEntity))
            return;
        ServerPlayerEntity vehicle = (ServerPlayerEntity) player.getVehicle();
        DragonStateProvider.getCap(player).ifPresent(playerCap -> {
            DragonStateProvider.getCap(vehicle).ifPresent(vehicleCap -> {
                player.stopRiding();
                vehicle.connection.send(new SSetPassengersPacket(vehicle));
                vehicleCap.setPassengerId(0);
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> vehicle), new SynchronizeDragonCap(player.getId(), vehicleCap.isHiding(), vehicleCap.getType(), vehicleCap.getSize(), vehicleCap.hasWings(), vehicleCap.getLavaAirSupply(), 0));
            });
        });
    }

    @SubscribeEvent
    public static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent changedDimensionEvent) {
        PlayerEntity playerEntity = changedDimensionEvent.getPlayer();
        DragonStateProvider.getCap(playerEntity).ifPresent(dragonStateHandler -> {
            NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SynchronizeDragonCap(playerEntity.getId(), dragonStateHandler.isHiding(), dragonStateHandler.getType(), dragonStateHandler.getSize(), dragonStateHandler.hasWings(), dragonStateHandler.getLavaAirSupply(), 0));
            NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new RefreshDragons(playerEntity.getId()));
        });
    }
}
