/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.data.core.io.load.MyLoader;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class MyWriter extends DatasetWriter {

    private String separator = ",";

    public MyWriter(DataSet dataset) {
        setDataset(dataset);
    }

    @Override
    public void save(String path) {
        StringBuilder sb = new StringBuilder();
        makeHeader(sb);
        sb.append("#VAL#").append(System.lineSeparator());

        final int dataCount = getDataset().getDataCount();
        for (int j = 0; j < dataCount; j++) {
            for (int i = 0; i < getDataset().getAtributteCount(); i++) {
                addCellToStrBuilder(i, sb, j);
                if (i < getDataset().getAtributteCount() - 1) {
                    sb.append(separator);
                }
            }
            sb.append(System.lineSeparator());
        }
        write(path, sb.toString());
    }

    private void makeHeader(StringBuilder sb) {
        getDataset().forEachAttr((attr) -> {
            if (attr.isNumeric()) {
                sb.append("#N#").append(separator).append(attr.getName());
            } else if (attr.isLinguistic()) {
                sb.append("#L#").append(separator).append(attr.getName());
            } else if (attr.isFuzzy()) {
                sb.append("#F#").append(separator).append(attr.getName()).append(separator);
                for (AttrValue attrValue : attr.fuzzy().getDomain()) {
                    sb.append(attrValue.getName());
                    if (attrValue.getIndexOfValue() != attr.getDomainSize() - 1) {
                        sb.append(separator);
                    }
                }
            } else {
                System.err.println("Unknown attr type");
            }
            sb.append(System.lineSeparator());
        });
    }

    private void addCellToStrBuilder(int i, StringBuilder sb, int j) {
        Attribute attr = getDataset().getAttribute(i);
        if (Attribute.isLingvistic(attr)) {
            sb.append(((LinguisticAttr) attr).getRow(j));
        } else if (Attribute.isNumeric(attr)) {
            sb.append(((NumericAttr) attr).getAttrValue().get(j));
        } else if (Attribute.isFuzzy(attr)) {
            for (AttrValue attrValue : ((FuzzyAttr) attr).getDomain()) {
                sb.append(attrValue.get(j));
                if (attrValue.getIndexOfValue() != attr.getDomainSize() - 1) {
                    sb.append(separator);
                }
            }
        }
    }

    private void write(String path, String text) {
        try {
            FileWriter fw = new FileWriter(new File(path));
            fw.write(text);
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(DatasetFactory.DataSetCode.IRIS);
        dt.lingvisticToFuzzy();
        dt.print();
        DatasetWriter dw = new MyWriter(dt);
        dw.save("s");

        dt = new DataSet(new MyLoader("s"));
        dt.print();

    }
}
