/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import minig.classification.fdt.FDTu;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.CategoricalAttr;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.dataset.UCIdatasetFactory.DatasetFactory;
import minig.data.core.io.load.RabArffLoeader;
import minig.data.fuzzification.Fuzzification;
import minig.data.fuzzification.functionsset.Triangular;
import projectutils.ProjectUtils;

/**
 *
 * @author rabcan
 */
public class RabArffWriter extends DatasetWriter {

    private String stringSeparator = "'";
    private boolean useKeyWords = true;

    public RabArffWriter(DataSet dataset) {
        setDataset(dataset);
    }

    private String getKeyWords(Attribute attr) {
        if (useKeyWords) {
            switch (attr.getType()) {
                case Attribute.NUMERIC:
                    return getKeyWordsNum(attr.numeric());
                case Attribute.FUZZY:
                    return getKeyWordsNum(attr.fuzzy());
            }
        }
        return "";
    }

    public void setStringSeparator(String stringSeparator) {
        this.stringSeparator = stringSeparator;
    }

    public void setUseKeyWords(boolean useKeyWords) {
        this.useKeyWords = useKeyWords;
    }

    private String getName(Attribute attr) {
        return stringSeparator + attr.getName() + stringSeparator;
    }

    private String getDomain(CategoricalAttr attr) {
        List<String> names = attr.getDomainNames();
        String result = "";
        for (int i = 0; i < attr.getDomainNames().size(); i++) {
            result += stringSeparator + names.get(i) + stringSeparator;
            if (attr.getDomainNames().size() - 1 != i) {
                result += ",";
            }
        }
        return "{" + result + "}";
    }

    private String getKeyWordsNum(NumericAttr attr) {
        List<String> keywords = new ArrayList<>(2);
        AttrValue.Type type = attr.getAttrValue().getType();
        switch (type) {
            case BINARY:
                keywords.add("binary");
                break;
            case FLOAT:
                keywords.add("float");
                break;
        }
        if (attr.isOutputAttr()) {
            keywords.add("output");
        }
        String result = keywords.isEmpty() ? "" : "{" + ProjectUtils.listToString(keywords, ", ") + "}";
        return result;
    }

    private String getKeyWordsNum(FuzzyAttr attr) {
        List<String> keywords = new ArrayList<>(2);
        AttrValue.Type type = attr.getAttrValue(0).getType();
        switch (type) {
            case BINARY:
                keywords.add("binary");
                break;
            case FLOAT:
                keywords.add("float");
                break;
        }
        if (attr.isOutputAttr()) {
            keywords.add("output");
        }
        String result = keywords.isEmpty() ? "" : "{" + ProjectUtils.listToString(keywords, ", ") + "}";
        return result;
    }

    public void getDataDefinition(StringBuilder sb) {
        String definitions = "";
        List<Attribute> attrs = getDataset().getAttributes();
        for (Attribute attr : attrs) {
            String definition = "@attribute ";
            definition += getKeyWords(attr) + " ";
            definition += getName(attr) + " ";
            switch (attr.getType()) {
                case Attribute.NUMERIC:
                    definition += "REAL";
                    break;
                case Attribute.LINGUISTIC:
                    definition += getDomain((CategoricalAttr) attr);
                    break;
                case Attribute.FUZZY:
                    definition += "FUZZY ";
                    definition += getDomain((CategoricalAttr) attr);
                    break;
            }
            definitions += definition + System.lineSeparator();
        }
        sb.append(definitions).append(System.lineSeparator()).append(System.lineSeparator());
        sb.append("@data").append(System.lineSeparator());
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

    private void addCellToStrBuilder(int i, StringBuilder sb, int j) {
        Attribute attr = getDataset().getAttribute(i);
        if (Attribute.isLingvistic(attr)) {
            sb.append(stringSeparator).append(((LinguisticAttr) attr).getRow(j)).append(stringSeparator);
        } else if (Attribute.isNumeric(attr)) {
            sb.append(((NumericAttr) attr).getAttrValue().getInDedaultType(j));
        } else if (Attribute.isFuzzy(attr)) {
            for (AttrValue attrValue : ((FuzzyAttr) attr).getDomain()) {
                sb.append(attrValue.getInDedaultType(j));
                if (attrValue.getIndexOfValue() != attr.getDomainSize() - 1) {
                    sb.append(",");
                }
            }
        }
    }

    @Override
    public void save(String path) {
        StringBuilder sb = new StringBuilder("@Relation " + getDataset().getName());
        sb.append(System.lineSeparator()).append(System.lineSeparator());
        getDataDefinition(sb);
        final int dataCount = getDataset().getDataCount();
        for (int j = 0; j < dataCount; j++) {
            for (int i = 0; i < getDataset().getAtributteCount(); i++) {
                addCellToStrBuilder(i, sb, j);
                if (i < getDataset().getAtributteCount() - 1) {
                    sb.append(",");
                }
            }
            sb.append(System.lineSeparator());
        }
        write(path, sb.toString());
    }

    public static void main(String[] args) {
        DataSet dataSet = DatasetFactory.getDataset(0);
        dataSet.setOutputAttr();

        dataSet = Fuzzification.byKMeansWH(dataSet, new Triangular());

        RabArffWriter rw = new RabArffWriter(dataSet);
        rw.save("data/rabarff/iris_fuzzy.arff");

        dataSet = new DataSet(new RabArffLoeader("data/rabarff/iris_fuzzy.arff"));
        dataSet.print();

        FuzzyDecisionTree fdt = new FDTu();
        fdt.setDataset(dataSet);
        fdt.buildModel();
        fdt.print();
    }

}
