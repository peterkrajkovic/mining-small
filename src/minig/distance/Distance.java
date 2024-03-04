/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.distance;

import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.DatasetInstance;
import minig.data.core.dataset.Instance;
import minig.data.core.dataset.NewInstance;

/**
 *
 * @author jrabc
 */
public abstract class Distance {

    public enum DistanceCode {
        Euclidian, Manhatan, Cosine, BrayCurtis, Canberra, Chebyshev, Jaccard, Minkowski, PearsonCoef,
        SorensenDice, SquaredEuclidian, WuYang, Rabcan, Afinity;

        private String name;

        static {
            Euclidian.name = "Euclidian";
            Manhatan.name = "Manhatan distance";
            Cosine.name = "Cosine distance";
            BrayCurtis.name = "BrayCurtis distance";
            Canberra.name = "Canberra distance";
            Chebyshev.name = "Chebyshev distance";
            Jaccard.name = "Jaccard distance";
            Minkowski.name = "Minkowski distance";
            PearsonCoef.name = "PearsonCoef distance";
            SorensenDice.name = "SorensenDice distance";
            SquaredEuclidian.name = "SquaredEuclidian distance";
            WuYang.name = "WuYang distance";
            Rabcan.name = "Rabcan distance";
            Afinity.name = "Afinity Propagation distance";
        }

    }

    public static Distance getDistanceByCode(final DistanceCode distance) {
        return switch (distance) {
            case Euclidian -> new Euclidian();
            case Chebyshev -> new Chebyshev();
            default -> new Euclidian();
        };
    }

    private DataSet dt;

    public Distance() {
    }

    public Distance(DataSet dt) {
        this.dt = dt;
    }

    public DataSet getDt() {
        return dt;
    }

    public void setDataset(DataSet dt) {
        this.dt = dt;
    }

    public abstract double getDistance(int id, int id2);

    public abstract double getDistance(int id, double[] centroid);

    public abstract double getDistance(double[] x, double[] c);

    public double getDistance(Instance instance, Instance instance2) {
        return getDistance(instance.getNumericData(), instance2.getNumericData());
    }

    /**
     * Dataset of distances must be the same
     *
     * @param instance
     * @param y
     * @return
     */
    public double getDistance(DatasetInstance instance, DatasetInstance y) {
        return getDistance(instance.getIndex(), y.getIndex());
    }

    public double getDistance(Instance instance, double[] y) {
        double[] instList = instance.getInputNumericData();
        return getDistance(instList, y);
    }

    public double getDistance(DatasetInstance instance, double[] y) {
        return getDistance(instance.getIndex(), y);
    }
}
