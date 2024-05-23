package com.rnimour.messaging.kafka

import com.rnimour.messaging.kafka.KafkaTopicConfig.Companion.bootstrapAddress
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory


@EnableKafka
@ConditionalOnProperty("rnimour.consume", havingValue = "true", matchIfMissing = true)
@Configuration
open class KafkaConsumerConfig {

    companion object {
        @Value(value = "rnimour.group")
        var groupId: String = "default-group"
    }

    @Bean
    open fun consumerFactory(): ConsumerFactory<String, String> {
        println("creating consumer factory with groupId=$groupId @bootstrapAddress $bootstrapAddress")
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    open fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    @PostConstruct
    fun constructionComplete() = println("This app is a consumer! groupId: $groupId")
}