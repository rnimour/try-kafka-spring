package com.rnimour.messaging.kafka

import com.rnimour.messaging.kafka.KafkaTopicConfig.Companion.TOPIC
import com.rnimour.messaging.kafka.KafkaTopicConfig.Companion.bootstrapAddress
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ConditionalOnProperty("rnimour.produce", havingValue = "true")
@Configuration
open class KafkaProducerConfig {

    @Bean
    open fun producerFactory(): ProducerFactory<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    open fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    @RestController
    class MessageRestController {

        @Autowired
        private lateinit var kafkaProducer: MyKafkaProducer

        @PostMapping("/message")
        fun post(@RequestBody body: String): ResponseEntity<String> {

            val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
            println("$time: received <$body>. sending to Kafka")

            kafkaProducer.sendMessage(TOPIC, body)

            return ResponseEntity.ok(body)
        }
    }

    @PostConstruct
    fun constructionComplete() = println("This app is a producer!")
}