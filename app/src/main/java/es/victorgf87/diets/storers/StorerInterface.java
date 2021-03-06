package es.victorgf87.diets.storers;

import java.util.List;

import es.victorgf87.diets.classes.DrankWaterGlass;
import es.victorgf87.diets.classes.WeightRegister;
import es.victorgf87.diets.classes.exerciseactivities.ExerciseActivity;
import es.victorgf87.diets.classes.recipes.Ingredient;
import es.victorgf87.diets.classes.recipes.Menu;
import es.victorgf87.diets.classes.recipes.Recipe;

/**
 * Created by Víctor on 26/09/2015.
 */
public interface StorerInterface
{
    public void storeWeight(WeightRegister weight);
    public List<WeightRegister> getAllWeights();
    public List<ExerciseActivity>getActivitiesList();

    public List<DrankWaterGlass> getAllGlasses();
    public void addDrankWaterGlass(DrankWaterGlass glass);

    public List<Ingredient> getAllIngredients();
    public List<Recipe> getAllRecipes();
    public List<Menu> getAllMenus();
}
