const amqp = require("amqplib");

const RABBIT_URL = "amqp://admin:admin123@localhost:5672";
const EXCHANGE = "demo.exchange";
const QUEUE = "demo.queue";
const ROUTING_KEY = "demo.key";

async function consume() {
  const connection = await amqp.connect(RABBIT_URL);
  const channel = await connection.createChannel();

  await channel.assertExchange(EXCHANGE, "direct", { durable: true });
  await channel.assertQueue(QUEUE, { durable: true });
  await channel.bindQueue(QUEUE, EXCHANGE, ROUTING_KEY);

  console.log("Waiting for messages...");

  channel.consume(QUEUE, (msg) => {
    if (msg) {
      const event = JSON.parse(msg.content.toString());
      console.log("Received event:", event);

      channel.ack(msg);
    }
  });
}

consume();
