/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.dataset.UCIdatasetFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.SplittableRandom;
import minig.classification.fdt.FuzzyDecisionTree;
import minig.classification.fdt.FDTu;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.core.io.load.CsvLoader;
import minig.data.core.io.load.RabArffLoeader;
import minig.data.fuzzification.LingvisticToNumericParser;
import minig.data.fuzzification.functionsset.Hard;
import minig.data.fuzzification.toFuzzzy.DatasetToFuzzy;
import minig.data.fuzzification.toFuzzzy.NumericToFuzzyKmeans;
import projectutils.ProjectUtils;
import projectutils.StopWatch;

/**
 *
 * @author jrabc
 */
public class DatasetFactory {

    private static DatasetFactory factory;

    private DataSet getVrtulky() {
        DataSet dt = null;
        try {
            dt = getDtFromCSVFile("vrtulky.csv", "AirCraft Blades", ",");
        } catch (IOException ex) {
            throw new Error(ex);
        }
        dt.setOutputAttrIndex(dt.getLastAttrIndex());
        libgvisticToNumeric(dt);
        return dt;
    }

    private static void libgvisticToNumeric(DataSet dt) {
        for (int i = 0; i < dt.getAtributteCount() - 1; i++) {
            Attribute attr = dt.getAttribute(i);
            LingvisticToNumericParser ltn = new LingvisticToNumericParser(attr);
            dt.replaceAttribute(ltn.toNumeric(), i);
        }
    }

    //21 next
    public enum DataSetCode {
        IRIS, TAE, PRIMA_INDIANS_DIABETES, VOWEL, WINE_QUALITY, CAR_EVALUATION, NURSERY, WEIGHTING_rapidminer, REPLAY_SET_rapidminer,
        TIC_TAC_TOE, TIC_TAC_TOE_BIN, SONAR, LIESKOVSKY_SEM, PREDNASKA_DATAMINING, POZICKY_PRIKLAD_FUZZYFIKACIE, ID3_PRIKLAD,
        GOLF_LINGVISTIC, CARS, BYAS_EXAMPPLE, GOLF_REGRESSION_FUZZY, FLOWMETER_METER_A, GINI_EXAMPLE, VRTULKY, HEART_DISEASES,
        SPECTF_HEART, WINE, LENSES, BLOGGERS, 
        /**
         * https://mydatamodels.zendesk.com/hc/en-us/articles/360028196732-Failure-prediction
         */
        FAILURE_PREDICTION;

        static {
            IRIS.id = 0;
            TAE.id = 1;
            PRIMA_INDIANS_DIABETES.id = 2;
            VOWEL.id = 3;
            WINE_QUALITY.id = 4;
            CAR_EVALUATION.id = 12;
            NURSERY.id = 13;
            WEIGHTING_rapidminer.id = 14;
            REPLAY_SET_rapidminer.id = 16;
            TIC_TAC_TOE.id = 17;
            TIC_TAC_TOE_BIN.id = 18;
            SONAR.id = 19;
            VRTULKY.id = 22;
            HEART_DISEASES.id = 23;
            SPECTF_HEART.id = 24;
            WINE.id = 25;
            LENSES.id = 26;
            FAILURE_PREDICTION.id = 27;
            BLOGGERS.id = 28;
            //--------------------------------------------------------------------------
            LIESKOVSKY_SEM.id = 5;
            PREDNASKA_DATAMINING.id = 6;
            POZICKY_PRIKLAD_FUZZYFIKACIE.id = 7;
            ID3_PRIKLAD.id = 8;
            GOLF_LINGVISTIC.id = 9;
            CARS.id = 10;
            BYAS_EXAMPPLE.id = 11;
            GOLF_REGRESSION_FUZZY.id = 15;
            FLOWMETER_METER_A.id = 20;
            GINI_EXAMPLE.id = 21;

        }

        private short id;

        private short getId() {
            return id;
        }

        public DataSet getDataSet() {
            return getDataset(id);
        }
    }

