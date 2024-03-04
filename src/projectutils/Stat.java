/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import projectutils.stat.IncrementalStat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import projectutils.elementcounter.ElementsCounter;

/**
 *
 * @author rabcan
 */
public final class Stat {

    public static final double CONF_INTERVAL_70_z_VLUE = 1.04;
    public static final double CONF_INTERVAL_75_z_VLUE = 1.15;
    public static final double CONF_INTERVAL_80_z_VLUE = 1.28;
    public static final double CONF_INTERVAL_85_z_VLUE = 1.44;
    public static final double CONF_INTERVAL_90_z_VLUE = 1.645;
    public static final double CONF_INTERVAL_91_z_VLUE = 1.70;
    public static final double CONF_INTERVAL_92_z_VLUE = 1.75;
    public static final double CONF_INTERVAL_93_z_VLUE = 1.81;
    public static final double CONF_INTERVAL_94_z_VLUE = 1.88;
    public static final double CONF_INTERVAL_95_z_VLUE = 1.960;
    public static final double CONF_INTERVAL_96_z_VLUE = 2.05;
    public static final double CONF_INTERVAL_97_z_VLUE = 2.17;
    public static final double CONF_INTERVAL_98_z_VLUE = 2.33;
    public static final double CONF_INTERVAL_99_z_VLUE = 2.576;
    /**
     * 99.9; z = 3.291
     */
    public static double CONF_INTERVAL_999_z_VLUE = 3.291;

    private int pocetPrvkov;
    private double[] interval = new double[2];

    private double sumNaDruhu = 0;
    private Double sumNasobky = 0d;

    private double suma = 0d;
    private double sumHarm = 0;
    private double sumaNaTretiu = 0d;
    private double sumPtychMocnin = 0;
    private double sumPLowerMocnin = 0;

    private double p = 5; //pre generalized mean/powered mean

    private double maxPrvok = Double.MIN_VALUE;
    private double minPrvok = Double.MAX_VALUE;

    private List<Double> values = new LinkedList<>();

    public Stat() {
    }

    public Stat(Collection<Double> dataSet) {
        for (Double data : dataSet) {
            this.add(data);
        }
    }

    public Stat(Double[] dataSet) {
        for (Double data : dataSet) {
            this.add(data);
        }
    }

    private void minMax(double cislo) {
        if (cislo > maxPrvok) {
            maxPrvok = cislo;
        }
        if (cislo < minPrvok) {
            minPrvok = cislo;
        }
    }

    public void add(double cislo) {
        pocetPrvkov++;
        minMax(cislo);
        values.add(cislo);

        //-----sumy
        sumNaDruhu += Math.pow(cislo, 2);
        if ((pocetPrvkov - 1) != 0) {
            sumNasobky *= cislo;
        } else {
            sumNasobky += cislo;
        }
        suma += cislo;
        sumHarm += 1 / cislo;
        sumaNaTretiu += Math.pow(cislo, 3);
        sumPtychMocnin += Math.pow(cislo, p);
        sumPLowerMocnin += Math.pow(cislo, p - 1);
    }

    /**
     * The standard score of a raw score x
     *
     * @param x
     * @return
     */
    public double standartScore_Z_SCORE(double x) {
        return (x - getMean()) / getStdDevP();
    }

    public void reset() {
        suma = 0;
        pocetPrvkov = 0;
        interval = new double[2];
        sumNaDruhu = 0;
        maxPrvok = Double.MIN_VALUE;
        minPrvok = Double.MAX_VALUE;
        sumNasobky = 0d;
        sumHarm = 0;
        sumaNaTretiu = 0d;
        values = new LinkedList<>();
        sumPtychMocnin = 0d;
        sumPLowerMocnin = 0;
    }

