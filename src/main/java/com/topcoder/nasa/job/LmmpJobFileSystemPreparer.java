package com.topcoder.nasa.job;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Prepares the local file system for the Hadoop job.
 * <p/>
 * This is a drop-in replacement for a legacy bash script and, as such, does not consider any of the
 * intricacies of multiple jobs being submitted at the same time or while one job is already
 * running.
 *
 */
@Component
public class LmmpJobFileSystemPreparer {
    private static final Logger LOG = LoggerFactory.getLogger(LmmpJobFileSystemPreparer.class);

    @Autowired
    private LmmpJobFiles lmmpJobFiles;

    // =============================================================================================

    /**
     * Creates the "part" file that will be sent to Hadoop. This file simply contains the
     * <b>names</b> of the image files with their parent path (i.e. directory) stripped.
     * 
     * @param imageFiles
     *            the image files to pull the names from.
     */
    public void createPartFileFor(LmmpJob job, Collection<File> imageFiles) {
        File partFile = lmmpJobFiles.computePartFileFor(job);

        try {
            FileWriter writer = new FileWriter(partFile);

            for (File imageFile : imageFiles) {
                String fileName = imageFile.getName().toUpperCase();

                LOG.info("Writing {} to part file {}", fileName, partFile);

                writer.write(fileName);
                writer.write('\n');
            }

            writer.close();
        } catch (IOException e) {
            throw new IllegalStateException("Exception creating part file", e);
        }
    }

    /**
     * Cleans the job's work directory.
     */
    public void cleanWorkDirectory(LmmpJob job) {
        File workDirectroy = lmmpJobFiles.computeJobLocalWorkDirectory(job);

        try {
            LOG.info("Clearing down workDirectory " + workDirectroy);
            FileUtils.cleanDirectory(workDirectroy);
            workDirectroy.delete();
        } catch (IOException e) {
            throw new IllegalStateException("Exception while cleaning workDirectroy: "
                    + workDirectroy);
        }
    }

}
