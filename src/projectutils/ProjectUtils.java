/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.UUID;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author jrabc
 */
public class ProjectUtils {

    private static DecimalFormat dfPercentage = new DecimalFormat("0.000");

    public static double logistic(double x) {
        double y = 0.0;
        if (x < -40) {
            y = 2.353853e+17;
        } else if (x > 40) {
            y = 1.0 + 4.248354e-18;
        } else {
            y = 1.0 + Math.exp(-x);
        }

        return 1.0 / y;
    }

    public static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }

    public static <T> T[] concateArray(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static double[] concateArray(double[] a, double[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        double[] c = new double[a.length + b.length];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public static double round(double value, int places) {
        if (Double.isNaN(value)) {
            return Double.NaN;
        }
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();

    }

    public static String roundAndFormat(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return formatDouble(bd.doubleValue());
    }

    public static double[][] readSimetricDoubleArray(String filename, String separator) throws IOException {
        double[][] matrix = null;

        InputStream stream = new FileInputStream(new File(filename));
        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
        String line;
        int row = 0;
        int size = 0;

        while ((line = buffer.readLine()) != null) {
            String[] vals = line.trim().split(separator);
            if (matrix == null) {
                size = vals.length;
                matrix = new double[size][size];
            }
            for (int col = 0; col < size; col++) {
                matrix[row][col] = Double.parseDouble(vals[col]);
            }
            row++;
        }
        return matrix;
    }

    public static double[][] readDoubleArray(String filename, String separator) throws IOException {

        List<String> lines = Files.readAllLines(new File(filename).toPath());
        String[] vals;
        int row = 0, rows = lines.size(), columns = lines.get(0).split(separator).length;
        double[][] matrix = new double[rows][columns];
        for (String line : lines) {
            vals = line.split(separator);
            for (int col = 0; col < columns; col++) {
                matrix[row][col] = Double.parseDouble(vals[col]);
            }
            row++;
        }
        return matrix;
    }

    public static <T extends Comparable> void quickSort(T[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    public static <T extends Comparable> void quickSort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    public static <T extends Comparable> void quickSort(double[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    public static JFrame showImage(BufferedImage bi) throws HeadlessException {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(bi)));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        return frame;
    }

    public static <T extends Comparable> void quickSort(T[] arr, int low, int high) {
        if (arr == null || arr.length == 0) {
            return;
        }
        if (low >= high) {
            return;
        }
        // pick the pivot
        int middle = low + (high - low) / 2;
        T pivot = arr[middle];
        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j) {
            while (arr[i].compareTo(pivot) < 0) {
                i++;
            }
            while (arr[j].compareTo(pivot) > 0) {
                j--;
            }
            if (i <= j) {
                T temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
        }
        if (low < j) {
            quickSort(arr, low, j);
        }
        if (high > i) {
            quickSort(arr, i, high);
        }
    }

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return "uuid = " + uuid;
    }

    public static File textToTempFile(String text) {
        PrintWriter writer = null;
        final File file = new File(generateString());
        try {

            writer = new PrintWriter(file, "UTF-8");
            writer.write(text);
        } catch (FileNotFoundException ex) {
            throw new Error(ex);
        } catch (UnsupportedEncodingException ex) {
            throw new Error(ex);
        }
        writer.close();
        file.deleteOnExit();
        return file;
    }

    public static <T> T[] concatArrays(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static <T> List<T> concatLists(List<T>... lists) {
        ArrayList<T> result = new ArrayList<>();
        for (List<T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    public static void bubbleSort(double[] arr) {
        int n = arr.length;
        double temp = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (arr[j - 1] > arr[j]) {
                    //swap elements  
                    temp = arr[j - 1];
                    arr[j - 1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
    }

    public static <T extends Comparable> void bubbleSort(T[] arr) {
        int n = arr.length;
        T temp;
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (arr[j - 1].compareTo(arr[j]) > 0) {
                    //swap elements  
                    temp = arr[j - 1];
                    arr[j - 1] = arr[j];
                    arr[j] = temp;
                }
            }
        }
    }

    public static void quickSort(int[] arr, int low, int high) {
        if (arr == null || arr.length == 0) {
            return;
        }

        if (low >= high) {
            return;
        }

        // pick the pivot
        int middle = low + (high - low) / 2;
        int pivot = arr[middle];

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j) {
            while (arr[i] < pivot) {
                i++;
            }

            while (arr[j] > pivot) {
                j--;
            }

            if (i <= j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
        }

        // recursively sort two sub parts
        if (low < j) {
            quickSort(arr, low, j);
        }

        if (high > i) {
            quickSort(arr, i, high);
        }
    }

    public static void quickSort(double[] arr, int low, int high) {
        if (arr == null || arr.length == 0) {
            return;
        }

        if (low >= high) {
            return;
        }

        // pick the pivot
        int middle = low + (high - low) / 2;
        double pivot = arr[middle];

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j) {
            while (arr[i] < pivot) {
                i++;
            }

            while (arr[j] > pivot) {
                j--;
            }

            if (i <= j) {
                double temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
        }

        // recursively sort two sub parts
        if (low < j) {
            quickSort(arr, low, j);
        }

        if (high > i) {
            quickSort(arr, i, high);
        }
    }

    public static void toClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    public static <T> List<T> getNRandomElements(List<T> lst, int n) {
        List<T> copy = new ArrayList<>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, n);
    }

    public static <T> List<T> getNRandomElements(List<T> lst, int n, SplittableRandom s) {
        List<T> copy = new ArrayList<>(lst);
        shuffle(copy, s);
        return copy.subList(0, n);
    }

    public static <T> List<T> getNRandomElements(List<T> lst, int n, Random r) {
        List<T> copy = new ArrayList<>(lst);
        Collections.shuffle(copy, r);
        return copy.subList(0, n);
    }

    public static <T> List<T> getNRandomElementsSafe(List<T> lst, int n, SplittableRandom s) {
        if (n < lst.size()) {
            return getNRandomElements(lst, n, s);
        } else {
            List<T> copy = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                copy.add(lst.get(s.nextInt(0, lst.size())));
            }
            return copy;
        }
    }

    public static double round(double value) {
        return (double) Math.round(value * 100000d) * 0.000001;
    }

    public static boolean isBetween(final double leftC, double x, final double rightC) {
        return leftC < x && x <= rightC;
    }

    public static double power(double a, double b) {
        double ret = a * b;
        if (Double.isInfinite(ret)) {
            return a < 0 || b < 0 ? 0 : 1;
        } else if (Double.isNaN(ret)) {
            return a < 0 || b < 0 ? 0 : 1;
        }
        return ret;
    }

    public static double sum(List<Double> list) {
        double sum = 0;
        for (Double d : list) {
            sum += d;
        }
        return sum;
    }

    public static double sum(float[] f) {
        double sum = 0;
        for (int i = 0; i < f.length; i++) {
            sum += f[i];
        }
        return sum;
    }

    public static double sum(double[] f) {
        double sum = 0;
        for (int i = 0; i < f.length; i++) {
            sum += f[i];
        }
        return sum;
    }

    public static int sum(int[] f) {
        int sum = 0;
        for (int i = 0; i < f.length; i++) {
            sum += f[i];
        }
        return sum;
    }

    public static int sum(boolean[] f) {
        int sum = 0;
        for (int i = 0; i < f.length; i++) {
            sum += booleanToDouble(f[i]);
        }
        return sum;
    }

    public static double booleanToDouble(boolean b) {
        if (b) {
            return 1;
        }
        return 0;
    }

    public static boolean doubleToBoolean(double b) {
        if (Double.isNaN(b)) {
            throw new Error("NaN can not be converted to boolean");
        }
        return b > 0.5;
    }

    public static boolean intToBoolean(int b) {
        return b > 0;
    }

    public static int booleanToInt(boolean b) {
        return b == false ? 0 : 1;
    }

    public static double pow2(double a) {
        double ret = a * a;
        return ret;
    }

    public static double pow(double a, int pow) {
        double p = a;
        for (int i = 0; i < pow - 1; i++) {
            p *= a;
        }
        return p;
    }

    /**
     *
     * @param a
     * @param x
     * @param b
     * @return true if a < x <= b
     */
    public static boolean isBetweenR(final double a, double x, final double b) {
        return a < x && x <= b;
    }

    /**
     *
     * @param a
     * @param x
     * @param b
     * @return true if a <= x < b
     */
    public static boolean isBetweenL(final double a, double x, final double b) {
        return a <= x && x < b;
    }

    public static boolean isBetweenLR(final double a, double x, final double b) {
        return Double.compare(a, x) <= 0 && Double.compare(x, b) <= 0;
    }


    public static void printMatrix(double[][] m) {
        ConsolePrintable.println(arrayToString(m));
    }

    public static void printAndClear(StringBuilder sb, int maxStringLength) {
        if (sb.length() > maxStringLength) {
            ConsolePrintable.print(sb.toString());
            sb.setLength(0);
        }
    }

    public static void printAndClear(StringBuilder sb, int maxStringLength, BufferedWriter out) {
        if (sb.length() > maxStringLength) {
            try {
                out.write(sb.toString());
            } catch (IOException ex) {
                throw new Error(ex);
            }
            sb.setLength(0);
        }
    }

    public static double log2(double x) {
        if (x == 0) {
            return 0;
        }
        return Math.log(x) / Math.log(2);
    }

    public static double log(double x, double z) {
        if (x == 0) {
            return 0;
        }
        return StrictMath.log(x) / StrictMath.log(z);
    }

    public static String listToString(List<Double> list) {
        String bi = "[";
        for (int i = 0; i < list.size(); i++) {
            final Double x = list.get(i);
            if (i < list.size() - 1) {
                bi += formatDouble(round(x, 3)) + ", ";
            } else {
                bi += formatDouble(round(x, 3));
            }
        }
        return bi + "]";
    }

    public static String listToString(List list, String separator) {
        String bi = "";
        for (int i = 0; i < list.size(); i++) {
            final Object x = list.get(i);
            if (i < list.size() - 1) {
                bi += x + separator;
            } else {
                bi += x;
            }
        }
        return bi;
    }

    public static String arrayToString(double[][] list) {
        StringBuilder sb = new StringBuilder();
        for (double[] ds : list) {
            sb.append(arrayToString(ds)).append(System.lineSeparator());
        }
        return sb.toString();
    }

    public static String arrayToString(double[] list) {
        String bi = "[";
        for (int i = 0; i < list.length; i++) {
            if (i < list.length - 1) {
                bi += dfPercentage.format(list[i]) + "; ";
            } else {
                bi += dfPercentage.format(list[i]);
            }
        }
        return bi + "]";
    }

    public static int randBetween(int min, int max, SplittableRandom rand) {
        return --min + rand.nextInt(max - min) + 1;
    }

    public static int randBetween(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static double randBetween(double min, double max, SplittableRandom rand) {
        return min + (rand.nextDouble() * ((max - min) + 1));
    }

    public static double randBetween(double min, double max) {
        return min + (Math.random() * ((max - min) + 1));
    }

    public static int getMaxValueIndex(List<Double> val) {
        int i = -1;
        double max = Double.NEGATIVE_INFINITY;
        for (int k = 0; k < val.size(); k++) {
            double d = val.get(k);
            if (d > max) {
                i = k;
                max = d;
            }
        }
        return i;
    }

    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }

    public static File getDirectory(File f) {
        if (f.isDirectory()) {
            return f;
        }
        return f.getParentFile();
    }

    public static int getMaxValueIndex(double[] val) {
        int i = -1;
        double max = Double.NEGATIVE_INFINITY;
        for (int k = 0; k < val.length; k++) {
            double d = val[k];
            if (d > max) {
                i = k;
                max = d;
            }
        }
        return i;
    }

    /**
     * input list is updated
     *
     * @param values
     * @return
     */
    @Deprecated
    public static List<Double> formFrom0To1(List<Double> values) {
        double max = values.parallelStream().reduce(Double::max).get();
        double min = values.parallelStream().reduce(Double::min).get();
        for (int i = 0; i < values.size(); i++) {
            double x = values.get(i);
            x = (x - min) / (max - min);
            values.set(i, x);
        }
        return values;
    }

    @Deprecated
    private static List<Double> from0To1_intoNewList(List<Double> values) {
        double max = values.parallelStream().reduce(Double::max).get();
        double min = values.parallelStream().reduce(Double::min).get();
        List<Double> newList = new LinkedList<>();
        for (int i = 0; i < values.size(); i++) {
            double x = values.get(i);
            x = (x - min) / (max - min);
            newList.add(x);
        }
        return newList;
    }

    @Deprecated
    public static List<Double> formFrom0To1(List<Double> values, boolean newList) {
        if (newList) {
            return from0To1_intoNewList(values);
        } else {
            return formFrom0To1(values);
        }
    }

    public static void sumDoubleArrays(double[] a, double[] b, double[] output) {
        for (int i = 0; i < a.length; i++) {
            output[i] = a[i] + b[i];
        }
    }

    public static void divDoubleArrayByScalar(double scalar, double[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i] / scalar;
        }
    }

    public static LinkedList<Integer> getPrimeFactors(int x) {
        LinkedList<Integer> primes = new LinkedList();
        int i = 2;
        for (; i <= x / i;) {
            while (x % i == 0) {
                primes.add(i);
                x = x / i;
            }
            i++;
        }
        if (x > 1) {
            primes.add(x);
        }
        return primes;
    }

    public static LinkedList<BigInteger> getPrimeFactors(BigInteger num) {
        LinkedList<BigInteger> primes = new LinkedList();
        BigInteger i = BigInteger.valueOf(2);
        for (; i.compareTo(num.divide(i)) <= 0;) {
            while (num.mod(i).compareTo(BigInteger.ZERO) == 0) {
                primes.add(i);
                num = num.divide(i);
            }
            i = i.add(BigInteger.ONE);
        }
        if (num.compareTo(BigInteger.ONE) > 0) {
            primes.add(num);
        }
        return primes;
    }

    /**
     * Fisher-Yates shuffle
     *
     * @param array
     * @param random
     */
    public static void shuffle(int[] array, SplittableRandom random) {
        for (int i = 0; i < array.length; i++) {
            int randomValue = i + random.nextInt(array.length - i);
            int randomElement = array[randomValue];
            array[randomValue] = array[i];
            array[i] = randomElement;
        }
    }

    public static String[] splitToArray(final String line, final char delimiter) {
        CharSequence[] temp = new CharSequence[(line.length() / 2) + 1];
        int wordCount = 0;
        int i = 0;
        int j = line.indexOf(delimiter, 0); // first substring

        while (j >= 0) {
            temp[wordCount++] = line.substring(i, j);
            i = j + 1;
            j = line.indexOf(delimiter, i); // rest of substrings
        }

        temp[wordCount++] = line.substring(i); // last substring

        String[] result = new String[wordCount];
        System.arraycopy(temp, 0, result, 0, wordCount);

        return result;
    }

    public static ArrayList<String> splitToList(final String line, final char delimiter) {
        ArrayList<String> result = new ArrayList<>();
        int i = 0;
        int j = line.indexOf(delimiter, 0); // first substring
        while (j >= 0) {
            result.add(line.substring(i, j));
            i = j + 1;
            j = line.indexOf(delimiter, i); // rest of substrings
        }
        result.add(line.substring(i)); // last substring
        return result;
    }

    public static String[] splitToArray(String[] resultArr, final String line, final char delimiter) {
        int i = 0;
        int j = line.indexOf(delimiter, 0); // first substring
        int added = 0;
        while (j >= 0) {
            resultArr[added++] = line.substring(i, j);
            i = j + 1;
            j = line.indexOf(delimiter, i); // rest of substrings
        }
        resultArr[added++] = line.substring(i); // last substring
        if (resultArr.length != added) {
            throw new Error("splitToArray: Results are shoreter than input array");
        }
        return resultArr;
    }

    /**
     * @param str
     * @return
     */
    public static boolean isRealNumber(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            ++i;
        }
        int integerPartSize = 0;
        int exponentPartSize = -1;
        while (i < length) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                if (c == '.' && integerPartSize > 0 && exponentPartSize == -1) {
                    exponentPartSize = 0;
                } else {
                    return false;
                }
            } else if (exponentPartSize > -1) {
                ++exponentPartSize;
            } else {
                ++integerPartSize;
            }
            ++i;
        }
        if ((str.charAt(0) == '0' && i > 1 && exponentPartSize < 1)
                || exponentPartSize == 0 || (str.charAt(length - 1) == '.')) {
            return false;
        }
        return true;
    }

    public static boolean isIntNumber(String s) {
        if (s == null) {
            return false;
        }
        if (s.isEmpty()) {
            return false;
        }
        s = s.trim();
        for (int a = 0; a < s.length(); a++) {
            if (a == 0 && s.charAt(a) == '-') {
                continue;
            }
            if (!Character.isDigit(s.charAt(a))) {
                return false;
            }
        }
        return true;
    }

    public static int[] getBestKIndices(double[] array, int num) {
        //create sort able array with index and value pair
        IndexValuePair[] pairs = new IndexValuePair[array.length];
        for (int i = 0; i < array.length; i++) {
            pairs[i] = new IndexValuePair(i, array[i]);
        }

        //sort
        Arrays.sort(pairs, new Comparator<IndexValuePair>() {
            public int compare(IndexValuePair o1, IndexValuePair o2) {
                return Double.compare(o2.value, o1.value);
            }
        });

        //extract the indices
        int[] result = new int[num];
        for (int i = 0; i < num; i++) {
            result[i] = pairs[i].index;
        }
        return result;
    }

    // Function to split array into two parts in Java
    public static Object[][] splitArray(Object[] inp) {

        int n = inp.length;

        Object[] a = Arrays.copyOfRange(inp, 0, (n + 1) / 2);
        Object[] b = Arrays.copyOfRange(inp, (n + 1) / 2, n);

        Object[][] array = {a, b};
        return array;
    }

    public static Object[][] splitArray(Object[] array, int chunkSize) {
        int numOfChunks = (int) Math.ceil((double) array.length / chunkSize);
        Object[][] output = new Object[numOfChunks][];

        for (int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);

            Object[] temp = new Object[length];
            System.arraycopy(array, start, temp, 0, length);
            output[i] = temp;
        }

        return output;
    }

    private static class IndexValuePair {

        private int index;
        private double value;

        public IndexValuePair(int index, double value) {
            this.index = index;
            this.value = value;
        }
    }

    public static void shuffle(List list, SplittableRandom random) {
        for (int i = 0; i < list.size(); i++) {
            int randomValue = i + random.nextInt(list.size() - i);
            Object randomElement = list.get(randomValue);
            list.set(randomValue, list.get(i));
            list.set(i, randomElement);
        }
    }

    /**
     * "##.###"
     *
     * @param d
     * @return
     */
    public static String formatDouble(double d) {
        return String.format(Locale.US, "%.3f", d);
    }

    public static String formatString(String d, int width) {
        return String.format(Locale.US, "%" + width + "s", d);
    }

    public static void clearArray(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }
    }

    public static void main(String[] args) {
        System.out.println(formatString("s", 5));
    }
}
