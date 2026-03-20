const express = require("express");
const mysql = require("mysql2");

const app = express();

const db = mysql.createConnection({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME
});

db.connect((err) => {
  if (err) {
    console.error("DB connection failed:", err);
  } else {
    console.log("Connected to MySQL");
  }
});

app.get("/", (req, res) => {
  db.query("SELECT NOW() AS time", (err, results) => {
    if (err) return res.send("Error");
    res.send("Database time: " + results[0].time);
  });
});

app.listen(3000, () => {
  console.log("Server running on port 3000");
});