package model;

import register.MapObjRegister;

import java.util.HashMap;

/**
 * Simulation Model is the main model of the simulation
 * @author Zhanghaoji
 * @date 2021/6/23 11:59
 * @author Zhengrundong
 * @date 2021/6/24 20:57
 */
public class SimulationModel  {
	
	  private final int RUNNING = 0;

	  private final int CHARGING = 1;

	  private final int ARRIVED = 2;

    private final double powerT = 25; //The power consumed per mile

	  private final int FULL = 3;
	
    private final double powerT = 30;//The power consumed per mile
	
    private HashMap<String, Place> placeMap = new HashMap<>(); // all places

    private HashMap<String, Car> carMap = new HashMap<>(); // all cars

    private HashMap<String, Station> stationMap = new HashMap<>(); // all stations

    private String stationText = ""; // station text to be displayed

    private String carText = ""; // car text to be displayed

    private String placeText = ""; // place text to be displayed

    private int state = RUNNING;
    
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
        str += "编号\t电量\t速度\t当前位置\t居住地\t工作地\t运行状态\n";
        for(int i = 1; carMap.containsKey(String.valueOf(i)); ++i) {
            Car car = carMap.get(String.valueOf(i));
            str += String.format("%s\t%.0f/%.0f\t%.0f\t(%.0f,%.0f)\t%s\t%s\t%s\n", car.getId(), car.getPower(), car.getCapacity(),
                    car.getSpeed(), car.getCurPlace().getX(), car.getCurPlace().getY(), car.getHomePlaceID(), car.getWorkPlaceID(),car.getState());
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
    	 for(int i = 1; carMap.containsKey(String.valueOf(i)); i++) {
             Car car = carMap.get(String.valueOf(i));
             double dis = Place.getDistanceOf(car.getCurPlace(), placeMap.get(car.getWorkPlaceID()));
             
             if(powerT*dis<=car.getPower()) {
            	 car.setState("RUNNING");
        	 	 for(int j = 1; stationMap.containsKey(String.valueOf(j)); ++j) {
        	 		 Station sta = stationMap.get(String.valueOf(j));
        	 		 sta.removeACar(car);
        	 		stationMap.replace(String.valueOf(j),sta);
        	 		 }
            	 carMovingStep(car,car.getCurPlace(), placeMap.get(car.getWorkPlaceID()));
             }
             
             else if(powerT*dis>car.getPower()&&car.getState().equals("RUNNING")) {
            	 double minDis = 40;
            	 String minId = null;
            	 for(int j = 1; stationMap.containsKey(String.valueOf(j)); ++j) {
                     Station sta = stationMap.get(String.valueOf(j));
                     Place p = sta.getPlace();
                    double disStation = Place.getDistanceOf(car.getCurPlace(), p);
                    if(!car.getState().equals("FULL")) {
                    	if(sta.addACar(car))
                    		car.setState("CHARGING");
                    }
                    if(minDis>disStation) {
                    	minDis = disStation;
                    	minId = String.valueOf(j);
                    }
                    stationMap.replace(String.valueOf(j),sta);
                 }
            	 carMovingStep(car,car.getCurPlace(), stationMap.get(minId).getPlace());
             }
             
             else if(powerT*dis>=car.getPower()&&car.getState().equals("CHARGING")) {
            	 double minDis = 40;
            	 String minId = null;
            	 for(int j = 1; stationMap.containsKey(String.valueOf(j)); ++j) {
                     Station sta = stationMap.get(String.valueOf(j));
                     Place p = sta.getPlace();
                    double disStation = Place.getDistanceOf(car.getCurPlace(), p);
                    if(!car.getState().equals("FULL")) {
                    	if(sta.addACar(car))
                    	car.setState("CHARGING");
                    }
                    if(minDis>disStation) {
                    	minDis = disStation;
                    	minId = String.valueOf(j);
                    }
                    stationMap.replace(String.valueOf(j),sta);
                 }
            	 carMovingStep(car,car.getCurPlace(), stationMap.get(minId).getPlace());
             }

             else if(powerT*dis>=car.getPower()&&car.getState().equals("FULL")) {
            	 double minDis = 40;
            	 String minId = null;
            	 for(int j = 1; stationMap.containsKey(String.valueOf(j)); ++j) {
                     Station sta = stationMap.get(String.valueOf(j));
                     Place p = sta.getPlace();
                    double disStation = Place.getDistanceOf(car.getCurPlace(), p);
                    if(!car.getState().equals("FULL")) {
                    	if(sta.addACar(car))
                    		car.setState("CHARGING");
                    }
                    if(minDis>disStation) {
                    	minDis = disStation;
                    	minId = String.valueOf(j);
                    }
                    stationMap.replace(String.valueOf(j),sta);
                 }
            	 carMovingStep(car,car.getCurPlace(), stationMap.get(minId).getPlace());
             }
             carMap.replace(String.valueOf(i), car);
             updateCarText();
             updateStationText();
         }
    }

