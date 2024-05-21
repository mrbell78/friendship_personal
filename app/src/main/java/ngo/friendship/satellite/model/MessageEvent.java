package ngo.friendship.satellite.model;

public class MessageEvent {
    private String busMsg;

    public MessageEvent(String busMsg) {
        this.busMsg = busMsg;
    }

    public String getBusMsg() {
        return busMsg;
    }

    public void setBusMsg(String busMsg) {
        this.busMsg = busMsg;
    }


}
