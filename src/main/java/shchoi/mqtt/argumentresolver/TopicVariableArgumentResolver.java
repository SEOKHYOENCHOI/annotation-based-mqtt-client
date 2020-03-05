package shchoi.mqtt.argumentresolver;

import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import shchoi.mqtt.annotation.TopicVariable;

import java.lang.reflect.Parameter;
import java.util.Arrays;

public class TopicVariableArgumentResolver implements MqttArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(TopicVariable.class);
    }

    @Override
    public Object resolve(Parameter parameter, Message message, String topic) {
        String publishedTopic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);

        TopicVariable topicVariable = parameter.getAnnotation(TopicVariable.class);
        int topicIndex = topicVariable.index();

        String[] topicLevels = splitByTopicLevelSeparator(topic);
        String[] publishedTopicLevels = splitByTopicLevelSeparator(publishedTopic);

        validateTopicIndex(topicIndex, topicLevels);

        return getTopicVariable(topicIndex, topicLevels, publishedTopicLevels);
    }

    private String[] splitByTopicLevelSeparator(String topic) {
        return topic.split(MqttTopic.TOPIC_LEVEL_SEPARATOR);
    }

    private void validateTopicIndex(int index, String[] topicLevels) {
        long wildcardCount = Arrays.stream(topicLevels)
                .filter(MqttTopic.SINGLE_LEVEL_WILDCARD::equals)
                .count();

        if (index >= wildcardCount) {
            throw new IllegalArgumentException("Topic Index가 Wildcard 수 보다 큽니다.");
        }
    }

    private String getTopicVariable(int topicIndex, String[] topicLevels, String[] publishedTopicLevels) {
        int matchCount = 0;

        for (int i = 0; i < topicLevels.length; i++) {
            if (MqttTopic.SINGLE_LEVEL_WILDCARD.equals(topicLevels[i])) {
                matchCount++;
            }

            if (topicIndex < matchCount) {
                return publishedTopicLevels[i];
            }
        }

        throw new IllegalArgumentException("해당하는 Topic Variable을 찾을 수 없습니다.");
    }
}
