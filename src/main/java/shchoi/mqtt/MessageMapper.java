package shchoi.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

public class MessageMapper {
    private static final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

    static {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        converter.setObjectMapper(mapper);
    }

    public static Object mapFromMessage(Message message, Class<?> type) {
        return converter.fromMessage(message, type);
    }

    public static <T> Message mapToMessage(T payload, MessageHeaders headers) {
        return converter.toMessage(payload, headers);
    }
}
