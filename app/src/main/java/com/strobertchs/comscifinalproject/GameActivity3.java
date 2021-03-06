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

/**
 * Created by Gilb on 1/23/2017.
 */

public class GameActivity3 extends Activity{

    int temp;
    int jabTime;
    int screenWidth;
    int screenHeight;
    int currency = 0;
    Intent i;
    Intent startShop;

    playerInfo player;
    character enemy1;
    character jumpButtonDown;
    character dpadLeft;
    character dpadRight;
    character punchButton;
    character enemyBottle;
    character portal;
    character spike;
    character grassPlatform;
    character grassPlatform2;
    character heart;
    character coin;
    int lvl = 2;


    int charBlockBottomGap = 3;

    boolean charMoveLeft;
    boolean charMoveRight;
    boolean charMoveUp;
    boolean charMoveDown;
    boolean doubleJump;
    boolean enemyBottleMoveLeft = true;
    boolean enemiesDefeated = false;


    double blockSize;
    int numBlocksWide;
    int numBlocksHigh;
    int mysteriousBottomGapBlock = 15;

    boolean noPush = true;
    boolean enemyNoFlip = true;
    boolean noFlip = true;
    int ground1;
    boolean noGravity = false;

    int tempUp = 1;
    boolean tempUpFinal;

    Canvas canvas;
    GameActivityView gameActivityView;

    Bitmap grassPlatformBitmap;
    Bitmap jumpNoPushBitmap, jumpPushBitmap;
    Bitmap dpadLeftBitmap;
    Bitmap dpadRightBitmap;
    Bitmap punchButtonBitmap;
    Bitmap portalBitmap;
    Bitmap spikeBitmap;
    Bitmap coinBitmap;
    Bitmap heartBitmap;
    Bitmap charBitmap0, charBitmap1, charBitmap2, charBitmap3, charBitmap4, charBitmap5, charBitmap6, charBitmap7, charBitmap8, charBitmap9;
    Bitmap charWalkBitmap0,charWalkBitmap1,charWalkBitmap2,charWalkBitmap3,charWalkBitmap4,charWalkBitmap5,charWalkBitmap6,charWalkBitmap7,charWalkBitmap8,charWalkBitmap9;
    Bitmap charJabBitmap0,charJabBitmap1,charJabBitmap2,charJabBitmap3,charJabBitmap4,charJabBitmap5,charJabBitmap6,charJabBitmap7,charJabBitmap8,charJabBitmap9;
    Bitmap enemyPopBottleBitmap0,enemyPopBottleBitmap1,enemyPopBottleBitmap2,enemyPopBottleBitmap3,enemyPopBottleBitmap4,enemyPopBottleBitmap5,enemyPopBottleBitmap6,enemyPopBottleBitmap7,enemyPopBottleBitmap8,enemyPopBottleBitmap9;
    Bitmap[] charJabBitmapArray;
    Bitmap[] charBitmapArray;
    Bitmap[] charWalkBitmapArray;
    Bitmap[] enemyPopBottleBitmapArray;
    Bitmap backgroundBitmap;



    int charFrame = 0;
    int jabFrame = 0;
    boolean charJab = false;

    //stats
    long lastFrameTime;
    int fps;
    int score;
    int hi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //   loadSound();
        configureDisplay();
        gameActivityView = new GameActivityView(this);
        setContentView(gameActivityView);

        i = new Intent(this, MainActivity.class);
        startShop = new Intent(this, shop.class);
        player.setHealth(getIntent().getIntExtra("health", 0));
        currency = getIntent().getIntExtra("currency", 0);
        player.setSpeed(7 + getIntent().getIntExtra("speed",0));
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

