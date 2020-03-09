package shchoi.mqtt.argumentresolver;

import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import shchoi.mqtt.annotation.TopicVariable;
import shchoi.mqtt.util.VariableTopicUtils;

import java.lang.reflect.Parameter;
import java.util.Objects;

public class TopicVariableArgumentResolver implements MqttArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(TopicVariable.class);
    }

    @Override
    public Object resolve(Parameter parameter, Message<?> message, String variableTopic) {
        String publishedTopic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        Objects.requireNonNull(publishedTopic, "received topic is null");

        TopicVariable topicVariable = parameter.getAnnotation(TopicVariable.class);
        String variableName = topicVariable.name();

        return VariableTopicUtils.getTopicVariable(variableName, variableTopic, publishedTopic);
    }
}
