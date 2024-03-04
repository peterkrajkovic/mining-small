/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minig.data.core.io.load;

/**
 *
 * @author Jan Rabcan {jrabcanj@gmail.com}
 */
public class LineException extends Error {

    public LineException(int lineno, String message) {
        super("Parse error line " + lineno + ": " + message);
    }

    public LineException(int fileLine, int dataLine, String message) {
        super(getMessage(fileLine, dataLine, message));
    }

    private static String getMessage(int fileLine, int dataLine, String message) {
        final String name = "DATA ERROR at file line: " + fileLine
                + "\n   " + dataLine + "-th instance cannot be loaded "
                + "\n   Error: " + message;
        return name;
    }

}
