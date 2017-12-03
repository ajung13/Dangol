package ac.sogang.dangol;

/**
 * Created by Hyunah on 2017-11-23.
 */

public class MyItem {
    private int id;
    private String title;
    private String contents;
    private String date;
    private String location;
    private int emotion, weather;
    private String imageAddr;

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getContent(){
        return contents;
    }
    public void setContent(String contents){
        this.contents = contents;
    }

    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public int getID(){
        return id;
    }
    public void setID(int ID){
        this.id = ID;
    }

    public String getLocation(){
        return location;
    }
    public void setLocation(String location){
        this.location = location;
    }

    public int getEmotion(){
        return emotion;
    }
    public void setEmotion(int emotion){
        this.emotion = emotion;
    }

    public int getWeather(){
        return weather;
    }
    public void setWeather(int weather){
        this.weather = weather;
    }

    public String getImageAddr(){
        return imageAddr;
    }
    public void setImageAddr(String addr){
        this.imageAddr = addr;
    }
}
