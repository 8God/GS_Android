package com.gaoshou.common.network;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gaoshou.common.base.GsApplication;
import com.gaoshou.common.utils.FileUtil;

public class ConnectorDatabank {

    private SQLiteDatabase sqliteDB;

    private static final String DB_CACHE_DIR_NAME = "database";

    private static final String DB_NAME = GsApplication.class.getPackage() + ".db";

    private static final int DB_VERSION = 1;

    private static final String DB_TABLE_NAME = "offline_data";

    private static final String DB_COL_ID_NAME = "id";

    private static final String DB_COL_REQUEST_NAME = "request";

    private static final String DB_COL_RESPONSE_NAME = "response";

    private void createTable() {
        StringBuffer sqlStrBuf = new StringBuffer();

        sqlStrBuf.append("CREATE TABLE IF NOT EXISTS ");
        sqlStrBuf.append(DB_TABLE_NAME);
        sqlStrBuf.append(" (");
        sqlStrBuf.append(DB_COL_ID_NAME);
        sqlStrBuf.append(" INTEGER PRIMARY KEY, ");
        sqlStrBuf.append(DB_COL_REQUEST_NAME);
        sqlStrBuf.append(" TEXT, ");
        sqlStrBuf.append(DB_COL_RESPONSE_NAME);
        sqlStrBuf.append(" TEXT)");
        sqliteDB.execSQL(sqlStrBuf.toString());
    }

    public ConnectorDatabank(Context notApplicationContext) {

        File databaseDir = FileUtil.getCacheDir(notApplicationContext, DB_CACHE_DIR_NAME);
        File databaseFile = new File(databaseDir, DB_NAME);

        if (null != databaseFile) {
            if (databaseFile.exists()) {
                sqliteDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
                if (null != sqliteDB && sqliteDB.getVersion() != DB_VERSION) {
                    sqliteDB.close();
                    if (null != databaseDir && databaseDir.exists()) {
                        File[] subFileArray = databaseDir.listFiles();
                        if (null != subFileArray) {
                            for (int i = 0; i < subFileArray.length; i++) {
                                subFileArray[i].delete();
                            }
                        }

                    }

                    sqliteDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
                    if (null != sqliteDB) {
                        sqliteDB.setVersion(DB_VERSION);
                    }
                }

            } else {
                sqliteDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
                if (null != sqliteDB) {
                    sqliteDB.setVersion(DB_VERSION);
                }
            }
        } // if (null != databaseFile)

        if (null != sqliteDB) {
            createTable();
        }
    }

    public String getData(String request) {

        Log.i("JNSTesting", "get request : " + request);
        String response = "";
        if (null != sqliteDB) {
            String selection = DB_COL_REQUEST_NAME + "=?";
            Cursor cursor = sqliteDB.query(DB_TABLE_NAME, new String[] { DB_COL_RESPONSE_NAME }, selection, new String[] { request }, null, null, null);

            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    response = cursor.getString(0);
                }
            }
        }

        return response;
    }

    public void saveData(String request, String response) {

        if (isExistent(request)) {
            updateData(request, response);
        } else {
            insertData(request, response);
        }

    }

    public void closeDatabank() {
        if (null != sqliteDB) {
            sqliteDB.close();
        }
    }

    private boolean isExistent(String request) {

        boolean isExistent = true;
        if (null != sqliteDB) {
            String selection = DB_COL_REQUEST_NAME + "=?";
            Cursor cursor = sqliteDB.query(DB_TABLE_NAME, new String[] { DB_COL_REQUEST_NAME }, selection, new String[] { request }, null, null, null);
            if (null == cursor || cursor.getCount() < 1) {
                isExistent = false;
            }
        }

        return isExistent;
    }

    private void updateData(String request, String response) {

        if (null != sqliteDB) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DB_COL_REQUEST_NAME, request);
            contentValues.put(DB_COL_RESPONSE_NAME, response);
            String whereClause = DB_COL_REQUEST_NAME + "=?";

            sqliteDB.update(DB_TABLE_NAME, contentValues, whereClause, new String[] { request });
        }

    }

    private void insertData(String request, String response) {

        Log.i("JNSTesting", "insert request : " + request);
        Log.i("JNSTesting", "insert response : " + response);
        if (null != sqliteDB) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DB_COL_REQUEST_NAME, request);
            contentValues.put(DB_COL_RESPONSE_NAME, response);

            sqliteDB.insert(DB_TABLE_NAME, null, contentValues);
        }

    }
    
    public void deleteData() {
//        File databaseDir = FileUtil.getCacheDir(notApplicationContext, DB_CACHE_DIR_NAME);
//        File databaseFile = new File(databaseDir, DB_NAME);
        sqliteDB.delete(DB_TABLE_NAME, null, null);
    }
}
