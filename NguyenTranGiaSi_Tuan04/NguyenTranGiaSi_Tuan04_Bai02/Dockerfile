# Base image
FROM python:3.9

# Thư mục làm việc trong container
WORKDIR /app

# Copy file requirements
COPY requirements.txt .

# Cài thư viện
RUN pip install --no-cache-dir -r requirements.txt

# Copy source code
COPY . .

# Mở port 5000
EXPOSE 5000

# Chạy ứng dụng
CMD ["python", "app.py"]