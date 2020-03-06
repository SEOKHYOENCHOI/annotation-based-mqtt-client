package shchoi.mqtt;

import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class MqttSender {
    private static final int DEFAULT_QOS = 1;
    private static final boolean DEFAULT_RETAINED = false;
    private static final boolean PUBLISH_TOPIC_WILDCARD_ALLOWED = false;

    private final MqttPahoMessageHandler messageHandler;

    public MqttSender(MqttPahoMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public <T> void sendMessage(String topic, T payload) {
        sendMessage(topic, payload, DEFAULT_QOS, DEFAULT_RETAINED);
    }

    public <T> void sendMessage(String topic, T payload, int qos) {
        sendMessage(topic, payload, qos, DEFAULT_RETAINED);
    }

    public <T> void sendMessage(String topic, T payload, boolean retained) {
        sendMessage(topic, payload, DEFAULT_QOS, retained);
    }

    public <T> void sendMessage(String topic, T payload, int qos, boolean retained) {
        MqttTopic.validate(topic, PUBLISH_TOPIC_WILDCARD_ALLOWED);

        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setHeader(MqttHeaders.TOPIC, topic);
        accessor.setHeader(MqttHeaders.QOS, qos);
        accessor.setHeader(MqttHeaders.RETAINED, retained);

        Message message = MessageMapper.mapToMessage(payload, accessor.toMessageHeaders());

        messageHandler.handleMessage(message);
    }
}
