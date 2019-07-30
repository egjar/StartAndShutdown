package home.egjar.startandshutdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.util.Objects;

import static home.egjar.startandshutdown.DBContract.DATABASE_NAME;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link widgetConfigureActivity widgetConfigureActivity}
 */
public class widget extends AppWidgetProvider {
    final static String START_COMPUTER = "start_computer";
    final static String STOP_COMPUTER = "stop_computer";
    final static String SUSPEND_COMPUTER = "suspend_computer";
    final static String SWITCH_HEADER = "switch_header";
    static SparseArray<Configuration> activeConfigurations = new SparseArray<>();
    private static DBHelper dbHelper;
    private static SQLiteDatabase db;

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {
        Configuration activeConfiguration = new Configuration();
        if(activeConfiguration.isEntryExist(context,appWidgetId)) {
            activeConfiguration.loadFromDB(context, appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId, activeConfiguration);
        }
        else {
            Toast.makeText(context,R.string.EntryNotFound,Toast.LENGTH_LONG).show();
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Configuration activeConfiguration) {
        activeConfigurations.put(appWidgetId, activeConfiguration);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        // Construct the RemoteViews object
        MyPendingIntent start = new MyPendingIntent(context, widget.class, appWidgetId, START_COMPUTER);
        MyPendingIntent stop = new MyPendingIntent(context, widget.class, appWidgetId, STOP_COMPUTER);
        MyPendingIntent suspend = new MyPendingIntent(context, widget.class, appWidgetId, SUSPEND_COMPUTER);
        MyPendingIntent config = new MyPendingIntent(context, widgetConfigureActivity.class,
                appWidgetId, AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        MyPendingIntent switcher = new MyPendingIntent(context, widget.class, appWidgetId, SWITCH_HEADER);
        views.setOnClickPendingIntent(R.id.btnStart, start.get());
        views.setOnClickPendingIntent(R.id.btnHibernate, suspend.get());
        views.setOnClickPendingIntent(R.id.btnShutdown, stop.get());
        views.setOnClickPendingIntent(R.id.btnSettings, config.get());
        views.setOnClickPendingIntent(R.id.txtHeader, switcher.get());

        if (activeConfiguration.ip_header_mode()) {
            views.setTextViewText(R.id.txtHeader, activeConfiguration.getIp());
        } else {
            views.setTextViewText(R.id.txtHeader, activeConfiguration.getDomain());
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.i("myLog", "Widget [" + appWidgetId + "] updated");
    }

    private static PendingIntent defaultPendingIntent(Context context, int viewId, int appWidgetId, String action) {
        Intent intent = new Intent(context, widget.class);
        intent.setAction(action);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getActivity(context, appWidgetId, intent, 0);
    }

    private static void openDB(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        if (db == null) {
            db = dbHelper.getWritableDatabase();
        }
    }

    private static boolean checkDBExist(Context context) {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    static SQLiteDatabase getDB(Context context) {
        if (db == null) openDB(context);
        return db;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.i("myLog", "onUpdate started");
        boolean exist = checkDBExist(context); //Check state before DB creation
        openDB(context);
        if (exist) {
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
        Log.i("myLog", "onUpdate finished");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        openDB(context);
        for (int appWidgetId : appWidgetIds) {
            activeConfigurations.remove(appWidgetId);
            dbHelper.deleteDBEntry(db, appWidgetId);
            Log.i("myLog", "Widget [" + appWidgetId + "] deleted");
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.i("myLog", "Widget Enabled");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        activeConfigurations.clear();
        context.deleteDatabase(DATABASE_NAME);
        Log.i("myLog", "Widget Disabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (!Objects.equals(intent.getAction(), AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)) {
            int activeConfigurationId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (activeConfigurationId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                Configuration activeConfiguration = activeConfigurations.get(activeConfigurationId);
                if (activeConfiguration != null) {
                    if (Objects.equals(intent.getAction(), START_COMPUTER)) {
                        //TODO Разработать механизм WOL
                        Toast.makeText(context, "Start pressed:" + activeConfiguration.getId(),
                                Toast.LENGTH_SHORT).show();
                    } else if (Objects.equals(intent.getAction(), SUSPEND_COMPUTER)) {
                        Toast.makeText(context, "Suspend pressed:" + activeConfiguration.getId(),
                                Toast.LENGTH_SHORT).show();
//                    activeConfiguration.initPSRemoting().remoteSuspend();
                    } else if (Objects.equals(intent.getAction(), STOP_COMPUTER)) {
                        Toast.makeText(context, "Stop pressed:" + activeConfiguration.getId(),
                                Toast.LENGTH_SHORT).show();
                        activeConfiguration.initPSRemoting().remoteShutdown();
                    } else if (Objects.equals(intent.getAction(), SWITCH_HEADER)) {
                        activeConfiguration.switchHeader_mode();
                        updateAppWidget(context, AppWidgetManager.getInstance(context),
                                activeConfiguration.getId(), activeConfiguration);
                    }
                }
            }
        }
    }
}