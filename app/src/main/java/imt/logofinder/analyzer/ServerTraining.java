package imt.logofinder.analyzer;

import android.os.Environment;
import android.util.Log;

import org.bytedeco.javacpp.opencv_features2d;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import imt.logofinder.beans.Brand;
import imt.logofinder.http.HttpRequest;

/**
 * Created by Tom on 11/02/2018.
 */

public class ServerTraining {
    private static final String TAG = "SERVER_TRAINING";
    private String Root = "http://imtimagemobile.000webhostapp.com/";
    private String Vocabulaire = null;
    private List<String> Classifiers = null;
    private List<Brand> brands = null;

    public ServerTraining() {
        Classifiers = new ArrayList<>();
        brands = new ArrayList<>();

        //Peut-être mettre la version des fichiers dans l'index ?
        if (isDownloadNeeded()) {
            Log.d(TAG, "Téléchargement des fichiers...");
            File logoFinderDir = new File(Environment.getExternalStorageDirectory(), "/LogoFinder");
            if (logoFinderDir.exists())
                logoFinderDir.delete();//Si le dossier existe on le supprime pour éviter les conflits entre classifiers de train différents.
            remoteIndex();
            remoteVocabulary();
            remoteClassifiers();
        }
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
                    String currentHash = jObj.getJSONArray("vocab_hash").getString(0);
                    //On récupère le hash distant
                    HttpRequest hashRequest = new HttpRequest();
                    String data = hashRequest.execute(Root + "index.json").get();
                    JSONObject newData = new JSONObject(data);
                    String newHash = newData.getJSONArray("vocab_hash").getString(0);
                    if (newData.has("vocab_hash") && newHash.equals(currentHash)) {
                        return false;
                    }
                }
            } catch (IOException | JSONException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public ServerTraining(String root) {
        this.Root = root;
    }

    private void remoteIndex() {
        try {

            HttpRequest index = new HttpRequest();
            String data = index.execute(Root + "index.json").get();
            File indexJson = new File(Environment.getExternalStorageDirectory() + "/LogoFinder/index.json");
            FileWriter indexWriter = new FileWriter(indexJson);
            indexWriter.write(data);
            indexWriter.close();

            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("vocabulaire")) {
                this.Vocabulaire = jsonObject.getJSONArray("vocabulaire").getString(0);

            }
            if (jsonObject.has("classifiers")) {
                JSONArray jsonArray = jsonObject.getJSONArray("classifiers").getJSONArray(0);
                for (int i = 0; i < jsonArray.length(); i++) {
                    this.Classifiers.add(jsonArray.getString(i));
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
            File fvoc = new File(Environment.getExternalStorageDirectory(), "/LogoFinder/vocabulary.yml");
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
        for (String classifier : this.Classifiers) {
            String filename = classifier.substring(classifier.lastIndexOf('/') + 1);
            String className = filename.replace(".xml", "");
            int a = 0;
            Brand brand = new Brand(className.replace(" ", ""), Root, Root + classifier, null);
            brands.add(brand);
        }
    }
}
