#!/bin/bash
mysql_exec=''

function start {

	echo   '____________________________________________________________________'
	echo   
	echo   'L2J-Brasil 3.0 - Database Installer'
	echo   '____________________________________________________________________'

	if [ -z $mysql_exec ]; then
		mysql_exec=$(which mysql)
	fi
	
	if [ -z $mysql_exec ]; then
		echo "Mysql Client not found. Configure mysql_exec with executable mysql path."
		exit
	fi
	
	chooseOption
}

function chooseOption {
	getInstallType
	if [ "$installType" == "1" ]; then 
		installLoginDatabase
	else 
		if [ "$installType" == "2" ]; then
			installGameDatabase
		else
			exit
		fi
	fi
}

function getInstallType {
	echo
	echo   '1 - Full install loginserver databases'
	echo   '2 - Full install gameserver databases'
	echo   'Any other - Exit from installer'
	echo ---------------------------------------------------------------------
	echo -n 'choose the kind of Database installation: '

	read installType
}

function checkFile {

	if [ ! -f $1 ]; then
		echo "File not found: $1. Put the file with the database configuration on the executable folder."
		exit
	fi

}

function installLoginDatabase {
	sql_list="login_sql.list"
	loginDBConfiguration="login_database.conf"

	checkFile $loginDBConfiguration
	checkFile $sql_list

	echo "Installing Login Server Database..."

	executeSqlFromFile $sql_list $loginDBConfiguration
	echo   'Database login server has been installed!!'
	echo   '____________________________________________________________________'

	chooseOption
}

function executeSqlFromFile {
	script_count=$(wc -l < $1)
	count=0
	while IFS= read -r sql
	do
	  count=$(( $count + 1 ))
	  echo -en "Installing sql: $sql [ progress: $(( $count * 100 / $script_count ))% ]\033[0K\r "
	  $mysql_exec --defaults-extra-file=$2 < $sql
	done < "$1"
	echo 
}


function installGameDatabase {
	sql_list="game_sql.list"
	gameDBConfiguration="game_database.conf"

	checkFile $gameDBConfiguration
	checkFile $sql_list

	echo "Installing Game Server's database..."

	executeSqlFromFile $sql_list $gameDBConfiguration
	echo   'Database Game Server has been installed!!'
	echo   '____________________________________________________________________'

	chooseOption
}

start



