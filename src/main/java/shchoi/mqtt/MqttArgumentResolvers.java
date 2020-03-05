package shchoi.mqtt;

import org.springframework.messaging.Message;
import shchoi.mqtt.argumentresolver.MappingPayloadArgumentResolver;
import shchoi.mqtt.argumentresolver.MqttArgumentResolver;
import shchoi.mqtt.argumentresolver.RawPayloadArgumentResolver;
import shchoi.mqtt.argumentresolver.TopicVariableArgumentResolver;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class MqttArgumentResolvers {
    private final List<MqttArgumentResolver> resolvers;

    public MqttArgumentResolvers() {
        this.resolvers = new ArrayList<>();

        this.resolvers.add(new TopicVariableArgumentResolver());
        this.resolvers.add(new RawPayloadArgumentResolver());
        this.resolvers.add(new MappingPayloadArgumentResolver());
    }

    public Object resolve(Parameter parameter, Message message, String topic) {
        return resolvers.stream()
                .filter(resolver -> resolver.supports(parameter))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 resolver를 찾지 못했습니다."))
                .resolve(parameter, message, topic);
    }
}
