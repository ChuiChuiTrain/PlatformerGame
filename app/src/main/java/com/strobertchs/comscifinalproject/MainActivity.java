package com.strobertchs.comscifinalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends Activity {

    Canvas canvas;
    SnakeAnimView snakeAnimView;

    //The snake head sprite sheet
    Bitmap playerBitmap;
    //The portion of the bitmap to be drawn in the current frame
    Rect rectToBeDrawn;
    //The dimensions of a single frame
    int frameHeight = 483;
    int frameWidth = 289;
    int numFrames = 10;
    int frameNumber;

    double blockSize;
    int numBlocksWide;
    int numBlocksHigh;


    int screenWidth;
    int screenHeight;

    int slideNum = 0;
    Bitmap slide1,slide2,slide3,slide4,slide5,slide6,slide7,slide8,slide9,slide10,slide11;
    Bitmap[] storySlides;
    Bitmap backgroundBitmap;

    //stats
    long lastFrameTime;
    int fps;
    int hi;

    //fps 60 variable
    private int FPS = 60;

    //To start the game from onTouchEvent
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //find out the width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.charidle);

        snakeAnimView = new SnakeAnimView(this);
        setContentView(snakeAnimView);

        i = new Intent(this, GameActivity.class);

        configureDisplay();

    }

    class SnakeAnimView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingSnake;
        Paint paint;

        public SnakeAnimView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
            frameWidth=playerBitmap.getWidth()/numFrames;
            frameHeight=playerBitmap.getHeight();
        }


        @Override
        public void run() {
            while (playingSnake) {
                update();
                draw();
                controlFPS();

            }

        }

        public void update() {

            //which frame should we draw
            rectToBeDrawn = new Rect((frameNumber * frameWidth)-1, 0,
                    (frameNumber * frameWidth +frameWidth)-1, frameHeight);

            //now the next frame
            frameNumber++;

            //don't try and draw frames that don't exist
            if(frameNumber == numFrames){
                frameNumber = 0;//back to the first frame
            }

        }

        public void draw() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                //Paint paint = new Paint();
                canvas.drawColor(Color.BLACK);//the background
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(screenWidth/10);
                canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
                canvas.drawText("Trash Fighter", 400, 150, paint);
                paint.setTextSize(25);
                //canvas.drawText("High Score:" + hi, 100, screenHeight-50, paint);

                //Draw the snake head
                //make this Rect whatever size and location you like
                //(startX, startY, endX, endY)
                Rect destRect = new Rect(screenWidth/2-100, screenHeight/2-167, screenWidth/2+100, screenHeight/2+167);

                canvas.drawBitmap(playerBitmap, rectToBeDrawn, destRect, paint);

                if (slideNum <= 10){
                    canvas.drawBitmap(storySlides[slideNum],0,0,paint);
                }

                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        public void controlFPS() {
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = 100 - FPS;
            if (timeThisFrame > 0) {
                fps = (int) (1000 / FPS);
            }
            if (timeToSleep > 0) {

                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                }

            }

            lastFrameTime = System.currentTimeMillis();
        }


        public void pause() {
            playingSnake = false;
            try {
                ourThread.join();
            } catch (InterruptedException e) {
            }

        }

        public void resume() {
            playingSnake = true;
            ourThread = new Thread(this);
            ourThread.start();
        }


        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {



            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    slideNum++;
            }

            if(slideNum > 11){
                startActivity(i);
            }

            return true;
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        while (true) {
            snakeAnimView.pause();
            break;
        }

        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        snakeAnimView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        snakeAnimView.pause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            snakeAnimView.pause();
            finish();
            return true;
        }
        return false;
    }

    public void configureDisplay(){
        //find out the width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        //Determine the size of each block/place on the game board
        blockSize = screenWidth/400.0;

        //Determine how many game blocks will fit into the height and width
        //Leave one block for the score at the top
        numBlocksWide = 400;
        numBlocksHigh = (int) Math.round(screenHeight/blockSize);

        slide1 = BitmapFactory.decodeResource(getResources(), R.drawable.story1);
        slide2 = BitmapFactory.decodeResource(getResources(), R.drawable.story2);
        slide3 = BitmapFactory.decodeResource(getResources(), R.drawable.story3);
        slide4 = BitmapFactory.decodeResource(getResources(), R.drawable.story4);
        slide5= BitmapFactory.decodeResource(getResources(), R.drawable.story5);
        slide6 = BitmapFactory.decodeResource(getResources(), R.drawable.story6);
        slide7 = BitmapFactory.decodeResource(getResources(), R.drawable.story7);
        slide8 = BitmapFactory.decodeResource(getResources(), R.drawable.story8);
        slide9 = BitmapFactory.decodeResource(getResources(), R.drawable.story9);
        slide10 = BitmapFactory.decodeResource(getResources(), R.drawable.story10);
        slide11 = BitmapFactory.decodeResource(getResources(), R.drawable.story11);
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.menubackground);


        slide1 = Bitmap.createScaledBitmap(slide1, screenWidth, screenHeight, false);
        slide2 = Bitmap.createScaledBitmap(slide2, screenWidth, screenHeight, false);
        slide3 = Bitmap.createScaledBitmap(slide3, screenWidth, screenHeight, false);
        slide4 = Bitmap.createScaledBitmap(slide4, screenWidth, screenHeight, false);
        slide5 = Bitmap.createScaledBitmap(slide5, screenWidth, screenHeight, false);
        slide6 = Bitmap.createScaledBitmap(slide6, screenWidth, screenHeight, false);
        slide7 = Bitmap.createScaledBitmap(slide7, screenWidth, screenHeight, false);
        slide8 = Bitmap.createScaledBitmap(slide8, screenWidth, screenHeight, false);
        slide9 = Bitmap.createScaledBitmap(slide9, screenWidth, screenHeight, false);
        slide10 = Bitmap.createScaledBitmap(slide10, screenWidth, screenHeight, false);
        slide11 = Bitmap.createScaledBitmap(slide11, screenWidth, screenHeight, false);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenWidth, screenHeight, false);


        //storySlides =  new Bitmap[]{slide1, slide2, slide3, slide4, slide5, slide6, slide7, slide8, slide9, slide10, slide11};
        storySlides = new Bitmap[]{slide1,slide2,slide3,slide4,slide5,slide6,slide7,slide8,slide9,slide10,slide11};

    }




}