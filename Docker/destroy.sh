docker compose down
docker volume rm -f $(docker volume ls -q)
docker rmi -f $(docker images -aq)