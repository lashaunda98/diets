package es.victorgf87.diets.storers;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.victorgf87.diets.classes.DrankWaterGlass;
import es.victorgf87.diets.classes.WeightRegister;
import es.victorgf87.diets.classes.WrapperInterface;
import es.victorgf87.diets.classes.exerciseactivities.ExerciseActivity;
import es.victorgf87.diets.classes.recipes.Ingredient;
import es.victorgf87.diets.classes.recipes.IngredientWrapperList;
import es.victorgf87.diets.classes.recipes.Menu;
import es.victorgf87.diets.classes.recipes.Recipe;
import es.victorgf87.diets.classes.recipes.RecipeWrapperList;

/**
 * Created by Víctor on 26/09/2015.
 */
public class DBStorer extends SQLiteOpenHelper implements StorerInterface
{
    private Context context;
    private final static String DB_NAME="diets_db";
    private final static Integer DB_VERSION=1;
    private final static String TABLE_INGREDIENTS_NAME="INGREDIENTS";
    private final static String TABLE_RECIPE_INGREDIENTS_NAME="RECIPEINGREDIENTS";
    private final static String TABLE_RECIPES_NAME="RECIPES";





    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     * @param context to use to open or create the database
     */
    public DBStorer(Context context) {
        super(context, DBStorer.DB_NAME, null, DBStorer.DB_VERSION);
        this.context=context;
    }

    private void createTables(SQLiteDatabase db)
    {
        List<String>queries=new ArrayList<>();

        /*

        String queryEquivalentIngredients="CREATE TABLE "+DBStorer.EQUIVALENT_INGREDIENTS_TABLE_NAME+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ID1 INTEGER," +
                "ID2 INTEGER," +
                "FOREIGN KEY(ID1) REFERENCES "+DBStorer.INGREDIENTS_TABLE_NAME+"(INGREDIENTID)," +
                "FOREIGN KEY(ID2) REFERENCES "+DBStorer.INGREDIENTS_TABLE_NAME+"(INGREDIENTID)," +
                ");";
         */

        /*
        Creating ingredients table
         */

        String query="CREATE TABLE "+DBStorer.TABLE_INGREDIENTS_NAME+"(" +
                "INGREDIENTID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "INGREDIENTNAME TEXT," +
                "WEIGHT INTEGER" +
                ");";
        queries.add(query);

        query="CREATE TABLE "+DBStorer.TABLE_RECIPES_NAME+"(" +
                "RECIPEID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "RECIPENAME TEXT," +
                "ELABORATION TEXT," +
                "PEOPLE INTEGER" +
                ");";
        queries.add(query);

        query="CREATE TABLE "+DBStorer.TABLE_RECIPE_INGREDIENTS_NAME+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "INGREDIENTID INTEGER," +
                "RECIPEID INTEGER," +
                "FOREIGN KEY(INGREDIENTID) REFERENCES "+DBStorer.TABLE_INGREDIENTS_NAME+"(INGREDIENTID)," +
                "FOREIGN KEY(RECIPEID) REFERENCES "+DBStorer.TABLE_RECIPES_NAME+"(RECIPEID)" +
                ");";

        queries.add(query);

        for(String str:queries)
            db.execSQL(str);
    }

