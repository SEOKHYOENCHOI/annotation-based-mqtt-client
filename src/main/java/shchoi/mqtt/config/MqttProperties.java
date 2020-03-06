package shchoi.mqtt.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MqttProperties {
    private static final String SETTER_PREFIX = "set";
    private static final String WILL_PREFIX = "will";

    private String userName;
    private String password;
    private Integer connectionTimeout;
    private Integer keepAliveInterval;
    private Boolean cleanSession;
    private Boolean automaticReconnect;
    private Integer maxInflight;
    private Integer mqttVersion;
    private String willTopic;
    private String willPayload;
    private Integer willQos;
    private Boolean willRetained;
    private List<String> endpoints;

    public MqttConnectOptions createConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(endpoints.toArray(new String[0]));
        setWillIn(options);
        setPasswordIn(options);
        setProperties(options);

        return options;
    }

    private void setWillIn(MqttConnectOptions options) {
        if (Objects.nonNull(this.willTopic) &&
                Objects.nonNull(this.willPayload) &&
                Objects.nonNull(this.willQos) &&
                Objects.nonNull(this.willRetained)) {

            options.setWill(willTopic, willPayload.getBytes(), willQos, willRetained);
        }
    }

    private void setPasswordIn(MqttConnectOptions options) {
        if(Objects.nonNull(password)) {
            options.setPassword(password.toCharArray());
        }
    }

    private void setProperties(MqttConnectOptions options) {
        Arrays.stream(this.getClass().getDeclaredFields())
                .filter(this::isNotWillProperty)
                .filter(this::isNotExcludedField)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .forEach(field -> setOption(options, field));
    }

    private void setOption(MqttConnectOptions options, Field field) {
        Object value = getFieldValue(field);

        if (Objects.nonNull(value)) {
            Method setter = getSetter(options, field);
            try {
                setter.invoke(options, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(field.getName());
            }
        }
    }

    private Method getSetter(MqttConnectOptions options, Field field) {
        String setterName = generateSetterName(field.getName());
        try {
            return Arrays.stream(options.getClass().getMethods())
                    .filter(method -> method.getName().equals(setterName))
                    .findAny()
                    .orElseThrow(NoSuchMethodException::new);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(field.getName());
        }
    }

    private String generateSetterName(String name) {
        StringBuilder builder = new StringBuilder(name);
        char firstChar = Character.toUpperCase(builder.charAt(0));
        builder.setCharAt(0, firstChar);
        return builder.insert(0, SETTER_PREFIX).toString();
    }

    private Object getFieldValue(Field field) {
        try {
            return field.get(this);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(field.getName());
        }
    }

    private boolean isNotWillProperty(Field field) {
        return !field.getName().startsWith(WILL_PREFIX);
    }

    private boolean isNotExcludedField(Field field){
        return !"endpoints".equals(field.getName()) && !"password".equals(field.getName());
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(Integer keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public Boolean getCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(Boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public Boolean getAutomaticReconnect() {
        return automaticReconnect;
    }

    public void setAutomaticReconnect(Boolean automaticReconnect) {
        this.automaticReconnect = automaticReconnect;
    }

    public Integer getMaxInflight() {
        return maxInflight;
    }

    public void setMaxInflight(Integer maxInflight) {
        this.maxInflight = maxInflight;
    }

    public Integer getMqttVersion() {
        return mqttVersion;
    }

    public void setMqttVersion(Integer mqttVersion) {
        this.mqttVersion = mqttVersion;
    }

    public String getWillTopic() {
        return willTopic;
    }

    public void setWillTopic(String willTopic) {
        this.willTopic = willTopic;
    }

    public String getWillPayload() {
        return willPayload;
    }

    public void setWillPayload(String willPayload) {
        this.willPayload = willPayload;
    }

    public Integer getWillQos() {
        return willQos;
    }

    public void setWillQos(Integer willQos) {
        this.willQos = willQos;
    }

    public Boolean getWillRetained() {
        return willRetained;
    }

    public void setWillRetained(Boolean willRetained) {
        this.willRetained = willRetained;
    }

    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }
}
