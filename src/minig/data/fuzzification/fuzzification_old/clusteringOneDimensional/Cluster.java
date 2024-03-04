/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.fuzzification.fuzzification_old.clusteringOneDimensional;

import java.util.List;

/**
 *
 * @author jrabc
 */
public interface Cluster {

    public double getCenter();

    default double getEuclideDistance(double number) {
        double abs = Math.abs(number - getCenter());
        return Math.pow(abs, 2);
    }

}