    @Override
    public List<Recipe> getAllRecipes()
    {
        List<Recipe> ret=new ArrayList<>();
        String query="SELECT RECIPEID, RECIPENAME, INGREDIENTID, INGREDIENTNAME, WEIGHT, ELABORATION, PEOPLE  FROM "+DBStorer.TABLE_RECIPES_NAME+" NATURAL JOIN "+DBStorer.TABLE_RECIPE_INGREDIENTS_NAME+
                " NATURAL JOIN "+DBStorer.TABLE_INGREDIENTS_NAME+
                " ORDER BY RECIPEID, INGREDIENTID;";
        Cursor cursor=getReadableDatabase().rawQuery(query,null);

        int amount=cursor.getCount();

        Integer recipeId=null, ingredientId=-1, weight=-1, lastRecipeId=null, people;
        String recipeName, ingredientName, elaboration;
        Recipe currentRecipe=null;
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                recipeId=cursor.getInt(0);
                recipeName=cursor.getString(1);
                ingredientId=cursor.getInt(2);
                ingredientName=cursor.getString(3);
                weight=cursor.getInt(4);
                elaboration=cursor.getString(5);
                people=cursor.getInt(6);

                if(lastRecipeId!=recipeId)
                {
                    currentRecipe=new Recipe(recipeId, recipeName, elaboration,people, new ArrayList<Ingredient>());
                    lastRecipeId=recipeId;
                    ret.add(currentRecipe);
                }
                Ingredient newIngredient=new Ingredient(ingredientId,ingredientName,weight);
                currentRecipe.addIngredient(newIngredient);

                cursor.moveToNext();
            }
        }
        return ret;
    }


    @Override
    public List<Ingredient> getAllIngredients()
    {
        List<Ingredient> ret=new ArrayList<Ingredient>();
        String query="SELECT INGREDIENTID, INGREDIENTNAME, WEIGHT FROM "+DBStorer.TABLE_INGREDIENTS_NAME+";";
        Cursor cursor=getReadableDatabase().rawQuery(query,null);

        Integer ingredientId;
        Integer weight;
        String ingredientName;
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                ingredientId=cursor.getInt(0);
                ingredientName=cursor.getString(1);
                weight=cursor.getInt(2);
                ret.add(new Ingredient(ingredientId,ingredientName,weight));
            }
        }

        return ret;
    }

    private void populateIngredients(SQLiteDatabase db) throws Exception {
        List<Ingredient> ingredients=getListFromXML("ingredients.xml", IngredientWrapperList.class);
        ContentValues values=new ContentValues();
        for(Ingredient ingredient:ingredients)
        {
            values.clear();
            values.put("INGREDIENTID", ingredient.getId());
            values.put("INGREDIENTNAME",ingredient.getName());
            values.put("WEIGHT",ingredient.getWeight());
            db.insert(DBStorer.TABLE_INGREDIENTS_NAME,null,values);
        }
    }

    private void populateRecipes(SQLiteDatabase db)throws Exception
    {
        List<Recipe> recipes=getListFromXML("recipes.xml", RecipeWrapperList.class);
        ContentValues values=new ContentValues();
        for(Recipe recipe:recipes)
        {
            values.clear();
            values.put("RECIPEID", recipe.getId());
            values.put("ELABORATION", recipe.getElaboration());
            values.put("RECIPENAME", recipe.getName());
            values.put("PEOPLE", recipe.getPeople());
            db.insert(DBStorer.TABLE_RECIPES_NAME,null,values);
            for(Ingredient ingredient:recipe.getIngredients())
            {
                values.clear();
                values.put("INGREDIENTID", ingredient.getId());
                values.put("RECIPEID", recipe.getId());
                db.insert(DBStorer.TABLE_RECIPE_INGREDIENTS_NAME,null,values);
            }
        }
    }



    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try {
            createTables(db);
            populateIngredients(db);
            populateRecipes(db);

        }
        catch(Exception e)
        {
            int a=3;
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    /**
     * Reads the text of an asset. Should not be run on the UI thread.
     *
     * @param mgr
     *            The {@link AssetManager} obtained via {@link Context#getAssets()}
     * @param path
     *            The path to the asset.
     * @return The plain text of the asset
     */
    public static String readAsset(AssetManager mgr, String path) {
        String contents = "";
        InputStream is = null;
        BufferedReader reader = null;
        try {
            is = mgr.open(path);
            reader = new BufferedReader(new InputStreamReader(is));
            contents = reader.readLine();
            String line = null;
            while ((line = reader.readLine()) != null) {
                contents += '\n' + line;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return contents;
    }


    @Override
    public void storeWeight(WeightRegister weight) {

    }

    @Override
    public List<WeightRegister> getAllWeights() {
        return null;
    }

    @Override
    public List<ExerciseActivity> getActivitiesList() {
        return null;
    }

    @Override
    public List<DrankWaterGlass> getAllGlasses() {
        return null;
    }

    @Override
    public void addDrankWaterGlass(DrankWaterGlass glass) {

    }





    @Override
    public List<Menu> getAllMenus() {
        return null;
    }


    private static enum MEALS_TYPES {
        BREAKFAST,MORNING,LUNCH,AFTERNOON,DINNER
    }


    /*private List<EquivalentIngredient> readEquivalentIngredients() throws Exception
    {
        String readString=readAsset(context.getAssets(), "equivalent_ingredients.xml");
        Serializer serializer = new Persister();

        EquivalentIngredientsWrapper ingredientList=serializer.read(EquivalentIngredientsWrapper.class, readString);
        return ingredientList.list;
    }*/

    private <WrapperClass extends WrapperInterface,ReturnedList> List<ReturnedList> getListFromXML(String fileName, Class<WrapperClass>klass) throws Exception {
        String readString=readAsset(context.getAssets(), fileName);
        Serializer serializer = new Persister();
        WrapperClass t= serializer.read(klass, readString);
        return t.getList();
    }

}
