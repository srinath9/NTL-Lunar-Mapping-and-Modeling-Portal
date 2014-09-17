package com.topcoder.nasa.job.hadoop;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.topcoder.nasa.job.LmmpJob;
import com.topcoder.nasa.job.LmmpJobFiles;

/**
 * Prepares the HDFS environment for this run (in exactly the same way the start.sh script used to.)
 * <p/>
 * See {@link #doPrepareEnvironment()}.
 */
public class HadoopEnvironmentPreparer {
    private static final Logger LOG = LoggerFactory.getLogger(HadoopEnvironmentPreparer.class);

    private FileSystem fileSystem;

    @Autowired
    private LmmpJobFiles lmmpJobFiles;

    @Autowired
    private HadoopJobFiles hadoopJobFiles;

    public boolean prepareFor(LmmpJob job) {
        LOG.info("Preparing Hadoop HDFS environment for new job...");
        try {
            doPrepareEnvironment(job);
            LOG.info("Done preparing Hadoop HDFS environment!");
            return true;
        } catch (ConnectException ce) {
            LOG.error("Unable to connect to HDFS - cannot clean up environment");
            return false;
        } catch (Exception e) {
            throw new IllegalStateException("Exception while cleaning up Hadoop environment", e);
        }
    }

    private void doPrepareEnvironment(LmmpJob job) throws IOException {
        String hdfsDistCacheDirectory = hadoopJobFiles.computeHadoopDistcacheDirectoryFor(job);
        LOG.info("Deleting {} recursively from HDFS", hdfsDistCacheDirectory);
        fileSystem.delete(new Path(hdfsDistCacheDirectory), true);

        LOG.info("Creating {} in HDFS", hdfsDistCacheDirectory);
        fileSystem.mkdirs(new Path(hdfsDistCacheDirectory));

        LOG.info("Putting CustomerPartitioner.jar into HDFS");
        fileSystem.copyFromLocalFile(new Path(hadoopJobFiles.getCustomPartitionerJar().getPath()),
                new Path(hdfsDistCacheDirectory));

        String hdfsUrlDirectory = hadoopJobFiles.computeHadoopUrlDirectoryFor(job);
        LOG.info("Creating {} in HDFS", hdfsUrlDirectory);
        fileSystem.mkdirs(new Path(hdfsUrlDirectory));

        File partFile = lmmpJobFiles.computePartFileFor(job);
        LOG.info("Putting {} into HDFS", partFile);
        fileSystem.copyFromLocalFile(new Path(partFile.getPath()), new Path(hdfsUrlDirectory));
    }

    public void cleanDownFor(LmmpJob job) {
        String hdfsJobPath = hadoopJobFiles.computeHdfsJobPath(job);

        LOG.info("Clearing down HDFS Job Path {}", hdfsJobPath);

        try {
            fileSystem.delete(new Path(hdfsJobPath), true);
        } catch (Exception e) {
            throw new IllegalStateException("Exception while clearing down Hadoop job directory", e);
        }
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
}
