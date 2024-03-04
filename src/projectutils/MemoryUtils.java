/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

import projectutils.structures.BinaryVector;
import projectutils.structures.FloatVector;
import projectutils.structures.Vector;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class MemoryUtils {

    public static long sizeOf(boolean[] arr) {
        return 32 + arr.length;
    }
    
    public static long sizeOf(Boolean[] arr) {
        return 32 + arr.length;
    }

    public static long sizeOf(boolean arr) {
        return 1;
    }

    public static long sizeOf(double[] arr) {
        return 32 + 64 * arr.length;
    }

    public static long sizeOf(double x) {
        return 64;
    }

    public static long sizeOf(int[] arr) {
        return 32 + 32 * arr.length;
    }

    public static long sizeOf(int arr) {
        return 32;
    }

    public static long sizeOf(byte[] arr) {
        return 32 + 1 * arr.length;
    }

    public static long sizeOf(byte x) {
        return 1;
    }

    public static long sizeOf(short[] arr) {
        return 32 + 16 * arr.length;
    }

    public static long sizeOf(short x) {
        return 16;
    }

    public static long sizeOf(float[] arr) {
        return 32 + 32 * arr.length;
    }

    public static long sizeOf(float x) {
        return 32;
    }

    public static long sizeOf(long[] arr) {
        return 32 + 64 * arr.length;
    }

    public static long sizeOf(long x) {
        return 64;
    }

    public static double bitsToBytes(long bits) {
        return bits / 8;
    }

    public static void main(String[] args) {
        Vector v = FloatVector.vector(1, 1, 2, 5, 4, 5);
        Vector a = new BinaryVector(true,false, true, true, true, false);
        v.mul(a).print();
    }

}
