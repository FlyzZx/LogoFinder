package imt.logofinder.sql.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import imt.logofinder.Model.ServerOptions;
import imt.logofinder.sql.MamanDAO;

/**
 * Created by Tom on 18/02/2018.
 */

public class ServerDao extends MamanDAO {

    public static final String NOM_TABLE = "options";
    public static final String NOM_COLONNE_SERVERNAME = "servername";
    public static final String NOM_COLONNE_SERVERPATH = "serverpath";
    public static final String NOM_COLONNE_ISDELETABLE = "isdeletable";
    public static final String KEY = "id";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NOM_TABLE + " (" +
                 KEY + " INTEGER PRIMARY KEY," +
                 NOM_COLONNE_SERVERNAME + " TEXT," +
                 NOM_COLONNE_SERVERPATH + " TEXT," +
                 NOM_COLONNE_ISDELETABLE +" INTEGER );";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NOM_TABLE;
    public ServerDao(Context context) {
        super(context);
    }

    public void add(String serverName,String serverPath,int isDeletable){
        ContentValues values = new ContentValues();
        values.put(NOM_COLONNE_SERVERNAME,serverName);
        values.put(NOM_COLONNE_SERVERPATH,serverPath);
        //values.put(NOM_COLONNE_ISDELETABLE,isDeletable);
        db.insert(NOM_TABLE,null,values);
    }
    public void delete(long id){
        db.delete(NOM_TABLE,KEY +"= ?",new String[]{String.valueOf(id)});
    }
    public void select(){

    }
    public ArrayList<ServerOptions> selectAll(){
        ArrayList<ServerOptions> retour = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + NOM_TABLE +";",new String []{});
        while(cursor.moveToNext()){
            ServerOptions temp = new ServerOptions(cursor.getString(cursor.getColumnIndex(NOM_COLONNE_SERVERNAME)),cursor.getString(cursor.getColumnIndex(NOM_COLONNE_SERVERPATH)));
            retour.add(temp);
        }
        return retour;
    }
    public void update(){

    }
}
