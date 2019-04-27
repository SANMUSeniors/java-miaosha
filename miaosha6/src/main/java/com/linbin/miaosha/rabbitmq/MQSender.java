package com.linbin.miaosha.rabbitmq;

import com.linbin.miaosha.redis.RediService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate ;

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = RediService.beanToString(mm);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(com.imooc.miaosha.rabbitmq.MQConfig.MIAOSHA_QUEUE, msg);
    }
//    public void sendTopic(Object message) {
//		String msg = RediService.beanToString(message);
//		log.info("send topic message:"+msg);
//		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg+"1");
//		amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg+"2");
//	}
//		public void sendFanout(Object message) {
//		String msg = RedisService.beanToString(message);
//		log.info("send fanout message:"+msg);
//
    //	public void sendHeader(Object message) {
//		String msg = RedisService.beanToString(message);
//		log.info("send fanout message:"+msg);
//		MessageProperties properties = new MessageProperties();
//		properties.setHeader("header1", "value1");
//		properties.setHeader("header2", "value2");
//		Message obj = new Message(msg.getBytes(), properties);
//		amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", obj);
//	}
}
