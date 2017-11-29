package imt.logofinder.analyzer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import imt.logofinder.beans.Brand;
import imt.logofinder.http.HttpCallback;
import imt.logofinder.http.HttpRequest;

/**
 * Created by 41000440 on 29/11/2017.
 */

public class RemoteTraining implements HttpCallback {

    private static final int TAG_INDEX = 1;
    private static final int TAG_VOCABULARY = 2;
    private static final int TAG_CLASSIFIER = 3;

    private final String URL_REPO = "http://www-rech.telecom-lille.fr/nonfreesift/";
    private final String URL_INDEX = URL_REPO + "index.json";
    private final String URL_CLASS = URL_REPO + "classifiers/";

    private String vocabulary = null;
    private List<Brand> brands = null;

    public RemoteTraining() {
        brands = new ArrayList<>();
        HttpRequest index = new HttpRequest(this, TAG_INDEX);
        index.execute(URL_INDEX);
    }

    @Override
    public void onHttpResponse(Integer tag, String data) {
        switch(tag) {
            case TAG_INDEX:
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    if(jsonObject.has("vocabulary")) {
                        HttpRequest voc_req = new HttpRequest(this, TAG_VOCABULARY);
                        voc_req.execute(URL_REPO + jsonObject.getString("vocabulary"));
                    }
                    if(jsonObject.has("brands")) {
                        JSONArray jArr = (JSONArray) jsonObject.get("brands");
                        for(int i = 0; i < jArr.length(); i++) {
                            JSONObject b = (JSONObject) jArr.get(i);
                            String brandname = b.getString("brandname");
                            String url = b.getString("url");
                            String class_path = URL_CLASS + b.getString("classifier");
                            List<String> img_path = new ArrayList<>();
                            JSONArray imgs = (JSONArray) b.get("images");
                            for(int j = 0; j < imgs.length(); j++) {
                                img_path.add(String.valueOf(imgs.get(j)));
                            }

                            Brand brand = new Brand(brandname, url, class_path, img_path);
                            brands.add(brand);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case TAG_VOCABULARY:
                //Log.d("VOCABULARY", data);
                this.vocabulary = data;
                break;
            default:
                break;
        }
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }
}
