# https://hub.docker.com/r/anapsix/alpine-java/
FROM anapsix/alpine-java:8_jdk

USER root

ENV ANT_VERSION 1.9.9
RUN cd && \
    wget -q http://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz && \
    tar -xzf apache-ant-${ANT_VERSION}-bin.tar.gz && \
    mv apache-ant-${ANT_VERSION} /opt/ant && \
    rm apache-ant-${ANT_VERSION}-bin.tar.gz

ENV ANT_HOME /opt/ant
ENV PATH ${PATH}:/opt/ant/bin

RUN java -version
RUN ant -version

RUN mkdir /opt/src


VOLUME /opt/src
WORKDIR /opt/src

CMD ant