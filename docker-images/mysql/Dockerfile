FROM mysql:5.7

ENV MYSQL_ROOT_PASSWORD root
ENV MYSQL_DATABASE l2jdb
ENV SRC_BRANCH develop

RUN set -x \
    	&& apt-get update && apt-get install -y --no-install-recommends ca-certificates git && \
        mkdir -p /tmp/l2jbr && cd /tmp/l2jbr && \
        git init && git remote add -f origin https://github.com/L2jBrasil/L2jBrasil.git && \
        git config core.sparseCheckout true && \
        echo "L2JBrasil_DP/Database/sql" >> .git/info/sparse-checkout && \
        git pull origin ${SRC_BRANCH} && \
        ls -lah L2JBrasil_DP/Database/sql && \
        cp L2JBrasil_DP/Database/sql/*.sql /docker-entrypoint-initdb.d/ && \
        rm -rf /var/lib/apt/lists/* && \
        apt-get purge -y --auto-remove ca-certificates git

ADD mysqld.cnf /etc/mysql/mysql.conf.d/mysqld.cnf
ADD ./custom/*.sql /docker-entrypoint-initdb.d/
# RUN ls -lah /docker-entrypoint-initdb.d