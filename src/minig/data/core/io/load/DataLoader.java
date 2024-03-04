/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.load;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import minig.data.core.dataset.DataSet;

/**
 *
 * @author jrabc
 */
public abstract class DataLoader {

    private DataSet dataset;
    private String separator = ",";

    private InputStream inputStream;

    public DataLoader(DataSet dataset, String path, String separator) {
        initFileInputStream(path);
        this.dataset = dataset;
        this.separator = separator;
        dataset = new DataSet();
    }

    public DataLoader(String path, String separator) {
        initFileInputStream(path);
        this.separator = separator;
        dataset = new DataSet();
    }

    public DataLoader(String path) {
        initFileInputStream(path);
        dataset = new DataSet();
    }

    public DataLoader() {
        dataset = new DataSet();
    }

    public DataLoader(DataSet dataset, String path) {
        initFileInputStream(path);
        this.dataset = dataset;
    }

    public DataLoader(InputStream is, String separator) {
        this.inputStream = is;
        this.separator = separator;
        dataset = new DataSet();
    }

    /**
     * string.getByte
     */
    public DataLoader(byte[] is, String separator) {
        this.separator = separator;
        inputStream = new ByteArrayInputStream(is);
        dataset = new DataSet();
    }

    /**
     * string.getByte
     */
    public DataLoader(byte[] is) {
        inputStream = new ByteArrayInputStream(is);
        dataset = new DataSet();
    }

    public DataLoader(InputStream is) {
        dataset = new DataSet();
        this.inputStream = is;
    }

    private void initFileInputStream(String path) {
        try {
            inputStream = new FileInputStream(new File(path));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public final void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public DataSet getDataset() {
        return dataset;
    }

    public abstract void load();
}
