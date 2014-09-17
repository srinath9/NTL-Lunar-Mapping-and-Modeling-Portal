package com.topcoder.nasa.rest;

import gov.nasa.pds.entities.SearchCriteria;

/**
 * Lmmp specialization of the PDS SearchCriteria which adds the output format as a field.
 *
 */
public class LmmpJobCriteria extends SearchCriteria {
    private String outputFormat;
    private int outputSizePercentage;

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
    
    public int getOutputSizePercentage() {
        return outputSizePercentage;
    }
    
    public void setOutputSizePercentage(int outputSizePercentage) {
        this.outputSizePercentage = outputSizePercentage;
    }
}
