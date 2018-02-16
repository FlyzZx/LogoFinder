package imt.logofinder.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import java.security.AccessController;

import imt.logofinder.R;
import imt.logofinder.sql.DbSchema;

import static imt.logofinder.sql.DbSchema.*;

/**
 * Created by TOM on 15/02/2018.
 */

public class OptionsActivity extends AppCompatActivity {
    private EditText editText_nom_serveur = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //Instanciation du TextView

        this.editText_nom_serveur = (EditText) findViewById(R.id.editText_nom_serveur);

        //Instanciation du helper SQLite pour la DAL
        DbHelper dbHelper = new DbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TableOption._ID,
                TableOption.NOM_COLONNE_SERVERNAME,
                TableOption.NOM_COLONNE_SERVERPATH
        };

      Cursor cursor = db.query(TableOption.NOM_TABLE,projection,null,null,null,null,null);
      cursor.moveToFirst();
      String nom_serveur = cursor.getString(cursor.getColumnIndex(TableOption.NOM_COLONNE_SERVERNAME));
      String path_serveur = cursor.getString(cursor.getColumnIndex(TableOption.NOM_COLONNE_SERVERPATH));

        this.editText_nom_serveur.setText(nom_serveur);


    }



}
