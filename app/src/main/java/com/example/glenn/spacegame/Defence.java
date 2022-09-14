package com.example.glenn.spacegame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class Defence {
    private RectF rect;

    private boolean isVisible;

    public Defence(int row, int column, int wallNumber, int screenX, int screenY){

        int width = screenX / 70;
        int height = screenY / 40;

        isVisible = true;

        // Sometimes a bullet slips through this padding.
        // Set padding to zero if this annoys you
        int squarePadding = 1;

        // The number of shelters
        int wallPadding = screenX / 6;
        int startHeight = screenY - (screenY /2);

        rect = new RectF(column * width + squarePadding +
                (wallPadding * wallNumber) +
                wallPadding + wallPadding * wallNumber,
                row * height + squarePadding + startHeight,
                column * width + width - squarePadding +
                        (wallPadding * wallNumber) +
                        wallPadding + wallPadding * wallNumber,
                row * height + height - squarePadding + startHeight);
    }
    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }
}

