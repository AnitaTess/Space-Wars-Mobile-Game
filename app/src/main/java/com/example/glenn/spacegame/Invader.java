package com.example.glenn.spacegame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import java.util.Random;

public class Invader {

    RectF rect;

    Random generator = new Random();

    // The Invader ship will be represented by a Bitmap
    private Bitmap bitmap2;

    // Size of the invader ship
    private float length;
    private float height;

    // X horizontal coordinate
    private float x;

    // Y is the vertical coordinate
    private float y;

    // Pixels per second - invader speed
    private float shipSpeed;

    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the ship moving and in which direction
    private int shipMoving = RIGHT;

    // is the invader ship visible
    boolean isVisible;


    public Invader(Context context, int row, int column, int screenX, int screenY) {

        // Initialize a  RectF
        rect = new RectF();

        length = screenX / 30;
        height = screenY / 30;

        isVisible = true;

        int padding = screenX / 50;

        x = column * (length + padding);
        y = row * (length + padding/5);

        // Initialize the bitmap
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemyblue);


        // stretch the first bitmap to a size appropriate for the screen resolution
        bitmap2 = Bitmap.createScaledBitmap(bitmap2,
                (int) (length),
                (int) (height),
                false);

        // How fast is the invader in pixels per second
        shipSpeed = 40;


    }
    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }

    public RectF getRect(){
        return rect;
    }

    public Bitmap getBitmap2(){
        return bitmap2;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getLength(){
        return length;
    }


    public void update(long fps){
        if(shipMoving == LEFT){
            x = x - shipSpeed / fps;
        }

        if(shipMoving == RIGHT){
            x = x + shipSpeed / fps;
        }

        // Update rect which is to detect hits
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;

    }

    public void dropDownAndReverse(){
        if(shipMoving == LEFT){
            shipMoving = RIGHT;
        }else{
            shipMoving = LEFT;
        }

        y = y + height;

        shipSpeed = shipSpeed * 1.18f;
    }

    public boolean takeAim(float playerX, float PlayerLength){

        int randomNumber = -1;

        // If horizontally aligned with the player
        if((playerX + PlayerLength > x &&
                playerX + PlayerLength < x + length) || (playerX > x && PlayerLength < x + length)) {

            //chance to shoot
            randomNumber = generator.nextInt(100);
            if(randomNumber == 0) {
                return true;
            }

        }

        // If fire randomly
        randomNumber = generator.nextInt(1000);
        if(randomNumber == 0){
            return true;
        }

        return false;
    }
}
