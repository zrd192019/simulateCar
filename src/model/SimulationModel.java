package model;

import register.MapObjRegister;

import java.util.HashMap;

/**
 * Simulation Model is the main model of the simulation
 * @author Zhanghaoji
 * @date 2021.06.2021/6/23 11:59
 * @author Zhengrundong
 * @date 2021.06.2021/6/24 20:57
 */
public class SimulationModel  {

    private final int RUNNING = 0;

    private final int CHARGING = 1;

    private final int ARRIVED = 2;

    private final int FULL = 3;

    private HashMap<String, Place> placeMap = new HashMap<>(); // all places

    private HashMap<String, Car> carMap = new HashMap<>(); // all cars

    private HashMap<String, Station> stationMap = new HashMap<>(); // all stations

    private String stationText = ""; // station text to be displayed

    private String carText = ""; // car text to be displayed

    private String placeText = ""; // place text to be displayed

    private void updateStationText() {
        String str = "充电站信息\n";
        str += "编号\t充电桩数量\t充电速度\t所在位置\n";
        for(int i = 1; stationMap.containsKey(String.valueOf(i)); ++i) {
            Station sta = stationMap.get(String.valueOf(i));
            Place p = sta.getPlace();
            str += String.format("%s\t%d/%d\t%.2f\t(%.0f,%.0f)\n", sta.getId(), sta.getLineSize(), sta.getNumber(), sta.getChargingSpeed(), p.getX(), p.getY());
        }
        stationText = str;
    }

    public void updateCarText() {
        String str = "电动汽车信息\n";
        str += "编号\t电量\t速度\t当前位置\t居住地\t工作地\t运行状态\t当前目的地\t最终目的地\n";
        for(int i = 1; carMap.containsKey(String.valueOf(i)); ++i) {
            Car car = carMap.get(String.valueOf(i));
            str += String.format("%s\t%.0f/%.0f\t%.0f\t(%.0f,%.0f)\t%s\t%s\t%s\t(%.0f,%.0f)\t(%.0f,%.0f)\n", car.getId(), car.getPower(), car.getCapacity(),
                    car.getCurSpeed(), car.getCurPlace().getX(), car.getCurPlace().getY(), car.getHomePlaceID(), car.getWorkPlaceID(),car.getState(), car.getDestination().getX(), car.getDestination().getY(), car.getFinalDestination().getX(), car.getFinalDestination().getY());
        }
        carText = str;
    }

    public void updatePlaceText() {
        String str = "区域信息\n";
        str += "编号\t区域类型\t区域位置\n";
        for(int i = 1; placeMap.containsKey(String.valueOf(i)); ++i) {
            Place p = placeMap.get(String.valueOf(i));
            String typ = "";
            switch (p.getType()) {
                case "living": typ = "生活区"; break;
                case "industrial": typ = "工业区"; break;
                case "business": typ = "商业区"; break;
            }
            str += String.format("%s\t%s\t(%.0f,%.0f)\n", p.getId(), typ, p.getX(), p.getY());
        }
        placeText = str;
    }

