package home.egjar.startandshutdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

class MyPendingIntent extends Intent {
    private Context context;
    private int widgetId;
    private String action;

    MyPendingIntent(Context context, Class<?> cls, int widgetId, String action) {
        super(context, cls);
        this.context = context;
        this.widgetId = widgetId;
        this.action = action;
        this.setAction(action);
        this.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
    }

    PendingIntent get() {
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)) {
            return PendingIntent.getActivity(context, widgetId,
                    this, 0);
        } else {
            return PendingIntent.getBroadcast(context, widgetId,
                    this, 0);
        }
    }
}
