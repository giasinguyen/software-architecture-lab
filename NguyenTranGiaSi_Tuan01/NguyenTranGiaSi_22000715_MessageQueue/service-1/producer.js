const amqp = require("amqplib");

const RABBIT_URL = "amqp://admin:admin123@localhost:5672";
const EXCHANGE = "demo.exchange";
const ROUTING_KEY = "demo.key";

async function sendEvent(event) {
  const connection = await amqp.connect(RABBIT_URL);
  const channel = await connection.createChannel();

  await channel.assertExchange(EXCHANGE, "direct", { durable: true });

  channel.publish(
    EXCHANGE,
    ROUTING_KEY,
    Buffer.from(JSON.stringify(event))
  );

  console.log("Event sent:", event);

  setTimeout(() => {
    connection.close();
  }, 500);
}

module.exports = sendEvent;
