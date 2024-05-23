package com.rnimour.messaging.kafka

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component

@Component
class MyKafkaProducer {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    fun sendMessage(topic: String, message:String) {
        val future = kafkaTemplate.send(topic, message)
        future.whenComplete(handleCompletion(message))
    }

    private fun handleCompletion(message: String) = { result: SendResult<String, String>?, ex: Throwable? ->
        if (ex == null) {
            println("@offset ${result?.recordMetadata?.offset()}, sent message <$message>")
        } else {
            println("could not send message <$message> because of ${ex.message}")
        }
    }
}