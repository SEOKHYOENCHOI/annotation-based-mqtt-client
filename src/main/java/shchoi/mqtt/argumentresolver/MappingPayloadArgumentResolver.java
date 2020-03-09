package shchoi.mqtt.argumentresolver;

import org.springframework.messaging.Message;
import shchoi.mqtt.MessageMapper;

import java.lang.reflect.Parameter;

public class MappingPayloadArgumentResolver implements MqttArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return !parameter.getType().isAssignableFrom(String.class);
    }

    @Override
    public Object resolve(Parameter parameter, Message<?> message, String variableTopic) {
        return MessageMapper.mapFromMessage(message, parameter.getType());
    }
}
