package by.jackraidenph.dragonsurvival.common.tags;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class DSEntityTypeTags
{
    public static final Tags.IOptionalNamedTag<EntityType<?>> STORM_BREATH_CANNOT_CHARGE;

    static {
        STORM_BREATH_CANNOT_CHARGE = tag("storm_breath_cannot_charge");
    }
    private static Tags.IOptionalNamedTag<EntityType<?>> tag(String... name)
    {
        return EntityTypeTags.createOptional(new ResourceLocation(DragonSurvivalMod.MODID,
                String.join("/", name)
                ));
    }
}
