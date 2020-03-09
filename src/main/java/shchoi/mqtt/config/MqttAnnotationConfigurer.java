package shchoi.mqtt.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.util.StringUtils;
import shchoi.mqtt.MqttListenerRegistry;
import shchoi.mqtt.annotation.MqttListener;
import shchoi.mqtt.util.VariableTopicUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

@Configuration
public class MqttAnnotationConfigurer implements ApplicationContextAware {
    private final MqttListenerRegistry registry;
    private final MqttPahoMessageDrivenChannelAdapter adapter;

    public MqttAnnotationConfigurer(MqttListenerRegistry registry, MessageProducer adapter) {
        this.registry = registry;
        this.adapter = (MqttPahoMessageDrivenChannelAdapter) adapter;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Arrays.stream(applicationContext.getBeanDefinitionNames())
                .map(applicationContext::getBean)
                .forEach(this::addTopics);
    }

    private void addTopics(Object object) {
        Class<?> clazz = object.getClass();

        Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(MqttListener.class))
                .forEach(method -> addTopic(object, method));
    }

    private void addTopic(Object object, Method method) {
        MqttListener mqttListener = method.getAnnotation(MqttListener.class);

        String variableTopic = mqttListener.topic();
        if (StringUtils.isEmpty(variableTopic)) {
            return;
        }

        String topic = VariableTopicUtils.toWildcardTopic(variableTopic);

        registry.register(topic, variableTopic, object, method);
        if (!Arrays.asList(adapter.getTopic()).contains(topic)) {
            adapter.addTopic(topic, mqttListener.qos());
        }
    }
}
