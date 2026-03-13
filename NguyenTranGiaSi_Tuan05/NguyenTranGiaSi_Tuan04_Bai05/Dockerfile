# Build stage
FROM golang:1.20-alpine AS builder

WORKDIR /app
COPY main.go .
RUN go build -o main .

# Run stage
FROM alpine:latest
WORKDIR /root/
COPY --from=builder /app/main .

CMD ["./main"]