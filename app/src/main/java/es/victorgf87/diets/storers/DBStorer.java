package es.victorgf87.diets.storers;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.victorgf87.diets.R;
import es.victorgf87.diets.classes.DrankWaterGlass;
import es.victorgf87.diets.classes.ExerciseActivity;
import es.victorgf87.diets.classes.WeightRegister;
import es.victorgf87.diets.classes.recipes.Ingredient;
import es.victorgf87.diets.classes.recipes.Menu;
import es.victorgf87.diets.classes.recipes.Recipe;

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



    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String queryWeights="CREATE TABLE "+DBStorer.WEIGHT_REGISTERS_TABLE_NAME+
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TIMESTAMP LONG," +
                "WEIGHT REAL);";
        db.execSQL(queryWeights);
        Log.d("DBStorer onCreate", "DB created successfully");


        /**
         * Creating, parsing activities and storing them
         */
        String queryActivities="CREATE TABLE "+DBStorer.ACTIVITIES_TABLE_NAME+
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "LOSS INTEGER,"+
                "NAME TEXT UNIQUE);";
        db.execSQL(queryActivities);

        /**
         * Creating table for glasses
         */

        String queryGlasses="CREATE TABLE "+DBStorer.GLASSES_TABLE_NAME+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TIMESTAMP LONG);";
        db.execSQL(queryGlasses);

        String queryIngredients="CREATE TABLE "+DBStorer.INGREDIENTS_TABLE_NAME+"(" +
                "INGREDIENTID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT UNIQUE," +
                "WEIGHT INTEGER);";
        db.execSQL(queryIngredients);

        String queryRecipes="CREATE TABLE "+DBStorer.RECIPES_TABLE_NAME+"(" +
                "RECIPEID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT UNIQUE," +
                "ELABORATION TEXT," +
                "PEOPLE INTEGER);";
        db.execSQL(queryRecipes);

        String queryRecipesIngredients="CREATE TABLE "+DBStorer.RECIPES_INGREDIENTS_TABLE_NAME+"(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "INGREDIENTID INTEGER," +
                "RECIPEID INTEGER," +
                "FOREIGN KEY(RECIPEID) REFERENCES "+DBStorer.RECIPES_TABLE_NAME+"(RECIPEID)," +
                "FOREIGN KEY(INGREDIENTID) REFERENCES "+DBStorer.INGREDIENTS_TABLE_NAME+"(INGREDIENTID));";
        db.execSQL(queryRecipesIngredients);


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
        }




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


        //if(db.isOpen())db.close();
        int a=3;
        int c=a;
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


    private List<Recipe>readRecipes(SQLiteDatabase db) throws IOException, XmlPullParserException
    {
        List<Recipe>ret=new ArrayList<>();
        XmlResourceParser pars= context.getResources().getXml(R.xml.recipes);
        int eventType=pars.getEventType();
        Integer currentId=null;
        String currentName=null;
        Integer currentPeople=null;
        String currentElaboration=null;
        String parsed=null;



        while(eventType!=XmlResourceParser.END_DOCUMENT)
        {
            try {
                String nameGotten=pars.getName();
                Log.d("DBStorer", "El nombre es "+nameGotten);
                if(nameGotten!=null && nameGotten.equals("Recipe"))
                {
                    currentId=Integer.valueOf(pars.getAttributeValue(null, "id"));
                    currentName=pars.getAttributeValue(null, "name");
                    currentPeople =Integer.parseInt(pars.getAttributeValue(null, "people"));
                    currentElaboration=pars.getAttributeValue(null,"elaboration");
                    Recipe newRecipe=new Recipe(currentElaboration,currentId,currentName,currentPeople);

                    pars.next();
                    nameGotten=pars.getName();
                    while(nameGotten.equals("Ingredient"))
                    {
                        try {
                            Integer currentIngredientId = Integer.parseInt(pars.getAttributeValue(null, "id"));
                            Ingredient currentIngredient = getIngredientById(currentIngredientId, db);
                            newRecipe.addIngredient(currentIngredient);

                            int a = 3;
                            int b = a;
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                            int a=3;
                            int b=a;
                        }
                        pars.next();
                        nameGotten = pars.getName();
                    }
                    ret.add(newRecipe);

                }


            }
            catch(Exception e)
            {
                int a=3;
            }
            pars.next();
            eventType=pars.getEventType();
        }
        int a=3;
        int b=a;
        return ret;
    }

    private Ingredient getIngredientById(Integer id, SQLiteDatabase db)
    {
        String[] selection=new String[]{""+id};
        String query="select INGREDIENTID, NAME, WEIGHT from "+DBStorer.INGREDIENTS_TABLE_NAME+" where INGREDIENTID=?";
        Cursor cursor=db.rawQuery(query,selection);
        if(cursor.moveToFirst())
        {
            String name=cursor.getString(1);
            Integer weight=cursor.getInt(2);
            return new Ingredient(id,name,weight);
        }
        return null;
    }

    private List<Ingredient> readIngredients()throws IOException, XmlPullParserException
    {
        // Create ResourceParser for XML file
        //XmlResourceParser xpp = context.getResources().getXml(R.xml.activities);
        List<Ingredient> ret=new ArrayList<>();


        XmlResourceParser pars= context.getResources().getXml(R.xml.ingredients);


        int eventType=pars.getEventType();
        String currentName=null;
        Integer currentWeight=null;
        String parsed=null;
        Integer currentId=null;
        while(eventType!=XmlResourceParser.END_DOCUMENT)
        {
            try {
                String nameGotten=pars.getName();
                Log.d("DBStorer", "El nombre es "+nameGotten);
                if(nameGotten!=null && nameGotten.equals("Ingredient"))
                {
                    currentId=Integer.valueOf(pars.getAttributeValue(null,"id"));
                    currentName=pars.getAttributeValue(null, "name");
                    currentWeight =Integer.parseInt(pars.getAttributeValue(null,"weight"));
                    ret.add(new Ingredient(currentId,currentName,currentWeight));

                }


            }
            catch(Exception e)
            {
                int a=3;

            }
            pars.next();
            eventType=pars.getEventType();
        }
        int a=3;
        int b=a;
        return ret;
    }


    private List<Menu> readMenus() throws IOException, XmlPullParserException
    {
        List<Menu> ret=new ArrayList<>();
        XmlResourceParser pars=context.getResources().getXml(R.xml.menus);

        int eventType=pars.getEventType();
        String currentName=null;
        Integer currentCals=null;
        Integer currentId=null;
        String parsed=null;
        String currentDescription=null;

        while(eventType!=XmlResourceParser.END_DOCUMENT)
        {
            try {
                String nameGotten=pars.getName();
                Log.d("DBStorer", "El nombre es "+nameGotten);
                if(nameGotten!=null && nameGotten.equals("Menu"))
                {
                    try
                    {
                        currentId=Integer.parseInt(pars.getAttributeValue(null, "id"));
                        pars.next();
                        pars.next();
                        List<Recipe>recipes=extractRecipeListFromTag(pars);
                        int a=3;
                    }
                    catch(Exception e)
                    {
                        int a=3;
                        int b=a;
                    }


                    //ret.add(new ExerciseActivity(currentCals, currentName));

                }


            }
            catch(Exception e)
            {
                int a=3;
            }
            pars.next();
            eventType=pars.getEventType();
        }

        return ret;
    }

    private List<Recipe> extractRecipeListFromTag(XmlResourceParser pars) throws IOException, XmlPullParserException {

        List<Recipe> recipes=new ArrayList<>();
        String nameGotten=pars.getName();
        while(nameGotten.equals("Recipe"))
        {
            Integer currentId=Integer.parseInt(pars.getAttributeValue(null,"id"));

            int a=3;
        }
        return recipes;
    }

    private Recipe getRecipeById(Integer id)
    {
        Recipe ret=null;
        String query="select ";
        return ret;
    }

    private List<ExerciseActivity> readExerciseActivities()  throws IOException, XmlPullParserException
    {
        // Create ResourceParser for XML file
        //XmlResourceParser xpp = context.getResources().getXml(R.xml.activities);
        List<ExerciseActivity> ret=new ArrayList<>();


        XmlResourceParser pars= context.getResources().getXml(R.xml.activities);


        int eventType=pars.getEventType();
        String currentName=null;
        Integer currentCals=null;
        String parsed=null;
        while(eventType!=XmlResourceParser.END_DOCUMENT)
        {
            try {
                String nameGotten=pars.getName();
                Log.d("DBStorer", "El nombre es "+nameGotten);
                if(nameGotten!=null && nameGotten.equals("activity"))
                {
                    currentName=pars.getAttributeValue(null,"name");
                    currentCals =Integer.parseInt(pars.getAttributeValue(null,"loss"));
                    ret.add(new ExerciseActivity(currentCals, currentName));

                }


            }
            catch(Exception e)
            {

            }
            pars.next();
            eventType=pars.getEventType();
        }
        int a=3;
        int b=a;
        return ret;
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
