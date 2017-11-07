# https://hub.docker.com/r/anapsix/alpine-java/
FROM anapsix/alpine-java:8_jdk

USER root

ENV ANT_FILENAME=apache-ant-1.9.7 \
    ANT_HOME=/opt/ant \
    PATH=${PATH}:/opt/ant/bin


ENV ANT_CONTRIB_VERSION 1.0b2

ADD https://www.apache.org/dist/ant/binaries/${ANT_FILENAME}-bin.tar.bz2 /tmp/ant.tar.bz2

RUN tar -C /opt -xjf /tmp/ant.tar.bz2 && \
    ln -s /opt/${ANT_FILENAME} /opt/ant && \
    rm -rf /tmp/* /var/cache/apk/* /opt/ant/manual/*

RUN java -version

RUN ant --version


CMD /bin/sh