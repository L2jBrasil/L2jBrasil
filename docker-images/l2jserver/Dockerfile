# https://hub.docker.com/r/anapsix/alpine-java/
FROM anapsix/alpine-java:8_jdk

USER root


ENV DB_NAME l2jdb
ENV DB_HOST mysql
ENV DB_USER l2jdb
ENV DB_PWD l2jdb
ENV SERVER_IP 127.0.0.1
ENV SRC_BRANCH develop
ENV LOGIN_HOST 127.0.0.1


ENV TIMEZONE            America/Sao_Paulo

ENV ANT_VERSION 1.9.9
RUN cd && \
    wget -q http://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz && \
    tar -xzf apache-ant-${ANT_VERSION}-bin.tar.gz && \
    mv apache-ant-${ANT_VERSION} /opt/ant && \
    rm apache-ant-${ANT_VERSION}-bin.tar.gz

ENV ANT_HOME /opt/ant
ENV PATH ${PATH}:/opt/ant/bin


# Let's roll
RUN	apk update && \
	apk upgrade && \
	apk add --update  tzdata unzip && \
	apk add --no-cache git && \
	cp /usr/share/zoneinfo/${TIMEZONE} /etc/localtime && \
	echo "${TIMEZONE}" > /etc/timezone && \
	mkdir /l2jbr

RUN git clone -b ${SRC_BRANCH} --single-branch https://github.com/L2jBrasil/L2jBrasil.git /opt/src
RUN ant  -verbose -buildfile  /opt/src/L2JBrasil_DP/build.xml
RUN ant  -verbose -buildfile  /opt/src/L2JBrasil_CORE/build.xml
RUN cd /opt/src && ls -lah && \
	unzip L2JBrasil_CORE/build/L2JBrasil_CORE.zip -d /l2jbr  && \
	unzip L2JBrasil_DP/build/L2JBrasil_DP.zip -d /l2jbr && ls /l2jbr/Game/lib/*.jar

# Update login/game server properties (NOT Working :( )
#RUN cd /l2jbr && \
#	sed -i "s/Login = root/Login = ${DB_USER}/g" Login/config/login.properties && \
#    	sed -i "s/Password =/Password = ${DB_PWD}/g" Login/config/login.properties && \
#    	sed -i "s/MaximumDbConnections = 10/MaximumDbConnections = 100/g" Login/config/login.properties && \
#    	sed -i "s|URL = jdbc:mysql://localhost/l2jdb|URL = jdbc:mysql://${DB_HOST}/${DB_NAME}|g" Login/config/login.properties && \
#    	sed -i "s|URL = jdbc:mysql://localhost/l2jdb|URL = jdbc:mysql://${DB_HOST}/${DB_NAME}|g" Game/config/server.properties && \
#    	sed -i "s/Login = root/Login = ${DB_USER}/g" Game/config/server.properties && \
#    	sed -i "s/Password = /Password = ${DB_PWD}/g" Game/config/server.properties && \
#    	sed -i "s/user = root/user = ${DB_USER}/g" Database/login_database.conf && \
#    	sed -i "s/password = root/password = ${DB_PWD}/g" Database/login_database.conf && \
#    	sed -i "s/user = root/user = ${DB_USER}/g" Database/game_database.conf && \
#    	sed -i "s/password = root/password = ${DB_PWD}/g" Database/game_database.conf && \
#    	sed -i "s/LoginHost = 127.0.0.1/LoginHost = ${LOGIN_HOST}/g" Database/game_database.conf && \
#    	sed -i "s/ExternalHostname = 127.0.0.1/ExternalHostname = ${SERVER_IP}/g" Game/config/server.properties


#CMD cat /l2jbr/Game/config/server.properties
#CMD cat /l2jbr/Login/config/login.properties

# All other custom config should be place in a file like that:
COPY config/Game/* /l2jbr/Game/
COPY config/Login/* /l2jbr/Login/

VOLUME /opt/src
VOLUME /l2jbr
WORKDIR /l2jbr

# CMD java -Djava.util.logging.config.file=console.cfg -cp "Game/lib/*" com.it.br.gsregistering.GameServerRegister

CMD cd /l2jbr/Login/ && java -Xmx32m -XX:+UseParallelGC -XX:+AggressiveOpts -XX:ParallelGCThreads=2 -cp "../Game/lib/*" com.it.br.loginserver.L2LoginServer &  \
sleep 10 && cd /l2jbr/Game/ && java -Xmx1024m -XX:+AggressiveOpts -XX:+HeapDumpOnOutOfMemoryError -cp "./lib/*" com.it.br.gameserver.GameServer
