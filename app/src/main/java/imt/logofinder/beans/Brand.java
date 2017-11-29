package imt.logofinder.beans;

import android.util.Log;

import java.util.List;

import imt.logofinder.http.HttpCallback;
import imt.logofinder.http.HttpRequest;

/**
 * Created by 41000440 on 29/11/2017.
 */

public class Brand implements HttpCallback {

    private final static Integer TAG_CLASSIFIER = 1;

    private String brandname;
    private String url;
    private String classifier;
    private List<String> images;

    public Brand(String brandname, String url, String classifier, List<String> images) {
        this.brandname = brandname;
        this.url = url;
        this.images = images;
        HttpRequest class_req = new HttpRequest(this, TAG_CLASSIFIER);
        class_req.execute(classifier);
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public void onHttpResponse(Integer tag, String data) {
        Log.d(this.brandname, data);
        if(!data.isEmpty()) {
            this.classifier = data;
        }
    }
}
