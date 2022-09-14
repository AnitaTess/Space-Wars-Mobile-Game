package com.example.glenn.spacegame;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SpaceGameView extends SurfaceView implements Runnable{

    private Context context;

    // The invaders bullets
    private Bullet[] invadersBullets = new Bullet[200];
    private int nextBullet;
    private int maxInvaderBullets = 10;

    // Array of 60 invaders
    Invader[] invaders = new Invader[60];
    int numInvaders = 0;

    // This is our thread
    private Thread gameThread = null;

    // Our SurfaceHolder to lock the surface before we draw our graphics
    private SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    private volatile boolean playing;

    // Game is paused at the start
    private boolean paused = true;

    // A Canvas and a Paint object
    private Canvas canvas;
    private Paint paint;

    // This variable tracks the game frame rate
    private long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    // The size of the screen in pixels
    private int screenX;
    private int screenY;

    // The score
    public int score = 0;
    private boolean first;

    // Lives
    private int lives = 3;


    private Spaceship spaceShip;
    private Bullet bullet;
    private PowerUp powerUp;
    private Bitmap bitmapback;
    // The player's shelters are built from bricks
    private Defence[] squares = new Defence[200];
    private int numSquares;


    // This special constructor method runs
    public SpaceGameView(Context context, int x, int y) {

        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);

        // Make a globally available copy of the context so we can use it in another method
        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;


        initLevel();

    }



    private void initLevel(){

        spaceShip = new Spaceship(context, screenX, screenY);
        bullet = new Bullet(screenY,screenX);

        // Build invaders bullets
        for(int i = 0; i < invadersBullets.length; i++){
            invadersBullets[i] = new Bullet(screenY, screenX);
        }
        // Build an army of invaders
        numInvaders = 0;
        for(int column = 0; column < 10; column ++ ){
            for(int row = 0; row < 3; row ++ ){
                invaders[numInvaders] = new Invader(context, row, column, screenX, screenY);
                numInvaders ++;
            }
        }

        // Build walls
        numSquares = 0;
        for(int wallNumber = 0; wallNumber < 3; wallNumber++){
            for(int column = 0; column < 10; column ++ ) {
                for (int row = 0; row < 2; row++) {
                    squares[numSquares] = new Defence(row, column, wallNumber, screenX, screenY);
                    numSquares++;
                }
            }
        }

        //Spawns collectable power ups
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                powerUp = new PowerUp(context, screenX, screenY);

            }
        },0,10000);
    }


    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if(!paused){
                update();
            }

            // Draw the frame
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

        }

    }



    private void update(){

        spaceShip.update(fps);
        powerUp.update(fps);

        if(bullet.getStatus())
            bullet.update(fps);

            checkCollisions();

        // Did an invader hit the edge of the screen
        boolean hitEdge = false;

        // Has the player lost
        boolean lost = false;



        // Update the invaders if visible is set true
        for (int i = 0; i < numInvaders; i++) {

            if (invaders[i].getVisibility()) {
                // Move the next invader
                invaders[i].update(fps);

                // Does he want to take a shot?
                if (invaders[i].takeAim(spaceShip.getX(),
                        spaceShip.getLength())) {

                    // If positive spawn the bullet
                    if (invadersBullets[nextBullet].shoot(invaders[i].getX()
                                    + invaders[i].getLength() / 2,
                            invaders[i].getY(), bullet.DOWN)) {


                        // Next shot
                        nextBullet++;

                        // Loop back to the first one if we have reached the last
                        if (nextBullet == maxInvaderBullets) {
                            // This stops the firing of another bullet until one completes
                            nextBullet = 0;
                        }
                    }
                }

                // If that move caused them to hit the edge of the screen change to true
                if (invaders[i].getX() > screenX - invaders[i].getLength()
                        || invaders[i].getX() < 0) {

                    hitEdge = true;

                }
            }

        }

        // Update all the invaders bullets if active
        for(int i = 0; i < invadersBullets.length; i++){
            if(invadersBullets[i].getStatus()) {
                invadersBullets[i].update(fps);
            }
        }

        // Did an invader hit the edge of the screen
        if (hitEdge) {

            // Move all the invaders down and change direction
            for (int i = 0; i < numInvaders; i++) {
                invaders[i].dropDownAndReverse();
                // Have the invaders landed
                if (invaders[i].getY() > screenY - screenY / 10) {
                    lost = true;
                    checkCollisions();
                }
            }

            if (lost) {
                initLevel();
            }
        }

        // Update the players bullet
        if(bullet.getStatus()){
            bullet.update(fps);
        }
        // Update all the invaders bullets if active
        for(int i = 0; i < invadersBullets.length; i++){
            if(invadersBullets[i].getStatus()) {
                invadersBullets[i].update(fps);
            }
        }

        // Has the player's bullet hit the top of the screen
        if(bullet.getImpactPointY() < 0){
            bullet.setInactive();
        }


        // Has an invaders bullet hit the bottom of the screen
        for(int i = 0; i < invadersBullets.length; i++){
            if(invadersBullets[i].getImpactPointY() > screenY){
                invadersBullets[i].setInactive();
            }
        }

        // Has the player's bullet hit an invader
        if(bullet.getStatus()) {
            for (int i = 0; i < numInvaders; i++) {
                if (invaders[i].getVisibility()) {
                    if (RectF.intersects(bullet.getRect(), invaders[i].getRect())) {
                        invaders[i].setInvisible();
                        bullet.setInactive();
                        score = score + 10;
                        // Has the player won
                        if(score == numInvaders * 10){
                            paused = true;
                            score = 0;
                            lives = 3;
                            initLevel();
                        }
                    }
                }
            }
        }

        // Has an alien bullet hit a shelter brick
        for(int i = 0; i < invadersBullets.length; i++){
            if(invadersBullets[i].getStatus()){
                for(int j = 0; j < numSquares; j++){
                    if(squares[j].getVisibility()){
                        if(RectF.intersects(invadersBullets[i].getRect(), squares[j].getRect())){
                            // A collision has occurred
                            invadersBullets[i].setInactive();
                            squares[j].setInvisible();
                        }
                    }
                }
            }

        }

        // Has a player bullet hit a shelter brick
        if(bullet.getStatus()){
            for(int i = 0; i < numSquares; i++){
                if(squares[i].getVisibility()){
                    if(RectF.intersects(bullet.getRect(), squares[i].getRect())){
                        // A collision has occurred
                        bullet.setInactive();
                        squares[i].setInvisible();
                    }
                }
            }
        }

        // Has an invader bullet hit the player ship
        for(int i = 0; i < invadersBullets.length; i++){
            if(invadersBullets[i].getStatus()){
                if(RectF.intersects(spaceShip.getRect(), invadersBullets[i].getRect())){
                    invadersBullets[i].setInactive();
                    lives --;


                    // Is it game over?
                    if(lives == 0){
                        paused = true;
                        lives = 3;
                        score = 0;
                        initLevel();

                    }
                }
            }
        }

    }


    private void checkCollisions(){
        RectF collectable = new RectF(powerUp.getRect());
        RectF player = new RectF(spaceShip.getRect());

        if(collectable.intersect(player))
        {
            powerUp.setInactive();
            switch (powerUp.getPowerUpType())
            {
                case "life":
                    lives++;
                    break;

                case"speed":
                    spaceShip.spaceShipSpeed += 100;
                    Log.d("Player speed", String.valueOf(spaceShip.spaceShipSpeed));
                    break;

                default:
                    break;
            }
        }

        if(bullet.getImpactPointY() < 0)
            bullet.setInactive();
        if(bullet.getImpactPointY() > screenY)
            bullet.setInactive();

        if(bullet.getImpactPointX() < 0)
            bullet.setInactive();
        if(bullet.getImpactPointX() > screenX)
            bullet.setInactive();

        }




    private void draw(){
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
           canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Choose the brush color for drawing
            paint.setColor(Color.RED);

              bitmapback = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
              bitmapback = Bitmap.createScaledBitmap(bitmapback, (int) (screenX), (int) (screenY),false);
          //  canvas.drawBitmap(background.getBitmap(), spaceShip.getX(), spaceShip.getY() , paint);
            //  draw the defender
            canvas.drawBitmap(bitmapback, 0, 0, paint);
            canvas.drawBitmap(spaceShip.getBitmap(), spaceShip.getX(), spaceShip.getY() , paint);

            if(powerUp.isActive())
            {
                canvas.drawBitmap(powerUp.getCurrentPowerUp(), powerUp.getX(), powerUp.getY() , paint);
            }

            if(bullet.getStatus())
                canvas.drawRect(bullet.getRect(), paint);

            for(int i = 0; i < numSquares; i++){
                paint.setColor(Color.YELLOW);
                if(squares[i].getVisibility()) {
                    canvas.drawRect(squares[i].getRect(), paint);
                }
            }
            // Draw the invaders
            for (int i = 0; i < numInvaders; i++) {
                if (invaders[i].getVisibility()) {

                    canvas.drawBitmap(invaders[i].getBitmap2(), invaders[i].getX(), invaders[i].getY(), paint);

                }
            }

            // Draw the players bullet if active
            if(bullet.getStatus()){
                paint.setColor(Color.GREEN);
                canvas.drawRect(bullet.getRect(), paint);
            }


            // draw/update all the invader's bullets if active
            for(int i = 0; i < invadersBullets.length; i++){
                if(invadersBullets[i].getStatus()) {
                    paint.setColor(Color.RED);
                    canvas.drawRect(invadersBullets[i].getRect(), paint);
                }
            }


            // Draw the score and remaining lives
            // Change the brush color
            paint.setColor(Color.argb(255,  249, 129, 0));
            paint.setTextSize(40);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 10,50, paint);

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }


    // If SpaceGameActivity is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }


    // If SpaceInvadersActivity is started then
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen

            case MotionEvent.ACTION_DOWN:

                paused = false;

                if(motionEvent.getY() > screenY - screenY / 2) {
                    if (motionEvent.getX() > screenX / 2) {
                        spaceShip.setMovementState(spaceShip.RIGHT);
                    } else {
                        spaceShip.setMovementState(spaceShip.LEFT);
                    }

                }

                if(motionEvent.getY() < screenY - screenY / 2) {
                    if (motionEvent.getX() > screenX / 2) {

                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength()/2,spaceShip.getY(),0);
                    } else {

                        bullet.shoot(spaceShip.getX()+ spaceShip.getLength()/2,spaceShip.getY()+ spaceShip.getHeight(),0);
                    }


                }


                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:

             //   if(motionEvent.getY() > screenY - screenY / 10) {
                    spaceShip.setMovementState(spaceShip.STOPPED);
             //   }
                break;
        }
        return true;
    }





}  // end class
