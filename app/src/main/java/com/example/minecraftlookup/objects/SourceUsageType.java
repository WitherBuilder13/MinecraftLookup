package com.example.minecraftlookup.objects;

public class SourceUsageType {
    String project, name;
    int id, latestContributor;

    public SourceUsageType() {}

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLatestContributor() {
        return latestContributor;
    }

    public void setLatestContributor(int latestContributor) {
        this.latestContributor = latestContributor;
    }

    @Override
    public String toString() {
        return "{SourceUsageType: " + id + " - (" + project + ") " + name;
    }
}
