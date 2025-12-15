package com.example.minecraftlookup.util;

public class SessionData {

    public static boolean HOME;
    static int loggedInUser;

    public static int getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(int loggedInUser) {
        SessionData.loggedInUser = loggedInUser;
    }
}
