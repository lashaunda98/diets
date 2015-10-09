package es.victorgf87.diets.storers;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.victorgf87.diets.classes.DrankWaterGlass;
import es.victorgf87.diets.classes.WeightRegister;
import es.victorgf87.diets.classes.exerciseactivities.ExerciseActivity;
import es.victorgf87.diets.classes.exerciseactivities.ExerciseActivityWrapperList;
import es.victorgf87.diets.classes.recipes.EquivalentIngredient;
import es.victorgf87.diets.classes.recipes.EquivalentIngredientsWrapper;
import es.victorgf87.diets.classes.recipes.Ingredient;
import es.victorgf87.diets.classes.recipes.IngredientWrapperList;
import es.victorgf87.diets.classes.recipes.Menu;
import es.victorgf87.diets.classes.recipes.MenuWrapperList;
import es.victorgf87.diets.classes.recipes.Recipe;
import es.victorgf87.diets.classes.recipes.RecipeWrapperList;

/**
 * Created by Víctor on 26/09/2015.
 */
public class DBStorer extends SQLiteOpenHelper implements StorerInterface
{
    private Context context;
    private final static String DB_NAME="diets_db";
    private final static Integer DB_VERSION=2;
    private final static String WEIGHT_REGISTERS_TABLE_NAME="WEIGHTREGISTERS";
    private final static String ACTIVITIES_TABLE_NAME="ACTIVITIES";
    private final static String GLASSES_TABLE_NAME="DRANKWATERGLASSES";
    private final static String INGREDIENTS_TABLE_NAME="INGREDIENTS";
    private final static String RECIPES_TABLE_NAME="RECIPES";
    private final static String RECIPES_INGREDIENTS_TABLE_NAME="RECIPESINGREDIENTS";
    private final static String EQUIVALENT_INGREDIENTS_TABLE_NAME="EQUIVALENTINGREDIENTS";


    @Override
    public void storeWeight(WeightRegister weight)
    {
        SQLiteDatabase db=getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put("TIMESTAMP",weight.getDateTimeStamp());
        values.put("WEIGHT",weight.getWeight());


        db.insert(DBStorer.WEIGHT_REGISTERS_TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public List<WeightRegister> getAllWeights()
    {
        String query="select ID, TIMESTAMP, WEIGHT from "+DBStorer.WEIGHT_REGISTERS_TABLE_NAME;
        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor=db.rawQuery(query, null);

        List<WeightRegister> ret=new ArrayList<WeightRegister>();

        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                Integer id=cursor.getInt(0);
                long timeStamp=cursor.getLong(1);
                double weight=cursor.getDouble(2);
                WeightRegister newRegister=new WeightRegister(id, weight, timeStamp);
                ret.add(newRegister);
                cursor.moveToNext();
            }
        }

        Collections.sort(ret);
        return ret;
    }


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
        /*
            Creating weight register table
         */
        String queryWeights="CREATE TABLE "+DBStorer.WEIGHT_REGISTERS_TABLE_NAME+
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TIMESTAMP LONG," +
                "WEIGHT REAL);";
        db.execSQL(queryWeights);
        Log.d("DBStorer onCreate", "DB created successfully");


        /**
         * Creating, activities table
         */
        String queryActivities="CREATE TABLE "+DBStorer.ACTIVITIES_TABLE_NAME+
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "LOSS INTEGER,"+
                "NAME TEXT UNIQUE);";
        db.execSQL(queryActivities);

        /*
         * Creating table for glasses
         */

