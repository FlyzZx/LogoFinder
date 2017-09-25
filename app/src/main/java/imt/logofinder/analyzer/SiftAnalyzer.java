package imt.logofinder.analyzer;

import org.bytedeco.javacpp.opencv_core.*;

import java.util.List;

import static org.bytedeco.javacpp.opencv_imgcodecs.*;

/**
 * Created by nico on 24/09/2017.
 */

public class SiftAnalyzer {
    private Mat image_scn = null;
    private List<Mat> refLogos = null;

    public SiftAnalyzer(String image_scn) throws Exception {
        if(image_scn.isEmpty()) {
            throw new Exception("Fichier d'entrée incorrect");
        }

        this.image_scn = imread(image_scn);
    }

    /**
     * Fonction d'initialisation des images de reférence vers des Mat OpenCV
     */
    public void initialize() {

    }

    /**
     * Analyse l'image et renvois le chemin vers l'image de référence, ou une chaine vide si non trouvé
     */
    public String analyze() {

        return "";
    }
}

/*SIFT sift = null;
			sift = SIFT.create(0, 3, 0.04, 10, 1.6);
			KeyPointVector keys_img = new KeyPointVector();
			KeyPointVector keys_logo = new KeyPointVector();
			Mat desc_img = new Mat();
			Mat desc_logo = new Mat();
			sift.detectAndCompute(image, new Mat(), keys_img, desc_img);
			sift.detectAndCompute(logo, new Mat(), keys_logo, desc_logo);

			BFMatcher matcher = new BFMatcher();
			DMatchVector matches = new DMatchVector();
			matcher.match(desc_img, desc_logo, matches);
			//matcher.knnMatch(desc_img, desc_logo, matches, 2);
			DMatchVector goodMatchs = new DMatchVector();
			FloatRawIndexer idx = desc_img.createIndexer();

			DMatch[] arrDm;
			int idxTab = 0, sizeTab = 0;

			for(int i = 0; i < idx.rows(); i++) {
				if(sizeTab < 25 && (matches.get(i).distance() < 0.75 * matches.get(i + 1).distance())) {
					sizeTab++;
				}
			}
			arrDm = new DMatch[sizeTab];

			for(int i = 0; i < idx.rows(); i++) {
				if(idxTab < 25 && (matches.get(i).distance() < 0.75 * matches.get(i + 1).distance())) {
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
			for(int i = 0; i < n; i++) {
				Point2f p1 = keys_logo.get(goodMatchs.get(2 * i).trainIdx()).pt();
				pt1Idx.put(2 * i, p1.x());
				pt1Idx.put(2*i+1, p1.y());
	            Point2f p2 = keys_img.get(goodMatchs.get(2 * i).queryIdx()).pt();
	            pt2Idx.put(2*i, p2.x());
	            pt2Idx.put(2*i+1, p2.y());
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

                    printMatUINT8(obj_corners);
                    printMatUINT8(scn_corners);
                    Mat outM = new Mat();
                    drawMatches(image, keys_img, logo, keys_logo, goodMatchs, outM);

                    idxF = scn_corners.createBuffer();
                    line(outM, new Point((int) idxF.get(0), (int) idxF.get(1)) , new Point((int) idxF.get(2), (int) idxF.get(3)), new Scalar(0), 7, 8, 0);
                    line(outM, new Point((int) idxF.get(3), (int) idxF.get(2)) , new Point((int) idxF.get(1), (int) idxF.get(0)), new Scalar(0), 7, 8, 0);

                    line(outM, new Point((int) idxF.get(0), (int) idxF.get(1)) , new Point((int) idxF.get(1), (int) idxF.get(0)), new Scalar(0), 7, 8, 0);
                    line(outM, new Point((int) idxF.get(2), (int) idxF.get(3)) , new Point((int) idxF.get(3), (int) idxF.get(2)), new Scalar(0), 7, 8, 0);


                    image = outM;
                    refreshImage();*/
