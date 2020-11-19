package com.sandklef.compliance.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LicenseMatrix {

    public static int LICENSE_MATRIX_TRUE = 0;
    public static int LICENSE_MATRIX_FALSE = 1;
    public static int LICENSE_MATRIX_UNKNOWN = 2;

    private License[] licenseList;
    private int[][] licenseMatrix;

    public LicenseMatrix(int size) {
        licenseList = new License[size];
        licenseMatrix = new int[size][size];
    }

    public int size() {
        return licenseList.length;
    }

    public void addLicense(License l, int index) throws IllegalStateException {
        // if the key (l) has already been added, with another index than the current
        // bail out with an exception
        int licenseIndex = indexOfLicense(l);
        if (licenseIndex<0) {
            licenseList[index] = l;;
        } else {
            if (licenseIndex!=index) {
                throw new IllegalStateException("Index for " + l.spdx() + " is both " + licenseIndex + " and requested to be " + index);
            }
        }
    }

    public int valueAt(int i, int j) {
        return licenseMatrix[i][j];
    }

    public void printMatrix() {
        System.out.println("Matrix verification");
        System.out.println("Sizes: " + licenseList.length + " " + licenseMatrix[0].length);

        System.out.println("License headers");
        System.out.print("matrix: ");
        for (int i=0; i<licenseList.length; i++) {
            System.out.print("| " + licenseList[i]);
        }
        System.out.print("list:   ");
        for (int i=0; i<licenseList.length; i++) {
            System.out.print("| " + licenseMatrix[0][i]);
        }
        System.out.println("");
        System.out.print("matrix: ");
        for (int i=0; i<licenseList.length; i++) {
            License l = licenseList[i];
            System.out.println(" * " + l + "  ==> index: " + i);
        }
    }


    public int indexOfLicense(License license) {
        //System.out.println("find index of: " + license);
        for (int i=0; i<licenseList.length; i++) {
            if (licenseList[i]!=null) {

            }

        //    System.out.println(" i: " + i + " ==> " + licenseList[i]);
            if (licenseList[i]!=null && licenseList[i].spdx().equals(license.spdx())) {
                return i;
            }
         }
        return -1;
    }

    public void addLicenseCompatibility(License license, int row, int index, int value) {
        licenseMatrix[row][index] = value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<licenseList.length; i++)  {
            sb.append("\"");
            sb.append(licenseList[i]);
            sb.append("\" ");
        }
        sb.append("\n");
        for (int i=0; i<licenseMatrix.length; i++) {
            sb.append("\"" + licenseList[i].spdx().charAt(0) + "\"  ");

            for (int j=0; j<licenseMatrix.length; j++) {
                sb.append(" " + licenseMatrix[i][j]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
