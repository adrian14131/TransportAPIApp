package pl.ozog.transportapiapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import pl.ozog.transportapiapp.model.stops.StopPoint;

/**
 * TODO: document your custom view class.
 */
public class StopPointElementView extends View {

    StopPoint stopPoint;
    public StopPointElementView(Context context) {
        super(context);
    }

    public StopPointElementView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StopPointElementView(Context context, StopPoint stopPoint) {
        super(context);
        this.stopPoint = stopPoint;
    }
}