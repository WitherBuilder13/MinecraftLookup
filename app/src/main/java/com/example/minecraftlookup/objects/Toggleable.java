package com.example.minecraftlookup.objects;

import androidx.annotation.NonNull;

public class Toggleable<T> {
    T entry;
    boolean toggled;

    public Toggleable(T e, boolean t) {
        entry = e;
        toggled = t;
    }

    public T getEntry() {
        return entry;
    }

    public void setEntry(T entry) {
        this.entry = entry;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    @NonNull
    @Override
    public String toString() {
        return "{Toggleable: " + getEntry() + ", " + isToggled() + "}";
    }
}
