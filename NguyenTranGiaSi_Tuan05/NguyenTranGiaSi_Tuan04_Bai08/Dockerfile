FROM postgres:15

# Thông tin xác thực mặc định (có thể được ghi đè lúc chạy)
ENV POSTGRES_USER=myuser
ENV POSTGRES_PASSWORD=mypassword
ENV POSTGRES_DB=mydb

# Chép file SQL vào thư mục khởi tạo tự động của ảnh PostgreSQL
COPY init.sql /docker-entrypoint-initdb.d/