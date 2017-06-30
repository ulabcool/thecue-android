package usabilla.thecue.beacons;

public class CloudBeaconDetails {

    private String beaconName;

    public CloudBeaconDetails(String beaconName) {
        this.beaconName = beaconName;
    }

    public String getBeaconName() {
        return beaconName;
    }

    @Override
    public String toString() {
        return "[beaconName: " + getBeaconName()  + "]";
    }
}
