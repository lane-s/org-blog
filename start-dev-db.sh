#!/bin/bash
echo "Running postgres container for development..."
docker run --rm --name org-blog-pg -e POSTGRES_PASSWORD=docker -d -p 5432:5432 -v ~/docker/volumes/postgres:/var/lib/postgresql/data postgres
echo "Starting postgres..."
sleep 2s
PGPASSWORD=docker psql -h localhost -U postgres -c 'create database org_blog'
PGPASSWORD=docker psql -h localhost -U postgres -c 'create database org_blog_test'
