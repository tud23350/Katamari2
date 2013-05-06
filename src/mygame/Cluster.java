package mygame;

import java.util.LinkedList;

/**
 *
 * @author Jake
 */
public class Cluster {

    float[][] kinectPointCloud;
    float[][] outliers;
    float clust_flag = 0;

    public Cluster(float[][] kinectPointCloud) {
        this.kinectPointCloud = kinectPointCloud;
    }

    public float[][][] clustering() {
        float[][] inliers = filterInliers();
        outliers = new float[kinectPointCloud.length - inliers.length][4];
        int counter, k = 0;
        for (int i = 0; i < kinectPointCloud.length; i++) {
            counter = 0;
            for (float[] j : inliers) {
                if (kinectPointCloud[i][0] == j[0] && kinectPointCloud[i][1] == j[1] && kinectPointCloud[i][2] == j[2]) {
                    counter++;
                }
            }
            if (counter == 0) {
                outliers[k][0] = kinectPointCloud[i][0];
                outliers[k][1] = kinectPointCloud[i][1];
                outliers[k][2] = kinectPointCloud[i][2];
                outliers[k][3] = 0;
                k++;
            }
        }

        float[][] euclid_dis = new float[outliers.length][outliers.length];
        for (int i = 0; i < euclid_dis.length; i++) {
            for (int j = 0; j < euclid_dis[0].length; j++) {
                euclid_dis[i][j] = (float) Math.sqrt(Math.abs((outliers[i][0] - outliers[j][0]) * (outliers[i][0] - outliers[j][0])
                        + (outliers[i][1] - outliers[j][1]) * (outliers[i][1] - outliers[j][1])
                        + (outliers[i][2] - outliers[j][2]) * (outliers[i][2] - outliers[j][2])));
            }
        }
        float max_dist = 40f;
        for (int i = 0; i < euclid_dis.length; i++) {
            for (int j = i; j < euclid_dis[0].length; j++) {
                if (euclid_dis[i][j] <= max_dist) {
                    if (outliers[i][3] == 0 && outliers[j][3] == 0) {
                        clust_flag++;
                        outliers[i][3] = clust_flag;
                        outliers[j][3] = clust_flag;
                    } else if (outliers[i][3] != 0 && outliers[j][3] == 0) {
                        outliers[j][3] = outliers[i][3];
                    } else if (outliers[i][3] == 0 && outliers[j][3] != 0) {
                        outliers[i][3] = outliers[j][3];
                    }
                }
            }
        }

        float[][][] clusters = new float[(int) clust_flag][outliers.length][outliers[0].length];
        float[][][] temp = new float[(int) clust_flag][outliers.length][outliers[0].length];
        int counter2 = 0, c_place = 0, cluster_limit = 100;
        for (int i = 0; i < clust_flag; i++) {
            k = 0;
            counter2 = 0;
            for (float[] a : outliers) {
                if (a[3] == i) {
                    clusters[i][k][0] = a[0];
                    clusters[i][k][1] = a[1];
                    clusters[i][k][2] = a[2];
                    k++;
                }
            }
            if(counter2 > cluster_limit){
                for(int p = 0; p < k; p++){
                    clusters[c_place][p][0] = temp[i][p][0];
                    clusters[c_place][p][1] = temp[i][p][1];
                    clusters[c_place][p][2] = temp[i][p][2];
                }
                c_place++;
            }
        }

        k = 0;
        for (float[][] object : clusters) {
            float min_X = (float) Double.POSITIVE_INFINITY;
            float max_X = (float) Double.NEGATIVE_INFINITY;
            float min_Y = (float) Double.POSITIVE_INFINITY;
            float max_Y = (float) Double.NEGATIVE_INFINITY;
            float min_Z = (float) Double.POSITIVE_INFINITY;
            float max_Z = (float) Double.NEGATIVE_INFINITY;
            for (float[] point : object) {
                if (point[0] < min_X) {
                    min_X = point[0];
                }
                if (point[0] > max_X) {
                    max_X = point[0];
                }
                if (point[1] < min_Y) {
                    min_Y = point[1];
                }
                if (point[1] > max_Y) {
                    max_Y = point[1];
                }
                if (point[2] < min_Z) {
                    min_Z = point[2];
                }
                if (point[2] > max_Z) {
                    max_Z = point[2];
                }
            }
            clusters[k][0][3] = min_X;
            clusters[k][1][3] = max_X;
            clusters[k][2][3] = min_Y;
            clusters[k][3][3] = max_Y;
            clusters[k][4][3] = min_Z;
            clusters[k][5][3] = max_Z;
            k++;
        }
        return clusters;
    }
    
        // create point-subset of inliers, from RANSAC inlier list ---
    private float[][] filterInliers() {
        LinkedList<float[]> in = RANSAC.supportingPoints;
        float[][] f = new float[in.size()][4];
        int i = 0;
        for (float[] a : in) {
            f[i][0] = a[0];
            f[i][1] = a[1];
            f[i][2] = a[2];
            f[i][3] = 0;
            i++;
        }
        //in.toArray(f);
        return (f);
    }
}
