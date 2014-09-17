package com.topcoder.nasa.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.nasa.job.LmmpJob;
import com.topcoder.nasa.job.LmmpJobImageUrlRepository;
import com.topcoder.nasa.job.LmmpJobRepository;

/**
 * Resource that acts as programmatic gateway to the {@link LmmpJob} universe.
 *
 */
@Component
@Path("/jobs")
public class LmmpJobResource {
    @Autowired
    private LmmpJobRepository lmmpJobRepository;

    @Autowired
    private LmmpJobImageUrlRepository lmmpJobImageUrlRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<LmmpJobDto> getJobs() {
        List<LmmpJobDto> dtos = new ArrayList<LmmpJobDto>();

        List<LmmpJob> allJobs = lmmpJobRepository.findAll();

        for (LmmpJob job : allJobs) {
            LmmpJobDto dto = new LmmpJobDto();

            dto.setFailReason(job.getFailInfo());
            dto.setHadoopJobId(job.getHadoopJobId());
            dto.setJobCriteria(job.getJobCriteria());
            dto.setStatus(job.getStatus().displayName());
            dto.setUuid(job.getUuid());
            dto.setNumberOfImages(job.getNumberOfImages());
            dto.setImageUrls(lmmpJobImageUrlRepository.getImageUrls(job));
            dto.setRequestStart(job.getCreated());
            dto.setRequestEnd(job.getFinished());

            dtos.add(dto);
        }

        return dtos;
    }
}
