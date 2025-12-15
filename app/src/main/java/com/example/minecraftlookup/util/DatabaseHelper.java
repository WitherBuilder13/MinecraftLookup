package com.example.minecraftlookup.util;

import static com.example.minecraftlookup.objects.Contribution.ContributionTypes.*;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.minecraftlookup.objects.Contribution;
import com.example.minecraftlookup.objects.MCObject;
import com.example.minecraftlookup.objects.SourceUsage;
import com.example.minecraftlookup.objects.SourceUsageType;

import java.util.ArrayList;
import java.util.Collections;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MinecraftLookup.db";

    public static final String USERS = "Users", OBJECTS = "Objects", PROJECT_IDS = "ProjectIDs", OBJECT_TYPES = "ObjectTypes",
            SOURCES = "Sources", USAGES = "Usages", SOURCE_USAGE_TYPES = "SourceUsageTypes";
    public static final String RECENT_OBJECTS = "RecentObjects", OBJECT_SOURCE_RELATIONSHIPS = "ObjectSourceRelationships", OBJECT_USAGE_RELATIONSHIPS = "ObjectUsageRelationships",
            OBJECT_CONTRIBUTIONS = "ObjectContributions", SOURCE_CONTRIBUTIONS = "SourceContributions", USAGE_CONTRIBUTIONS = "UsageContributions",
            SOURCE_USAGE_TYPE_CONTRIBUTIONS = "SourceUsageTypeContributions";

    public DatabaseHelper(Context c) {
        // increment version when database fundamentally changes (needs to reset)
        super(c, DATABASE_NAME, null, 14);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USERS + " (id integer primary key autoincrement not null, username varchar(100) unique)");
        db.execSQL("CREATE TABLE " + OBJECTS + " (id integer primary key autoincrement not null, type varchar(100), project varchar(100), name varchar(100), latestContributor integer, " +
                "foreign key (type) references " + OBJECT_TYPES + " (type), foreign key (project) references " + PROJECT_IDS + " (projectID), foreign key (latestContributor) references " + USERS + " (id))");
        db.execSQL("CREATE TABLE " + PROJECT_IDS + " (projectID varchar(100) primary key not null)");
        db.execSQL("CREATE TABLE " + OBJECT_TYPES + " (objectType varchar(100) primary key not null)");
        db.execSQL("CREATE TABLE " + SOURCES + " (id integer primary key autoincrement not null, object integer, type integer, description varchar(1000), latestContributor integer, " +
                "foreign key (object) references " + OBJECTS + " (id), foreign key (type) references " + SOURCE_USAGE_TYPES + " (id), foreign key (latestContributor) references " + USERS + " (id))");
        db.execSQL("CREATE TABLE " + USAGES + " (id integer primary key autoincrement not null, object integer, type integer, description varchar(1000), latestContributor integer, " +
                "foreign key (object) references " + OBJECTS + " (id), foreign key (type) references " + SOURCE_USAGE_TYPES + " (id), foreign key (latestContributor) references " + USERS + " (id))");
        db.execSQL("CREATE TABLE " + SOURCE_USAGE_TYPES + " (id integer primary key autoincrement not null, project varchar(100), name varchar(100), latestContributor integer, " +
                "foreign key (project) references " + PROJECT_IDS + " (projectID), foreign key (latestContributor) references " + USERS + " (id))");

        db.execSQL("CREATE TABLE " + RECENT_OBJECTS + " (userID integer, object integer, " +
                "foreign key (userID) references " + USERS + " (id), foreign key (object) references " + OBJECTS + " (id))");
        db.execSQL("CREATE TABLE " + OBJECT_SOURCE_RELATIONSHIPS + " (source integer, object integer, " +
                "foreign key (source) references " + SOURCES + " (id), foreign key (object) references " + OBJECTS + " (id))");
        db.execSQL("CREATE TABLE " + OBJECT_USAGE_RELATIONSHIPS + " (usage integer, object integer, " +
                "foreign key (usage) references " + USAGES + " (id), foreign key (object) references " + OBJECTS + " (id))");
        db.execSQL("CREATE TABLE " + OBJECT_CONTRIBUTIONS + " (contributor integer, object integer, " +
                "foreign key (contributor) references " + USERS + " (id), foreign key (object) references " + OBJECTS + " (id))");
        db.execSQL("CREATE TABLE " + SOURCE_CONTRIBUTIONS + " (contributor integer, source integer, " +
                "foreign key (contributor) references " + USERS + " (id), foreign key (source) references " + SOURCES + " (id))");
        db.execSQL("CREATE TABLE " + USAGE_CONTRIBUTIONS + " (contributor integer, usage integer, " +
                "foreign key (contributor) references " + USERS + " (id), foreign key (usage) references " + USAGES + " (id))");
        db.execSQL("CREATE TABLE " + SOURCE_USAGE_TYPE_CONTRIBUTIONS + " (contributor integer, sourceUsageType integer, " +
                "foreign key (contributor) references " + USERS + " (id), foreign key (sourceUsageType) references " + SOURCE_USAGE_TYPES + " (id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String[] tables = {
                USERS, OBJECTS, PROJECT_IDS, OBJECT_TYPES, SOURCES, USAGES, SOURCE_USAGE_TYPES,
                RECENT_OBJECTS, OBJECT_SOURCE_RELATIONSHIPS, OBJECT_USAGE_RELATIONSHIPS, OBJECT_CONTRIBUTIONS, SOURCE_CONTRIBUTIONS, USAGE_CONTRIBUTIONS, SOURCE_USAGE_TYPE_CONTRIBUTIONS
        };

        for (String table : tables)
            db.execSQL("DROP TABLE IF EXISTS " + table);

        onCreate(db);
    }

    // -----------------------------------------------------------------------------------------------------------------------------

    public void addBuiltinValues() {

        if (tableIsEmpty(USERS)) {
            // INSERT INTO Users (id, username) VALUES (0, 'Built-in')
            String sql = "INSERT INTO " + USERS + " (id, username) VALUES (0, '" + CommonUtils.DUMMY_USER + "')";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(sql);
            db.close();
        }

        if (tableIsEmpty(PROJECT_IDS)) {
            String sql = "INSERT INTO " + PROJECT_IDS + " (projectID) VALUES ('Minecraft')";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(sql);
            db.close();
        }

        if (tableIsEmpty(OBJECT_TYPES)) {
            String sql = "INSERT INTO " + OBJECT_TYPES + " (objectType) VALUES ('Block'), ('Item'), ('Mob'), ('Entity'), ('Status Effect'), ('Enchantment'), ('Biome'), ('Dimension')";
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(sql);
            db.close();
        }
    }

    // WRITE TO DATABASE -----------------------------------------------------------------------------------------------------------------------------

    public void addUserToDB(String username) {
        // INSERT INTO Users (username) VALUES ('username');
        String sql = "INSERT INTO " + USERS + " (username) VALUES ('" + SQLFormat(username) + "')";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public void addObjectToDB(MCObject object) {
        // INSERT INTO Objects (type, project, name, latestContributor) VALUES ();
        String sql = "INSERT INTO " + OBJECTS + " (type, project, name, latestContributor) VALUES " +
                "('" + SQLFormat(object.getType()) + "', '" + SQLFormat(object.getProject()) + "', '" + SQLFormat(object.getName()) + "', " + object.getLatestContributor() + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }
    
    public void addProjectIDToDB(String projectID) {
        // INSERT INTO ProjectIDs (projectID) VALUES ('projectID');
        String sql = "INSERT INTO " + PROJECT_IDS + " (projectID) VALUES ('" + SQLFormat(projectID) + "')";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }
    
    public void addObjectTypeToDB(String objectType) {
        String sql = "INSERT INTO " + OBJECT_TYPES + " (objectType) VALUES ('" + SQLFormat(objectType) + "')";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }
    
    public void addSourceUsageToDB(SourceUsage sourceUsage, boolean source) {
        String sql = "INSERT INTO " + (source ? SOURCES : USAGES) + " (object, type, description, latestContributor) VALUES " +
                "(" + sourceUsage.getObject() + ", " + sourceUsage.getType() + ", '" + SQLFormat(sourceUsage.getDescription()) + "', " + sourceUsage.getLatestContributor() + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public void addSourceUsageTypeToDB(SourceUsageType sourceUsageType) {
        String sql = "INSERT INTO " + SOURCE_USAGE_TYPES + " (project, name, latestContributor) VALUES ('" +
                SQLFormat(sourceUsageType.getProject()) + "', '" + SQLFormat(sourceUsageType.getName()) + "', " +
                sourceUsageType.getLatestContributor() + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }


    public void addRecentObject(int objectID, int user) {
        // INSERT INTO RecentObjects (contributor, object) VALUES (contributor, objectID);
        String sqlDelete = "DELETE FROM " + RECENT_OBJECTS + " WHERE userID = " + user + " and object = " + objectID;
        String sqlInsert = "INSERT INTO " + RECENT_OBJECTS + " (userID, object) VALUES (" + user + ", " + objectID + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlDelete);
        db.execSQL(sqlInsert);
        db.close();
    }

    public void addObjectSourceUsageRelationship(int sourceUsageID, int objectID, boolean isSource) {
        if (relationshipExists(objectID, sourceUsageID, isSource))
            return;

        String sqlInsert = "INSERT INTO " + (isSource ? OBJECT_SOURCE_RELATIONSHIPS : OBJECT_USAGE_RELATIONSHIPS) +
                " (" + (isSource ? "source" : "usage") + ", object) VALUES (" + sourceUsageID + ", " + objectID + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlInsert);
    }

    public void addObjectContribution(int objectID, int user) {
        if (contributionExists(user, objectID, OBJECT_CONTRIBUTIONS))
            return;

        String sql = "INSERT INTO " + OBJECT_CONTRIBUTIONS + " (contributor, object) VALUES (" + user + ", " + objectID + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public void addSourceUsageContribution(int sourceUsageID, int contributor, boolean isSource) {
        if (contributionExists(contributor, sourceUsageID, (isSource ? SOURCE_CONTRIBUTIONS : USAGE_CONTRIBUTIONS)))
            return;

        String sql = "INSERT INTO " + (isSource ? SOURCE_CONTRIBUTIONS : USAGE_CONTRIBUTIONS) + " (contributor, " + (isSource ? "source" : "usage") +
                ") VALUES (" + contributor + ", " + sourceUsageID + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public void addSourceUsageTypeContribution(int sourceUsageTypeID, int contributor) {
        if (contributionExists(contributor, sourceUsageTypeID, SOURCE_USAGE_TYPE_CONTRIBUTIONS))
            return;

        String sql = "INSERT INTO " + SOURCE_USAGE_TYPE_CONTRIBUTIONS + " (contributor, sourceUsageType) VALUES (" + contributor + ", " + sourceUsageTypeID + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    // -------------------------------------------------------------------------------------------------------------------

    public void updateUserInDB(int id, String username) {
        // UPDATE Users SET username = 'username' WHERE id = id
        String sql = "UPDATE " + USERS + " SET username = '" + SQLFormat(username) + "' WHERE id = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public void updateObjectInDB(MCObject object) {
        String sql = "UPDATE " + OBJECTS + " SET " +
                "type = '" + SQLFormat(object.getType() +
                ", project = '" + SQLFormat(object.getProject() +
                ", name = '" + SQLFormat(object.getName()))) + "', " +
                ", latestContributor = " + object.getLatestContributor() +
                " WHERE id = " + object.getId();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public void updateSourceUsageInDB(SourceUsage sourceUsage, boolean isSource) {
        String sql = "UPDATE " + (isSource ? SOURCES : USAGES) + " SET " +
                "type = " + sourceUsage.getType() +
                ", description = '" + SQLFormat(sourceUsage.getDescription()) +
                "', latestContributor = " + sourceUsage.getLatestContributor() +
                " WHERE id = " + sourceUsage.getId();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    // -------------------------------------------------------------------------------------------------------------------

    public void deleteUserFromDB(int id) {
        // DELETE FROM Users WHERE id = id
        String sqlUsers = "DELETE FROM " + USERS + " WHERE id = " + id;
        String sqlRecentObjects = "DELETE FROM " + RECENT_OBJECTS + " WHERE userID = " + id;
        String sqlObjectContributions = "DELETE FROM " + OBJECT_CONTRIBUTIONS + " WHERE contributor = " + id;
        String sqlSourceContributions = "DELETE FROM " + SOURCE_CONTRIBUTIONS + " WHERE contributor = " + id;
        String sqlUsageContributions = "DELETE FROM " + USAGE_CONTRIBUTIONS + " WHERE contributor = " + id;
        String sqlSourceUsageTypeContributions = "DELETE FROM " + SOURCE_USAGE_TYPE_CONTRIBUTIONS + " WHERE contributor = " + id;

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sqlUsers);
        db.execSQL(sqlRecentObjects);
        db.execSQL(sqlObjectContributions);
        db.execSQL(sqlSourceContributions);
        db.execSQL(sqlUsageContributions);
        db.execSQL(sqlSourceUsageTypeContributions);
        db.close();
    }

    public void deleteObjectFromDB(int id) {
        String sqlObjects = "DELETE FROM " + OBJECTS + " WHERE id = " + id;
        String sqlRecentObjects = "DELETE FROM " + RECENT_OBJECTS + " WHERE object = " + id;
        String sqlObjectSourceRelationships = "DELETE FROM " + OBJECT_SOURCE_RELATIONSHIPS + " WHERE object = " + id;
        String sqlObjectUsageRelationships = "DELETE FROM " + OBJECT_USAGE_RELATIONSHIPS + " WHERE object = " + id;
        String sqlObjectContributions = "DELETE FROM " + OBJECT_CONTRIBUTIONS + " WHERE object = " + id;

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sqlObjects);
        db.execSQL(sqlRecentObjects);
        db.execSQL(sqlObjectContributions);
        db.execSQL(sqlObjectSourceRelationships);
        db.execSQL(sqlObjectUsageRelationships);
        db.close();
    }

    public void deleteSourceUsageFromDB(int id, boolean isSource) {
        String sqlSourceUsages = "DELETE FROM " + (isSource ? SOURCES : USAGES) + " WHERE id = " + id;
        String sqlObjectSourceUsageRelationships = "DELETE FROM " + (isSource ? OBJECT_SOURCE_RELATIONSHIPS : OBJECT_USAGE_RELATIONSHIPS) + " WHERE " + (isSource ? "source" : "usage") + " = " + id;
        String sqlSourceUsageContributions = "DELETE FROM " + (isSource ? SOURCE_CONTRIBUTIONS : USAGE_CONTRIBUTIONS) + " WHERE " + (isSource ? "source" : "usage") + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(sqlSourceUsages);
        db.execSQL(sqlObjectSourceUsageRelationships);
        db.execSQL(sqlSourceUsageContributions);
        db.close();
    }

    // READ FROM DATABASE -------------------------------------------------------------------------------------------------------------------

    public boolean tableIsEmpty(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        int rows = (int) DatabaseUtils.queryNumEntries(db, table);
        db.close();

        return rows == 0;
    }

    public boolean userExists(String username) {
        // SELECT COUNT(username) FROM Users WHERE username = 'username';
        String query = "SELECT COUNT(*) FROM " + USERS + " WHERE username = '" + SQLFormat(username) + "'";

        return exists(query);
    }

    public boolean objectExists(String type, String project, String name) {
        String query = "SELECT COUNT(*) FROM " + OBJECTS + " WHERE type = '" + SQLFormat(type) + "' AND  project = '" + SQLFormat(project) + "' AND name = '" + SQLFormat(name) + "'";

        return exists(query);
    }

    public boolean objectTypeExists(String objectType) {
        String query = "SELECT COUNT(*) FROM " + OBJECT_TYPES + " WHERE objectType = '" + SQLFormat(objectType) + "'";

        return exists(query);
    }

    public boolean projectIDExists(String projectID) {
        String query = "SELECT COUNT(*) FROM " + PROJECT_IDS + " WHERE projectID = '" + SQLFormat(projectID) + "'";

        return exists(query);
    }

    public boolean sourceUsageTypeExists(String name, String project) {
        String query = "SELECT COUNT(*) FROM " + SOURCE_USAGE_TYPES + " WHERE name = '" + SQLFormat(name) + "' AND project = '" + SQLFormat(project) + "'";

        return exists(query);
    }

    public boolean relationshipExists(int object, int sourceUsage, boolean isSource) {
        String query = "SELECT COUNT(*) FROM " + (isSource ? OBJECT_SOURCE_RELATIONSHIPS : OBJECT_USAGE_RELATIONSHIPS) +
                " WHERE object = " + object + " AND " + (isSource ? "source" : "usage") + " = " + sourceUsage;

        return exists(query);
    }

    public boolean contributionExists(int contributor, int id, String tableName) {
        if (!(tableName.equals(OBJECT_CONTRIBUTIONS) || tableName.equals(SOURCE_CONTRIBUTIONS) || tableName.equals(USAGE_CONTRIBUTIONS) || tableName.equals(SOURCE_USAGE_TYPE_CONTRIBUTIONS)))
            return true;

        String reference = "";

        switch (tableName) {
            case OBJECT_CONTRIBUTIONS:
                reference = "object";
                break;
            case SOURCE_CONTRIBUTIONS:
                reference = "source";
                break;
            case USAGE_CONTRIBUTIONS:
                reference = "usage";
                break;
            case SOURCE_USAGE_TYPE_CONTRIBUTIONS:
                reference = "sourceUsageType";
                break;
        }
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE contributor = " + contributor + " AND " + reference + " = " + id;

        return exists(query);
    }


    private boolean exists(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        int count = cursor.getInt(0);

        cursor.close();
        db.close();

        return count != 0;
    }

    // ---------------------------------------------------------------------------------------------------------------------

    public int findLatestID(String tableName) {
        String query = "SELECT id FROM " + tableName + " ORDER BY id DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int id;

        cursor.moveToFirst();
        id = cursor.getInt(0);
        cursor.close();

        return id;
    }

    public int findUserID(String username) {
        if (userExists(username)) {
            // SELECT id FROM Users WHERE username = 'username';
            String query = "SELECT id FROM " + USERS + " WHERE username = '" + SQLFormat(username) + "'";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            cursor.moveToFirst();
            int id = cursor.getInt(0);

            cursor.close();
            db.close();

            return id;
        }

        return -1;
    }

    public String findUser(int id) {
        // SELECT id FROM Users WHERE id = id;
        String query = "SELECT username FROM " + USERS + " WHERE id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        String username;

        if (cursor.moveToFirst()) {
            username = cursor.getString(0);
            cursor.close();
        } else
            username = "";

        db.close();

        return username;
    }

    public MCObject findObject(int id) {
        String query = "SELECT * FROM " + OBJECTS + " WHERE id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MCObject object = new MCObject();

        if (cursor.moveToFirst()) {
            object.setId(cursor.getInt(0));
            object.setType(cursor.getString(1));
            object.setProject(cursor.getString(2));
            object.setName(cursor.getString(3));
            object.setLatestContributor(cursor.getInt(4));
        } else
            object = null;

        cursor.close();
        db.close();

        return object;
    }

    public SourceUsage findSourceUsage(int id, boolean isSource) {
        String query = "SELECT * FROM " + (isSource ? SOURCES : USAGES) + " WHERE id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        SourceUsage sourceUsage = new SourceUsage();

        if (cursor.moveToFirst()) {
            sourceUsage.setId(cursor.getInt(0));
            sourceUsage.setObject(cursor.getInt(1));
            sourceUsage.setType(cursor.getInt(2));
            sourceUsage.setDescription(cursor.getString(3));
            sourceUsage.setLatestContributor(cursor.getInt(4));
        } else
            sourceUsage = null;

        cursor.close();
        db.close();

        return sourceUsage;
    }

    public SourceUsageType findSourceUsageType(int id) {
        String query = "SELECT * FROM " + SOURCE_USAGE_TYPES + " WHERE id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        SourceUsageType sourceUsageType = new SourceUsageType();

        if (cursor.moveToFirst()) {
            sourceUsageType.setId(cursor.getInt(0));
            sourceUsageType.setProject(cursor.getString(1));
            sourceUsageType.setName(cursor.getString(2));
            sourceUsageType.setLatestContributor(cursor.getInt(3));
        } else
            sourceUsageType = null;

        cursor.close();
        db.close();

        return sourceUsageType;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public ArrayList<String> findUsers(String username) {
        String query = "SELECT username FROM " + USERS + " WHERE username LIKE '%" + SQLFormat(username) + "%'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> usersList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                usersList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usersList;
    }

    public ArrayList<MCObject> getAllObjects() {
        String query = "SELECT id FROM " + OBJECTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> objectIDs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                objectIDs.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        ArrayList<MCObject> objectsList = new ArrayList<>();

        for (int objectID : objectIDs)
            objectsList.add(findObject(objectID));

        db.close();

        return objectsList;
    }

    public ArrayList<MCObject> findObjects(ArrayList<String> types, ArrayList<String> projects, ArrayList<String> contributors, String search) {
        if (types.isEmpty() || projects.isEmpty() || contributors.isEmpty())
            return new ArrayList<>();

        String query = findObjectsQuery(types, projects, contributors, search);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> objectIDsFromCriteria = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                objectIDsFromCriteria.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        ArrayList<MCObject> objectsList = new ArrayList<>();

        for (int objectIDFromCriteria : objectIDsFromCriteria)
            objectsList.add(findObject(objectIDFromCriteria));

        db.close();

        return objectsList;
    }

    public ArrayList<MCObject> getAllRelevantObjects(int sourceUsageID, boolean isSource) {
        String query = "SELECT object FROM " + (isSource ? OBJECT_SOURCE_RELATIONSHIPS : OBJECT_USAGE_RELATIONSHIPS) + " WHERE " + (isSource ? "source" : "usage") + " = " + sourceUsageID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<MCObject> objectsList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                MCObject object = findObject(cursor.getInt(0));
                objectsList.add(object);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return objectsList;
    }

    public ArrayList<String> getAllStringIDs(String tableName) {
        if (!(tableName.equals(OBJECT_TYPES) || tableName.equals(PROJECT_IDS))) {
            Log.e("Database", "Cannot return String value from table " + tableName);
            return null;
        }

        // SELECT * FROM ObjectTypes
        String query = "SELECT * FROM " + tableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> stringIDList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                stringIDList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return stringIDList;
    }

    public ArrayList<SourceUsage> findSourceUsages(int object, ArrayList<MCObject> relevantObjects, ArrayList<SourceUsageType> types, ArrayList<String> contributors, String search, boolean isSource) {
        if (types.isEmpty() || contributors.isEmpty())
            return new ArrayList<>();

        String query = findSourceUsagesQuery(object, relevantObjects, types, contributors, search, isSource);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> sourceUsageIDsFromCriteria = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                sourceUsageIDsFromCriteria.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        ArrayList<SourceUsage> sourceUsagesList = new ArrayList<>();

        for (int sourceUsageIDFromCriteria : sourceUsageIDsFromCriteria)
            sourceUsagesList.add(findSourceUsage(sourceUsageIDFromCriteria, isSource));

        db.close();

        return sourceUsagesList;
    }

    public ArrayList<SourceUsageType> getAllSourceUsageTypes() {
        String query = "SELECT id FROM " + SOURCE_USAGE_TYPES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Integer> sourceUsageTypeIDs = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                sourceUsageTypeIDs.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        cursor.close();

        ArrayList<SourceUsageType> sourceUsageTypeList = new ArrayList<>();

        for (int sourceUsageTypeID : sourceUsageTypeIDs)
            sourceUsageTypeList.add(findSourceUsageType(sourceUsageTypeID));

        db.close();

        return sourceUsageTypeList;
    }


    public ArrayList<MCObject> getRecentObjects(int user) {
        String queryR = "SELECT object FROM " + RECENT_OBJECTS + " WHERE userID = " + user;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorR = db.rawQuery(queryR, null);
        ArrayList<MCObject> objectsList = new ArrayList<>();

        if (cursorR.moveToFirst()) {
            do {
                int id = cursorR.getInt(0);
                String queryO = "SELECT type,project,name,latestContributor FROM " + OBJECTS + " WHERE id = " + id;
                Cursor cursorO = db.rawQuery(queryO, null);

                if (cursorO.moveToFirst()) {
                    String type = cursorO.getString(0);
                    String project = cursorO.getString(1);
                    String name = cursorO.getString(2);
                    int latestContributor = cursorO.getInt(3);

                    MCObject object = new MCObject();
                    object.setId(id);
                    object.setType(type);
                    object.setProject(project);
                    object.setName(name);
                    object.setLatestContributor(latestContributor);

                    objectsList.add(object);
                }

                cursorO.close();
            } while (cursorR.moveToNext());
        }

        cursorR.close();
        db.close();

        Collections.reverse(objectsList);

        return objectsList;
    }

    public ArrayList<Contribution> findContributions(int contributor, ArrayList<Contribution.ContributionTypes> types, String search) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Contribution> contributionsList = new ArrayList<>();

        if (types.contains(OBJECT)) {
            String query = fCQObjects(contributor, search);
            Cursor cursor = db.rawQuery(query, null);
            addToContributionList(cursor, contributionsList, OBJECT);
            cursor.close();
        }

        if (types.contains(SOURCE)) {
            String query = fCQSources(contributor, search);
            Cursor cursor = db.rawQuery(query, null);
            addToContributionList(cursor, contributionsList, SOURCE);
            cursor.close();
        }

        if (types.contains(USAGE)) {
            String query = fCQUsages(contributor, search);
            Cursor cursor = db.rawQuery(query, null);
            addToContributionList(cursor, contributionsList, USAGE);
            cursor.close();
        }

        if (types.contains(SOURCE_USAGE_TYPE)) {
            String query = fCQSourceUsageTypes(contributor, search);
            Cursor cursor = db.rawQuery(query, null);
            addToContributionList(cursor, contributionsList, SOURCE_USAGE_TYPE);
            cursor.close();
        }

        return contributionsList;
    }

    // ----------------------------------------------------------------------------------------------------------------

    private String SQLFormat(String string) {
        return string.replace("'", "''");
    }

    @NonNull
    private String findObjectsQuery(ArrayList<String> types, ArrayList<String> projects, ArrayList<String> contributors, String search) {

        // SELECT O.id FROM Objects AS O
        // INNER JOIN ObjectContributions OC ON O.id = OC.object

        // WHERE
        // O.type IN ('type1', 'type2', ...)
        // AND O.project IN ('project1', 'project2', ...)
        // AND OC.contributor IN ('contributor1', 'contributor2', ...)
        // AND O.name LIKE '%name%'

        StringBuilder query = new StringBuilder("SELECT O.id FROM " + OBJECTS + " AS O" +
                " INNER JOIN " + OBJECT_CONTRIBUTIONS + " OC ON O.id = OC.object" +
                " WHERE");

        query.append(" O.type IN ('").append(SQLFormat(types.get(0))).append("'");

        if (types.size() > 1)
            for (int i = 1; i < types.size(); i++)
                query.append(", '").append(SQLFormat(types.get(i))).append("'");

        query.append(") AND O.project IN ('").append(SQLFormat(projects.get(0))).append("'");

        if (projects.size() > 1)
            for (int i = 1; i < projects.size(); i++)
                query.append(", '").append(SQLFormat(projects.get(i))).append("'");

        query.append(") AND OC.contributor IN (").append(findUserID(SQLFormat(contributors.get(0))));

        if (contributors.size() > 1)
            for (int i = 1; i < contributors.size(); i++)
                query.append(", ").append(findUserID(SQLFormat(contributors.get(i))));

        query.append(") AND O.name").append(likeSearch(search));

        return query.toString();
    }

    @NonNull
    private String findSourceUsagesQuery(int object, ArrayList<MCObject> relevantObjects, ArrayList<SourceUsageType> types, ArrayList<String> contributors, String search, boolean isSource) {

        // SELECT SU.id FROM Sources AS SU
        // INNER JOIN SourceContributions SUC ON SU.id = SUC.source
        // INNER JOIN ObjectSourceRelationships OSUR ON SU.id = OSUR.source
        // INNER JOIN Objects O ON OSUR.object = O.id

        // WHERE
        // SU.object = object
        // AND SU.type IN (typeID1, typeID1, ...)
        // AND SUC.contributor IN (contributorID1, contributorID2, ...)
        // AND OSUR.object IN (relevantObjectID1, relevantObjectID2, ...)
        // AND (O.name LIKE '%search%' OR SU.description LIKE '%search%')

        String SU = (isSource ? SOURCES : USAGES);
        String su = (isSource ? ".source" : ".usage");
        String CONTRIBUTIONS = (isSource ? SOURCE_CONTRIBUTIONS : USAGE_CONTRIBUTIONS);
        String OBJECT_RELATIONSHIPS = (isSource ? OBJECT_SOURCE_RELATIONSHIPS : OBJECT_USAGE_RELATIONSHIPS);

        StringBuilder query = new StringBuilder("SELECT DISTINCT SU.id FROM " + SU + " AS SU" +
                " INNER JOIN " + CONTRIBUTIONS + " SUC ON SU.id = SUC" + su +
                " INNER JOIN " + OBJECT_RELATIONSHIPS + " OSUR ON SU.id = OSUR" + su +
                " INNER JOIN " + OBJECTS + " O ON OSUR.object = O.id" +
                " WHERE SU.object = " + object);

        query.append(" AND SU.type IN (").append(types.get(0).getId());

        if (types.size() > 1)
            for (int i = 1; i < types.size(); i++)
                query.append(",").append(types.get(i).getId());

        query.append(") AND SUC.contributor IN (").append(findUserID(SQLFormat(contributors.get(0))));

        if (contributors.size() > 1)
            for (int i = 1; i < contributors.size(); i++)
                query.append(",").append(findUserID(SQLFormat(contributors.get(i))));

        if (!relevantObjects.isEmpty()) {
            query.append(") AND OSUR.object IN (").append(relevantObjects.get(0).getId());

            if (relevantObjects.size() > 1)
                for (int i = 1; i < relevantObjects.size(); i++)
                    query.append(",").append(relevantObjects.get(i).getId());
        }

        query.append(") AND (O.name").append(likeSearch(search)).append(" OR SU.description").append(likeSearch(search)).append(")");

        return query.toString();
    }

    @NonNull
    private String fCQObjects(int contributor, String search) {

        // SELECT OC.object FROM ObjectContributions AS OC
        // INNER JOIN Objects O ON OC.object = O.id

        // WHERE OC.contributor = user
        // AND (
        // O.name LIKE '%search%'
        // OR O.type LIKE '%search%'
        // OR O.project LIKE '%search%'
        // )

        String likeSearch = likeSearch(search);

        return "SELECT OC.object FROM " + OBJECT_CONTRIBUTIONS + " AS OC" +
                " INNER JOIN " + OBJECTS + " O ON OC.object = O.id" +

                " WHERE OC.contributor = " + contributor +
                " AND (O.name" + likeSearch +
                " OR O.type" + likeSearch +
                " OR O.project" + likeSearch +
                ")";
    }

    @NonNull
    private String fCQSources(int contributor, String search) {

        // SELECT SC.source FROM SourceContributions AS SC
        // INNER JOIN Sources S ON SC.source = S.id
        // INNER JOIN Objects O ON S.object = O.id
        // INNER JOIN ObjectSourceRelationships OSR ON S.id = OSR.source
        // INNER JOIN Objects RO ON OSR.object = RO.id
        // INNER JOIN SourceUsageTypes SUT ON S.type = SUT.id

        // WHERE
        // SC.contributor = user
        // AND (
        // S.description LIKE search
        // OR O.name LIKE search
        // OR O.type LIKE search
        // O.project
        // RO.name
        // RO.type
        // RO.project
        // SUT.name
        // SUT.project
        // )

        String likeSearch = likeSearch(search);

        return "SELECT DISTINCT SC.source FROM " + SOURCE_CONTRIBUTIONS + " AS SC" +
                " INNER JOIN " + SOURCES + " S ON SC.source = S.id" +
                " INNER JOIN " + OBJECTS + " O ON S.object = O.id" +
                " INNER JOIN " + OBJECT_SOURCE_RELATIONSHIPS + " OSR ON S.id = OSR.source" +
                " INNER JOIN " + OBJECTS + " RO ON OSR.object = RO.id" +
                " INNER JOIN " + SOURCE_USAGE_TYPES + " SUT ON S.type = SUT.id " +

                " WHERE SC.contributor = " + contributor +
                " AND (S.description" + likeSearch +
                " OR O.name" + likeSearch +
                " OR O.type" + likeSearch +
                " OR O.project" + likeSearch +
                " OR RO.name" + likeSearch +
                " OR RO.type" + likeSearch +
                " OR RO.project" + likeSearch +
                " OR SUT.name" + likeSearch +
                " OR SUT.project" + likeSearch +
                ")";
    }

    private String fCQUsages(int contributor, String search) {
        String likeSearch = likeSearch(search);

        return "SELECT DISTINCT UC.usage FROM " + USAGE_CONTRIBUTIONS + " AS UC" +
                " INNER JOIN " + USAGES + " U ON UC.usage = U.id" +
                " INNER JOIN " + OBJECTS + " O ON U.object = O.id" +
                " INNER JOIN " + OBJECT_USAGE_RELATIONSHIPS + " OUR ON U.id = OUR.usage" +
                " INNER JOIN " + OBJECTS + " RO ON OUR.object = RO.id" +
                " INNER JOIN " + SOURCE_USAGE_TYPES + " SUT ON U.type = SUT.id " +

                " WHERE UC.contributor = " + contributor +
                " AND (U.description" + likeSearch +
                " OR O.name" + likeSearch +
                " OR O.type" + likeSearch +
                " OR O.project" + likeSearch +
                " OR RO.name" + likeSearch +
                " OR RO.type" + likeSearch +
                " OR RO.project" + likeSearch +
                " OR SUT.name" + likeSearch +
                " OR SUT.project" + likeSearch +
                ")";
    }

    private String fCQSourceUsageTypes(int contributor, String search) {

        // SELECT SUTC.sourceUsageType FROM SourceUsageTypeContributions AS SUTC
        // INNER JOIN SourceUsageTypes SUT ON SUTC.sourceUsageType = SUT.id

        // WHERE
        // SUTC.contributor = user
        // AND (
        // SUT.name LIKE search
        // OR SUT.project
        // )

        return "SELECT SUTC.sourceUsageType FROM " + SOURCE_USAGE_TYPE_CONTRIBUTIONS + " AS SUTC" +
                " INNER JOIN " + SOURCE_USAGE_TYPES + " SUT ON SUTC.sourceUsageType = SUT.id" +

                " WHERE SUTC.contributor = " + contributor +
                " AND (SUT.name" + likeSearch(search) +
                " OR SUT.project" + likeSearch(search) +
                ")";
    }

    private void addToContributionList(Cursor cursor, ArrayList<Contribution> list, Contribution.ContributionTypes type) {
        if (cursor.moveToFirst()) {
            do {
                Contribution contribution = new Contribution();
                contribution.setId(cursor.getInt(0));
                contribution.setType(type);

                list.add(contribution);
            } while (cursor.moveToNext());
        }
    }

    private String likeSearch(String search) {
        return " LIKE '%" + SQLFormat(search) + "%'";
    }
}
