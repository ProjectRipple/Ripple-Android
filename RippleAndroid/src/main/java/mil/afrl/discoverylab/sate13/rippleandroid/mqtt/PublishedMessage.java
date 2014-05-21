package mil.afrl.discoverylab.sate13.rippleandroid.mqtt;

/**
 * Container for a message published with MQTT
 */
public class PublishedMessage {
    private String topic;
    private String payload;

    PublishedMessage(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
