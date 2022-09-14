package com.example.glenn.spacegame;

import android.graphics.RectF;

public class Bullet {

    private float x;
    private float y;

    private RectF rect;

    // Which way is it shooting
     public int UP = 0;
     public int DOWN = 1;
public int RIGHT = 2;
public int LEFT = 3;
    // Going nowhere
    int heading = -1;
    float speed = 650;
   private int screenY;
    private int screenX;
    private int width;
    private int height;

    private boolean isActive;

    public Bullet(int screenY, int screenX) {

      //  height = screenY / 20;
        isActive = false;
        this.screenX =screenX;
        this.screenY = screenY;
        this.rect = new RectF();
    }

    public boolean shoot(float startX, float startY, int direction) {
        if (!isActive) {

            x = startX;
            y = startY;
            heading = direction;
            isActive = true;

            if ((direction == RIGHT)||(direction==LEFT))
            {  width = screenX/20;
                 height = 1;}

                 else{height = screenY/20;
                width = 1;}

            return true;
        }

        // Bullet already active
        return false;
    }

    public void update(long fps) {

        // Just move up or down
        if(heading == UP){
        y = y - speed / fps;
           }else if (heading == DOWN){
               y = y + speed / fps;
         }
         else if (heading == RIGHT){
            x = x + speed / fps;
        }

        else
        { x = x - speed / fps;}

        // Update rect
        rect.left = x+10;
        rect.right = x + width;
        rect.top = y+10;
        rect.bottom = y + height;
    }


    public RectF getRect(){
        return  rect;
    }

    public boolean getStatus(){
        return isActive;
    }

    public void setInactive(){
        isActive = false;
    }

    public float getImpactPointY() {
        if (heading == DOWN) {
            return y + height;
        }

        return y;

    }

    public float getImpactPointX() {
      if (heading == RIGHT){
        return  x + width; }

         return x;}
        //



}
