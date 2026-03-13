# Base image
FROM node:18

# Tạo thư mục làm việc
WORKDIR /app

# Copy file package
COPY package*.json ./

# Cài dependencies
RUN npm install

# Copy source code
COPY . .

# Mở port 3000
EXPOSE 3000

# Chạy ứng dụng
CMD ["npm", "start"]