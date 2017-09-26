package imt.logofinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import imt.logofinder.R;
import imt.logofinder.analyzer.SiftAnalyzer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_CAP = 1;
    private static final int GALLERY_CAP = 2;

    private String tempPath = "";
    private Bitmap image;

    private Button btn_takePic = null;
    private Button btn_choosePic = null;
    private Button btn_analyze = null;
    private ImageView imageView_main = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Initialisation des composants
         */

        //Bouton prise de photo
        this.btn_takePic = (Button) findViewById(R.id.btn_takePic);
        this.btn_takePic.setOnClickListener(this);
        //Bouton récupération depuis gallerie
        this.btn_choosePic = (Button) findViewById(R.id.btn_choosePic);
        this.btn_choosePic.setOnClickListener(this);
        //Bouton analyse de l'image
        this.btn_analyze = (Button) findViewById(R.id.btn_analyze);
        this.btn_analyze.setOnClickListener(this);

        //ImageView Main
        this.imageView_main = (ImageView) findViewById(R.id.imageView_main);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            Intent secretDebug = new Intent(this, SecretDebugActivity.class);

            startActivity(secretDebug);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_takePic:
                takePic();
                break;
            case R.id.btn_choosePic:
                imageFromGallery();
            case R.id.btn_analyze:
                try {
                    SiftAnalyzer siftAnalyzer = new SiftAnalyzer(this, this.tempPath);
                    String outPath = siftAnalyzer.analyze();
                    Bitmap bmp = BitmapFactory.decodeFile(outPath);
                    this.imageView_main.setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //TODO Appel fonction analyse
                break;
            default:
                break;
        }
    }

    private void imageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent,
                "Select Picture"), GALLERY_CAP);
    }

    private File createImageTemp() throws IOException {
        String name = "tmp";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(name, ".jpg", storageDir);

        this.tempPath = image.getAbsolutePath();
        return image;
    }

    private void takePic() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picIntent.resolveActivity(getPackageManager()) != null) {
            File tmpFile = null;
            try {
                tmpFile = createImageTemp();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(tmpFile != null) {
                Uri tmpUri = FileProvider.getUriForFile(this, "imt.logofinder", tmpFile);
                picIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);
                startActivityForResult(picIntent, IMAGE_CAP);
            }

        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == IMAGE_CAP && resultCode == Activity.RESULT_OK) {
            if(!this.tempPath.equals("")) {
                this.image = BitmapFactory.decodeFile(this.tempPath);
                this.imageView_main.setImageBitmap(image);
            }
        } else if (requestCode == GALLERY_CAP && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            this.image = BitmapFactory.decodeFile(getRealPathFromURI(uri));
            this.imageView_main.setImageBitmap(image);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File file = new File(this.tempPath);
        file.delete();
    }
}
