package com.topcoder.nasa.rest;

import javax.servlet.http.HttpServletRequest;

public class ResourceUtil {
    /**
     * Computes the request URL + query parameters from the given request
     * 
     * @param request
     *            what to compute the request url from
     * @return the computed url
     */
    public static String getRequestUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();

        if (request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }

        return requestURL.toString();
    }

}
