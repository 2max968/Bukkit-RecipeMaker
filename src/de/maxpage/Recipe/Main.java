package de.maxpage.Recipe;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.gson.JsonDeserializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin
{
    @Override
    public void onEnable() {
        LoadRecipes();
    }

    public void LoadRecipes()
    {
        File file = new File("recipes.json");
        Gson gson = new Gson();
        Type type = new TypeToken<List<RecipeInfo>>(){}.getType();

        List<RecipeInfo> recipes = new ArrayList<>();

        if(file.exists())
        {
            try {
                JsonReader reader = new JsonReader(new FileReader(file));
                recipes = gson.fromJson(reader, type);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        for(RecipeInfo info : recipes)
        {
            Recipe r = CreateRecipe(info);
            if(r != null && Bukkit.addRecipe(r))
            {
                Print(ChatColor.GREEN + " successfully added recipe");
            }
        }
    }

    public Recipe CreateRecipe(RecipeInfo info)
    {
        NamespacedKey key = new NamespacedKey(this, this.getName() + "_" + info.hashCode());
        Material outMat = GetMaterial(info.Output);
        if(outMat == null)
        {
            Print(ChatColor.RED + " Not a material: '" + info.Output + "'!");
            return null;
        }
        ItemStack outStack = new ItemStack(outMat, info.OutputNumber);

        Material[] inMats = new Material[info.Input.length];
        for(int i = 0; i < inMats.length; i++)
        {
            inMats[i] = GetMaterial(info.Input[i]);
            if(inMats[i] == null)
            {
                Print(ChatColor.RED + " Not a material: '" + info.Input[i] + "'!");
                return null;
            }
        }

        if(info.Shape == null) {
            ShapelessRecipe r = new ShapelessRecipe(key, outStack);
            for (int i = 0; i < inMats.length; i++) {
                r.addIngredient(inMats[i]);
            }
            return r;
        }
        else{
            String numbers = "123456789";
            ShapedRecipe r = new ShapedRecipe(key, outStack);
            r.shape(info.Shape);
            for (int i = 0; i < inMats.length; i++) {
                r.setIngredient(numbers.charAt(i), inMats[i]);
            }
            return r;
        }
    }

    public void Print(String... text)
    {
        getServer().getConsoleSender().sendMessage(text);
    }

    public Material GetMaterial(String name)
    {
        var mat =  Material.matchMaterial(name);
        if(mat == null)
        {
            for(var _mat : Material.values())
            {
                if(_mat.toString().equalsIgnoreCase(name))
                    mat = _mat;
            }
        }
        return mat;
    }
}
