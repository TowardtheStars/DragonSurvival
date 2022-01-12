package by.jackraidenph.dragonsurvival.api.jei;

import by.jackraidenph.dragonsurvival.client.gui.DragonScreen;
import by.jackraidenph.dragonsurvival.server.containers.DragonContainer;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DragonInventoryGUIHandler implements IRecipeTransferInfo<DragonContainer>, IGuiContainerHandler<DragonScreen>
{
	@Nonnull
	@Override
	public Class<DragonContainer> getContainerClass()
	{
		return DragonContainer.class;
	}
	
	@Nonnull
	@Override
	public ResourceLocation getRecipeCategoryUid()
	{
		return VanillaRecipeCategoryUid.CRAFTING;
	}
	
	@Override
	public boolean canHandle(@Nonnull DragonContainer container)
	{
		return true;
	}
	
	@Nonnull
	@Override
	public List<Slot> getRecipeSlots(@Nonnull DragonContainer container)
	{
		return container.craftingSlots;
	}
	
	@Nonnull
	@Override
	public List<Slot> getInventorySlots(@Nonnull DragonContainer container)
	{
		return container.inventorySlots;
	}
	
	@Nonnull
	@Override
	public List<Rectangle2d> getGuiExtraAreas(DragonScreen screen)
	{
		List<Rectangle2d> list = new ArrayList<>();
		if(screen.clawsMenu){
			int size = 80;
			list.add(new Rectangle2d(screen.getLeftPos() - size, screen.getGuiTop() - 70, size, screen.height + 70));
		}
		return list;
	}
}
