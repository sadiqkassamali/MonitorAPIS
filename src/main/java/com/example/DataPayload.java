package com.example;

public class DataPayload {
    private String applicationName;
    private String tag;
    private int failPercent;
    private int passPercent;
    private String env;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getFailPercent() {
        return failPercent;
    }

    public void setFailPercent(int failPercent) {
        this.failPercent = failPercent;
    }

    public int getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(int passPercent) {
        this.passPercent = passPercent;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    private String featureName;
    private String dateTime;

    // Getters and setters (you can generate them in your IDE)
}
