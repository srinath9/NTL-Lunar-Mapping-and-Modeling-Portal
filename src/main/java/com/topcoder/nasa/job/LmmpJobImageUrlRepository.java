package com.topcoder.nasa.job;

import java.util.List;

/**
 * Since we are not using an ORM, the "easy way out" for mapping parent-child relationships is to
 * specify the children as distinct, seprate entities that are fetched at a different time to their
 * parents.
 * <p/>
 * This avoids the parent class repository from having to do dirty checking on child elements etc.
 * <p/>
 * This breaks the domain driven design ideoleogy, but its the pragmatic thing to do.
 */
public interface LmmpJobImageUrlRepository {
    /**
     * Sets the image URLs for the given {@link LmmpJob}.
     * 
     * @param job
     *            the {@link LmmpJob} to set the URLs for
     * @param urls
     *            the image URLs themselves
     */
    public void setImageUrls(LmmpJob job, List<String> urls);

    /**
     * Gets the image URLs for the given {@link LmmpJob}
     * 
     * @param job
     *            the {@link LmmpJob} to get the URLs for
     * @return a list of URLs; returns an empty list if none
     */
    public List<String> getImageUrls(LmmpJob job);
}
