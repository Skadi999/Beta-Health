package party.lemons.betahealth;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import party.lemons.betahealth.Config;

/**
 * Created by Sam on 6/04/2018.
 */
@Mod(modid="betahealth", name= "Beta Health", version = "1.0.3")
@Mod.EventBusSubscriber
public class BetaHealth
{
	public static Configuration config;
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        File directory = e.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "betahealth.cfg"));
        Config.readConfig();

    }
	//sets default stack size
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		ForgeRegistries.ITEMS.forEach(i -> {
			if(i instanceof ItemFood)
				i.setMaxStackSize(Config.stackValue);
		});
	}

	@SubscribeEvent
	public static void onItemRightClick(PlayerInteractEvent event)
	{
		if(event instanceof PlayerInteractEvent.LeftClickBlock || event instanceof PlayerInteractEvent.LeftClickEmpty)
			return;

		EntityPlayer player = event.getEntityPlayer();
		ItemStack stack = event.getItemStack();
		if(!stack.isEmpty() && stack.getItem() instanceof ItemFood)
		{
			Item item = stack.getItem();

			if(item instanceof ItemSeedFood)
			{
				if(event.getFace() != null && event.getPos() != null && event.getFace() == EnumFacing.UP)
				{
					Block block = player.world.getBlockState(event.getPos()).getBlock();
					if(block == ((ItemSeedFood)item).soilId)
					{
						return;
					}
				}
			}
			if(item == Items.ROTTEN_FLESH)
			{
				event.setCanceled(true);
			}
			else
			{
				int amt = ((ItemFood)item).getHealAmount(stack);
				eat(amt, player);
				((ItemFood)item).onFoodEaten(stack, player.world, player);
				stack.shrink(1);
				event.setCanceled(true);				
			}
				
		}
	}

	@SubscribeEvent
	public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event)
	{
		//cake
		Block block = event.getEntityPlayer().world.getBlockState(event.getPos()).getBlock();
		if(block == Blocks.CAKE)
		{
			eat(2, event.getEntityPlayer());
		}
	}
	//the function that will execute when u eat an item
	public static void eat(int amt, EntityPlayer player)
	{
		player.heal(amt);
		player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
	}
	//sets default hunger amount
	@SubscribeEvent
	public static void setHunger(TickEvent.PlayerTickEvent event)
	{
		event.player.getFoodStats().setFoodLevel(Config.hungerValue);
	}
	//makes the hunger bar not display
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onHud(RenderGameOverlayEvent.Pre event)
	{
		GuiIngameForge.renderFood = false;
	}
}
