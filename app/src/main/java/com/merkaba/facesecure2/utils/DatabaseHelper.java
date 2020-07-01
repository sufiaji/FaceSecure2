package com.merkaba.facesecure2.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.merkaba.facesecure2.activity.MainActivity;
import com.merkaba.facesecure2.model.Attendance;
import com.merkaba.facesecure2.model.Encoding;
import com.merkaba.facesecure2.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Although Google recommends to use Room Persistance Library
 * it is not necessary we need to follow their guides
 * Afterall, it depend on the needs
 *
 * Here, we dont need all mezmerize features of Room Persistance
 * And also Room Persistance creates a lots boilerplate codes too
 */

/**
 * Created by Pradhono Rakhmono Aji @merkaba.co.id
 * 29/05/2020
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "facesecure";

    private static final String TABLE_USER = "EUSER";
    private static final String TABLE_ENCODING = "ENCODING";
    private static final String TABLE_ATTENDANCE = "ATTENDANCE";

    private static final String COL_ID = "id";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_NAME = "name";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_CREATED_LOCAL = "created_local";
    private static final String COL_ENCODING_ARRAY = "encoding_array";
    private static final String COL_STATUS = "status";
    private static final String COL_LOCATION = "location";
    private static final String COL_IMAGE = "image";
    private static final String COL_UNSENT = "unsent";
    private static final String STRING_TRUE = "X";
    private static final String STRING_FALSE = " ";

    private String CREATE_USER_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " ( "
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_USER_ID + " TEXT UNIQUE, "
            + COL_CREATED_AT + " INTEGER, "
            + COL_CREATED_LOCAL + " TEXT, "
            + COL_NAME + " TEXT )";

    private String CREATE_ATTENDANCE_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_ATTENDANCE + " ( "
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_USER_ID + " TEXT, "
            + COL_CREATED_AT + " INTEGER, "
            + COL_STATUS + " TEXT, "
            + COL_LOCATION + " TEXT, "
            + COL_IMAGE + " BLOB, "
            + COL_UNSENT + " TEXT )";

    private String CREATE_ENCODING_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_ENCODING + " ( "
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_USER_ID + " TEXT UNIQUE, "
            + COL_ENCODING_ARRAY + " TEXT )";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE_STATEMENT);
        db.execSQL(CREATE_ENCODING_TABLE_STATEMENT);
        db.execSQL(CREATE_ATTENDANCE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENCODING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);

        // create new tables
        onCreate(db);
    }

    public boolean userExists(String userId) {
        String select_statement = "SELECT * FROM " + TABLE_USER + " WHERE " + COL_USER_ID + " = '" + userId + "'";
        Log.d(MainActivity.class.getSimpleName(), select_statement);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_statement, null);
        if(cursor==null) return false;
        if(cursor.getCount()<=0) return false;
        return true;
    }

    public boolean encodingExists(String userId) {
        String select_statement = "SELECT * FROM " + TABLE_ENCODING + " WHERE " + COL_USER_ID + " = '" + userId + "'";
        Log.d(MainActivity.class.getSimpleName(), select_statement);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_statement, null);
        if(cursor==null) return false;
        if(cursor.getCount()<=0) return false;
        return true;
    }

    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, user.getUserId());
        values.put(COL_NAME, user.getName());
        values.put(COL_CREATED_AT, user.getCreatedAt());
        values.put(COL_CREATED_LOCAL, user.getIsLocal());
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return id;
    }

    public String getUserName(String userId) {
        String name = "";
        String select_statement = "SELECT " + COL_NAME + " FROM " + TABLE_USER + " WHERE " + COL_USER_ID + " = '" + userId + "'";
        Log.d(MainActivity.class.getSimpleName(), select_statement);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_statement, null);
        if(cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            name = cursor.getString(cursor.getColumnIndex(COL_NAME));
        }
//        cursor.close(); // add Aji
        return name;
    }

    public long insertEncoding(Encoding encoding) {
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, encoding.getUserId());
        values.put(COL_ENCODING_ARRAY, encoding.getEncodingArray());
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insertWithOnConflict(TABLE_ENCODING, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close(); // add Aji
        return id;
    }

    public List<Encoding> getAllEncodings() {
        ArrayList<Encoding> encodings = new ArrayList<>();
        String select_statement = "SELECT * FROM " + TABLE_ENCODING;
        Log.d(MainActivity.class.getSimpleName(), select_statement);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_statement, null);
        if(cursor!=null && cursor.moveToFirst()) {
            do {
                String userId = cursor.getString(cursor.getColumnIndex(COL_USER_ID));
                String encodingArray = cursor.getString(cursor.getColumnIndex(COL_ENCODING_ARRAY));
                Encoding encoding = new Encoding(userId, encodingArray);
                encodings.add(encoding);
            } while(cursor.moveToNext());
        }
//        cursor.close(); // add Aji
        return encodings;
    }

    public Attendance getLastAttendance(String userId) {
        Attendance attendance = null;
        String select_statement = "SELECT * FROM " + TABLE_ATTENDANCE
                + " WHERE " + COL_USER_ID + " = '" + userId
                + "' ORDER BY " + COL_CREATED_AT + " DESC LIMIT 1";
        Log.d(MainActivity.class.getSimpleName(), select_statement);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_statement, null);
        if(cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
            String idUser = userId;
            long createdAt = cursor.getLong(cursor.getColumnIndex(COL_CREATED_AT));
            String status = cursor.getString(cursor.getColumnIndex(COL_STATUS));
            String location = cursor.getString(cursor.getColumnIndex(COL_LOCATION));
            String unSent = cursor.getString(cursor.getColumnIndex(COL_UNSENT));
            attendance = new Attendance(id, idUser, createdAt, status, location, unSent);
        }
        return attendance;
    }

    public List<Attendance> getAttendanceBetweenDate(long from , long to) {
        ArrayList<Attendance> attendances = new ArrayList<>();
        String select_statement = "SELECT * FROM " + TABLE_ATTENDANCE
                + " WHERE " + COL_CREATED_AT + " BETWEEN " + from + " AND " + to
                + " ORDER BY " + COL_CREATED_AT;
        Log.d(MainActivity.class.getSimpleName(), select_statement);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_statement, null);
        if(cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
                String idUser = cursor.getString(cursor.getColumnIndex(COL_USER_ID));
                long createdAt = cursor.getLong(cursor.getColumnIndex(COL_CREATED_AT));
                String status = cursor.getString(cursor.getColumnIndex(COL_STATUS));
                String location = cursor.getString(cursor.getColumnIndex(COL_LOCATION));
                String unSent = cursor.getString(cursor.getColumnIndex(COL_UNSENT));
                Attendance attendance = new Attendance(id, idUser, createdAt, status, location, unSent);
                attendances.add(attendance);
            } while(cursor.moveToNext());
        }
        return attendances;
    }

    public String populateAttendance(String fromDate, String toDate) {
        DateUtils dateUtils = new DateUtils("/");
        String fromDatetime = fromDate + " 00:00:01.000000";
        String toDatetime = toDate + " 23:59:59.000000";
        long epochFrom = dateUtils.stringToEpoch(fromDatetime);
        long epochTo = dateUtils.stringToEpoch(toDatetime);
        List<Attendance> attendances = getAttendanceBetweenDate(epochFrom, epochTo);
        String lines = "";
        if(attendances.size()==0) {
            return lines;
        }
        for(Attendance at : attendances) {
            String datetime = at.getCreatedDateTimeFormatted();
            String date = datetime.split(" ")[0];
            String[] datesplit = date.split("-"); // regex comes from Attendance object is "-"
            String date1 = datesplit[2] + "/" + datesplit[1] + "/" + datesplit[0];
            String time1 = datetime.split(" ")[1];
            String[] time1s = time1.split(Pattern.quote("."));
            String time = time1;
            if(time1s.length > 0)
                time = time1s[0];
            String userId = at.getUserId();
            String terminalId = at.getLocation();
            String status = "1";
            if(at.getStatus().equalsIgnoreCase(MainActivity.STRING_CLOCK_OUT)) {
                status = "2";
            }
            lines = lines + "[" + date1 + "-" + time + "]" + userId + "/" + terminalId + "/128/0/" + status + MainActivity.CARRIAGE_RETURN;
        }
        return lines;
    }

    public List<Attendance> getUnsentAttendance() {
        ArrayList<Attendance> attendances = new ArrayList<>();
        String select_statement = "SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COL_UNSENT + " = '" + STRING_TRUE + "'";
        Log.d(MainActivity.class.getSimpleName(), select_statement);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_statement, null);
        if(cursor!=null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
                String userId = cursor.getString(cursor.getColumnIndex(COL_USER_ID));
                String location = cursor.getString(cursor.getColumnIndex(COL_LOCATION));
                String status = cursor.getString(cursor.getColumnIndex(COL_STATUS));
                String unSent = cursor.getString(cursor.getColumnIndex(COL_UNSENT));
                long createdAt = cursor.getLong(cursor.getColumnIndex(COL_CREATED_AT));
                byte[] imageByte = cursor.getBlob(cursor.getColumnIndex(COL_IMAGE));
                Attendance attendance = new Attendance(id, userId, createdAt, status, location, imageByte, unSent);
                attendances.add(attendance);
            } while(cursor.moveToNext());
        }
        return attendances;
    }

    public List<User> getLocalUser() {
        ArrayList<User> users = new ArrayList<>();
        String select_statement = "SELECT * FROM " + TABLE_USER + " WHERE " + COL_CREATED_LOCAL + " = '" + STRING_TRUE + "'";
        Log.d(MainActivity.class.getSimpleName(), select_statement);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select_statement, null);
        if(cursor!=null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
                String userId = cursor.getString(cursor.getColumnIndex(COL_USER_ID));
                int createdAt = cursor.getInt(cursor.getColumnIndex(COL_CREATED_AT));
                String isLocal = cursor.getString(cursor.getColumnIndex(COL_CREATED_LOCAL));
                String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
                User user = new User(userId, name, createdAt, isLocal);
//                Attendance attendance = new Attendance(id, userId, createdAt, status, location, unSent);
                users.add(user);
            } while(cursor.moveToNext());
        }
        return users;
    }

    public int updateSentAttendances(List<Attendance> attendances) {
//        db.update(TABLE_JOB_DETAIL, data, COL_ID + " IN (?,?,?)", new String[]{"1","2","3"});
        try {
            ArrayList<String> listId = new ArrayList<>();
            String idQst = " IN (" ;
            int len = attendances.size();
            int i=0;
            for(Attendance at : attendances) {
                // the ID
                listId.add(Integer.toString(at.getId()));
                // the Qst mark
                i = i+1;
                idQst = idQst + "?";
                if(i==len) {
                    idQst = idQst + ")";
                } else {
                    idQst = idQst + ",";
                }
            }
            String[] arrayId = Utils.toStringArray(listId);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_UNSENT, STRING_FALSE);
            int id = db.update(TABLE_ATTENDANCE, values, COL_ID + idQst, arrayId);
            db.close();
            return id;
        } catch (Exception ex) {
            return -99;
        }
    }

    public long insertAttendance(Attendance attendance) {
        // combine date and time, then convert to epoch
        String datetime = attendance.getCreatedAt() + " " + attendance.getCreatedOn();
        long epoch = new DateUtils("-").stringToEpoch(datetime);
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, attendance.getUserId());
        values.put(COL_CREATED_AT, epoch);
        values.put(COL_STATUS, attendance.getStatus());
        values.put(COL_LOCATION, attendance.getLocation());
        values.put(COL_UNSENT, attendance.getUnSent());
        if(attendance.getBlobImage()!=null && attendance.getBlobImage().length > 0)
            values.put(COL_IMAGE, attendance.getBlobImage());
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(TABLE_ATTENDANCE, null, values);
        db.close();
        return id;
    }

    public void deleteLocalUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<User> users = getLocalUser();
        for(User user : users) {
            db.delete(TABLE_USER, COL_USER_ID + " = '" + user.getUserId() + "'", null);
            db.delete(TABLE_ENCODING, COL_USER_ID + " = '" + user.getUserId() + "'", null);
            db.delete(TABLE_ATTENDANCE, COL_USER_ID + " = '" + user.getUserId() + "'", null);
        }
        db.close();
    }

    public int setUserLocalFlag(String userId, String isLocal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CREATED_LOCAL, isLocal);
        int id = db.update(TABLE_USER, values, COL_USER_ID + " = '" + userId + "'", null);
        db.close();
        return id;
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
