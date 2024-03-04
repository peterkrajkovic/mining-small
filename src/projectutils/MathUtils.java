/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import java.util.Random;

/**
 *
 * @author rabcan
 */
public class MathUtils {

    public static double uniform(double min, double max, Random rng) {
        return rng.nextDouble() * (max - min) + min;
    }

    public static long factorial(int number) {
        long result = 1;

        for (int factor = 2; factor <= number; factor++) {
            result *= factor;
        }

        return result;
    }

    public static int binomial(int n, double p, Random rng) {
        if (p < 0 || p > 1) {
            return 0;
        }

        int c = 0;
        double r;

        for (int i = 0; i < n; i++) {
            r = rng.nextDouble();
            if (r < p) {
                c++;
            }
        }

        return c;
    }

    public static double sigmoid(double x) {
        return 1. / (1. + Math.pow(Math.E, -x));
    }

    public static double dsigmoid(double x) {
        return x * (1. - x);
    }

    public static double tanh(double x) {
        return Math.tanh(x);
    }

    public static double dtanh(double x) {
        return 1. - x * x;
    }

    public static double ReLU(double x) {
        if (x > 0) {
            return x;
        } else {
            return 0.;
        }
    }

    public static double dReLU(double x) {
        if (x > 0) {
            return 1.;
        } else {
            return 0.;
        }
    }

    public static double min(double a, double b, double c) {
        return Math.min(Math.min(a, b), c);
    }
}