    public SimulationModel(MapObjRegister register) {
        /**
         * place data
         */
        HashMap<String, Object> hMap = register.getRegisteredDataObjs("model.Place");
        for(Object d: hMap.values()) {
            if(d == null)
                continue;
            HashMap<String, Object> dMap = (HashMap<String, Object>)d;
            Place place = new Place((String)dMap.get("id"), (String)dMap.get("type"), Integer.parseInt((String)dMap.get("X-coordinate")), Integer.parseInt((String)dMap.get("Y-coordinate")));
            placeMap.put(place.getId(), place);
        }
        /**
         * car data
         */
        hMap = register.getRegisteredDataObjs("model.Car");
        for(Object d: hMap.values()) {
            if(d == null)
                continue;
            HashMap<String, Object> dMap = (HashMap<String, Object>)d;
            String hID = (String)dMap.get("homePlaceID");
            Place hplace = new Place(placeMap.get(hID).getId(),placeMap.get(hID).getType(),placeMap.get(hID).getX(),placeMap.get(hID).getY());
            Car car = new Car((String)dMap.get("id"), Double.parseDouble((String)dMap.get("capacity")), Double.parseDouble((String)dMap.get("speed")),
                    hID, (String)dMap.get("workPlaceID"), hplace);
            car.setDestination(placeMap.get(car.getWorkPlaceID()));
            car.setFinalDestination(placeMap.get(car.getWorkPlaceID()));
            carMap.put(car.getId(), car);
        }
        /**
         * station data
         */
        hMap = register.getRegisteredDataObjs("model.Station");
        for(Object d: hMap.values()) {
            if(d == null)
                continue;
            HashMap<String, Object> dMap = (HashMap<String, Object>)d;
            Station sta = new Station((String)dMap.get("id"), Integer.parseInt((String)dMap.get("number")), Double.parseDouble((String)dMap.get("chargingSpeed")),
                    placeMap.get((String)dMap.get("placeID")));
            stationMap.put(sta.getId(), sta);
        }

        updatePlaceText();
        updateStationText();
        updateCarText();
    }

    /**
     * simulate data
     */
    public void simulate() {
        HashMap<String, Station> stationMap2 = (HashMap<String, Station>) stationMap.clone();
        for(int i = 1; carMap.containsKey(String.valueOf(i)); ++i) {
            Car car = carMap.get(String.valueOf(i));
            double dis = Place.getDistanceOf(car.getCurPlace(), car.getFinalDestination());
            double maxDis = 0;

            for(int j = 1; stationMap2.containsKey(String.valueOf(j)); ++j) {
                Station sta = stationMap2.get(String.valueOf(j));
                Place p = sta.getPlace();
                double disS = Place.getDistanceOf(p,car.getFinalDestination());
                if(maxDis < disS) {
                    maxDis = disS;
                }
            }

            Place curPlace = car.getCurPlace();
            Place finalDestination = car.getFinalDestination();
            if(car.getPower() >= maxDis + dis && car.getState().equals("RUNNING")) {
                car.setDestination(finalDestination);
                carMovingStep(car, curPlace, finalDestination);
            } else if(car.getState().equals("RUNNING")) {
                double minDis = 100;
                Place destination = null;
                for(int j = 1; stationMap2.containsKey(String.valueOf(j)); ++j) {
                    Station sta = stationMap.get(String.valueOf(j));
                    Place p = sta.getPlace();
                    double disS = Place.getDistanceOf(curPlace, finalDestination) - Place.getDistanceOf(p, finalDestination);
                    if(p.getX() == car.getDestination().getX() && p.getY() == car.getDestination().getY() && disS >= 0 && sta.getLineSize() < sta.getNumber()) {
                        destination = p;
                        break;
                    }
                }
                if(destination != null) {
                    car.setDestination(destination);
                } else {
                    for(int j = 1; stationMap2.containsKey(String.valueOf(j)); ++j) {
                        Station sta = stationMap.get(String.valueOf(j));
                        Place p = sta.getPlace();
                        double disTemp = Place.getDistanceOf(curPlace, p);
                        double disS = Place.getDistanceOf(curPlace, finalDestination) - Place.getDistanceOf(p, finalDestination);
                        if(disS >= 0 && sta.getLineSize() < sta.getNumber() && disTemp <= car.getPower()) {
                            car.setDestination(p);
                            break;
                        } else if(disTemp < car.getPower() && sta.getLineSize() < sta.getNumber() && disTemp < minDis) {
                            car.setDestination(p);
                            minDis = disTemp;
                        }
                    }
                }
                carMovingStep(car,car.getCurPlace(), car.getDestination());
            } else {
                carMovingStep(car,car.getCurPlace(), car.getDestination());
            }
            carMap.replace(String.valueOf(i), car);
        }
        updateCarText();
        updateStationText();
    }

