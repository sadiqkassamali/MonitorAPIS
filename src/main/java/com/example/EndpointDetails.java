package com.example;

public class EndpointDetails {

    private String     uniqueId;
    private String url;
    private String method;
    private String endpoint;
    private String requestBody;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationFeature() {
        return applicationFeature;
    }

    public void setApplicationFeature(String applicationFeature) {
        this.applicationFeature = applicationFeature;
    }

    public String getApplicationEnvironment() {
        return applicationEnvironment;
    }

    public void setApplicationEnvironment(String applicationEnvironment) {
        this.applicationEnvironment = applicationEnvironment;
    }

    private String contentType;
    private String applicationName;
    private String applicationFeature;
    private String applicationEnvironment;

}
