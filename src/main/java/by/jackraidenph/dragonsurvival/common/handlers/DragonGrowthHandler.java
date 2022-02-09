package by.jackraidenph.dragonsurvival.common.handlers;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import by.jackraidenph.dragonsurvival.common.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.config.ConfigHandler;
import by.jackraidenph.dragonsurvival.data.DSTags;
import by.jackraidenph.dragonsurvival.network.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.entity.player.SyncSize;
import by.jackraidenph.dragonsurvival.common.items.DSItems;
import by.jackraidenph.dragonsurvival.misc.DragonLevel;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID)
public class DragonGrowthHandler {
    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        PlayerEntity player = event.getPlayer();
        World world = player.getCommandSenderWorld();

        DragonStateProvider.getCap(player).ifPresent(handler -> {
            if (!handler.isDragon())
                return;

            double size = handler.getSize();

            if (size >= ConfigHandler.SERVER.maxGrowthSize.get())
                return;

            boolean canContinue = false;
            Tags.IOptionalNamedTag<Item> tag = DSTags.Items.GROW_ADULT;
            List<Tags.IOptionalNamedTag<Item>> otherTag = Lists.newArrayList();

            switch (handler.getLevel())
            {
                case BABY:
                    tag = DSTags.Items.GROW_NEWBORN;
                    otherTag.add(DSTags.Items.GROW_YOUNG);
                    otherTag.add(DSTags.Items.GROW_ADULT);
                    break;
                case YOUNG:
                    tag = DSTags.Items.GROW_YOUNG;
                    otherTag.add(DSTags.Items.GROW_ADULT);
                    otherTag.add(DSTags.Items.GROW_NEWBORN);
                    break;
                case ADULT:
                    tag = DSTags.Items.GROW_ADULT;
                    otherTag.add(DSTags.Items.GROW_YOUNG);
                    otherTag.add(DSTags.Items.GROW_NEWBORN);
                    break;
            }
            canContinue = tag.contains(item);

//            List<Item> newbornList = DSItemTags.GROW_NEWBORN.getValues();
//            List<Item> youngList = ConfigUtils.parseConfigItemList(ConfigHandler.SERVER.growYoung.get());
//            List<Item> adultList = ConfigUtils.parseConfigItemList(ConfigHandler.SERVER.growAdult.get());
//
//            List<Item> allowedItems = new ArrayList<>();
//
//            switch (handler.getLevel()) {
//                case BABY:
//                    if (newbornList.contains(item))
//                        canContinue = true;
//                    else if (youngList.contains(item) || adultList.contains(item))
//                        allowedItems = newbornList;
//
//                    break;
//                case YOUNG:
//                    if (youngList.contains(item))
//                        canContinue = true;
//                    else if (newbornList.contains(item) || adultList.contains(item))
//                        allowedItems = youngList;
//
//                    break;
//                case ADULT:
//                    if (adultList.contains(item))
//                        canContinue = true;
//                    else if (newbornList.contains(item) || youngList.contains(item))
//                        allowedItems = adultList;
//
//                    break;
//            }

            if (!canContinue) {
                if (otherTag.stream().anyMatch(ptag -> ptag.contains(item)) && world.isClientSide()) {
                    List<String> displayData = tag.getValues().stream()
                            .map(i -> new ItemStack(i).getDisplayName().getString())
                            .collect(Collectors.toList());
                    StringBuilder result = new StringBuilder();

                    for (int i = 0; i < displayData.size(); i++) {
                        String entry = displayData.get(i);

                        result.append(entry).append(i + 1 < displayData.size() ? ", " : "");
                    }

                    player.displayClientMessage(new TranslationTextComponent("ds.invalid_grow_item", result), false);
                }

                return;
            }
    
            int increment = getIncrement(item, handler.getLevel());
            size += increment;
            handler.setSize(size, player);
            
            if(!player.isCreative())
            event.getItemStack().shrink(1);

            if (world.isClientSide)
                return;

            NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncSize(player.getId(), size));
            player.refreshDimensions();
        });
    }
    
    public static int getIncrement(Item item, DragonLevel level)
    {
        List<Item> newbornList = DSTags.Items.GROW_NEWBORN.getValues();
        List<Item> youngList = DSTags.Items.GROW_YOUNG.getValues();
        List<Item> adultList = DSTags.Items.GROW_ADULT.getValues();
        
        int increment = 0;
        
        if(item == DSItems.starBone){
            return -2;
        }
        
        switch (level) {
            case BABY:
                    if(adultList.contains(item)){
                        increment = 3;
                    }else if(youngList.contains(item)){
                        increment = 2;
                    }else if(newbornList.contains(item)){
                        increment = 1;
                    }
                break;
            case YOUNG:
                if(adultList.contains(item)){
                    increment = 2;
                }else if(youngList.contains(item)){
                    increment = 1;
                }
                break;
    
            case ADULT:
                if(adultList.contains(item)){
                    increment = 1;
                }
                break;
        }
        return increment;
    }
    
    public static long newbornToYoung = TimeUnit.SECONDS.convert(3, TimeUnit.HOURS);
    public static long youngToAdult = TimeUnit.SECONDS.convert(15, TimeUnit.HOURS);
    public static long adultToMax = TimeUnit.SECONDS.convert(24, TimeUnit.HOURS);
    public static long beyond = TimeUnit.SECONDS.convert(30, TimeUnit.DAYS);
    
    @SubscribeEvent
    public static void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
        if (!ConfigHandler.SERVER.alternateGrowing.get())
            return;
        
        PlayerEntity player = event.player;
        World world = player.getCommandSenderWorld();
        
        if (world.isClientSide || event.phase == Phase.END)
            return;
        
        if(!DragonStateProvider.isDragon(player)) return;
        
        if (player.tickCount % (60 * 20) != 0)
            return;
    
        DragonStateProvider.getCap(player).ifPresent(handler -> {
            if(handler.growing) {
                /*
                    1. Newborn - young = 3-4 h
                    2. Young - adult = 15-20h
                    3. Adult - maximum growth = 24h
                    4. After maximum growth. = 30 days for max growth
                 */
            
                double d = 0;
                double timeIncrement = 60 * 20;
            
                if(handler.getSize() < DragonLevel.YOUNG.size){
                    d = (((DragonLevel.YOUNG.size - DragonLevel.BABY.size) / ((newbornToYoung * 20.0))) * timeIncrement) * ConfigHandler.SERVER.newbornGrowthModifier.get();
                
                }else if(handler.getSize() < DragonLevel.ADULT.size){
                    d = (((DragonLevel.ADULT.size - DragonLevel.YOUNG.size) / ((youngToAdult * 20.0))) * timeIncrement) * ConfigHandler.SERVER.youngGrowthModifier.get();
                
                }else if(handler.getSize() < 40){
                    d = (((40 - DragonLevel.ADULT.size) / ((adultToMax * 20.0))) * timeIncrement) * ConfigHandler.SERVER.adultGrowthModifier.get();
                }else{
                    d = (((60 - 40) / ((beyond * 20.0))) * timeIncrement) * ConfigHandler.SERVER.maxGrowthModifier.get();
                }
            
                double size = handler.getSize() + d;
                size = Math.min(size, ConfigHandler.SERVER.maxGrowthSize.get());
                
                if(handler.getSize() != size) {
                    handler.setSize(size, player);
    
                    NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncSize(player.getId(), size));
                    player.refreshDimensions();
                }
            }
        });
    }
    
}