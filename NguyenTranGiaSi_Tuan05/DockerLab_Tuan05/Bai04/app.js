const express = require('express');
const app = express();

const PORT = 3000;

app.get('/', (req, res) => {
  res.send('Hello from Docker Compose 🚀');
});

app.listen(PORT, () => {
  console.log(`App running on port ${PORT}`);
});