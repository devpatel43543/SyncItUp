# Frontend/Dockerfile
FROM node:18-alpine AS build

WORKDIR /app

ARG PROFILE

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the frontend code
COPY . .

# Build the React app with Vite
RUN if [[ "$PROFILE" == "dev" || "$PROFILE" == "prod" ]]; then \
      npm run build:$PROFILE; \
    else \
      npm run build; \
    fi

# Stage for serving the app
FROM nginx:alpine

# Copy built files to Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Expose the port Nginx listens on
EXPOSE 80

# Command to run Nginx
CMD ["nginx", "-g", "daemon off;"]