    public void carMovingStep(Car car,Place present,Place destination,int state) {
        if(state==RUNNING) {
             if(car.getCurPlace().getX()<placeMap.get(car.getWorkPlaceID()).getX()) {
                 car.setCurPlace(car.getCurPlace().getX()+1,car.getCurPlace().getY());
                 car.setPower(car.getPower()-powerT);
             } else if(car.getCurPlace().getX()>placeMap.get(car.getWorkPlaceID()).getX()) {
                 car.setCurPlace(car.getCurPlace().getX()-1,car.getCurPlace().getY());
                 car.setPower(car.getPower()-powerT);
             } else if(car.getCurPlace().getY()<placeMap.get(car.getWorkPlaceID()).getY()){
                 car.setCurPlace(car.getCurPlace().getX(),car.getCurPlace().getY()+1);
                 car.setPower(car.getPower()-powerT);
             } else if(car.getCurPlace().getY()>placeMap.get(car.getWorkPlaceID()).getY()){
                 car.setCurPlace(car.getCurPlace().getX(),car.getCurPlace().getY()-1);
                 car.setPower(car.getPower()-powerT);
             } else
                 car.setSpeed(0);
        }  else if(state==CHARGING){
             car.setSpeed(0);
             car.setPower(car.getPower()+powerT);
        }
    
    public void carMovingStep(Car car,Place present,Place destination) {
        if( car.getState().equals("RUNNING")) {
            if(car.getCurPlace().getX()<placeMap.get(car.getWorkPlaceID()).getX()) {
                car.setCurPlace(car.getCurPlace().getX()+1,car.getCurPlace().getY());
                car.setPower(car.getPower()-powerT);
            } else if(car.getCurPlace().getX()>placeMap.get(car.getWorkPlaceID()).getX()) {
                car.setCurPlace(car.getCurPlace().getX()-1,car.getCurPlace().getY());
                car.setPower(car.getPower()-powerT);
            } else if(car.getCurPlace().getY()<placeMap.get(car.getWorkPlaceID()).getY()){
                car.setCurPlace(car.getCurPlace().getX(),car.getCurPlace().getY()+1);
                car.setPower(car.getPower()-powerT);
            } else if(car.getCurPlace().getY()>placeMap.get(car.getWorkPlaceID()).getY()){
                car.setCurPlace(car.getCurPlace().getX(),car.getCurPlace().getY()-1);
                car.setPower(car.getPower()-powerT);
            } else {
                car.setSpeed(0);
                car.setState("ARRIVED");
            }
		    } else if(car.getState().equals("CHARGING")){
            car.setSpeed(0);
            if(car.getPower()+powerT<car.getCapacity())
                car.setPower(car.getPower()+powerT);
            else {
                car.setState("FULL");
                for(int j = 1; stationMap.containsKey(String.valueOf(j)); j++) {
                    Station sta = stationMap.get(String.valueOf(j));
                    sta.removeACar(car);
                    stationMap.replace(String.valueOf(j),sta);
                }
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
