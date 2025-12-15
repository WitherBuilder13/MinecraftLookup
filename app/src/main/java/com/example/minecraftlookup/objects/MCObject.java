package com.example.minecraftlookup.objects;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class MCObject implements Serializable {
    String type, project, name;
    int id, latestContributor;

    public MCObject() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLatestContributor() {
        return latestContributor;
    }

    public void setLatestContributor(int latestContributor) {
        this.latestContributor = latestContributor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "{Object: " + id + " - (" + type + ") " + project + ": " + name + "}";
    }
}
