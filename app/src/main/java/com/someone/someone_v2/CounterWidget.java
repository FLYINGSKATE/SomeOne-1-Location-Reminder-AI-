package com.someone.someone_v2;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.w3c.dom.Text;

import io.paperdb.Paper;



/**
 * Implementation of App Widget functionality.
 */
public class CounterWidget extends AppWidgetProvider {



    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        //Init Paper
        Paper.init(context);

        String title=Paper.book().read("title");
        String description=Paper.book().read("description");
        String remDays=Paper.book().read("remDays");


        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.counter_widget);
        //views.setTextViewText(R.id.appwidget_title, title);
        views.setTextViewText(R.id.appwidget_remDays,remDays);
        views.setImageViewBitmap(R.id.imageView, buildUpdate(title,context));


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static Bitmap buildUpdate(String time,Context context)
    {
        int textSizePixels=40;
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(context.getAssets(),"AmalfiCoast.ttf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTextSize(textSizePixels);
        paint.setTextAlign(Paint.Align.LEFT);
        Bitmap myBitmap = Bitmap.createBitmap(((int) paint.measureText(time)), (int) textSizePixels+200, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        myCanvas.drawText(time,  0, myBitmap.getHeight()-30, paint);
        return myBitmap;
    }
}

