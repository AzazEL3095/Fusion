package alexndr.plugins.Fusion;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import alexndr.api.content.blocks.SimpleBlock;
import alexndr.api.content.items.SimpleArmor;
import alexndr.api.content.items.SimpleAxe;
import alexndr.api.content.items.SimpleHoe;
import alexndr.api.content.items.SimpleItem;
import alexndr.api.content.items.SimplePickaxe;
import alexndr.api.content.items.SimpleShovel;
import alexndr.api.content.items.SimpleSword;
import alexndr.api.core.ContentRegistry;
import alexndr.api.core.ContentTypes;
import alexndr.api.core.LogHelper;
import alexndr.api.helpers.game.TabHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * @author AleXndrTheGr8st
 */
public class Content 
{
	private static boolean simpleores = Loader.isModLoaded("simpleores") && Settings.enableSimpleOres.asBoolean();
	private static boolean netherrocks = Loader.isModLoaded("netherrocks") && Settings.enableNetherrocks.asBoolean();
	
	/**
	 * Loads all the Fusion content, by calling the methods below.
	 */
	public static void preInitialize()
	{
		try{doItems(); LogHelper.verboseInfo("Fusion", "All items were added successfully");}
		catch(Exception e){LogHelper.severe("Fusion", "Items were not added successfully. This is a serious problem!"); e.printStackTrace();}
		try{doBlocks(); LogHelper.verboseInfo("Fusion", "All blocks were added successfully");}
		catch(Exception e){LogHelper.severe("Fusion", "Blocks were not added successfully. This is a serious problem!"); e.printStackTrace();}
		try{doTools(); LogHelper.verboseInfo("Fusion", "All tools were added successfully");}
		catch(Exception e){LogHelper.severe("Fusion", "Tools were not added successfully. This is a serious problem!"); e.printStackTrace();}
		try{doArmor(); LogHelper.verboseInfo("Fusion", "All armor was added successfully");}
		catch(Exception e){LogHelper.severe("Fusion", "Armor was not added successfully. This is a serious problem!"); e.printStackTrace();}
		try{doAchievements(); LogHelper.verboseInfo("Fusion", "All achievements were added successfully");}
		catch(Exception e){LogHelper.severe("Fusion", "Achievements were not added successfully. This is a serious problem!"); e.printStackTrace();}
	}
	
	/**
	 * Sets the tabs that the blocks/items will be in. Called during Init phase.
	 */
	public static void initialize()
	{
		try{setTabs(); LogHelper.verboseInfo("Fusion", "Successfully set tabs for all blocks/items");}
		catch(Exception e){LogHelper.severe("Fusion", "Tabs were not successfully set for blocks/items. This is a serious problem!"); e.printStackTrace();}
	}
	
	/**
	 * Loads Fusion Items.
	 */
	public static void doArmor()
	{
		steel_helmet = new SimpleArmor(Fusion.armorSteel, 0).modId("fusion").setType("steel").setUnlocalizedName("steel_helmet");
		steel_chestplate = new SimpleArmor(Fusion.armorSteel, 1).modId("fusion").setType("steel").setUnlocalizedName("steel_chestplate");
		steel_leggings = new SimpleArmor(Fusion.armorSteel, 2).modId("fusion").setType("steel").setUnlocalizedName("steel_leggings");
		steel_boots = new SimpleArmor(Fusion.armorSteel, 3).modId("fusion").setType("steel").setUnlocalizedName("steel_boots");
		
		if(simpleores)
			ContentSimpleOres.doArmor();
	}
	
	/**
	 * Loads Fusion Blocks.
	 */
	public static void doBlocks()
	{
		steel_block = new SimpleBlock(Material.iron).modId("fusion").setConfigValues(Settings.steelBlock).setBlockName("steel_block");
		
		fusion_furnace = new BlockFusionFurnace(false).setHardness(Settings.fusionFurnace.getHardness()).setResistance(Settings.fusionFurnace.getResistance()).setBlockName("fusion_furnace");
		fusion_furnace_lit = new BlockFusionFurnace(true).setHardness(Settings.fusionFurnace.getHardness()).setResistance(Settings.fusionFurnace.getResistance()).setLightLevel(Settings.fusionFurnace.getLightValue()).setBlockName("fusion_furnace_lit");
		
		//Block Registering
		GameRegistry.registerBlock(fusion_furnace, "fusion_furnace");
		GameRegistry.registerBlock(fusion_furnace_lit, "fusion_furnace_lit");
		ContentRegistry.registerBlock(fusion_furnace, "fusion_furnace", "fusion", ContentTypes.Block.MACHINE);
		
		if(simpleores)
			ContentSimpleOres.doBlocks();
	}
	
