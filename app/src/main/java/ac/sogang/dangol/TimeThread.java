package ac.sogang.dangol;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Hyunah on 2017-12-11.
 */

public class TimeThread extends Service {
    private LocationManager manager = null;
    @Override
    public void onStart(Intent intent, int startId){
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
/*        String dbName = "Dangol";
        long sleepTime = 180000;
//            long sleepTime = 10000;
        long minTime = 5000;
        float minDistance = 10;

        SQLiteDatabase mDB;
        String nowDateTime = "";

        try {
            String sql = "";
            //제일 처음 위치를 받아옴 (초기화)
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, MainActivity.gpsListener );
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);

            sleep(2000);
            Location location1 = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location1 == null)   location1 = lastlocation;
            Double latitude = location1.getLatitude();
            Double longitude = location1.getLongitude();
            nowDateTime = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(System.currentTimeMillis());

            int count = 0;
            while(true) {
                //잠을 재운다
                sleep(sleepTime);

                //3분 후 값을 읽어온다
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, gpsListener);
                Location location2 = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location2 == null) location2 = lastlocation;
                if (location2 == null) break;
                String nowDateTime2 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(System.currentTimeMillis());

                if(count != 7){
                    if (location1.distanceTo(location2) <= 10) {
                        // Location Class에 존재하는 distanceTo 함수, 두 지점 사이의 거리를 Meter 단위로 반환, 만'약 두 지점 사이가 10m 이하이면 count++
                        count++;
                        String str = location2.getLatitude() + ", " + location2.getLongitude() + ", ";
                        Log.e("dangol_task_check_data", str + Integer.toString(count));
                    }
                    else {
                        // 데이터 리셋, 위치 재설정
                        count = 0;
                        location1 = location2;
                        latitude = location2.getLatitude();
                        longitude = location2.getLongitude();
                        nowDateTime = nowDateTime2;
                    }
                }
                else {

                    // 유효한 데이터일 경우 데이터 저장
                    mDB = context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
                    sql = "INSERT INTO realData(Latitude, Longitude, Time) VALUES (" + latitude + ", " + longitude + ", '" + nowDateTime + "');";
                    mDB.execSQL(sql);
                    Log.e("dangol_task", sql);
                    mDB.close();
                }
            }
        }catch(SecurityException se){
            Log.e("dangol_task", "se: " + se.toString());
        }catch(InterruptedException ie){
            Log.e("dangol_task", "ie: " + ie.toString());
        }catch(SQLiteException sqe){
            Log.e("dangol_task", "sqe: " + sqe.toString());
        }catch(Exception e){
            Log.e("dangol_task", e.toString());
        }
        Log.e("dangol_main", "thread dead at " + nowDateTime);*/
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onDestroy(){
        Log.e("dangol_service", "service done");
    }
}
