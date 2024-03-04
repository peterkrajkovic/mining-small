/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation;

import minig.models.Classifier;
import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public class Result {

    private Classifier classifier;
    private DataSet dt;
    private double accuracy;

    public Result(Classifier classifier, DataSet dt, double accuracy) {
        this.classifier = classifier;
        this.dt = dt;
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "classifier=" + classifier.getClass().getSimpleName() + ", dt=" + dt.getName() + ", accuracy=" + accuracy * 100 + "%\n";
    }

}
