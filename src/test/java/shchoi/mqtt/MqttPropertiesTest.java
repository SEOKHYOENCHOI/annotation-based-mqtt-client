package shchoi.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MqttPropertiesTest {
    @LocalServerPort
    int port;

    @Value("${mqtt.properties.userName}")
    String userName;

    @Value("${mqtt.properties.connectionTimeout}")
    Integer connectionTimeout;

    @Value("${mqtt.properties.automaticReconnect}")
    Boolean automaticReconnect;

    @Value("${mqtt.properties.willTopic}")
    String willTopic;

    @Value("${mqtt.properties.endpoints}")
    String[] endpoints;

    @Autowired
    MqttPahoClientFactory clientFactory;

    @Test
    public void name() {
        MqttConnectOptions options = clientFactory.getConnectionOptions();

        assertThat(options.getUserName()).isEqualTo(userName);
        assertThat(options.getConnectionTimeout()).isEqualTo(connectionTimeout);
        assertThat(options.isAutomaticReconnect()).isEqualTo(automaticReconnect);
        assertThat(options.getServerURIs()).isEqualTo(endpoints);
    }
}
