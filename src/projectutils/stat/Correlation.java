/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat;

import java.util.Vector;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.NumericAttr;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class Correlation {

    public double covar(AttrValue x, AttrValue y) {
        double m = 0;
        double XX = 0;
        double YY = 0;
        double xMean = x.getStat().getMean();
        double yMean = y.getStat().getMean();
        for (int i = 0; i < x.getValues().size(); i++) {
            double X = x.get(i) - xMean;
            double Y = y.get(i) - yMean;
            XX += X * X;
            YY += Y * Y;
            m += (X) * (Y);
        }
        return m / (Math.sqrt(XX * YY));
    }

    public double covar(NumericAttr x, NumericAttr y) {
        double m = 0;
        double XX = 0;
        double YY = 0;
        double xMean = x.getStat().getMean();
        double yMean = y.getStat().getMean();
        for (int i = 0; i < x.getValues().size(); i++) {
            double X = x.get(i) - xMean;
            double Y = y.get(i) - yMean;
            XX += X * X;
            YY += Y * Y;
            m += (X) * (Y);
        }
        return m / (Math.sqrt(XX * YY));
    }

}
