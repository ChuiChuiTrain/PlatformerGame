package com.strobertchs.comscifinalproject;

/**
 * Created by Charles on 2017-01-25.
 */

public class playerInfo extends object {
    private int health;
    private int speed;
    private boolean weapon;

    public playerInfo(int blockX, int blockY, int height, int width, double blockSize){
        super(blockX,blockY,height,width,blockSize);
    }

    public int getHealth(){
        return health;
    }
    public int getSpeed(){
        return speed;
    }
    public void setHealth(int health){
        this.health = health;
    }
    public void setSpeed(int speed){
        this.speed = speed;
    }

    public void deductHealth(){
        this.health--;
    }
    public void addHealth(){
        this.health++;
    }
    public void increaseSpeed(){
        this.speed++;
    }

}
