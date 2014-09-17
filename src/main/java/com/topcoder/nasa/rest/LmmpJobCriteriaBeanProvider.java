package com.topcoder.nasa.rest;

import gov.nasa.pds.entities.SearchCriteria;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * Copy pasta from <a href=
 * "http://stackoverflow.com/questions/5722506/how-do-you-map-multiple-query-parameters-to-the-fields-of-a-bean-on-jersey-get-r"
 * >here</a>. Helps us bind a bunch of query params to a bean of or choosing without having to manually call a bunch of
 * getters and setters.
 */
@Provider
public final class LmmpJobCriteriaBeanProvider implements InjectableProvider<LmmpJobCriteriaParam, Parameter> {
    private static final Logger LOG = LoggerFactory.getLogger(LmmpJobCriteriaBeanProvider.class);

    @Context
    private final HttpContext hc;

    public LmmpJobCriteriaBeanProvider(@Context HttpContext hc) {
        this.hc = hc;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<LmmpJobCriteria> getInjectable(ComponentContext ic, final LmmpJobCriteriaParam a, final Parameter c) {

        if (SearchCriteria.class != c.getParameterClass()) {
            return null;
        }

        return new Injectable<LmmpJobCriteria>() {

            public LmmpJobCriteria getValue() {
                String json = hc.getRequest().getEntity(String.class);

                try {
                    return new org.codehaus.jackson.map.ObjectMapper().readValue(json, LmmpJobCriteria.class);
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to unmarshal payload as LmmpSearchCriteria");
                }
            }
        };
    }
}