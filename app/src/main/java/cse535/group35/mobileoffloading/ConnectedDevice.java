package cse535.group35.mobileoffloading;

public class ConnectedDevice {
    public ConnectedDevice(String endpointId){
        setEndpointId(endpointId);
    }
    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public int getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(int deviceState) {
        this.deviceState = deviceState;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public DeviceLocation getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(DeviceLocation deviceLocation) {
        this.deviceLocation = deviceLocation;
    }

    public int getBatteryState() {
        return batteryState;
    }

    public void setBatteryState(int batteryState) {
        this.batteryState = batteryState;
    }

    public class DeviceLocation{
        private double latitude;
        private double longitude;
    }
    private String endpointId;
    private int deviceState;
    private boolean isBusy;
    private DeviceLocation deviceLocation;
    private int batteryState;




}
