package shchoi.mqtt.argumentresolver;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.messaging.Message;
import shchoi.mqtt.MessageMapper;

import java.lang.reflect.Parameter;

public class MappingPayloadArgumentResolver implements MqttArgumentResolver {
    private final MessageMapper messageMapper;

    public MappingPayloadArgumentResolver(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return !parameter.getType().isAssignableFrom(String.class) && !parameter.getType().isAssignableFrom(MqttMessage.class);
    }

    @Override
    public Object resolve(Parameter parameter, Message<?> message, String variableTopic) {
        return messageMapper.mapFromMessage(message, parameter.getType());
    }
}
