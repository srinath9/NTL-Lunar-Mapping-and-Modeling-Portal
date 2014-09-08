package com.topcoder.nasa.ode.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Container for the ODEResults field in the ODE REST response: <a
 * href="http://oderest.rsl.wustl.edu/ODE_REST%20_V2.0.pdf">API</a>.
 *
 */
public class ProductsWrapper {
    @JsonProperty("Product")
    public List<Product> products;
}