    public static final int IRIS = 0;
    public static final int TAE = 1;
    public static final int PRIMA_INDIANS_DIABETES = 2;
    public static final int VOWEL = 3;
    public static final int WINE_QUALITY = 4;
    public static final int CAR_EVALUATION = 12;
    public static final int NURSERY = 13;
    public static final int WEIGHTING_rapidminer = 14;
    public static final int REPLAY_SET_rapidminer = 16;
    public static final int TIC_TAC_TOE = 17;
    public static final int TIC_TAC_TOE_BIN = 18;
    public static final int SONAR = 19;
    public static final int FLOWMETER_METER_A = 20;
    public static final int VRTULKY = 22;
    public static final int HEART_DISEASES = 23;
    public static final int SPECTF_HEART = 24;
    public static final int WINE = 25;
    public static final int LENSES = 26;
    public static final int FAILURE_PREDICTION = 27;
    public static final int BLOGGERS = 28;
    //--------------------------------------------------------------------------
    public static final int LIESKOVSKY_SEM = 5;
    public static final int PREDNASKA_DATAMINING = 6;
    public static final int POZICKY_PRIKLAD_FUZZYFIKACIE = 7;
    public static final int ID3_PRIKLAD = 8;
    public static final int GOLF_LINGVISTIC = 9;
    public static final int CARS = 10;
    public static final int BYAS_EXAMPPLE = 11;
    public static final int GOLF_REGRESSION_FUZZY = 15;
    public static final int GINI_EXAMPLE = 21;

    public static DatasetFactory getFactory() {
        if (factory == null) {
            factory = new DatasetFactory();
        }
        return factory;
    }

    public static DataSet getDataset(DataSetCode dataset) {
        return getFactory().createDataset(dataset.getId());
    }

    public static DataSet getDataset(int datasetId) {
        return getFactory().createDataset(datasetId);
    }

    public static DataSet getANDDataSet(int samples) {
        DataSet dt = new DataSet();
        NumericAttr attr = new NumericAttr("x1");
        NumericAttr attr2 = new NumericAttr("x2");
        LinguisticAttr output = new LinguisticAttr("Output");
        dt.addAttributes(attr, attr2, output);
        SplittableRandom rn = new SplittableRandom(10000);
        for (int i = 0; i < samples; i++) {
            double x = rn.nextLong(0, 2);
            double y = rn.nextLong(0, 2);
            int classIndex;
            if ((y == 1) && (x == 1)) {
                classIndex = 1;
            } else {
                classIndex = 0;
            }
            dt.addInstance(x, y, Integer.toString(classIndex));
        }
        dt.setOutputAttr();
        return dt;
    }

