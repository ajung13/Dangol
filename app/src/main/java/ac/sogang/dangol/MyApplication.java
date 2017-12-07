package ac.sogang.dangol;
import android.app.Application;
import com.tsengvn.typekit.Typekit;

/**
 * Created by Hyunah on 2017-11-25.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        registerActivityLifecycleCallbacks(new ac.sogang.dangol.dangolApp());

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "fonts/NanumSquareRoundR.ttf"))
                .addBold(Typekit.createFromAsset(this, "fonts/NanumSquareRoundB.ttf"));
    }
}
