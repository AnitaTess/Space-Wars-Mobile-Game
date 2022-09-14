package com.example.glenn.spacegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import java.util.Random;

public class PowerUp {
    public Bitmap bitmapHealth;
    public Bitmap currentPowerUp;
    public String[] bitmapPowerUp = {"life","speed"};
    private int randomIndex;
    RectF rect;
    private Random random;
    private float height;
    private float length;
    private float x;
    private float y;
    private int speed;
    private int screenX;
    private float minSpawn = 200;
    private float maxSpawn = 1500;
    private float randomX;
    private int screenY;
    private boolean isActive;

    public PowerUp(Context context, int screenX, int screenY)
    {
        rect = new RectF();
        isActive = true;
        length = screenX/10;
        height = screenY/10;
        randomX = getRandomFloat(minSpawn,maxSpawn);
        randomIndex = getIndex();

        switch (bitmapPowerUp[randomIndex])
        {
            case "life":
                currentPowerUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.life);
                currentPowerUp = Bitmap.createScaledBitmap(currentPowerUp,
                        (int) (length),
                        (int) (height),
                        false);
                break;
            case "speed":
                currentPowerUp = BitmapFactory.decodeResource(context.getResources(), R.drawable.speed);
                currentPowerUp = Bitmap.createScaledBitmap(currentPowerUp,
                        (int) (length),
                        (int) (height),
                        false);
                break;
            default:
                break;
        }

        speed = 150;
        x = randomX;
        y = -100;

        this.screenX = screenX;
        this.screenY = screenY;
    }
    public void update(long fps){

        if(isActive)
        {
            y = y + speed/fps;

            rect.top = y;
            rect.bottom = y + height;
            rect.left = x;
            rect.right = x + length;

            //If power up does not get collected then will set inactive and disappear
            if (getY() > screenY)
            {
                setInactive();
            }
        }

    }



    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getHeight() {return height;}
    public float getLength()
    {
        return length;
    }

    public RectF getRect() {
        return rect;
    }

    public Bitmap getCurrentPowerUp() {
        return currentPowerUp;
    }


    public boolean isActive() {
        return isActive;
    }

    public void Rect()
    {
        rect.setEmpty();
    }

    public void setInactive()
    {
        isActive = false;
        Rect();
    }

    public int getIndex()
    {
        return (new Random().nextInt(bitmapPowerUp.length));
    }

    public static float getRandomFloat(float min, float max)
    {
        return (new Random().nextFloat() * (max - min)) + min;
    }

    public String getPowerUpType(){
        return bitmapPowerUp[randomIndex];
    }

}
