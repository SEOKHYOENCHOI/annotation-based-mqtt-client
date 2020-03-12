package shchoi.mqtt.argumentresolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.messaging.Message;
import shchoi.mqtt.InvocationFailedException;

import java.lang.reflect.Parameter;

public class MappingPayloadArgumentResolver implements MqttArgumentResolver {
    private final ObjectMapper objectMapper;

    public MappingPayloadArgumentResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return !parameter.getType().isAssignableFrom(String.class) && !parameter.getType().isAssignableFrom(MqttMessage.class);
    }

    @Override
    public Object resolve(Parameter parameter, Message<?> message, String variableTopic) {
        try {
            return objectMapper.readValue(message.getPayload().toString(), parameter.getType());
        } catch (JsonProcessingException e) {
            throw new InvocationFailedException("Message Conversion Fail. " + e.getMessage()
                    + ", Message: " + message
                    + ", Parameter: " + parameter);
        }
    }
}
