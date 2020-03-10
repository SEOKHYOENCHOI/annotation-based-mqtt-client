# Annotation Based Mqtt Client

## Usage
#### Dependency 추가
```
...

repositories {
    jcenter()
    ...
}

dependencies {
    implementation 'shchoi:annotation-based-mqtt-client:1.0.2'
    ...
...
```

#### application.yml 설정
* [Sample](https://github.com/SEOKHYOENCHOI/annotation-based-mqtt-client/blob/master/src/test/resources/application.yml) 


#### Properties
```yaml
userName: [String]
password: [String]
connectionTimeout: [Integer]
keepAliveInterval: [Integer]
cleanSession: [Boolean]
automaticReconnect: [Boolean]
maxInflight: [Integer]
mqttVersion: [Integer]
willTopic: [String]
willPayload: [String]
willQos: [Integer]
willRetained: [Boolean]
endpoints: [List<String>]
```

#### Subscribe, Publish
* Subscribe
```java
@MqttListener(topic = "/example")
public void subscribe(String payload){
    System.out.println(payload);
}
```

* Publish
```java
@RestController
@RequiredArgsConstructor
public class ExampleController{
    private final MqttSender mqttSender;
    
    @PostMapping("/examples")
    public void publish(@RequestBody Request request) {
        String topic = "/example/topic";
        Object payload = request.getPayload();
        int qos = 2;
        boolean retained = true;

        mqttSender.sendMessage(topic, payload, qos, retained);
    }
}
```

* [Sample](https://github.com/SEOKHYOENCHOI/annotation-based-mqtt-client/blob/master/src/test/java/shchoi/sample/MqttController.java) 

#### Topic Variable Annotation
* Mqtt Single Level Wildcard의 값을 전달
* Annotation의 index 값으로 Topic 내의 Single Level Wildcard 순서에 따라 접근 가능. Default Index는 0

```java
@MqttListener(topic = "/example/+/topic/+/variable")
public void subscribe(String payload, @TopicVariable String first, @TopicVariable(index = 1) String second){
    System.out.println(payload);
    System.out.println(first);
    System.out.println(second);
}
```