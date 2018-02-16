package imt.logofinder.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;

import imt.logofinder.R;
import imt.logofinder.sql.DbSchema;

import static imt.logofinder.sql.DbSchema.*;

/**
 * Created by TOM on 15/02/2018.
 */

public class OptionsActivity extends AppCompatActivity implements OnItemSelectedListener {
    private TextView textView_chemin_serveur = null;
    private Spinner spinner_ddl_servers = null;
    private DbHelper dbHelper = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //Instanciation des Composants
        this.textView_chemin_serveur = (TextView) findViewById(R.id.textView_chemin_serveur);
        this.spinner_ddl_servers = (Spinner) findViewById(R.id.spinner_ddl_servers);


        //Instanciation du helper SQLite pour la DAL
         dbHelper = new DbHelper(getApplicationContext());





        //Listeners
        this.spinner_ddl_servers.setOnItemSelectedListener(this);


        fillDropDownList();

    }

    private void fillDropDownList(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TableOption._ID,
                TableOption.NOM_COLONNE_SERVERNAME,
        };

        Cursor cursor = db.query(TableOption.NOM_TABLE,projection,null,null,null,null,null);
        List serverItems = new ArrayList<>();
        while(cursor.moveToNext()){
            String serverItem = cursor.getString(cursor.getColumnIndex(TableOption.NOM_COLONNE_SERVERNAME));
            serverItems.add(serverItem);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new  ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,serverItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_ddl_servers.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String serverName = parent.getItemAtPosition(pos).toString();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TableOption._ID,
                TableOption.NOM_COLONNE_SERVERNAME,
                TableOption.NOM_COLONNE_SERVERPATH
        };
        String selection = TableOption.NOM_COLONNE_SERVERNAME + " = ?";
        String[] selectionArgs = { serverName };


        Cursor cursor = db.query(TableOption.NOM_TABLE,projection,selection,selectionArgs,null,null,null);
        cursor.moveToFirst();
        String path_serveur = cursor.getString(cursor.getColumnIndex(TableOption.NOM_COLONNE_SERVERPATH));

        this.textView_chemin_serveur.setText(path_serveur);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