            charMoveDown = true;
            /*
            //our starting snake
            getSnake();
            //get an apple to munch
            getApple();
            */
        }

        public void gravity() {
            if (noGravity == false) {
                player.setBlockY(player.getBlockY() + 10);
            }
        }

        public int applyGravityTo(int positionY){
            positionY = positionY + 10;
            return positionY;
        }

        public void checkJump(){
            //jump
            if (tempUpFinal && (tempUp == 10)){
                charMoveUp = false;
                tempUp = 1;
                tempUpFinal = false;
            }

        }

        public void jumpIfApplicable(){
            //10 stage jump
            if (charMoveUp && (player.getBlockY() > 0)) {
                if (tempUp == 10){
                    tempUpFinal = true;
                }
                if (tempUp == 9) {
                    player.setBlockY(player.getBlockY() - 1);
                    tempUp = 10;
                }
                if (tempUp == 8) {
                    player.setBlockY(player.getBlockY() - 2);
                    tempUp = 9;
                }
                if (tempUp == 7) {
                    player.setBlockY(player.getBlockY() - 5);
                    tempUp = 8;
                }
                if (tempUp == 6) {
                    player.setBlockY(player.getBlockY() - 12);
                    tempUp = 7;
                }
                if (tempUp == 5){
                    player.setBlockY(player.getBlockY() - 18);
                    tempUp = 6;
                }
                if (tempUp == 4){
                    player.setBlockY(player.getBlockY() - 23);
                    tempUp = 5;
                }
                if (tempUp == 3){
                    player.setBlockY(player.getBlockY() - 26);
                    tempUp = 4;
                }
                if (tempUp == 2){
                    player.setBlockY(player.getBlockY() - 28);
                    tempUp = 3;
                }
                if (tempUp == 1){
                    player.setBlockY(player.getBlockY() - 30);
                    tempUp = 2;
                }
            }

            else if (charMoveUp && (player.getBlockY() < 0)){
                charMoveUp = false;
                tempUp = 1;
                tempUpFinal = false;
            }
        }

        public boolean generalButtonTouchEvent(float motionEventX, float motionEventY, int xValue, int yValue, int blockWidth, int blockHeight){
            if (((motionEventX >= xValue) && (motionEventX <= (xValue + (int) Math.round(blockWidth * blockSize))))
                    && ((motionEventY >= yValue) && (motionEventY <= (yValue + (int) Math.round(blockHeight * blockSize))))){
                return true;
            }
            else{
                return false;
            }
        }

        @Override
        public void run() {
            while (playingGame) {

                updateGame();
                drawGame();
                controlFPS();
                checkJump();
                charFrame++;

                if(charFrame > 9){
                    charFrame = 0;
                }
                if (charJab){
                    jabFrame++;
                    if (jabFrame > 9){
                        jabFrame = 0;
                        charJab = false;
                    }
                }

            }

        }

        public void updateGame() {

            noGravity = false;

            if (((player.getBlockX() + (player.getBlockWidth()/2)) > grassPlatform.getBlockX()) && ((player.getBlockX() + (player.getBlockWidth()/2)) < (grassPlatform.getBlockX() + grassPlatform.getBlockWidth()))) {
                //checks if the character is within range of (10 block to 0 blocks above) the platform
                if (((player.getBlockY() + player.getBlockHeight() - charBlockBottomGap) >= grassPlatform.getBlockY() - 10) && ((player.getBlockY() + player.getBlockHeight() - charBlockBottomGap) <= grassPlatform.getBlockY())) {
                    //sets noGravity to true so the character does not continue falling
                    noGravity = true;
                    //places the character on the platform
                    player.setBlockY(grassPlatform.getBlockY() - player.getBlockHeight() + charBlockBottomGap);
                }
            }

            if (((player.getBlockX() + (player.getBlockWidth()/2)) > grassPlatform2.getBlockX()) && ((player.getBlockX() + (player.getBlockWidth()/2)) < (grassPlatform2.getBlockX() + grassPlatform2.getBlockWidth()))) {
                if (((player.getBlockY() + player.getBlockHeight() - charBlockBottomGap) >= grassPlatform2.getBlockY() - 10) && ((player.getBlockY() + player.getBlockHeight() - charBlockBottomGap) <= grassPlatform2.getBlockY())) {
                    noGravity = true;
                    player.setBlockY(grassPlatform2.getBlockY() - player.getBlockHeight() + charBlockBottomGap);
                }
            }

            // "charPosition.x < numBlocksWide - charWidth" restricts it from going any further to the right
            if (charMoveRight && (player.getBlockX() < (numBlocksWide - player.getBlockWidth()))) {
                //move char right by 7
                player.setBlockX(player.getBlockX() + player.getSpeed());
            }

            // "charPosition.x > 0" restricts it from going any further to the left of it is smaller than 0
            if (charMoveLeft && (player.getBlockX() > 0)) {
                //move char left by 7
                player.setBlockX(player.getBlockX() - player.getSpeed());
            }


            //call the jumpIfApplicable method
            jumpIfApplicable();

            //if the player is in the air, apply gravity
            if (player.getBlockY() < (ground1 - player.getBlockHeight())) {
                gravity();
            }

            //if the player has hit the ground or lower, set the player on the ground
            if (player.getBlockY() >= (ground1 - player.getBlockHeight())){
                player.setBlockY(ground1 - player.getBlockHeight());

            }


            if (enemyBottle.getBlockX() > (300 - enemyBottle.getBlockWidth())){
                enemyBottleMoveLeft = true;
            }

            if (enemyBottleMoveLeft){
                enemyBottle.setBlockX(enemyBottle.getBlockX() - 5);
            }

            if (enemyBottle.getBlockX() < 0){
                enemyBottleMoveLeft = false;
            }

            if (!enemyBottleMoveLeft){
                enemyBottle.setBlockX(enemyBottle.getBlockX() + 5);
            }

            //collision detection between the char and enemyBottle

            if ((Math.abs((player.getBlockX() + (player.getBlockWidth()/2)) - (enemyBottle.getBlockX() + (enemyBottle.getBlockWidth()/2))) <= (player.getBlockWidth() + enemyBottle.getBlockWidth())/2 - 15)
                    && (Math.abs((player.getBlockY() + (player.getBlockHeight()/2)) - (enemyBottle.getBlockY() + (enemyBottle.getBlockHeight()/2))) <= (player.getBlockHeight() + enemyBottle.getBlockHeight())/2 - 10)){
                if (charJab) {
                    enemyBottle.setBlockX(-200);
                    enemyBottle.setBlockY(40);
                    currency += 3;
                }
                else{
                    if(player.getHealth() > 0) {
                        player.setBlockX(50);
                        player.setBlockY(150);
                        player.deductHealth();
                    }
                    else{
                        startActivity(i);
                    }
                }
            }
            //collision between player and spike
            if ((Math.abs((player.getBlockX() + (player.getBlockWidth()/2)) - (spike.getBlockX() + (spike.getBlockWidth()/2))) <= (player.getBlockWidth() + spike.getBlockWidth())/2 - 15)
                    && (Math.abs((player.getBlockY() + (player.getBlockHeight()/2)) - (spike.getBlockY() + (spike.getBlockHeight()/2))) <= (player.getBlockHeight() + spike.getBlockHeight())/2 - 10)){
                if(player.getHealth() > 0){
                    player.setBlockX(100);
                    player.setBlockY(130);
                    player.deductHealth();
                }
                else{
                    startActivity(i);
                }
            }

            if ((Math.abs((player.getBlockX() + (player.getBlockWidth()/2)) - (portal.getBlockX() + (portal.getBlockWidth()/2))) <= (player.getBlockWidth() + portal.getBlockWidth())/2 - 15)
                    && (Math.abs((player.getBlockY() + (player.getBlockHeight()/2)) - (portal.getBlockY() + (portal.getBlockHeight()/2))) <= (player.getBlockHeight() + portal.getBlockHeight())/2 - 10)){
                startShop.putExtra("level", lvl);
                startShop.putExtra("health", player.getHealth());
                startShop.putExtra("speed", player.getSpeed());
                startShop.putExtra("currency", currency);
                startShop.putExtra("lvl", 4);
                startActivity(startShop);
            }

            player.setPositionX((int) Math.round(player.getBlockX() * blockSize));
            player.setPositionY((int) Math.round(player.getBlockY() * blockSize));

            enemy1.setPositionX((int) Math.round(enemy1.getBlockX() * blockSize));
            enemy1.setPositionY((int) Math.round(enemy1.getBlockY() * blockSize));

            jumpButtonDown.setPositionX((int) Math.round(jumpButtonDown.getBlockX() * blockSize));
            jumpButtonDown.setPositionY((int) Math.round(jumpButtonDown.getBlockY() * blockSize));

            dpadLeft.setPositionX((int) Math.round(dpadLeft.getBlockX() * blockSize));
            dpadLeft.setPositionY((int) Math.round(dpadLeft.getBlockY() * blockSize));

            dpadRight.setPositionX((int) Math.round(dpadRight.getBlockX() * blockSize));
            dpadRight.setPositionY((int) Math.round(dpadRight.getBlockY() * blockSize));

            enemyBottle.setPositionX((int) Math.round(enemyBottle.getBlockX() * blockSize));
            enemyBottle.setPositionY((int) Math.round(enemyBottle.getBlockY() * blockSize));

            portal.setPositionX((int) Math.round(portal.getBlockX() * blockSize));
            portal.setPositionY((int) Math.round(portal.getBlockY() * blockSize));

            spike.setPositionX((int) Math.round(spike.getBlockX() * blockSize));
            spike.setPositionY((int) Math.round(spike.getBlockY() * blockSize));

            grassPlatform.setPositionX((int) Math.round(grassPlatform.getBlockX() * blockSize));
            grassPlatform.setPositionY((int) Math.round(grassPlatform.getBlockY() * blockSize));

            grassPlatform2.setPositionX((int) Math.round(grassPlatform2.getBlockX() * blockSize));
            grassPlatform2.setPositionY((int) Math.round(grassPlatform2.getBlockY() * blockSize));



        }

        public void drawGame() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                //Paint paint = new Paint();
                canvas.drawColor(Color.BLACK);//the background
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
                paint.setTextSize(25);
                canvas.drawBitmap(heartBitmap, heart.getPositionX(), heart.getPositionY(), paint);
                canvas.drawText("x"+ player.getHealth(), heart.getPositionX() + heart.getRoundedWidth(), heart.getPositionY() + heart.getRoundedHeight(), paint);
                canvas.drawBitmap(coinBitmap, coin.getPositionX(), coin.getPositionY(), paint);
                canvas.drawText("x"+ currency, coin.getPositionX() + coin.getRoundedWidth(), coin.getPositionY() + (int) Math.round(coin.getRoundedHeight() * 0.8), paint);

                canvas.drawBitmap(grassPlatformBitmap, grassPlatform.getPositionX(), grassPlatform.getPositionY(), paint);
                canvas.drawBitmap(grassPlatformBitmap, grassPlatform2.getPositionX(), grassPlatform2.getPositionY(), paint);
                if(!enemyBottleMoveLeft) {
                    enemyNoFlip = true;
                }
                if(enemyBottleMoveLeft){
                    enemyNoFlip = false;
                }
                if(enemyNoFlip){
                    canvas.drawBitmap(enemyPopBottleBitmapArray[charFrame],enemyBottle.getPositionX(),enemyBottle.getPositionY(),paint);
                }
                if(!enemyNoFlip){
                    Matrix flipHorizontalMatrix = new Matrix();
                    flipHorizontalMatrix.setScale(-1,1);
                    flipHorizontalMatrix.postTranslate(enemyBottle.getPositionX() + enemyBottle.getRoundedWidth(), enemyBottle.getPositionY());
                    canvas.drawBitmap(enemyPopBottleBitmapArray[charFrame],flipHorizontalMatrix, paint);
                }
                canvas.drawBitmap(portalBitmap, portal.getPositionX(),portal.getPositionY(), paint);
                canvas.drawBitmap(spikeBitmap,spike.getPositionX(),spike.getPositionY(),paint);


                if (charMoveRight){
                    noFlip = true;
                }
                if (charMoveLeft){
                    noFlip = false;
                }

                if (noFlip) {

                    if (charMoveRight) {
                        canvas.drawBitmap(charWalkBitmapArray[charFrame], player.getPositionX(), player.getPositionY(), paint);
                    }
                    if (charJab) {
                        /*
                        if(player.getBlockX() < (numBlocksWide - player.getBlockWidth())){
                            player.setBlockX(player.getBlockX() + 3);
                        }
                        */
                        canvas.drawBitmap(charJabBitmapArray[jabFrame], player.getPositionX(), player.getPositionY(), paint);
                    }
                    else if (!charMoveRight){
                        canvas.drawBitmap(charBitmapArray[charFrame], player.getPositionX(), player.getPositionY(), paint);
                    }
                }

                if (!noFlip){
                    /*
                    if (firstTouchCycle){
                        charPositionX = (int) Math.round((player.getBlockX() + (charBlockWidth/2)) * blockSize);
                        //firstTouchCycle = false;
                    }
                    */
                    //Flipping the character when facing to the left
                    Matrix flipHorizontalMatrix = new Matrix();
                    flipHorizontalMatrix.setScale(-1,1);
                    player.setPositionX(player.getPositionX() + (int) Math.round(player.getBlockWidth() * blockSize));
                    flipHorizontalMatrix.postTranslate(player.getPositionX(), player.getPositionY());


                    if (charMoveLeft){
                        canvas.drawBitmap(charWalkBitmapArray[charFrame], flipHorizontalMatrix, paint);
                    }
                    if (charJab) {
                        /*if (player.getBlockX() > 0) {
                            player.setBlockX(player.getBlockX() - 3);
                        }
                        */
                        canvas.drawBitmap(charJabBitmapArray[jabFrame], flipHorizontalMatrix, paint);
                    }

                    else if (!charMoveLeft){
                        canvas.drawBitmap(charBitmapArray[charFrame], flipHorizontalMatrix, paint);
                    }
                }

                canvas.drawBitmap(dpadLeftBitmap, dpadLeft.getPositionX(), dpadLeft.getPositionY(), paint);
                canvas.drawBitmap(dpadRightBitmap, dpadRight.getPositionX(), dpadRight.getPositionY(), paint);
                canvas.drawBitmap(punchButtonBitmap, punchButton.getPositionX(), punchButton.getPositionY(), paint);

                //if the button is not being pushed down
                if (noPush) {
                    canvas.drawBitmap(jumpNoPushBitmap, jumpButtonDown.getPositionX(), jumpButtonDown.getPositionY(), paint);
                }
                //if the button is being pushed down
                if (!noPush){
                    canvas.drawBitmap(jumpPushBitmap, jumpButtonDown.getPositionX(), jumpButtonDown.getPositionY(), paint);
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

            boolean pointer1Jump, pointer1Right, pointer1Left, pointer1Jab;
            pointer1Jump = pointer1Right = pointer1Left = pointer1Jab = false;
            boolean pointer2Jump, pointer2Right, pointer2Left, pointer2Jab;
            pointer2Jump = pointer2Right = pointer2Left = pointer2Jab = false;

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

                    if (generalButtonTouchEvent(motionEvent.getX(), motionEvent.getY(), jumpButtonDown.getPositionX(), jumpButtonDown.getPositionY(), jumpButtonDown.getBlockWidth(), jumpButtonDown.getBlockHeight())){
                        //making it so that you can only jump once this is hardcoded for this map specifically for now.
                        if (noGravity == true || player.getBlockY() == (ground1 - player.getBlockHeight())){
                            charMoveUp = true;
                            noPush = false;

                        }
                        else{
                            noPush = false;

                        }
                        pointer1Jump = true;
                    }
                    /*if (motionEvent.getY() <= screenHeight / 2){
                        charMoveUp = true;
                    }
                    */
                    //move right

                    if (generalButtonTouchEvent(motionEvent.getX(), motionEvent.getY(), dpadRight.getPositionX(), dpadRight.getPositionY(), dpadRight.getBlockWidth(), dpadRight.getBlockHeight())) {
                        charMoveRight = true;
                        charMoveLeft = false;
                        charJab = false;
                        pointer1Right = true;
                        //firstTouchCycle = true;

                    }
                    //move left
                    if (generalButtonTouchEvent(motionEvent.getX(), motionEvent.getY(), dpadLeft.getPositionX(), dpadLeft.getPositionY(), dpadLeft.getBlockWidth(), dpadLeft.getBlockHeight())){
                        charMoveLeft = true;
                        charMoveRight = false;
                        charJab = false;
                        pointer1Left = true;
                        //firstTouchCycle = true;

                    }
                    //Jab animation when we click the jab button
                    if (generalButtonTouchEvent(motionEvent.getX(),motionEvent.getY(),punchButton.getPositionX(),punchButton.getPositionY(), punchButton.getBlockWidth(), punchButton.getBlockWidth())){
                        charMoveRight = false;
                        charMoveLeft = false;
                        charJab = true;

                        pointer1Jab = true;

                    }

                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    int pointerIndex = motionEvent.getActionIndex();


                    if (generalButtonTouchEvent(motionEvent.getX(pointerIndex), motionEvent.getY(pointerIndex), jumpButtonDown.getPositionX(), jumpButtonDown.getPositionY(), jumpButtonDown.getBlockWidth(), jumpButtonDown.getBlockHeight())){
                        //making it so that you can only jump once this 702-3453251-5641836is hardcoded for this map specifically for now.
                        if (noGravity == true || player.getBlockY() == (ground1 - player.getBlockHeight())){
                            charMoveUp = true;
                            noPush = false;
                        }
                        else{
                            noPush = false;
                        }
                        pointer2Jump = true;
                    }
                    /*if (motionEvent.getY() <= screenHeight / 2){
                        charMoveUp = true;
                    }
                    */
                    //move right
                    if (generalButtonTouchEvent(motionEvent.getX(pointerIndex), motionEvent.getY(pointerIndex), dpadRight.getPositionX(), dpadRight.getPositionY(), dpadRight.getBlockWidth(), dpadRight.getBlockHeight())) {
                        charMoveRight = true;
                        charMoveLeft = false;
                        charJab = false;
                        pointer2Right = true;
                        //firstTouchCycle = true;

                    }
                    //move left
                    if (generalButtonTouchEvent(motionEvent.getX(pointerIndex), motionEvent.getY(pointerIndex), dpadLeft.getPositionX(), dpadLeft.getPositionY(), dpadLeft.getBlockWidth(), dpadLeft.getBlockHeight())){
                        charMoveLeft = true;
                        charMoveRight = false;
                        charJab = false;
                        pointer2Left = true;
                        //firstTouchCycle = true;

                    }
                    //Jab animation when we click the jab button
                    if (generalButtonTouchEvent(motionEvent.getX(pointerIndex), motionEvent.getY(pointerIndex),punchButton.getPositionX(),punchButton.getPositionY(), punchButton.getBlockWidth(), punchButton.getBlockWidth())){
                        charMoveRight = false;
                        charMoveLeft = false;
                        charJab = true;

                        pointer2Jab = true;

                    }

                    break;


                case MotionEvent.ACTION_UP:

                    if (!pointer1Jump) {
                        noPush = true;
                    }
                    charMoveRight = false;
                    charMoveLeft = false;

                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    if (!pointer2Jump) {
                        noPush = true;
                    }

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


        grassPlatform = new character(0,110,18,300,blockSize);
        grassPlatform2 = new character(340,145,18,60,blockSize);

        player = new playerInfo(200,170,50,30,blockSize);

        enemy1 = new character(360,0,75,33,blockSize);

        jumpButtonDown = new character(350,numBlocksHigh - mysteriousBottomGapBlock - 50,40,40,blockSize);

        dpadLeft = new character(0,numBlocksHigh - mysteriousBottomGapBlock - 50,40,40,blockSize);

        dpadRight = new character(60,numBlocksHigh - mysteriousBottomGapBlock - 50,40,40,blockSize);

        punchButton = new character(300,numBlocksHigh - mysteriousBottomGapBlock - 50,40,40,blockSize);

        enemyBottle = new character(155,40,75,45,blockSize);

        int jabWidth = player.getBlockWidth() + (int) Math.round(blockSize * 45);

        portal = new character(0,25,50,30,blockSize);

        ground1 = numBlocksHigh - 30;
        heart = new character(5,5,20, 25, blockSize);
        coin = new character(60,5,25,25,blockSize);
        spike = new character(150,100,40,50,blockSize);

        //Load bitmaps
        charBitmap0 = BitmapFactory.decodeResource(getResources(), R.drawable.idle1);
        charBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.idle2);
        charBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.idle3);
        charBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.idle4);
        charBitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.idle5);
        charBitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.idle6);
        charBitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.idle7);
        charBitmap7 = BitmapFactory.decodeResource(getResources(), R.drawable.idle8);
        charBitmap8 = BitmapFactory.decodeResource(getResources(), R.drawable.idle9);
        charBitmap9 = BitmapFactory.decodeResource(getResources(), R.drawable.idle10);
        charWalkBitmap0 = BitmapFactory.decodeResource(getResources(), R.drawable.walk_000);
        charWalkBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.walk_001);
        charWalkBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.walk_002);
        charWalkBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.walk_003);
        charWalkBitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.walk_004);
        charWalkBitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.walk_005);
        charWalkBitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.walk_006);
        charWalkBitmap7 = BitmapFactory.decodeResource(getResources(), R.drawable.walk_007);
        charWalkBitmap8 = BitmapFactory.decodeResource(getResources(), R.drawable.walk_008);
        charWalkBitmap9 = BitmapFactory.decodeResource(getResources(), R.drawable.walk_009);
        charJabBitmap0 = BitmapFactory.decodeResource(getResources(), R.drawable.jab_000);
        charJabBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.jab_001);
        charJabBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.jab_002);
        charJabBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.jab_003);
        charJabBitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.jab_004);
        charJabBitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.jab_005);
        charJabBitmap6 = BitmapFactory.decodeResource(getResources(), R.drawable.jab_006);
        charJabBitmap7 = BitmapFactory.decodeResource(getResources(), R.drawable.jab_007);
        charJabBitmap8 = BitmapFactory.decodeResource(getResources(), R.drawable.jab_008);
        charJabBitmap9 = BitmapFactory.decodeResource(getResources(), R.drawable.jab_009);
        enemyPopBottleBitmap0 = BitmapFactory.decodeResource(getResources(),R.drawable.walkbottle_000);
        enemyPopBottleBitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.walkbottle_001);
        enemyPopBottleBitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.walkbottle_002);
        enemyPopBottleBitmap3 = BitmapFactory.decodeResource(getResources(),R.drawable.walkbottle_003);
        enemyPopBottleBitmap4 = BitmapFactory.decodeResource(getResources(),R.drawable.walkbottle_004);
        enemyPopBottleBitmap5 = BitmapFactory.decodeResource(getResources(),R.drawable.walkbottle_005);
        enemyPopBottleBitmap6 = BitmapFactory.decodeResource(getResources(),R.drawable.walkbottle_006);
        enemyPopBottleBitmap7 = BitmapFactory.decodeResource(getResources(),R.drawable.walkbottle_007);
        enemyPopBottleBitmap8 = BitmapFactory.decodeResource(getResources(),R.drawable.walkbottle_008);
        enemyPopBottleBitmap9 = BitmapFactory.decodeResource(getResources(),R.drawable.walkbottle_009);
        jumpNoPushBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jumpnopush);
        jumpPushBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jumppush);
        dpadLeftBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dpadleft);
        dpadRightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dpadright);
        punchButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.punchbutton);
        portalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.portal);
        spikeBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.spike);
        grassPlatformBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.concreteplateform);
        coinBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
        heartBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.heart);


        //scale the bitmaps to match the block size
        charBitmap0 = Bitmap.createScaledBitmap(charBitmap0, player.getRoundedWidth(), player.getRoundedHeight(), false);
        charBitmap1 = Bitmap.createScaledBitmap(charBitmap1, player.getRoundedWidth(), player.getRoundedHeight(), false);
        charBitmap2 = Bitmap.createScaledBitmap(charBitmap2, player.getRoundedWidth(), player.getRoundedHeight(), false);
        charBitmap3 = Bitmap.createScaledBitmap(charBitmap3, player.getRoundedWidth(), player.getRoundedHeight(), false);
        charBitmap4 = Bitmap.createScaledBitmap(charBitmap4, player.getRoundedWidth(), player.getRoundedHeight(), false);
        charBitmap5 = Bitmap.createScaledBitmap(charBitmap5, player.getRoundedWidth(), player.getRoundedHeight(), false);
        charBitmap6 = Bitmap.createScaledBitmap(charBitmap6, player.getRoundedWidth(), player.getRoundedHeight(), false);
        charBitmap7 = Bitmap.createScaledBitmap(charBitmap7, player.getRoundedWidth(), player.getRoundedHeight(), false);
        charBitmap8 = Bitmap.createScaledBitmap(charBitmap8, player.getRoundedWidth(), player.getRoundedHeight(), false);
        charBitmap9 = Bitmap.createScaledBitmap(charBitmap9, player.getRoundedWidth(), player.getRoundedHeight(), false);
        charWalkBitmap0 = Bitmap.createScaledBitmap(charWalkBitmap0,player.getRoundedWidth(), player.getRoundedHeight(), false);
        charWalkBitmap1 = Bitmap.createScaledBitmap(charWalkBitmap1,player.getRoundedWidth(), player.getRoundedHeight(), false);
        charWalkBitmap2 = Bitmap.createScaledBitmap(charWalkBitmap2,player.getRoundedWidth(), player.getRoundedHeight(), false);
        charWalkBitmap3 = Bitmap.createScaledBitmap(charWalkBitmap3,player.getRoundedWidth(), player.getRoundedHeight(), false);
        charWalkBitmap4 = Bitmap.createScaledBitmap(charWalkBitmap4,player.getRoundedWidth(), player.getRoundedHeight(), false);
        charWalkBitmap5 = Bitmap.createScaledBitmap(charWalkBitmap5,player.getRoundedWidth(), player.getRoundedHeight(), false);
        charWalkBitmap6 = Bitmap.createScaledBitmap(charWalkBitmap6,player.getRoundedWidth(), player.getRoundedHeight(), false);
        charWalkBitmap7 = Bitmap.createScaledBitmap(charWalkBitmap7,player.getRoundedWidth(), player.getRoundedHeight(), false);
        charWalkBitmap8 = Bitmap.createScaledBitmap(charWalkBitmap8,player.getRoundedWidth(), player.getRoundedHeight(), false);
        charWalkBitmap9 = Bitmap.createScaledBitmap(charWalkBitmap9,player.getRoundedWidth(), player.getRoundedHeight(), false);
        charJabBitmap0 = Bitmap.createScaledBitmap(charJabBitmap0,jabWidth , player.getRoundedHeight(), false);
        charJabBitmap1 = Bitmap.createScaledBitmap(charJabBitmap1,jabWidth, player.getRoundedHeight(), false);
        charJabBitmap2 = Bitmap.createScaledBitmap(charJabBitmap2,jabWidth, player.getRoundedHeight(), false);
        charJabBitmap3 = Bitmap.createScaledBitmap(charJabBitmap3,jabWidth, player.getRoundedHeight(), false);
        charJabBitmap4 = Bitmap.createScaledBitmap(charJabBitmap4,jabWidth, player.getRoundedHeight(), false);
        charJabBitmap5 = Bitmap.createScaledBitmap(charJabBitmap5,jabWidth, player.getRoundedHeight(), false);
        charJabBitmap6 = Bitmap.createScaledBitmap(charJabBitmap6,jabWidth, player.getRoundedHeight(), false);
        charJabBitmap7 = Bitmap.createScaledBitmap(charJabBitmap7,jabWidth, player.getRoundedHeight(), false);
        charJabBitmap8 = Bitmap.createScaledBitmap(charJabBitmap8,jabWidth, player.getRoundedHeight(), false);
        charJabBitmap9 = Bitmap.createScaledBitmap(charJabBitmap9,jabWidth, player.getRoundedHeight(), false);
        enemyPopBottleBitmap0 = Bitmap.createScaledBitmap(enemyPopBottleBitmap0,enemyBottle.getRoundedWidth(),enemyBottle.getRoundedHeight(),false);
        enemyPopBottleBitmap1 = Bitmap.createScaledBitmap(enemyPopBottleBitmap1,enemyBottle.getRoundedWidth(),enemyBottle.getRoundedHeight(),false);
        enemyPopBottleBitmap2 = Bitmap.createScaledBitmap(enemyPopBottleBitmap2,enemyBottle.getRoundedWidth(),enemyBottle.getRoundedHeight(),false);
        enemyPopBottleBitmap3 = Bitmap.createScaledBitmap(enemyPopBottleBitmap3,enemyBottle.getRoundedWidth(),enemyBottle.getRoundedHeight(),false);
        enemyPopBottleBitmap4 = Bitmap.createScaledBitmap(enemyPopBottleBitmap4,enemyBottle.getRoundedWidth(),enemyBottle.getRoundedHeight(),false);
        enemyPopBottleBitmap5 = Bitmap.createScaledBitmap(enemyPopBottleBitmap5,enemyBottle.getRoundedWidth(),enemyBottle.getRoundedHeight(),false);
        enemyPopBottleBitmap6 = Bitmap.createScaledBitmap(enemyPopBottleBitmap6,enemyBottle.getRoundedWidth(),enemyBottle.getRoundedHeight(),false);
        enemyPopBottleBitmap7 = Bitmap.createScaledBitmap(enemyPopBottleBitmap7,enemyBottle.getRoundedWidth(),enemyBottle.getRoundedHeight(),false);
        enemyPopBottleBitmap8 = Bitmap.createScaledBitmap(enemyPopBottleBitmap8,enemyBottle.getRoundedWidth(),enemyBottle.getRoundedHeight(),false);
        enemyPopBottleBitmap9 = Bitmap.createScaledBitmap(enemyPopBottleBitmap9,enemyBottle.getRoundedWidth(),enemyBottle.getRoundedHeight(),false);
        grassPlatformBitmap = Bitmap.createScaledBitmap(grassPlatformBitmap, grassPlatform.getRoundedWidth(), grassPlatform.getRoundedHeight(), false);
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, heart.getRoundedWidth(), heart.getRoundedHeight(), false);
        coinBitmap = Bitmap.createScaledBitmap(coinBitmap, coin.getRoundedWidth(), coin.getRoundedHeight(), false);
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rainybackground);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, screenWidth, screenHeight, false);


        jumpNoPushBitmap = Bitmap.createScaledBitmap(jumpNoPushBitmap, jumpButtonDown.getRoundedWidth(), jumpButtonDown.getRoundedHeight(), false);
        jumpPushBitmap = Bitmap.createScaledBitmap(jumpPushBitmap, jumpButtonDown.getRoundedWidth(), jumpButtonDown.getRoundedHeight(), false);
        dpadLeftBitmap = Bitmap.createScaledBitmap(dpadLeftBitmap, dpadLeft.getRoundedWidth(), dpadLeft.getRoundedHeight(), false);
        dpadRightBitmap = Bitmap.createScaledBitmap(dpadRightBitmap, dpadRight.getRoundedWidth(), dpadRight.getRoundedHeight(), false);
        punchButtonBitmap = Bitmap.createScaledBitmap(punchButtonBitmap, punchButton.getRoundedWidth(), punchButton.getRoundedHeight(),false);
        portalBitmap = Bitmap.createScaledBitmap(portalBitmap, portal.getRoundedWidth(), portal.getRoundedHeight(), false);
        spikeBitmap = Bitmap.createScaledBitmap(spikeBitmap, spike.getRoundedWidth(), spike.getBlockHeight(), false);


        enemyPopBottleBitmapArray = new Bitmap[]{enemyPopBottleBitmap0,enemyPopBottleBitmap1,enemyPopBottleBitmap2,enemyPopBottleBitmap3,enemyPopBottleBitmap4,enemyPopBottleBitmap5,enemyPopBottleBitmap6,enemyPopBottleBitmap7,enemyPopBottleBitmap8,enemyPopBottleBitmap9};
        charBitmapArray = new Bitmap[]{charBitmap0, charBitmap1, charBitmap2, charBitmap3, charBitmap4, charBitmap5, charBitmap6, charBitmap7, charBitmap8, charBitmap9};
        charWalkBitmapArray = new Bitmap[]{charWalkBitmap0, charWalkBitmap1, charWalkBitmap2, charWalkBitmap3, charWalkBitmap4, charWalkBitmap5, charWalkBitmap6, charWalkBitmap7, charWalkBitmap8, charWalkBitmap9};
        charJabBitmapArray = new Bitmap[]{charJabBitmap0,charJabBitmap1,charJabBitmap2,charJabBitmap3,charJabBitmap4,charJabBitmap5,charJabBitmap6,charJabBitmap7,charJabBitmap8,charJabBitmap9};

    }
}
