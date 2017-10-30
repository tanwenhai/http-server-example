package com.twh.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Url拼接
 * @author
 */
public class URLBuilder {
    private String scheme;
    private String host;
    private URL origin;
    private int port;
    private List<String> paths = new LinkedList<>();

    private static final char PATH_SPLIT = '/';

    public URLBuilder() {
        port = -1;
    }

    public URLBuilder origin(String origin) {
        try {
            this.origin = new URL(origin);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }

        return this;
    }

    public URLBuilder scheme(String scheme) {
        this.scheme = scheme;

        return this;
    }

    public URLBuilder host(String host) {
        this.host = host;

        return this;
    }

    public URLBuilder addPath(String path) {
        if (path.charAt(0) != PATH_SPLIT) {
            path = PATH_SPLIT + path;
        }

        paths.add(path);

        return this;
    }

    public URLBuilder port(int port) {
        this.port = port;

        return this;
    }

    public URL build() throws MalformedURLException {
        return new URL(buildString());
    }

    public String buildString() {
        StringBuilder sb = new StringBuilder();
        if (origin == null) {
            if (scheme != null) {
                sb.append(scheme).append("://");
            }
            if (host != null) {
                sb.append(host);
            }
            if (port != -1) {
                sb.append(":").append(port);
            }
        } else {
            sb.append(origin.toString());
        }

        StringBuilder pathSb = new StringBuilder();
        for (String path : paths) {
            pathSb.append(path);
        }
        sb.append(pathSb.toString().replaceAll("\\w//", "/"));

        return sb.toString();
    }
}