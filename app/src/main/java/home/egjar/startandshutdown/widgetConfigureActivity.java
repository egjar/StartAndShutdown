package home.egjar.startandshutdown;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * The configuration screen for the {@link widget widget} AppWidget.
 */
public class widgetConfigureActivity extends Activity {
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = widgetConfigureActivity.this;
            // When the button is clicked, store the string locally
            Configuration currentConfiguration = new Configuration.Builder()
                    .withID(mAppWidgetId)
                    .withIP(((EditText) findViewById(R.id.txtIP)).getText().toString())
                    .withMAC(((EditText) findViewById(R.id.txtMAC)).getText().toString())
                    .withUsername(((EditText) findViewById(R.id.txtUsername)).getText().toString())
                    .withPassword(((EditText) findViewById(R.id.txtPassword)).getText().toString())
                    .withDomain(((EditText) findViewById(R.id.txtDomain)).getText().toString())
                    .build();

            currentConfiguration.saveToDB(getApplicationContext());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            widget.updateAppWidget(context, appWidgetManager, mAppWidgetId, currentConfiguration);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            Log.e("myLogs", "Widget [" + mAppWidgetId + "] configured");
            finish();
        }
    };

    private void fillFields(Configuration activeConfiguration) {
        ((EditText) findViewById(R.id.txtIP)).setText(activeConfiguration.getIp());
        ((EditText) findViewById(R.id.txtMAC)).setText(activeConfiguration.getMAC());
        ((EditText) findViewById(R.id.txtUsername)).setText(activeConfiguration.getUsername());
        ((EditText) findViewById(R.id.txtPassword)).setText(activeConfiguration.getPassword());
        ((EditText) findViewById(R.id.txtDomain)).setText(activeConfiguration.getDomain());
    }

    public widgetConfigureActivity() {
        super();
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);


        setContentView(R.layout.widget_configure);
        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        Configuration activeConfiguration = widget.activeConfigurations.get(mAppWidgetId);
        if (activeConfiguration != null) {
            fillFields(activeConfiguration);
        } else {
            activeConfiguration = new Configuration();
            if(activeConfiguration.isEntryExist(getApplicationContext(),mAppWidgetId)){
                activeConfiguration.loadFromDB(getApplicationContext(),mAppWidgetId);
                fillFields(activeConfiguration);
            }
        }
        findViewById(R.id.configSaveButton).setOnClickListener(mOnClickListener);
        Log.i("myLogs", "Configuration activity ready. Widget ID=" + mAppWidgetId);
    }
}

