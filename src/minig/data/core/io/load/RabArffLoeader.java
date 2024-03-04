/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.load;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import projectutils.ConsolePrintable;
import projectutils.ProjectUtils;
import projectutils.StopWatch;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class RabArffLoeader extends DataLoader {

    private short state;
    private final short COMMENT = 0;
    private final short HEADER = 1;
    private final short DATA = 2;
    private int lineNumber;
    private int dataStartAt;

    private String[] tokens = null;
    // private final char delimiter = ',';

    private boolean printComments = false;

    public RabArffLoeader(String path) {
        super(path);
    }

    /**
     * string.getByte
     */
    public RabArffLoeader(byte[] is) {
        super(is);
    }

    private void parse(BufferedReader r) throws IOException {
        initParsing();
        String line;
        lineNumber = 1;
        while ((line = r.readLine()) != null) {
            try {
                parseLine(line);
            } catch (LineException e) {
                System.err.println(e.getMessage());
            }
            lineNumber++;
        }
    }

    public void setPrintComments(boolean printComments) {
        this.printComments = printComments;
    }

    private void initParsing() {
        state = COMMENT;
    }

    private void parseLine(String line) throws LineException {
        switch (state) {
            case COMMENT:
                if (!line.isEmpty() && line.charAt(0) == '%') {
                    if (line.length() >= 2) {
                        if (printComments) {
                            ConsolePrintable.println(line.substring(2));
                        }
                    }
                } else {
                    state = HEADER;
                    parseLine(line);
                }
                break;
            case HEADER:
                String lowerline = line.toLowerCase();
                if (lowerline.startsWith("@relation")) {
                    parseRelationDefinition(line);
                } else if (lowerline.startsWith("@attribute")) {
                    try {
                        parseAttributeDefinition(line);
                    } catch (LineException e) {
                        System.err.println("Warning: " + e.getMessage());
                    }
                } else if (lowerline.startsWith("@data")) {
                    tokens = new String[getDataset().getRowLength()];
                    dataStartAt = lineNumber;
                    state = DATA;
                }
                break;
            case DATA:
                if (!line.isEmpty() && line.charAt(0) != '%') {
                    parseData(line);
                }
                break;
        }
    }

    private void parseRelationDefinition(String line) {
        int i = line.indexOf(' ');
        getDataset().setName(line.substring(i + 1));
    }

    private void parseAttributeDefinition(String line) throws LineException {
        final Pattern defPattern = Pattern.compile("[\\-a-zA-Z_][\\-a-zA-Z0-9_]*|\\{[^\\}]+\\}|\'[^\']+\'|\"[^\"]+\"");
        final Scanner s = new Scanner(line);
        final String attr = s.findInLine(defPattern);
        final String keyword = s.findInLine(defPattern);
        final boolean isKeyWord = keyword.charAt(0) == '{';
        String name = isKeyWord ? s.findInLine(defPattern) : keyword;
        name = name.trim();
        name = removeAposthropes(name);
        final String type = s.findInLine(defPattern);

        ArrayList<String> keywords = null;
        if (isKeyWord) {
            keywords = ProjectUtils.splitToList(keyword.substring(1, keyword.length() - 1), ',');
        }

        if (name == null || type == null) {
            throw new LineException(lineNumber, "Attribute definition cannot be parsed");
        }

        String lowertype = type.toLowerCase();

        if (lowertype.equals("real") || lowertype.equals("numeric") || lowertype.equals("integer")) {
            handleNumeric(name, keywords);
        } else if (lowertype.equals("linguistics") || lowertype.equals("string") || (type.startsWith("{") && type.endsWith("}"))) {
            handleLingvistic(type, name, keywords);
        } else if (lowertype.equals("linguistics") || lowertype.equals("fuzzy") || lowertype.equals("string") || (type.startsWith("{") && type.endsWith("}"))) {
            if (!lowertype.equals("fuzzy")) {
                handleLingvistic(type, name, keywords);
            } else if (lowertype.equals("fuzzy")) {
                handleFuzzy(s.findInLine(defPattern), name, keywords);
            }
        } else {
            throw new LineException(lineNumber, "Attribute of type \"" + type + "\" not supported (yet)");
        }
    }

    public String removeAposthropes(String name) {
        if (name.length() == 0) {
            return name;
        }
        if (name.charAt(0) == '\'' && name.charAt(name.length() - 1) == '\'') {
            name = name.substring(1, name.length() - 1);
        }
        return name;
    }

    private void handleFuzzy(String vals, String name, ArrayList<String> keywords) {
        AttrDescriptor ad = new AttrDescriptor(keywords);
        vals = vals.substring(1, vals.length() - 1);
        vals = vals.trim();
        String[] values = vals.split("\\s*,\\s*");
        FuzzyAttr attr = new FuzzyAttr(name, values, ad.attrValueType);
        getDataset().addAttribute(attr);
        if (ad.isOutput) {
            getDataset().setOutputAttr();
        }
    }

    private void handleLingvistic(String vals, String name, ArrayList<String> keywords) {
        AttrDescriptor ad = new AttrDescriptor(keywords);
        vals = vals.substring(1, vals.length() - 1);
        LinguisticAttr attr = new LinguisticAttr(name);
        String[] values = ProjectUtils.splitToArray(vals, ',');
        if (values != null) {
            for (String value : values) {
                value = value.trim();
                attr.addAttrValue(removeAposthropes(value));
            }
        }
        getDataset().addAttribute(attr);
        if (ad.isOutput) {
            getDataset().setOutputAttr();
        }
    }

    private void handleNumeric(String name, ArrayList<String> keywords) {
        AttrDescriptor ad = new AttrDescriptor(keywords);
        NumericAttr attr = new NumericAttr(name, ad.attrValueType);
        getDataset().addAttribute(attr);
        if (ad.isOutput) {
            getDataset().setOutputAttr();
        }
    }

    private class AttrDescriptor {

        boolean isOutput = false;
        AttrValue.Type attrValueType = AttrValue.Type.DOUBLE;

        AttrDescriptor(ArrayList<String> keywords) {
            if (keywords != null) {
                for (String keyword : keywords) {
                    switch (keyword.toLowerCase().trim()) {
                        case "output":
                            isOutput = true;
                            break;
                        case "double":
                            attrValueType = AttrValue.Type.DOUBLE;
                            break;
                        case "float":
                            attrValueType = AttrValue.Type.FLOAT;
                            break;
                        case "binary":
                            attrValueType = AttrValue.Type.BINARY;
                            break;
                    }
                }
            }
        }

    }

    private void initTokens(String line) throws LineException {
        boolean inWord = false;
        boolean q = false;
        int startIndex = 0;
        int added = 0;
        for (int i = 0; i < line.length(); i++) {
            if (!inWord) {
                startIndex = i;
                if (line.charAt(i) != ' ' && line.charAt(i) != ',') {
                    inWord = true;
                    startIndex = i;
                    if (line.charAt(i) == '\'') {
                        q = true;
                    }
                }
            } else {
                if (q && line.charAt(i) == '\'') {
                    q = false;
                    tokens[added++] = line.substring(startIndex + 1, i);
                    inWord = false;
                } else if (!q && line.charAt(i) == ',') {
                    inWord = false;
                    tokens[added++] = line.substring(startIndex, i).trim();
                }
            }
        }
        if (inWord) {
            tokens[added] = line.substring(startIndex, line.length()).trim();
        }
    }

    private int getInstanceNumber() {
        return lineNumber - dataStartAt;
    }

    private void parseData(String line) throws LineException {
        if (line.charAt(0) == '{' && line.charAt(line.length() - 1) == '}') {
            throw new LineException(lineNumber, getInstanceNumber(), "Sparse data not supported (yet). Intance weighting not supported (yet)");
        } else {
            //String[] tokens = line.split("\\s*,\\s*");
            initTokens(line);

            getDataset().addInstance(tokens);
        }
    }

    @Override
    public void load() {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(getInputStream()));
            parse(bufferedReader);
            bufferedReader.close();
        } catch (IOException ex) {
            throw new Error(ex.toString());
        } finally {
            try {
                tokens = null;
                bufferedReader.close();
                getInputStream().close();
            } catch (NullPointerException | IOException ex) {
                throw new Error(ex.toString());
            }
        }
    }

    public static void main(String[] args) {

        //RabArffLoeader arff = new RabArffLoeader("D:\\Datasets\\RapidMiner\\Titanic.arff");
        //RabArffLoeader arff = new RabArffLoeader("D:\\Datasets\\cifar10\\train.arff");
        // RabArffLoeader arff = new RabArffLoeader("C:\\Users\\jrabc\\Desktop\\iris.arff");
        // RabArffLoeader arff = new RabArffLoeader("D:\\Datasets\\RapidMiner\\WineData.arff");
        String data = "% 1. Title: Iris Plants Database\n"
                + "% \n"
                + "% 2. Sources:\n"
                + "%      (a) Creator: Jan Rabcan\n"
                + "%      (c) Date: 15-4-2018\n"
                + "% \n"
                + "\n"
                + "@RELATION Iris Plants Database\n"
                + "\n"
                + "@ATTRIBUTE {binary} hmotnost	REAL\n"
                + "@ATTRIBUTE {double} 'POVA'	FUZZY {POHODAK, BLBECEK}\n"
                + "@ATTRIBUTE {output} class 	{SI SOMAR, NIE SI SOMAR}\n"
                + "\n"
                + "@DATA\n"
                + "120 , 0.574337025,0.425662975,   'SI SOMAR'\n"
                + "120,0.842237403,0.157762597,SI SOMAR\n"
                + "82,0.37105537,0.62894463,NIE SI SOMAR\n"
                + "51,0.768946436,0.231053564,SI SOMAR\n"
                + "58,0.538546775,0.461453225,SI SOMAR";

        // RabArffLoeader arff = new RabArffLoeader("data/rabarff/iris.arff");
        RabArffLoeader arff = new RabArffLoeader(data.getBytes());

        arff.setPrintComments(false);
        StopWatch sw = new StopWatch();
        arff.load();
        //  arff.getDataset().print();
        sw.printTimeAndReset("loaded: ");
        arff.getDataset().print();
        arff.getDataset().shuffleData();
        sw.printTimeAndReset("Shuffled: ");
        arff.getDataset().shuffleAttrs();
        sw.printTimeAndReset("Reordered: ");
    }

}
