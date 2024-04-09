#!/bin/bash

# Define the Docker image and container names
IMAGE_NAME=$1
DOCKERFILE_PATH="src/main/docker/newDockerfile.jvm"
CONTAINER_NAME=$2

# Build the Docker image
echo "Building Docker image: $IMAGE_NAME..."
docker build -f $DOCKERFILE_PATH -t $IMAGE_NAME --build-arg PROFILE=colo .

# Check if the container is already running, and if so, stop and remove it
if [ $(docker ps -q -f name=$CONTAINER_NAME) ]; then
    echo "Stopping and removing existing $CONTAINER_NAME container..."
    docker stop $CONTAINER_NAME
    docker rm $CONTAINER_NAME
fi

# Run the Docker container
echo "Running new $CONTAINER_NAME container..."
docker run -id \
  --name $CONTAINER_NAME \
  --network="host" \
  -p 51511:51511 \
  $IMAGE_NAME

echo "Done! $CONTAINER_NAME container should now be running on port 51511."
