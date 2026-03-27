import express from 'express';

const app = express();
const PORT = process.env.PORT || 3000;

app.get('/', (_req, res) => {
  res.json({ message: 'Hello from optimized Docker!', status: 'ok' });
});

app.get('/health', (_req, res) => {
  res.json({ status: 'healthy' });
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});

export default app;
