/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification;

import java.util.Collection;
import minig.classification.fdt.criterrions.FuzzyCriterion;
import minig.data.core.attribute.AttrValue;
import minig.data.core.attribute.Attribute;
import minig.data.core.attribute.FuzzyAttr;
import minig.data.core.attribute.LinguisticAttr;
import minig.data.core.attribute.NumericAttr;
import minig.data.core.dataset.DataSet;
import minig.data.fuzzification.functionsset.FuzzyMapper;
import minig.data.fuzzification.functionsset.Triangular;
import minig.data.fuzzification.fuzzification_old.DatasetFuzzification.FC_Means_cH;
import minig.data.fuzzification.fuzzification_old.DatasetFuzzification.KMeans_withoutOA_wH;
import minig.data.fuzzification.intervals.BetweenClassIntervals;
import minig.data.fuzzification.toFuzzzy.DatasetToFuzzy;
import minig.data.fuzzification.toFuzzzy.NumericToFuzzyKmeans;
import minig.data.fuzzification.toFuzzzy.NumericToFuzzyMemberships;
import minig.distance.Distance;
import projectutils.stat.MaxElement;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public interface Fuzzification {

    public FuzzyAttr fuzzify(NumericAttr attr, FuzzyMapper mapper);

    public default FuzzyAttr fuzzify(NumericAttr attr) {
        return fuzzify(attr, new Triangular());
    }

    public default DataSet fuzzify(DataSet dt, FuzzyMapper membershipMapper) {
        DataSet dataset = new DataSet();
        dataset.setName(dt.getName());
        for (int i = 0; i < dt.getAtributteCount(); i++) {
            Attribute attribute = dt.getAttribute(i);
            if (Attribute.isLingvistic(attribute)) {
                LingvisticToFuzzy ltf = new LingvisticToFuzzy((LinguisticAttr) attribute);
                FuzzyAttr attr = ltf.toFuzzy();
                dataset.addAttribute(attr);
            } else if (Attribute.isNumeric(attribute)) {
                FuzzyAttr attr = fuzzify(attribute.numeric(), membershipMapper);
                dataset.addAttribute(attr);
            } else {
                dataset.addAttribute(attribute);
            }
        }
        if (dt.isOutputAttributeSet()) {
            dataset.setOutputAttrIndex(dt.getOutputAttrIndex());
        }
        if (dataset.isEmpty()) {
            dataset.initDatasetInstances();
        }
        return dataset;
    }

    public static FuzzyAttr toFuzzy(LinguisticAttr attr) {
        return attr.toFuzzy();
    }

    public default DataSet fuzzify(DataSet dt) {
        return fuzzify(dt, new Triangular());
    }

    //----------------------------------------------STATIC
    public static FuzzyAttr getFuzzy(NumericAttr attr, int termsCount) {
        return KMeans_withoutOA_wH.getAttrS(attr, termsCount);
    }

    public static DataSet getFuzzyDataset(int termsCount, NumericAttr... attrs) {
        DataSet dt = new DataSet();
        for (NumericAttr attr : attrs) {
            dt.addAttribute(KMeans_withoutOA_wH.getAttrS(attr, termsCount));
        }
        return dt;
    }

    public static DataSet getFuzzyDataset(int termsCount, Collection<NumericAttr> attrs) {
        DataSet dt = new DataSet();
        for (NumericAttr attr : attrs) {
            dt.addAttribute(KMeans_withoutOA_wH.getAttrS(attr, termsCount));
        }
        return dt;
    }

    /**
     * Fuzzification is performed by fuzzy clustering (Fuzzy C Means) based
     * method.Algorithm makes fuzzifycation of each input attribute
     * individually. Memberships of numeric values to clusters are used as fuzzy
     * values. Number of terms of fuzzy attributes are determined by conditional
     * entropy between fuzzified attribute and output attribute.
     *
     * @param dt dataset which is fuzzified
     * @param fuzziness default value is 2 (it is <I>m</I> parameter of FCM
     * algorithm)
     * @return fuzzified dataset (all attributes of the dataset will be fuzzy)
     */
    public static DataSet byFCmeansCH(DataSet dt, double fuzziness) {
        final FC_Means_cH km = new FC_Means_cH(dt);
        km.setFuzziness(fuzziness);
        return km.getFuzzyDataset();
    }

    /**
     * Fuzzification is performed by fuzzy clustering (Fuzzy C Means) based
     * method.Algorithm makes fuzzifycation of each input attribute
     * individually.Memberships of numeric values to clusters are used as fuzzy
     * values. Number of terms of fuzzy attributes are determined by conditional
     * entropy between fuzzified attribute and output attribute.
     *
     * @param dt dataset which is fuzzified
     * @param fuzziness default value is 2 (it is <I>m</I> parameter of FCM
     * algorithm)
     * @param criterion criterion for determining of number of terms
     * @return fuzzified dataset (all attributes of the dataset will be fuzzy)
     */
    public static DataSet byFCmeansCH(DataSet dt, double fuzziness, FC_Means_cH.Criterion criterion) {
        final FC_Means_cH km = new FC_Means_cH(dt);
        km.setCriterion(FC_Means_cH.Criterion.GainRatio);
        km.setFuzziness(fuzziness);
        return km.getFuzzyDataset();
    }

    /**
     * Attribute is obtained by FCM algorithm.The number of terms of each input
     * numeric attribute is determined by conditional entropy between fuzzified
     * attribute and the output attribute.
     *
     * @param inputDataset
     * @param minTermsCount
     * @param maxTermsCount
     * @param OAterms
     * @return
     */
    public static DataSet byFCmeansCH(DataSet inputDataset, int minTermsCount, int maxTermsCount, int OAterms) {
        FC_Means_cH dtf = new FC_Means_cH(inputDataset);
        dtf.setCriterion(FC_Means_cH.Criterion.InformationGain);
        dtf.setMinTermCount(minTermsCount);
        dtf.setMaxTermCount(maxTermsCount);
        dtf.setOutputAttributeTerms(OAterms);
        return dtf.getFuzzyDataset();
    }

    public static DataSet byFCmeansCH(DataSet inputDataset, int minTermsCount, int maxTermsCount, double fuzziness) {
        FC_Means_cH dtf = new FC_Means_cH(inputDataset);
        dtf.setCriterion(FC_Means_cH.Criterion.InformationGain);
        dtf.setMinTermCount(minTermsCount);
        dtf.setMaxTermCount(maxTermsCount);
        dtf.setFuzziness(fuzziness);
        return dtf.getFuzzyDataset();
    }

   

  
   
    

    public static DataSet byMembershipToClasses(DataSet inputDataset) {
        return new DatasetToFuzzy(new NumericToFuzzyMemberships(inputDataset.getOutbputAttribute()), inputDataset).getDataset();
    }

//    public static DataSet fuzzyByKMeansFuzzyEntropy(DataSet dt) {
//        // K_Means_wH df = new K_Means_wH(dt);
//        K_Means_wH df = new K_Means_wH(dt);
//        return df.getFuzzyDataset();
//    }
    /**
     * Fuzzification is performed by centroids based method. Centroids are
     * obtained by KMneas algorithm for each input attribute individually. Then,
     * this centroids are used as centers of specified membership function.
     * <br><b>HANDLE MISSING VALUES</b><br>
     *
     * @param dt dataset which is fuzzified
     * @param fm define used membership function
     * @return fuzzified dataset (all attributes of the dataset will be fuzzy)
     */
    public static DataSet byKMeansWH(DataSet dt, FuzzyMapper fm) {
        final NumericToFuzzyKmeans km = new NumericToFuzzyKmeans(dt.getOutbputAttribute());
        km.setFuzzyMapper(fm);
        km.setMaxRuns(20000);
        DatasetToFuzzy dtf = new DatasetToFuzzy(km, dt);
        return dtf.getDataset();
    }

    public static DataSet byKMeansWH(DataSet dt, FuzzyMapper fm, int minTerms, int maxTerms) {
        final NumericToFuzzyKmeans km = new NumericToFuzzyKmeans(dt.getOutbputAttribute());
        km.setFuzzyMapper(fm);
        km.setMinTermCount(minTerms);
        km.setMaxTermCount(maxTerms);
        km.setMaxRuns(20000);
        DatasetToFuzzy dtf = new DatasetToFuzzy(km, dt);
        return dtf.getDataset();
    }





}
