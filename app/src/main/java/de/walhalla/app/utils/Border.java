package de.walhalla.app.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import org.jetbrains.annotations.NotNull;

import de.walhalla.app.App;
import de.walhalla.app.R;

public class Border {
    @NotNull
    public static LayerDrawable getBlack(int bgColor, int Left, int Top, int Right, int Bottom) {
        // Initialize new color drawables R.color.black
        ColorDrawable borderColorDrawable = new ColorDrawable(App.getContext().getResources().getColor(R.color.black, null));
        ColorDrawable backgroundColorDrawable = new ColorDrawable(bgColor);
        backgroundColorDrawable.setAlpha(0);

        // Initialize a new array of drawable objects
        Drawable[] drawables = new Drawable[]{
                borderColorDrawable,
                backgroundColorDrawable
        };

        // Initialize a new layer drawable instance from drawables array
        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        // Set padding for background color layer
        layerDrawable.setLayerInset(
                1, // Index of the drawable to adjust [background color layer]
                Left, // Number of pixels to add to the left bound [left border]
                Top, // Number of pixels to add to the top bound [top border]
                Right, // Number of pixels to add to the right bound [right border]
                Bottom // Number of pixels to add to the bottom bound [bottom border]
        );

        // Finally, return the one or more sided bordered background drawable
        return layerDrawable;
    }
    @NotNull
    public static LayerDrawable getBlackTransparent(int Left, int Top, int Right, int Bottom) {
        // Initialize new color drawables R.color.black
        ColorDrawable borderColorDrawable = new ColorDrawable(App.getContext().getResources().getColor(R.color.black, null));
        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        //ColorDrawable backgroundColorDrawable = new ColorDrawable(transparentDrawable);

        // Initialize a new array of drawable objects
        Drawable[] drawables = new Drawable[]{
                borderColorDrawable,
                transparentDrawable
        };

        // Initialize a new layer drawable instance from drawables array
        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        // Set padding for background color layer
        layerDrawable.setLayerInset(
                1, // Index of the drawable to adjust [background color layer]
                Left, // Number of pixels to add to the left bound [left border]
                Top, // Number of pixels to add to the top bound [top border]
                Right, // Number of pixels to add to the right bound [right border]
                Bottom // Number of pixels to add to the bottom bound [bottom border]
        );

        // Finally, return the one or more sided bordered background drawable
        return layerDrawable;
    }
}
