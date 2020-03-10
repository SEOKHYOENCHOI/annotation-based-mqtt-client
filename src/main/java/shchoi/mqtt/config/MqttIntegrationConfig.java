package shchoi.mqtt.config;


import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import shchoi.mqtt.MqttMessageDispatcher;

import java.util.Objects;

@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttIntegrationConfig {
    private MqttProperties properties;
    private String clientId;

    private final MqttMessageDispatcher messageDispatcher;

    public MqttIntegrationConfig(MqttMessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        if (Objects.isNull(clientId)) {
            clientId = "Client";
        }

        clientId += "-" + System.nanoTime();

        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = properties.createConnectOptions();
        factory.setPersistence(new MemoryPersistence());
        factory.setConnectionOptions(options);
        return factory;
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return messageDispatcher::dispatch;
    }

    @Bean
    public MessageProducer inbound(MqttPahoClientFactory factory) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, factory);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MqttPahoMessageHandler mqttOutbound(MqttPahoClientFactory factory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, factory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultRetained(true);
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    public void setProperties(MqttProperties properties) {
        this.properties = properties;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
