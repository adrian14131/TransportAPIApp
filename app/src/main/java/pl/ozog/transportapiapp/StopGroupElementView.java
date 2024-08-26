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

import androidx.annotation.Nullable;

import pl.ozog.transportapiapp.model.stops.StopGroup;

/**
 * TODO: document your custom view class.
 */
public class StopGroupElementView extends View {

    StopGroup stopGroup;

    public StopGroupElementView(Context context) {
        super(context);
    }

    public StopGroupElementView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StopGroupElementView(Context context, StopGroup stopGroup) {
        super(context);
        this.stopGroup = stopGroup;
    }
}