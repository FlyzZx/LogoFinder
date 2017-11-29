package imt.logofinder.beans;

import java.util.List;
import java.util.concurrent.ExecutionException;

import imt.logofinder.http.HttpRequest;

/**
 * Created by 41000440 on 29/11/2017.
 */

public class Brand {

    private final static Integer TAG_CLASSIFIER = 1;

    private String brandname;
    private String url;
    private String classifier;
    private List<String> images;

    public Brand(String brandname, String url, String classifier, List<String> images) {
        this.brandname = brandname;
        this.url = url;
        this.images = images;
        HttpRequest class_req = new HttpRequest();
        try {
            this.classifier = class_req.execute(classifier).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
}
