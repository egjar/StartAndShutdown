package home.egjar.startandshutdown;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static home.egjar.startandshutdown.DBContract.*;

class DBHelper extends SQLiteOpenHelper {
    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    void deleteDBEntry(SQLiteDatabase db, int id) {
        String[] strIds = {Integer.toString(id)};
        int deleteRows = db.delete(SETTINGS_TABLE, COLUMN_ID + " LIKE ?", strIds);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