	/**
	 * Loads Fusion Tools.
	 */
	public static void doItems()
	{
		steel_ingot = new SimpleItem().modId("fusion").isIngot().setUnlocalizedName("steel_ingot");
		small_steel_chunk = new SimpleItem().modId("fusion").isIngot().setUnlocalizedName("small_steel_chunk");
		medium_steel_chunk = new SimpleItem().modId("fusion").isIngot().setUnlocalizedName("medium_steel_chunk");
		large_steel_chunk = new SimpleItem().modId("fusion").isIngot().setUnlocalizedName("large_steel_chunk");
		
		if(simpleores)
			ContentSimpleOres.doItems();
	}
	
	/**
	 * Loads Fusion Armor.
	 */
	public static void doTools()
	{
		steel_pickaxe = new SimplePickaxe(Fusion.toolSteel).modId("fusion").setUnlocalizedName("steel_pickaxe");
		steel_axe = new SimpleAxe(Fusion.toolSteel).modId("fusion").setUnlocalizedName("steel_axe");
		steel_shovel = new SimpleShovel(Fusion.toolSteel).modId("fusion").setUnlocalizedName("steel_shovel");
		steel_sword = new SimpleSword(Fusion.toolSteel).modId("fusion").setUnlocalizedName("steel_sword");
		steel_hoe = new SimpleHoe(Fusion.toolSteel).modId("fusion").setUnlocalizedName("steel_hoe");
		
		if(simpleores)
			ContentSimpleOres.doTools();
	}
	
	/**
	 * Loads Fusion Achievements.
	 */
	public static void doAchievements()
	{
		fusionAch = new Achievement("achievement.fusionAch", "fusionAch", 9, 7, fusion_furnace, AchievementList.buildFurnace).setSpecial().registerStat();
		steelAch = new Achievement("achievement.steelAch", "steelAch", 8, 9, steel_ingot, fusionAch).registerStat();
		steelChestplateAch = new Achievement("achievement.steelChestplateAch", "steelChestplateAch", 8, 11, steel_chestplate, steelAch).registerStat();

		if(simpleores)
			ContentSimpleOres.doAchievements();
	}
	
	/**
	 * Sets tabs for Fusion content.
	 */
	public static void setTabs()
	{
		fusion_furnace.setCreativeTab(TabHelper.decorationsTab());
		for(Item armor : ContentRegistry.getItemListFromModId("fusion", ContentTypes.Item.ARMOR))
			armor.setCreativeTab(TabHelper.combatTab());
		for(Block block : ContentRegistry.getBlockListFromModId("fusion"))
			block.setCreativeTab(TabHelper.decorationsTab());
		for(Block ore : ContentRegistry.getBlockListFromModId("fusion", ContentTypes.Block.ORE))
			ore.setCreativeTab(TabHelper.blocksTab());
		for(Item item : ContentRegistry.getItemListFromModId("fusion", ContentTypes.Item.INGOT))
			item.setCreativeTab(TabHelper.materialsTab());
		for(Item tool : ContentRegistry.getItemListFromModId("fusion", ContentTypes.Item.TOOL))
			tool.setCreativeTab(TabHelper.toolsTab());
		for(Item weapon : ContentRegistry.getItemListFromModId("fusion", ContentTypes.Item.WEAPON))
			weapon.setCreativeTab(TabHelper.combatTab());
	}
	
	//Armor
	public static Item steel_helmet;
	public static Item steel_chestplate;
	public static Item steel_leggings;
	public static Item steel_boots;
	
	//Blocks
	public static Block steel_block;
	public static Block fusion_furnace, fusion_furnace_lit;
	
	//Items
	public static Item steel_ingot;
	public static Item small_steel_chunk;
	public static Item medium_steel_chunk;
	public static Item large_steel_chunk;
	
	//Tools
	public static Item steel_pickaxe;
	public static Item steel_axe;
	public static Item steel_shovel;
	public static Item steel_sword;
	public static Item steel_hoe;
	
	//Achievements
	public static Achievement fusionAch, steelAch, steelChestplateAch;
}