    public void carMovingStep(Car car,Place present,Place destination) {
        if(car.getState().equals("RUNNING")) {
            double speed = car.getMaxSpeed();
            car.setCurSpeed(speed);
            if(car.getCurPlace().getX() < car.getDestination().getX()) {
                double dis = Math.min(speed, car.getDestination().getX() - car.getCurPlace().getX());
                dis = Math.min(dis, car.getPower());
                car.setCurPlace(car.getCurPlace().getX() + dis,car.getCurPlace().getY());
                car.setPower(car.getPower() - dis);
            } else if(car.getCurPlace().getX() > car.getDestination().getX()) {
                double dis = Math.min(speed, car.getCurPlace().getX() - car.getDestination().getX());
                dis = Math.min(dis, car.getPower());
                car.setCurPlace(car.getCurPlace().getX() - dis,car.getCurPlace().getY());
                car.setPower(car.getPower() - dis);
            } else if(car.getCurPlace().getY() < car.getDestination().getY()){
                double dis = Math.min(speed, car.getDestination().getY() - car.getCurPlace().getY());
                dis = Math.min(dis, car.getPower());
                car.setCurPlace(car.getCurPlace().getX(),car.getCurPlace().getY() + dis);
                car.setPower(car.getPower() - dis);
            } else if(car.getCurPlace().getY() > car.getDestination().getY()){
                double dis = Math.min(speed, car.getCurPlace().getY() - car.getDestination().getY());
                dis = Math.min(dis, car.getPower());
                car.setCurPlace(car.getCurPlace().getX(),car.getCurPlace().getY() - dis);
                car.setPower(car.getPower() - dis);
            }
            if(car.getCurPlace().getX() == car.getDestination().getX() && car.getCurPlace().getY() == car.getDestination().getY()) {
                car.setCurSpeed(0);
                car.setState("ARRIVED");
                if(car.getPower() < 200) {
                    for (int j = 1; stationMap.containsKey(String.valueOf(j)); ++j) {
                        Station sta = stationMap.get(String.valueOf(j));
                        if (sta.addACar(car)) {
                            if (sta.getLineSize() <= sta.getNumber())
                                car.setState("CHARGING");
                            else {
                                sta.removeACar(car);
                                car.setState("RUNNING");
                            }
                        }
                    }
                }
            }
        } else if(car.getState().equals("CHARGING")){
            car.setCurSpeed(0);
            double chargingSpeed = 0.0;
            Station nowStation = null;
            for(int j = 1; stationMap.containsKey(String.valueOf(j)); ++j) {
                Station sta = stationMap.get(String.valueOf(j));
                if(sta.hasACar(car)) {
                    chargingSpeed = sta.getChargingSpeed();
                    nowStation = sta;
                    break;
                }
            }
            if(chargingSpeed == 0.0) {
                car.setState("RUNNING");
            } else if(car.getPower() + chargingSpeed < car.getCapacity()) {
                car.setPower(car.getPower() + chargingSpeed);
            } else {
                car.setPower(car.getCapacity());
                car.setState("RUNNING");
                car.setDestination(car.getFinalDestination());
                if(car.getCurPlace().getX() == car.getFinalDestination().getX() && car.getCurPlace().getY() == car.getFinalDestination().getY()) {
                    car.setState("ARRIVED");
                }
                boolean b = nowStation.removeACar(car);
                System.out.println("b = " + b);
            }
        } else if(car.getState().equals("ARRIVED")) {
            car.setCurSpeed(0);
            car.setState("RUNNING");
            if(car.getCurPlace().getX() == placeMap.get(car.getWorkPlaceID()).getX() && car.getCurPlace().getY() == placeMap.get(car.getWorkPlaceID()).getY()){
                car.setDestination(placeMap.get(car.getHomePlaceID()));
                car.setFinalDestination(placeMap.get(car.getHomePlaceID()));
            } else {
                car.setDestination(placeMap.get(car.getWorkPlaceID()));
                car.setFinalDestination(placeMap.get(car.getWorkPlaceID()));
            }
        }
    }

    public HashMap<String, Place> getPlaceMap() {
        return placeMap;
    }

    public String getStationText() {
        return stationText;
    }

    public String getCarText() {
        return carText;
    }

    public String getPlaceText() {
        return placeText;
    }
}
