package ac.sogang.dangol;
import android.app.Application;

/**
 * Created by Hyunah on 2017-11-25.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        registerActivityLifecycleCallbacks(new ac.sogang.dangol.dangolApp());
    }
}
