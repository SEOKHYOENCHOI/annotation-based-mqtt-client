package shchoi.mqtt.util;

import org.eclipse.paho.client.mqttv3.MqttTopic;

public final class VariableTopicUtils {
    private VariableTopicUtils() {
    }

    public static String toWildcardTopic(String variableTopic) {
        return variableTopic.replaceAll("\\{[a-zA-Z0-9]*\\}", "+");
    }

    public static String getTopicVariable(
            String variableName,
            String variableTopic,
            String publishedTopic) {

        String[] topicLevels = splitByTopicLevelSeparator(variableTopic);
        String[] publishedTopicLevels = splitByTopicLevelSeparator(publishedTopic);

        return getTopicVariable(variableName, topicLevels, publishedTopicLevels);
    }

    public static String getTopicVariable(
            String variableName,
            String[] topicLevels,
            String[] publishedTopicLevels) {

        for (int i = 0; i < topicLevels.length; i++) {
            if (("{" + variableName + "}").equals(topicLevels[i])) {
                return publishedTopicLevels[i];
            }
        }

        throw new IllegalArgumentException("Not found variable '" + variableName + "' on topic path.");
    }

    public static String[] splitByTopicLevelSeparator(String topic) {
        return topic.split(MqttTopic.TOPIC_LEVEL_SEPARATOR);
    }
}
