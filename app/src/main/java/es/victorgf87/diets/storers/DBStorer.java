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

import javax.xml.parsers.SAXParserFactory;

import es.victorgf87.diets.R;
import es.victorgf87.diets.classes.DrankWaterGlass;
import es.victorgf87.diets.classes.ExerciseActivity;
import es.victorgf87.diets.classes.WeightRegister;

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
                "TIMESTAMP INTEGER," +
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
                "ID INTEGER PRIMAR KEY AUTOINCREMENT," +
                "TIMESTAMP INTEGER);";
        db.execSQL(queryGlasses);


        try {
            List<ExerciseActivity>activities= doRead();
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
        /*
        String queryGlasses="CREATE TABLE "+DBStorer.GLASSES_TABLE_NAME+"(" +
                "ID INTEGER PRIMAR KEY AUTOINCREMENT," +
                "TIMESTAMP INTEGER);";
         */
        List<DrankWaterGlass>ret=new ArrayList<>();
        String query="select id, timestamp from "+DBStorer.GLASSES_TABLE_NAME+";";
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                Integer id=cursor.getInt(0);
                Integer timeStamp=cursor.getInt(1);
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
        cv.put("timestamp",glass.getDateTimeStamp());

        db.insert(DBStorer.GLASSES_TABLE_NAME,null,cv);
    }

    private List<ExerciseActivity> doRead()  throws IOException, XmlPullParserException
    {
        // Create ResourceParser for XML file
        //XmlResourceParser xpp = context.getResources().getXml(R.xml.activities);
        List<ExerciseActivity> ret=new ArrayList<>();

        SAXParserFactory factory = SAXParserFactory.newInstance();

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
