package by.jackraidenph.dragonsurvival.data.generator;

import by.jackraidenph.dragonsurvival.data.tags.DSEntityTypeTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class DSEntityTypeGenerator extends EntityTypeTagsProvider
{
    public DSEntityTypeGenerator(DataGenerator pGenerator, String modId, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(pGenerator, modId, existingFileHelper);
    }

    @Override
    protected void addTags()
    {
        tag(DSEntityTypeTags.STORM_BREATH_CANNOT_CHARGE)
                .addOptional(new ResourceLocation("upgrade_aquatic:thrasher"))
                .addOptional(new ResourceLocation("upgrade_aquatic:great_thrasher"));
        tag(DSEntityTypeTags.STORM_BREATH_CANNOT_SPREAD)
                .add(EntityType.ARMOR_STAND);
    }
}
