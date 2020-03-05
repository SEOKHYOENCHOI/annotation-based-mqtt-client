package shchoi.mqtt;

import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConversionException;
import shchoi.mqtt.config.InvocationFailedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


public class MqttListenerInvoker {
    private final String topic;
    private final Object object;
    private final Method method;
    private final MqttArgumentResolvers resolvers = new MqttArgumentResolvers();

    public MqttListenerInvoker(String topic, Object object, Method method) {
        this.topic = topic;
        this.object = object;
        this.method = method;
    }

    public void invoke(Message message) {
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

    private Object[] getArguments(Message message) {
        return Arrays.stream(method.getParameters())
                .map(parameter -> resolvers.resolve(parameter, message, topic))
                .toArray();
    }
}
