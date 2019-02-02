package com.gaoshou.common.network;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

@SuppressWarnings("deprecation")
public class HttpDeleteWithEntity extends HttpEntityEnclosingRequestBase {

    private static final String METHOD_NAME = "DELETE";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpDeleteWithEntity(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpDeleteWithEntity(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpDeleteWithEntity() {
        super();
    }
}
