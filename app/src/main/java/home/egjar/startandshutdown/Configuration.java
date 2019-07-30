package home.egjar.startandshutdown;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;

import static home.egjar.startandshutdown.DBContract.*;

class Configuration {

    private int id;
    private String ipAddress;
    private String macAddress;
    private String username;
    private String password;
    private boolean header_mode = false;
    private String domain;

    Configuration() {

    }

    //Getters and setters

    String getIp() {
        return ipAddress;
    }

    String getDomain() {
        return domain;
    }

    String getMAC() {
        return macAddress;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    boolean ip_header_mode() {
        return header_mode;
    }

    //Functions and methods

    void switchHeader_mode() {
        header_mode = !header_mode;
    }

    void saveToDB(Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_IPADDRESS, ipAddress);
        contentValues.put(COLUMN_MACADDRESS, macAddress);
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_DOMAIN, domain);
        try {
            Cursor cursor = loadCursorFromDB(context);
            if (cursor.moveToFirst()) {
                String selection = COLUMN_ID + " LIKE ?";
                String[] selectionArgs = {Integer.toString(id)};
                widget.getDB(context).update(SETTINGS_TABLE, contentValues, selection, selectionArgs);
                Toast.makeText(context, R.string.settings_updated, Toast.LENGTH_SHORT).show();
            } else {
                widget.getDB(context).insertOrThrow(SETTINGS_TABLE, null, contentValues);
                Toast.makeText(context, R.string.settings_saved, Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } catch (SQLException e) {
            Toast.makeText(context, "Error" + e.getCause() + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void readFromCursor(@NotNull Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        this.ipAddress = cursor.getString(cursor.getColumnIndex(COLUMN_IPADDRESS));
        this.macAddress = cursor.getString(cursor.getColumnIndex(COLUMN_MACADDRESS));
        this.username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
        this.password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
        this.domain = cursor.getString(cursor.getColumnIndex(COLUMN_DOMAIN));
    }

    boolean isEntryExist(Context context, int id){
        boolean result=false;
        this.id = id;
        Cursor cursor = loadCursorFromDB(context);
        result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    private Cursor loadCursorFromDB(Context context) {
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        String sortOrder = COLUMN_ID + " DESC";
        return widget.getDB(context).query(SETTINGS_TABLE,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    void loadFromDB(Context context, int id) {
//        Cursor cursor = db.rawQuery("SELECT * FROM " + SETTINGS_TABLE + " WHERE " +
//                COLUMN_ID + " = '" + id + "'", null);
        this.id = id;
        Cursor cursor = loadCursorFromDB(context);
        readFromCursor(cursor);
        cursor.close();
    }

    PSRemoting initPSRemoting() {
        return new PSRemoting(ipAddress, domain, username, password);
    }

    static class Builder {
        private Configuration newConfiguration;

        Builder() {
            newConfiguration = new Configuration();
        }

        Builder withID(int id) {
            newConfiguration.id = id;
            return this;
        }

        Builder withIP(String ipAddress) {
            newConfiguration.ipAddress = ipAddress;
            return this;
        }

        Builder withMAC(String macAddress) {
            newConfiguration.macAddress = macAddress;
            return this;
        }

        Builder withUsername(String username) {
            newConfiguration.username = username;
            return this;
        }

        Builder withPassword(String password) {
            newConfiguration.password = password;
            return this;
        }

        Builder withDomain(String domain) {
            newConfiguration.domain = domain;
            return this;
        }

        Configuration build() {
            return newConfiguration;
        }
    }
}
