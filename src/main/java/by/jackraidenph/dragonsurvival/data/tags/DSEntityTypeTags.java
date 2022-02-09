package by.jackraidenph.dragonsurvival.data.tags;

import by.jackraidenph.dragonsurvival.DragonSurvivalMod;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class DSEntityTypeTags
{
    public static final Tags.IOptionalNamedTag<EntityType<?>> STORM_BREATH_CANNOT_CHARGE;
    public static final Tags.IOptionalNamedTag<EntityType<?>> STORM_BREATH_CANNOT_SPREAD;

    static {
        STORM_BREATH_CANNOT_CHARGE = tag("storm_breath_cannot_charge");
        STORM_BREATH_CANNOT_SPREAD = tag("storm_breath_cannot_charge");
    }
    private static Tags.IOptionalNamedTag<EntityType<?>> tag(String... name)
    {
        return EntityTypeTags.createOptional(new ResourceLocation(DragonSurvivalMod.MODID,
                String.join("/", name)
                ));
    }
}
