package shchoi.mqtt;

import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConversionException;
import shchoi.mqtt.config.InvocationFailedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


public class MqttListenerInvoker {
    private final String topic;
    private final String variableTopic;
    private final Object object;
    private final Method method;
    private final MqttArgumentResolvers resolvers;

    public MqttListenerInvoker(String topic, String variableTopic, Object object, Method method, MqttArgumentResolvers resolvers) {
        this.topic = topic;
        this.variableTopic = variableTopic;
        this.object = object;
        this.method = method;
        this.resolvers = resolvers;
    }

    public void invoke(Message<?> message) {
        try {
            Object[] arguments = getArguments(message);
            method.invoke(object, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InvocationFailedException("Invocation Fail. " + e.getMessage() + ", Message: " + message);
        } catch (MessageConversionException e) {
            throw new InvocationFailedException("Message Conversion Fail. " + e.getMessage() + ", Message: " + message);
        }
    }

    public String getTopic() {
        return this.topic;
    }

    public String getVariableTopic() {
        return this.variableTopic;
    }

    private Object[] getArguments(Message<?> message) {
        return Arrays.stream(method.getParameters())
                .map(parameter -> resolvers.resolve(parameter, message, variableTopic))
                .toArray();
    }
}
