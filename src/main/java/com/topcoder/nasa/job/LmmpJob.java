package com.topcoder.nasa.job;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.topcoder.nasa.rest.LmmpJobCriteria;

/**
 * Defines a "job" that is created when a generation request is submitted successfully.
 *
 */
public class LmmpJob {
    private static final Logger LOG = LoggerFactory.getLogger(LmmpJob.class);

    public enum Status {
        RUNNING_PDS_API("running"), RUNNING_HADOOP("running"), RUNNING_EXECUTABLES("running"), FAILED, COMPLETED, KILLED;

        private String displayName;

        Status() {
            this.displayName = name().toLowerCase();
        }

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return displayName;
        }
    }

    /** The final file name to check for job completion */
    private static final String FINAL_PATH = "/tmp/{uuid}_final";
    private static final String MOSAIC_FILE = FINAL_PATH + "/mosaic.";
    private static final String VRT_FILE = FINAL_PATH + "/mosaic.vrt";

    /** The default output format to use - geotiff. */
    private static final String DEFAULT_OUTPUT_FORMAT = "gtiff";

    /** The UUID of this Job */
    private String uuid;

    /** This Job's current status */
    private Status status;

    /** If this job failed, here is where we store why */
    private String failInfo;

    /** The Hadoop Job ID for this job */
    private String hadoopJobId;

    /** The criteria associated with this job. */
    private LmmpJobCriteria jobCriteria;

    /** The number of images that matches this job's criteria */
    private Integer numberOfImages;

    /** When this job was created */
    private Date created;

    /** When this job transitioned into a terminal state */
    private Date finished;

    // =========================================================================

    /**
     * Constructor for creating a <b>new</b> Job that automatically assigns a (random) UUID and sets
     * the job in RUNNING status.
     */
    public LmmpJob(LmmpJobCriteria jobCriteria) {
        this.uuid = UUID.randomUUID().toString();
        this.status = Status.RUNNING_PDS_API;
        this.created = new Date();
        this.jobCriteria = jobCriteria;

        LOG.info("Created new Job with uuid {}", uuid);
    }

    /**
     * Constructor for creating an <b>existing</b> Job and populating all the fields.
     */
    public LmmpJob(String uuid, String statusStr, String hadoopJobId, String failInfo,
            LmmpJobCriteria jobCriteria, Integer numImages, Date created, Date finished) {
        this.status = Status.valueOf(statusStr);
        this.uuid = uuid;
        this.hadoopJobId = hadoopJobId;
        this.failInfo = failInfo;
        this.jobCriteria = jobCriteria;
        this.numberOfImages = numImages;
        this.created = created;
        this.finished = finished;

        LOG.debug("Loaded Job with uuid {}, status {} and hadoopJobId {}", uuid, status,
                hadoopJobId);
    }

    // =========================================================================

    public String getUuid() {
        return uuid;
    }

    public Status getStatus() {
        return status;
    }

    public void markAsRunningExecutables() {
        status = Status.RUNNING_EXECUTABLES;
    }

    public void failed(String failInfo) {
        this.failInfo = failInfo;
        status = Status.FAILED;

        finished = new Date();
    }

    public String getFailInfo() {
        return failInfo;
    }

    public void completed() {
        status = Status.COMPLETED;

        finished = new Date();
    }

    public void killed() {
        status = Status.KILLED;
    }

    public File getFinalPath() {
        String fileName = FINAL_PATH.replace("{uuid}", uuid);

        return new File(fileName);
    }

    public File getJobVrtFile() {
        String fileName = VRT_FILE.replace("{uuid}", uuid);

        return new File(fileName);
    }

    public File getJobCompletedFile() {
        String fileName = MOSAIC_FILE.replace("{uuid}", uuid);

        fileName = fileName + jobCriteria.getOutputFormat();

        return new File(fileName);
    }

    public String getHadoopJobId() {
        return hadoopJobId;
    }

    public void setHadoopJobId(String hadoopJobId) {
        this.hadoopJobId = hadoopJobId;
        status = Status.RUNNING_HADOOP;
    }

    public String getOutputFormat() {
        String outputFormat = jobCriteria.getOutputFormat();

        if (outputFormat == null) {
            outputFormat = DEFAULT_OUTPUT_FORMAT;
        }

        return outputFormat;
    }

    /**
     * Map the {@link #outputFormat} to its file extension.
     */
    public String getFileType() {
        String outputFormat = getOutputFormat();

        if (outputFormat.toLowerCase().equals("gtiff")) {
            return "tiff";
        }

        return outputFormat;
    }

    public int getOutputSizePercentage() {
        return jobCriteria.getOutputSizePercentage();
    }

    public LmmpJobCriteria getJobCriteria() {
        return jobCriteria;
    }

    public Integer getNumberOfImages() {
        return numberOfImages;
    }

    public void setNumberOfImages(Integer numberOfImages) {
        this.numberOfImages = numberOfImages;
    }

    public Date getCreated() {
        return created;
    }

    public Date getFinished() {
        return finished;
    }

    // =========================================================================

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LmmpJob)) {
            return false;
        }

        LmmpJob that = (LmmpJob) obj;

        return this.uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    // =========================================================================

    @Override
    public String toString() {
        return "LmmpJob [uuid=" + uuid + ", status=" + status + "]";
    }

}
