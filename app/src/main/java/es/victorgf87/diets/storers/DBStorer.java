package es.victorgf87.diets.storers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.victorgf87.diets.classes.WeightRegister;

/**
 * Created by Víctor on 26/09/2015.
 */
public class DBStorer extends SQLiteOpenHelper implements StorerInterface
{
    private final static String DB_NAME="diets_db";
    private final static Integer DB_VERSION=1;
    private final static String WEIGHT_REGISTERS_TABLE_NAME="WEIGHTREGISTERS";

    @Override
    public void storeWeight(WeightRegister weight)
    {
        SQLiteDatabase db=getWritableDatabase();
        String query="insert into "+DBStorer.WEIGHT_REGISTERS_TABLE_NAME+" " +
                "values(";

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
        String query="CREATE TABLE "+DBStorer.WEIGHT_REGISTERS_TABLE_NAME+
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TIMESTAMP INTEGER," +
                "WEIGHT REAL);";
        db.execSQL(query);
        Log.d("DBStorer onCreate", "DB created successfully");

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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
