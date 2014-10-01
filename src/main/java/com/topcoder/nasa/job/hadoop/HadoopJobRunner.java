package com.topcoder.nasa.job.hadoop;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.streaming.StreamJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.nasa.job.LmmpJob;
import com.topcoder.nasa.job.LmmpJobFiles;

/**
 * Runs the Hadoop Streaming job for the mosaic generation. Note that this class has its roots in a
 * script "start.sh" and pretty much mimics EXACTLY what that script did.
 */
// http://stackoverflow.com/questions/9849776/calling-a-mapreduce-job-from-a-simple-java-program
// http://stackoverflow.com/questions/12654327/hadoop-streaming-1-0-3-unrecognized-d-command
@Component
public class HadoopJobRunner {
    private static final Logger LOG = LoggerFactory.getLogger(HadoopJobRunner.class);

    @Autowired
    private JobClient jobClient;

    @Autowired
    private LmmpJobFiles lmmpJobFiles;

    @Autowired
    private HadoopJobFiles hadoopJobFiles;

    /** Legacy handle.sh script path. */
    private String hadoopHandleScript;

    /** Legacy isis run script path */
    private String isisRunScript;

    /** Moon Map file path */
    private String moonMapFile;

    public String executeFor(LmmpJob job) {
        try {
            return doExecuteFor(job);
        } catch (Exception e) {
            throw new IllegalStateException("Exception while creating/starting Hadoop job", e);
        }
    }

    private String doExecuteFor(LmmpJob job) throws IOException {
        LOG.info("Starting Hadoop job...");

        String finalDirectoryName = job.getUuid();

        String[] args = new String[] {
                "-mapper",
                "\"/bin/cat\"",
                //
                "-reducer",
                "\"/bin/sh " + hadoopHandleScript + " " + lmmpJobFiles.computePicDirectoryFor(job)
                        + " " + finalDirectoryName + " " + isisRunScript  + " " + moonMapFile + "\"",
                //
                "-input", hadoopJobFiles.computeHadoopUrlDirectoryFor(job),
                //
                "-output", hadoopJobFiles.computeHadoopOutputDirectoryFor(job) };

        LOG.info("Submission job with args {}", Arrays.asList(args));

        final JobConf jobConf = new StreamJob().createJob(args);

        jobConf.set("mapred.cache.files",
                hadoopJobFiles.computeHadoopCustomerParitionerJarFileNameFor(job));
        jobConf.set("mapred.job.classpath.files",
                hadoopJobFiles.computeHadoopCustomerParitionerJarFileNameFor(job));
        jobConf.set("mapred.reduce.tasks.speculative.execution", "false");
        jobConf.set("mapred.task.timeout", "0");
        jobConf.set("mapred.map.tasks", "1");
        jobConf.set("mapred.min.split.size", "67108864");
        jobConf.set("mapred.reduce.tasks", "5");
        jobConf.set("mapred.reduce.max.attempts", "1");
        jobConf.set("mapreduce.tasktracker.reduce.tasks.maximum", "1");

        final RunningJob runningJob = jobClient.submitJob(jobConf);
        String jobId = runningJob.getID().toString();

        LOG.info("Job has been submitted and assigned id {}", jobId);

        logJobProgressAsync(jobConf, runningJob);

        return jobId;
    }

    private void logJobProgressAsync(final JobConf jobConf, final RunningJob runningJob) {
        // this is a cheap hack but it's the only "real" way I could find of
        // doing this -- log the job
        new Thread(new Runnable() {
            public void run() {
                try {
                    jobClient.monitorAndPrintJob(jobConf, runningJob);
                } catch (Exception e) {
                    LOG.info("Exception while dumping job status to log?", e);
                }
            }
        }).start();
    }

    public void setIsisRunScript(String isisRunScript) {
        this.isisRunScript = isisRunScript;
    }

    public void setMoonMapFile(String moonMapFile) {
        this.moonMapFile = moonMapFile;
    }

    public void setHadoopHandleScript(String hadoopHandleScript) {
        this.hadoopHandleScript = hadoopHandleScript;
    }

}
