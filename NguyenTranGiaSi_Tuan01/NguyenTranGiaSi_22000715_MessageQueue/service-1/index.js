const express = require("express");
const sendEvent = require("./producer");

const app = express();
app.use(express.json());

app.post("/orders", async (req, res) => {
  const event = {
    orderId: "ORD-001",
    amount: 1000,
    createdAt: new Date()
  };

  await sendEvent(event);
  res.send("Order event sent!");
});

app.listen(3001, () => {
  console.log("Service 1 running at http://localhost:3001");
});
