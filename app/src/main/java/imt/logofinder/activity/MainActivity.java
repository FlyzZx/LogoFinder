package imt.logofinder.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.relinker.ReLinker;

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
import imt.logofinder.fragment.PendingDownloadDialog;

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
    private PendingDownloadDialog pendingDialog = null;
    private TextView txtStatus = null;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_options:
                Intent optionsIntent = new Intent(this, OptionsActivity.class);
                startActivity(optionsIntent);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReLinker.Logger logger = new ReLinker.Logger() {
            @Override
            public void log(String message) {
                Log.v("HODOR", "(hold the door) " + message);
            }
        };
        ReLinker.log(logger).recursively().loadLibrary(this.getBaseContext(), "jniopencv_core");
        ReLinker.log(logger).recursively().loadLibrary(this.getBaseContext(), "opencv_core");
        ReLinker.log(logger).recursively().loadLibrary(this.getBaseContext(), "jniopencv_imgcodecs");
        ReLinker.log(logger).recursively().loadLibrary(this.getBaseContext(), "opencv_imgcodecs");
        ReLinker.log(logger).recursively().loadLibrary(this.getBaseContext(), "jniopencv_imgproc");
        ReLinker.log(logger).recursively().loadLibrary(this.getBaseContext(), "opencv_imgproc");
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


        //ImageView Main
        this.imageView_main = (ImageView) findViewById(R.id.imageView_main);

        //Récupération du vocabulaire
        SharedPreferences sp = getSharedPreferences("logo", MODE_PRIVATE);
        String srv = sp.getString("choosenServer", "");
        if (!srv.equals("")) {
            this.servertest = new ServerTraining(srv, this);
            try {
                this.servertest.getRemoteFiles();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
                break;
            case R.id.btn_analyze:
                Thread predictTh = new Thread() {
                    @Override
                    public void run() {
                        try {
                            LogoFinder logoFinder = new LogoFinder();
                            logoFinder.setVocabularyDir(Environment.getExternalStorageDirectory() + "/LogoFinder");
                            logoFinder.setClassifierDir((Environment.getExternalStorageDirectory() + "/LogoFinder/Classifiers"));
                            MainActivity.this.outPath = logoFinder.predict(MainActivity.this.tempPath);
                            if (!outPath.equals("")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.this.onReturnPredict(MainActivity.this.outPath);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                predictTh.start();
                break;
            default:
                break;
        }
    }

    private void onReturnPredict(String ret) {
        //Toast.makeText(this, outPath, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ResultActivity.class);
        Bundle extras = new Bundle();
        extras.putString("EXTRA_CLASSE", ret);
        extras.putString("EXTRA_TEMPATH", this.tempPath);
        intent.putExtras(extras);
        startActivity(intent);

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
        String name = "tmp.jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir + "/" + name);
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
        Bitmap retour = this.image;
        try {
            ei = new ExifInterface(this.tempPath);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);


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

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return retour;
        }

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
                dir.createNewFile();
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


