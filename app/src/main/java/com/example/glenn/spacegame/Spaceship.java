package com.example.glenn.spacegame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Log;

public class Spaceship {

    RectF rect;
    public Bitmap bitmapup;
    private float height;
    private float length;
    private float x;
    private float y;
    private int screenX;
    private int screenY;
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    ///maybe more movement than this
    private int SpaceShipMoving = STOPPED;
    public int spaceShipSpeed;

    public Spaceship(Context context, int screenX, int screenY){

        rect = new RectF();
        length = screenX/12;
        height = screenY/12;
        x = screenX / 2;
        y = 750;

        spaceShipSpeed = 450;

        bitmapup = BitmapFactory.decodeResource(context.getResources(), R.drawable.spaceshipup);
        bitmapup = Bitmap.createScaledBitmap(bitmapup,
                (int) (length),
                (int) (height),
                false);

        this.screenX = screenX;
        this.screenY = screenY;

    }

    public void setMovementState(int state){
        SpaceShipMoving = state;
    }


    public void update(long fps){
        if(SpaceShipMoving == LEFT){
            x = x - spaceShipSpeed / fps;
            if ((x+length)<=0)
                x = screenX;
        }
        if(SpaceShipMoving == RIGHT){
            x = x + spaceShipSpeed / fps;
            if (x>=screenX)
                x = 0 - length;
        }


        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;

    }


    public RectF getRect(){
        return rect;
    }

    public Bitmap getBitmap(){

        return bitmapup;
    }

    public float getX(){
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public float getY(){
        return y;
    }
    public void setY(int y){
        this.y = y;
    }
    public float getLength(){
        return length;
    }
    public float getHeight(){
        return height;
    }

}
