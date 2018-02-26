package imt.logofinder.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import imt.logofinder.Model.ServerOptionAdapter;
import imt.logofinder.Model.ServerOptions;
import imt.logofinder.R;
import imt.logofinder.analyzer.ServerTraining;
import imt.logofinder.fragment.AddServerDialogFragment;
import imt.logofinder.fragment.PendingDownloadDialog;
import imt.logofinder.sql.dao.ServerDao;

/**
 * Created by TOM on 15/02/2018.
 */

public class OptionsActivity extends AppCompatActivity implements OnItemSelectedListener, View.OnClickListener, AddServerDialogFragment.CreateServerListener {
    private TextView textView_chemin_serveur = null;
    private Spinner spinner_ddl_servers = null;

    private Button btn_add_server = null;
    ServerDao serverDao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //Instanciation des Composants
        this.textView_chemin_serveur = (TextView) findViewById(R.id.textView_chemin_serveur);
        this.spinner_ddl_servers = (Spinner) findViewById(R.id.spinner_ddl_servers);
        this.btn_add_server = (Button) findViewById(R.id.btn_add_server);

        //Instanciation du helper SQLite pour la DAL

        serverDao = new ServerDao(getApplicationContext());
        serverDao.open();
        if(serverDao.selectAll().size() == 0) {
            serverDao.add("Perso", "http://imtimagemobile.000webhostapp.com/", 1);
        }
        serverDao.close();

        String actuelSrv = getSharedPreferences("logo", MODE_PRIVATE).getString("choosenServer", "");
        if(!actuelSrv.equals("")) {
            textView_chemin_serveur.setText(actuelSrv);
        }


        //Listeners
        this.spinner_ddl_servers.setOnItemSelectedListener(this);
        this.btn_add_server.setOnClickListener(this);

        fillDropDownList();

    }

    private void fillDropDownList() {
        serverDao.open();
        ArrayList<ServerOptions> serverItems = serverDao.selectAll();
        serverDao.close();

        ServerOptionAdapter soa = new ServerOptionAdapter(this, serverItems);
        spinner_ddl_servers.setAdapter(soa);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        ServerOptions serverOptions = (ServerOptions) parent.getItemAtPosition(pos);

        //Recupère le chemin du serveur selectionné
        SharedPreferences sp = getSharedPreferences("logo", MODE_PRIVATE);
        sp.edit().putString("choosenServer", serverOptions.getServerPath()).commit();


        ServerTraining serverTraining = new ServerTraining(serverOptions.getServerPath(), this);
        try {
            serverTraining.getRemoteFiles();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_add_server:
                AddServerDialogFragment fragment = new AddServerDialogFragment();
                fragment.show(this.getFragmentManager(), "addServer");
                fragment.setCreateServerListener(this);
                break;
            default:
                break;


        }


    }

    @Override
    public void onServerCreation(String servername, String serverpath) {
        if(!serverpath.endsWith("/")) {
            serverpath += "/";
        }
        serverDao.open();
        serverDao.add(servername, serverpath, 1);
        serverDao.close();
        fillDropDownList();
    }
}
