package by.jackraidenph.dragonsurvival.common.entity.creatures.hitbox;

import by.jackraidenph.dragonsurvival.common.handlers.DragonHitboxHandler;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nonnull;

public class DragonHitboxPart extends PartEntity<DragonHitBox>
{
	public EntitySize size;
	@Nonnull
	public final DragonHitBox parentMob;
	public String name;
	
	public DragonHitboxPart(@Nonnull DragonHitBox parent, String name, float sizeX, float sizeY)
	{
		super(parent);
		this.size = EntitySize.scalable(sizeX, sizeY);
		this.parentMob = parent;
		this.name = name;
	}
	
	@Override
	public boolean isInvulnerableTo(@Nonnull DamageSource pSource)
	{
		return DragonHitboxHandler.isThisPlayer(this, pSource.getEntity()) || super.isInvulnerableTo(pSource) || pSource == DamageSource.IN_WALL;
	}
	
	@Override
	protected void defineSynchedData() {}
	
	@Override
	protected void readAdditionalSaveData(@Nonnull CompoundNBT p_70037_1_) {}
	
	@Override
	protected void addAdditionalSaveData(@Nonnull CompoundNBT p_213281_1_) {}
	
	public boolean hurt(@Nonnull DamageSource pSource, float pAmount) {
		return !this.isInvulnerableTo(pSource) && this.parentMob.hurt(this, pSource, pAmount);
	}
	
	@Override
	public boolean isPickable()
	{
		return parentMob.isPickable();
	}
	
	@Nonnull
	@Override
	public Vector3d getDeltaMovement()
	{
		return parentMob.getDeltaMovement();
	}
	
	@Nonnull
	public EntitySize getDimensions(@Nonnull Pose p_213305_1_) {
		return this.size;
	}
}
