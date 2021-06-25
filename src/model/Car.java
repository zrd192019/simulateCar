package model;

/**
 * Car is short for "Electric Vehicle"
 * @author Zhanghaoji
 * @date 2021.06.2021/6/21 17:32
 * @author Zhengrundong
 * @date 2021.06.2021/6/24 16:22
 */
public class Car {

    private String id;

    private double capacity; // capacity of power, unit: km

    private double power; // remaining power, unit: km

    private double speed; // speed of driving, unit: km/h

    private String homePlaceID; // everyday needs to go home

    private String workPlaceID; // nearly everyday needs to go work

    private Place curPlace; // current place of the car
    
    private String state;

    public Car(String id, double capacity, double speed, String homePlaceID, String workPlaceID, Place curPlace) {
        this.id = id;
        this.capacity = capacity;
        this.power = capacity;
        this.speed = speed;
        this.homePlaceID = homePlaceID;
        this.workPlaceID = workPlaceID;
        this.curPlace = curPlace;
        this.state="RUNNING";
    }

    public String getId() {
        return id;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getPower() {
        return power;
    }

    public double getSpeed() {
        return speed;
    }

    public String  getHomePlaceID() {
        return homePlaceID;
    }

    public String getWorkPlaceID() {
        return workPlaceID;
    }

    public Place getCurPlace() {
        return curPlace;
    }
    public void setCurPlace(double x,double y) {
    	this.curPlace.setX(x);
    	this.curPlace.setY(y);
    }
    public void setPower(double power) {
    	this.power = power;
    }
    public void setSpeed(double speed) {
    	this.speed = speed;
    }
    public void setState(String state) {
    	this.state=state;
    }
    public String getState() {
    	return state;
    }
}
