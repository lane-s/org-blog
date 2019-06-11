#!/bin/bash
docker run --rm --name org-blog-pg -e POSTGRES_PASSWORD=docker -d -p 5432:5432 postgres
PGPASSWORD=docker psql -h localhost -U postgres -c 'create database org_blog_test'