    public DataSet createDataset(int datasetId) {
        DataSet dt = null;
        try {
            switch (datasetId) {
                case IRIS:
                    return getDtFromFile("Iris.txt", "IRIS");
                case TAE:
                    return getDtFromFile("tae.txt", "TAE");
                case PRIMA_INDIANS_DIABETES:
                    return getDtFromFile("pima-indians-diabetes.txt", "PIMA INDIANS DIABETES");
                case VOWEL:
                    return getDtFromFile("vowel.txt", "VOWEL");
                case WINE_QUALITY:
                    return getDtFromFile("wine.txt", "WINE", ";");
                case LIESKOVSKY_SEM:
                    return getDtFromFile("lieskovsky.txt", "LIESKOVSKY_SEM");
                case PREDNASKA_DATAMINING:
                    return getDtFromFile("prednaskalevashenko.txt", "PREDNASKALEVASHENKO");
                case POZICKY_PRIKLAD_FUZZYFIKACIE:
                    return getPozickyDt();
                case ID3_PRIKLAD:
                    return getID3priklad();
                case GOLF_LINGVISTIC:
                    return getGolf();
                case CARS:
                    return getCarDataset();
                case BYAS_EXAMPPLE:
                    return getDtFromFile("byasExample.txt", "NAIVE BAYES EXAMPLE", ",");
                case CAR_EVALUATION:
                    return getDtFromFile("CarEvaluation.txt", "CARS EVALUATION", ",");
                case NURSERY:
                    return getDtFromFile("Nursery", "Nursery", " ");
                case WEIGHTING_rapidminer:
                    return getWeighting();
                case GOLF_REGRESSION_FUZZY:
                    return getRegresionGolf();
                case REPLAY_SET_rapidminer:
                    return getDtFromFile("Ripley-Set", "RIPLEY-SET", ";");
                case TIC_TAC_TOE:
                    return getDtFromCSVFile("tic-tac-toe.txt", "TIC TAC TOE", ",");
                case TIC_TAC_TOE_BIN:
                    return getDtFromCSVFile("tic-tac-toe-BIN", "TIC TAC TOE", " ");
                case SONAR:
                    return getSonarDT();
                case FLOWMETER_METER_A:
                    return getDtFromFile("flowmeter-MeterA.txt", "FLOWMETER - METER A", ",");
                case GINI_EXAMPLE:
                    return this.getGiniExampleDT();
                case VRTULKY:
                    return this.getVrtulky();
                case HEART_DISEASES:
                    return getDtFromArffFile("heartdiseases", "HEART DISEASES");
                case SPECTF_HEART:
                    return getDtFromFile("SPECTHEART.txt", "WINE", 0);
                case WINE:
                    DataSet dta = getDtFromArffFile("wineData", "SPECT HEART");
                    dta.setOutputAttrIndex(0);
                    return dta;
                case LENSES:
                    DataSet d = getDtFromArffFile("lenses", "Database for fitting contact lenses");
                    d.setOutputAttr();
                    return d;
                case FAILURE_PREDICTION:
                    DataSet da = getDtFromFile("failure_prediction.txt", "Failure Prediction", ",");
                    da.setOutputAttr();
                    return da;
                case BLOGGERS:
                    DataSet dss = getDtFromArffFile("blogger", "Database for fitting contact lenses");
                    dss.setOutputAttr();
                    return dss;
                default:
                    throw new Error("datasetId is not known");

            }
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    private DataSet getDtFromFile(String fileName, String name) throws IOException {
        return new DataSet("data/" + fileName) {
            {
                setOutputAttrIndex(getAtributteCount() - 1);
                setName(name);
            }
        };
    }

    private DataSet getDtFromFile(String fileName, String name, int outputAttrIndex) throws IOException {
        return new DataSet("data/" + fileName) {
            {
                setOutputAttrIndex(outputAttrIndex);
                setName(name);
            }
        };
    }

    private DataSet getDtFromArffFile(String fileName, String name) throws IOException {
        return new DataSet(new RabArffLoeader("data/" + fileName)) {
            {
                setOutputAttrIndex(getAtributteCount() - 1);
                setName(name);
            }
        };
    }

    /**
     * The original dataset from the reference consists of 5 different folders,
     * each with 100 files, with each file representing a single subject/person.
     * Each file is a recording of brain activity for 23.6 seconds. The
     * corresponding time-series is sampled into 4097 data points. Each data
     * point is the value of the EEG recording at a different point in time. So
     * we have total 500 individuals with each has 4097 data points for 23.5
     * seconds.
     * <br><br>
     * We divided and shuffled every 4097 data points into 23 chunks, each chunk
     * contains 178 data points for 1 second, and each data point is the value
     * of the EEG recording at a different point in time. So now we have 23 x
     * 500 = 11500 pieces of information(row), each information contains 178
     * data points for 1 second(column), the last column represents the label y
     * {1,2,3,4,5}.
     * * <br><br>
     * The response variable is y in column 179, the Explanatory variables X1,
     * X2, ..., X178
     * * <br><br>
     * y contains the category of the 178-dimensional input vector. Specifically
     * y in {1, 2, 3, 4, 5}:
     * * <br><br>
     * 5 - eyes open, means when they were recording the EEG signal of the brain
     * the patient had their eyes open
     * * <br><br>
     * 4 - eyes closed, means when they were recording the EEG signal the
     * patient had their eyes closed
     * * <br><br>
     * 3 - Yes they identify where the region of the tumor was in the brain and
     * recording the EEG activity from the healthy brain area
     * * <br><br>
     * 2 - They recorder the EEG from the area where the tumor was located
     * * <br><br>
     * 1 - Recording of seizure activity
     *
     * All subjects falling in classes 2, 3, 4, and 5 are subjects who did not
     * have epileptic seizure. Only subjects in class 1 have epileptic seizure.
     * Our motivation for creating this version of the data was to simplify
     * access to the data via the creation of a .csv version of it. Although
     * there are 5 classes most authors have done binary classification, namely
     * class 1 (Epileptic seizure) against the rest.
     *
     *
     * @return
     */
    public static DataSet getEEG() {
        try {
            DataSet dataset = new DataSet("data/EEG_andrejczak");
            dataset.setName("EEG");
            dataset.removeAttribue(0);
            dataset.setOutputAttr();
            return dataset;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private DataSet getDtFromFile(String datasetName, String name, String separator) throws IOException {
        return new DataSet("data/" + datasetName, separator) {
            {
                setOutputAttrIndex(getAtributteCount() - 1);
                setName(name);
            }
        };
    }

    private DataSet getDtFromCSVFile(String fileName, String datasetName, String separator) throws IOException {
        return new DataSet(new CsvLoader("data/" + fileName, separator)) {
            {
                setOutputAttrIndex(getAtributteCount() - 1);
                setName(datasetName);
            }
        };
    }

    public static DataSet getPozickyDt() {
        DataSet dt = new DataSet();
        dt.addAttribute(new NumericAttr("vek", 20, 23, 26, 29, 30, 31, 34, 36, 38, 40, 41, 44, 47, 50, 53));
        dt.addAttribute(new LinguisticAttr("rozhodnutie",true, "0", "1", "0", "0", "0", "0", "1", "1", "2", "1", "1", "2", "2", "2", "1"));
        dt.setOutputAttr();
        return dt;
    }

    public DataSet getSonarDT() {
        DataSet dt = new DataSet(new CsvLoader("data/" + "Sonar", ","));
        dt.setName("SONAR");
        dt.setOutputAttrIndex(0);
        dt.forEachInputAttr(a -> {
            LingvisticToNumericParser ltn = new LingvisticToNumericParser(a);
            dt.replaceAttribute(ltn.toNumeric(), a);
        });
        return dt;
    }

    public DataSet getWeighting() throws IOException {
        DataSet dt = new DataSet("data/" + "weighting.csv", ";");
        dt.setOutputAttrIndex(dt.getLastAttrIndex());
        dt.setName("WRIGHTING");
        return dt;
    }

    private DataSet getGiniExampleDT() {
        DataSet dt = new DataSet();
        dt.setName("Gini example");
        LinguisticAttr name = new LinguisticAttr("Name");
        name.addRows("human", "pigeon", "elephant", "leopard shark", "turtle", "penguin", "eel", "dolphin", "spiny anteater", "gila monster");

        LinguisticAttr bodyTemp = new LinguisticAttr("Body Temperature");
        bodyTemp.addAttrValues("warm-blooded", "cold-blooded");
        bodyTemp.addRows(0, 0, 0, 1, 1, 1, 1, 0, 0, 1);

        LinguisticAttr givesBirth = new LinguisticAttr("Gives Birth");
        givesBirth.addAttrValues("no", "yes");
        givesBirth.addRows(1, 0, 1, 1, 0, 0, 0, 1, 0, 0);

        LinguisticAttr fourLegged = new LinguisticAttr("Four Legged");
        fourLegged.addAttrValues("no", "yes");
        fourLegged.addRows(0, 0, 1, 0, 1, 0, 0, 0, 1, 1);

        LinguisticAttr hibernates = new LinguisticAttr("Hibernates");
        hibernates.addAttrValues("no", "yes");
        hibernates.addRows(0, 0, 0, 0, 0, 0, 0, 0, 1, 1);

        LinguisticAttr classLabel = new LinguisticAttr("Class");
        classLabel.addAttrValues("no", "yes");
        classLabel.addRows(1, 0, 1, 0, 0, 0, 0, 1, 1, 0);

        //      dt.addAttributes(name, bodyTemp, givesBirth, fourLegged, hibernates, classLabel);
        dt.addAttributes(bodyTemp, givesBirth, fourLegged, hibernates, classLabel);
        dt.setOutputAttrIndex(dt.getLastAttrIndex());
        DataSet dd = dt.getEmptyCopy();
        for (int i = 0; i < dt.getDataCount(); i++) {
            dd.addInstance(dt.getInstance(i));
        }
        return dd;
    }

    public static DataSet getID3priklad() {
        DataSet dt = new DataSet();
        LinguisticAttr attr = new LinguisticAttr("VYSKA");
        attr.addValue("nízky");
        attr.addValue("vysoký");
        attr.addValue("vysoký");
        attr.addValue("vysoký");
        attr.addValue("nízky");
        attr.addValue("vysoký");
        attr.addValue("vysoký");
        attr.addValue("nízky");
        LinguisticAttr attr3 = new LinguisticAttr("VLASY");
        attr3.addValue("blond");
        attr3.addValue("tmavé");
        attr3.addValue("blond");
        attr3.addValue("tmavé");
        attr3.addValue("tmavé");
        attr3.addValue("ryšavé");
        attr3.addValue("blond");
        attr3.addValue("blond");
        LinguisticAttr attr4 = new LinguisticAttr("OČI");
        attr4.addValue("hnedé");
        attr4.addValue("hnedé");
        attr4.addValue("modré");
        attr4.addValue("modré");
        attr4.addValue("modré");
        attr4.addValue("modré");
        attr4.addValue("hnedé");
        attr4.addValue("modré");
        LinguisticAttr attro = new LinguisticAttr("TRIEDA");
        attro.addValue("-");
        attro.addValue("-");
        attro.addValue("+");
        attro.addValue("-");
        attro.addValue("-");
        attro.addValue("+");
        attro.addValue("-");
        attro.addValue("+");
        dt.addAttribute(attr);
        dt.addAttribute(attr3);
        dt.addAttribute(attr4);
        dt.addAttribute(attro);
        dt.setOutputAttrIndex(dt.getAtributteCount() - 1);
        DataSet dd = dt.getEmptyCopy();
        for (int i = 0; i < dt.getDataCount(); i++) {
            dd.addInstance(dt.getInstance(i));
        }
        return dd;
    }

    public static DataSet getGolf() {
        LinguisticAttr play = new LinguisticAttr("play");
        LinguisticAttr Outlook = new LinguisticAttr("Outlook");
        LinguisticAttr temperature = new LinguisticAttr("temperature");
        LinguisticAttr Humidity = new LinguisticAttr("Humidity");
        LinguisticAttr wind = new LinguisticAttr("wind");
        DataSet dt = new DataSet();
        dt.addAttribute(Outlook);
        dt.addAttribute(temperature);
        dt.addAttribute(Humidity);
        dt.addAttribute(wind);
        dt.addAttribute(play);
        dt.setOutputAttrIndex(dt.getAtributteCount() - 1);
        Outlook.addValue("Sunny");
        temperature.addValue("Hot");
        Humidity.addValue("High");
        wind.addValue("Weak");
        play.addValue("No");
        Outlook.addValue("Sunny");
        temperature.addValue("Hot");
        Humidity.addValue("High");
        wind.addValue("Strong");
        play.addValue("No");
        Outlook.addValue("Overcast");
        temperature.addValue("Hot");
        Humidity.addValue("High");
        wind.addValue("Weak");
        play.addValue("Yes");
        Outlook.addValue("Rain");
        temperature.addValue("Mild");
        Humidity.addValue("High");
        wind.addValue("Weak");
        play.addValue("Yes");
        Outlook.addValue("Rain");
        temperature.addValue("Cool");
        Humidity.addValue("Normal");
        wind.addValue("Weak");
        play.addValue("Yes");
        Outlook.addValue("Rain");
        temperature.addValue("Cool");
        Humidity.addValue("Normal");
        wind.addValue("Strong");
        play.addValue("No");
        Outlook.addValue("Overcast");
        temperature.addValue("Cool");
        Humidity.addValue("Normal");
        wind.addValue("Strong");
        play.addValue("Yes");
        Outlook.addValue("Sunny");
        temperature.addValue("Mild");
        Humidity.addValue("High");
        wind.addValue("Weak");
        play.addValue("No");
        Outlook.addValue("Sunny");
        temperature.addValue("Cool");
        Humidity.addValue("Normal");
        wind.addValue("Weak");
        play.addValue("Yes");
        Outlook.addValue("Rain");
        temperature.addValue("Mild");
        Humidity.addValue("Normal");
        wind.addValue("Weak");
        play.addValue("Yes");
        Outlook.addValue("Sunny");
        temperature.addValue("Mild");
        Humidity.addValue("Normal");
        wind.addValue("Strong");
        play.addValue("Yes");
        Outlook.addValue("Overcast");
        temperature.addValue("Mild");
        Humidity.addValue("High");
        wind.addValue("Strong");
        play.addValue("Yes");
        Outlook.addValue("Overcast");
        temperature.addValue("Hot");
        Humidity.addValue("Normal");
        wind.addValue("Weak");
        play.addValue("Yes");
        Outlook.addValue("Rain");
        temperature.addValue("Mild");
        Humidity.addValue("High");
        wind.addValue("Strong");
        play.addValue("No");
        dt.initDatasetInstances();
        return dt;
    }

    public static DataSet getCarDataset() {
        DataSet dt = new DataSet();
        LinguisticAttr engine = new LinguisticAttr("Model", Arrays.asList("small", "small", "small", "medium", "large", "medium", "large", "large", "medium", "large", "small", "small", "medium", "small", "medium"));
        LinguisticAttr turbo = new LinguisticAttr("SC/Turbo", Arrays.asList("no", "no", "yes", "no", "no", "no", "yes", "no", "yes", "no", "no", "no", "no", "yes", "no"));
        LinguisticAttr weight = new LinguisticAttr("Weight", Arrays.asList("avg", "light", "avg", "heavy", "avg", "light", "heavy", "heavy", "light", "avg", "light", "avg", "heavy", "avg", "heavy"));
        LinguisticAttr fueleco = new LinguisticAttr("Fuel Eco", Arrays.asList("good", "avg", "bad", "bad", "bad", "bad", "bad", "bad", "bad", "bad", "good", "avg", "bad", "avg", "bad"));
        LinguisticAttr fast = new LinguisticAttr("fast", Arrays.asList("no", "no", "yes", "yes", "yes", "no", "no", "no", "yes", "yes", "no", "no", "no", "no", "no"));
        dt.addAttribute(engine);
        dt.addAttribute(turbo);
        dt.addAttribute(weight);
        dt.addAttribute(fueleco);
        dt.addAttribute(fast);
        dt.setOutputAttrIndex(dt.getAtributteCount() - 1);
        dt.initDatasetInstances();
        return dt;
    }

    public static DataSet getRegresionGolf() {
        DataSet dt = getGolf();
        dt.removeAttribue(dt.getLastAttrIndex());
        NumericAttr a = new NumericAttr("play", 25, 30, 46, 45, 52, 23, 43, 35, 38, 46, 48, 52, 44, 30);
        dt.addAttribute(a);
        dt.setOutputAttrIndex(dt.getLastAttrIndex());
        DataSet dd = dt.getEmptyCopy();
        for (int i = 0; i < dt.getDataCount(); i++) {
            dd.addInstance(dt.getInstance(i));
        }
        return dd;
    }

    /**
     * https://medium.com/machine-learning-researcher/dimensionality-reduction-pca-and-lda-6be91734f567
     *
     * @return
     */
    public static DataSet getPCA() {
        DataSet data = new DataSet();
        data.addAttributes(new NumericAttr("X1"), new NumericAttr("X2"));
        data.addInstance(2.5, 2.4);
        data.addInstance(0.5, 0.7);
        data.addInstance(2.2, 2.9);
        data.addInstance(1.9, 2.2);
        data.addInstance(3.1, 3.0);
        data.addInstance(2.3, 2.7);
        data.addInstance(2.0, 1.6);
        data.addInstance(1.0, 1.1);
        data.addInstance(1.5, 1.6);
        data.addInstance(1.1, 0.9);
        return data;
    }

    /**
     * http://www.sci.utah.edu/~shireen/pdfs/tutorials/Elhabian_LDA09.pdf
     *
     * @return
     */
    public static DataSet getLDA() {
        DataSet data = new DataSet();
        data.addAttributes(new NumericAttr("X1"), new NumericAttr("X2"), new LinguisticAttr("Class"));
        data.addInstance(4., 2., "1");
        data.addInstance(2., 4., "1");
        data.addInstance(2., 3., "1");
        data.addInstance(3., 6., "1");
        data.addInstance(4., 4., "1");
        data.addInstance(9., 10., "2");
        data.addInstance(6., 8., "2");
        data.addInstance(9., 5., "2");
        data.addInstance(8., 7., "2");
        data.addInstance(10., 8., "2");
        data.setOutputAttr();
        return data;
    }

    private static void test() {
      
        for (int i = 0; i < DataSetCode.values().length; i++) {
            StopWatch sw = new StopWatch();
            DataSet d = DatasetFactory.getDataset(i);
            double t = sw.getCurrentTime();
            System.out.println(ProjectUtils.formatDouble(t) + "   done " + i + ".   " + "data count = " + d.getDataCount() + "   attr count = " + d.getAtributteCount());
        }
    }

}
