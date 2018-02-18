package imt.logofinder.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import imt.logofinder.R;
import imt.logofinder.analyzer.LogoFinder;
import imt.logofinder.analyzer.ServerTraining;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_CAP = 1;
    private static final int GALLERY_CAP = 2;
    private static final int RETURN_PERM = 100;

    private String tempPath = "";
    private String outPath = "";
    private Bitmap image;

    private ServerTraining servertest;

    private Button btn_takePic = null;
    private Button btn_choosePic = null;
    private Button btn_analyze = null;
    private Button btn_options = null;


    private ImageView imageView_main = null;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RETURN_PERM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    this.finish();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * PERMISSIONS
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, RETURN_PERM);
        }

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
        //Bouton Options
        this.btn_options = (Button) findViewById(R.id.btn_options);
        this.btn_options.setOnClickListener(this);


        //ImageView Main
        this.imageView_main = (ImageView) findViewById(R.id.imageView_main);

        //Récupération du vocabulaire
        //TODO ASYNCTASK
        SharedPreferences sp = getSharedPreferences("logo", MODE_PRIVATE);
        String srv = sp.getString("choosenServer", "");
        if(!srv.equals("")) {
            this.servertest = new ServerTraining(srv);
            this.servertest.getRemoteFiles();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {

        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_takePic:
                // eraseFileTemp();
                takePic();
                break;
            case R.id.btn_choosePic:
                // eraseFileTemp();
                imageFromGallery();
            case R.id.btn_analyze:
                try {
                    LogoFinder logoFinder = new LogoFinder();
                    logoFinder.setVocabularyDir(Environment.getExternalStorageDirectory() + "/LogoFinder");
                    logoFinder.setClassifierDir((Environment.getExternalStorageDirectory() + "/LogoFinder/Classifiers"));
                    String outPath = logoFinder.predict(this.tempPath);
                    this.outPath = outPath;
                    if (this.outPath.equals("")) {
                        Toast.makeText(this, "Vocabulaire non detecté !", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, this.outPath, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_options:
                Intent optionsIntent = new Intent(this,OptionsActivity.class);
                startActivity(optionsIntent);
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
        if (!this.tempPath.equals("")) { //On supprime l'image temporaire si il y en a déjà une existante
            File toDel = new File(this.tempPath);
            toDel.delete();
        }
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
            if (tmpFile != null) {
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
        if (requestCode == IMAGE_CAP && resultCode == Activity.RESULT_OK) {
            if (!this.tempPath.equals("")) {
                this.image = BitmapFactory.decodeFile(this.tempPath);
                this.image = findGoodImageOrientation();
                this.imageView_main.setImageBitmap(image);
            }
        } else if (requestCode == GALLERY_CAP && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
            }
            try {
                File imgTemp = createImageTemp();
                copyFile(getRealPathFromURI(uri), imgTemp.getAbsolutePath());
                this.tempPath = imgTemp.getAbsolutePath();
                this.image = BitmapFactory.decodeFile(this.tempPath);
                this.image = findGoodImageOrientation();
                //this.imageView_main.setImageURI(uri);
                this.imageView_main.setImageBitmap(image);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void eraseFileTemp() {
        if (!this.tempPath.equals("")) {
            File file = new File(this.tempPath);
            file.delete();
        }
        if (!this.outPath.equals("")) {
            File f = new File(this.outPath);
            f.delete();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // eraseFileTemp();
    }

    public Bitmap findGoodImageOrientation() {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(this.tempPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap retour = null;
        switch (orientation) {
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

    private void copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }
}


