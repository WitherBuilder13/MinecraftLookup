package com.example.minecraftlookup.objects;

public class SourceUsage {
    String description;
    int id, object, type, latestContributor;

    public SourceUsage() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObject() {
        return object;
    }

    public void setObject(int object) {
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLatestContributor() {
        return latestContributor;
    }

    public void setLatestContributor(int latestContributor) {
        this.latestContributor = latestContributor;
    }
}
