FROM java:8-alpine
RUN mkdir -p /org-blog /org-blog/resources
WORKDIR /org-blog
COPY target/*-standalone.jar ./org-blog-standalone.jar
COPY resources resources
CMD java -jar org-blog-standalone.jar
EXPOSE 3000
