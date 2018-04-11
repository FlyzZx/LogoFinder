package imt.logofinder.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import imt.logofinder.R;
import imt.logofinder.fragment.AddServerDialogFragment;
import imt.logofinder.fragment.AddTrainDialog;
import imt.logofinder.http.CustomPostRequest;
import imt.logofinder.http.CustomRequest;
import imt.logofinder.tools.ImageUtil;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener, AddTrainDialog.OnTrainAddedListener {
    private TextView textView_title = null;
    private String classe = "";
    private Bitmap image = null;
    private String tempPath = null;
    private ImageView imageView_result = null;
    private Button btn_good = null;
    private Button btn_bad = null;

    public final static String serverAddress = "51.254.205.180";
    public final static String pathToClasses = "/classes";
    public final static String pathToAddTrain = "/addToTrain";
    public final static String pathStartTraining = "/startTraining?nwords=200";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textView_title = findViewById(R.id.textView_title);
        imageView_result = findViewById(R.id.imageView_Result);
        btn_good = findViewById(R.id.btn_good);
        btn_bad = findViewById(R.id.btn_bad);

        btn_good.setOnClickListener(this);
        btn_bad.setOnClickListener(this);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        classe = extras.getString("EXTRA_CLASSE");
        classe = classe.substring(classe.lastIndexOf('/')+1,classe.lastIndexOf('.'));
        textView_title.setText(classe);

        tempPath = extras.getString("EXTRA_TEMPATH");
        image = BitmapFactory.decodeFile(tempPath);
        this.imageView_result.setImageBitmap(image);
    }

    @Override
    public void onClick(View view) {
        int id  = view.getId();
        switch (id){
            case R.id.btn_good:
                this.finish();
                break;
            case R.id.btn_bad:
                //TODO APPEL SERVICE WEB
                //Affichage d'un dialog
                AddTrainDialog fragment = new AddTrainDialog();
                fragment.setOnTraiAddedListener(this);
                fragment.show(this.getFragmentManager(), "addTrain");
                //fragment.setCreateServerListener(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTrainAdded(String classe) {
        try {
            //Récupération de l'image en B64
            if(classe.contains(".xml")) {
                String[] tmp = classe.split(".xml");
                classe = tmp[0];
            }
            String img64 = ImageUtil.convert(image);
            String parameters = "classes=" + URLEncoder.encode(classe, "UTF-8") + "&image=" + URLEncoder.encode(img64, "UTF-8");
            CustomPostRequest request = new CustomPostRequest();
            String ret = request.execute(serverAddress, "8080", pathToAddTrain, parameters).get();
            Toast.makeText(this, "Ajouté sur le serveur", Toast.LENGTH_SHORT).show();
            this.finish();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
