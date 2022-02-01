package by.jackraidenph.dragonsurvival.config;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = DragonSurvivalMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {

	public static final ClientConfig CLIENT;
	public static final ForgeConfigSpec clientSpec;
	public static final CommonConfig COMMON;
	public static final ForgeConfigSpec commonSpec;
	public static final ServerConfig SERVER;
	public static final ForgeConfigSpec serverSpec;

	public static final Predicate<Object> generatePredicate(IForgeRegistry<?> type)
	{
		return (obj) ->
		{
			try
			{
				String text = String.valueOf(obj);
				String[] itemSplit = text.split(":");
				if (itemSplit.length >= 3 && itemSplit[0].equalsIgnoreCase("item"))
				{
					return Optional.of(new ResourceLocation(itemSplit[1], itemSplit[2])).isPresent();
				} else if (itemSplit.length == 2)
				{
					ResourceLocation location = ResourceLocation.tryParse(text);
					return location != null && type.containsKey(location);
				}
			} catch (Exception ignored)
			{
			}
			return false;
		};
	}
	
	public static final Predicate<Object> biomePredicate = generatePredicate(ForgeRegistries.BIOMES);
	
	public static final Predicate<Object> effectPredicate = generatePredicate(ForgeRegistries.POTIONS);
	
	public static final Predicate<Object> entityPredicate = generatePredicate(ForgeRegistries.ENTITIES);
	
	public static final Predicate<Object> itemPredicate = generatePredicate(ForgeRegistries.ITEMS);
	
	public static final Predicate<Object> blockPredicate = generatePredicate(ForgeRegistries.BLOCKS);
	
	public static final Predicate<Object> tagPredicate = (obj) -> {
		try {
			String text = String.valueOf(obj);
			String[] itemSplit = text.split(":");
			if (itemSplit.length >= 3 && itemSplit[0].equalsIgnoreCase("tag")) {
				return Optional.of(new ResourceLocation(itemSplit[1], itemSplit[2])).isPresent();
			}else if(itemSplit.length == 2){
				ResourceLocation location = ResourceLocation.tryParse(text);
				return location != null && (BlockTags.getAllTags().getTag(location) != null || ItemTags.getAllTags().getTag(location) != null || EntityTypeTags.getAllTags().getTag(location) != null);
			}
		}catch (Exception ignored){}
		return false;
	};
	
	public static final Predicate<Object> resourcePredicate = (obj) -> {
		try{
			String text = String.valueOf(obj);
			String[] itemSplit = text.split(":");
			if(itemSplit.length >= 3){
				return Optional.of(new ResourceLocation(itemSplit[1], itemSplit[2])).isPresent();
			}else if(itemSplit.length == 2){
				return Optional.of(new ResourceLocation(itemSplit[0], itemSplit[1])).isPresent();
			}
		}catch (Exception ignored){}
		return false;
	};
	
	public static final Predicate<Object> blocksAndTagsPredicate = blockPredicate.or(tagPredicate);
	public static final Predicate<Object> itemsAndTagsPredicate = itemPredicate.or(tagPredicate);
	public static final Predicate<Object> entitiesAndTagsPredicate = entityPredicate.or(tagPredicate);
	
	public static boolean isResourcePredicate(Predicate<Object> obj){
		return testPredicates(obj, resourcePredicate, true);
	}
	
	public static boolean isEntityPredicate(Predicate<Object> obj){
		return testPredicates(obj, entityPredicate, false);
	}
	
	public static boolean isTagPredicate(Predicate<Object> obj){
		return testPredicates(obj, tagPredicate, false);
	}
	
	public static boolean isEffectPredicate(Predicate<Object> obj){
		return testPredicates(obj, effectPredicate, false);
	}
	
	public static boolean isBiomePredicate(Predicate<Object> obj){
		return testPredicates(obj, biomePredicate, false);
	}
	
	public static boolean isItemPredicate(Predicate<Object> obj){
		return testPredicates(obj, itemPredicate, false);
	}
	
	public static boolean isBlockPredicate(Predicate<Object> obj){
		return testPredicates(obj, blockPredicate, false);
	}
	
	private static boolean testPredicates(Predicate<Object> obj, Predicate<Object> baselinePred, boolean lenient)
	{
		String[] tests = new String[]{"item:minecraft:dirt", "block:minecraft:dirt", "tag:forge:ores",
		                              "minecraft:dirt", "entity:minecraft:dirt", "effect:minecraft:dirt", "minecraft:plains",
		                              "biome:minecraft:dirt"
		};
		
		ArrayList<String> testedKeys = new ArrayList<>();
		
		for(String t : tests){
			if(baselinePred.test(t)){
				testedKeys.add(t);
			}
		}
		if(!lenient) {
			for (String curTest : testedKeys) {
				if (!obj.test(Collections.singletonList(curTest))) {
					return false;
				}
			}
		}else{
			for (String curTest : tests) {
				boolean val1 = baselinePred.test(curTest);
				boolean val2 = obj.test(Collections.singletonList(curTest));

				if(val2 && !val1){
					return false;
				}
			}
		}
		return true;
	}
	
	static {
		final Pair<ClientConfig, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT = client.getLeft();
		clientSpec = client.getRight();
		final Pair<CommonConfig, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON = common.getLeft();
		commonSpec = common.getRight();
		final Pair<ServerConfig, ForgeConfigSpec> server = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
		SERVER = server.getLeft();
		serverSpec = server.getRight();
	}
}
