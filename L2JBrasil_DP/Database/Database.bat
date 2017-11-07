@echo off

REM ###############################################
REM ## Configurate Database Connections please!  ##
REM ###############################################
REM Please, type here you dir to mysql directory \bin. Example : C:\Program Files\MySQL\MySQL Server 5.7\bin
set mysqlBinPath=C:\Program Files\MySQL\MySQL Server 5.7\bin

set DateT=%date%

REM Configurate database connection loginserver
set lsuser=root
set lspass=
set lsdb=l2jdb
set lshost=localhost

REM Configurate database connection Gameserver
set gsuser=root
set gspass=
set gsdb=l2jdb
set gshost=localhost
REM ############################################

set mysqldumpPath="%mysqlBinPath%\mysqldump"
set mysqlPath="%mysqlBinPath%\mysql"


:Step1
cls
echo. ---------------------------------------------------------------------
echo.
echo.   L2J-Brasil - Database Login Server
echo. _____________________________________________________________________
echo.
echo.   1 - Full install database loginserver`s.
echo.   2 - Full install loginserver db, go to install gameserver databases
echo.   3 - Exit from installer
echo. ---------------------------------------------------------------------

set Step1prompt=x
set /p Step1prompt= Please enter values :
if /i %Step1prompt%==1 goto LoginInstall
if /i %Step1prompt%==2 goto fullinstall
if /i %Step1prompt%==3 goto fullend
goto Step1


:LoginInstall

echo Clear database : %lsdb% and install database loginserver`s.
echo.
%mysqlPath% -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < login_install.sql
echo Update table accounts.sql
%mysqlPath% -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < ../Database/sql/accounts.sql
echo Update table gameservers.sql
%mysqlPath% -h %lshost% -u %lsuser% --password=%lspass% -D %lsdb% < ../Database/sql/gameservers.sql
echo.
echo Database login server has been installed with no errors!!
pause
goto :Step1


:fullinstall

echo Drop and clean old gamserver database`s.
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < full_install.sql
set title=installed
goto CreateTables

:CreateTables

echo Now be %title%ed database gameserver`s.
pause

echo *** Sucesfull 1 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/account_data.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/accounts.sql

echo *** Sucesfull 2 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/armor.sql

echo *** Sucesfull 3 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/char_creation_items.sql

echo *** Sucesfull 4 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/auction.sql


echo *** Sucesfull 5 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/auction_bid.sql

echo *** Sucesfull 6 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/auction_watch.sql

echo *** Sucesfull 7 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/augmentations.sql

echo *** Sucesfull 8 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/autoanounce.sql

echo *** Sucesfull 9 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/auto_chat.sql

echo *** Sucesfull 10 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/auto_chat_text.sql

echo *** Sucesfull 11 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/boxaccess.sql

echo *** Sucesfull 12 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/boxes.sql

echo *** Sucesfull 13 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/castle.sql

echo *** Sucesfull 14 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/castle_door.sql

echo *** Sucesfull 15 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/castle_doorupgrade.sql

echo *** Sucesfull 16 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/castle_siege_guards.sql

echo *** Sucesfull 17 percents. ***

echo *** Sucesfull 18 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_friends.sql

echo *** Sucesfull 19 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_hennas.sql

echo *** Sucesfull 20 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_macroses.sql

echo *** Sucesfull 21 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_quests.sql

echo *** Sucesfull 22 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_raid_points.sql

echo *** Sucesfull 23 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_recipebook.sql

echo *** Sucesfull 24 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_recommends.sql

echo *** Sucesfull 25 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_shortcuts.sql

echo *** Sucesfull 26 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_skills.sql

echo *** Sucesfull 27 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_skills_save.sql

echo *** Sucesfull 28 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_subclasses.sql

echo *** Sucesfull 29 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/characters.sql

echo *** Sucesfull 30 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/clan_data.sql

echo *** Sucesfull 31 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/clan_privs.sql

echo *** Sucesfull 32 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/clan_skills.sql

echo *** Sucesfull 33 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/clan_subpledges.sql

echo *** Sucesfull 34 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/clan_wars.sql

