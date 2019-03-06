package com.strobertchs.comscifinalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class shop extends Activity {
    int screenWidth;
    int screenHeight;
    Intent map1, map2, map3, map4, map5;
    Intent i;
    character fab;
    character speedButton;
    character healthButton;
    character coinButton;
    character nextSignButton;
    double blockSize;
    int numBlocksWide;
    int numBlocksHigh;
    int ground1;
    int lvl;
    Canvas canvas;
    GameActivityView gameActivityView;
    Bitmap healthBitmap;
    Bitmap speedBitmap;
    Bitmap coinBitmap;
    Bitmap nextSignBitmap;
    Bitmap fabBitmap;
    int charFrame = 0;

    int playerHealth;
    int mapNum = 1;
    int playerSpeed;
    //This is money
    int playerCurrency;
    int speedUpgradeCost = 1;
    int healthUpgradeCost = 1;

    //stats
    long lastFrameTime;
    int fps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   loadSound();
        configureDisplay();
        gameActivityView = new GameActivityView(this);
        setContentView(gameActivityView);
        i = new Intent(this, MainActivity.class);
        map5 = new Intent(this, FinalLevel.class);
        map4 = new Intent(this, GameActivity4.class);
        map3 = new Intent(this, GameActivity3.class);
        map2 = new Intent(this, GameActivity2.class);
        map1 = new Intent(this,MainActivity.class);



        playerHealth = getIntent().getIntExtra("health", 0);
        playerCurrency = getIntent().getIntExtra("currency", 0);
        lvl = getIntent().getIntExtra("lvl", 2);
        mapNum = lvl;


    }
    class GameActivityView extends SurfaceView implements Runnable {
        Thread ourThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playingGame;
        Paint paint;
        public GameActivityView(Context context) {
            super(context);
            ourHolder = getHolder();
            paint = new Paint();
        }
        public boolean generalButtonTouchEvent(float motionEventX, float motionEventY, int xValue, int yValue, int blockWidth, int blockHeight) {
            if (((motionEventX >= xValue) && (motionEventX <= (xValue + (int) Math.round(blockWidth * blockSize))))
                    && ((motionEventY >= yValue) && (motionEventY <= (yValue + (int) Math.round(blockHeight * blockSize))))) {
                return true;
            } else {
                return false;
            }
        }
        @Override
        public void run() {
            while (playingGame) {
                drawGame();
                controlFPS();
                charFrame++;
                if (charFrame > 9) {
                    charFrame = 0;
                }
            }
        }

        public void drawGame() {
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                //Paint paint = new Paint();
                canvas.drawColor(Color.BLACK);//the background
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(screenWidth/30);
                canvas.drawBitmap(fabBitmap, fab.getPositionX(), fab.getPositionY(), paint);
                canvas.drawText("Health: " + playerHealth, healthButton.getPositionX() + 25, healthButton.getPositionY() - 50, paint);
                canvas.drawText("Speed Level: " + playerSpeed, speedButton.getPositionX() + 25, speedButton.getPositionY() - 50, paint);
                canvas.drawText("Money: " + playerCurrency, coinButton.getPositionX() + coinButton.getRoundedWidth(), coinButton.getPositionY() + coinButton.getRoundedHeight()/2, paint);
                //canvas.drawText("SALE! EVERYTHING 1BTC", (int) Math.round(screenWidth/3.0), (int) Math.round(screenHeight * 0.7), paint);
                canvas.drawBitmap(healthBitmap, healthButton.getPositionX(), healthButton.getPositionY(), paint);
                canvas.drawBitmap(speedBitmap, speedButton.getPositionX(), speedButton.getPositionY(), paint);
                canvas.drawBitmap(coinBitmap, coinButton.getPositionX(), coinButton.getPositionY(), paint);
                canvas.drawBitmap(nextSignBitmap, nextSignButton.getPositionX(), nextSignButton.getPositionY(), paint);

                if (mapNum == 2){
                    paint.setTextSize(screenWidth/50);
                    canvas.drawText(" 'Pollution is one of the biggest global killer, over 100 million people affected' ", 20, screenHeight -75 , paint);
                }
                if (mapNum == 3){
                    paint.setTextSize(screenWidth/50);
                    canvas.drawText(" 'Ships & Cargos dumped 14 billion pounds of garbage into the ocean in 1975' ", 20, screenHeight - 75 , paint);                }
                if (mapNum == 4){
                    paint.setTextSize(screenWidth/50);
                    canvas.drawText(" 'A million of seabirds and hudred thousands of sea mammals are killed by pollution annually.' ", 20, screenHeight - 75 , paint);
                }
                if (mapNum == 5){
                    paint.setTextSize(screenWidth/50);
                    canvas.drawText(" 'Living with high levels of air pollutants have 20% higher risk of death from lung cancer' ", 20, screenHeight - 75 , paint);
                }
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
        public void controlFPS() {
            int FPS = 60;
            long timeThisFrame = (System.currentTimeMillis() - lastFrameTime);
            long timeToSleep = 100 - FPS;
            if (timeThisFrame > 0) {
                fps = (int) (1000 / FPS);
            }
            if (timeToSleep > 0) {

                try {
                    ourThread.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    //Print an error message to the console
                    Log.e("error", "failed to load sound files");
                }
            }
            lastFrameTime = System.currentTimeMillis();
        }
        public void pause() {
            playingGame = false;
            try {
                ourThread.join();
            } catch (InterruptedException e) {
            }
        }
        public void resume() {
            playingGame = true;
            ourThread = new Thread(this);
            ourThread.start();
        }
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (generalButtonTouchEvent(motionEvent.getX(), motionEvent.getY(), healthButton.getPositionX(), healthButton.getPositionY(), healthButton.getBlockWidth(), healthButton.getBlockHeight())){
                        if (playerCurrency >= healthUpgradeCost) {
                            playerHealth++;
                            playerCurrency = playerCurrency - healthUpgradeCost;
                            healthUpgradeCost++;
                        }
                    }
                    if (generalButtonTouchEvent(motionEvent.getX(), motionEvent.getY(), speedButton.getPositionX(), speedButton.getPositionY(), speedButton.getBlockWidth(), speedButton.getBlockHeight())) {
                        if (playerCurrency >= speedUpgradeCost) {
                            playerSpeed++;
                            playerCurrency = playerCurrency - speedUpgradeCost;
                            speedUpgradeCost++;
                        }
                    }
                    if (generalButtonTouchEvent(motionEvent.getX(), motionEvent.getY(), nextSignButton.getPositionX(), nextSignButton.getPositionY(), nextSignButton.getBlockWidth(), nextSignButton.getBlockHeight())) {

                        if(mapNum == 2){
                            map2.putExtra("level", lvl);
                            map2.putExtra("health", playerHealth);
                            map2.putExtra("speed", playerSpeed);
                            map2.putExtra("currency", playerCurrency);
                            startActivity(map2);
                        }
                        if(mapNum == 3){
                            map3.putExtra("level", lvl);
                            map3.putExtra("health", playerHealth);
                            map3.putExtra("speed", playerSpeed);
                            map3.putExtra("currency", playerCurrency);
                            startActivity(map3);
                        }
                        if(mapNum == 4){
                            map4.putExtra("level", lvl);
                            map4.putExtra("health", playerHealth);
                            map4.putExtra("speed", playerSpeed);
                            map4.putExtra("currency", playerCurrency);
                            startActivity(map4);
                        }
                        if(mapNum == 5){
                            map5.putExtra("level", lvl);
                            map5.putExtra("health", playerHealth);
                            map5.putExtra("speed", playerSpeed);
                            map5.putExtra("currency", playerCurrency);
                            startActivity(map5);
                        }

                    }

                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
            }
            return true;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        while (true) {
            gameActivityView.pause();
            break;
        }
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        gameActivityView.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        gameActivityView.pause();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            gameActivityView.pause();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return false;
    }
    public void configureDisplay() {
        //find out the width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        //Determine the size of each block/place on the game board
        blockSize = screenWidth / 400.0;
        //Determine how many game blocks will fit into the height and width
        //Leave one block for the score at the top
        numBlocksWide = 400;

        numBlocksHigh = (int) Math.round(screenHeight / blockSize);

        fab = new character(0, 0, numBlocksHigh, 400, blockSize);
        healthButton = new character(50, 80, 70, 70, blockSize);
        speedButton = new character(150, 80, 70, 70, blockSize);
        coinButton = new character(5, 5, 50, 50, blockSize);
        nextSignButton = new character(330, 140, 75, 75, blockSize);
        ground1 = numBlocksHigh - 30;

        fabBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.shopbackground);
        fabBitmap = Bitmap.createScaledBitmap(fabBitmap, fab.getRoundedWidth(), fab.getRoundedHeight(), false);
        healthBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.heart);
        healthBitmap = Bitmap.createScaledBitmap(healthBitmap, healthButton.getRoundedWidth(), healthButton.getRoundedHeight(), false);

        speedBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.shoe);
        speedBitmap = Bitmap.createScaledBitmap(speedBitmap, speedButton.getRoundedWidth(), speedButton.getRoundedHeight(), false);

        coinBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.coin);
        coinBitmap = Bitmap.createScaledBitmap(coinBitmap, coinButton.getRoundedWidth(), coinButton.getRoundedHeight(), false);

        nextSignBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.nextsign);
        nextSignBitmap = Bitmap.createScaledBitmap(nextSignBitmap, nextSignButton.getRoundedWidth(), nextSignButton.getRoundedHeight(), false);

    }

}
