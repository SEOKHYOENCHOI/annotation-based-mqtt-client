package shchoi.mqtt;

import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MqttListenerRegistry {
    private final List<MqttListenerInvoker> registry = new ArrayList<>();

    public void register(String topic, String variableTopic, Object object, Method method) {
        MqttTopic.validate(topic, true);

        MqttListenerInvoker invoker = new MqttListenerInvoker(topic, variableTopic, object, method);
        registry.add(invoker);
    }

    public List<MqttListenerInvoker> getInvokers(String topic) {
        return registry.stream()
                .filter(invoker -> MqttTopic.isMatched(invoker.getTopic(), topic))
                .collect(Collectors.toList());
    }
}
