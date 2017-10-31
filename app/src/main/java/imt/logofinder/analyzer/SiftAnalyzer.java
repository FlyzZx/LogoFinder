package imt.logofinder.analyzer;

import android.content.Context;
import android.os.Environment;

import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_features2d;
import org.bytedeco.javacpp.opencv_features2d.BFMatcher;
import org.bytedeco.javacpp.opencv_xfeatures2d;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imt.logofinder.activity.MainActivity;

import static org.bytedeco.javacpp.opencv_calib3d.RANSAC;
import static org.bytedeco.javacpp.opencv_calib3d.findHomography;
import static org.bytedeco.javacpp.opencv_core.CV_32FC2;
import static org.bytedeco.javacpp.opencv_core.CV_64FC1;
import static org.bytedeco.javacpp.opencv_core.perspectiveTransform;
import static org.bytedeco.javacpp.opencv_features2d.drawMatches;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.line;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
import static org.bytedeco.javacpp.opencv_xfeatures2d.*;

/**
 * Created by nico on 24/09/2017.
 */

public class SiftAnalyzer {
    //Valeurs par défaut : (0,3,0.04,10.0,1.6)
    private final String DB_PATH = "/logodb/";
    private final int nFeatures = 0;
    private final int nOctaveLayer = 3;
    private final double contrastThreshold = 0.04;
    private final double edgeThreshold = 10.0;
    private final double sigma = 1.6;
    private final double matchRatio = 0.8;


    private Mat image_scn = null;
    private Map<String, Map<String, Float>> refLogos = null;

    private Context context;

    public SiftAnalyzer(Context context, String image_scn) throws Exception {
        if (image_scn.isEmpty()) {
            throw new Exception("Fichier d'entrée incorrect");
        }

        this.image_scn = imread(image_scn);
        resize(this.image_scn, this.image_scn, new Size(400, 600));
        this.context = context;
        initialize();
    }

    /**
     * Fonction d'initialisation des images de reférence vers des Mat OpenCV
     */
    public void initialize() {
        refLogos = new HashMap<>();

        File dir = Environment.getExternalStorageDirectory();
        String dbPath = dir.getPath() + this.DB_PATH;
        File dbDirectory = new File(dbPath);

        File[] logos = dbDirectory.listFiles();
        for (File f : logos) {
            String name = f.getName();
            Map<String, Float> logoData = new HashMap<>();
            for (File lo : f.listFiles()) {
                logoData.put(lo.getAbsolutePath(), 0f);
            }
            refLogos.put(name, logoData);
            //refLogos.put(f.getAbsolutePath(), 0f);
        }
    }

    /**
     * Analyse l'image et renvois le chemin vers l'image de référence, ou une chaine vide si non trouvé
     */
    public String analyze() {
        float bMatch = 100000f; //Très grande distance
        String retour = "";
        for (String classes : refLogos.keySet()) {
            for (String logopath : refLogos.get(classes).keySet()) {
                Mat logo = imread(logopath);
                resize(logo, logo, new Size(400, 400));

                SIFT sift = SIFT.create(nFeatures, nOctaveLayer, contrastThreshold, edgeThreshold, sigma);
                KeyPointVector keys_img = new KeyPointVector();
                KeyPointVector keys_logo = new KeyPointVector();
                Mat desc_img = new Mat();
                Mat desc_logo = new Mat();
                sift.detectAndCompute(image_scn, new Mat(), keys_img, desc_img);
                sift.detectAndCompute(logo, new Mat(), keys_logo, desc_logo);

                BFMatcher matcher = new BFMatcher();
                DMatchVector matches = new DMatchVector();
                matcher.match(desc_img, desc_logo, matches);
                //matcher.knnMatch(desc_img, desc_logo, matches, 2);

                //Récupération des meilleurs matchs
                DMatchVector goodMatchs = new DMatchVector();
                FloatRawIndexer idx = desc_img.createIndexer();

                DMatch[] arrDm;
                int idxTab = 0, sizeTab = 0;

                for (int i = 0; i < idx.rows(); i++) {
                    if (sizeTab < 25 && (matches.get(i).distance() < matchRatio * matches.get(i + 1).distance())) {
                        sizeTab++;
                    }
                }
                arrDm = new DMatch[sizeTab];

                for (int i = 0; i < idx.rows(); i++) {
                    if (idxTab < 25 && (matches.get(i).distance() < matchRatio * matches.get(i + 1).distance())) {
                        arrDm[idxTab] = matches.get(i);
                        idxTab++;
                    }
                }
                goodMatchs.put(arrDm);

                float d = moyenneDistance(arrDm);
                refLogos.get(classes).put(logopath, d);
            }
        }
        //Récupération du meilleur match
        //Map<String, Float> moyClasses = new HashMap<>();
        for(String classes : refLogos.keySet()) {
            Float moy = 0f;
            for(String logo : refLogos.get(classes).keySet()) {
                moy += refLogos.get(classes).get(logo);
            }
            //moyClasses.put(classes, );
            moy = moy / refLogos.get(classes).size();
            if (bMatch > moy) {
                bMatch = moy;
                retour = refLogos.get(classes).keySet().iterator().next();
            }
        }
        return retour;
    }


    public float moyenneDistance(DMatch[] arrDm) {
        float distance = 0;
        for(int i = 0;i<arrDm.length;i++) {
            distance+=  arrDm[i].distance();
        }
        return distance / arrDm.length;
    }
}