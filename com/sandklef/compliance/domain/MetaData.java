package com.sandklef.compliance.domain;

public class MetaData {

    private String producer;
    private String version;

    public String producer() {
        return producer;
    }

    public String version() {
        return version;
    }

    public MetaData(String producer, String version) {
        this.producer = producer;
        this.version = version;
    }
}
