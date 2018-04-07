package imt.logofinder.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import imt.logofinder.R;
import imt.logofinder.fragment.AddServerDialogFragment;
import imt.logofinder.fragment.AddTrainDialog;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView_title = null;
    private String classe = "";
    private Bitmap image = null;
    private String tempPath = null;
    private ImageView imageView_result = null;
    private Button btn_good = null;
    private Button btn_bad = null;

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
                fragment.show(this.getFragmentManager(), "addServer");
                //fragment.setCreateServerListener(this);
                break;
            default:
                break;
        }
    }
}
