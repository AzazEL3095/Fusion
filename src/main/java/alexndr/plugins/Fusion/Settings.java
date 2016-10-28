package alexndr.plugins.Fusion;

import java.io.File;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import alexndr.api.config.Configuration;
import alexndr.api.config.types.ConfigArmor;
import alexndr.api.config.types.ConfigBlock;
import alexndr.api.config.types.ConfigEntry;
import alexndr.api.config.types.ConfigFusionRecipe;
import alexndr.api.config.types.ConfigItem;
import alexndr.api.config.types.ConfigTool;
import alexndr.api.config.types.ConfigValue;
import alexndr.api.logger.LogHelper;

public class Settings 
{

	private static Configuration settings = new Configuration();

	public static void createOrLoadSettings(FMLPreInitializationEvent event) 
	{
		settings.setModName(ModInfo.NAME);
		File configDir = new File(event.getModConfigurationDirectory(), "AleXndr");
		File settingsFile = new File(configDir, "FusionSettings.xml");
		settings.setFile(settingsFile);

		LogHelper.verbose(ModInfo.NAME, "Loading Settings...");
		try {
			settings.load();
			settings.createHelpEntry(ModInfo.URL);

			// Toggles
			ConfigEntry toggles = new ConfigEntry("Fusion Toggles", "Toggles");
			toggles.createNewValue("EnableCustomRecipes").setDataType("@B")
					.setCurrentValue("false").setDefaultValue("false");
			toggles.createNewValue("EnableExtraChunkRecipes").setDataType("@B")
					.setCurrentValue("false").setDefaultValue("false");
			toggles = settings.get(toggles);
			customRecipes = toggles.getValueByName("EnableCustomRecipes");
			extraChunkRecipes = toggles
					.getValueByName("EnableExtraChunkRecipes");

			ConfigEntry contentToggles = new ConfigEntry("Content Toggles",
					"Toggles");
			contentToggles.createNewValue("EnableSimpleOres").setDataType("@B")
					.setCurrentValue("true").setDefaultValue("true");
			contentToggles.createNewValue("EnableNetherrocks").setDataType("@B").setCurrentValue("false")
					.setDefaultValue("false");
			contentToggles = settings.get(contentToggles);
			
			enableSimpleOres = contentToggles
					.getValueByName("EnableSimpleOres");
			enableNetherrocks = contentToggles
					.getValueByName("EnableNetherrocks");
			
			// CUSTOM RECIPES
			if (customRecipes.asBoolean() == true) 
			{
				ConfigEntry customRecipeConfig = new ConfigEntry(
						"Recipe Config", "CustomRecipes");
				customRecipeConfig.createNewValue("NumCustomRecipes")
						.setDataType("@I").setCurrentValue("1")
						.setDefaultValue("1");
				customRecipeConfig = settings.get(customRecipeConfig);
				numCustomRecipes = customRecipeConfig.getValueByName("NumCustomRecipes");
				customFusionRecipes = new ConfigFusionRecipe[numCustomRecipes.asInt()];
				
				for (int i = 0; i < numCustomRecipes.asInt(); i++) 
				{
					ConfigFusionRecipe recipe = 
							new ConfigFusionRecipe("Custom Recipe #" + (i + 1), "CustomRecipes");
					recipe.createNewValue("Input1").setDataType("@S")
						.setCurrentValue("minecraft:cobblestone").setDefaultValue("input1");
					recipe.createNewValue("Input2").setDataType("@S")
						.setCurrentValue("minecraft:flint").setDefaultValue("input2");
					recipe.createNewValue("Catalyst").setDataType("@S")
						.setCurrentValue("minecraft:stone").setDefaultValue("catalyst");
					recipe.createNewValue("Output").setDataType("@S")
						.setCurrentValue("minecraft:gravel").setDefaultValue("output");
					customFusionRecipes[i] = settings.get(recipe).asConfigFusionRecipe();
				} // end-for
			} // end-if
			else {
				customFusionRecipes = null;
				numCustomRecipes = null;
			}
			// Items
			steelIngot = settings.get(
					new ConfigItem("Steel Ingot", "Items").setStackSize(64)
							.setSmeltingXP(0.4F)).asConfigItem();
			bronzeIngot = settings.get(
					new ConfigItem("Bronze Ingot", "Items").setStackSize(64)
							.setSmeltingXP(0.4F)).asConfigItem();
			thyriumIngot = settings.get(
					new ConfigItem("Thyrium Ingot", "Items").setStackSize(64)
							.setSmeltingXP(0.4F)).asConfigItem();
			sinisiteIngot = settings.get(
					new ConfigItem("Sinisite Ingot", "Items").setStackSize(64)
							.setSmeltingXP(0.4F)).asConfigItem();

			// Blocks
			steelBlock = settings.get(
					new ConfigBlock("Steel Block", "Blocks").setHardness(7.0F)
							.setResistance(12.0F).setLightValue(0.0F)
							.setHarvestTool("pickaxe")
							.setBeaconBase(true)).asConfigBlock();
			bronzeBlock = settings.get(
					new ConfigBlock("Bronze Block", "Blocks").setHardness(7.0F)
							.setResistance(12.0F).setLightValue(0.0F)
							.setHarvestTool("pickaxe")
							.setBeaconBase(true)).asConfigBlock();
			thyriumBlock = settings.get(
					new ConfigBlock("Thyrium Block", "Blocks")
							.setHardness(7.0F).setResistance(12.0F)
							.setLightValue(0.0F).setHarvestTool("pickaxe")
							.setBeaconBase(true)).asConfigBlock();
			sinisiteBlock = settings.get(
					new ConfigBlock("Sinisite Block", "Blocks")
							.setHardness(7.0F).setResistance(12.0F)
							.setLightValue(0.0F).setHarvestTool("pickaxe")
							.setBeaconBase(true)).asConfigBlock();

			fusionFurnace = settings.get(
					new ConfigBlock("Fusion Furnace", "Machines")
							.setHardness(3.5F).setResistance(10.0F)
							.setLightValue(1.0F).setHarvestTool("pickaxe")
							.setCreativeTab("SimpleMachines")).asConfigBlock();

			// Tools
			steelTools = settings.get(
					new ConfigTool("Steel Tools", "Tools").setUses(700)
							.setHarvestLevel(2).setHarvestSpeed(7.5F)
							.setDamageVsEntity(3.0F).setEnchantability(24))
					.asConfigTool();
			bronzeTools = settings.get(
					new ConfigTool("Bronze Tools", "Tools").setUses(800)
							.setHarvestLevel(2).setHarvestSpeed(9.0F)
							.setDamageVsEntity(2.0F).setEnchantability(7))
					.asConfigTool();
			thyriumTools = settings.get(
					new ConfigTool("Thyrium Tools", "Tools").setUses(2000)
							.setHarvestLevel(3).setHarvestSpeed(22.0F)
							.setDamageVsEntity(6.0F).setEnchantability(28))
					.asConfigTool();
			sinisiteTools = settings.get(
					new ConfigTool("Sinisite Tools", "Tools").setUses(4100)
							.setHarvestLevel(5).setHarvestSpeed(18.0F)
							.setDamageVsEntity(8.0F).setEnchantability(11))
					.asConfigTool();

			thyriumBow = settings.get(new ConfigEntry("Thyrium Bow", "Bows"));
			thyriumBow.createNewValue("DamageModifier").setDataType("@F")
					.setCurrentValue("5.0").setDefaultValue("5.0");
			thyriumBow.createNewValue("ZoomAmount").setDataType("@F")
					.setCurrentValue("0.35").setDefaultValue("0.35");
			thyriumBowDamageModifier = thyriumBow
					.getValueByName("DamageModifier");
			thyriumBowZoomAmount = thyriumBow.getValueByName("ZoomAmount");

			sinisiteBow = settings.get(new ConfigEntry("Sinisite Bow", "Bows"));
			// .setComment("The damage multiplier of the Sinisite Bow (versus vanilla bow).").setCommentIndentNumber(2);
			sinisiteBow.createNewValue("DamageModifier").setDataType("@F")
					.setCurrentValue("6.0").setDefaultValue("6.0");
			// .setComment("The knockback level of the Sinisite Bow.").setCommentIndentNumber(2);
			sinisiteBow.createNewValue("KnockbackAmount").setDataType("@I")
					.setCurrentValue("2").setDefaultValue("2");
			sinisiteBowDamageModifier = sinisiteBow
					.getValueByName("DamageModifier");
			sinisiteBowKnockbackAmount = sinisiteBow
					.getValueByName("KnockbackAmount");

			// Armor
			steelArmor = settings.get(
					new ConfigArmor("Steel Armor", "Armor").setDurability(20)
							.setHelmReduction(3).setChestReduction(6)
							.setLegsReduction(5).setBootsReduction(3)
							.setEnchantability(14)).asConfigArmor();
			bronzeArmor = settings.get(
					new ConfigArmor("Bronze Armor", "Armor").setDurability(16)
							.setHelmReduction(3).setChestReduction(5)
							.setLegsReduction(3).setBootsReduction(1)
							.setEnchantability(7)).asConfigArmor();
			thyriumArmor = settings.get(
					new ConfigArmor("Thyrium Armor", "Armor").setDurability(39)
							.setHelmReduction(4).setChestReduction(7)
							.setLegsReduction(6).setBootsReduction(3)
							.setEnchantability(28)).asConfigArmor();
			sinisiteArmor = settings.get(
					new ConfigArmor("Sinisite Armor", "Armor")
							.setDurability(56).setHelmReduction(5)
							.setChestReduction(8).setLegsReduction(6)
							.setBootsReduction(5).setEnchantability(11))
					.asConfigArmor();

		} catch (Exception e) {
			LogHelper
					.severe("Fusion",
							"Settings failed to load correctly. The plugin may not function correctly");
			e.printStackTrace();
		} 
		finally {
			settings.save();
			LogHelper.verbose("Fusion", "Settings loaded successfully");
		}
	} // end ()

	public static ConfigItem steelIngot, bronzeIngot, thyriumIngot,
			sinisiteIngot;

	public static ConfigEntry thyriumBow, sinisiteBow;
	public static ConfigBlock steelBlock, bronzeBlock, thyriumBlock,
			sinisiteBlock;
	public static ConfigBlock fusionFurnace;
	public static ConfigTool steelTools, bronzeTools, thyriumTools,
			sinisiteTools;
	public static ConfigArmor steelArmor, bronzeArmor, thyriumArmor,
			sinisiteArmor;

	public static ConfigValue customRecipes, extraChunkRecipes, updateChecker;
	public static ConfigValue enableSimpleOres, enableNetherrocks;
	public static ConfigValue thyriumBowDamageModifier, thyriumBowZoomAmount,
			sinisiteBowDamageModifier, sinisiteBowKnockbackAmount;
	public static ConfigValue numCustomRecipes;
	public static ConfigFusionRecipe [] customFusionRecipes;
	
} // end Settings class
