package usabilla.thecue.beacons;

import android.util.Log;

import java.util.Arrays;

public class BeaconManager {
    private String TAG = "BeaconManager";

    private ProximityContentManager proximityContentManager;

    BeaconManager() {
        proximityContentManager = new ProximityContentManager(this,
                Arrays.asList(
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 55959, 45134)),
                new EstimoteCloudBeaconDetailsFactory());
        proximityContentManager.setListener(new ProximityContentManager.Listener() {
            @Override
            public void onContentChanged(Object content) {
                if (content != null) {
                    CloudBeaconDetails beaconDetails = (CloudBeaconDetails) content;
                    Log.i(TAG, "You're in " + beaconDetails.getBeaconName() + "'s range!");
                } else {
                    Log.i(TAG, "No beacons in range.");
                }
            }
        });
    }
}
