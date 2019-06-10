#!/bin/bash
lein uberjar
docker build -t lane-s/org-blog .
