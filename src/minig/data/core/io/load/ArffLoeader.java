/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.load;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Pattern;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;

/**
 * USE RabArffLoader. This one is slow, implementation is done according to
 * public source. RabArffLoader has boosted performance and extended
 * functionality.
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
@Deprecated
public class ArffLoeader extends DataLoader {

    private int state;
    private final int COMMENT = 0;
    private final int HEADER = 1;
    private final int DATA = 2;
    private int lineNumber;

    /**
     * Load an ArffFile.
     *
     * @param filename
     * @return loader
     */
    /**
     * Construct an empty ArffFile.
     *
     * @param path
     */
    public ArffLoeader(String path) {
        super(path);
    }

    private void parse(BufferedReader r) throws IOException {
        initParsing();
        String line;
        lineNumber = 1;
        boolean hadErrors = false;
        while ((line = r.readLine()) != null) {
            try {
                parseLine(line);
            } catch (LineException e) {
                hadErrors = true;
                System.err.println(e.getMessage());
            }
            lineNumber++;
        }
    }

    private void initParsing() {
        state = COMMENT;
    }

    private void parseLine(String line) throws LineException {
        switch (state) {
            case COMMENT:
                if (!line.isEmpty() && line.charAt(0) == '%') {
                    if (line.length() >= 2) {
                        //TODO print comment collectedComment.append(line.substring(2));
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

    private Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*|\\{[^\\}]+\\}|\\'[^\\']+\\'|\\\"[^\\\"]+\\\"");

    private void parseAttributeDefinition(String line) throws LineException {
        Scanner s = new Scanner(line);
        String keyword = s.findInLine(pattern);
        String name = s.findInLine(pattern);
        String type = s.findInLine(pattern);

        if (name == null || type == null) {
            throw new LineException(lineNumber, "Attribute definition cannot be parsed");
        }

        String lowertype = type.toLowerCase();

        if (lowertype.equals("real") || lowertype.equals("numeric") || lowertype.equals("integer")) {
            NumericAttr attr = new NumericAttr(name);
            getDataset().addAttribute(attr);
        } else if (lowertype.equals("string") || (type.startsWith("{") && type.endsWith("}"))) {
            type = type.substring(1, type.length() - 1);
            type = type.trim();
            LinguisticAttr attr = new LinguisticAttr(name);
            String[] values = type.split("\\s*,\\s*");
            if (values != null) {
                attr.addAttrValues(values);
            }
            getDataset().addAttribute(attr);
        } else {
            throw new LineException(lineNumber, "Attribute of type \"" + type + "\" not supported (yet)");
        }
    }

    private void parseData(String line) throws LineException {
        int attrCount = getDataset().getAtributteCount();
        if (line.charAt(0) == '{' && line.charAt(line.length() - 1) == '}') {
            throw new LineException(lineNumber, "Sparse data not supported (yet).");
        } else {
            String[] tokens = line.split("\\s*,\\s*");
            for (int i = 0; i < attrCount; i++) {
                //System.out.printf("line %d token %d: %s%n", lineno, i, tokens[i]);
                final int type = getDataset().getAttribute(i).getType();
                switch (type) {
                    case Attribute.NUMERIC:
                        double x = parseDouble(tokens, i);
                        getDataset().getAttribute(i).addRow(x);
                        break;
                    case Attribute.LINGUISTIC:
                        getDataset().getAttribute(i).addRow(tokens[i]);
                        break;
                }
            }
        }
    }

    private double parseDouble(String[] tokens, int i) {
        double x;
        try {
            x = Double.parseDouble(tokens[i]);
        } catch (NumberFormatException e) {
            x = Double.NaN;
        }
        return x;
    }

    public static void main(String[] args) {
        ArffLoeader arff = new ArffLoeader("C:\\Users\\jrabc\\Desktop\\iris.arff");
        arff.load();
        DataSet dt = arff.getDataset();
        dt.print();
    }

    @Override
    public void load() {
        try {
            parse(new BufferedReader(new InputStreamReader(getInputStream())));
            getDataset().initDatasetInstances();
        } catch (FileNotFoundException ex) {
            throw new Error(ex);
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

}
