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
import java.util.List;

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

    private final String DB_PATH = "/logodb/";
    private final int nFeatures = 0;
    private final int nOctaveLayer = 3;
    private final double contrastThreshold = 0.04;
    private final double edgeThreshold = 10.0;
    private final double sigma = 1.6;

    private Mat image_scn = null;
    private List<Mat> refLogos = null;

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
        refLogos = new ArrayList<>();
        File dir = Environment.getExternalStorageDirectory();
        String dbPath = dir.getPath() + this.DB_PATH;
        File dbDirectory = new File(dbPath);
        File[] logos = dbDirectory.listFiles();
        for (File f : logos) {
            Mat tmp = imread(f.getPath());
            resize(tmp, tmp, new Size(400, 400));
            refLogos.add(tmp);
        }
    }

    /**
     * Analyse l'image et renvois le chemin vers l'image de référence, ou une chaine vide si non trouvé
     */
    public String analyze() {
        for (Mat logo : refLogos) {
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
            DMatchVector goodMatchs = new DMatchVector();
            FloatRawIndexer idx = desc_img.createIndexer();

            DMatch[] arrDm;
            int idxTab = 0, sizeTab = 0;

            for (int i = 0; i < idx.rows(); i++) {
                if (sizeTab < 25 && (matches.get(i).distance() < 0.75 * matches.get(i + 1).distance())) {
                    sizeTab++;
                }
            }
            arrDm = new DMatch[sizeTab];

            for (int i = 0; i < idx.rows(); i++) {
                if (idxTab < 25 && (matches.get(i).distance() < 0.75 * matches.get(i + 1).distance())) {
                    arrDm[idxTab] = matches.get(i);
                    idxTab++;
                }
            }
            goodMatchs.put(arrDm);

            int total = (int) keys_logo.size();
            Mat queryMat = new Mat(total, 1, CV_32FC2);
            Mat trainMat = new Mat(total, 1, CV_32FC2);
            Mat mask = new Mat(total, 1, CV_32FC2);
            Mat h = new Mat(3, 3, CV_64FC1);

            int n = (int) (goodMatchs.size() / 2);

            queryMat.resize(n);
            trainMat.resize(n);
            mask.resize(n);
            FloatBuffer pt1Idx = queryMat.createBuffer();
            FloatBuffer pt2Idx = trainMat.createBuffer();
            for (int i = 0; i < n; i++) {
                Point2f p1 = keys_logo.get(goodMatchs.get(2 * i).trainIdx()).pt();
                pt1Idx.put(2 * i, p1.x());
                pt1Idx.put(2 * i + 1, p1.y());
                Point2f p2 = keys_img.get(goodMatchs.get(2 * i).queryIdx()).pt();
                pt2Idx.put(2 * i, p2.x());
                pt2Idx.put(2 * i + 1, p2.y());
            }


            h = findHomography(queryMat, trainMat, RANSAC, 5, mask, 2000, 0.1);

            Mat obj_corners = new Mat(4, 1, CV_32FC2);
            obj_corners.resize(4);
            FloatBuffer idxF = obj_corners.createBuffer();

            idxF.put(0);
            idxF.put(0);
            idxF.put(logo.cols());
            idxF.put(0);
            idxF.put(logo.cols());
            idxF.put(logo.rows());
            idxF.put(0);
            idxF.put(logo.rows());

            Mat scn_corners = new Mat(4, 1, CV_32FC2);
            scn_corners.resize(4);
            perspectiveTransform(obj_corners, scn_corners, h);

            Mat outM = new Mat();
            drawMatches(image_scn, keys_img, logo, keys_logo, goodMatchs, outM);

            idxF = scn_corners.createBuffer();
            line(outM, new Point((int) idxF.get(0), (int) idxF.get(1)), new Point((int) idxF.get(2), (int) idxF.get(3)), new Scalar(0), 7, 8, 0);
            line(outM, new Point((int) idxF.get(3), (int) idxF.get(2)), new Point((int) idxF.get(1), (int) idxF.get(0)), new Scalar(0), 7, 8, 0);

            line(outM, new Point((int) idxF.get(0), (int) idxF.get(1)), new Point((int) idxF.get(1), (int) idxF.get(0)), new Scalar(0), 7, 8, 0);
            line(outM, new Point((int) idxF.get(2), (int) idxF.get(3)), new Point((int) idxF.get(3), (int) idxF.get(2)), new Scalar(0), 7, 8, 0);

            File tmpMatch = new File(Environment.getExternalStorageDirectory() + "/out.jpg");
            try {
                if (tmpMatch.createNewFile()) {
                    imwrite(tmpMatch.getPath(), outM);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tmpMatch.getPath();

        }
        return "";
    }
}