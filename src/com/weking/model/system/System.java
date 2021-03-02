package com.weking.model.system;

public class System {
    private String args;

    private String aves;

    private String name;

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args == null ? null : args.trim();
    }

    public String getAves() {
        return aves;
    }

    public void setAves(String aves) {
        this.aves = aves == null ? null : aves.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}