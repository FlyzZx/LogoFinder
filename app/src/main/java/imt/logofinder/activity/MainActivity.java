package imt.logofinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
    private String outPath = "";
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
            try {
                SiftAnalyzer siftAnalyzer = new SiftAnalyzer(this, this.tempPath);
                String outPath = siftAnalyzer.analyze();
                this.outPath = outPath;
                Intent secretDebug = new Intent(this, SecretDebugActivity.class);
                secretDebug.putExtra("imgPath", outPath);
                startActivity(secretDebug);
            } catch (Exception e) {
                e.printStackTrace();
            }

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

                //TODO Appel fonction analyse
                break;
            default:
                break;
        }
    }

    private void imageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_CAP);
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
                this.image = findGoodImageOrientation();
                this.imageView_main.setImageBitmap(image);
            }
        } else if (requestCode == GALLERY_CAP && resultCode == Activity.RESULT_OK) {
            Uri uri =null;
            if(data != null){
                uri = data.getData();
            }

            this.tempPath = getRealPathFromURI(uri);
            this.image = BitmapFactory.decodeFile(this.tempPath);
            this.image = findGoodImageOrientation();
            //this.imageView_main.setImageURI(uri);
            this.imageView_main.setImageBitmap(image);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File file = new File(this.tempPath);
        if(!this.outPath.equals("")) {
            File f = new File(Environment.getExternalStorageDirectory() + this.outPath);
            f.delete();
        }
        file.delete();
    }

    public Bitmap findGoodImageOrientation(){
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(this.tempPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap retour = null;
        switch(orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                retour = rotateImage(this.image, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                retour = rotateImage(this.image, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                retour = rotateImage(this.image, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                retour = this.image;
        }
        return retour;

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}


