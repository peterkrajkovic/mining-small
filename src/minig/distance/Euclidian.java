/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.distance;

import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.Instance;

/**
 *
 * @author jrabc
 */
public class Euclidian extends Distance {

    public Euclidian(DataSet dt) {
        super(dt);
    }

    public Euclidian() {
    }

    @Override
    public double getDistance(int id, double[] centroid) {
        double distance = 0, r;
        for (int i = 0; i < centroid.length; i++) {
            NumericAttr attr = getDt().getNumericAttrs().get(i);
            r = attr.get(id) - centroid[i];
            distance += r * r;
        }
        return Math.sqrt(distance);
    }

    @Override
    public double getDistance(int id, int id2) {
        double distance = 0, r;
        for (int i = 0; i < getDt().getNumAttrCount(); i++) {
            NumericAttr attr = getDt().getNumericAttrs().get(i);
            r = attr.get(id) - attr.get(id2);
            distance += r * r;
        }
        return Math.sqrt(distance);
    }

    /**
     * Output attribute is skipped
     *
     * @param ins
     * @param d
     * @return
     */
    public double getDistance(Instance ins, double[] d) {
        double distance = 0, c, x, r;
        double[] instList = ins.getInputNumericData();
        for (int i = 0; i < instList.length; i++) {
            c = instList[i];
            x = d[i];
            r = x - c;
            distance += r * r;
        }
        return Math.sqrt(distance);
    }

    @Override
    public double getDistance(double[] x, double[] c) {
        double distance = 0, r;
        for (int i = 0; i < c.length; i++) {
            r = x[i] - c[i];
            distance += r * r;
        }
        return Math.sqrt(distance);
    }

}
