/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils.stat;

import java.util.Vector;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.CategoricalAttr;
import minig.data.core.attribute.NumericAttr;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class Covariance {

    private boolean populationCovar = false;

    public double covar(AttrValue x, AttrValue y) {
        double covar = 0;
        double xMean = x.getStat().getMean();
        double yMean = y.getStat().getMean();
        double n = this.populationCovar ? 1d / x.getDataCount() : 1d / (x.getDataCount() - 1);
        for (int i = 0; i < x.getDataCount(); i++) {
            double X = x.get(i);
            double Y = y.get(i);
            covar += (X - xMean) * (Y - yMean);
        }
        return n * covar;
    }

    public double covar(NumericAttr x, NumericAttr y) {
        double covar = 0;
        double xMean = x.getStat().getMean();
        double yMean = y.getStat().getMean();
        double n = this.populationCovar ? 1d / x.getDataCount() : 1d / (x.getDataCount() - 1);
        for (int i = 0; i < x.getDataCount(); i++) {
            double X = x.get(i);
            double Y = y.get(i);
            covar += (X - xMean) * (Y - yMean);
        }
        return n * covar;
    }

    /**
     * default = false
     *
     * @param populationCovar
     */
    public void setPopulationCovar(boolean populationCovar) {
        this.populationCovar = populationCovar;
    }

}
