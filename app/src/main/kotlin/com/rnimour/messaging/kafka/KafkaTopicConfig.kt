package com.rnimour.messaging.kafka

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
open class KafkaTopicConfig {

    companion object {
        @Value(value = "\${spring.kafka.bootstrap-servers}") // not actually working!!! angery!!! 😡❗❗❗
        var bootstrapAddress: String = "localhost:9092"
        const val TOPIC = "my-spring-topic"
    }

    @Bean
    open fun kafkaAdmin(): KafkaAdmin {
        val configs = HashMap<String, Any>()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        return KafkaAdmin(configs)
    }

    @Bean
    open fun mySpringTopic(): NewTopic {
        return NewTopic(TOPIC, 1, 1)
    }
}