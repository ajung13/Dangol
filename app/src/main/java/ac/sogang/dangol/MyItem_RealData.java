package ac.sogang.dangol;

/**
 * Created by serin on 2017-12-01.
 */

public class MyItem_RealData {
    private String date;
    private String time;
    private Double latitude;
    private Double longitude;
    
    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time = time;
    }

    public Double getLatitude(){
        return latitude;
    }
    public void setLatitude(Double latitude){
        this.latitude = latitude;
    }

    public Double getLongitude(){
        return longitude;
    }
    public void setLongitude(Double Longitude){
        this.longitude = longitude;
    }
}
