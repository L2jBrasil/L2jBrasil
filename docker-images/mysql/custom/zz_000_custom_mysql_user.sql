CREATE USER 'docker'@'%'  IDENTIFIED WITH mysql_native_password BY 'l2jdb';
GRANT ALL PRIVILEGES ON *.* TO 'docker'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;