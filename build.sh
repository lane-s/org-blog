#!/bin/bash
lein uberjar
docker build -t melodylane/org-blog .
