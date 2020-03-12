package shchoi.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import shchoi.mqtt.argumentresolver.*;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Component
public class MqttArgumentResolvers {
    private final List<MqttArgumentResolver> resolvers;

    public MqttArgumentResolvers(ObjectMapper objectMapper) {
        this.resolvers = new ArrayList<>();

        this.resolvers.add(new TopicVariableArgumentResolver());
        this.resolvers.add(new RawPayloadArgumentResolver());
        this.resolvers.add(new MappingPayloadArgumentResolver(new ObjectMapper()));
        this.resolvers.add(new MqttMessageArgumentResolver(new DefaultPahoMessageConverter()));
    }

    public Object resolve(Parameter parameter, Message<?> message, String variableTopic) {
        return resolvers.stream()
                .filter(resolver -> resolver.supports(parameter))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 resolver를 찾지 못했습니다."))
                .resolve(parameter, message, variableTopic);
    }
}
