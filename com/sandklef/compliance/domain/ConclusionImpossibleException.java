package com.sandklef.compliance.domain;

public class ConclusionImpossibleException extends Exception {

    private final Component component;

    public ConclusionImpossibleException(String msg, Component c) {
        super(msg);
        component = c;
    }

    public Component component() {
        return component;
    }

}
