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

    private double maxSpeed; // maximum speed, unit: km/h

    private double curSpeed; // current speed, unit: km/h

    private String homePlaceID; // everyday needs to go home

    private String workPlaceID; // nearly everyday needs to go work

    private Place curPlace; // current place of the car

    private String state;

    private Place destination;

    private Place finalDestination;

    public Car(String id, double capacity, double maxSpeed, String homePlaceID, String workPlaceID, Place curPlace) {
        this.id = id;
        this.capacity = capacity;
        this.power = capacity;
        this.maxSpeed = maxSpeed;
        this.curSpeed = this.maxSpeed;
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

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getCurSpeed() {
        return curSpeed;
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

    public String getState() {
        return state;
    }

    public Place getDestination() {
        return destination;
    }

    public Place getFinalDestination() {
        return finalDestination;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void setCurSpeed(double curSpeed) {
        this.curSpeed = curSpeed;
    }

    public void setCurPlace(double x,double y) {
        this.curPlace.setX(x);
        this.curPlace.setY(y);
    }

    public void setState(String state) {
        this.state=state;
    }

    public void setDestination(Place destination) {
        this.destination=destination;
    }

    public void setFinalDestination(Place finDes) {
        this.finalDestination =finDes;
    }

}
