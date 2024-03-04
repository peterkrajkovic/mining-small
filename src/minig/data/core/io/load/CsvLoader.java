/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.load;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public class CsvLoader extends DataLoader {

    private boolean header = false;
    private BufferedReader br;

    public CsvLoader(String path) {
        super(path);
    }

    public CsvLoader(DataSet dataset, String path) {
        super(dataset, path);
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public CsvLoader(String path, String separator, boolean header) {
        super(path, separator);
        this.header = header;
    }

    public CsvLoader(InputStream is, String separator, boolean header) {
        super(is, separator);
        this.header = header;
    }

    public CsvLoader(byte[] string, String separator, boolean header) {
        super(string, separator);
        this.header = header;
    }

    public CsvLoader(String path, String separator) {
        super(path, separator);
    }

    public CsvLoader(DataSet dataset, String path, String separator) {
        super(dataset, path, separator);
    }

    private String[] readRow() {
        String nextline = "";
        try {
            nextline = br.readLine();
        } catch (IOException ex) {
            throw new Error(ex);
        }
        if (nextline == null) {
            return null;
        }
        return nextline.split(getSeparator());
    }

    private void createAttributes() {
        if (header) {
            for (String attrName : readRow()) {
                LinguisticAttr attr = new LinguisticAttr(attrName);
                getDataset().addAttribute(attr);
            }
        } else {
            String[] line = readRow();
            for (int i = 0; i < line.length; i++) {
                LinguisticAttr attr = new LinguisticAttr("Attr " + (i + 1));
                getDataset().addAttribute(attr);
            }
//            try {
//                br.close();
//            } catch (IOException ex) {
//                throw new Error(ex);
//            }
        }
    }

    @Override
    public void load() {
        this.br = new BufferedReader(new InputStreamReader(getInputStream()));
        createAttributes();
        String[] row;
        while ((row = readRow()) != null) {
            getDataset().addInstance(row);
        }
        try {
            br.close();
            getInputStream().close();
        } catch (IOException ex) {
            Logger.getLogger(CsvLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        String data
                = "0.3,0.7,0,0.8,0.1,0.1,0.2,0.8,0,0.4,0.6,0,0.7,0.1,0.2\n"
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

        DataSet dt = new DataSet(new CsvLoader(data.getBytes(), ",", false) {
            {
                setHeader(true);
            }
        });
        dt.print();
    }

}
