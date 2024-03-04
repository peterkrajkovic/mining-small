/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.load;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public class MyLoader extends DataLoader {

    public MyLoader(String path) {
        super(path);
    }

    public MyLoader(String path, String separator) {
        super(path, separator);
    }

    public MyLoader(InputStream is, String separator) {
        super(is, separator);
    }

    public MyLoader(InputStream is) {
        super(is);
    }

    /**
     * string.getByte
     */
    public MyLoader(byte[] is, String separator) {
        super(is, separator);
    }

    /**
     * string.getByte
     */
    public MyLoader(byte[] is) {
        super(is);
    }

    public MyLoader(DataSet dataset, String path, String separator) {
        super(dataset, path, separator);
    }

    @Override
    public void load() {

        load(getInputStream());
    }

    public void load(InputStream is) {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            String pom;
            boolean attrsReaded = false;
            while ((pom = br.readLine()) != null) {
                if (!attrsReaded) {
                    if (pom.trim().equals("#VAL#")) {
                        attrsReaded = true;
                        continue;
                    }
                    String[] attributeInfo = pom.split(getSeparator());
                    if (attributeInfo[0].equalsIgnoreCase("#name#")) {
                        getDataset().setName(attributeInfo[1]);
                        continue;
                    }
                    Attribute a = getAttribute(attributeInfo);
                    getDataset().addAttribute(a);
                } else {
                    String[] row = pom.split(getSeparator());
                    getDataset().addInstance(row);
                }
            }
            close(br);
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    private <T extends Attribute> T getAttribute(String[] attributeInfo) {
        T attr;
        if (attributeInfo.length == 1) {
            String[] newattributeInfo = new String[2];
            newattributeInfo[0] = attributeInfo[0];
            newattributeInfo[1] = attributeInfo[0];
            attributeInfo = newattributeInfo;
        }
        switch (attributeInfo[0].toUpperCase()) {
            case "#N#":
                attr = (T) new NumericAttr(attributeInfo[1]);
                break;
            case "#L#":
                attr = (T) new LinguisticAttr(attributeInfo[1]);
                break;
            case "#F#":
                attr = (T) new FuzzyAttr(attributeInfo[1]);
                break;
            default:
                attr = (T) new NumericAttr(attributeInfo[1]);
        }
        for (int i = 2; i < attributeInfo.length; i++) {
            attr.addValue(attributeInfo[i]);
        }
        return attr;
    }

    private void close(BufferedReader br) {
        try {
            br.close();
            getInputStream().close();
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    public static void main(String[] args) {

        String data = "#F#, A1, A11, A12, A13\n"
                + "#F#, A2, A21, A22, A23\n"
                + "#F#, A3, A31, A32, A33\n"
                + "#F#, A4, A41, A42, A43\n"
                + "#F#, B, B1, B2, B3\n"
                + "#VAL#\n"
                + "0.3,0.7,0,0.8,0.1,0.1,0.2,0.8,0,0.4,0.6,0,0.7,0.1,0.2\n"
                + "0.7,0.3,0,0.3,0.4,0.3,0.7,0.3,0,0.3,0.7,0,0.8,0.2,0\n"
                + "0.8,0.2,0,0.9,0.1,0,0.2,0.8,0,0.3,0.7,0,0.3,0.6,0.1\n"
                + "0.3,0.7,0,0.1,0.5,0.4,0.8,0.2,0,0.5,0.5,0,0.1,0.7,0.2\n"
                + "0.9,0.1,0,0.2,0.2,0.6,0.7,0.3,0,0.4,0.6,0,0.5,0.4,0.1\n"
                + "0.7,0.3,0,0.9,0.1,0,0.3,0.7,0,0.4,0.6,0,0.2,0.2,0.6\n"
                + "0,1,0,0.9,0.1,0,0.5,0.5,0,0.9,0.1,0,0,0.1,0.9\n"
                + "0.4,0.6,0,0.2,0.3,0.5,0.8,0.2,0,0.6,0.4,0,0.1,0.2,0.7\n"
                + "0.1,0.9,0,0,0.3,0.7,0.2,0.8,0,0.7,0.3,0,0.7,0.1,0.2\n"
                + "0.9,0.1,0,0.7,0.3,0,0.7,0.3,0,0.2,0.8,0,0.8,0.1,0.1\n"
                + "0.6,0.4,0,0,0.4,0.6,0.4,0.6,0,0.5,0.5,0,0.2,0.7,0.1\n"
                + "0.2,0.8,0,0.3,0.5,0.2,0.6,0.4,0,0.2,0.8,0,0.2,0.1,0.7\n"
                + "0.4,0.6,0,0.5,0.4,0.1,0.3,0.7,0,0.1,0.9,0,0,0.6,0.4\n"
                + "0.4,0.6,0,0.2,0.7,0.1,0.7,0.3,0,0.5,0.5,0,0.8,0.1,0.1\n"
                + "0.4,0.6,0,0.2,0.5,0.3,0.8,0.2,0,0.8,0.2,0,0,0.3,0.7";
        
        DataSet dt = new DataSet(new MyLoader(new ByteArrayInputStream(data.getBytes())));
        DataSet dta = new DataSet(new MyLoader("data/kubinec.txt"));

        dta.print();
    }

}
