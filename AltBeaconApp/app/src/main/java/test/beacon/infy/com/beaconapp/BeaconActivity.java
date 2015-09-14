package test.beacon.infy.com.beaconapp;

import android.app.Activity;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;


public class BeaconActivity extends Activity  implements BeaconConsumer{

    TextView textView =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        textView = (TextView) findViewById(R.id.dispTxt);
        textView.setText("Showing distances");
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.bind(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    private static final String TAG = "BeaconsEveryWhere";

    private BeaconManager beaconManager;
    @Override
    public void onBeaconServiceConnect() {

        final Region region = new Region("myBeacons", Identifier.parse("3B677699-DD5C-44DC-9502-FE30A3C6BF2F"),null,null);
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(TAG,"didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG,"didExitRegion");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                for(Beacon iBeacon : beacons){
                      String line = "Distance : "+ iBeacon.getDistance()+" Major: " + iBeacon.getId2()
                            + "Minor: " + iBeacon.getId3()
                             + "BluetoothAddress: "
                            + iBeacon.getBluetoothAddress()
                             + "Rssi: "
                            + iBeacon.getRssi() + "TxPower: "
                            + iBeacon.getTxPower();
                    toastToDisplay(line);

                }

            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toastToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                //Toast.makeText(YourActivity.this, "" + line,
                // Toast.LENGTH_LONG).show();
                TextView yourTextView = (TextView) findViewById(R.id.dispTxt);
                yourTextView.setText(line);
            }
        });
    }
}
