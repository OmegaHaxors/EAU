package org.ja13.eau.misc;

import org.ja13.eau.EAU;
import org.ja13.eau.transparentnode.electricalfurnace.ElectricalFurnaceProcess;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.ja13.eau.EAU;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
//import org.ja13.eau.electricalfurnace.ElectricalFurnaceProcess;

public class RecipesList {

    public static final ArrayList<RecipesList> listOfList = new ArrayList<RecipesList>();

    private final ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
    private final ArrayList<ItemStack> machineList = new ArrayList<ItemStack>();

    public RecipesList() {
        listOfList.add(this);
    }

    public ArrayList<Recipe> getRecipes() {
        return recipeList;
    }

    public ArrayList<ItemStack> getMachines() {
        return machineList;
    }

    public void addRecipe(Recipe recipe) {
        recipeList.add(recipe);
        recipe.setMachineList(machineList);
    }

    public void addMachine(ItemStack machine) {
        machineList.add(machine);
    }

    public Recipe getRecipe(ItemStack input) {
        for (Recipe r : recipeList) {
            if (r.canBeCraftedBy(input)) return r;
        }
        return null;
    }

    public ArrayList<Recipe> getRecipeFromOutput(ItemStack output) {
        ArrayList<Recipe> list = new ArrayList<Recipe>();
        for (Recipe r : recipeList) {
            for (ItemStack stack : r.getOutputCopy()) {
                if (Utils.areSame(stack, output)) {
                    list.add(r);
                    break;
                }
            }
        }
        return list;
    }

    public static ArrayList<Recipe> getGlobalRecipeWithOutput(ItemStack output) {
        output = output.copy();
        output.stackSize = 1;
        ArrayList<Recipe> list = new ArrayList<Recipe>();
        for (RecipesList recipesList : listOfList) {
            list.addAll(recipesList.getRecipeFromOutput(output));
        }

        FurnaceRecipes furnaceRecipes = FurnaceRecipes.smelting();

        {
            Iterator it = furnaceRecipes.getSmeltingList().entrySet().iterator();
            while (it.hasNext()) {
                try {
                    Map.Entry pairs = (Map.Entry) it.next();
                    Recipe recipe; // List<Integer>, ItemStack
                    ItemStack stack = (ItemStack) pairs.getValue();
                    ItemStack li = (ItemStack) pairs.getKey();
                    if (Utils.areSame(output, stack)) {
                        list.add(recipe = new Recipe(li.copy(), output, ElectricalFurnaceProcess.energyNeededPerSmelt));
                        recipe.setMachineList(EAU.furnaceList);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }

        return list;
    }

    public static ArrayList<Recipe> getGlobalRecipeWithInput(ItemStack input) {
        input = input.copy();
        input.stackSize = 64;
        ArrayList<Recipe> list = new ArrayList<Recipe>();
        for (RecipesList recipesList : listOfList) {
            Recipe r = recipesList.getRecipe(input);
            if (r != null)
                list.add(r);
        }

        FurnaceRecipes furnaceRecipes = FurnaceRecipes.smelting();
        ItemStack smeltResult = furnaceRecipes.getSmeltingResult(input);
        Recipe smeltRecipe;
        if (smeltResult != null) {
            try {
                ItemStack input1 = input.copy();
                input1.stackSize = 1;
                list.add(smeltRecipe = new Recipe(input1, smeltResult, ElectricalFurnaceProcess.energyNeededPerSmelt));
                smeltRecipe.machineList.addAll(EAU.furnaceList);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        return list;
    }
}
/*		FurnaceRecipes.smelting().addSmelting(in.itemID, in.getItemDamage(),
                findItemStack("Copper ingot"), 0);*/
