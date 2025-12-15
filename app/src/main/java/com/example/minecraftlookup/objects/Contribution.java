package com.example.minecraftlookup.objects;

public class Contribution {

    int id;
    ContributionTypes type;

    public Contribution() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContributionTypes getType() {
        return type;
    }

    public void setType(ContributionTypes type) {
        this.type = type;
    }

    public enum ContributionTypes {
        OBJECT,
        SOURCE,
        USAGE,
        SOURCE_USAGE_TYPE
    }
}
