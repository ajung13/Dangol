package ac.sogang.dangol;

/**
 * Created by Hyunah on 2017-11-23.
 */

public class MyItem {
    private int id;
    private String title;
    private String contents;
    private String date;

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
}
