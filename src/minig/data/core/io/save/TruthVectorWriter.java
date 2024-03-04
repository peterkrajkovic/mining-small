/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.CategoricalAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import projectutils.ErrorMessages;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class TruthVectorWriter extends DatasetWriter {

    public TruthVectorWriter(DataSet dataset) {
        super(dataset);
    }

    @Override
    public void save(String path) {
        if (getDataset().isOutputAttributeSet() && getDataset().getOutbputAttribute().isCategorical()) {
            StringBuilder sb = new StringBuilder();
            appendLine(sb, getDataset().getInputAttrCount());
            List<Attribute> attributes = getDataset().getAttributes();
            for (int i = 0; i < attributes.size(); i++) {
                Attribute attr = attributes.get(i);
                if (attr.isInputAttr()) {
                    appendLine(sb, i + 1); //index 
                    appendLine(sb, attr.getDomainSize()); //number of states
                    for (int j = 0; j < attr.getDomainSize(); j++) {
                        appendLine(sb, 1. / attr.getDomainSize());
                    }
                }
            }
            CategoricalAttr attr = getDataset().getOutbputAttribute();
            for (int i = 0; i < getDataset().getDataCount(); i++) {
                appendLine(sb, attr.getClassIndex(i));
            }
            File file = new File(path);
            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
                fw.write(sb.toString());
                fw.close();
            } catch (IOException ex) {
                throw new Error(ex);
            }
        } else {
            throw new Error(ErrorMessages.OUTPUT_ATTRIBUTE_MUST_BE_SET + " & " + ErrorMessages.OUTPUT_ATTRIBUTE_MUST_BE_CATEGORICAL);
        }

    }

    private void appendLine(StringBuilder sb, String line) {
        sb.append(line).append(System.lineSeparator());
    }

    private void appendLine(StringBuilder sb, int line) {
        sb.append(Integer.toString(line)).append(System.lineSeparator());
    }

    private void appendLine(StringBuilder sb, double line) {
        sb.append(Double.toString(line)).append(System.lineSeparator());
    }

    public static void main(String[] args) {
        DataSet d = DatasetFactory.getDataset(DatasetFactory.DataSetCode.GOLF_LINGVISTIC);
        TruthVectorWriter tw = new TruthVectorWriter(d);
        tw.save("sss");
    }
}
