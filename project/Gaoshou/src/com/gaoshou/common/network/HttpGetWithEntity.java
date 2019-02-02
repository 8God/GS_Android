package com.gaoshou.common.network;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

@SuppressWarnings("deprecation")
public class HttpGetWithEntity extends HttpEntityEnclosingRequestBase {

    private static final String METHOD_NAME = "GET";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpGetWithEntity(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpGetWithEntity(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpGetWithEntity() {
        super();
    }
}