echo *** Sucesfull 35 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/clan_notices.sql

echo *** Sucesfull 36 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/clanhall.sql

echo *** Sucesfull 37 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/clanhall_functions.sql

echo *** Sucesfull 38 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/class_list.sql

echo *** Sucesfull 39 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/cursed_weapons.sql

echo *** Sucesfull 40 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/dimensional_rift.sql

echo *** Sucesfull 41 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/droplist.sql

echo *** Sucesfull 42 percents. ***

echo *** Sucesfull 43 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/etcitem.sql

echo *** Sucesfull 44 percents. ***

echo *** Sucesfull 45 percents. ***


echo *** Sucesfull 46 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/forums.sql

echo *** Sucesfull 47 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/games.sql

echo *** Sucesfull 48 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/global_tasks.sql

echo *** Sucesfull 49 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/grandboss_data.sql

echo *** Sucesfull 50 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/grandboss_list.sql

echo *** Sucesfull 51 percents. ***
echo *** Sucesfull 52 percents. ***

echo *** Sucesfull 53 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/henna_trees.sql

echo *** Sucesfull 54 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/heroes.sql

echo *** Sucesfull 55 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/items.sql

echo *** Sucesfull 56 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/itemsonground.sql

echo *** Sucesfull 57 percents. ***

echo *** Sucesfull 58 percents. ***

echo *** Sucesfull 59 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/merchant_areas_list.sql

echo *** Sucesfull 60 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/merchant_buylists.sql

echo *** Sucesfull 61 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/merchant_lease.sql

echo *** Sucesfull 62 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/merchant_shopids.sql

echo *** Sucesfull 63 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/merchants.sql

echo *** Sucesfull 64 percents. ***

echo *** Sucesfull 65 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/mods_wedding.sql
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/character_offline_trade.sql

echo *** Sucesfull 66 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/npc.sql

echo *** Sucesfull 67 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/npcskills.sql

echo *** Sucesfull 68 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/olympiad_nobles.sql

echo *** Sucesfull 69 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/pets.sql

echo *** Sucesfull 70 percents. ***

echo *** Sucesfull 71 percents. ***

echo *** Sucesfull 72 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/posts.sql

echo *** Sucesfull 73 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/raidboss_spawnlist.sql

echo *** Sucesfull 74 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/random_spawn.sql

echo *** Sucesfull 75 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/random_spawn_loc.sql

echo *** Sucesfull 76 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/seven_signs.sql

echo *** Sucesfull 77 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/seven_signs_festival.sql

echo *** Sucesfull 78 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/seven_signs_status.sql

echo *** Sucesfull 79 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/siege_clans.sql

echo *** Sucesfull 80 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/skill_learn.sql

echo *** Sucesfull 81 percents. ***

echo *** Sucesfull 82 percents. ***

echo *** Sucesfull 83 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/spawnlist.sql

echo *** Sucesfull 84 percents. ***

echo *** Sucesfull 85 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/topic.sql

echo *** Sucesfull 86 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/weapon.sql

echo *** Sucesfull 87 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/zone_vertices.sql

echo *** Sucesfull 88 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/quest_global_data.sql

echo *** Sucesfull 89 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/castle_manor_procure.sql

echo *** Sucesfull 90 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/castle_manor_production.sql

echo *** Sucesfull 91 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/custom_droplist.sql

echo *** Sucesfull 92 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/custom_notspawned.sql

echo *** Sucesfull 93 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/custom_npc.sql

echo *** Sucesfull 94 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/custom_spawnlist.sql

echo *** Sucesfull 95 percents. ***

echo *** Sucesfull 96 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/four_sepulchers_spawnlist.sql

echo *** Sucesfull 97 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/vanhalter_spawnlist.sql

echo *** Sucesfull 98 percents. ***

echo *** Sucesfull 99 percents. ***
%mysqlPath% -h %gshost% -u %gsuser% --password=%gspass% -D %gsdb% < ../Database/sql/zone.sql

echo *** Sucesfull 100 percents. **
echo.
echo GameServer Database %title%.
pause
goto :Step1

:end
echo.
echo Installing sucessfull.
echo.
pause

:fullend
