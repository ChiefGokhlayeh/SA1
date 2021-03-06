FROM debian:stable

ARG DEV_USERNAME=vscode
ARG DEV_USER_UID=1000
ARG DEV_USER_GID=$DEV_USER_UID

ARG TOMCAT_USERNAME=tomcat
ARG TOMCAT_USER_UID=1001
ARG TOMCAT_USER_GID=$TOMCAT_USER_UID

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
  && apt-get install -y sudo \
  && groupadd --gid $DEV_USER_GID $DEV_USERNAME \
  && useradd --uid $DEV_USER_UID --gid $DEV_USER_GID -m $DEV_USERNAME \
  && groupadd --gid $TOMCAT_USER_GID $TOMCAT_USERNAME \
  && useradd --uid $TOMCAT_USER_UID --gid $TOMCAT_USER_GID -M $TOMCAT_USERNAME \
  && usermod --gid $TOMCAT_USER_GID $DEV_USERNAME \
  && echo $DEV_USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$DEV_USERNAME \
  && chmod 0440 /etc/sudoers.d/$DEV_USERNAME

RUN apt-get update \
  && apt-get install -y \
  chromium \
  curl \
  git \
  gradle \
  libnss3-tools \
  maven \
  nodejs \
  npm \
  openjdk-11-jdk \
  && npm install yarn -g

RUN mkdir -p /usr/local/tomcat \
  && cd /usr/local/tomcat \
  && curl https://ftp-stud.hs-esslingen.de/pub/Mirrors/ftp.apache.org/dist/tomcat/tomcat-9/v9.0.36/bin/apache-tomcat-9.0.36.tar.gz --output tomcat.tar.gz \
  && tar xvzf tomcat.tar.gz --strip-components=1 \
  && rm -f tomcat.tar.gz \
  && chown tomcat:tomcat -R . \
  && chmod -R g=u .

RUN cd /usr/local/bin \
  && curl -L -o mkcert https://github.com/FiloSottile/mkcert/releases/download/v1.4.1/mkcert-v1.4.1-linux-amd64 \
  && chmod +x mkcert

USER $DEV_USERNAME

ENV DEBIAN_FRONTEND=dialog
