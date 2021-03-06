package com.googleandroidtv.tvrecommendations.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.googleandroidtv.R;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class DbHelper extends SQLiteOpenHelper {
    private static boolean DEBUG;
    private static String TAG;
    private static DbHelper sDbHelper;
    private Context mContext;
    private Object mLock;
    private boolean mMigrationEnabled;
    private Long mMostRecentTimeStamp;

    private class GetEntitiesTask extends AsyncTask<Void, Void, Void> {
        private List<String> mBlacklistedPackages;
        private HashMap<String, Entity> mEntities;
        private Listener mListener;

        public GetEntitiesTask(Listener listener) {
            this.mListener = listener;
        }

        protected Void doInBackground(Void... params) {
            HashMap<String, Entity> entities = new HashMap();
            SQLiteDatabase db = DbHelper.this.getWritableDatabase();
            Cursor c = db.query("entity", null, null, null, null, null, null);
            try {
                String key;
                Entity entity;
                int keyIndex = c.getColumnIndexOrThrow("key");
                int bonusIndex = c.getColumnIndex("notif_bonus");
                int bonusTimeIndex = c.getColumnIndex("bonus_timestamp");
                int orderIndex = c.getColumnIndex("oob_order");
                int postedRecIndex = c.getColumnIndex("has_recs");
                while (c.moveToNext()) {
                    key = c.getString(keyIndex);
                    double bonus = bonusIndex == -1 ? 0.0d : c.getDouble(bonusIndex);
                    long bonusTime = bonusTimeIndex == -1 ? 0 : c.getLong(bonusTimeIndex);
                    long initialOrder = orderIndex == -1 ? 0 : c.getLong(orderIndex);
                    boolean postedRec = postedRecIndex != -1 && c.getLong(postedRecIndex) == 1;
                    if (!TextUtils.isEmpty(key)) {
                        Entity ent = new Entity(DbHelper.this.mContext, DbHelper.this, key, initialOrder, postedRec);
                        if (bonusTime != 0 && bonus > 0.0d) {
                            ent.setBonusValues(bonus, bonusTime);
                        }
                        entities.put(key, ent);
                    }
                }
                c = db.query("entity_scores", null, null, null, null, null, null);
                keyIndex = c.getColumnIndexOrThrow("key");
                int componentIndex = c.getColumnIndex("component");
                int entityScoreIndex = c.getColumnIndex("entity_score");
                int lastOpenedIndex = c.getColumnIndex("last_opened");
                while (c.moveToNext()) {
                    key = c.getString(keyIndex);
                    String component = c.getString(componentIndex);
                    long entityScore = entityScoreIndex == -1 ? 0 : c.getLong(entityScoreIndex);
                    long lastOpened = lastOpenedIndex == -1 ? 0 : c.getLong(lastOpenedIndex);
                    synchronized (DbHelper.this.mLock) {
                        if (DbHelper.this.mMostRecentTimeStamp.longValue() < lastOpened) {
                            DbHelper.this.mMostRecentTimeStamp = Long.valueOf(lastOpened);
                        }
                        try {
                        } catch (Throwable th) {
                            c.close();
                        }
                    }
                    if (!TextUtils.isEmpty(key)) {
                        entity = (Entity) entities.get(key);
                        if (entity != null) {
                            entity.setOrder(component, entityScore);
                            entity.setLastOpenedTimeStamp(component, lastOpened);
                        }
                    }
                }
                c.close();
                c = db.query("buckets", new String[]{"key", "group_id", "last_updated"}, null, null, null, null, "key, last_updated");
                try {
                    String group;
                    keyIndex = c.getColumnIndexOrThrow("key");
                    int groupIndex = c.getColumnIndex("group_id");
                    int timeStampIndex = c.getColumnIndex("last_updated");
                    while (c.moveToNext()) {
                        key = c.getString(keyIndex);
                        group = c.getString(groupIndex);
                        long time = c.getLong(timeStampIndex);
                        if (!TextUtils.isEmpty(key)) {
                            entity = (Entity) entities.get(key);
                            if (entity != null) {
                                entity.addBucket(group, time);
                            }
                        }
                    }
                    c = db.query("buffer_scores", new String[]{"_id", "key", "group_id", "day", "mClicks", "mImpressions"}, null, null, null, null, "key, group_id, _id");
                    try {
                        keyIndex = c.getColumnIndexOrThrow("key");
                        groupIndex = c.getColumnIndex("group_id");
                        int dayIndex = c.getColumnIndex("day");
                        int clicksIndex = c.getColumnIndex("mClicks");
                        int impressionsIndex = c.getColumnIndex("mImpressions");
                        while (c.moveToNext()) {
                            key = c.getString(keyIndex);
                            group = c.getString(groupIndex);
                            int day = c.getInt(dayIndex);
                            int clicks = clicksIndex == -1 ? 0 : c.getInt(clicksIndex);
                            int impressions = impressionsIndex == -1 ? 0 : c.getInt(impressionsIndex);
                            if (!(TextUtils.isEmpty(key) || day == -1)) {
                                entity = (Entity) entities.get(key);
                                if (entity != null) {
                                    entity.getSignalsBuffer(group).set(DateUtil.getDate(day), new Signals(clicks, impressions));
                                }
                            }
                        }
                        if (DbHelper.DEBUG) {
                            Log.v(DbHelper.TAG, "Done retrieving entities");
                        }
                        this.mEntities = entities;
                        this.mBlacklistedPackages = DbHelper.this.loadBlacklistedPackages();
                        return null;
                    } finally {
                        c.close();
                    }
                } finally {
                    c.close();
                }
            } finally {
                c.close();
            }
        }

        public void onPostExecute(Void result) {
            this.mListener.onEntitiesLoaded(this.mEntities, this.mBlacklistedPackages);
        }
    }

    public interface Listener {
        void onEntitiesLoaded(HashMap<String, Entity> hashMap, List<String> list);
    }

    private class RemoveEntityTask extends AsyncTask<Void, Void, Void> {
        boolean mFullRemoval;
        private String mKey;

        public RemoveEntityTask(String key, boolean fullRemoval) {
            this.mKey = key;
            this.mFullRemoval = fullRemoval;
        }

        protected Void doInBackground(Void... params) {
            String selection = "key=?";
            String[] selectionArgs = new String[]{this.mKey};
            SQLiteDatabase db = DbHelper.this.getWritableDatabase();
            if (this.mFullRemoval) {
                db.delete("entity", selection, selectionArgs);
            } else {
                ContentValues cv = new ContentValues();
                cv.put("key", this.mKey);
                cv.put("notif_bonus", Integer.valueOf(0));
                cv.put("bonus_timestamp", Integer.valueOf(0));
                db.update("entity", cv, selection, selectionArgs);
            }
            db.delete("entity", "key=?", selectionArgs);
            db.delete("buckets", "key=? ", selectionArgs);
            db.delete("buffer_scores", "key=?", selectionArgs);
            db.delete("rec_blacklist", "key=?", selectionArgs);
            if (DbHelper.DEBUG) {
                Log.v(DbHelper.TAG, "Done deleting " + this.mKey);
            }
            return null;
        }
    }

    private class RemoveGroupTask extends AsyncTask<Void, Void, Void> {
        private String mGroup;
        private String mKey;

        public RemoveGroupTask(String entityKey, String group) {
            this.mKey = entityKey;
            this.mGroup = group;
        }

        protected Void doInBackground(Void... params) {
            String[] selectionArgs = new String[]{this.mKey, this.mGroup};
            SQLiteDatabase db = DbHelper.this.getWritableDatabase();
            db.delete("buckets", "key=? AND group_id=? ", selectionArgs);
            String str = "buffer_scores";
            db.delete(str, "key=? AND group_id=? ", new String[]{this.mKey, this.mGroup});
            if (DbHelper.DEBUG) {
                Log.v(DbHelper.TAG, "Done deleting Key = " + this.mKey + " , Group = " + this.mGroup);
            }
            return null;
        }
    }

    private class SaveEntityTask extends AsyncTask<Void, Void, Void> {
        private final HashMap<String, List<ContentValues>> mActiveDayBuffers;
        private final HashMap<String, ContentValues> mComponents;
        private final ContentValues mEntityValues;
        private final HashMap<String, ContentValues> mGroups;
        private final String mKey;

        public SaveEntityTask(Entity entity) {
            this.mKey = entity.getKey();
            this.mEntityValues = new ContentValues();
            this.mEntityValues.put("key", this.mKey);
            this.mEntityValues.put("notif_bonus", Double.valueOf(entity.getBonus()));
            this.mEntityValues.put("bonus_timestamp", Long.valueOf(entity.getBonusTimeStamp()));
            this.mEntityValues.put("has_recs", entity.hasPostedRecommendations() ? "1" : "0");
            this.mComponents = new HashMap();
            for (String component : entity.getEntityComponents()) {
                ContentValues cv = new ContentValues();
                cv.put("key", this.mKey);
                cv.put("component", component);
                cv.put("entity_score", Long.valueOf(entity.getOrder(component)));
                cv.put("last_opened", Long.valueOf(entity.getLastOpenedTimeStamp(component)));
                this.mComponents.put(component, cv);
            }
            this.mGroups = new HashMap();
            this.mActiveDayBuffers = new HashMap();
            for (String groupId : entity.getGroupIds()) {
                ContentValues cv = new ContentValues();
                cv.put("key", this.mKey);
                cv.put("group_id", groupId);
                cv.put("last_updated", Long.valueOf(entity.getGroupTimeStamp(groupId)));
                this.mGroups.put(groupId, cv);
                ActiveDayBuffer buffer = entity.getSignalsBuffer(groupId);
                if (buffer != null) {
                    ArrayList<ContentValues> values = new ArrayList();
                    int size = buffer.size();
                    for (int i = 0; i < size; i++) {
                        Signals value = buffer.getAt(i);
                        int day = buffer.getDayAt(i);
                        if (!(value == null || day == -1)) {
                            cv = new ContentValues();
                            cv.put("_id", Integer.valueOf(i));
                            cv.put("key", this.mKey);
                            cv.put("group_id", groupId);
                            cv.put("day", Integer.valueOf(day));
                            cv.put("mClicks", Integer.valueOf(value.mClicks));
                            cv.put("mImpressions", Integer.valueOf(value.mImpressions));
                            values.add(cv);
                        }
                    }
                    this.mActiveDayBuffers.put(groupId, values);
                }
            }
        }

        protected Void doInBackground(Void... param) {
            String[] strArr;
            SQLiteDatabase db = DbHelper.this.getWritableDatabase();
            ContentValues contentValues = this.mEntityValues;
            String[] strArr2 = new String[1];
            strArr2[0] = this.mKey;
            if (db.update("entity", contentValues, "key=? ", strArr2) == 0) {
                db.insert("entity", null, this.mEntityValues);
            }
            for (Entry<String, ContentValues> pair : this.mComponents.entrySet()) {
                int count;
                String component = (String) pair.getKey();
                ContentValues cv = (ContentValues) pair.getValue();
                long timeStamp = cv.getAsLong("last_opened").longValue();
                synchronized (DbHelper.this.mLock) {
                    if (DbHelper.this.mMostRecentTimeStamp.longValue() < timeStamp) {
                        DbHelper.this.mMostRecentTimeStamp = Long.valueOf(timeStamp);
                    }
                }
                db = DbHelper.this.getWritableDatabase();
                if (component == null) {
                    strArr = new String[1];
                    strArr[0] = this.mKey;
                    count = db.update("entity_scores", cv, "key=? AND component IS NULL", strArr);
                } else {
                    try {
                        strArr = new String[1];
                        strArr[0] = this.mKey;
                        db.delete("entity_scores", "key=? AND component IS NULL", strArr);
                        strArr = new String[2];
                        strArr[0] = this.mKey;
                        strArr[1] = component;
                        count = db.update("entity_scores", cv, "key=? AND component=?", strArr);
                    } catch (Throwable th) {
                        strArr2 = new String[2];
                        strArr2[0] = this.mKey;
                        strArr2[1] = component;
                        count = db.update("entity_scores", cv, "key=? AND component=?", strArr2);
                    }
                }
                if (count == 0) {
                    db.insert("entity_scores", null, cv);
                }
            }
            for (Entry<String, ContentValues> pair2 : this.mGroups.entrySet()) {
                String groupId = (String) pair2.getKey();
                ContentValues cv = (ContentValues) pair2.getValue();
                strArr = new String[2];
                strArr[0] = this.mKey;
                strArr[1] = groupId;
                if (db.update("buckets", cv, "key=? AND group_id=? ", strArr) == 0) {
                    db.insert("buckets", null, cv);
                }
                List<ContentValues> signals = (List) this.mActiveDayBuffers.get(groupId);
                if (signals != null) {
                    for (int i = 0; i < signals.size(); i++) {
                        ContentValues signalValues = (ContentValues) signals.get(i);
                        strArr = new String[3];
                        strArr[0] = this.mKey;
                        strArr[1] = groupId;
                        strArr[2] = "" + i;
                        if (db.update("buffer_scores", signalValues, "key=? AND group_id=? AND _id=?", strArr) == 0) {
                            db.insert("buffer_scores", null, signalValues);
                        }
                    }
                }
            }
            if (DbHelper.DEBUG) {
                Log.v(DbHelper.TAG, "Done saving " + this.mKey);
            }
            return null;
        }
    }

    static {
        TAG = "DbHelper";
        DEBUG = false;
        sDbHelper = null;
    }

    public static DbHelper getInstance(Context context) {
        if (sDbHelper == null) {
            synchronized (DbHelper.class) {
                if (sDbHelper == null) {
                    sDbHelper = new DbHelper(context.getApplicationContext());
                }
            }
        }
        return sDbHelper;
    }

    public DbHelper(Context context) {
        this(context, "recommendations.db", true);
    }

    public DbHelper(Context context, String databaseName, boolean migrationEnabled) {
        super(context, databaseName, null, 2);
        this.mMostRecentTimeStamp = new Long(0);
        this.mLock = new Object();
        this.mContext = context;
        this.mMigrationEnabled = migrationEnabled;
    }

    public void onCreate(SQLiteDatabase db) {
        createAllTables(db);
        DateUtil.setInitialRankingAppliedFlag(this.mContext, tryMigrateState(db));
    }

    private boolean tryMigrateState(SQLiteDatabase db) {
        if (!this.mMigrationEnabled) {
            return false;
        }
        this.mMigrationEnabled = false;
        try {
            ContentResolver contentResolver = null;
            this.mContext.getPackageManager().getPackageInfo("com.googleandroidtv", 0);
            boolean migrated = false;
            InputStream inputStream = null;
            try {
                contentResolver = this.mContext.getContentResolver();
                try {
                    inputStream = contentResolver.openInputStream(DbMigrationContract.CONTENT_URI);
                } catch (FileNotFoundException e) {
                }
                if (inputStream != null) {
                    loadFromSavedStateInTransaction(db, inputStream);
                    migrated = true;
                }
            } catch (IOException e2) {
                Log.e(TAG, "Cannot migrate recommendation state.", e2);
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e3) {
                    }
                }
            }
            if(contentResolver != null)
                contentResolver.update(DbMigrationContract.CONTENT_UPDATE_URI, new ContentValues(), null, null);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e4) {
                }
            }
            return migrated;
        } catch (NameNotFoundException e5) {
            return false;
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case android.support.v7.recyclerview.R.styleable.RecyclerView_android_descendantFocusability /*1*/:
                setHasRecommendationsTrue(db, getPartnerOutOfBoxPackages());
                setHasRecommendationsTrue(db, getOutOfBoxPackages());
            default:
        }
    }

    String[] getOutOfBoxPackages() {
        return this.mContext.getResources().getStringArray(R.array.out_of_box_order);
    }

    String[] getPartnerOutOfBoxPackages() {
        return ServicePartner.get(this.mContext).getOutOfBoxOrder();
    }

    private void setHasRecommendationsTrue(SQLiteDatabase db, String[] packageNames) {
        if (packageNames != null && packageNames.length != 0) {
            ContentValues cv = new ContentValues();
            cv.put("has_recs", Integer.valueOf(1));
            String[] args = new String[1];
            for (String packageName : packageNames) {
                args[0] = packageName;
                db.update("entity", cv, "key=?", args);
            }
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.mMigrationEnabled = false;
        removeAllTables(db);
        onCreate(db);
    }

    void createAllTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS entity(key TEXT PRIMARY KEY, notif_bonus REAL, bonus_timestamp INTEGER, oob_order INTEGER, has_recs INTEGER)");
        db.execSQL(" CREATE TABLE IF NOT EXISTS entity_scores(key TEXT NOT NULL , component TEXT, entity_score INTEGER NOT NULL, last_opened INTEGER,  PRIMARY KEY ( key, component),  FOREIGN KEY ( key) REFERENCES entity(key))");
        db.execSQL("CREATE TABLE IF NOT EXISTS rec_blacklist(key TEXT PRIMARY KEY) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS buckets(key TEXT NOT NULL, group_id TEXT NOT NULL, last_updated INTEGER NOT NULL,  PRIMARY KEY (key, group_id))");
        db.execSQL("CREATE TABLE IF NOT EXISTS buffer_scores(_id INTEGER NOT NULL, key TEXT NOT NULL, group_id TEXT NOT NULL, day INTEGER NOT NULL, mClicks INTEGER, mImpressions INTEGER,  PRIMARY KEY (_id, group_id, key))");
    }

    void removeAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS entity");
        db.execSQL("DROP TABLE IF EXISTS entity_scores");
        db.execSQL("DROP TABLE IF EXISTS rec_blacklist");
        db.execSQL("DROP TABLE IF EXISTS buckets");
        db.execSQL("DROP TABLE IF EXISTS buffer_scores");
    }

    public void saveEntity(Entity entity) {
        if (!TextUtils.isEmpty(entity.getKey())) {
            createSaveEntityTask(entity).execute(new Void[0]);
        }
    }

    AsyncTask<Void, Void, Void> createSaveEntityTask(Entity entity) {
        return new SaveEntityTask(entity);
    }

    public void removeEntity(String key, boolean fullRemoval) {
        if (!TextUtils.isEmpty(key)) {
            new RemoveEntityTask(key, fullRemoval).execute(new Void[0]);
        }
    }

    public void removeGroupData(String key, String group) {
        if (!TextUtils.isEmpty(key)) {
            new RemoveGroupTask(key, group).execute(new Void[0]);
        }
    }

    public void getEntities(Listener listener) {
        new GetEntitiesTask(listener).execute(new Void[0]);
    }

    public List<String> loadRecommendationsPackages() {
        ArrayList<String> packageNames = new ArrayList();
        Cursor c = getReadableDatabase().query("entity", new String[]{"key"}, "key IS NOT NULL AND has_recs=1", null, null, null, "key");
        while (c.moveToNext()) {
            try {
                packageNames.add(c.getString(0));
            } finally {
                c.close();
            }
        }
        return packageNames;
    }

    public List<String> loadBlacklistedPackages() {
        ArrayList<String> packageNames = new ArrayList();
        Cursor c = getReadableDatabase().query("rec_blacklist", new String[]{"key"}, "key IS NOT NULL", null, null, null, null);
        while (c.moveToNext()) {
            try {
                packageNames.add(c.getString(0));
            } finally {
                c.close();
            }
        }
        return packageNames;
    }

    public void saveBlacklistedPackages(String[] blacklistedPackages) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM rec_blacklist");
            for (String packageName : blacklistedPackages) {
                cv.put("key", packageName);
                db.insert("rec_blacklist", null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public long getMostRecentTimeStamp() {
        long longValue;
        synchronized (this.mLock) {
            longValue = this.mMostRecentTimeStamp.longValue();
        }
        return longValue;
    }

    public void loadFromSavedState(InputStream stream) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            loadFromSavedStateInTransaction(db, stream);
            db.setTransactionSuccessful();
        } catch (IOException e) {
            Log.e(TAG, "Cannot load data from saved state", e);
        } finally {
            db.endTransaction();
        }
    }

    private void loadFromSavedStateInTransaction(SQLiteDatabase db, InputStream stream) throws IOException {
        ObjectInputStream in = new ObjectInputStream(stream);
        int version = in.readInt();
        if (version != 1) {
            throw new IOException("Unknown saved state format " + version);
        }
        ContentValues values = new ContentValues();
        while (true) {
            values.clear();
            try {
                char recordType = in.readChar();
                switch (recordType) {
                    case 'b':
                        values.put("key", in.readUTF());
                        values.put("group_id", in.readUTF());
                        values.put("last_updated", Long.valueOf(in.readLong()));
                        db.insert("buckets", null, values);
                        break;
                    case 'c':
                        values.put("key", in.readUTF());
                        String component = in.readUTF();
                        String str = "component";
                        if (TextUtils.isEmpty(component)) {
                            component = null;
                        }
                        values.put(str, component);
                        values.put("entity_score", Integer.valueOf(in.readInt()));
                        values.put("last_opened", Long.valueOf(in.readLong()));
                        db.insert("entity_scores", null, values);
                        break;
                    case 'e':
                        values.put("key", in.readUTF());
                        values.put("notif_bonus", Float.valueOf(in.readFloat()));
                        values.put("bonus_timestamp", Long.valueOf(in.readLong()));
                        values.put("has_recs", Integer.valueOf(in.readBoolean() ? 1 : 0));
                        db.insert("entity", null, values);
                        break;
                    case 'k':
                        values.put("key", in.readUTF());
                        db.insert("rec_blacklist", null, values);
                        break;
                    case 's':
                        values.put("_id", Integer.valueOf(in.readInt()));
                        values.put("key", in.readUTF());
                        values.put("group_id", in.readUTF());
                        values.put("day", Integer.valueOf(in.readInt()));
                        values.put("mClicks", Integer.valueOf(in.readInt()));
                        values.put("mImpressions", Integer.valueOf(in.readInt()));
                        db.insert("buffer_scores", null, values);
                        break;
                    default:
                        throw new IOException("Unrecognized record type: " + recordType);
                }
            } catch (EOFException e) {
                return;
            }
        }
    }

    public File getRecommendationMigrationFile() throws IOException {
        if (!this.mMigrationEnabled) {
            return null;
        }
        File file = new File(this.mContext.getFilesDir(), "migration_recs");
        DbStateWriter writer = new DbStateWriter(new FileOutputStream(file));
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("entity", new String[]{"key", "notif_bonus", "bonus_timestamp", "has_recs"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                writer.writeEntity(cursor.getString(0), cursor.getFloat(1), cursor.getLong(2), cursor.getInt(3) != 0);
            } finally {
                cursor.close();
            }
        }
        cursor = db.query("entity_scores", new String[]{"key", "component", "entity_score", "last_opened"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                writer.writeComponent(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getLong(3));
            } finally {
                cursor.close();
            }
        }
        cursor = db.query("buckets", new String[]{"key", "group_id", "last_updated"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                writer.writeBucket(cursor.getString(0), cursor.getString(1), cursor.getLong(2));
            } finally {
                cursor.close();
            }
        }
        cursor = db.query("buffer_scores", new String[]{"_id", "key", "group_id", "day", "mClicks", "mImpressions"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                writer.writeSignals(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5));
            } finally {
                cursor.close();
            }
        }
        cursor = db.query("rec_blacklist", new String[]{"key"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            try {
                writer.writeBlacklistedPackage(cursor.getString(0));
            } finally {
                cursor.close();
            }
        }
        writer.close();
        return file;
    }
}
