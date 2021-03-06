package imt.logofinder.analyzer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import imt.logofinder.R;
import imt.logofinder.beans.Brand;
import imt.logofinder.fragment.PendingDownloadDialog;
import imt.logofinder.http.HttpRequest;

/**
 * Created by Tom on 11/02/2018.
 */

public class ServerTraining {
    private static final String TAG = "SERVER_TRAINING";
    private String Root = "";
    private String Vocabulaire = null;
    private List<String> Classifiers = null;
    private List<Brand> brands = null;
    private Activity activityParent = null;
    private PendingDownloadDialog pendingDownloadDialog;
    private TextView txtStatusDl = null;

    private class MyAsyncTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            //txtStatusDl = pendingDownloadDialog.getTextViewStatus();
            publishProgress("Vérification de l'index...");
            if (isDownloadNeeded()) {
                deleteRecursive(new File(Environment.getExternalStorageDirectory() + "/LogoFinder"));
                Log.d(TAG, "Téléchargement des fichiers...");
                publishProgress("Téléchargement de l'index...");
                remoteIndex();
                publishProgress("Téléchargement du vocabulaire...");
                remoteVocabulary();
                publishProgress("Téléchargement des classes...");
                remoteClassifiers();
            }

            return 1;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            if(txtStatusDl == null) {
                txtStatusDl = pendingDownloadDialog.getTextViewStatus();
            }
            txtStatusDl.setText(values[0]);
        }

        @Override
        protected void onPreExecute() {
            pendingDownloadDialog = new PendingDownloadDialog();
            pendingDownloadDialog.setCancelable(false);
            pendingDownloadDialog.show(activityParent.getFragmentManager(), "DL");
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            pendingDownloadDialog.dismiss();
        }
    }

    public void getRemoteFiles() throws Exception {
        Classifiers = new ArrayList<>();
        brands = new ArrayList<>();

        try {
            MyAsyncTask sync = new MyAsyncTask();
            if(Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
                sync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.Root);
            } else {
                sync.execute(this.Root);
            }
        } catch (Exception e) {
            throw new Exception("Erreur, serveur indisponible");
        }

    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }



    private boolean isDownloadNeeded() {
        File indexJsonFile = new File(Environment.getExternalStorageDirectory() + "/LogoFinder/index.json");
        if (indexJsonFile.exists()) {
            try {
                FileReader fr = new FileReader(indexJsonFile);
                char[] bDatas = new char[(int) indexJsonFile.length()];
                fr.read(bDatas, 0, (int) indexJsonFile.length());
                fr.close();
                JSONObject jObj = new JSONObject(new String(bDatas));
                if (jObj.has("vocab_hash")) {
                    String currentHash = jObj.getString("vocab_hash");
                    //On récupère le hash distant
                    HttpRequest hashRequest = new HttpRequest();
                    String data = hashRequest.execute(Root + "index.json").get();
                    JSONObject newData = new JSONObject(data);
                    String newHash = newData.getString("vocab_hash");
                    if (newData.has("vocab_hash") && newHash.equals(currentHash)) {
                        return false;
                    }
                }
            } catch (InterruptedException | ExecutionException | IOException e) {
                return false;
            } catch (JSONException jE) {
                return true;
            }
        }
        return true;
    }

    public ServerTraining(String root, Activity parent) {
        this.Root = root;
        this.activityParent = parent;
        //getRemoteFiles();
    }

    private void remoteIndex() {
        File dicoFolder = new File(Environment.getExternalStorageDirectory(), "/LogoFinder");
        if (!dicoFolder.exists()) dicoFolder.mkdirs();//Si le dossier n'existe pas on le crée
        try {
            HttpRequest index = new HttpRequest();
            String data = index.execute(Root + "index.json").get();

            File indexJson = new File(Environment.getExternalStorageDirectory() + "/LogoFinder/index.json");
            FileWriter indexWriter = new FileWriter(indexJson);
            indexWriter.write(data);
            indexWriter.close();

            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("vocabulary")) {
                this.Vocabulaire = jsonObject.getString("vocabulary");
            }
            if (jsonObject.has("brands")) {
                JSONArray jsonArray = jsonObject.getJSONArray("brands");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject objTmp = jsonArray.getJSONObject(i);
                    if(objTmp.has("classifier")) {
                        this.Classifiers.add("classifiers/" + objTmp.getString("classifier"));
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void remoteVocabulary() {
        try {
            HttpRequest vocab = new HttpRequest();
            String data = vocab.execute(Root + Vocabulaire).get();
            File dicoFolder = new File(Environment.getExternalStorageDirectory(), "/LogoFinder");
            if (!dicoFolder.exists()) dicoFolder.mkdirs();//Si le dossier n'existe pas on le crée
            File fvoc = new File(Environment.getExternalStorageDirectory(), "/LogoFinder/vocab.yml");
            FileWriter fileWriter = new FileWriter(fvoc);
            fileWriter.write(data);
            fileWriter.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void remoteClassifiers() {
        File classifierDir = new File(Environment.getExternalStorageDirectory(), "/LogoFinder/classifiers");
        if (!classifierDir.exists()) classifierDir.mkdirs();//Si le dossier n'
        for (String classifier : this.Classifiers) {
            String filename = classifier.substring(classifier.lastIndexOf('/') + 1);
            String className = filename.replace(".xml", "");
            int a = 0;
            Brand brand = new Brand(className.replace(" ", ""), Root, Root + classifier, null);
            brands.add(brand);
        }
    }
}
