package shchoi.mqtt.argumentresolver;

import org.springframework.messaging.Message;

import java.lang.reflect.Parameter;

public interface MqttArgumentResolver {
    boolean supports(Parameter parameter);

    Object resolve(Parameter parameter, Message<?> message, String variableTopic);
}
