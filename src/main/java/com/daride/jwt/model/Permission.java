package com.daride.jwt.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Permission {

    @SerializedName("module")
    String module;

    @SerializedName("scope")
    String scope;

    @SerializedName("actions")
    List<String> actions = new ArrayList<>();

    public Permission() {

    }

    public Permission(String module, String scope, List<String> actions) {
        this.module = module;
        this.scope = scope;
        this.actions = actions;
    }

    public String getModule() {

        return module;
    }

    public void setModule(String module) {

        this.module = module;
    }

    public String getScope() {

        return scope;
    }

    public void setScope(String scope) {

        this.scope = scope;
    }

    public List<String> getActions() {

        return actions;
    }

    public void setActions(List<String> actions) {

        this.actions = actions;
    }

    public void addActions(String actions) {

        this.actions.add(actions);
    }
}
