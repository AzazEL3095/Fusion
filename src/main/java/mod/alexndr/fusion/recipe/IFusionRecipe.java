package mod.alexndr.fusion.recipe;

import mod.alexndr.fusion.Fusion;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public interface IFusionRecipe extends IRecipe<RecipeWrapper>
{
    ResourceLocation TYPE_ID = new ResourceLocation(Fusion.MODID, "alloying");

    @Override
    default boolean canFit(int width, int height)
    {
        return false;
    }

    @Override
    default IRecipeType<?> getType()
    {
        return Registry.RECIPE_TYPE.getValue(TYPE_ID).get();
    }
    
    
} // end-class
