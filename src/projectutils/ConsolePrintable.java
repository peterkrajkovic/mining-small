/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectutils;

/**
 *
 * @author jrabc
 */
public interface ConsolePrintable {

    default void print() {
        if (System.console() == null) {
            System.out.println(this.toString());
        } else {
            System.console().writer().println(this.toString());
        }
    }

    public static void println(String string) {
        if (System.console() == null) {
            System.out.println(string);
        } else {
            System.console().writer().println(string);
        }
    }

    public static void append(String string) {
        if (System.console() == null) {
            System.out.append(string);
        } else {
            System.console().writer().append(string);
        }
    }

    public static void print(String string) {
        if (System.console() == null) {
            System.out.print(string);
        } else {
            System.console().writer().print(string);
        }
    }

    public static void newLine() {
        if (System.console() == null) {
            System.out.println();
        } else {
            System.console().writer().print(System.lineSeparator());
        }
    }

}
