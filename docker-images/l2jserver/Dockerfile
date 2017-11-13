# https://hub.docker.com/r/anapsix/alpine-java/
FROM anapsix/alpine-java:8_jdk

USER root

ENV DB_USER root
ENV DB_PWD foopwd123

ENV TIMEZONE            America/Sao_Paulo
ENV SRC_BRANCH develop

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

# Update login/game server properties
RUN cd /l2jbr && \
	sed -i "s/Login = root/Login = ${DB_USER}/g" Login/config/login.properties && \
	sed -i "s/Password =/Password = ${DB_PWD}/g" Login/config/login.properties && \
	sed -i "s/MaximumDbConnections = 10/MaximumDbConnections = 100/g" Login/config/login.properties && \
	sed -i "s/Login = root/Login = ${DB_USER}/g" Game/config/server.properties && \
	sed -i "s/Password =/Password = ${DB_PWD}/g" Game/config/server.properties
	
RUN cd /l2jbr && \
	sed -i "s/user = root/user = ${DB_USER}/g" Database/login_database.conf && \
	sed -i "s/password = root/password = ${DB_PWD}/g" Database/login_database.conf && \	
	sed -i "s/user = root/user = ${DB_USER}/g" Database/game_database.conf && \
	sed -i "s/password = root/password = ${DB_PWD}/g" Database/game_database.conf
	 
	
VOLUME /opt/src
VOLUME /l2jbr
WORKDIR /l2jbr

# CMD java -Djava.util.logging.config.file=console.cfg -cp "Game/lib/*" com.it.br.gsregistering.GameServerRegister

CMD java -Xmx32m -XX:+UseParallelGC -XX:+AggressiveOpts -XX:ParallelGCThreads=2 -cp "Game/lib/*" com.it.br.loginserver.L2LoginServer &  \
sleep 10 && java  -Xmx512m -XX:+AggressiveOpts -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -cp "Game/lib/*" com.it.br.gameserver.GameServer
