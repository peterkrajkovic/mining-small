/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation.performance;

import projectutils.ProjectUtils;
import projectutils.stat.MeanAbsoluteError;
import projectutils.stat.RootMeanSquareDeviation;
import minig.data.core.dataset.DataSet;
import minig.models.Prediction;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class RegressionPerformance implements Performance<Prediction> {

    private DataSet dataset;
    private Prediction model;

    private RootMeanSquareDeviation rootMeanSquareError = new RootMeanSquareDeviation();
    private MeanAbsoluteError absoluteMeanError = new MeanAbsoluteError();

    public RegressionPerformance(Prediction model, DataSet dataset) {
        this.dataset = dataset;
        this.model = model;
    }

    public RegressionPerformance() {
    }

    public DataSet getDataset() {
        return dataset;
    }

    public void setTestingDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    public Prediction getModel() {
        return model;
    }

    public void setModel(Prediction model) {
        this.model = model;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append("REGRESSION BY: ").append(model.getClass().getSimpleName()).append(System.lineSeparator());;
        sb.append("Training instances: ").append(model.getDataset().getDataCount()).append(System.lineSeparator());
        sb.append("Testing  instances: ").append(dataset.getDataCount()).append(System.lineSeparator());

        sb.append("RMSE: ").append(ProjectUtils.formatDouble(this.rootMeanSquareError.get())).append(System.lineSeparator());
        sb.append("MAE : ").append(ProjectUtils.formatDouble(this.absoluteMeanError.get())).append(System.lineSeparator());
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    @Override
    public final double resetAndEvaluate() {
        for (int i = 0; i < dataset.getDataCount(); i++) {
            double res = model.predict(dataset.getInstance(i));
            double val = (double) dataset.getOutbputAttribute().getRow(i);
            rootMeanSquareError.add(val, res);
            absoluteMeanError.add(val, res);
        }
        return rootMeanSquareError.get();
    }

    @Override
    public double appendToEvaluation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
