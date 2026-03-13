FROM node:18-alpine

WORKDIR /app

# Copy package config and install dependencies
COPY package*.json ./
RUN npm install

# Copy application code
COPY . .

# Build the application
RUN npm run build

EXPOSE 3000

# Command to run the application
CMD ["npm", "start"]