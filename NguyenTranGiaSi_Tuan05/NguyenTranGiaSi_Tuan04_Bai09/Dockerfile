FROM redis:latest

# Copy cấu hình tùy chỉnh
COPY redis.conf /usr/local/etc/redis/redis.conf

# Cổng mặc định
EXPOSE 6379

CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]