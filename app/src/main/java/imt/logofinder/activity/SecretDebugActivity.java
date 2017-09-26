package imt.logofinder.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import imt.logofinder.R;

public class SecretDebugActivity extends AppCompatActivity {
    ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_debug);

        this.imageView = (ImageView) findViewById(R.id.imageView_debug);
        Bundle b = this.getIntent().getExtras();
        String outPath = b.getString("imgPath");
        Bitmap bmp = BitmapFactory.decodeFile(outPath);
        this.imageView.setImageBitmap(bmp);
    }
}
