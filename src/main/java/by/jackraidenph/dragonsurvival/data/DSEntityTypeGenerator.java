package by.jackraidenph.dragonsurvival.data;

import by.jackraidenph.dragonsurvival.common.tags.DSEntityTypeTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraft.entity.EntityType;
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
                .add(EntityType.ARMOR_STAND);
    }
}
