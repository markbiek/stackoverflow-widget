package com.janustech.stackoverflowwidget;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class StackWidgetProvider extends AppWidgetProvider {

	//private static final String ACTION_CLICK = "ACTION_CLICK";
	
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	    Log.d("Custom", "Enabled");
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
	    super.onReceive(context, intent);
	    Log.d("Custom", "Receive");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		JSONObject userData = this.getUserData(305);
		String widgetText = "";

		Log.d("MyWidgetProvider.onUpdate", "");
		
		ComponentName thisWidget = new ComponentName(context,
				StackWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);

			
			widgetText = getUserString(userData, "display_name") + 
						 "\t" + getUserString(userData, "reputation");
			
			remoteViews.setTextViewText(R.id.update, widgetText);

			// Register an onClickListener
			Intent intent = new Intent(context, StackWidgetProvider.class);

			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
	
	private JSONObject getUserData(int id) {
		try {
			JSONObject json = RestJsonClient.connect("http://api.stackexchange.com/2.0/users/" + id + "?site=stackoverflow");
			JSONArray items = json.getJSONArray("items");
			
			if(items.length() > 0) {
				return items.getJSONObject(0);
			}
		}catch(Exception e) {
			Log.e("getUserData", e.getMessage());
		}
		
		return null;
	}
	
	private String getUserString(JSONObject user, String field) {
		try {
			return user.getString(field);
		}catch(JSONException e) {
			Log.e("getUserStrVal", e.getMessage());
		}
		return "";
	}
}
		
