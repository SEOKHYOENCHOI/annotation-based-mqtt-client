package shchoi.mqtt.argumentresolver;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.messaging.Message;

import java.lang.reflect.Parameter;

public class MqttMessageArgumentResolver implements MqttArgumentResolver {
    private final MqttMessageConverter messageConverter;

    public MqttMessageArgumentResolver(MqttMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.getType().isAssignableFrom(MqttMessage.class);
    }

    @Override
    public Object resolve(Parameter parameter, Message<?> message, String variableTopic) {
        return messageConverter.fromMessage(message, null);
    }
}
