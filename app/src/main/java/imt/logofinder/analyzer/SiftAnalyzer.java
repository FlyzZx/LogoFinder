package imt.logofinder.analyzer;

import android.content.Context;
import android.os.Environment;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_features2d;
import org.bytedeco.javacpp.opencv_features2d.BFMatcher;
import org.bytedeco.javacpp.opencv_ml;
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
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_ITER;
import static org.bytedeco.javacpp.opencv_core.KMEANS_PP_CENTERS;
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
    private Mat vocabulary = null;

    private Context context;
    private RemoteTraining dictionnary;
    private SIFT detector;
    private opencv_features2d.FlannBasedMatcher matcher;
    private opencv_features2d.BOWImgDescriptorExtractor bowide;
    private opencv_ml.SVM[] classifiers;


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
     * Fonction d'initialisation des images de reférence vers des descripteurs OpenCV
     *  // TODO: 16/01/2018 A modifier pour enlever le déprecated CvMat
     */
    private void initialize() {
        dictionnary = new RemoteTraining();

        //Chargement du vocabulaire
        Loader.load(opencv_core.class);
        opencv_core.CvFileStorage storage = opencv_core.cvOpenFileStorage(dictionnary.getVocabulary(), null, opencv_core.CV_STORAGE_READ);
        Pointer p = opencv_core.cvReadByName(storage, null, "vocabulary", opencv_core.cvAttrList());
        opencv_core.CvMat cvMat = new opencv_core.CvMat(p);
        vocabulary = new opencv_core.Mat(cvMat);
        opencv_core.cvReleaseFileStorage(storage);

        this.detector = SIFT.create(this.nFeatures, this.nOctaveLayer, this.contrastThreshold, this.edgeThreshold, this.sigma);
        this.matcher = new opencv_features2d.FlannBasedMatcher();
        this.bowide = new opencv_features2d.BOWImgDescriptorExtractor(detector, matcher);
        this.bowide.setVocabulary(this.vocabulary);

        classifiers = new opencv_ml.SVM[dictionnary.getBrands().size()];
        for (int i = 0 ; i < dictionnary.getBrands().size() ; i++) {
            //System.out.println("Ok. Creating class name from " + className);
            //open the file to write the resultant descriptor
            classifiers[i] = opencv_ml.SVM.create();
            classifiers[i] = opencv_ml.SVM.load(dictionnary.getBrands().get(i).getClassifier());
        }
    }

    /**
     * Analyse l'image et renvois le chemin vers l'image de référence, ou une chaine vide si non trouvé
     * // TODO: 09/01/2018 Utilisation du RemoteTraining pour BOW 
     */
    public String analyze() {
        Mat response_hist = new Mat();
        KeyPointVector keypoints = new KeyPointVector();
        Mat inputDescriptors = new Mat();
        detector.detectAndCompute(image_scn, new Mat(), keypoints, inputDescriptors);
        bowide.compute(image_scn, keypoints, response_hist);

        //Recherche du meilleur match
        float minF = Float.MAX_VALUE;
        String bestMatch = null;

        long timePrediction = System.currentTimeMillis();

        for(int i = 0; i < classifiers.length; i++) {
            float res = classifiers[i].predict(response_hist);

            if(res < minF) {
                minF = res;
                bestMatch = dictionnary.getBrands().get(i).getBrandname();
            }
        }
        timePrediction = System.currentTimeMillis() - timePrediction;

        if(bestMatch != null) {
            return bestMatch + " in " + timePrediction + " ms";
        } else return "";
    }


    public float moyenneDistance(DMatch[] arrDm) {
        float distance = 0;
        for(int i = 0;i<arrDm.length;i++) {
            distance+=  arrDm[i].distance();
        }
        return distance / arrDm.length;
    }
}