package imt.logofinder.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.security.AccessControlContext;

/**
 * Created by TWAE on 16/02/2018.
 */

public final class DbSchema {

    private DbSchema(){}

    public static class TableOption implements BaseColumns{
        public static final String NOM_TABLE = "options";
        public static final String NOM_COLONNE_SERVERNAME = "servername";
        public static final String NOM_COLONNE_SERVERPATH = "serverpath";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TableOption.NOM_TABLE + " (" +
                    TableOption._ID + " INTEGER PRIMARY KEY," +
                    TableOption.NOM_COLONNE_SERVERNAME + " TEXT," +
                    TableOption.NOM_COLONNE_SERVERPATH + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TableOption.NOM_TABLE;


    public static class DbHelper extends SQLiteOpenHelper {


        public static final int DB_VERSION = 1;
        public static final String DB_Name = "LogoFinder.db";

        public DbHelper(Context context){
            super(context,DB_Name, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
            ContentValues values = new ContentValues();
            values.put(TableOption.NOM_COLONNE_SERVERNAME,"Default");
            values.put(TableOption.NOM_COLONNE_SERVERPATH,"http://imtimagemobile.000webhostapp.com/");
            db.insert(TableOption.NOM_TABLE,null,values);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
