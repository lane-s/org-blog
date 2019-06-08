echo "Running postgres container..."
docker run --rm --name pg-portfolio-blog -e POSTGRES_PASSWORD=docker -d -p 5432:5432 -v ~/docker/volumes/postgres:/var/lib/postgresql/data postgres
echo "Starting database..."
sleep 2s
PGPASSWORD=docker psql -h localhost -U postgres -d portfolio_blog
