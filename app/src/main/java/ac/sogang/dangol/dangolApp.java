package ac.sogang.dangol;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by Hyunah on 2017-11-25.
 */

public class dangolApp implements Application.ActivityLifecycleCallbacks {
    // I use four separate variables here. You can, of course, just use two and
    // increment/decrement them instead of using four and incrementing them all.
    private int resumed;
    private int paused;
    private int started;
    private int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//        Log.e("dangol_thread", "created");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //Log.e("dangol_thread", "destroyed");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //Log.e("dangol_thread", "resumed");
        ++resumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        android.util.Log.w("test", "application is in foreground: " + (resumed > paused));
        //Log.e("dangol_thread", "paused");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        //Log.e("dangol_thread", "started");
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        android.util.Log.w("test", "application is visible: " + (started > stopped));
        //Log.e("dangol_thread", "stopped");
    }

    // If you want a static function you can use to check if your application is
    // foreground/background, you can use the following:
    /*
    // Replace the four variables above with these four
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    // And these two public static functions
    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }
    */
}
