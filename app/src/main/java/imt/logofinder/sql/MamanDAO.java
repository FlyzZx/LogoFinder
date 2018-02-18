package imt.logofinder.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Tom on 18/02/2018.
 */

public abstract class MamanDAO {
    protected final static int VERSION = 1;
    protected final static String DBNAME="logofinder.db";

    protected SQLiteDatabase db = null;
    protected DatabaseHandler handler = null;
    public MamanDAO(Context context){
        this.handler = new DatabaseHandler(context,DBNAME,null,VERSION);
    }

    public SQLiteDatabase open(){
        db = handler.getWritableDatabase();
        return db;
    }

    public void close(){
        db.close();
    }

    public SQLiteDatabase getDb(){
        return db;
    }

}
