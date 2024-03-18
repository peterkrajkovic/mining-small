package demo;

import minig.classification.mdd.MDD;

import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class DPLDexamples {


    public static MDD IDPLDTYPEIII(MDD mdd, int index, int from, int to, int j) {

        //transformation functions
        Function<Integer, Integer> lowerThanOne = x -> (x < j) ? 1 : 0;
        Function<Integer, Integer> atLeastOne = x -> (x >= j) ? 1 : 0;

        //apply function
        BinaryOperator<Integer> binaryAND = (x, y) -> x == 1 && y == 1 ? 1 : 0;

        //dpld computation
        DPLD dpld  = new DPLD();
        MDD result = dpld.UniversalIDPLD(mdd, index, from, to, lowerThanOne,atLeastOne, binaryAND);

        return result;
    }

    public static MDD IDPLDTYPEI(MDD mdd, int index, int from, int to, int j) {

        //transformation functions
        Function<Integer, Integer> equalsJ = x -> (x == j) ? 1 : 0;
        Function<Integer, Integer> lowerThanJ = x -> (x < j) ? 1 : 0;

        //apply function
        BinaryOperator<Integer> binaryAND = (x, y) -> x == 1 && y == 1 ? 1 : 0;

        //dpld computation
        DPLD dpld  = new DPLD();
        MDD result = dpld.UniversalIDPLD(mdd, index, from, to, equalsJ, lowerThanJ, binaryAND);

        return result;
    }

    public static MDD DPLD(MDD mdd, int index, int from, int to) {

        //apply function
        BinaryOperator<Integer> equals = (x, y) -> Objects.equals(x, y) ? 1 : 0;

        //dpld computation
        DPLD dpld  = new DPLD();
        MDD result = dpld.UniversalDPLD(mdd, index, from, to, equals);

        return result;
    }


}
