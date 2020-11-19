package com.sandklef.compliance.csv;

import com.sandklef.compliance.domain.*;

import java.io.*;

public class LicenseMatrixReader {

    private static LicenseMatrix fillMatrixHeaders(String lineWithHeaders) {
      //  System.out.println(" line: " + lineWithHeaders);
        String[] headers = lineWithHeaders.split(",");
        LicenseMatrix matrix = new LicenseMatrix(headers.length - 2);
        for (int i = 0; i < headers.length; i++) {
            String s = headers[i];
            if (i == 0) {
                System.out.println(" ignore " + s);
            } else if (i == headers.length - 1) {
                System.out.println(" ignore " + s);
            } else {
        //        System.out.println(" *      " + s);
                matrix.addLicense(new License(s), i-1);
            }
        }
        return matrix;
    }

    private static int valueToInt(String value) throws IllegalLicenseExpression {
        switch (value) {
            case "Yes":
                return LicenseMatrix.LICENSE_MATRIX_TRUE;
            case "No":
                return LicenseMatrix.LICENSE_MATRIX_FALSE;
            case "?":
                return LicenseMatrix.LICENSE_MATRIX_UNKNOWN;
            case "":
                return LicenseMatrix.LICENSE_MATRIX_TRUE;
            default:
                return LicenseMatrix.LICENSE_MATRIX_UNKNOWN;
        }
    }

    private static void fillMatrixValues(LicenseMatrix matrix, int row,  String lineWithValues) throws IllegalLicenseExpression {
       // System.out.println("fillMatrixValues");
        String[] values = lineWithValues.split(",");
        for (int i = 0; i < values.length; i++) {
            String s = values[i];
         //   System.out.println("  * " + i + "  " + s);
            if (i == 0) {
       //         System.out.println("   ignore " + s);
            } else if (i == values.length - 1) {
     //           System.out.println("   ignore " + s);
            } else {
   //             System.out.println("   **      " + s + " to " + row + " " + (i-1));
                int value  = valueToInt(s);
                matrix.addLicenseCompatibility(new License(s), row, i-1, value);
            }
        }
    }

    public static LicenseMatrix readMatrix(String fileName) throws IOException, LicenseMatrixException {
        LicenseMatrix matrix = null;
        BufferedReader reader = null;
        System.out.println("matrix file: " + fileName);
        int lineCount = 0;
        if (fileName.equals("-")) {
            reader = new BufferedReader(new InputStreamReader(System.in));
        } else {
            reader = new BufferedReader(new FileReader(fileName));
        }
        StringBuilder sb = new StringBuilder();
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (lineCount == 0) {
       //             System.out.println(" line: " + line);
                    matrix = fillMatrixHeaders(line);
                } else {
                 //   System.out.println(" fill " + lineCount + " / " + matrix.size() + " " + line);
                    if (lineCount == matrix.size()-1) {
                        break;
                    }
                    fillMatrixValues(matrix, lineCount-1, line);
                }
                lineCount++;
            }
          //  System.out.println("");

        } catch (IOException | IllegalLicenseExpression e) {
            System.out.println(e);
            System.exit(1);
        }

//        System.out.println("matrix read");

  //      System.out.println(matrix.toString());

        return matrix;
    }


}
