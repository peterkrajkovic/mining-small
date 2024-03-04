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
import projectutils.ArrayUtils;
import projectutils.ProjectUtils;

/**
 *
 * @author jrabc
 */
public class CsvWriter extends DatasetWriter {

    private String separator = ",";
    private boolean header = false;

    public CsvWriter() {
        super();
    }

    public CsvWriter(boolean header) {
        super();
        setHeader(header);
    }

    public CsvWriter(DataSet dataset) {
        super(dataset);
    }

    public CsvWriter(DataSet dataset, String separator) {
        super(dataset);
        this.separator = separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    private String getHeader() {
        return ArrayUtils.toString(getDataset().getAttributeNames(), separator, false) + System.lineSeparator();
    }

    @Override
    public void save(String path) {
        FileWriter fw = null;
        try {
            File file = new File(path);
            StringBuilder sb = new StringBuilder(50000);
            if (header) {
                sb.append(getHeader());
            }
            final int dataCount = getDataset().getDataCount();
            for (int j = 0; j < dataCount; j++) {
                for (int i = 0; i < getDataset().getAtributteCount(); i++) {
                    addCellToStrBuilder(i, sb, j);
                    if (i < getDataset().getAtributteCount() - 1) {
                        if (!Attribute.isFuzzy(getDataset().getAttribute(i))) {
                            sb.append(separator);
                        }
                    }
                }
                sb.append(System.lineSeparator());
            }
            fw = new FileWriter(file);
            fw.write(sb.toString());
        } catch (IOException ex) {
            throw new Error(ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                throw new Error(ex);
            }
        }
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
                sb.append(separator);
            }
        }
    }

    public static void main(String[] args) {
        DataSet dt = DatasetFactory.getDataset(DatasetFactory.BYAS_EXAMPPLE);
        dt.print();
        DatasetWriter s = new CsvWriter(dt);
        s.save("");
    }

}