    public double getMax() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return maxPrvok;
    }

    public double getMin() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return minPrvok;
    }

    /**
     * The range from the minimum to the maximum;<br><br> range = max - min
     *
     * @return rozsah
     */
    public double getRange() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return maxPrvok - minPrvok;
    }

    /**
     * In statistics, the mid-range or mid-extreme of a set of statistical data
     * values is the arithmetic mean of the maximum and minimum values in a data
     * set,
     *
     * @return (min+max)/2
     */
    public double getMidRange() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return (maxPrvok + minPrvok) / 2;
    }

    public double getInterquartileRange() {
        return getQuartile(75) - getQuartile(25);
    }

    /**
     *
     * @param number
     * @return frekvenciu zadaneho cisla
     */
    public double getFrequency(double number) {
        int pocetVyskytov = 0;
        for (double j : values) {
            if (j == number) {
                pocetVyskytov++;
            }
        }
        return pocetVyskytov;
    }

    /**
     * <b>Standard Error of mean(Infinite Population)</b>
     * <br>
     * standard error of the mean (SEM)
     *
     * @return
     */
    public double getStdErrMean() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return getStdDevP() / Math.sqrt(pocetPrvkov);
    }

    /**
     * Mean squared error MSE(mean)<br>
     * http://en.wikipedia.org/wiki/Mean_squared_error
     *
     * @return
     */
    public double getMeanSquareErr() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return Math.pow(getStdDevP() / Math.sqrt(pocetPrvkov), 2);
    }

    public int getCount() {
        return pocetPrvkov;
    }

    public double getSum() {
        return suma;
    }

    public double getSumDruhychMocnin() {
        return sumNaDruhu;
    }

    public double getSumTretichMocnin() {
        return sumaNaTretiu;
    }

    public double getSumPrevratenychHodnot() {
        return sumHarm;
    }

    public double getSumPtychMocnin() {
        return sumPtychMocnin;
    }

    public double getSumPminus1Mocnin() {
        return sumPLowerMocnin;
    }

    public double getProduct() {
        return sumNasobky;
    }

    public double getMean() {
        return suma / pocetPrvkov;
    }

    public Stat getVyhladeneDatat(int pocetBodov) {
        int start = 0;
        Stat lst = new Stat();
        int intervall = values.size() / pocetBodov;
        if (intervall > 1 && intervall < values.size() - 1) {
            for (int j = 0; j < pocetBodov; j++) {
                IncrementalStat s = new IncrementalStat();
                for (int i = start; i < start + intervall; i++) {
                    if (values.get(i) == null) {
                        break;
                    }
                    s.add(values.get(i));
                }
                start += intervall;
                if (s.getCount() > 0) {
                    lst.add(s.getMean());
                }
            }
        }
        return lst;
    }

    public List<Double> getValues() {
        return values;
    }

    /**
     *
     * @param k vráti k-tu najväčsiu hodnotu. <br><br> Príklad: k = 3 <br>dáta =
     * 4,1,5,1,1,2<br> zoradí dáta<br> 1,1,1,2,4,5<br> vráti index hodnotu na
     * tretej pozícií tj. X3 = 2
     * @return vráti k-tu najväčšiu hodnotu zo zoradených údajov. Rára aj
     * duplicitné údaje.
     */
    public double getLargeK(int k) {
        try {
            LinkedList<Double> lst = new LinkedList(values);
            Collections.sort(lst);
            return lst.get(lst.size() - (k + 1));
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    /**
     *
     * @param k vráti k-tu najmenššiu hodnotu. <br><br> Príklad: k = 3 <br>dáta
     * = 4,1,5,1,1,2<br> zoradí dáta<br> 1,1,1,2,4,5<br> vráti index hodnotu na
     * tretej pozícií tj. X3 = 1
     * @return vráti k-tu najmenššiu hodnotu zo zoradených údajov. Rára aj
     * duplicitné údaje.
     */
    public double getSmallK(int k) {
        try {
            LinkedList<Double> lst = new LinkedList(values);
            Collections.sort(lst);
            return lst.get(k);
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    /**
     * In mathematics, a contraharmonic mean is a function complementary to the
     * harmonic mean. The contraharmonic mean is a special case of the Lehmer
     * mean, L_p, where p=2.
     * <br><br>
     * The contraharmonic mean of a set of positive numbers is defined as the
     * arithmetic mean of the squares of the numbers divided by the arithmetic
     * mean of the numbers:
     * <br>suma^2 / suma
     *
     * @return
     */
    public double getAvgContraharmonic() {
        return sumNaDruhu / suma;
    }

    /**
     * . Pre tento priemer je potrebné mať nasetované P.
     *
     * @return Vráti lahmerov priemer.
     */
    public double getAvgLahmer() {
        return sumPtychMocnin / sumPLowerMocnin;
    }

    public double getAvgGenralized() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return Math.pow((1d / pocetPrvkov) * sumPtychMocnin, 1d / p);
    }

    /**
     * <b> Geometrický priemer </b>* <br>nefunguje pre veľký počet prvkov<br>
     * <br><br>
     * Majme súbor s rozsahom n, vytvorený kladnými hodnotami. Geometrický
     * priemer z nevytriedených dát je n– tá odmocnina z ich súčinu.
     * <br> Je zrejmé, že geometrický priemer má zmysel iba pre dáta, v ktorých
     * sú všetky hodnoty kladné čísla.
     * <br><br>
     * Geometrický priemer je vhodnejšou mierou polohy pre pomerovú premennú
     * (špeciálny typ intervalovej premennej s bodom absolútnej nuly, pod ktorú
     * hodnota premennej nemôže klesnúť - napr. hmotnosť, výška, vek)
     * s pozitívnou šikmosťou (napr. rozdelenie príjmov obyvateľstva).
     * <br><br>
     * Geometrický priemer sa často používa v ekonómii a biológii, keď je
     * premenná skôr súčinom ako súčtom mnohých malých efektov (logaritmus
     * premennej má bližšie k symetrickému normálnemu rozdeleniu ako samotná
     * premenná). Vypočíta sa ako n-tá odmocnina súčinu všetkých hodnôt:
     *
     * @return Geometrický priemer
     */
    public double getGeometricalMean() {
        if (pocetPrvkov > 0) {
            return Math.pow(sumNasobky, 1d / pocetPrvkov);
        } else {
            return Double.NaN;
        }
    }

    /**
     * <b>Harmonický priemer</b> (vždy z nenulových a kladných hodnôt) je
     * definovaný ako podiel početu prvkov a sumy prevrátených hodnôt
     * <br><br><br>
     * Harmonický priemer sa používa na charakterizovanie hodnôt, ktoré
     * predstavujú napríklad výkonové limity – teda dosiahnuť u každej osoby ten
     * istý výkon pri rôznom čase alebo rôzny výkon za jednotku času (1. osoba
     * urobí prácu za hod, teda jej hodinový výkon je, …,atď.)
     *
     * <br><br>
     * PRÍKLAD
     * <br><br>Harmonický priemer sa používa na výpočet priemernej rýchlosti ak
     * sú vzdialenosti konštantné a čas premenlivý. V prípade rôznych
     * vzdialeností a rovnakých časov sa však musí použiť aritmetický priemer.
     * Priemerná rýchlosť auta, ktoré išlo 2 hodiny rýchlosťou 90 km/h a ďalšie
     * 2 hodiny 130 km/h sa rovná: (90+130)/2=110 km/h. Do výpočtu harmonického
     * priemeru možno zahrnúť iba nenulové hodnoty.
     * <br><br>
     *
     * @return Harmonický priemer
     */
    public double getHarmonicalMean() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return pocetPrvkov / sumHarm;
    }

    /**
     * <b>Kvadratický priemer</b> Je to (pre ľubovoľné reálne čísla) odmocnina
     * zo súčtu štvorcov jednotlivých hodnôt, delená počtom meraní n.
     * <br><br>
     * Kvadratický priemer sa obyčajne používa vo fyzike, kde sa často označuje
     * ako efektívna hodnota.
     * <br><br>
     *
     * @return Kvadratický priemer
     */
    public double getQuadraticMean() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return Math.sqrt(sumNaDruhu / pocetPrvkov);
    }

    /**
     * <b>Kubický priemer</b> It is used for predicting the life expectancy of
     * machine parts.
     * <br><br>
     *
     * @return Kubický priemer
     */
    public double getCubicMean() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return Math.pow((1d / pocetPrvkov) * sumaNaTretiu, 1d / 3);
    }

    /**
     * Pozor!!! funkcia je pomala. Cely statisticky subor sa musi zoradit. Zo
     * zoradeneho stat suboru vrati prostrednut hodnotu.
     *
     * @return median stat. suboru
     */
    public double getMedian() {
        if (!values.isEmpty()) {
            LinkedList<Double> lst = new LinkedList(values);
            Collections.sort(lst);

            if (values.size() % 2 == 0) {
                return ((double) lst.get(lst.size() / 2) + (double) lst.get((lst.size() / 2) - 1)) / 2;
            } else {
                return (double) lst.get(lst.size() / 2);
            }
        } else {
            return Double.NaN;
        }
    }

    /**
     * <b>Variační koeficient</b> predstavuje relatívnu mieru variability.
     * Používa sa na porovnávanie variability medzi súbormi dát s odlišnými
     * priemermi. Variačný koeficient výšky vzorky ľudí bude rovnaký bez ohľadu
     * na to, či výšku budeme vyjadrovať v centimetroch alebo metroch. Vypočíta
     * sa ako podiel štandardnej odchýlky a priemeru.
     *
     *
     * @return Variační koeficient
     */
    public double getCoefOfVariation() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return getStdDevP() / Math.abs(getMean());
    }

    /**
     * Pozor!!! funkcia je pomala. Cely statisticky subor sa musi prejst a
     * ulozit zakazdym do countera. Z countera sa potom vyberie max!!!
     *
     * @return modus stat. suboru
     */
    public double getMode() {
        ElementsCounter<Double> a = new ElementsCounter();
        for (double j : values) {
            a.addElement(j);
        }
        if (a.getMax() == null) {
            return Double.NaN;
        }
        return a.getMax();
    }

    /**
     * Variačné rozpätie je rozdiel medzi maximálnou a minimálnou hodnotou
     * štatistického znaku v jednom štatistickom súbore.
     *
     * @return Variačné rozpätie, ang asni range
     */
    public double getVariacneRozpetie() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        return getMax() - getMin();
    }

    /**
     *
     * @return rozptyl stat. suboru
     */
    public double getVarianceP() {
        if (pocetPrvkov == 0) {
            return Double.NaN;
        }
        return (sumNaDruhu - ((suma * suma) / pocetPrvkov)) / (pocetPrvkov);
    }

    /**
     * Výberový rozptyl, v excely vzorec VARP
     *
     * @return Výberový rozptyl
     */
    public double getVarianceS() {
        if (pocetPrvkov == 0) {
            return Double.NaN;
        }
        return (sumNaDruhu - ((suma * suma) / (pocetPrvkov))) / (pocetPrvkov - 1);
    }

    /**
     * The Population Standard Deviation
     *
     * @return
     */
    public double getStdDevP() {
        if (pocetPrvkov < 1) {
            return Double.NaN;
        }
        return Math.sqrt(getVarianceP());
    }

    /**
     * @return relatívna smerodajná odchýlka [ (stddev/mean)*100]
     */
    public double getRelativeStdDev() {
        return (getStdDevP() / getMean()) * 100;
    }

    /**
     * Výberová smerodajná odchýlka, v excely vzorec STDEVP
     * <br> * The sample Standard Deviation
     *
     * @return Výberová smerodajná odchýlka
     */
    public double getStdDevS() {
        if (pocetPrvkov < 1) {
            return Double.NaN;
        }
        return Math.sqrt(getVarianceS());
    }

    /**
     * Priemerná odchýlka štatistického súboru. POMALE!!!
     * <br><br>
     * Vzorec: (sum(|Xi - X|))/n; //[X == mean]
     * <br><br> pri výpočte využíva aritmetický priemer. Ideálne je to počítať s
     * modusom, ale to je oveľa pomalššie.
     *
     * @return Priemerná odchýlka štatistického súboru.
     */
    public double getAbsDevMean() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        double vysl = 0;
        double mean = getMean();
        for (double j : values) {
            vysl += Math.abs(j - mean);
        }
        return vysl / pocetPrvkov;
    }

    /**
     * Priemerná odchýlka štatistického súboru. VELMI POMALE!!!
     * <br><br>
     * Vzorec: (sum(|Xi - X|))/n
     * <br><br> pri výpočte využíva median[X - media].
     *
     * @return Priemerná odchýlka štatistického súboru.
     */
    public double getAbsDevMedian() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        double vysl = 0;
        double median = getMedian();
        for (double j : values) {
            vysl += Math.abs(j - median);
        }
        return vysl / pocetPrvkov;
    }

    /**
     * Retrive the quartile value from an array .
     *
     * @param lowerPercent The percent cut off. For the lower quartile use 25,
     * for the upper-quartile use 75
     * @return
     */
    public double getQuartile(double lowerPercent) {
        if (values.size() < 4) {
            return Double.NaN;
        }

        LinkedList<Double> lst = new LinkedList(values);
        Collections.sort(lst);
        int n = (int) Math.round(lst.size() * lowerPercent / 100);
        return lst.get(n);
    }

    /**
     * Priemerná odchýlka štatistického súboru. VELMI POMALE!!!
     * <br><br>
     * Vzorec: (sum(|Xi - X|))/n; //[X == mode]
     * <br><br> pri výpočte využíva modus.
     *
     * @return Priemerná odchýlka štatistického súboru.
     */
    public double getAbsDevMode() {
        if (values.isEmpty()) {
            return Double.NaN;
        }
        double vysl = 0;
        double mode = getMode();
        for (double j : values) {
            vysl += Math.abs(j - mode);
        }
        return vysl / pocetPrvkov;
    }

    public double[] getConfidenceInterval95() {
        try {
            interval[0] = ((getMean()) - ((1.96 * getStdDevP()) / Math.sqrt(pocetPrvkov)));
            interval[1] = ((getMean()) + ((1.96 * getStdDevP()) / Math.sqrt(pocetPrvkov)));
        } catch (Exception e) {
            interval[0] = Double.NaN;
            interval[1] = Double.NaN;
        }
        return interval;
    }

    /**
     *
     * @param z pre z-values obsahuje trieda Stat statické premenné
     * @return interval spoľahlivosti podľla zadaného parametra z.
     */
    public double[] getConfidenceInterval(double z) {
        try {
            interval[0] = ((getMean()) - ((z * getStdDevP()) / Math.sqrt(pocetPrvkov)));
            interval[1] = ((getMean()) + ((z * getStdDevP()) / Math.sqrt(pocetPrvkov)));
        } catch (Exception e) {
            interval[0] = Double.NaN;
            interval[1] = Double.NaN;
        }
        return interval;
    }

    /**
     * <br>POZOR nepomylit s p-value.
     * p sa využíva pri výpočte sumy p-tych mocnín prvkov štat. súboru. Ten sa
     * využíva pri výpočte generalized mean.
     *
     * @return
     */
    public double getP() {
        return p;
    }

    /**
     * <br>POZOR nepomylit s p-value.
     * nastaví sa p aj pLower. p sa využíva pri výpočte sumy p-tych mocnín
     * prvkov štat. súboru. Ten sa využíva pri výpočte generalized mean, lahmer
     * mean.
     * <br> inicializované na hodnotu 5. Po pridaní prvého čísla by sa už p
     * meniť nemalo.
     *
     * @param p
     */
    public void setP(double p) {
        this.p = p;
    }

    /**
     * Pozor!!! funkcia je pomala, cely statisticky subor sa musi prejst a
     * ulozit zakazdym do countera!!!
     *
     * @return ElementsCounterList
     */
    public ElementsCounter<Double> getElementsCounterList() {
        ///   ElementsCounterList<Double> counter = new ElementsCounterList<>(values);
        return null;//counter;
    }

    /**
     * Retrive the quartile value from an array .
     *
     * @param values THe array of data
     * @param lowerPercent The percent cut off. For the lower quartile use 25,
     * for the upper-quartile use 75
     * @return
     */
    public static double quartile(double[] values, double lowerPercent) {

        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("The data array either is null or does not contain any data.");
        }

        // Rank order the values
        double[] v = new double[values.length];
        System.arraycopy(values, 0, v, 0, values.length);
        Arrays.sort(v);

        int n = (int) Math.round(v.length * lowerPercent / 100);

        return v[n];

    }

    /**
     * Uloží dáta transponovane(do stĺpca)
     *
     * @param path cesta do súboru, kam budú dáta uložené
     */
    public void exportDataT(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (double number : values) {
            sb.append(number).append("\n");
        }
        FileWriter fl = new FileWriter(new File(path));
        fl.write(sb.toString());
        fl.flush();
        fl.close();
    }

    /**
     * Dáta uloží vo formáte CVS.
     *
     * @param path cesta do súboru, kam budú dáta uložené
     * @throws java.io.IOException
     */
    public void exportDataToCVS(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (double number : values) {
            sb.append(number).append(", ");
        }
        try (FileWriter fl = new FileWriter(new File(path))) {
            fl.write(sb.toString());
            fl.flush();
        }
    }

    /**
     * Načíta dáta. Načítavaný súbor musí obsahovať čísla vo formáte CVS
     * <br><br>Príklad: 1, 2.5, 9.8,...
     *
     * @param path = cesta do súboru, kam budú dáta uložené
     */
    public void importDataFromCVS(String path) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        String pom;
        String[] ret;
        while ((pom = br.readLine()) != null) {
            ret = pom.split(",");
            for (String number : ret) {
                number = number.trim();
                if (!number.isEmpty()) {
                    add(Double.parseDouble(number.trim()));
                }
            }
        }
        br.close();
    }

    /**
     * Načíta dáta. Dáta musia byť uloženie vo stĺpci.
     * <br><br>Príklad<br><br>1<br>5<br>8<br>9.7<br>9.2<br>1.5<br>...
     *
     * @param path = cesta do súboru, kam budú dáta uložené
     */
    public void importDataT(String path) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        String pom;
        while ((pom = br.readLine()) != null) {
            add(Double.parseDouble(pom.trim()));
        }
        br.close();
    }

    public static String printIntervalSpolahlivost(double[] interval) {
        return "< " + String.format("%2.3f", interval[0]) + " , " + String.format("%2.3f", interval[1]) + " >";
    }

    public static String printIntervalSpolahlivost(double[] interval, int pocDesatinychMiest) {
        return "< " + String.format("%2." + pocDesatinychMiest + "f", interval[0]) + " , " + String.format("%2." + pocDesatinychMiest + "f", interval[1]) + " >";
    }

    /**
     * Meria smer a stupeň asymetrie rozdelenia premennej.
     * <br><br>
     * <b>Kladná hodnota</b> (pravostranná šikmosť) znamená, že priemer je väčší
     * ako medián, teda väčšina hodnôt je menšia ako priemer. <br>
     * <b>Záporná hodnota</b>(ľavostranná šikmosť) znamená, že medián je väčší
     * ako priemer a teda väčšina hodnôt je väčšia ako priemer.<br>
     * <b>Šikmosť rovná</b> 0 znamená symetrické rozdelenie, teda priemer
     * a medián sa rovnajú.
     *
     * @return Šikmosť (Skewness) - miera tvaru
     */
    public double getSkewness() {
        if (values.size() < 4) {
            return Double.NaN;
        }
        double skew = Double.NaN;
        double m = getMean();
        double length = values.size();
        final double variance = getVarianceS();
        double accum3 = 0.0;
        for (int i = 0; i < length; i++) {
            final double d = values.get(i) - m;
            accum3 += d * d * d;
        }
        accum3 /= variance * Math.sqrt(variance);
        skew = (length / ((length - 1) * (length - 2))) * accum3;
        return skew;
    }

    /**
     * <b>Špicatosť (Kurtosis)<b> - meria hustotu chvostov rozdelenia premennej,
     * teda charakterizuje výskyt extrémne vysokých a extrémne nízkych hodnôt.
     * <br><br>Špicatosť rozdelenia sa porovnáva so špicatosťou normálneho
     * rozdelenia, ktorého špicatosť sa rovná 3.
     * <br><br>Unimodálne rozdelenia, ktorých špicatosť je väčšia, majú
     * hustejšie chvosty (výskyt extrémnych hodnôt je častejší) ako normálne
     * rozdelenie. Takéto rozdelenia majú vyšší vrchol.
     * <br><br>Unimodálne rozdelenia, ktorých špicatosť je menšia ako 3, majú
     * nižšie chvosty, teda výskyt extrémnych hodnôt je menej častý ako
     * u normálneho rozdelenia. Takéto rozdelenia sú plochejšie.
     *
     *
     * @return Špicatosť (Kurtosis)
     */
    public double getKurtosis() {
        if (values.size() < 4) {
            return Double.NaN;
        }
        double kurt = Double.NaN;

        double mean = getMean();
        double stdDev = getStdDevP();

        double accum3 = 0.0;
        for (int i = 0; i < values.size(); i++) {
            accum3 += Math.pow(values.get(i) - mean, 4.0);
        }
        accum3 /= Math.pow(stdDev, 4.0d);

        double n0 = values.size();
        double coefficientOne = (n0 * (n0 + 1)) / ((n0 - 1) * (n0 - 2) * (n0 - 3));
        double termTwo = (3 * Math.pow(n0 - 1, 2.0)) / ((n0 - 2) * (n0 - 3));

        // Calculate kurtosis
        kurt = (coefficientOne * accum3) - termTwo;
        return kurt;
    }

    /**
     * Asi to nie je ten pravý percentil, ale ako ukazovateľ to môže poslúžiť
     * celkom dobre.
     *
     * @param cislo
     * @return vráti počet väčších prvkov ako zadané číslo. Výsledok je
     * vyjadrený v percentách
     */
    public double getPercentile(double cislo) {
        if (pocetPrvkov == 0) {
            return Double.NaN;
        }
        double countBigger = 0;
        for (double number : values) {
            if (cislo > number) {
                countBigger++;
            }
        }
        return ((countBigger) / values.size()) * 100;
    }

    /**
     * population covariance (n-1)
     *
     * @param first
     * @param second
     * @return
     */
    public static double getCovarianceP(Stat first, Stat second) {
        double covar = 0;
        int pocet = Math.min(first.getCount(), second.getCount());
        if (pocet == 0) {
            return Double.NaN;
        }
        for (int i = 0; i < pocet; i++) {
            covar += (first.getValues().get(i) - first.getMean()) * (second.getValues().get(i) - second.getMean()) / (pocet - 1);
        }
        return covar;
    }

    /**
     * sample covariance (n-1)
     *
     * @param first
     * @param second
     * @return
     */
    public static double getCovarianceS(Stat first, Stat second) {
        double covar = 0;
        int pocet = Math.min(first.getCount(), second.getCount());
        if (pocet == 0) {
            return Double.NaN;
        }
        for (int i = 0; i < pocet; i++) {
            covar += (first.getValues().get(i) - first.getMean()) * (second.getValues().get(i) - second.getMean()) / (pocet);
        }
        return covar;
    }

    /**
     * Sample Pearson’s Product-Moment Correlation Coefficient
     *
     * @param first
     * @param second
     * @return
     */
    public static double getCorrCoefS(Stat first, Stat second) {
        return getCovarianceS(first, second) / StrictMath.sqrt(first.getVarianceS() * second.getVarianceS());
    }

    /**
     * Population Pearson’s Product-Moment Correlation Coefficient
     *
     * @param first
     * @param second
     * @return
     */
    public static double getCorrCoefP(Stat first, Stat second) {
        return getCovarianceP(first, second) / Math.sqrt(first.getVarianceS() * second.getVarianceS());
    }

    public static void main(String[] args) {
        Stat first = new Stat();
        first.add(2.5);
        first.add(0.5);
        first.add(2.2);
        first.add(1.9);
        first.add(3.1);
        first.add(2.3);
        first.add(2);
        first.add(1);
        first.add(1.5);
        first.add(1.1);

        Stat s = new Stat();
        s.add(2.4);
        s.add(0.7);
        s.add(2.9);
        s.add(2.2);
        s.add(3.0);
        s.add(2.7);
        s.add(1.6);
        s.add(1.1);
        s.add(1.6);
        s.add(0.9);

        //System.out.println(Stat.getCovarianceP(s, s));
        //System.out.println(Stat.getCovarianceP(s, first));
        Stat stat = new Stat();
        stat.add(18);
        stat.add(19);
        stat.add(17);
        stat.add(19);
        stat.add(19);
        stat.add(21);
        stat.add(23);
        stat.add(20);
        stat.add(18);
        stat.add(19);
        stat.add(22);

        System.out.println(stat.getMean());

    }

}
