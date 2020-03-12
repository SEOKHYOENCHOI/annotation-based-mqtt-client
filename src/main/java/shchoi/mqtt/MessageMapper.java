package shchoi.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    private final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

    public MessageMapper(ObjectMapper objectMapper) {
        this.converter.setObjectMapper(objectMapper);
    }

    public Object mapFromMessage(Message message, Class<?> type) {
        return converter.fromMessage(message, type);
    }

    public <T> Message mapToMessage(T payload, MessageHeaders headers) {
        return converter.toMessage(payload, headers);
    }
}
