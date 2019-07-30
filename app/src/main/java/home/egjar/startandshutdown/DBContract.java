package home.egjar.startandshutdown;

import android.provider.BaseColumns;

class DBContract implements BaseColumns {
    static final String COLUMN_MACADDRESS = "macaddress";
    static final String SETTINGS_TABLE = "settings";
    static final String COLUMN_ID = "id";
    static final String COLUMN_IPADDRESS = "ipaddress";
    static final String COLUMN_USERNAME = "username";
    static final String COLUMN_PASSWORD = "password";
    static final String COLUMN_DOMAIN = "domain";
    static final String DATABASE_NAME = "myDb.db";
    static final Integer DATABASE_VERSION = 1;
    static final String CREATE_TABLE = "create table "
            + SETTINGS_TABLE + " ("
            + _ID + " integer primary key autoincrement,"
            + COLUMN_ID + " integer unique,"
            + COLUMN_IPADDRESS + " text,"
            + COLUMN_MACADDRESS + " text,"
            + COLUMN_USERNAME + " text,"
            + COLUMN_PASSWORD + " text,"
            + COLUMN_DOMAIN + " text"
            + ");";
    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SETTINGS_TABLE;
}