        String queryGlasses="CREATE TABLE "+DBStorer.GLASSES_TABLE_NAME+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TIMESTAMP LONG);";
        db.execSQL(queryGlasses);


        /*
         * Creating table for ingredients
         */
        String queryIngredients="CREATE TABLE "+DBStorer.INGREDIENTS_TABLE_NAME+"(" +
                "INGREDIENTID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT UNIQUE," +
                "WEIGHT INTEGER);";
        db.execSQL(queryIngredients);

        /*
        Creating N:N table for equivalent ingredients
         */

        String queryEquivalentIngredients="CREATE TABLE "+DBStorer.EQUIVALENT_INGREDIENTS_TABLE_NAME+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ID1 INTEGER," +
                "ID2 INTEGER," +
                "FOREIGN KEY(ID1) REFERENCES "+DBStorer.INGREDIENTS_TABLE_NAME+"(INGREDIENTID)," +
                "FOREIGN KEY(ID2) REFERENCES "+DBStorer.INGREDIENTS_TABLE_NAME+"(INGREDIENTID)," +
                ");";

        /*
            Creaging table for recipes
         */
        String queryRecipes="CREATE TABLE "+DBStorer.RECIPES_TABLE_NAME+"(" +
                "RECIPEID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT UNIQUE," +
                "ELABORATION TEXT," +
                "PEOPLE INTEGER);";
        db.execSQL(queryRecipes);

        /*
            Creating n:n table between ingredients and recipes.
         */
        String queryRecipesIngredients="CREATE TABLE "+DBStorer.RECIPES_INGREDIENTS_TABLE_NAME+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "INGREDIENTID INTEGER," +
                "RECIPEID INTEGER," +
                "FOREIGN KEY(RECIPEID) REFERENCES "+DBStorer.RECIPES_TABLE_NAME+"(RECIPEID)," +
                "FOREIGN KEY(INGREDIENTID) REFERENCES "+DBStorer.INGREDIENTS_TABLE_NAME+"(INGREDIENTID));";
        db.execSQL(queryRecipesIngredients);
    }


    private void populateIngredients(SQLiteDatabase db)
    {
        try
        {
            List<Ingredient> ingredients=readIngredients();
            for(Ingredient ing: ingredients)
            {
                ContentValues values=new ContentValues();
                values.put("INGREDIENTID",ing.getId());
                values.put("NAME",ing.getName());
                values.put("WEIGHT",ing.getWeight());
                db.insert(DBStorer.INGREDIENTS_TABLE_NAME, null, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void populateRecipes(SQLiteDatabase db)
    {
        try
        {
            List<Recipe>recipes=readRecipes(db);
            for(Recipe recipe: recipes)
            {
                ContentValues recipeValues=new ContentValues();
                recipeValues.put("NAME",recipe.getName());
                recipeValues.put("RECIPEID", recipe.getId());
                recipeValues.put("PEOPLE", recipe.getPeople());
                recipeValues.put("ELABORATION",recipe.getElaboration());
                db.insert(DBStorer.RECIPES_TABLE_NAME,null,recipeValues);

                List<Integer> ingredientIds=recipe.getIngredientIds();
                for(Integer in: ingredientIds)
                {
                    ContentValues values=new ContentValues();
                    values.put("RECIPEID",recipe.getId());
                    values.put("INGREDIENTID", in);
                    db.insert(DBStorer.RECIPES_INGREDIENTS_TABLE_NAME,null,values);
                }
            }

            int a=3;
            int b=a;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateExerciseActivities(SQLiteDatabase db)
    {
        try {
            List<ExerciseActivity>activities= readExerciseActivities();
            for(ExerciseActivity ea: activities)
            {
                ContentValues cv=new ContentValues();
                cv.put("LOSS",ea.getCalsLoss());
                cv.put("NAME",ea.getName());
                db.insert(DBStorer.ACTIVITIES_TABLE_NAME, null, cv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            int kldsjf=3;
            int kldsajfl=kldsjf;
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
        createTables(db);
        populateIngredients(db);
        populateRecipes(db);
        populateExerciseActivities(db);
        populateEquivalentIngredients(db);





        //if(db.isOpen())db.close();
    }


    private void populateEquivalentIngredients(SQLiteDatabase db)
    {
        try {
            List<EquivalentIngredient>equivalences= readEquivalentIngredients();
            for(EquivalentIngredient equivalence:equivalences)
            {
                ContentValues cv=new ContentValues();
                cv.put("ID", equivalence.getId());
                cv.put("ID1", equivalence.getId1());
                cv.put("ID2", equivalence.getId2());
                db.insert(DBStorer.EQUIVALENT_INGREDIENTS_TABLE_NAME,null,cv);

            }
            /*for(ExerciseActivity ea: activities)
            {
                ContentValues cv=new ContentValues();
                cv.put("LOSS",ea.getCalsLoss());
                cv.put("NAME",ea.getName());
                db.insert(DBStorer.ACTIVITIES_TABLE_NAME, null, cv);
            }*/
            int a=3;
            int b=a;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            int kldsjf=3;
            int kldsajfl=kldsjf;
        }
    }

    private List<EquivalentIngredient> readEquivalentIngredients() throws Exception {
        String readString=readAsset(context.getAssets(), "equivalent_ingredients.xml");
        Serializer serializer = new Persister();

        EquivalentIngredientsWrapper ingredientList=serializer.read(EquivalentIngredientsWrapper.class, readString);
        return ingredientList.list;
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

    private List<Ingredient> getIngredientsFromRecipe(Integer recipeId)
    {
        try {
            SQLiteDatabase db = getReadableDatabase();
            List<Ingredient> ret = new ArrayList<>();
            String[] selections = new String[]{"" + recipeId};
            /*String query = "SELECT " + DBStorer.INGREDIENTS_TABLE_NAME + ".NAME, " +
                    DBStorer.INGREDIENTS_TABLE_NAME +
                    ".INGREDIENTID, "+DBStorer.INGREDIENTS_TABLE_NAME+
                    ".WEIGHT FROM " + DBStorer.RECIPES_TABLE_NAME +
                    " NATURAL JOIN " + DBStorer.RECIPES_INGREDIENTS_TABLE_NAME +
                    " NATURAL JOIN " + DBStorer.INGREDIENTS_TABLE_NAME + " where " +
                    DBStorer.RECIPES_TABLE_NAME+".RECIPEID=?";*/

            String query = "SELECT " + DBStorer.INGREDIENTS_TABLE_NAME + ".NAME, " +
                    DBStorer.INGREDIENTS_TABLE_NAME +
                    ".INGREDIENTID, "+DBStorer.INGREDIENTS_TABLE_NAME+
                    ".WEIGHT FROM " + DBStorer.RECIPES_TABLE_NAME +
                    ", " + DBStorer.RECIPES_INGREDIENTS_TABLE_NAME +
                    ", " + DBStorer.INGREDIENTS_TABLE_NAME + " where " +
                    DBStorer.RECIPES_TABLE_NAME+".RECIPEID=? and "+DBStorer.RECIPES_TABLE_NAME+".RECIPEID="+DBStorer.RECIPES_INGREDIENTS_TABLE_NAME+".RECIPEID " +
                    "and "+DBStorer.RECIPES_INGREDIENTS_TABLE_NAME+".INGREDIENTID="+DBStorer.INGREDIENTS_TABLE_NAME+".INGREDIENTID";

            Cursor curs = db.rawQuery(query, selections);
            Integer count=curs.getCount();



            if (curs.moveToFirst()) {
                while (!curs.isAfterLast()) {
                    String name = curs.getString(0);
                    Integer id=curs.getInt(1);
                    Integer weight=curs.getInt(2);

                    Ingredient ingredient=new Ingredient(id,name,weight);
                    ret.add(ingredient);
                    curs.moveToNext();
                }
            }

            if (db.isOpen())
                db.close();
            return ret;
        }
        catch(Exception e)
        {
            int a=3;
            int b=a;
            return null;
        }
    }


    @Override
    public List<Ingredient> getAllIngredients()
    {
        SQLiteDatabase db=getReadableDatabase();

        String query="select ID, NAME, WEIGHT from "+DBStorer.INGREDIENTS_TABLE_NAME;
        Cursor cursor=db.rawQuery(query, null);

        List<Ingredient> ret=new ArrayList<>();

        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                Integer id=cursor.getInt(0);
                String name=cursor.getString(1);
                Integer weight=cursor.getInt(2);
                Ingredient newIngredient=new Ingredient(id,name,weight);
                ret.add(newIngredient);
                cursor.moveToNext();
            }
        }

        if(db.isOpen())db.close();
        return ret;
    }

    @Override
    public List<Recipe> getAllRecipes()
    {
        SQLiteDatabase db=getReadableDatabase();
        List<Recipe> ret=new ArrayList<>();
        String query="Select RECIPEID, NAME, PEOPLE, ELABORATION from "+DBStorer.RECIPES_TABLE_NAME+";";
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                Integer id=cursor.getInt(0);
                String name=cursor.getString(1);
                Integer people=cursor.getInt(2);
                String elaboration=cursor.getString(3);
                Recipe newRecipe=new Recipe(elaboration,id,name,people);
                List<Ingredient> ingredients=this.getIngredientsFromRecipe(id);
                newRecipe.setIngredients(ingredients);
                ret.add(newRecipe);
                cursor.moveToNext();
            }
        }
        return ret;
    }

    @Override
    public List<ExerciseActivity> getActivitiesList()
    {
        String query="select ID, NAME, LOSS from "+DBStorer.ACTIVITIES_TABLE_NAME;
        SQLiteDatabase db=getReadableDatabase();

        Cursor cursor=db.rawQuery(query, null);

        List<ExerciseActivity> ret=new ArrayList<ExerciseActivity>();

        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                Integer id=cursor.getInt(0);
                String name=cursor.getString(1);
                Integer loss=cursor.getInt(2);
                ExerciseActivity newRegister=new ExerciseActivity(id, name, loss);
                ret.add(newRegister);
                cursor.moveToNext();
            }
        }

        return ret;
    }

    @Override
    public List<DrankWaterGlass> getAllGlasses()
    {

        List<DrankWaterGlass>ret=new ArrayList<>();
        String query="select id, timestamp from "+DBStorer.GLASSES_TABLE_NAME+";";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                Integer id=cursor.getInt(0);
                long timeStamp=cursor.getLong(1);
                Date date=new Date(timeStamp);
                DrankWaterGlass newGlass=new DrankWaterGlass(id,date);
                ret.add(newGlass);
                cursor.moveToNext();
            }
        }

        if(db.isOpen())db.close();
        return ret;
    }

    @Override
    public void addDrankWaterGlass(DrankWaterGlass glass)
    {
        SQLiteDatabase db=getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("timestamp", glass.getDateTimeStamp());

        db.insert(DBStorer.GLASSES_TABLE_NAME,null,cv);
    }


    private List<Recipe>readRecipes(SQLiteDatabase db) throws Exception {
        String readString=readAsset(context.getAssets(), "recipes.xml");
        Serializer serializer = new Persister();

        RecipeWrapperList ingredientList=serializer.read(RecipeWrapperList.class, readString);
        return ingredientList.list;
    }

    private Ingredient getIngredientById(Integer id, SQLiteDatabase db)
    {
        String[] selection=new String[]{""+id};
        String query="select INGREDIENTID, NAME, WEIGHT from "+DBStorer.INGREDIENTS_TABLE_NAME+" where INGREDIENTID=?";
        Cursor cursor=db.rawQuery(query, selection);
        if(cursor.moveToFirst())
        {
            String name=cursor.getString(1);
            Integer weight=cursor.getInt(2);
            return new Ingredient(id,name,weight);
        }
        return null;
    }

    private List<Ingredient> readIngredients() throws Exception
    {
        String readString=readAsset(context.getAssets(), "ingredients.xml");
        Serializer serializer = new Persister();

        IngredientWrapperList ingredientList=serializer.read(IngredientWrapperList.class, readString);
        return ingredientList.list;

    }


    private List<Menu> readMenus() throws Exception
    {
        String readString=readAsset(context.getAssets(), "menus.xml");
        Serializer serializer = new Persister();

        MenuWrapperList ingredientList=serializer.read(MenuWrapperList.class, readString);
        return ingredientList.list;

    }



    private Recipe getRecipeById(Integer id)
    {
        Recipe ret=null;
        String query="select ";
        return ret;
    }

    private List<ExerciseActivity> readExerciseActivities() throws Exception {
        String readString=readAsset(context.getAssets(), "activities.xml");
        Serializer serializer = new Persister();

        ExerciseActivityWrapperList ingredientList=serializer.read(ExerciseActivityWrapperList.class, readString);
        return ingredientList.list;
    }


    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {




    }
}
