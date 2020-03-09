package shchoi.mqtt.argumentresolver;

import org.springframework.messaging.Message;
import shchoi.mqtt.annotation.TopicVariable;

import java.lang.reflect.Parameter;

public class RawPayloadArgumentResolver implements MqttArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.getType().isAssignableFrom(String.class) && !parameter.isAnnotationPresent(TopicVariable.class);
    }

    @Override
    public Object resolve(Parameter parameter, Message<?> message, String variableTopic) {
        return message.getPayload();
    }
}
