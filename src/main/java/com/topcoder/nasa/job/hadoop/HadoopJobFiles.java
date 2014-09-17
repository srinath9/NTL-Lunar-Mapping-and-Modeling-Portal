package com.topcoder.nasa.job.hadoop;

import java.io.File;

import com.topcoder.nasa.job.LmmpJob;

/**
 * Creates the names of files and directories in Hadoop for a given {@link LmmpJob}.
 */
public class HadoopJobFiles {
    /** Name of the job-specific distribution cache - where job-specific jars are stored. */
    private static final String DISTCACHE_DIRECTORY_NAME = "distcache";

    /** The name of the directory a job's reduced output will be stored in. */
    private static final String OUTPUT_DIRECTORY_NAME = "output";

    /** The HDFS url directory name. */
    private static final String URL_DIRECTORY_NAME = "url";

    /**
     * This is required by the Hadoop job - not sure exactly what it does, but it contains some
     * logic for working out what files go into what reducer.
     */
    private File customPartitionerJar;

    /**
     * @return Where is the CustomerPartitioner.jar stored locally? We need to upload this for
     *         Hadoop. We probably only need one per job, but it's inconsequential as it's so small.
     */
    public File getCustomPartitionerJar() {
        return customPartitionerJar;
    }

    /**
     * Compute the distribution cache directory in HDFS for each Hadoop job.
     * 
     * @param job
     *            the job in question
     * @return the cache directory
     */
    public String computeHadoopDistcacheDirectoryFor(LmmpJob job) {
        return computeHdfsJobPath(job) + DISTCACHE_DIRECTORY_NAME;
    }

    /**
     * Computes the absolute path of the CustomParitioner.jar file in HDFS, for ths job
     * 
     * @param job
     *            the job in question
     * @return the absolute path os CustomPartitioner.jar
     */
    public String computeHadoopCustomerParitionerJarFileNameFor(LmmpJob job) {
        return computeHadoopDistcacheDirectoryFor(job) + "/" + customPartitionerJar.getName();
    }

    /**
     * Compute the output directory in HDFS for each Hadoop job.
     * 
     * @param job
     *            the job in question
     * @return the output directory
     */
    public String computeHadoopOutputDirectoryFor(LmmpJob job) {
        return computeHdfsJobPath(job) + OUTPUT_DIRECTORY_NAME;
    }

    /**
     * Compute the URL directory in HDFS for each Hadoop job.
     * 
     * @param job
     *            the job in question
     * @return the output directory
     */
    public String computeHadoopUrlDirectoryFor(LmmpJob job) {
        return computeHdfsJobPath(job) + URL_DIRECTORY_NAME;
    }

    /**
     * Computes the HDFS job path for this {@link LmmpJob}.
     * 
     * @param job
     *            the job in question.
     * @return the job path.
     */
    public String computeHdfsJobPath(LmmpJob job) {
        return "/" + job.getUuid() + "/";
    }

    public void setCustomPartitionerJar(File customPartitionerJar) {
        this.customPartitionerJar = customPartitionerJar;
    }

}
