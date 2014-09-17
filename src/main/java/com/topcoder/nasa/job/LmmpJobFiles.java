package com.topcoder.nasa.job;

import java.io.File;
import java.io.IOException;

import com.topcoder.nasa.job.LmmpJob;

/**
 * Manages the creation of File objects associated with {@link LmmpJob}s.
 */
public class LmmpJobFiles {
    /** Pics for each job will be stored in a directory called "pic". */
    private static final String PIC_DIRECTORY_NAME = "pic";

    /** The HDFS part file name. */
    private static final String PART_FILE_NAME = "part-000000";

    /** Each {@link LmmpJob} will stage its work underneath localWorkDirectory/jobUuid. */
    private File localWorkDirectory;

    /**
     * Where should log files be stored for this job?
     * 
     * @param job
     *            the job in question
     * @return directory File representing the log location
     */
    public File computeLogDirectoryFor(LmmpJob job) {
        return computeJobLocalWorkDirectory(job);
    }

    /**
     * Where should the individual images (pics) be stored for this job?
     * 
     * @param job
     *            the job in question
     * @return directory File representing the pic directory.
     */
    public File computePicDirectoryFor(LmmpJob job) {
        File picDirectory = new File(computeJobLocalWorkDirectory(job), PIC_DIRECTORY_NAME);
        
        picDirectory.mkdir();
        
        return picDirectory;
    }

    /**
     * Compute the local File that will be used to store the part contents for the Hadoop job
     * 
     * @param job
     *            the job in question
     * @return the part file
     */
    public File computePartFileFor(LmmpJob job) {
        return new File(computeJobLocalWorkDirectory(job), PART_FILE_NAME);
    }

    public File computeJobLocalWorkDirectory(LmmpJob job) {
        File directory = new File(localWorkDirectory, job.getUuid());

        directory.mkdirs();

        return directory;
    }

    public void setLocalWorkDirectory(File localWorkDirectory) {
        this.localWorkDirectory = localWorkDirectory;
    }
}
