package shchoi.mqtt;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MqttMessageDispatcher {
    private final MqttListenerRegistry registry;

    public void dispatch(Message<?> message) {
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        List<MqttListenerInvoker> invokers = registry.getInvokers(topic);

        if (Objects.isNull(topic) || Objects.isNull(invokers)) {
            return;
        }

        invokers.forEach(invoker -> invoker.invoke(message));
    }
}
