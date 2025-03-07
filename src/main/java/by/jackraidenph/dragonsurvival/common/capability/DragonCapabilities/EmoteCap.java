package by.jackraidenph.dragonsurvival.common.capability.DragonCapabilities;

import by.jackraidenph.dragonsurvival.common.capability.DragonStateHandler;
import by.jackraidenph.dragonsurvival.client.emotes.Emote;
import by.jackraidenph.dragonsurvival.client.emotes.EmoteRegistry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Objects;

public class EmoteCap implements DragonCapability
{
	private Emote currentEmote;
	public int emoteTick;
	
	public String serverEmote;
	public int serverTick;
	
	public boolean emoteMenuOpen = false;
	
	public Emote getCurrentEmote()
	{
		return currentEmote;
	}
	
	public void setCurrentEmote(Emote currentEmote)
	{
		this.currentEmote = currentEmote;
		emoteTick = 0;
	}
	
	@Override
	public INBT writeNBT(Capability<DragonStateHandler> capability, Direction side)
	{
		CompoundNBT tag = new CompoundNBT();
		
		tag.putString("emote", getCurrentEmote() != null ? getCurrentEmote().animation : "nil");
		tag.putBoolean("emoteOpen", emoteMenuOpen);
		
		return tag;
	}
	
	@Override
	public void readNBT(Capability<DragonStateHandler> capability, Direction side, INBT base)
	{
		CompoundNBT tag = (CompoundNBT) base;
		
		String emoteId = tag.getString("emote");
		
		if(!emoteId.equals("nil")){
			for(Emote emote : EmoteRegistry.EMOTES){
				if(Objects.equals(emote.animation, emoteId)){
					setCurrentEmote(emote);
					break;
				}
			}
		}
		
		emoteMenuOpen = tag.getBoolean("emoteOpen");
	}
	
	@Override
	public void clone(DragonStateHandler oldCap)
	{
		emoteMenuOpen = oldCap.getEmotes().emoteMenuOpen;
		
		currentEmote = oldCap.getEmotes().getCurrentEmote();
		emoteTick = oldCap.getEmotes().emoteTick;
		
		serverEmote = oldCap.getEmotes().serverEmote;
		serverTick = oldCap.getEmotes().serverTick;
	}
}
