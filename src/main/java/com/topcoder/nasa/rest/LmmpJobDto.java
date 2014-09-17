package com.topcoder.nasa.rest;

import java.util.Date;
import java.util.List;

public class LmmpJobDto {
    private String uuid;
    private String status;
    private String hadoopJobId;
    private String failReason;
    private LmmpJobCriteria jobCriteria;
    private Integer numberOfImages;
    private List<String> imageUrls;
    private Date requestStart;
    private Date requestEnd;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHadoopJobId() {
        return hadoopJobId;
    }

    public void setHadoopJobId(String hadoopJobId) {
        this.hadoopJobId = hadoopJobId;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public LmmpJobCriteria getJobCriteria() {
        return jobCriteria;
    }

    public void setJobCriteria(LmmpJobCriteria jobCriteria) {
        this.jobCriteria = jobCriteria;
    }

    public Integer getNumberOfImages() {
        return numberOfImages;
    }

    public void setNumberOfImages(Integer numberOfImages) {
        this.numberOfImages = numberOfImages;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Date getRequestStart() {
        return requestStart;
    }

    public void setRequestStart(Date requestStart) {
        this.requestStart = requestStart;
    }

    public Date getRequestEnd() {
        return requestEnd;
    }

    public void setRequestEnd(Date requestEnd) {
        this.requestEnd = requestEnd;
    }
}
