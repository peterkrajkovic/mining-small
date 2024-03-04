/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evaluation.performance;

import projectutils.ConsolePrintable;
import minig.data.core.dataset.DataSet;
import minig.models.Model;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 * @param <T>
 */
public interface Performance<T extends Model> extends ConsolePrintable {

    public double appendToEvaluation();

    public double resetAndEvaluate();

    public void setModel(T c);

    public void setTestingDataset(DataSet dt);
}
