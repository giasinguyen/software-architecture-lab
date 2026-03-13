FROM nginx:latest

# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy the static website
COPY index.html /usr/share/nginx/html/

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]