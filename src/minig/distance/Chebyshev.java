/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.distance;

import java.util.List;
import minig.data.core.attribute.Attribute;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.Instance;

/**
 *
 * @author jrabc
 */
public class Chebyshev extends Distance {

    public Chebyshev(DataSet dt) {
        super(dt);
    }

    public Chebyshev() {
    }

    @Override
    public double getDistance(int id, double[] centroid) {
        double maxdistance = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < centroid.length; i++) {
            Attribute attr = getDt().getAttribute(i);
            if (Attribute.isNumeric(attr)) {
                double c = centroid[i];
                double x = (double) attr.getRow(id);
                double distance = Math.abs(c- x);
                if (distance > maxdistance) {
                    maxdistance = distance;
                }
            }
        }
        return maxdistance;
    }

    @Override
    public double getDistance(int id, int id2) {
        double maxdistance = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < getDt().getAtributteCount(); i++) {
            Attribute attr = getDt().getAttribute(i);
            if (Attribute.isNumeric(attr)) {
                double c = (double) attr.getRow(id);
                double x = (double) attr.getRow(id2);
                double distance = Math.abs(c- x);
                if (distance > maxdistance) {
                    maxdistance = distance;
                }
            }
        }
        return maxdistance;
    }

    @Override
    public double getDistance(double[] x, double[] c) {
        double maxdistance = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < x.length; i++) {
            double distance = Math.abs(c[i]- x[i]);
            if (distance > maxdistance) {
                maxdistance = distance;
            }
        }
        return maxdistance;
    }

}
