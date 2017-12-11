/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.it.br.gameserver.network;

/**
 *
 * @author Noctarius & Nille02
 */
public enum SystemMessageId
{
	/**
	 * ID: 0<br>
	 * Message: You have been disconnected from the server.
	 */
	YOU_DESCONNECTED_FROM_SERVER(0),
	
	/**
	 * ID: 1<br>
	 * Message: The server will be coming down in $s1 second(s).  Please find a safe place to log out
	 */
	THE_SERVER_WILL_DOWN_S1(1),
	
	/**
	 * ID: 2<br>
	 * Message: $s1 does not exist.
	 */
	S1_NOT_EXIST(2),
	
	/**
	 * ID: 3<br>
	 * Message: $s1 is not currently logged in.
	 */
	S1_IS_NOT_ONLINE(3),
	
	/**
	 * ID: 4<br>
	 * Message: You cannot ask yourself to apply to a clan.
	 */
	CANNOT_INVITE_YOURSELF(4),
	
	/**
	 * ID: 5<br>
	 * Message: $s1 already exists.
	 */
	S1_ALREADY_EXISTS(5),

	/**
	 * ID: 6<br>
	 * Message: $s1 does not exist
	 */
	S1_DOES_NOT_EXIST(6),
	
	/** 
     * ID: 7<br> 
     * Message: You are already a member of $s1.
     */ 
    ALREADY_MEMBER_OF_S1(7),
	
	/**
	 * ID: 8<br>
	 * Message: You are working with another clan.
	 */
	YOU_ARE_WORKING_WITH_ANOTHER_CLAN(8),

	/**
	 * ID: 9<br>
	 * Message: $s1 is not a clan leader.
	 */
	S1_IS_NOT_A_CLAN_LEADER(9),
	
	/**
	 * ID: 10<br>
	 * Message: $s1 is working with another clan.
	 */
	S1_WORKING_WITH_ANOTHER_CLAN(10),
	
	/** 
     * ID: 11<br> 
     * Message: There are no applicants for this clan.
     */ 
     THERE_ARE_NO_APPLICANTS_FOR_CLAN(11),
     
     /**
  	 * ID: 12<br>
  	 * Message: Applicant information is incorrect.
  	 */
     THE_APPLICANT_IS_INCORRECT(12),
     
     /**
 	 * ID: 13<br>
 	 * Message: Unable to disperse: your clan has requested to participate in a castle siege.
 	 */
 	CANNOT_DISSOLVE_CAUSE_CLAN_WILL_PARTICIPATE_IN_CASTLE_SIEGE(13),
 	
 	/**
     * ID: 14<br>
     * Message: Unable to disperse: your clan owns one or more castles or hideouts.
     */
 	CANNOT_DISSOLVE_CAUSE_CLAN_OWNS_CASTLE_HIDEOUT(14),
 	
 	/**
     * ID: 15<br>
     * Message: You are in siege.
     */
 	YOU_ARE_IN_SIEGE(15),
 	
 	/**
     * ID: 16<br>
     * Message: You are not in siege.
     */
 	YOU_ARE_NOT_IN_SIEGE(16),
 	
 	/**
     * ID: 17<br>
     * Message: The castle siege has begun.
     */
 	SIEGE_BEGUN(17),
 	
 	/**
     * ID: 18<br>
     * Message: The castle siege has ended
     */
 	SIEGE_ENDED(18),
 	
 	/**
     * ID: 19<br>
     * Message: There is a new Lord of the castle!
     */
 	THERE_ARE_NEW_LORD(19),
 	
 	/**
     * ID: 20<br>
     * Message: The gate is being opened.
     */
 	THE_GATE_BEING_OPENED(20),
 	
 	/**
     * ID: 21<br>
     * Message: The gate is being destroyed
     */
 	THE_GATE_BEING_DESTROYED(21),
 	
 	 /**
     * ID: 22<br>
     * Message: Your target is out of range.
     */
    TARGET_TOO_FAR(22),

	/**
	 * ID: 23<br>
	 * Message: Not enough HP.
	 */
	NOT_ENOUGH_HP(23),
	
	/**
	 * ID: 24<br>
	 * Message: Not enough MP.
	 */
    NOT_ENOUGH_MP(24),
    
    /**
	 * ID: 25<br>
	 * Message: Rejuvenating HP.
	 */
	REJUVENATING_HP(25),
	
	/**
	 * ID: 26<br>
	 * Message: Rejuvenating MP.
	 */
    REJUVENATING_MP(26),
    
    /**
	 * ID: 27<br>
	 * Message: Your casting has been interrupted.
	 */
	CASTING_INTERRUPTED(27),
	
	/**
	 * ID: 28<br>
	 * Message: You have obtained $s1 adena.
	 */
	YOU_PICKED_UP_S1_ADENA(28),

	/**
	 * ID: 29<br>
	 * Message: You have obtained $s2 $s1.
	 */
	YOU_PICKED_UP_S1_S2(29),

	/**
	 * ID: 30<br>
	 * Message: You have obtained $s1.
	 */
	YOU_PICKED_UP_S1(30),
	
	/**
	 * ID: 31<br>
	 * Message: You cannot move while sitting.
	 */
    CANT_MOVE_SITTING(31),
    
    /**
     * ID: 32<br>
     * Message: You are unable to engage in combat. Please go to the nearest restart point.
     */
    UNABLE_TO_COMBAT(32),
    
    /**
     * ID: 33<br>
     * Message: You cannot move while casting.
     */
    CANT_MOVE_CASTING(33),
    
    /**
     * ID: 34<br>
     * Message: Welcome to the World of Lineage II.
     */
    WELCOME_TO_LINEAGE(34),
	
	/**
	 * ID: 35<br>
	 * Message: You hit for $s1 damage
	 */
	YOU_DID_S1_DMG(35),

	/**
	 * ID: 36<br>
	 * Message: $s1 hit you for $s2 damage.
	 */
	S1_HIT_YOU_S2_DMG(36),
	
	/**
	 * ID: 37<br>
	 * Message: $s1 hit you for $s2 damage.
	 */
	S1_HIT_YOU_S2_DMG2(37),
	
	/**
	 * ID: 38<br>
	 * Message: The TGS2002 event begins!
	 */
	
	/**
	 * ID: 39<br>
	 * Message: The TGS2002 event is over. Thank you very much.
	 */
	
	/**
	 * ID: 40<br>
	 * Message: This is the TGS demo: the character will immediately be restored.
	 */
	
	/**
	 * ID: 41<br>
	 * Message: You carefully nock an arrow.
	 */
	GETTING_READY_TO_SHOOT_AN_ARROW(41),
	
	/**
	 * ID: 42<br>
	 * Message: You have avoided $s1's attack.
	 */
	AVOIDED_S1S_ATTACK(42),

	/**
	 * ID: 43<br>
	 * Message: You have missed.
	 */
	MISSED_TARGET(43),

	/**
	 * ID: 44<br>
	 * Message: Critical hit!
	 */
	CRITICAL_HIT(44),
	
	/**
	 * ID: 45<br>
	 * Message: You have earned $s1 experience.
	 */
	EARNED_S1_EXP(45),
    
    /**
     * ID: 46<br>
     * Message: You use $s1.
     */
	USE_S1(46),
	
	/**
	 * ID: 47<br>
	 * Message: You begin to use a(n) $s1.
	 */
	BEGIN_USE_S1(47),
	
	/**
	 * ID: 48<br>
	 * Message: $s1 is not available at this time: being prepared for reuse.
	 */
	S1_NOT_AVAILABLE(48),
	
	/**
	 * ID: 49<br>
	 * Message: You have equipped your $s1.
	 */
	S1_EQUIPPED(49),
		
	/**
	 * ID: 50<br>
	 * Message: Your target cannot be found.
	 */
	TARGET_CANT_FOUND(50),

    /**
     * ID: 51<br>
     * Message: You cannot use this on yourself.
     */
	CANNOT_USE_ON_YOURSELF(51),

    /**
     * ID: 52<br>
     * Message: You have earned $s1 adena.
     */
	EARNED_ADENA(52),

    /**
     * ID: 53<br>
     * Message: You have earned $s2 $s1(s).
     */
    EARNED_S2_S1_S(53),

    /**
     * ID: 54<br>
     * Message: You have earned $s1.
     */
    EARNED_ITEM(54),
    
    /**
	 * ID: 55<br>
	 * Message: You have failed to pick up $s1 adena.
	 */
    FAILED_TO_PICKUP_S1_ADENA(55),

    /**
     * ID: 56<br>
     * Message: You have failed to pick up $s1.
     */
    FAILED_TO_PICKUP_S1(56),

    /**
     * ID: 57<br>
     * Message: You have failed to pick up $s2 $s1(s).
     */
    FAILED_TO_PICKUP_S2_S1_S(57),
    
    /**
     * ID: 58<br>
     * Message: You have failed to earn $s1 adena.
     */
    FAILED_EARN_S1_ADENA(58),
    
    /**
     * ID: 59<br>
     * Message: You have failed to earn $s1.
     */
    FAILED_EARN_S1(59),
    
    /**
	 * ID: 60<br>
	 * Message: You have failed to earn $s2 $s1(s).
	 */
    FAILED_EARN_S1_S2(60),
    
    /**
	 * ID: 61<br>
	 * Message: Nothing happened.
	 */
	NOTHING_HAPPENED(61),
	
	/**
	 * ID: 62<br>
	 * Message: Your $s1 has been successfully enchanted.
	 */
	S1_SUCCESSFULLY_ENCHANTED(62),

	/**
	 * ID: 63<br>
	 * Message: Your +$S1 $S2 has been successfully enchanted.
	 */
	S1_S2_SUCCESSFULLY_ENCHANTED(63),

	/**
	 * ID: 64<br>
	 * Message: The enchantment has failed! Your $s1 has been crystallized.
	 */
	ENCHANTMENT_FAILED_S1_EVAPORATED(64),

	/**
	 * ID: 65<br>
	 * Message: The enchantment has failed! Your +$s1 $s2 has been crystallized.
	 */
	ENCHANTMENT_FAILED_S1_S2_EVAPORATED(65),
	
	/**
	 * ID: 66<br>
	 * Message: $s1 has invited you to his/her party. Do you accept the invitation?
	 */
	S1_INVITED_YOU_TO_PARTY(66),
	
	/**
	 * ID: 67<br>
	 * Message: $s1 has invited you to the join the clan, $s2. Do you wish to join?
	 */
	S1_HAS_INVITED_YOU_TO_JOIN_THE_CLAN_S2(67),
	
	/**
	 * ID: 68<br>
	 * Message: Would you like to withdraw from the $s1 clan? If you leave, you will have to wait at least a day before joining another clan.
	 */
	WOULD_LIKE_WITHDRAW_S1_CLAN(68),
	
	/**
	 * ID: 69<br>
	 * Message: Would you like to dismiss $s1 from the clan? If you do so, you will have to wait at least a day before accepting a new member.
	 */
	WOULD_LIKE_DISMISS_S1_FROM_CLAN(69),
	
	/**
	 * ID: 70<br>
	 * Message: Do you wish to disperse the clan, $s1
	 */
	DO_WISH_DISPERSE_CLAN_S1(70),
	
	/**
	 * ID: 71<br>
	 * Message: How many of your $s1(s) do you wish to discard?
	 */
	HOW_MANY_YOUR_S1_DISCARD(71),
	
	/**
	 * ID: 72<br>
	 * Message: How many of your $s1(s) do you wish to move?
	 */
	HOW_MANY_YOUR_S1_MOVE(72),
	
	/**
	 * ID: 73<br>
	 * Message: How many of your $s1(s) do you wish to destroy?
	 */
	HOW_MANY_YOUR_S1_DESTROY(73),
	
	/**
	 * ID: 74<br>
	 * Message: Do you wish to destroy your $s1?
	 */
	WISH_DESTROY_S1(74),
	
	/**
	 * ID: 75<br>
	 * Message: ID does not exist.
	 */
	ID_NOT_EXIST(75),
	
	/**
	 * ID: 76<br>
	 * Message: Incorrect password.
	 */
	INCORRECT_PASSWORD(76),
	
	/**
	 * ID: 77<br>
	 * Message: You cannot create another character. 
	 * Please delete the existing character and try again.
	 */
	YOU_CANNOT_CREATE_MORE_CHAR(77),
	
	/**
	 * ID: 78<br>
	 * Message: Do you wish to delete $s1?
	 */
	WISH_DELETE_S1(78),
	
	/**
	 * ID: 79<br>
	 * Message: This name already exists.
	 */
	NAMING_NAME_ALREADY_EXISTS(79),

	/**
	 * ID: 80<br>
	 * Message: Names must be between 1-16 characters, excluding spaces or special characters.
	 */
	NAMING_CHARNAME_UP_TO_16CHARS(80),
	
	/**
	 * ID: 81 <br>
	 * Message: Please select your race.
	 */
	SELECT_RACE(81),
	
	/**
	 * ID: 82 <br>
	 * Message: Please select your occupation.
	 */
	SELECT_OCCUPATION(82),
	
	/**
	 * ID: 83 <br>
	 * Message: Please select your gender.
	 */
	SELECT_GENDER(83),
	
	/**
	 * ID: 84 <br>
	 * Message: You may not attack in a peaceful zone.
	 */
	CANT_ATK_PEACEZONE(84),
	
	/**
	 * ID: 85 <br>
	 * Message: You may not attack this target in a peaceful zone.
	 */
	TARGET_IN_PEACEZONE(85),
	
	/**
     * ID: 86<br>
     * Message: Please enter your ID
     */
	ENTER_ID(86),
	
	/**
     * ID: 87<br>
     * Message: Please enter your password.
     */
	ENTER_PASSWORD(87),
	
	/**
     * ID: 88<br>
     * Message: Your protocol version is different, please restart your client and run a full check.
     */
	PROTOCOL_ERROR(88),
	
	/**
     * ID: 89<br>
     * Message: Your protocol version is different, please continue.
     */
	PROTOCOL_ERROR2(89),
	
	/**
     * ID: 90<br>
     * Message: You are unable to connect to the server.
     */
	UNABLE_CONNECT_SERVER(90),
	
	/**
     * ID: 91<br>
     * Message: Please select your hairstyle.
     */
	SELECT_HAIR(91),
	
    /**
     * ID: 92<br>
     * Message: $s1 has worn off.
     */
    S1_HAS_WORN_OFF(92),
    
    /**
     * ID: 93<br>
     * Message: You do not have enough SP for this.
     */
    NO_ENOUGH_SP(93),
    
    /**
     * ID: 94<br>
     * Message: 2002 - 2007 Copyright NCsoft Corporation. All Rights Reserved.
     */
	COPYRIGHT(94),
	
	/**
	 * ID: 95<br>
	 * Message: You have earned $s1 experience and $s2 SP.
	 */
	YOU_EARNED_S1_EXP_AND_S2_SP(95),

	/**
	 * ID: 96<br>
	 * Message: Your level has increased!
	 */
	YOU_INCREASED_YOUR_LEVEL(96),

	/**
	 * ID: 97<br>
	 * Message: This item cannot be moved.
	 */
	CANNOT_MOVE_ITEM(97),
	
	/**
     * ID: 98<br>
     * Message: This item cannot be discarded.
     */
    CANNOT_DISCARD_THIS_ITEM(98),
    
    /**
     * ID: 99<br>
     * Message: This item cannot be traded or sold.
     */
    CANNOT_TRADE_THIS_ITEM(99),
    
    /**
     * ID: 100<br>
     * Message: $s1 has requested a trade. Do you wish to continue?
     */
    S1_REQUESTED_TRADE(100),
    
    /**
     * ID: 101<br>
     * Message: You cannot exit while in combat.
     */
    CANT_LOGOUT_WHILE_FIGHTING(101),

    /**
     * ID: 102<br>
     * Message: You cannot restart while in combat.
     */
    CANT_RESTART_WHILE_FIGHTING(102),
	
    /**
     * ID: 103<br>
     * Message: This ID is currently logged in.
     */
    THIS_ID_ALREADY_LOGGED(103),
    
	/**
     * ID: 104<br>
     * Message: You may not equip items while casting or performing a skill.
     */
	CANNOT_USE_ITEM_WHILE_USING_MAGIC(104),
	
	/**
	 * ID: 105<br>
	 * Message: You have invited $s1 to your party.
	 */
	YOU_INVITED_S1_TO_PARTY(105),
	
	/**
	 * ID: 106<br>
	 * Message: You have joined $s1's party.
	 */
	YOU_JOINED_S1_PARTY(106),

	/**
	 * ID: 107<br>
	 * Message: $s1 has joined the party.
	 */
	S1_JOINED_PARTY(107),

	/**
	 * ID: 108<br>
	 * Message: $s1 has left the party.
	 */
	S1_LEFT_PARTY(108),
	
    /**
     * ID: 109<br>
     * Message: Invalid target.
     */
    INCORRECT_TARGET(109),
	
	/**
	 * ID: 110<br>
	 * Message: The effect of $s1 flow through you.
	 */
	YOU_FEEL_S1_EFFECT(110),
	
	/**
	 * ID: 111<br>
	 * Message: Your shield defense has succeeded.
	 */
	SHIELD_DEFENCE_SUCCESSFULL(111),
	
	/**
	 * ID: 112<br>
	 * Message: You may no longer adjust items in the trade because the trade has been confirmed.
	 */
	NOT_ENOUGH_ARROWS(112),
	
	/**
	 * ID: 113<br>
	 * Message: $s1 cannot be used due to unsuitable terms.
	 */
	S1_CANNOT_BE_USED(113),
	
    /**
     * ID: 114<br>
     * Message: You have entered the shadow of the Mother Tree.
     */
 	ENTER_SHADOW_MOTHER_TREE(114),

 	/**
 	 * ID: 115<br>
 	 * Message: You have left the shadow of the Mother Tree.
 	 */
 	EXIT_SHADOW_MOTHER_TREE(115),
	
 	/**
 	 * ID: 116<br>
 	 * Message: You have entered a peaceful zone.
 	 */
 	YOU_ENTERED_PEACE_ZONE(116),
 	
 	/**
	 * ID: 117<br>
	 * Message: You have left the peaceful zone.
	 */
 	YOU_LEFT_PEACE_ZONE(117),
	/**
	 * ID: 118<br>
	 * Message: You have requested a trade with $s1
	 */
	REQUEST_S1_FOR_TRADE(118),

	/**
	 * ID: 119<br>
	 * Message: $s1 has denied your request to trade.
	 */
	S1_DENIED_TRADE_REQUEST(119),

	/**
	 * ID: 120<br>
	 * Message: You begin trading with $s1.
	 */
	BEGIN_TRADE_WITH_S1(120),

	/**
	 * ID: 121<br>
	 * Message: $s1 has confirmed the trade.
	 */
	S1_CONFIRMED_TRADE(121),
	
	/**
	 * ID: 122<br>
	 * Message: You may no longer adjust items in the trade because the trade has been confirmed.
	 */
	NO_ADJUST_ITEM_TRADE_COMFIRMED(122),
	
	/**
	 * ID: 123<br>
	 * Message: Your trade is successful.
	 */
	TRADE_SUCCESSFUL(123),

	/**
	 * ID: 124<br>
	 * Message: $s1 has canceled the trade.
	 */
	S1_CANCELED_TRADE(124),
	
	/**
	 * ID: 125<br>
	 * Message: Do you wish to exit the game?	
	 */
	YOU_WISH_EXIT_GAME(125),
	
	/**
	 * ID: 126<br>
	 * Message: Do you wish to exit to the character select screen?
	 */
	YOU_WISH_RESTART(126),
	
	/**
	 * ID: 127<br>
	 * Message: You have been disconnected from the server. Please login again.
	 */
	YOU_HAVE_BEEN_DESCONNECTED(127),
	
	/**
	 * ID: 128<br>
	 * Message: Your character creation has failed.
	 */
	CHARACTER_CREATE_FAILED(128),
	
	/**
	 * ID: 129<br>
	 * Message: Your inventory is full.
	 */
    SLOTS_FULL(129),
    
    /**
	 * ID: 130<br>
	 * Message: Your warehouse is full.
	 */
    WAREHOUSE_FULL(130),
    
    /**
	 * ID: 131<br>
	 * Message: $s1 has logged in.
	 */
    S1_HAS_LOGGED(131),
    
    /**
     * ID: 132<br>
     * Message: $s1 has been added to your friends list.
     */
    S1_ADDED_TO_FRIENDS(132),
    /**
     * ID: 133<br>
     * Message: $s1 has been removed from your friends list.
     */
    S1_REMOVED_FROM_YOUR_FRIENDS_LIST(133),

    /**
     * ID: 134<br>
     * Message: Please check your friends list again.
     */
    PLEACE_CHECK_YOUR_FRIEND_LIST_AGAIN(134),

	/**
	 * ID: 135<br>
	 * Message: $s1 did not reply to your invitation), your invite has been canceled.
	 */
	S1_DID_NOT_REPLY(135),

	/**
	 * ID: 136<br>
	 * Message: You have not replied to $s1's invitation), the offer has been canceled.
	 */
	YOU_DID_NOT_REPLY(136),
	
	/**
	 * ID: 137<br>
	 * Message: There are no more items in the shortcut.
	 */
	NO_MORE_ITEMS_ON_SHORTCUT(137),
	
	/**
	 * ID: 138<br>
	 * Message: Designate shortcut.
	 */
	DESIGNATE_SHORTCUT(138),
	
	/**
	 * ID: 139<br>
	 * Message: $s1 has resisted your $s2.
	 */
    S1_WAS_UNAFFECTED_BY_S2(139),
    
    /**
	 * ID: 140<br>
	 * Message: Your skill was removed due to a lack of MP.
	 */
	SKILL_REMOVED_DUE_LACK_MP(140),
	
    /**
	 * ID: 141<br>
	 * Message: Once the trade is confirmed, the item cannot be moved again.
	 */
    TRADE_CONFIMED_NO_MOVE_ITEM(141),
    
    /**
	 * ID: 142<br>
	 * Message: You are already trading with someone.
	 */
	ALREADY_TRADING(142),
    
	/**
	 * ID: 143<br>
	 * Message: $s1 is already trading with another person. Please try again later.
	 */
	S1_ALREADY_TRADING(143),
	
	/**
	 * ID: 144<br>
	 * Message: That is the incorrect target.
	 */
	TARGET_IS_INCORRECT(144),
    
    /**
	 * ID: 145<br>
	 * Message: That player is not online.
	 */
	TARGET_IS_NOT_FOUND_IN_THE_GAME(145),
	
	/**
	 * ID: 146<br>
	 * Message: Chatting is now permitted.
	 */
	CHATING_PERMITTED(146),
	
	/**
	 * ID: 147<br>
	 * Message: Chatting is currently prohibited.
	 */
	CHATING_PROHIBITED(147),
	
	/**
	 * ID: 148<br>
	 * Message: You cannot use quest items.
	 */
	YOU_CANNOT_USE_QUEST_ITEMS(148),
	
	/**
	 * ID: 149<br>
	 * Message: You cannot pick up or use items while trading.
	 */
    CANNOT_USE_ITEM_WHILE_TRADING(149),
	
    /**
     * ID: 150<br>
     * Message: You cannot discard or destroy an item while trading at a private store.
     */
    CANNOT_DISCARD_DESTROY_ITEM_WHILE_TRADING(150),
    
	/**
     * ID: 151<br>
     * Message: That is too far from you to discard.
     */
    CANNOT_DISCARD_DISTANCE_TOO_FAR(151),
    
    /**
	 * ID: 152<br>
	 * Message: You have invited the wrong target.
	 */
	YOU_HAVE_INVITED_THE_WRONG_TARGET(152),

	/**
	 * ID: 153<br>
	 * Message: $s1 is busy. Please try again later.
	 */
    S1_IS_BUSY_TRY_LATER(153),
	
	/**
	 * ID: 154<br>
	 * Message: Only the leader can give out invitations.
	 */
	ONLY_LEADER_CAN_INVITE(154),
	
	/**
	 * ID: 155<br>
	 * Message: The party is full.
	 */
	PARTY_FULL(155),
    
    /**
     * ID: 156<br>
     * Message: Drain was only 50 percent successful.
     */
    DRAIN_HALF_SUCCESFUL(156),
    
    /**
     * ID: 157<br>
     * Message: You resisted $s1's drain.
     */
    RESISTED_S1_DRAIN(157),

    /**
     * ID: 158<br>
     * Message: Your attack has failed.
     */
    ATTACK_FAILED(158),

    /**
     * ID: 159<br>
     * Message: You have resisted $s1's magic.
     */
    RESISTED_S1_MAGIC(159),
    
    /**
	 * ID: 160<br>
	 * Message: $s1 is a member of another party and cannot be invited.
	 */
	S1_IS_ALREADY_IN_PARTY(160),

	/**
	 * ID: 161<br>
	 * Message: That player is not currently online.
	 */
	INVITED_USER_NOT_ONLINE(161),
	
	/**
	 * ID: 162<br>
	 * Message: Warehouse is too far.
	 */
	WAREHOUSE_FAR(162),
	
	/**
	 * ID: 163<br>
	 * Message: You cannot destroy it because the number is incorrect.
	 */
    CANNOT_DESTROY_NUMBER_INCORRECT(163),
    
    /**
	 * ID: 164<br>
	 * Message: Waiting for another reply.
	 */
	WAITING_FOR_ANOTHER_REPLY(164),
	
	 /**
     * ID: 165<br>
     * Message: You cannot add yourself to your own friend list.
     */
    YOU_CANNOT_ADD_YOURSELF_TO_OWN_FRIEND_LIST(165),

    /**
     * ID: 166<br>
     * Message: Friend list is not ready yet. Please register again later.
     */
    FRIEND_LIST_NOT_READY_YET_REGISTER_LATER(166),

    /**
     * ID: 167<br>
     * Message: $s1 is already on your friend list.
     */
    S1_ALRADY_ON_LIST(167),

    /**
     * ID: 168<br>
     * Message: $s1 has requested to become friends.
     */
    S1_REQUESTED_TO_BECOME_FRIENDS(168),

    /**
     * ID: 169<br>
     * Message: Accept friendship 0/1 (1 to accept, 0 to deny)
     */
    
    /**
     * ID: 170<br>
     * Message: The user who requested to become friends is not found in the game.
     */
    THE_USER_YOU_REQUESTED_IS_NOT_IN_GAME(170),

    /**
     * ID: 171<br>
     * Message: $s1 is not on your friend list.
     */
    S1_NOT_ON_YOUR_FRIENDS_LIST(171),
	
    /**
	 * ID: 172<br>
	 * Message: You lack the funds needed to pay for this transaction.
	 */
    YOU_LACK_FUNDS_PAY_RANSACTION(172),
    
    /**
	 * ID: 173<br>
	 * Message: You lack the funds needed to pay for this transaction.
	 */
    YOU_LACK_FUNDS_PAY_RANSACTION2(173),
    
    /**
	 * ID: 174<br>
	 * Message: That person's inventory is full.
	 */
    THAT_PERSON_INVENTORY_FULL(174),
    
    /**
	 * ID: 175<br>
	 * Message: That skill has been de-activated as HP was fully recovered.
	 */
    THAT_SKILL_DEACTIVED_HP_RECOVERED(175),
    
	/**
	 * ID: 176<br>
	 * Message: That person is in message refusal mode.
	 */
	THE_PERSON_IS_IN_MESSAGE_REFUSAL_MODE(176),

	/**
	 * ID: 177<br>
	 * Message: Message refusal mode.
	 */
	MESSAGE_REFUSAL_MODE(177),

	/**
	 * ID: 178<br>
	 * Message: Message acceptance mode.
	 */
	MESSAGE_ACCEPTANCE_MODE(178),
	
	/**
     * ID: 179<br>
     * Message: You cannot discard those items here.
     */
	CANNOT_DESCARD_ITEM_HERE(179),
	
	/**
     * ID: 180<br>
     * Message: You have $s1 day(s) left until deletion.  Do you wish to cancel this action?
     */
	YOU_HAVE_S1_DAYS_UNTIL_DELETION(180),
	
    /**
     * ID: 181<br>
     * Message: Cannot see target.
     */
    CANT_SEE_TARGET(181),
    
    /**
     * ID: 182<br>
     * Message: Do you want to quit the current quest?
     */
    DO_YOU_WANT_QUIT_CURRENT_QUEST(182),
    
    /**
     * ID: 183<br>
     * Message: There are too many users on the server. Please try again later.
     */
    THERE_ARE_MANY_USERS(183),
    
    /**
     * ID: 184<br>
     * Message: Please try again later.
     */
    PLEASE_TRY_AGAIN_LATER(184),
    
    /**
     * ID: 185<br>
     * Message: You must first select a user to invite to your party.
     */
    YOU_MUST_SELECT_USER_TO_INVITE_TO_PARTY(185),
    
    /**
     * ID: 186<br>
     * Message: You must first select a user to invite to your clan.
     */
    YOU_MUST_SELECT_USER_TO_INVITE_TO_CLAN(186),
    
    /**
     * ID: 187<br>
     * Message: Select user to expel.
     */
    SELECT_USER_TO_EXPEL(187),
    
    /**
     * ID: 188<br>
     * Message: Please create your clan name.
     */
    CREATE_YOUR_CLAN_NAME(188),
    
	/**
	 * ID: 189<br>
	 * Message: Your clan has been created.
	 */
	CLAN_CREATED(189),
    
    /**
	 * ID: 190<br>
	 * Message: You have failed to create a clan.
	 */
	FAILED_TO_CREATE_CLAN(190),
	
	/**
	 * ID: 191<br>
	 * Message: Clan member $s1 has been expelled.
	 */
	CLAN_MEMBER_S1_EXPELLED(191),
	
	/**
	 * ID: 192<br>
	 * Message: You have failed to expel $s1 from the clan.
	 */
	FAILED_TO_EXPEL_S1_FROM_CLAN(192),
	
	/**
	 * ID: 193<br>
	 * Message: Clan has dispersed.
	 */
	CLAN_HAS_DISPERSED(193),
	
	/**
	 * ID: 194<br>
	 * Message: You have failed to disperse the clan.
	 */
	FAILED_DISPERSE_CLAN(194),
	/**
	 * ID: 195<br>
	 * Message: Entered the clan.
	 */
	ENTERED_THE_CLAN(195),

	/**
	 * ID: 196<br>
	 * Message: $s1 declined your clan invitation.
	 */
	S1_REFUSED_TO_JOIN_CLAN(196),
	
	/**
	 * ID: 197<br>
	 * Message: You have withdrawn from the clan.
	 */
	YOU_HAVE_WITHDRAWN_FROM_CLAN(197),
	
	/**
	 * ID: 198<br>
	 * Message: You have failed to withdraw from the $s1 clan.
	 */
	FAILED_WITHDRAW_S1_CLAN(198),
	
	/**
	 * ID: 199<br>
	 * Message: You have recently been dismissed from a clan. You are not allowed to join another clan for 24-hours.
	 */
	CLAN_MEMBERSHIP_TERMINATED(199),
	
    /**
	 * ID: 200<br>
	 * Message: You have withdrawn from the party.
	 */
	YOU_LEFT_PARTY(200),
	
	/**
	 * ID: 201<br>
	 * Message: $s1 was expelled from the party.
	 */
	S1_EXPELLED_FROM_PARTY(201),
	
	/**
	 * ID: 202<br>
	 * Message: You have been expelled from the party.
	 */
	YOU_HAVE_BEEN_EXPELLED_FROM_PARTY(202),
	
	/**
	 * ID: 203<br>
	 * Message: The party has dispersed.
	 */
	PARTY_DISPERSED(203),
	
	/**
     * ID: 204<br>
     * Message: Incorrect name. Please try again.
     */
	INCORRECT_NAME(204),
	
	/**
     * ID: 205<br>
     * Message: Incorrect character name.  Please try again.
     */
	INCORRECT_CHARACTER_NAME(205),
	
	/**
     * ID: 206<br>
     * Message: Please enter the name of the clan you wish to declare war on.
     */
	ENTER_CLAN_NAME_TO_DECLARE_WAR(206),
	
	/**
     * ID: 207<br>
     * Message: $s2 of the clan $s1 requests declaration of war. Do you accept?
     */
	S2_OF_CLAN_S1_REQUEST_WAR(207),
	
	/**
     * ID: 208<br>
     * Message: Please include file type when entering file path.
     */
	INCLUDE_FILE_TYPE(208),
	
	/**
     * ID: 209<br>
     * Message: The size of the image file is inappropriate.  Please adjust to 16*12.
     */
	SIZE_THE_IMAGE_INAPPROPRIATE(209),
	
	/**
     * ID: 210<br>
     * Message: Cannot find file. Please enter precise path.
     */
	CANNOT_FIND_FILE(210),
	
	/**
     * ID: 211<br>
     * Message: You may only register a 16 x 12 pixel, 256-color BMP
     */
	YOU_MAY_ONLY_REGISTER_BMP(211),
	
	 /**
     * ID: 212<br>
     * Message: You are not a clan member and cannot perform this action.
     */
    YOU_ARE_NOT_A_CLAN_MEMBER(212),
	
    /**
     * ID: 213<br>
     * Message: Not working. Please try again later.
     */
    NOT_WORKING(213),
    
	/**
     * ID: 214<br>
     * Message: Your title has been changed.
     */
    TITLE_CHANGED(214),
    
    /**
     * ID: 215<br>
     * Message: War with the $s1 clan has begun.
     */
    WAR_WITH_THE_S1_CLAN_HAS_BEGUN(215),

    /**
     * ID: 216<br>
     * Message: War with the $s1 clan has ended.
     */
    WAR_WITH_THE_S1_CLAN_HAS_ENDED(216),

    /**
     * ID: 217<br>
     * Message: You have won the war over the $s1 clan!
     */
    YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN(217),

    /**
     * ID: 218<br>
     * Message: You have surrendered to the $s1 clan.
     */
    YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN(218),

    /**
	 * ID: 219<br>
	 * Message: Your clan leader has died. You have been defeated by the $s1 Clan.
	 */
    YOUR_CLAN_LIDER_DIED(219),
    
    /**
	 * ID: 220<br>
	 * Message: You have $s1 minutes left until the clan war ends.
	 */
    YOU_HAVE_S1_MINUTES_UNTIL_END_CLAN_WAR(220),
    
    /**
	 * ID: 221<br>
	 * Message: The time limit for the clan war is up. War with the $s1 clan is over.
	 */
    TIME_CLAN_WAR_OVER_WITH_S1_CLAN(221),
    
	/**
	 * ID: 222<br>
	 * Message: $s1 has joined the clan.
	 */
	S1_HAS_JOINED_CLAN(222),
	
	/**
	 * ID: 223<br>
	 * Message: $s1 has withdrawn from the clan.
	 */
	S1_HAS_WITHDRAWN_FROM_THE_CLAN(223),
	
	/**
	 * ID: 224<br>
	 * Message: $s1 did not respond: Invitation to the clan has been cancelled.
	 */
	S1_DID_NOT_RESPOND_TO_CLAN_INVITATION(224),

	/**
	 * ID: 225<br>
	 * Message: You didn't respond to $s1's invitation: joining has been cancelled.
	 */
	YOU_DID_NOT_RESPOND_TO_S1_CLAN_INVITATION(225),
	
	 /**
     * ID: 226<br>
     * Message: The $s1 clan did not respond: war proclamation has been refused.
     */
	THE_S1_CLAN_NOT_RESPOND_WAR(226),
	
	 /**
     * ID: 227<br>
     * Message: Clan war has been refused because you did not respond to $s1 clan's war proclamation.
     */
	CLAN_WAR_REFUSED_YOU_NOT_RESPOND_REQUEST_S1_CLAN(227),
    /**
     * ID: 228<br>
     * Message: Request to end war has been denied.
     */
    REQUEST_TO_END_WAR_HAS_BEEN_DENIED(228),
	
	/**
	 * ID: 229<br>
	 * Message: You do not meet the criteria ir order to create a clan.
	 */
	YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN(229),
	
	/**
	 * ID: 230<br>
	 * Message: You must wait 10 days before creating a new clan.
	 */
	YOU_MUST_WAIT_XX_DAYS_BEFORE_CREATING_A_NEW_CLAN(230),
	
	/**
	 * ID: 231<br>
	 * Message: After a clan member is dismissed from a clan, the clan
	 * must wait at least a day before accepting a new member.
	 */
	YOU_MUST_WAIT_BEFORE_ACCEPTING_A_NEW_MEMBER(231),
	
	/**
	 * ID: 232<br>
	 * Message: After leaving or having been dismissed from a clan,
	 * you must wait at least a day before joining another clan.
	 */
	YOU_MUST_WAIT_BEFORE_JOINING_ANOTHER_CLAN(232),
	
	/**
	 * ID: 233<br>
	 * Message: The Academy/Royal Guard/Order of Knights is full 
	 * and cannot accept new members at this time.
	 */
	SUBCLAN_IS_FULL(233),
	
	/**
	 * ID: 234<br>
	 * Message: The target must be a clan member.
	 */
	TARGET_MUST_BE_IN_CLAN(234),
	
	/**
	 * ID: 235<br>
	 * Message: You are not authorized to bestow these rights.
	 */
	YOU_NOT_AUTHORIZED_BESTOW_RIGHTS(235),
	
	/**
	 * ID: 236<br>
	 * Message: ONLY_THE_CLAN_LEADER_IS_ENABLED.
	 */
	ONLY_THE_CLAN_LEADER_IS_ENABLED(236),
	
	/**
	 * ID: 237<br>
	 * Message: The clan leader could not be found.
	 */
	CLAN_LIDER_NOT_BE_FOUND(237),
	
	/**
	 * ID: 238<br>
	 * Message: Not joined in any clan.
	 */
	NOT_JOINED_IN_ANY_CLAN(238),
	
	/**
	 * ID: 239<br>
	 * Message: The clan leader cannot withdraw.
	 */
	CLAN_LEADER_CANNOT_WITHDRAW(239),
    
	/**
	 * ID: 240<br>
	 * Message: Currently involved in clan war.
	 */
	CURRENTLY_INVOLVED_CLAN_WAR(240),
	
	/**
	 * ID: 241<br>
	 * Message: Leader of the $s1 Clan is not logged in.
	 */
	LEADER_CLAN_S1_NOT_LOGGED(241),
	
    /**
	 * ID: 242<br>
	 * Message: You must select a target
	 */
	YOU_MUST_SELECT_A_TARGET(242),
	
	/**
     * ID: 243<br>
     * Message: You cannot declare war on an allied clan.
     */
	CANNOT_DECLARE_WAR_ON_ALLIED_CLAN(243),
	
	/**
     * ID: 244<br>
     * Message: You are not allowed to issue this challenge.
     */
	NOT_ALLOWED_ISSUE_CHALLENGE(244),
	
	/**
     * ID: 245<br>
     * Message: 5 days has not passed since you were refused war. Do you wish to continue?
     */
	FIVE_DAYS_NOT_PASSED_SINCE_YOU_REFUSED_WAR(245),
	
	/**
     * ID: 246<br>
     * Message: That clan is currently at war.
     */
	THAT_CLAN_CURRENTLY_AT_WAR(246),
	
	/**
     * ID: 247<br>
     * Message: You have already been at war with the $s1 clan: 
     * 5 days must pass before you can challenge this clan again.
     */
	YOU_ALREADY_BEEN_WAR_S1_CLAN_MUST_PASS_5DAYS(247),
	
	/**
     * ID: 248<br>
     * Message: You cannot proclaim war: the $s1 clan does not have enough members.
     */
	CANNOT_PROCLAIM_WAR_S1_CLAN_NOT_ENOUGH_MEMBERS(248),
	
	/**
     * ID: 249<br>
     * Message: Do you wish to surrender to the $s1 clan?
     */
	YOU_WISH_SURREND_S1_CLAN(249),
	
    /**
     * ID: 250<br>
     * Message: You have personally surrendered to the $s1 clan.
     * You are no longer participating in this clan war.
     */
    YOU_HAVE_PERSONALLY_SURRENDERED_TO_THE_S1_CLAN(250),
	
    /**
	 * ID: 251<br>
	 * Message: You cannot proclaim war: you are at war with another clan.
	 */
    CANNOT_PROCLAIM_WAR_ALREADY_WITH_ANOTHER_CLAN(251),
    
    /**
	 * ID: 252<br>
	 * Message: Enter the name of clan to surrender to.
	 */
    ENTER_CLAN_NAME_TO_SURRENDER(252),
    
    /**
	 * ID: 253<br>
	 * Message: Enter the name of the clan you wish to end the war with.
	 */
    ENTER_CLAN_NAME_WISH_END_WAR(253),
    
    /**
	 * ID: 254<br>
	 * Message: A clan leader cannot personally surrender.
	 */
    CLAN_LEADER_CANNOT_SURRENDER(254),
    
    /**
	 * ID: 255<br>
	 * Message: The $s1 Clan has requested to end war. Do you agree?
	 */
    THE_S1_CLAN_REQUEST_END_WAR(255),
    
    /**
	 * ID: 256<br>
	 * Message: Enter Title
	 */
    ENTER_TITLE(256),
    
    /**
	 * ID: 257<br>
	 * Message: Do you offer the $s1 clan a proposal to end the war?
	 */
    YOU_OFFER_S1_CLAN_END_WAR(257),
    
    /**
	 * ID: 258<br>
	 * Message: You are not involved in a clan war.
	 */
    YOU_ARE_NOT_INVOLVED_CLAN_WAR(258),
    
    /**
	 * ID: 259<br>
	 * Message: Select clan members from list.
	 */
    SELECT_CLAN_MENBER_FROM_LIST(259),
    
    /**
	 * ID: 260<br>
	 * Message: Fame level has decreased: 5 days have not passed since you were refused war.
	 */
    FAME_LEVEL_DECREASED(260),
    
	/**
	 * ID: 261<br>
	 * Message: Clan name is invalid.
	 */
	CLAN_NAME_INCORRECT(261),

	/**
	 * ID: 262<br>
	 * Message: Clan name's length is incorresct.
	 */
	CLAN_NAME_TOO_LONG(262),
	
	/**
	 * ID: 263<br>
	 * Message: You have already requested the dissolution of your clan.
	 */
	DISSOLUTION_IN_PROGRESS(263),
	
	/**
	 * ID: 264<br>
	 * Message: You cannot dissolve a clan while engaged in a war.
	 */
	CANNOT_DISSOLVE_WHILE_IN_WAR(264),
	
	/**
	 * ID: 265<br>
	 * Message: You cannot dissolve a clan during a siege or while protecting a castle.
	 */
	CANNOT_DISSOLVE_WHILE_IN_SIEGE(265),
	
	/**
	 * ID: 266<br>
	 * Message: You cannot dissolve a clan while owning a clan hall or castle.
	 */
	CANNOT_DISSOLVE_WHILE_OWNING_CLAN_HALL_OR_CASTLE(266),
	
	/**
	 * ID: 267<br>
	 * Message: There are no requests to disperse.
	 */
	NO_REQUESTS_TO_DISPERSE(267),
	
	/**
	 * ID: 268<br>
	 * Message: That player already belongs to another clan.
	 */
	PLAYER_ALREADY_ANOTHER_CLAN(268),
	
	/**
	 * ID: 269<br>
	 * Message: You cannot dismiss yourself.
	 */
	YOU_CANNOT_DISMISS_YOURSELF(269),
	
	/**
	 * ID: 270<br>
	 * Message:You have already surrendered.
	 */
	YOU_ALREADY_SURRENDERED(270),
	
	/**
	 * ID: 271<br>
	 * Message: A player can only be granted a title if the clan is level 3 or above
	 */
	CLAN_LVL_3_NEEDED_TO_ENDOWE_TITLE(271),
	
	/**
	 * ID: 272<br>
	 * Message: A clan crest can only be registered when the clan's skill level is 3 or above.
	 */
	CLAN_LVL_3_NEEDED_TO_SET_CREST(272),
	
	/**
	 * ID: 273<br>
	 * Message: A clan war can only be declared when a clan's skill level is 3 or above
	 */
	ONLY_CLAN_LVL_3_DECLARES_WAR(273),
	
	/**
	 * ID: 274<br>
	 * Message: Your clan's skill level has increased.
	 */
	CLAN_LEVEL_INCREASED(274),
    
	/**
	 * ID: 275<br>
	 * Message: Clan has failed to increase skill level.
	 */
	CLAN_FAILED_INCREASE_SKILL_LVL(275),
	
    /**
	 * ID: 276<br>
	 * Message: You do not have the necessary materials or prerequisites to learn this skill.
	 */
	ITEM_MISSING_TO_LEARN_SKILL(276),

	/**
	 * ID: 277<br>
	 * Message: You have earned $s1.
	 */
	LEARNED_SKILL_S1(277),

	/**
	 * ID: 278<br>
	 * Message: You do not have enough SP to learn this skill.
	 */
	NOT_ENOUGH_SP_TO_LEARN_SKILL(278),
	
	/**
	 * ID: 279<br>
	 * Message: You do not have enough adena.
	 */
	YOU_NOT_ENOUGH_ADENA(279),
	
	/**
     * ID: 280<br>
     * Message: You do not have any items to sell.
     */
	YOU_NOT_HAVE_ITEMS_TO_SELL(280),
	
	/**
     * ID: 281<br>
     * Message: You do not have enough adena to pay the fee.
     */
	NOT_ENOUGH_ADENA(281),
	
	/**
     * ID: 282<br>
     * Message: You have not deposited any items in your warehouse.
     */
    NO_ITEM_DEPOSITED_IN_WH(282),
    
    /**
     * ID: 283<br>
     * Message: You have entered a combat zone.
     */
    ENTERED_COMBAT_ZONE(283),

    /**
     * ID: 284<br>
     * Message: You have left a combat zone.
     */
    LEFT_COMBAT_ZONE(284),

    /**
     * ID: 285<br>
     * Message: Clan $s1 has succeeded in engraving the ruler!
     */
    CLAN_S1_HAS_SUCCESS_ENGRAVE(285),
    
    /**
     * ID: 286<br>
     * Message: Your base is being attacked.
     */
    YOUR_BASE_BEING_ATTACKED(286),
    
    /**
     * ID: 287<br>
     * Message: The opposing clan has started to engrave the monument!
     */
    OPPOSING_CLAN_STARTED_ENGRAVE(287),
    
    /**
     * ID: 288<br>
     * Message: The castle gate has been broken down.
     */
    CASTLE_GATE_BROKEN(288),
    
    /**
     * ID: 289<br>
     * Message: You cannot build another headquarters since one already exists.
     */
    HEADQUARTER_ALREADY_EXISTS(289),
    
    /**
     * ID: 290<br>
     * Message: You cannot set up a base here.
     */
    YOU_CANNOT_CREATE_BASE_HERE(290),
    
    /**
     * ID: 291<br>
     * Message: Clan $s1 is victorious over $s2's castle siege!
     */
    CLAN_S1_VICTORIOUS_S2_CASTLE(291),
    
    /**
     * ID: 292<br>
     * Message: $s1 has announced the castle siege time.
     */
    S1_ANNOUNCED_CASTLE_SIEGE_TIME(292),
    
    /**
     * ID: 293<br>
     * Message: The registration term for $s1 has ended.
     */
    REGISTRATION_PERIOD_FOR_S1_ENDED(293),
    
    /**
     * ID: 294<br>
     * Message: Because your clan is not currently on the 
     * offensive in a Clan Hall siege war, it cannot summon its base camp.
     */
    CLAN_DEFENSIVE_CLAN_HALL_SIEGE_CANNOT_SUMMON_BASE_CAMP(294),
    
    /**
     * ID: 295<br>
     * Message: $s1's siege was canceled because there were no clans that participated.
     */
    S1_SIEGE_WAS_CANCELED_BECAUSE_NO_CLANS_PARTICIPATED(295),
	
	/**
	 * ID: 296<br>
	 * Message: You received $s1 damage from taking a high fall.
	 */
	FALL_DAMAGE_S1(296),

	/**
	 * ID: 297<br>
	 * Message: You have taken $s1 damage because you were unable to breathe.
	 */
	DROWN_DAMAGE_S1(297),

	/**
	 * ID: 298<br>
	 * Message: You have dropped $s1.
	 */
	YOU_DROPPED_S1(298),
	
	/**
	 * ID: 299<br>
	 * Message: $s1 has obtained $s3 $s2.
	 */
	S1_PICKED_UP_S2_S3(299),

	/**
	 * ID: 300<br>
	 * Message: $s1 has obtained $s2.
	 */
	S1_PICKED_UP_S2(300),
    
    /**
	 * ID: 301<br>
	 * Message: $s2 $s1 has disappeared.
	 */
	DISSAPEARED_ITEM(301),
	
    /**
     * ID: 302<br>
     * Message: $s1 has disappeared.
     */
    S1_DISAPPEARED(302),
	
	/**
	 * ID: 303<br>
	 * Message: Select item to enchant.
	 */
	SELECT_ITEM_TO_ENCHANT(303),

	/**
	 * ID: 304<br>
	 * Message: Clan member $s1 has logged into game.
	 */
	CLAN_MEMBER_S1_LOGGED_IN(304),
	
	/**
	 * ID: 305<br>
	 * Message: The player declined to join your party.
	 */
	PLAYER_DECLINED(305),
	
	/**
	 * ID: 306<br>
	 * Message: You have failed to delete the character.
	 */
	FAILED_DELETE_CHAR(306),
	
	/**
	 * ID: 307<br>
	 * Message: You cannot trade with a warehouse keeper.
	 */
	YOU_CANNOT_TRADE_WAREHOUSE_KEEPER(307),
	
	/**
	 * ID: 308<br>
	 * Message: The player declined your clan invitation.
	 */
	PLAYER_DECLINED_CLAN_INVITATION(308),
	/**
	 * ID: 309<br>
	 * Message: You have succeeded in expelling the clan member.
	 */
	YOU_HAVE_SUCCEEDED_IN_EXPELLING_CLAN_MEMBER(309),
	
	/**
     * ID: 310<br>
     * Message: You have failed to expel the clan member.
     */
	FAILED_EXPEL_CLAN_MEMBER(310),
	
	/**
     * ID: 311<br>
     * Message: The clan war declaration has been accepted.
     */
	CLAN_WAR_ACCEPTED(311),
	
	/**
     * ID: 312<br>
     * Message: The clan war declaration has been refused.
     */
	CLAN_WAR_REFUSED(312),
	
	/**
     * ID: 313<br>
     * Message: The cease war request has been accepted.
     */
	CEASE_WAR_ACCEPTED(313),
	
	/**
     * ID: 314<br>
     * Message: You have failed to surrender.
     */
	FAILED_SURRENDER(314),
	
	/**
     * ID: 315<br>
     * Message: You have failed to personally surrender
     */
	FAILED_PERSONALLY_SURRENDER(315),
	
	/**
     * ID: 316<br>
     * Message: You have failed to withdraw from the party.
     */
	FAILED_WITHDRAW_FROM_PARTY(316),
	
	/**
     * ID: 317<br>
     * Message: You have failed to expel the party member.
     */
	FAILED_EXPEL_PARTY_MEMBER(317),
	
	/**
     * ID: 318<br>
     * Message: You have failed to disperse the party.
     */
	FAILED_DISPERSE_PARTY(318),
	
	/**
     * ID: 319<br>
     * Message: This door cannot be unlocked.
     */
    UNABLE_TO_UNLOCK_DOOR(319),

    /**
     * ID: 320<br>
     * Message: You have failed to unlock the door.
     */
    FAILED_TO_UNLOCK_DOOR(320),
    
    /**
     * ID: 321<br>
     * Message: It is not locked.
     */
    NO_LOCKED(321),
    
    /**
     * ID: 322<br>
     * Message: Please decide on the sales price.
     */
    DECIDE_SALES_PRICE(322),
    
    /**
     * ID: 323<br>
     * Message: Your force has increased to $s1 level.
     */
    FORCE_INCREASED_TO_S1(323),

    /**
     * ID: 324<br>
     * Message: Your force has reached maximum capacity.
     */
    FORCE_MAXLEVEL_REACHED(324),
    
    /**
	 * ID: 325<br>
	 * Message: The corpse has already disappeared.
	 */
    CORPSE_ALREADY_DISAPPEARED(325),
    
    /**
	 * ID: 326<br>
	 * Message: Select target from list.
	 */
    SELECT_TARGET_FROM_LIST(326),
    
    /**
	 * ID: 327<br>
	 * Message: You cannot exceed 80 characters.
	 */
    CANNOT_EXCEED_80_CHARACTERS(327),
    
    /**
	 * ID: 328<br>
	 * Message: Please input title using less than 128 characters.
	 */
    INPUT_TITLE_LESS_THAN_128_CHARACTERS(328),
    
    /**
	 * ID: 329<br>
	 * Message: Please input contents using less than 3000 characters.
	 */
    INPUT_CONTENTS_LESS_THAN_3000_CHARACTERS(329),
    
    /**
	 * ID: 330<br>
	 * Message: A one-line response may not exceed 128 characters.
	 */
    ONE_LINE_RESPONSE_MAY_NOT_EXCEED_128_CHARACTERS(330),
    
    /**
	 * ID: 331<br>
	 * Message: You have acquired $s1 SP.
	 */
    ACQUIRED_S1_SP(331),
    
    /**
	 * ID: 332<br>
	 * Message: Do you want to be restored?
	 */
    WANT_RESTORED(332),
    
    /**
	 * ID: 333<br>
	 * Message: You have received $s1 damage by Core's barrier.
	 */
    RECEIVED_S1_DAMAGE_FROM_CORE_BARRIER(333),
    
    /**
	 * ID: 334<br>
	 * Message: Please enter your private store display message.
	 */
    ENTER_PRIVATE_STORE_MSG(334),
    
    /**
	 * ID: 335<br>
	 * Message: $s1 has been aborted.
	 */
    S1_ABORTED(335),
    
    /**
	 * ID: 336<br>
	 * Message: You are attempting to crystalize $s1.  Do you wish to continue?
	 */
    ATTEMPTING_CRYSTALIZE_S1(336),
    
    /**
	 * ID: 337<br>
	 * Message: The soulshot you are attempting to use does not match the grade of your equipped weapon.
	 */
	SOULSHOTS_GRADE_MISMATCH(337),
	
	/**
	 * ID: 338<br>
	 * Message: You do not have enough soulshots for that.
	 */
	NOT_ENOUGH_SOULSHOTS(338),
	
	/**
     * ID: 339<br>
     * Message: Cannot use soulshots.
     */
	CANNOT_USE_SOULSHOTS(339),
	
	/**
	 * ID: 340<br>
	 * Message: Your private store is now open for business.
	 */
	PRIVATE_STORE_NOW_OPEN(340),
	
	/**
	 * ID: 341<br>
	 * Message: You do not have enough materials to perform that action.
	 */
	NO_ENOUGH_MATERIALS(341),
	
	/**
	 * ID: 342<br>
	 * Message: Power of the spirits enabled.
	 */
    ENABLED_SOULSHOT(342),
    
    /**
     * ID: 343<br>
     * Message: Sweeper failed, target not spoiled.
     */
    SWEEPER_FAILED_TARGET_NOT_SPOILED(343),
	
    /**
     * ID: 344<br>
     * Message: Power of the spirits disabled.	
     */
    POWER_SPIRITS_DISABLED(344),
    
    /**
     * ID: 345<br>
     * Message: Chat enabled.
     */
    CHAT_ENABLED(345),
    
    /**
     * ID: 346<br>
     * Message: Chat disabled.
     */
    CHAT_DESABLED(346),
    
    /**
     * ID: 347<br>
     * Message: Incorrect item count.
     */
    NOT_ENOUGH_ITEMS(347),
    
    /**
     * ID: 348<br>
     * Message: Incorrect item price
     */
    INCORRECT_PRICE(348),
    
    /**
     * ID: 349<br>
     * Message: Private store already closed
     */
    PRIVATE_STORE_ALREADY_CLOSED(349),
    
    /**
     * ID: 350<br>
     * Message: IItem out of stock.
     */
    ITEM_OUT_STOCK(350),
    
	 /**
     * ID: 351<br>
     * Message: Incorrect item count.
     */
    INCORRECT_ITEM_COUNT(351),
    
    /**
	 * ID: 352<br>
	 * Message: Incorrect item.
	 */
    INCORRECT_ITEM(352),
    
    /**
	 * ID: 353<br>
	 * Message: Cannot purchase.
	 */
    CANNOT_PURCHASE(353),
    
    /**
	 * ID: 354<br>
	 * Message: Cancel enchant.
	 */
    CANCEL_ENCHANT(354),
    
	/**
	 * ID: 355<br>
	 * Message: Inappropriate enchant conditions.
	 */
	INAPPROPRIATE_ENCHANT_CONDITION(355),
    
	/**
     * ID: 356<br>
     * Message: Reject resurrection.
     */
	REJECT_RES(356),
	
    /**
     * ID: 357<br>
     * Message: It has already been spoiled.
     */
    ALREDAY_SPOILED(357),
    
    /**
	 * ID: 358<br>
	 * Message: $s1 hour(s) until castle siege conclusion.
	 */
    S1_HOUR_UNTIL_SIEGE_CONCLUSION(358),
    
    /**
	 * ID: 359<br>
	 * Message: $s1 minute(s) until castle siege conclusion.
	 */
    S1_MINUTE_UNTIL_SIEGE_CONCLUSION(359),
    
    /**
	 * ID: 360<br>
	 * Message: Castle siege $s1 second(s) left!
	 */
    SIEGE_S1_SECONDS_LEFT(360),
    
    /**
	 * ID: 361<br>
	 * Message: Over-hit!
	 */
	OVER_HIT(361),

	/**
	 * ID: 362<br>
	 * Message: You have acquired $s1 bonus experience from a successful over-hit.
	 */
	ACQUIRED_BONUS_EXPERIENCE_THROUGH_OVER_HIT(362),
	
	/**
	 * ID: 363<br>
	 * Message: Chat available time: $s1 minute.
	 */
	CHAT_AVAILABLE_TIME_S1_MINUTE(363),
	
	/**
	 * ID: 364<br>
	 * Message: Enter user's name to search.
	 */
	ENTER_USER_NAME_TO_SEARCH(364),
	
	/**
	 * ID: 365<br>
	 * Message: Are you sure?
	 */
	ARE_YOU_SURE(365),
	
	/**
	 * ID: 366<br>
	 * Message: Please select your hair color.
	 */
	SELECT_HAIR_COLOR(366),
	
	/**
	 * ID: 367<br>
	 * Message:You cannot remove that clan character at this time.
	 */
	YOU_CANNOT_REMOVE_CHARACTER_FROM_CLAN_NOW(367),
	
	/**
	 * ID: 368<br>
	 * Message: Equipped +$s1 $s2.
	 */
	S1_S2_EQUIPPED(368),
	
	/**
	 * ID: 369<br>
	 * Message: You have obtained a +$s1 $s2.
	 */
	YOU_PICKED_UP_A_S1_S2(369),
	
	/**
     * ID: 370<br>
     * Message: Failed to pick up $s1.
     */
	FAILED_PICKUP_S1(370),
	
	/**
     * ID: 371<br>
     * Message: Acquired +$s1 $s2.
     */
    ACQUIRED_S1_S2(371),
	
    /**
	 * ID: 372<br>
	 * Message: Failed to earn $s1.
	 */
    FAILED_TO_EARN_S1(372),
    
    /**
	 * ID: 373<br>
	 * Message: You are trying to destroy +$s1 $s2.  Do you wish to continue?
	 */
    YOU_ARE_TRYING_DESTROY_S1_S2(373),
    
    /**
	 * ID: 374<br>
	 * Message: You are attempting to crystalize +$s1 $s2.  Do you wish to continue?
	 */
    YOU_ARE_ATTEMPTING_CRYSTALIZE_S1_S2(374),
    
    /**
	 * ID: 375<br>
	 * Message: You have dropped +$s1 $s2.
	 */
    YOU_DROPPED_S1_S2(375),
    
    /**
	 * ID: 376<br>
	 * Message: $s1 has obtained +$s2$s3.
	 */
    S1_OBTAINED_S2_S3(376),
    
    /**
	 * ID: 377<br>
	 * Message: $S1 $S2 disappeared.
	 */
    S1_S2_DISAPPEARED(377),
    
	/**
	 * ID: 378<br>
	 * Message: $s1 purchased $s2.
	 */
	S1_PURCHASED_S2(378),

	/**
	 * ID: 379<br>
	 * Message: $s1 purchased +$s2 $s3.
	 */
	S1_PURCHASED_S2_S3(379),

	/**
	 * ID: 380<br>
	 * Message: $s1 purchased $s3 $s2(s).
	 */
	S1_PURCHASED_S3_S2_S(380),
	
	/**
     * ID: 381<br>
     * Message: The game client encountered an error and was unable to connect to the petition server.
     */
    GAME_CLIENT_UNABLE_TO_CONNECT_TO_PETITION_SERVER(381),
    
    /**
     * ID: 382<br>
     * Message: Currently there are no users that have checked out a GM ID
     */
    NO_USER_CHECKED_OUT_GM_ID(382),
    
    /**
     * ID: 383<br>
     * Message: Request confirmed to end consultation at petition server.
     */
    REQUEST_END_PETITION_SERVER(383),
    
    /**
     * ID: 384<br>
     * Message: The client is not logged onto the game server.
     */
    CLIENT_NOT_LOGGED_GAME(384),
    
    /**
     * ID: 385<br>
     * Message: Request confirmed to begin consultation at petition server.
     */
    REQUEST_BEGIN_PETITION_SERVER(385),
    
    /**
     * ID: 386<br>
     * Message: The body of your petition must be more than five characters in length.
     */
    PETITION_MUST_BE_MORE_THAN_5_CHARACTERS(386),
    
    /**
     * ID: 387<br>
     * Message: This ends the GM petition consultation.
     * Please take a moment to provide feedback about this service.
     */
    THIS_END_THE_PETITION_PLEASE_PROVIDE_FEEDBACK(387),
    
    /**
     * ID: 388<br>
     * Message: Not under petition consultation.
     */
    NOT_UNDER_PETITION_CONSULTATION(388),

    /**
     * ID: 389<br>
     * Message: our petition application has been accepted. - Receipt No. is $s1.
     */
    PETITION_ACCEPTED_RECENT_NO_S1(389),

    /**
     * ID: 390<br>
     * Message: You may only submit one petition (active) at a time.
     */
    ONLY_ONE_ACTIVE_PETITION_AT_TIME(390),
    
    /**
     * ID: 391<br>
     * Message: Receipt No. $s1, petition cancelled.
     */
    RECENT_NO_S1_CANCELED(391),

    /**
     * ID: 392<br>
     * Message: Under petition advice.
     */
    UNDER_PETITION(392),
    
    /**
     * ID: 393<br>
     * Message: Failed to cancel petition. Please try again later.
     */
    FAILED_CANCEL_PETITION_TRY_LATER(393),
    
    /**
     * ID: 394<br>
     * Message: Petition consultation with $s1, under way.
     */
    PETITION_WITH_S1_UNDER_WAY(394),
    
    /**
     * ID: 395<br>
     * Message: Ending petition consultation with $s1.
     */
    PETITION_ENDED_WITH_S1(395),

    /**
     * ID: 396<br>
     * Message: Please login after changing your temporary password.
     */
    LOGIN_AFTER_CHANGE_YOUR_PASSWORD(396),
    
    /**
     * ID: 397<br>
     * Message: Not a paid account.
     */
    NOT_PAID_ACCOUNT(397),
    
    /**
     * ID: 398<br>
     * Message: There is no time left on this account.
     */
    TIME_NO_LEFT_THIS_ACCOUNT(398),
    
    /**
     * ID: 399<br>
     * Message: System error.
     */
    SYSTEM_ERROR(399),
    
    /**
     * ID: 400<br>
     * Message: You are attempting to drop $s1.  Do you wish to continue?
     */
    ATTEMPTING_DROP_S1(400),
    
    /**
     * ID: 401<br>
     * Message: You currently have too many quests in progress.
     */
    TOO_MANY_QUESTS(401),
    
    /**
     * ID: 402<br>
     * Message: You do not possess the correct ticket to board the boat.
     */
    DONT_POSSESS_CORRECT_TICKET_BOAT(402),
    
    /**
     * ID: 403<br>
     * Message: You have exceeded your out-of-pocket adena limit.
     */
    ADENA_LIMIT_EXCEEDED(403),
    /**
     * ID: 404<br>
     * Message: Your Create Item level is too low to register this recipe.
     */
    CREATE_LVL_TOO_LOW_TO_REGISTER(404),
    
    /**
     * ID: 405<br>
     * Message: The total price of the product is too high.
     */
    TOTAL_PRICE_TOO_HIGH(405),
    
    /**
     * ID: 406<br>
     * Message: Petition application accepted.
     */
    PETITION_APP_ACCEPTED(406),

    /**
     * ID: 407<br>
     * Message: Petition under process.
     */
    PETITION_UNDER_PROCESS(407),
	
    /**
	 * ID: 408<br>
	 * Message:$s1  Set Period
	 */
    SET_PERIOD(408),
    
    /**
	 * ID: 409<br>
	 * Message: Set Time-$s1: $s2: $s3
	 */
    SET_TIME_S1_S2_S3(409),
    
    /**
	 * ID: 410<br>
	 * Message:Registration Period
	 */
    REGISTRATION_PERIOD(410),
    
    /**
	 * ID: 411<br>
	 * Message: Registration TIme-$s1: $s2: $s3
	 */
    REGISTRATION_TIME_S1_S2_S3(411),
    
    /**
	 * ID: 412<br>
	 * Message:Battle begins in $s1: $s2: $s4
	 */
    BATTLE_BEGINS_S1_S2_S4(412),
    
    /**
	 * ID: 413<br>
	 * Message:Battle ends in $s1: $s2: $s5
	 */
    BATTLE_ENDS_S1_S2_S5(413),
    
    /**
	 * ID: 414<br>
	 * Message:Standby
	 */
    STANDBY(414),
    
    /**
	 * ID: 415<br>
	 * Message:Under Siege
	 */
    UNDER_SIEGE(415),
    
    /**
	 * ID: 416<br>
	 * Message:This item cannot be exchanged.
	 */
    THIS_ITEM_CANNOT_EXCHANGED(416),
    
    /**
	 * ID: 417<br>
	 * Message:$s1  has been disarmed
	 */
    S1_DISARMED(417),
    
    /**
	 * ID: 418<br>
	 * Message: There is a significant difference between 
	 * the item's price and its standard price. Please check again.
	 */
    THERE_ARE_SIGNICANT_DIFFERENCE_PRICE_ITEM(418),
    
    /**
	 * ID: 419<br>
	 * Message: $s1 minute(s) of usage time left.
	 */
    S1_MINUTES_USAGE_LEFT(419),
    
    /**
	 * ID: 420<br>
	 * Message: Time expired.
	 */
    TIME_EXPIRED(420),
    
    /**
	 * ID: 421<br>
	 * Message: Another person has logged in with the same account.
	 */
    LOGGED_SAME_ACCOUNT(421),
    
	/**
	 * ID: 422<br>
	 * Message: You have exceeded the weight limit.
	 */
	WEIGHT_LIMIT_EXCEEDED(422),
	
	/**
	 * ID: 423<br>
	 * Message: You have cancelled the enchanting process.
	 */
	ENCHANT_SCROLL_CANCELLED(423),
	
	/**
	 * ID: 424<br>
	 * Message: Does not fit strengthening conditions of the scroll.
	 */
	NOT_FIT_CONDITION_THE_SCROLL(424),
	
	/**
	 * ID: 425<br>
	 * Message: Your Create Item level is too low to register this recipe.
	 */
	CREATE_ITEM_LVL_TOO_LOW(425),
	
	/**
	 * ID: 426<br>
	 * Message: Your account has been reported for intentionally not 
	 * paying the cyber cafe fees.
	 */
	YOUR_ACCOUNT_BEEN_REPORTED_NOT_PAYING_CYBER_CAFE(426),
	
	/**
	 * ID: 427<br>
	 * Message: Please contact us.
	 */
	CONTACT_US(427),
	
	/**
	 * ID: 448<br>
	 * Message: System error, please log in again later.
	 */
	SYSTEM_ERROR2(448),
	
	/**
	 * ID: 449<br>
	 * Message: Password does not match this account.
	 */
	PASSWORD_NOT_MATCH(449),
	
	/**
	 * ID: 450<br>
	 * Message: Confirm your account information and log in again later.
	 */
	CONFIRM_ACCOUNT_INFORMATION(450),
	
	/**
	 * ID: 451<br>
	 * Message: The password you have entered is incorrect.
	 */
	PASSWORD_ENTERED_INCORRECT(451),
	
	/**
	 * ID: 452<br>
	 * Message: Please confirm your account information and try logging in again.
	 */
	PLEASE_CONFIRM_YOUR_ACCOUNT_INFORMATION(452),
	
	/**
	 * ID: 453<br>
	 * Message: Your account information is incorrect.
	 */
	ACCOUNT_INFORMATION_INCORRECT(453),
	
	/**
	 * ID: 455<br>
	 * Message: This account is already in use.  Access denied.
	 */
	ACCOUNT_ALREADY_USE(455),
	
	/**
	 * ID: 456<br>
	 * Message: Lineage II game services may be used by individuals 15 years of age or 
	 * older except for PvP servers, which may only be used by adults 18 years of age 
	 * and older. (Korea Only)
	 */
	LINEAGE_AGE_REQUERIMENT(456),
	
	/**
	 * ID: 457<br>
	 * Message: Server under maintenance. Please try again later.
	 */
	SERVER_UNDER_MAINTENANCE(457),
	
	/**
	 * ID: 458<br>
	 * Message: Your usage term has expired.	
	 */
	USAGE_TERM_EXPIRED(458),
	
	/**
	 * ID: 459<br>
	 * Message: Please visit the official Lineage II website at http://www.lineage2.com
	 */
	VISIT_WEBSITE(459),
	
	/**
	 * ID: 460<br>
	 * Message: to reactivate your account.
	 */
	REATIVATE_ACCOUNT(460),
	
	/**
	 * ID: 461<br>
	 * Message: Access failed.
	 */
	ACCESS_FAILED(461),
	
	/**
	 * ID: 462<br>
	 * Message: Please try again later.
	 */
	TRY_AGAIN(462),
	
	/**
     * ID: 464<br>
     * Message: This feature is only available alliance leaders.
     */
    FEATURE_ONLY_FOR_ALLIANCE_LEADER(464),

    /**
     * ID: 465<br>
     * Message: You are not currently allied with any clans.
     */
    NO_CURRENT_ALLIANCES(465),

    /**
     * ID: 466<br>
     * Message: You have exceeded the limit.
     */
    YOU_HAVE_EXCEEDED_THE_LIMIT(466),

    /**
     * ID: 467<br>
     * Message: You may not accept any clan within a day after expelling another clan.
     */
    CANT_INVITE_CLAN_WITHIN_1_DAY(467),

    /**
     * ID: 468<br>
     * Message: A clan that has withdrawn or been expelled cannot enter
     * into an alliance within one day of withdrawal or expulsion.
     */
    CANT_ENTER_ALLIANCE_WITHIN_1_DAY(468),

    /**
     * ID: 469<br>
     * Message: You may not ally with a clan you are currently at war with.
     * That would be diabolical and treacherous.
     */
    MAY_NOT_ALLY_CLAN_BATTLE(469),

    /**
     * ID: 470<br>
     * Message: Only the clan leader may apply for withdrawal from the alliance.
     */
    ONLY_CLAN_LEADER_WITHDRAW_ALLY(470),

    /**
     * ID: 471<br>
     * Message: Alliance leaders cannot withdraw.
     */
    ALLIANCE_LEADER_CANT_WITHDRAW(471),

    /**
     * ID: 472<br>
     * Message: You cannot expel yourself from the clan
     */
    CANNOT_EXPEL_YOURSELF_FROM_CLAN(472),
    /**
     * ID: 473<br>
     * Message: Different alliance.
     */
    DIFFERENT_ALLIANCE(473),

    /**
     * ID: 474<br>
     * Message: That clan does not exist.
     */
    CLAN_DOESNT_EXISTS(474),
	
    /**
	 * ID: 475<br>
	 * Message: Different alliance.
	 */
    
    /**
	 * ID: 476<br>
	 * Message: Please adjust the image size to 8x12.
	 */
    ADJUST_IMAGE_8X12(476),
    
	/**
	 * ID: 477<br>
	 * Message: No response. Invitation to join an alliance has been cancelled.
	 */
	NO_RESPONSE_TO_ALLY_INVITATION(477),

	/**
	 * ID: 478<br>
	 * Message: No response. Your entrance to the alliance has been cancelled.
	 */
	YOU_DID_NOT_RESPOND_TO_ALLY_INVITATION(478),
	
	/**
     * ID: 479<br>
     * Message: $s1 has joined as a friend.
     */
    S1_JOINED_AS_FRIEND(479),
    
    /**
     * ID: 480<br>
     * Message: Please check your friends list.	
     */
    CHECK_YOUR_FRIEND_LIST(480),
    /**
     * ID: 481<br>
     * Message: $s1 has been deleted from your friends list.
     */
    S1_HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST(481),

    /**
     * ID: 482<br>
     * Message: You cannot add yourself to your own friend list.
     */
    YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIENDS_LIST(482),

    /**
     * ID: 483<br>
     * Message: This function is inaccessible right now.  Please try again later.
     */
    FUNCTION_INACCESSIBLE(483),
    
    /**
     * ID: 484<br>
     * Message: This player is already registered in your friends list.
     */
    S1_ALREADY_IN_FRIENDS_LIST(484),

    /**
     * ID: 485<br>
     * Message: No new friend invitations may be accepted.
     */
    NO_NEW_INVITATIONS_ACCEPTED(485),

    /**
     * ID: 486<br>
     * Message: The following user is not in your friends list.
     */
    THE_USER_NOT_IN_FRIENDS_LIST(486),
    
    /**
     * ID: 487<br>
     * Message: ======<Friends List>======
     */
    FRIEND_LIST_HEAD(487),

    /**
     * ID: 488<br>
     * Message: $s1 (Currently: Online)
     */
    S1_ONLINE(488),

    /**
     * ID: 489<br>
     * Message: $s1 (Currently: Offline)
     */
    S1_OFFLINE(489),

    /**
     * ID: 490<br>
     * Message: ========================
     */
    FRIEND_LIST_FOOT(490),

	 /**
     * ID: 491<br>
     * Message: =======<Alliance Information>=======
     */
    ALLIANCE_INFO_HEAD(491),

    /**
     * ID: 492<br>
     * Message: Alliance Name: $s1
     */
    ALLIANCE_NAME_S1(492),

    /**
     * ID: 493<br>
     * Message: Connection: $s1 / Total $s2
     */
    CONNECTION_S1_TOTAL_S2(493),

    /**
     * ID: 494<br>
     * Message: Alliance Leader: $s2 of $s1
     */
    ALLIANCE_LEADER_S2_OF_S1(494),

    /**
     * ID: 495<br>
     * Message: Affiliated clans: Total $s1 clan(s)
     */
    ALLIANCE_CLAN_TOTAL_S1(495),
	
	/**
     * ID: 496<br>
     * Message: =====<Clan Information>=====
     */
    CLAN_INFO_HEAD(496),

    /**
     * ID: 497<br>
     * Message: Clan Name: $s1
     */
    CLAN_INFO_NAME(497),

    /**
     * ID: 498<br>
     * Message: Clan Leader: $s1
     */
    CLAN_INFO_LEADER(498),

    /**
     * ID: 499<br>
     * Message: Clan Level: $s1
     */
    CLAN_INFO_LEVEL(499),

    /**
     * ID: 500<br>
     * Message: ------------------------
     */
    CLAN_INFO_SEPARATOR(500),

    /**
     * ID: 501<br>
     * Message: ========================
     */
    CLAN_INFO_FOOT(501),
    
    /**
     * ID: 502<br>
     * Message: You already belong to another alliance.
     */
    ALREADY_JOINED_ALLIANCE(502),
    
    /**
     * ID: 503<br>
     * Message: $s1 (Friend) has logged in.
     */
    FRIEND_S1_HAS_LOGGED_IN(503),

    /**
     * ID: 504<br>
     * Message: Only clan leaders may create alliances.
     */
    ONLY_CLAN_LEADER_CREATE_ALLIANCE(504),

    /**
     * ID: 505<br>
     * Message: You cannot create a new alliance within 10 days after dissolution.
     */
    CANT_CREATE_ALLIANCE_10_DAYS_DISOLUTION(505),

    /**
     * ID: 506<br>
     * Message: Incorrect alliance name. Please try again.
     */
    INCORRECT_ALLIANCE_NAME(506),

    /**
     * ID: 507<br>
     * Message: Incorrect length for an alliance name.
     */
    INCORRECT_ALLIANCE_NAME_LENGTH(507),

    /**
     * ID: 508<br>
     * Message: This alliance name already exists.
     */
    ALLIANCE_ALREADY_EXISTS(508),

    /**
     * ID: 509<br>
     * Message: Cannot accept. clan ally is registered as an enemy during siege battle.
     */
    CANT_ACCEPT_ALLY_ENEMY_FOR_SIEGE(509),

    /**
     * ID: 510<br>
     * Message: You have invited someone to your alliance.
     */
    YOU_INVITED_FOR_ALLIANCE(510),

    /**
     * ID: 511<br>
     * Message: You must first select a user to invite.
     */
    SELECT_USER_TO_INVITE(511),

    /**
     * ID: 512<br>
     * Message: Do you really wish to withdraw from the alliance?
     */
    DO_YOU_WISH_TO_WITHDRW(512),

    /**
     * ID: 513<br>
     * Message: Enter the name of the clan you wish to expel.
     */
	ENTER_NAME_CLAN_TO_EXPEL(513),

	/**
	 * ID: 514<br>
	 * Message: Do you really wish to dissolve the alliance?
	 */
	DO_YOU_WISH_TO_DISOLVE(514),
	
	/**
     * ID: 515<br>
     * Messages: Enter a file name for the alliance crest.
     */
	ENTER_FILE_NAME_CREST(515),

	/**
     * ID: 516<br>
     * Message: $s1 has invited you to be their friend.
     */
    SI_INVITED_YOU_AS_FRIEND(516),

	/**
	 * ID: 517<br>
	 * Message: You have accepted the alliance.
	 */
    YOU_ACCEPTED_ALLIANCE(517),

    /**
     * ID: 518<br>
     * Message: You have failed to invite a clan into the alliance.
     */
    FAILED_TO_INVITE_CLAN_IN_ALLIANCE(518),

    /**
     * ID: 519<br>
     * Message: You have withdrawn from the alliance.
     */
    YOU_HAVE_WITHDRAWN_FROM_ALLIANCE(519),

    /**
     * ID: 520<br>
     * Message: You have failed to withdraw from the alliance.
     */
    YOU_HAVE_FAILED_TO_WITHDRAWN_FROM_ALLIANCE(520),

    /**
     * ID: 521<br>
     * Message: You have succeeded in expelling a clan.
     */
    YOU_HAVE_EXPELED_A_CLAN(521),

    /**
     * ID: 522<br>
     * Message: You have failed to expel a clan.
     */
    FAILED_TO_EXPELED_A_CLAN(522),

    /**
     * ID: 523<br>
     * Message: The alliance has been dissolved.
     */
    ALLIANCE_DISOLVED(523),

    /**
     * ID: 524<br>
     * Message: You have failed to dissolve the alliance.
     */
    FAILED_TO_DISOLVE_ALLIANCE(524),
    
    /**
     * ID: 525<br>
     * Message: You have succeeded in inviting a friend to your friends list.
     */
    YOU_HAVE_SUCCEEDED_INVITING_FRIEND(525),

    /**
     * ID: 526<br>
     * Message: You have failed to add a friend to your friends list.
     */
    FAILED_TO_INVITE_A_FRIEND(526),

    /**
     * ID: 527<br>
     * Message: $s1 leader, $s2, has requested an alliance.
     */
    S2_ALLIANCE_LEADER_OF_S1_REQUESTED_ALLIANCE(527),
    
	/**
	 * ID: 528<br>
	 * Message: Unable to find file at target location.
	 */
    FILE_NOT_FOUND(528),
    
    /**
	 * ID: 529<br>
	 * Message: You may only register an 8 x 12 pixel, 256-color BMP.
	 */
    YOU_MAY_ONLY_REGISTER_8X12_BMP(529),
    
    /**
	 * ID: 530<br>
	 * Message: The Spiritshot does not match the weapon's grade.
	 */
	SPIRITSHOTS_GRADE_MISMATCH(530),

	/**
	 * ID: 531<br>
	 * Message: You do not have enough Spiritshots for that.
	 */
	NOT_ENOUGH_SPIRITSHOTS(531),

	/**
	 * ID: 532<br>
	 * Message: You may not use Spiritshots.
	 */
	CANNOT_USE_SPIRITSHOTS(532),

	/**
	 * ID: 533<br>
	 * Message: Power of Mana enabled.
	 */
    ENABLED_SPIRITSHOT(533),
    
    /**
	 * ID: 534<br>
	 * Message: Power of Mana disabled.
	 */
    DISABLED_SPIRITSHOT(534),
    /**
	 * ID: 535<br>
	 * Message: Enter a name for your pet.
	 */
	NAMING_NAME_PET(535),
	
	/**
     * ID: 536<br>
     * Message: How much adena do you wish to transfer to your Inventory?
     */
	HOW_MUCH_ADENA_WISH_TRANSFER(356),
	
	/**
     * ID: 537<br>
     * Message: How much will you transfer?
     */
	HOW_MUCH_TRANSFER(537),
	
	/**
     * ID: 538<br>
     * Message: Your SP has decreased by $s1.
     */
    SP_DECREASED_S1(538),

    /**
     * ID: 539<br>
     * Message: Your Experience has decreased by $s1.
     */
    EXP_DECREASED_BY_S1(539),

    /**
	 * ID: 540<br>
	 * Message: Clan leaders may not be deleted. Dissolve the clan first and try again.
	 */
	CLAN_LEADERS_MAY_NOT_BE_DELETED(540),

	/**
	 * ID: 541<br>
	 * Message: You may not delete a clan member. Withdraw from the clan first and try again.
	 */
	CLAN_MEMBER_MAY_NOT_BE_DELETED(541),
	
	/**
     * ID: 542<br>
     * Message: The NPC server is currently down.  Pets and servitors cannot be summoned at this time.
     */
	NPC_SERVER_IS_DOWN(542),
	
	/**
     * ID: 543<br>
     * Message: You already have a pet.
     */
    YOU_ALREADY_HAVE_A_PET(543),
	
	/**
	 * ID: 544<br>
	 * Message: Your pet cannot carry this item.
	 */
	ITEM_NOT_FOR_PETS(544),
	
	/**
	 * ID: 545<br>
	 * Message: Your pet cannot carry any more items. Remove some, then try again.
	 */
	PET_CANNOT_CARRY_MORE_ITEMS(545),
	
	/**
	 * ID: 546<br>
	 * Message: Unable to place item, your pet is too encumbered.
	 */
	UNABLE_TO_PLACE_ITEM_ON_PET(546),
	
	/**
	 * ID: 547<br>
	 * Message: Summoning your pet.
	 */
    SUMMON_A_PET(547),

	/**
	 * ID: 548<br>
	 * Message: Your pet's name can be up to 8 characters in length.
	 */
	NAMING_PETNAME_UP_TO_8CHARS(548),
	
	 /**
     * ID: 549<br>
     * Message: To create an alliance, your clan must be Level 5 or higher.
     */
    TO_CREATE_AN_ALLY_YOU_CLAN_MUST_BE_LEVEL_5_OR_HIGHER(549),

    /**
     * ID: 550<br>
     * Message: You may not create an alliance during the term of dissolution postponement.
     */
    YOU_MAY_NOT_CREATE_ALLY_WHILE_DISSOLVING(550),
	
	/**
	 * ID: 551<br>
	 * Message: You cannot raise your clan level during the term of dispersion postponement.
	 */
	CANNOT_RISE_LEVEL_WHILE_DISSOLUTION_IN_PROGRESS(551),
    
    /**
	 * ID: 552<br>
	 * Message: During the grace period for dissolving a clan, the registration or
	 * deletion of a clan's crest is not allowed.
	 */
	CANNOT_SET_CREST_WHILE_DISSOLUTION_IN_PROGRESS(552),
	
	/**
	 * ID: 553<br>
	 * Message: The opposing clan has applied for dispersion.
	 */
	OPPOSING_CLAN_APPLIED_DISPERSION(553),
	
	/**
	 * ID: 554<br>
	 * Message: You cannot disperse the clans in your alliance.
	 */
	CANNOT_DISPERSE_THE_CLANS_IN_ALLY(554),
    
	/**
	 * ID: 555<br>
	 * Message: You cannot move - you are too encumbered.
	 */
	YOU_CANNOT_MOVE_ENCUMBERED(555),
	
	/**
	 * ID: 556<br>
	 * Message: You cannot move in this state.
	 */
	YOU_CANNOT_MOVE_THIS_STATE(556),
	
	/**
	 * ID: 557<br>
	 * Message: Your pet has been summoned and may not be destroyed.
	 */
	PET_SUMMONED_MAY_NOT_BE_DESTROYED(557),
	
	/**
	 * ID: 558<br>
	 * Message: Your pet has been summoned and cannot be let go.
	 */
	PET_SUMMONED_AND_CANNOT_LET_GO(558),
    /**
	 * ID: 559<br>
	 * Message: You have purchased $s2 from $s1.
	 */
	PURCHASED_S2_FROM_S1(559),

	/**
	 * ID: 560<br>
	 * Message: You have purchased +$s2 $s3 from $s1.
	 */
	PURCHASED_S2_S3_FROM_S1(560),

	/**
	 * ID: 561<br>
	 * Message: You have purchased $s3 $s2(s) from $s1.
	 */
	PURCHASED_S3_S2_S_FROM_S1(561),
	
	/**
	 * ID: 562<br>
	 * Message: You may not crystallize this item. Your crystallization skill level is too low.
	 */
	CRYSTALLIZE_LEVEL_TOO_LOW(562),
	
	/**
     * ID: 563<br>
     * Message: Failed to disable attack target.
     */
	FAILED_DISABLE_ATTACK_TARGET(563),
	
	/**
     * ID: 564<br>
     * Message: Failed to change attack target.
     */
	FAILED_CHANGE_ATTACK_TARGET(564),
	
	/**
     * ID: 565<br>
     * Message: Not enough luck.
     */
	NOT_ENOUGH_LUCK(565),
	
	/**
     * ID: 566<br>
     * Message: Your confusion spell failed.
     */
	YOUR_CONFUSION_SPELL_FAILED(566),
	
	/**
     * ID: 567<br>
     * Message: Your fear spell failed.
     */
	YOUR_FEAR_SPELL_FAILED(567),
	
    /**
     * ID: 568<br>
     * Message: Cubic Summoning failed.
     */
    CUBIC_SUMMONING_FAILED(568),
    
    /**
	 * ID: 569<br>
	 * Message: Caution -- this item's price greatly differs from non-player run shops. 
	 * Do you wish to continue?
	 */
    
    /**
	 * ID: 570<br>
	 * Message: How many  $s1(s) do you want to purchase?
	 */
    HOW_MANY_S1_PURCHASE(570),
    
    /**
	 * ID: 571<br>
	 * Message: How many  $s1(s) do you want to purchase?
	 */
    
    /**
	 * ID: 572<br>
	 * Message: Do you wish to join $s1's party? (Item distribution: Finders Keepers)
	 */
	S1_INVITED_YOU_TO_PARTY_FINDER_KEEPER(572),

	/**
	 * ID: 573<br>
	 * Message: Do you wish to join $s1's party? (Item distribution: Random)
	 */
	S1_INVITED_YOU_TO_PARTY_RANDOM(573),
	
	/**
     * ID: 574<br>
     * Message: Pets and Servitors are not available at this time.
     */
    PETS_ARE_NOT_AVAILABLE_AT_THIS_TIME(574),
	
    /**
     * ID: 575<br>
     * Message: How much adena do you wish to transfer to your pet?
     */
    HOW_MUCH_ADENA_TRANSFER_TO_PET(575),
    
    /**
     * ID: 576<br>
     * Message: How much do you wish to transfer?
     */
    HOW_MUCH_TRANSFER2(576),
    
    /**
     * ID: 577<br>
     * Message: You cannot summon during a trade or while using the private shops.
     */
    CANNOT_SUMMON_WHILE_TRADING(577),
    
	/**
     * ID: 578<br>
     * Message: You cannot summon during combat.
     */
    YOU_CANNOT_SUMMON_IN_COMBAT(578),

	/**
     * ID: 579<br>
     * Message: A pet cannot be sent back during battle.
     */
    PET_CANNOT_SENT_BACK_DURING_BATTLE(579),
	
    /**
     * ID: 580<br>
     * Message: You may not use multiple pets or servitors at the same time.
     */
    YOU_MAY_NOT_USE_MULTIPLE_SUMMONS(580),
    
	/**
     * ID: 581<br>
     * Message: There is a space in the name.
     */
	NAMING_THERE_IS_A_SPACE(581),

	/**
	 * ID: 582<br>
	 * Message: Inappropriate character name.
	 */
	NAMING_INAPPROPRIATE_CHARACTER_NAME(582),

	/**
	 * ID: 583<br>
	 * Message: Name includes forbidden words.
	 */
	NAMING_INCLUDES_FORBIDDEN_WORDS(583),

	/**
	 * ID: 584<br>
	 * Message: This is already in use by another pet.
	 */
	NAMING_ALREADY_IN_USE_BY_ANOTHER_PET(584),
	
	/**
	 * ID: 585<br>
	 * Message: Please decide on the price.
	 */
	DECIDE_THE_PRICE(585),
	
	/**
	 * ID: 586<br>
	 * Message: Pet items cannot be registered as shortcuts
	 */
	PET_ITEMS_CANNOT_REGISTERED_SHORTCUTS(586),
	
	/**
	 * ID: 587<br>
	 * Message: Irregular system speed.
	 */
	IRREGULAR_SYSTEM_SPEED(587),
	
	/**
	 * ID: 588<br>
	 * Message: Your pet's inventory is full.
	 */
	PET_INVENTORY_FULL(588),
	
	/**
	 * ID: 589<br>
	 * Message: A dead pet cannot be sent back.
	 */
    DEAD_PET_CANNOT_BE_RETURNED(589),

    /**
     * ID: 590<br>
     * Message: Your pet is motionless and any attempt you make to give
     * it something goes unrecognized.
     */
	CANNOT_GIVE_ITEMS_TO_DEAD_PET(590),
	
	/**
	 * ID: 591<br>
	 * Message: An invalid character is included in the pet's name.
	 */
	NAMING_PETNAME_CONTAINS_INVALID_CHARS(591),
	
	/**
     * ID: 592<br>
     * Message: Do you wish to dismiss your pet? 
     * Dismissing your pet will cause the pet necklace to disappear.
     */
	WISH_DISMISS_YOUR_PET(592),
	
	/**
     * ID: 593<br>
     * Message: Starving, grumpy and fed up, your pet has left.
     */
	FED_UP_YOUR_PET_HAS_LEFT(593),
	
	/**
     * ID: 594<br>
     * Message: You may not restore a hungry pet.
     */
    YOU_CANNOT_RESTORE_HUNGRY_PETS(594),
	
    /**
	 * ID: 595<br>
	 * Message: Your pet is very hungry.
	 */
    PET_VERY_HUNGRY(595),
    
    /**
	 * ID: 596<br>
	 * Message: Your pet ate a little, but is still hungry.
	 */
    PET_STILL_HUNGRY(596),
    
    /**
	 * ID: 597<br>
	 * Message: Your pet is very hungry. Please be careful.
	 */
    CAREFUL_PET_VERY_HUNGRY(597),
    
    /**
	 * ID: 598<br>
	 * Message: You may not chat while you are invisible
	 */
    MAY_NOT_CHAT_WHILE_INVISIBLE(598),
    
    /**
	 * ID: 599<br>
	 * Message: The GM has an imprtant notice. Chat has been temporarily disabled.
	 */
    CHAT_DISABLED_DUE_NOTICE(599),
    
	/**
	 * ID: 600<br>
	 * Message: You may not equip a pet item.
	 */
	CANNOT_EQUIP_PET_ITEM(600),
	
	/**
     * ID: 601<br>
     * Message: There are $S1 petitions currently on the waiting list.
     */
    S1_PETITION_ON_WAITING_LIST(601),

    /**
     * ID: 602<br>
     * Message: The petition system is currently unavailable. Please try again later.
     */
    PETITION_SYSTEM_CURRENT_UNAVAILABLE(602),
	
	/**
     * ID: 603<br>
     * Message: That item cannot be discarded or exchanged.
     */
    CANNOT_DISCARD_EXCHANGE_ITEM(603),
	
    /**
	 * ID: 604<br>
	 * Message: You may not call forth a pet or summoned creature from this location.
	 */
    CANNOT_SUMMON_FROM_THIS_LOCATION(604),
    
    /**
	 * ID: 605<br>
	 * Message: You may register up to 64 people on your list.
	 */
    YOU_MAY_REGISTER_UP_64_PEOPLE_ON_LIST(605),
    
    /**
	 * ID: 606<br>
	 * Message: You cannot be registered because the other person has already registered 64 people on his/her list.
	 */
    YOU_CANNOT_BE_REGISTERED_OTHER_ALREADY_REGISTERED_64(606),
    
    /**
	 * ID: 607<br>
	 * Message: You do not have any further skills to learn. Come back when you have reached Level $s1.
	 */
    DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN(607),
    
    /**
     * ID: 608<br>
     * Message: $s1 has obtained $s3 $s2 by using Sweeper.
     */
    S1_SWEEPED_UP_S2_S3(608),

    /**
     * ID: 609<br>
     * Message: $s1 has obtained $s2 by using Sweeper.
     */
    S1_SWEEPED_UP_S2(609),
    
    /**
	 * ID: 610<br>
	 * Message: Your skill has been canceled due to lack of HP.
	 */
	SKILL_REMOVED_DUE_LACK_HP(610),
	
	/**
     * ID: 611<br>
     * Message: You have succeeded in Confusing the enemy.
     */
	SUCESS_CONFUSE_ENEMY(611),
	
	/**
     * ID: 612<br>
     * Message: The Spoil condition has been activated.
     */
	SPOIL_SUCCESS(612),
	
	/**
     * ID: 613<br>
     * Message: ======<Ignore List>======
     */
	IGNORE_LIST_HEAD(613),
	
	/**
     * ID: 614<br>
     * Message: $s1 $s2<br>
     * ( USE THIS ID TO SEND CUSTOM MESSAGES TO CLIENT )
     */
    S1_S2(614),

	/**
     * ID: 615<br>
     * Message: You have failed to register the user to your Ignore List.
     */
    FAILED_TO_REGISTER_TO_IGNORE_LIST(615),

    /**
     * ID: 616<br>
     * Message: You have failed to delete the character.
     */
    FAILED_DELETE_CHAR2(616),

    /**
     * ID: 617<br>
     * Message: $s1 has been added to your Ignore List.
     */
    S1_WAS_ADDED_TO_YOUR_IGNORE_LIST(617),

    /**
     * ID: 618<br>
     * Message: $s1 has been removed from your Ignore List.
     */
    S1_WAS_REMOVED_FROM_YOUR_IGNORE_LIST(618),

    /**
     * ID: 619<br>
     * Message: $s1 has placed you on his/her Ignore List.
     */
    S1_HAS_ADDED_YOU_TO_IGNORE_LIST(619),
	
    /**
     * ID: 620<br>
     * Message: $s1  has placed you on his/her Ignore List.
     */
    S1_ADD_YOU_TO_IGNORE_LIST(620),
    
    /**
     * ID: 621<br>
     * Message: This server is reserved for players in Korea.  
     * To play Lineage II, please connect to the server in your region.
     */
    
    /**
     * ID: 622<br>
     * Message: You may not make a declaration of war during an alliance battle.
     */
    CANNOT_DECLARE_WAR_DURING_ALLIANCE_BATTLE(622),
    
    /**
     * ID: 623<br>
     * Message: Your opponent has exceeded the number of simultaneous 
     * alliance battles allowed.
     */
    OPPONENT_EXCEEDED_ALLIANCE_BATTLE_ALLOWED(623),
    
    /**
     * ID: 624<br>
     * Message: $s1 Clan leader is not currently connected to the game server.
     */
    S1_CLAN_LEADER_NOT_LOGGED(624),
    
    /**
     * ID: 625<br>
     * Message: Your request for Alliance Battle truce has been denied.
     */
    REQUEST_FOR_ALLIANCE_BATTLE_DANIED(625),
    
	/**
     * ID: 626<br>
     * Message: The $s1 clan did not respond: war proclamation has been refused.
     */
    WAR_PROCLAMATION_HAS_BEEN_REFUSED(626),
	
    /**
     * ID: 627<br>
     * Message: Clan battle has been refused because you did not respond to 
     * $s1 clan's war proclamation.
     */
    CLAN_BATTLE_REFUSED_DUE_YOU_NOT_RESPOND_S1_CLAN(627),
    
	/**
     * ID: 628<br>
     * Message: You have already been at war with the $s1 clan: 5 days must 
     * pass before you can declare war again.
     */
    ALREADY_AT_WAR_WITH_S1_WAIT_5_DAYS(628),
    
    /**
 	 * ID: 629<br>
 	 * Message: Your opponent has exceeded the number of simultaneous 
 	 * alliance battles allowed.
 	 */
    OPPONENT_EXCEEDED_ALLIANCE_BATTLE_ALLOWED2(629),
    
    /**
 	 * ID: 630<br>
 	 * Message: War with the $s1 clan has begun.
 	 */
    WAR_WITH_S1_CLAN_BEGUN(630),
    
    /**
 	 * ID: 631<br>
 	 * Message: War with the $s1 clan is over.
 	 */
    WAR_WITH_S1_CLAN_OVER(631),
    
    /**
 	 * ID: 632<br>
 	 * Message: You have won the war over the $s1 clan!
 	 */
    WON_WAR_OVER_S1_CLAN(632),
    
    /**
 	 * ID: 633<br>
 	 * Message: You have surrendered to the $s1 clan.
 	 */
    SURRENDERED_THE_S1_CLAN(633),
    
    /**
 	 * ID: 634<br>
 	 * Message: Your alliance leader has been slain. 
 	 * You have been defeated by the $s1 clan.
 	 */
    DEFEATED_BY_S1_CLAN(634),
    
    /**
 	 * ID: 635<br>
 	 * Message: The time limit for the clan war has been exceeded. 
 	 * War with the $s1 clan is over
 	 */
    TIME_LIMIT_WAR_S1_CLAN_OVER(635),
    
    /**
 	 * ID: 636<br>
 	 * Message: You are not involved in a clan war.
 	 */
    NOT_INVOLVED_IN_CLAN_WAR(436),
    
    /**
 	 * ID: 637<br>
 	 * Message: A clan ally has registered itself to the opponent.
 	 */
    A_CLAN_ALLY_HAS_REGISTERED_ITSELF(637),
    
    /**
 	 * ID: 638<br>
 	 * Message: You have already requested a Siege Battle.
 	 */
    YOU_ALREADY_REQUEST_SIEGE(638),
    
    /**
 	 * ID: 639<br>
 	 * Message: Your application has been denied because you have 
 	 * already submitted a request for another Siege Battle.
 	 */
    ALREADY_ANOTHER_SIEGE(639),
    
    /**
 	 * ID: 640<br>
 	 * Message: You have failed to refuse castle defense aid.
 	 */
    FAILED_REFUSE_CASTLE_DEFENSE(640),
    
    /**
 	 * ID: 642<br>
 	 * Message: You have failed to approve castle defense aid.
 	 */
    FAILED_APPROVE_CASTLE_DEFENSE(641),
    
    /**
 	 * ID: 642<br>
 	 * Message: You are already registered to the attacker
 	 * side and must cancel your registration before submitting your request.
 	 */
 	ALREADY_REGISTERED_ATTACKER_SIEGE(642),
    
 	/**
     * ID: 643<br>
     * Message: You have already registered to the defender side and must cancel your 
     * registration before submitting your request.	
     */
 	ALREADY_REGISTERED_DEFENDER_SIEGE(643),
 	
 	/**
     * ID: 644<br>
     * Message: You are not yet registered for the castle siege.
     */
 	YOU_NOT_REGISTERED_YET_SIEGE(644),
 	
 	/**
     * ID: 645<br>
     * Message: Only clans of level 4 or higher may register for a castle siege.	
     */
 	ONLY_CLAN_LVL_4_CAN_REGISTER_ON_SIEGE(645),
 	
 	/**
     * ID: 646<br>
     * Message: You do not have the authority to modify the castle defender list.
     */
 	YOU_AUTHORIZED_MODIFY_SIEGE_DEFENDER_LIST(646),
 	
 	/**
     * ID: 647<br>
     * Message: You do not have the authority to modify the siege time.
     */
 	YOU_NOR_AUTHORIZED_MODIFY_SIEGE_TIME(647),
 	
 	/**
     * ID: 648<br>
     * Message: No more registrations may be accepted for the attacker side.
     */
 	NO_MORE_REGISTRATIONS_ACCEPTED_FOR_ATTACKER_SIDE(648),
 	
 	/**
     * ID: 649<br>
     * Message: No more registrations may be accepted for the defender side
     */
 	NO_MORE_REGISTRATIONS_ACCEPTED_FOR_DEFENDER_SIDE(649),
 	
    /**
     * ID: 650<br>
     * Message: You may not summon from your current location.
     */
    YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION(650),
	
    /**
	 * ID: 651<br>
	 * Message: Place $s1 in the current location and direction. Do you wish to continue?
	 */
    PLACE_S1_CURRENT_LOCATION(651),
    
    /**
	 * ID: 652<br>
	 * Message: The target of the summoned monster is wrong.
	 */
    TARGET_OF_SUMMON_INCORRECT(652),
    
    /**
	 * ID: 653<br>
	 * Message: You do not have the authority to position mercenaries.
	 */
    NOT_AUTHORIZED_TO_POSITION_MERCENARIES(653),
    
    /**
	 * ID: 654<br>
	 * Message: You do not have the authority to cancel mercenary positioning.
	 */
    NOT_AUTHORIZED_TO_CANCEL_MERCENARIES_POSITIONING(654),
    
    /**
	 * ID: 655<br>
	 * Message: Mercenaries cannot be positioned here.
	 */
    MERCENARIES_CANNOT_POSITIONED_HERE(655),
    
    /**
	 * ID: 656<br>
	 * Message: This mercenary cannot be positioned anymore.
	 */
    THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE(656),
    
    /**
	 * ID: 657<br>
	 * Message: Positioning cannot be done here because the distance 
	 * between mercenaries is too short.
	 */
    CANNOT_POSITIONING_MERCENARIES_DISTANCE_SHORT(657),
    
    /**
	 * ID: 658<br>
	 * Message: This is not a mercenary of a castle that you own and so you cannot 
	 * cancel its positioning.
	 */
    MERCENARY_ANOTHER_CASTLE(658),
    
    /**
	 * ID: 659<br>
	 * Message: This is not the time for siege registration and so registrations 
	 * cannot be accepted or rejected.
	 */
    NO_TIME_FOR_SIEGE_REGISTRATION(659),
    
    /**
	 * ID: 660<br>
	 * Message: This is not the time for siege registration and so registration and 
	 * cancellation cannot be done.
	 */
    NO_TIME_FOR_SIEGE_REGISTRATION_CANNOT_CANCEL(660),
    
	/**
	 * ID: 661<br>
	 * Message: This character cannot be spoiled.
	 */
	SPOIL_CANNOT_USE(661),
	
    /**
     * ID: 662<br>
     * Message: The other player is rejecting friend invitations.
     */
    THE_PLAYER_IS_REJECTING_FRIEND_INVITATIONS(662),
    
    /**
	 * ID: 663<br>
	 * Message: The siege time has been declared for $s. It is not possible to 
	 * change the time after a siege time has been declared. Do you want to continue?
	 */
    SIEGE_TIME_DELACRED(663),
    
    /**
	 * ID: 664<br>
	 * Message: Please choose a person to receive.
	 */
    CHOOSE_PERSON_TO_RECEIVE(664),
    
    /**
	 * ID: 665<br>
	 * Message: $s2 of $s1 alliance is applying for alliance war. 
	 * Do you want to accept the challenge?
	 */
    WANT_ACCEPT_ALLIANCE_WAR_OF_S2_S1_ALLY(665),
    
    /**
	 * ID: 666<br>
	 * Message: A request for ceasefire has been received from $s1 alliance. Do you agree?
	 */
    RECEIVE_REQUEST_CEASEFIRE_FROM_S1_ALLY(666),
    
    /**
	 * ID: 667<br>
	 * Message: You are registering on the attacking side of the $s1 siege. 
	 * Do you want to continue?
	 */
    REGISTERING_ON_ATTACKING_OF_S1_SIEGE(667),
    
    /**
	 * ID: 668<br>
	 * Message: You are registering on the defending side of the $s1 siege. 
	 * Do you want to continue?
	 */
    REGISTERING_ON_DEFENDING_OF_S1_SIEGE(668),
    
    /**
	 * ID: 669<br>
	 * Message: You are canceling your application to participate in the $s1 siege battle.
	 *  Do you want to continue?
	 */
    CANCEL_PARTICIPATE_OF_S1_SIEGE(669),
    
    /**
	 * ID: 670<br>
	 * Message: You are refusing the registration of $s1 clan on the defending side. 
	 * Do you want to continue?
	 */
    REFUSING_REGISTRATION_OF_S1_CLAN_ON_DEFENDING(670),
    
    /**
	 * ID: 671<br>
	 * Message: You are agreeing to the registration of $s1 clan on the defending side. 
	 * Do you want to continue?
	 */
    AGREE_REGISTRATION_OF_S1_CLAN_ON_DEFENDING(671),
    
    /**
	 * ID: 672<br>
	 * Message: $s1 adena disappeared.
	 */
	DISSAPEARED_ADENA(672),
	
	/**
     * ID: 673<br>
     * Message: Only a clan leader whose clan is of level 2 or higher is allowed to 
     * participate in a clan hall auction.
     */
	ONLY_LEADER_CLAN_LVL_2_CAN_PARTICIPATE_AUCTION(673),
	
	/**
     * ID: 674<br>
     * Message: It has not yet been seven days since canceling an auction.
     */
	NO_PASSED_7_DAYS_SINCE_CANCEL_AUCTION(674),
	
	/**
     * ID: 675<br>
     * Message: There are no clan halls up for auction.
     */
	THERE_ARE_NO_CLANHALL_FOR_AUCTION(675),
	
	/**
     * ID: 676<br>
     * Message: Since you have already submitted a bid, you are not allowed to 
     * participate in another auction at this time.
     */
	ALREADY_SUBMITTED_A_BID(676),
	
	/**
     * ID: 677<br>
     * Message: Your bid price must be higher than the minimum price that can be bid.
     */
	BID_PRICE_MUST_BE_HIGHER_THAN_MINIMUM_PRICE(677),
	
	/**
     * ID: 678<br>
     * Message: You have submitted a bid in the auction of $s1.
     */
	SUBMITTED_BID_AUCTION_OF_S1(678),
	
	/**
     * ID: 679<br>
     * Message: You have canceled your bid.
     */
	CANCELED_YOUR_BID(679),
	
	/**
     * ID: 680<br>
     * Message: You cannot participate in an auction.
     */
	CANNOT_PARTICIPATE_IN_AUCTION(680),
	
    /**
     * ID: 681<br>
     * Message: The clan does not own a clan hall.
     */
    CLAN_HAS_NO_CLAN_HALL(681),
    
    /**
	 * ID: 682<br>
	 * Message: You are moving to another village. Do you want to continue?	
	 */
    MOVING_TO_ANOTHER_VILLAGE(682),
    
	/**
	 * ID: 683<br>
	 * Message: There are no priority rights on a sweeper.
	 */
	SWEEP_NOT_ALLOWED(683),
	
	/**
     * ID: 684<br>
     * Message: You cannot position mercenaries during a siege.
     */
	CANNOT_POSITION_MERCENARIES_IN_SIEGE(684),
	
	/**
     * ID: 685<br>
     * Message: You cannot apply for clan war with a clan that belongs to the same alliance.
     */
	CANNOT_APPLY_CLAN_WAR_BETWEEN_ALLY(685),
	
	/**
     * ID: 686<br>
     * Message: You have received $s1 damage from the fire of magic.
     */
	RECEIVED_S1_DMG_FROM_FIRE_OF_MAGIC(686),
	
	/**
     * ID: 687<br>
     * Message: You cannot move while frozen. Please wait.
     */
	CANNOT_MOVE_FROZEN(687),
	
	/**
     * ID: 688<br>
     * Message: The clan that owns the castle is automatically registered on 
     * the defending side.
     */
    CLAN_THAT_OWNS_CASTLE_IS_AUTOMATICALLY_REGISTERED_DEFENDING(688),
    
    /**
     * ID: 689<br>
     * Message: A clan that owns a castle cannot participate in another siege.
     */
    CLAN_THAT_OWNS_CASTLE_CANNOT_PARTICIPATE_ANOTHER_SIEGE(689),
    
    /**
     * ID: 690<br>
     * Message: You cannot register on the attacking side because you are part of an 
     * alliance with the clan that owns the castle.
     */
    ALLIED_CLAN_CANNOT_ATTACK_CASTLE(690),
    
    /**
     * ID: 691<br>
     * Message: $s1 clan is already a member of $s2 alliance.
     */
    S1_CLAN_ALREADY_MEMBER_OF_S2_ALLIANCE(691),

	/**
	 * ID: 692<br>
	 * Message: The other party is frozen. Please wait a moment.
	 */
	OTHER_PARTY_IS_FROZEN(692),
	
	/**
	 * ID: 693<br>
	 * Message: The package that arrived is in another warehouse.	
	 */
	PACKAGE_ARRIVED(693),
	
	/**
	 * ID: 694<br>
	 * Message: No packages have arrived.
	 */
	NO_PACKAGES_ARRIVED(694),
	
	/**
	 * ID: 695<br>
	 * Message: You cannot set the name of the pet.
	 */
	NAMING_YOU_CANNOT_SET_NAME_OF_THE_PET(695),
	
	/**
	 * ID: 696<br>
	 * Message: Your account is restricted for not paying your PC room usage fees.
	 */
	
	/**
	 * ID: 697<br>
	 * Message: The item enchant value is strange.
	 */
	THE_ITEM_ENCHANT_VALUE_IS_STRANGE(697),
	
	/**
	 * ID: 698<br>
	 * Message: The price is different than the same item on the sales list.
	 */
	
	/**
	 * ID: 699<br>
	 * Message: Currently not purchasing.
	 */
	CURRENTLY_NOT_PPURCHASING(699),
	
	/**
	 * ID: 700<br>
	 * Message: The purchase is complete.
	 */
	THE_PURCHASE_IS_COMPLETE(700),
	
	/**
	 * ID: 701<br>
	 * Message: You do not have enough required items.
	 */
	NOT_ENOUGH_REQUIRED_ITEMS(701),
	
    /**
     * ID: 702 <br>
     * Message: There are no GMs
     * currently visible in the public list as they may be
     * performing other functions at the moment.
     */
    NO_GM_PROVIDING_SERVICE_NOW(702),
	
	/**
	 * ID: 703<br>
	 * Message: ======<GM List>======
	 */
	GM_LIST(703),

	/**
	 * ID: 704<br>
	 * Message: GM : $s1
	 */
	GM_S1(704),
	
	/**
     * ID: 705<br>
     * Message: You cannot exclude yourself.
     */
	YOU_CANNOT_EXCLUDE_YOURSELF(705),
	
	/**
     * ID: 706<br>
     * Message: You can only register up to 64 names on your exclude list.
     */
	REGISTER_ONLY_64_ON_EXCLUDE_LIST(706),
	
    /**
     * ID: 707<br>
     * Message: You cannot teleport to a village that is in a siege.
     */
    NO_PORT_THAT_IS_IN_SIGE(707),

    /**
	 * ID: 708<br>
	 * Message: You do not have the right to use the castle warehouse.
	 */
    NOT_HAVE_RIGHT_TO_USE_CASTLE_WAREHOUSE(708),
    
	/**
	 * ID: 709<br>
	 * Message: You do not have the right to use the clan warehouse.
	 */
    YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE(709),

    /**
     * ID: 710<br>
     * Message: Only clans of clan level 1 or higher can use a clan warehouse.
     */
    ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE(710),
	
    /**
	 * ID: 711<br>
	 * Message: The siege of $s1 has started.
	 */
    SIEGE_OF_S1_STARTED(711),
    
    /**
	 * ID: 712<br>
	 * Message: The siege of $s1 has finished.
	 */
    SIEGE_OF_S1_FINISHED(712),
    
    /**
	 * ID: 713<br>
	 * Message: $s1/$s2/$s3 $s4:$s5
	 * (TO USE DATE)
	 */
    DATE_S1_S2_S3_S4_S5(713),
    
    /**
	 * ID: 714<br>
	 * Message: A trap device has been tripped.
	 */
    A_TRAP_TRIPPED(714),
    
    /**
	 * ID: 715<br>
	 * Message: The trap device has been stopped.
	 */
    THE_TRAP_STOPPED(715),
    
    /**
	 * ID: 716<br>
	 * Message: If a base camp does not exist, resurrection is not possible.
	 */
    RES_NOT_POSSIBLE_DUE_CAMP_DOES_NOT_EXIST(716),
    
    /**
	 * ID: 717<br>
	 * Message: The guardian tower has been destroyed and resurrection is not possible.
	 */
    GUARDIAN_TOWER_DESTOYED_RES_NOT_POSSIBLE(717),
    
    /**
	 * ID: 718<br>
	 * Message: The castle gates cannot be opened and closed during a siege.
	 */
    CASTLE_GATES_CANNOT_OPENED_DURING_SIEGE(718),
    
    /**
	 * ID: 719<br>
	 * Message: You failed at mixing the item.
	 */
    FAILED_MIXING_ITEM(719),
    
	/**
	 * ID: 720<br>
	 * Message: The purchase price is higher than the amount
	 * of money that you have and so you cannot open a personal store.
	 */
	THE_PURCHASE_PRICE_IS_HIGHER_THAN_MONEY(720),
	
	/**
     * ID: 721<br>
     * Message: You cannot create an alliance while participating in a siege.
     */
	CANNOT_CREATE_AN_ALLIANCE_DURING_SIEGE(721),
	
	 /**
     * ID: 722<br>
     * Message: You cannot dissolve an alliance while an affiliated clan is participating in a siege battle.
     */
    CANNOT_DISSOLVE_ALLY_WHILE_IN_SIEGE(722),

    /**
     * ID: 723<br>
     * Message: The opposing clan is participating in a siege battle.
     */
    OPPOSING_CLAN_IS_PARTICIPATING_IN_SIEGE(723),
    
    /**
     * ID: 730<br>
     * Message: - You have submitted your $s1th petition. - You may submit $s2 more petition(s) today.
     */
    SUBMITTED_YOU_S1_TH_PETITION_S2_LEFT(730),
    
    /**
     * ID: 733<br>
     * Message: We have received $s1 petitions from you today and that is
     * the maximum that you can submit in one day. You cannot submit any more petitions.
     */
    WE_HAVE_RECEIVED_S1_PETITIONS_TODAY(733),
    
    /**
     * ID: 736<br>
     * Message: The petition was canceled. You may submit $s1 more petition(s) today.
     */
    PETITION_CANCELED_SUBMIT_S1_MORE_TODAY(736),
    
    /**
     * ID: 738<br>
     * Message: You have not submitted a petition.
     */
    PETITION_NOT_SUBMITTED(738),

	/**
	 * ID: 749<br>
	 * Message: The effect of $s1 has been removed.
	 */
	EFFECT_S1_DISAPPEARED(749),
	
	/**
	 * ID: 760<br>
	 * Message: $s1 cannot join the clan because one day has not yet passed since he/she left another clan.
	 */
	S1_MUST_WAIT_BEFORE_JOINING_ANOTHER_CLAN(760),
	
	/**
     * ID: 761<br>
     * Message: $s1 clan cannot join the alliance because one day has not yet
     * passed since it left another alliance.
     */
    S1_CANT_ENTER_ALLIANCE_WITHIN_1_DAY(761),
	
    /**
     * ID: 764<br>
     * Message: You have been playing for an extended period of time. Please consider taking a break.
     */
    PLAYING_FOR_LONG_TIME(764),
    
	/**   
     * ID: 769<br>   
     * Message: A hacking tool has been discovered. Please try again after closing unnecessary programs.   
     */   
     HACKING_TOOL(769),
    
    /**
     * ID: 780<br>
     * Message: Observation is only possible during a siege.
     */
    ONLY_VIEW_SIEGE(780),
    
    /**
	 * ID: 781<br>
	 * Message: Observers cannot participate.
	 */
	OBSERVERS_CANNOT_PARTICIPATE(781),

	/**
	 * ID: 782<br>
	 * Message: You may not observe a siege with a pet or servitor summoned.
	 */
	YOU_MAY_NOT_OBSERVER_WITH_SUMMON(782),
	
    /**
     * ID: 783<br>
     * Message: Lottery ticket sales have been temporarily suspended.
     */
    LOTTERY_TICKET_SALES_TEMP_SUSPENDED(783),
	
    /**
     * ID: 784<br>
     * Message: Tickets for the current lottery are no longer available.
     */
    NO_LOTTERY_TICKETS_AVAILABLE(784),
    
	/**
	 * ID: 794<br>
	 * Message: You are not authorized to do that.
	 */
	YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT(794),
	
	 /**
     * ID: 745<br>
     * Message: You are currently not in a petition chat.
     */
    YOU_ARE_NOT_IN_PETITION_CHAT(745),
    
    /**
	 * ID: 750<br>
	 * Message: There are no other skills to learn.
	 */
	NO_MORE_SKILLS_TO_LEARN(750),

	 /**
     * ID: 797<br>
     * Message: You may create up to 24 macros.
     */
    YOU_MAY_CREATE_UP_TO_24_MACROS(797),
	
	/**
     * ID: 810<br>
     * Message: Invalid macro. Refer to the Help file for instructions.
     */
    INVALID_MACRO(810),

	/**
     * ID: 816<br>
     * Message: Tickets are now available for Monster Race $s1!
     */
    MONSRACE_TICKETS_AVAILABLE_FOR_S1_RACE(816),

    /**
     * ID: 817<br>
     * Message: Now selling tickets for Monster Race $s1!
     */
    MONSRACE_TICKETS_NOW_AVAILABLE_FOR_S1_RACE(817),

    /**
     * ID: 818<br>
     * Message: Ticket sales for the Monster Race will end in $s1 minute(s).
     */
    MONSRACE_TICKETS_STOP_IN_S1_MINUTES(818),

    /**
     * ID: 819<br>
     * Message: Tickets sales are closed for Monster Race $s1. Odds are posted.
     */
    MONSRACE_TICKET_SALES_CLOSED(819),

    /**
     * ID: 820<br>
     * Message: Monster Race $s2 will begin in $s1 minute(s)!
     */
    MONSRACE_BEGINS_IN_S1_MINUTES(820),

    /**
     * ID: 821<br>
     * Message: Monster Race $s1 will begin in 30 seconds!
     */
    MONSRACE_BEGINS_IN_30_SECONDS(821),

    /**
     * ID: 822<br>
     * Message: Monster Race $s1 is about to begin! Countdown in five seconds!
     */
    MONSRACE_COUNTDOWN_IN_FIVE_SECONDS(822),

    /**
     * ID: 823<br>
     * Message: The race will begin in $s1 second(s)!
     */
    MONSRACE_BEGINS_IN_S1_SECONDS(823),

    /**
     * ID: 824<br>
     * Message: They're off!
     */
    MONSRACE_RACE_START(824),

    /**
     * ID: 825<br>
     * Message: Monster Race $s1 is finished!
     */
    MONSRACE_RACE_END(825),

    /**
     * ID: 826<br>
     * Message: First prize goes to the player in lane $s1. Second prize goes to the player in lane $s2.
     */
    MONSRACE_FIRST_PLACE_S1_SECOND_S2(826),
    
    /**
     * ID: 827<br>
     * Message: You may not impose a block on a GM.
     */
    YOU_MAY_NOT_IMPOSE_A_BLOCK_AN_A_GM(827),
    
    /**
     * ID: 829<br>
     * Message: You cannot recommend yourself.
     */
    YOU_CANNOT_RECOMMEND_YOURSELF(829),

    /**
     * ID: 830<br>
     * Message: You have recommended $s1. You are authorized to make $s2 more recommendations.
     */
    YOU_HAVE_RECOMMENDED(830),
    
    /**
     * ID: 831<br>
     * Message: You have been recommended by $s1.
     */
    YOU_HAVE_BEEN_RECOMMENDED(831),

    /**
     * ID: 832<br>
     * Message: That character has already been recommended.
     */
    THAT_CHARACTER_IS_RECOMMENDED(832),

    /**
     * ID: 833<br>
     * Message: You are not authorized to make further recommendations at this time.
     * You will receive more recommendation credits each day at 1 p.m.
     */
    NO_MORE_RECOMMENDATIONS_TO_HAVE(833),

    /**
     * ID: 843<br>
     * Message: $s1 has rolled $s2.
     */
    S1_ROLLED_S2(834),

    /**
     * ID: 835<br>
     * Message: You may not throw the dice at this time. Try again later.
     */
    YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER(835),
    
    /**
     * ID: 837<br>
     * Message: Macro descriptions may contain up to 32 characters.
     */
    MACRO_DESCRIPTION_MAX_32_CHARS(837),
    
    /**
     * ID: 838<br>
     * Message: Enter the name of the macro.
     */
    ENTER_THE_MACRO_NAME(838),
    
    /**
     * ID: 840<br>
     * Message: That recipe is already registered.
     */
 	RECIPE_ALREADY_REGISTERED(840),
 	
 	/**
 	 * ID: 841<br>
 	 * Message: No further recipes may be registered.
 	 */
 	NO_FUTHER_RECIPES_CAN_BE_ADDED(841),

    /**
     * ID: 846<br>
     * Message: The siege of $s1 has been canceled due to lack of interest.
     */
    SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST(846),
    
    /**
 	 * ID: 853<br>
 	 * Message: You may not alter your recipe book while engaged in manufacturing.
 	 */
 	CANT_ALTER_RECIPEBOOK_WHILE_CRAFTING(853),
 	
 	/**
     * ID: 871<br>
     * Message: The seed has been sown.
     */
    THE_SEED_HAS_BEEN_SOWN(871),

    /**
     * ID: 872<br>
     * Message: This seed may not be sown here.
     */
    THIS_SEED_MAY_NOT_BE_SOWN_HERE(872),

    /**
     * ID: 873<br>
     * Message: That character does not exist.
     */
    CHARACTER_DOES_NOT_EXIST(873),
	
	/**
     * ID: 877<br>
     * Message: The symbol has been added.
     */
    SYMBOL_ADDED(877),
    
    /**
     * ID: 879<br>
     * Message: The manor system is currently under maintenance.
     */
    THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE(879),

    /**
     * ID: 880<br>
     * Message: The transaction is complete.
     */
    THE_TRANSACTION_IS_COMPLETE(880),

    /**
     * ID: 881<br>
     * Message: There is a discrepancy on the invoice.
     */
    THERE_IS_A_DISCREPANCY_ON_THE_INVOICE(881),

    /**
     * ID: 882<br>
     * Message: The seed quantity is incorrect.
     */
    THE_SEED_QUANTITY_IS_INCORRECT(882),

    /**
     * ID: 883<br>
     * Message: The seed information is incorrect.
     */
    THE_SEED_INFORMATION_IS_INCORRECT(883),

    /**
     * ID: 884<br>
     * Message: The manor information has been updated.
     */
    THE_MANOR_INFORMATION_HAS_BEEN_UPDATED(884),

    /**
     * ID: 885<br>
     * Message: The number of crops is incorrect.
     */
    THE_NUMBER_OF_CROPS_IS_INCORRECT(885),

    /**
     * ID: 886<br>
     * Message: The crops are priced incorrectly.
     */
    THE_CROPS_ARE_PRICED_INCORRECTLY(886),

    /**
     * ID: 887<br>
     * Message: The type is incorrect.
     */
    THE_TYPE_IS_INCORRECT(887),

    /**
     * ID: 888<br>
     * Message: No crops can be purchased at this time.
     */
    NO_CROPS_CAN_BE_PURCHASED_AT_THIS_TIME(888),

    /**
     * ID: 889<br>
     * Message: The seed was successfully sown.
     */
    THE_SEED_WAS_SUCCESSFULLY_SOWN(889),

    /**
     * ID: 890<br>
     * Message: The seed was not sown.
     */
    THE_SEED_WAS_NOT_SOWN(890),

    /**
     * ID: 891<br>
     * Message: You are not authorized to harvest.
     */
    YOU_ARE_NOT_AUTHORIZED_TO_HARVEST(891),

    /**
     * ID: 892<br>
     * Message: The harvest has failed.
     */
    THE_HARVEST_HAS_FAILED(892),

    /**
     * ID: 893<br>
     * Message: The harvest failed because the seed was not sown.
     */
    THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN(893),
    
    /**
     * ID: 894<br>
     * Message: Up to $s1 recipes can be registered.
     */
    UP_TO_S1_RECIPES_CAN_REGISTER(894),
    
    /**
     * ID: 898<br>
     * Message: Only characters of level 10 or above are authorized to make recommendations.
     */
    ONLY_LEVEL_SUP_10_CAN_RECOMMEND(898),

    /**
     * ID: 899<br>
     * Message: The symbol cannot be drawn.
     */
    CANT_DRAW_SYMBOL(899),
    
    /**
     * ID: 910<br>
     * Message: Current location : $s1, $s2, $s3 (Near Talking Island Village)
     */
    LOC_TI_S1_S2_S3(910),

    /**
     * ID: 911<br>
     * Message: Current location : $s1, $s2, $s3 (Near Gludin Village)
     */
    LOC_GLUDIN_S1_S2_S3(911),

    /**
     * ID: 912<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Town of Gludio)
     */
    LOC_GLUDIO_S1_S2_S3(912),

    /**
     * ID: 913<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Neutral Zone)
     */
    LOC_NETRAL_ZONE_S1_S2_S3(913),

    /**
     * ID: 914<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Elven Village)
     */
    LOC_ELVEN_S1_S2_S3(914),

    /**
     * ID: 915<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Dark Elf Village)
     */
    LOC_DARK_ELVEN_S1_S2_S3(915),

    /**
     * ID: 916<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Town of Dion)
     */
    LOC_DION_S1_S2_S3(916),

    /**
     * ID: 917<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Floran Village)
     */
    LOC_FLORAN_S1_S2_S3(917),

    /**
     * ID: 918<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Town of Giran)
     */
    LOC_GIRAN_S1_S2_S3(918),

    /**
     * ID: 919<br>
     * Message: Current location : $s1, $s2, $s3 (Near Giran Harbor)
     */
    LOC_GIRAN_HARBOR_S1_S2_S3(919),

    /**
     * ID: 920<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Orc Village)
     */
    LOC_ORC_S1_S2_S3(920),

    /**
     * ID: 921<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Dwarven Village)
     */
    LOC_DWARVEN_S1_S2_S3(921),

    /**
     * ID: 922<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Town of Oren)
     */
    LOC_OREN_S1_S2_S3(922),

    /**
     * ID: 923<br>
     * Message: Current location : $s1, $s2, $s3 (Near Hunters Village)
     */
    LOC_HUNTER_S1_S2_S3(923),

    /**
     * ID: 924<br>
     * Message: Current location : $s1, $s2, $s3 (Near Aden Castle Town)
     */
    LOC_ADEN_S1_S2_S3(924),

    /**
     * ID: 925<br>
     * Message: Current location : $s1, $s2, $s3 (Near the Coliseum)
     */
    LOC_COLISEUM_S1_S2_S3(925),

    /**
     * ID: 926<br>
     * Message: Current location : $s1, $s2, $s3 (Near Heine)
     */
    LOC_HEINE_S1_S2_S3(926),
    
    /**
     * ID: 927<br>
     * Message: The current time is $s1:$s2 in the day.
     */
    TIME_S1_S2_IN_THE_DAY(927),

    /**
     * ID: 928<br>
     * Message: The current time is $s1:$s2 in the night.
     */
    TIME_S1_S2_IN_THE_NIGHT(928),

    /**
     * ID: 930<br>
     * Message: Lottery tickets are not currently being sold.
     */
    NO_LOTTERY_TICKETS_CURRENT_SOLD(930),
    
    /**
     * ID: 933<br>
     * Message: The seed pricing greatly differs from standard seed prices.
     */
    THE_SEED_PRICING_GREATLY_DIFFERS_FROM_STANDARD_SEED_PRICES(933),

    /**
     * ID: 935<br>
     * Message: The amount is not sufficient and so the manor is not in operation.
     */
    THE_AMOUNT_IS_NOT_SUFFICIENT_AND_SO_THE_MANOR_IS_NOT_IN_OPERATION(935),

    /**
     * ID: 938<br>
     * Message: The community server is currently offline.
     */
    CB_OFFLINE(938),
    
    /**
     * ID: 970<br>
     * Message: $s2's MP has been drained by $s1.
     */
    S2_MP_HAS_BEEN_DRAINED_BY_S1(970),
    
    /**
     * ID: 971<br>
     * Message: Petitions cannot exceed 255 characters.
     */
    PETITION_MAX_CHARS_255(971),
    
    /**
     * ID: 972<br>
     * Message: This pet cannot use this item.
     */
    PET_CANNOT_USE_ITEM(972),
    
    /**
	 * ID: 974<br>
	 * Message: The soul crystal succeeded in absorbing a soul.
	 */
	SOUL_CRYSTAL_ABSORBING_SUCCEEDED(974),
	
	/**
	 * ID: 975<br>
	 * Message: The soul crystal was not able to absorb a soul.
	 */
	SOUL_CRYSTAL_ABSORBING_FAILED(975),
	
	/**
	 * ID: 976<br>
	 * Message: The soul crystal broke because it was not able to endure the soul energy.
	 */
	SOUL_CRYSTAL_BROKE(976),
	
	/**
	 * ID: 977<br>
	 * Message: The soul crystals caused resonation and failed at absorbing a soul.
	 */
	SOUL_CRYSTAL_ABSORBING_FAILED_RESONATION(977),
	
	/**
	 * ID: 978<br>
	 * Message: The soul crystal is refusing to absorb a soul.
	 */
	SOUL_CRYSTAL_ABSORBING_REFUSED(978),
	
	/**
     * ID: 1009<br>
     * Message: A strider cannot be ridden when dead.
     */
    STRIDER_CANT_BE_RIDDEN_WHILE_DEAD(1009),

    /**
     * ID: 1010<br>
     * Message: A dead strider cannot be ridden.
     */
    DEAD_STRIDER_CANT_BE_RIDDEN(1010),

    /**
     * ID: 1011<br>
     * Message: A strider in battle cannot be ridden.
     */
    STRIDER_IN_BATLLE_CANT_BE_RIDDEN(1011),

    /**
     * ID: 1012<br>
     * Message: A strider cannot be ridden while in battle.
     */
    STRIDER_CANT_BE_RIDDEN_WHILE_IN_BATTLE(1012),

    /**
     * ID: 1013<br>
     * Message: A strider can be ridden only when standing.
     */
    STRIDER_CAN_BE_RIDDEN_ONLY_WHILE_STANDING(1013),

	/**
	 * ID: 1014<br>
	 * Message: Your pet gained $s1 experience points.
	 */
    PET_EARNED_S1_EXP(1014),
	
	/**
	 * ID: 1015<br>
	 * Message: Your pet hit for $s1 damage.
	 */
	PET_HIT_FOR_S1_DAMAGE(1015),

	/**
	 * ID: 1016<br>
	 * Message: Your pet received $s2 damage caused by $s1.
	 */
	PET_RECEIVED_S2_DAMAGE_BY_S1(1016),
	
	/**
	 * ID: 1017<br>
	 * Message: Pet's critical hit!
	 */
	CRITICAL_HIT_BY_PET(1017),
	
	 /**
     * ID: 1026<br>
     * Message: The summoned monster gave damage of $s1
     */
	SUMMON_GAVE_DAMAGE_S1(1026),

	/**
	 * ID: 1027<br>
	 * Message: The summoned monster received damage of $s2 caused by $s1.
	 */
	SUMMON_RECEIVED_DAMAGE_S2_BY_S1(1027),
	
	/**
	 * ID: 1028<br>
	 * Message: Summoned monster's critical hit!
	 */
	CRITICAL_HIT_BY_SUMMONED_MOB(1028),
	
	/**
	 * ID: 1030<br>
	 * Message: <Party Information>
	 */
	PARTY_INFORMATION(1030),

	/**
	 * ID: 1031<br>
	 * Message: Looting method: Finders keepers
	 */
	LOOTING_FINDERS_KEEPERS(1031),
	
	/**
	 * ID: 1032<br>
	 * Message: Looting method: Random
	 */
	LOOTING_RANDOM(1032),

	/**
	 * ID: 1033<br>
	 * Message: Looting method: Random including spoil
	 */
	LOOTING_RANDOM_INCLUDE_SPOIL(1033),
	
	/**
	 * ID: 1034<br>
	 * Message: Looting method: By turn
	 */
	LOOTING_BY_TURN(1034),

	/**
	 * ID: 1035<br>
	 * Message: Looting method: By turn including spoil
	 */
	LOOTING_BY_TURN_INCLUDE_SPOIL(1035),
	
	/**
     * ID: 1036<br>
     * Message: You have exceeded the quantity that can be inputted.
     */
    YOU_HAVE_EXCEEDED_QUANTITY_THAT_CAN_BE_INPUTTED(1036),
    
    /**
     * ID: 1039<br>
     * Message: Items left at the clan hall warehouse can only be retrieved by the clan leader.
     * Do you want to continue?
     */
    ONLY_CLAN_LEADER_CAN_RETRIEVE_ITEMS_FROM_CLAN_WAREHOUSE(1039),
    
    /**
     * ID: 1041<br>
     * Message: The next seed purchase price is $s1 adena.
     */
    THE_NEXT_SEED_PURCHASE_PRICE_IS_S1_ADENA(1041),

    /**
     * ID: 1044<br>
     * Message: Monster race payout information is not available while tickets are being sold.
     */
    MONSRACE_NO_PAYOUT_INFO(1044),

    /**
     * ID: 1046<br>
     * Message: Monster race tickets are no longer available.
     */
    MONSRACE_TICKETS_NOT_AVAILABLE(1046),
    
    /**
     * ID: 1050<br>
     * Message: There are no communities in my clan. Clan communities are allowed for clans with skill levels of 2 and higher.
     */
    NO_CB_IN_MY_CLAN(1050),
	
	/**
	 * ID: 1051 <br>
	 * Message: Payment for your clan hall has not been made please make payment tomorrow.
	 */
	PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW(1051),
	/**
	 * ID: 1052 <br>
	 * Message: Payment of Clan Hall is overdue the owner loose Clan Hall.
	 */
	THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED(1052),
	
	/**
	 * ID: 1053<br>
	 * Message: It is not possible to resurrect in battlefields where a siege war is taking place.
	 */
	CANNOT_BE_RESURRECTED_DURING_SIEGE(1053),
	
	/**
     * ID: 1058<br>
     * Message: The sales price for seeds is $s1 adena.
     */
    THE_SALES_PRICE_FOR_SEEDS_IS_S1_ADENA(1058),

    /**
     * ID: 1060<br>
     * Message: The remainder after selling the seeds is $s1.
     */
    THE_REMAINDER_AFTER_SELLING_THE_SEEDS_IS_S1(1060),
	
 	/**
 	 * ID: 1061<br>
 	 * Message: The recipe cannot be registered. You do not have the ability to create items.
 	 */
 	CANT_REGISTER_NO_ABILITY_TO_CRAFT(1061),
	
	/**
	 * ID: 1064<br>
	 * Message: The equipment, +$s1 $s2, has been removed.
	 */
	EQUIPMENT_S1_S2_REMOVED(1064),
	
	/**
	 * ID: 1065<br>
	 * Message: While operating a private store or workshop, you cannot discard, destroy, or trade an item.
	 */
    CANNOT_TRADE_DISCARD_DROP_ITEM_WHILE_IN_SHOPMODE(1065),
	
	/**
     * ID: 1066<br>
     * Message: $s1 HP has been restored.
     */
    S1_HP_RESTORED(1066),

    /**
     * ID: 1067<br>
     * Message: $s2 HP has been restored by $s1
     */
    S2_HP_RESTORED_BY_S1(1067),

    /**
     * ID: 1068<br>
     * Message: $s1 MP has been restored.
     */
    S1_MP_RESTORED(1068),

    /**
     * ID: 1069<br>
     * Message: $s2 MP has been restored by $s1.
     */
    S2_MP_RESTORED_BY_S1(1069),
    
    /**
     * ID: 1112<br>
     * Message: The prize amount for the winner of Lottery #$s1 is $s2 adena. We have $s3 first prize winners.
     */
    AMOUNT_FOR_WINNER_S1_IS_S2_ADENA_WE_HAVE_S3_PRIZE_WINNER(1112),

    /**
     * ID: 1113<br>
     * Message: The prize amount for Lucky Lottery #$s1 is $s2 adena.
     * There was no first prize winner in this drawing, therefore the
     * jackpot will be added to the next drawing.
     */
    AMOUNT_FOR_LOTTERY_S1_IS_S2_ADENA_NO_WINNER(1113),

    /**
	 * ID: 1114<br>
	 * Message: Your clan may not register to participate in a siege while under a
	 * grace period of the clan's dissolution.
	 */
	CANT_PARTICIPATE_IN_SIEGE_WHILE_DISSOLUTION_IN_PROGRESS(1114),
    
    /**
	 * ID: 1116<br>
	 * Message: One cannot leave one's clan during combat.
	 */
	YOU_CANNOT_LEAVE_DURING_COMBAT(1116),
	
	/**
	 * ID: 1117<br>
	 * Message: A clan member may not be dismissed during combat.
	 */
	CLAN_MEMBER_CANNOT_BE_DISMISSED_DURING_COMBAT(1117),
    
    /**
     * ID: 1118<br>
     * Message: Progress in a quest is possible only when your inventory's
     * weight and volume are less than 80 percent of capacity.
     */
    INVENTORY_LESS_THAN_80_PERCENT(1118),
    
    /**
	 * ID: 1125<br>
	 * Message: An item may not be created while engaged in trading.
	 */
	CANNOT_CREATED_WHILE_ENGAGED_IN_TRADING(1125),
	
	/**
 	 * ID: 1135<br>
 	 * Message: While you are engaged in combat, you cannot operate a private store or private workshop.
 	 */
 	CANT_CRAFT_DURING_COMBAT(1135),
 	
 	/**
     * ID: 1137<br>
     * Message: $s1 harvested $s3 $s2(s).
     */
    S1_HARVESTED_S3_S2S(1137),

    /**
     * ID: 1138<br>
     * Message: $s1 harvested $s2(s).
     */
    S1_HARVESTED_S2S(1138),

	 /**
     * ID: 1176<br>
     * Message: This is a quest event period.
     */
	QUEST_EVENT_PERIOD(1176),

	/**
	 * ID: 1177<br>
	 * Message: This is the seal validation period.
	 */
	VALIDATION_PERIOD(1177),

	///**
	// * ID: 1178<br>
	// */
	// AVARICE_DESCRIPTION(1178),

	///**
	//* ID: 1179<br>
	// */
	// GNOSIS_DESCRIPTION(1179),

	///**
	// * ID: 1180<br>
	// */
	// STRIFE_DESCRIPTION(1180),

	/**
	 * ID: 1183<br>
	 * Message: This is the initial period.
	 */
	INITIAL_PERIOD(1183),

	/**
	 * ID: 1184<br>
	 * Message: This is a period of calculating statistics in the server.
	 */
	RESULTS_PERIOD(1184),
	
    /**
     * ID: 1188<br>
     * Message: Your selected target can no longer receive a recommendation.
     */
    YOU_NO_LONGER_RECIVE_A_RECOMMENDATION(1188),
    
    /**
     * ID: 1196<br>
     * Message: Your force has reached maximum capacity.
     */
    FORCE_MAXIMUM(1196),

	/**
     * ID: 1197<br>
     * Message: Summoning a servitor costs $s2 $s1.
     */
    SUMMONING_SERVITOR_COSTS_S2_S1(1197),
    
    /**
     * ID: 1200<br>
     * Message:($s1 ($s2 Alliance)
     */
    S1_S2_ALLIANCE(1200),

    /**
     * ID: 1202<br>
     * Message:($s1 (No alliance exists)
     */
    S1_NO_ALLI_EXISTS(1202),
    
    /**
     * ID: 1208<br>
     * Message: $s1 died and dropped $s3 $s2.
     */
    S1_DIED_DROPPED_S3_S2(1208),

	/**
     * ID: 1209<br>
     * Message: Congratulations. Your raid was successful.
     */
    RAID_WAS_SUCCESSFUL(1209),
    
    /**
	 * ID: 1210<br>
	 * Message: Seven Signs: The quest event period has begun.
	 * Visit a Priest of Dawn or Priestess of Dusk to participate in the event.
	 */
    QUEST_EVENT_PERIOD_BEGUN(1210),

    /**
     * ID: 1211<br>
     * Message: Seven Signs: The quest event period has ended.
     * The next quest event will start in one week.
     */
    QUEST_EVENT_PERIOD_ENDED(1211),

    /**
     * ID: 1212<br>
     * Message: Seven Signs: The Lords of Dawn have obtained the Seal of Avarice.
     */
    DAWN_OBTAINED_AVARICE(1212),

    /**
     * ID: 1213<br>
     * Message: Seven Signs: The Lords of Dawn have obtained the Seal of Gnosis.
     */
    DAWN_OBTAINED_GNOSIS(1213),

    /**
     * ID: 1214<br>
     * Message: Seven Signs: The Lords of Dawn have obtained the Seal of Strife.
     */
    DAWN_OBTAINED_STRIFE(1214),

    /**
     * ID: 1215<br>
     * Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Avarice.
     */
    DUSK_OBTAINED_AVARICE(1215),

    /**
     * ID: 1216<br>
     * Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Gnosis.
     */
    DUSK_OBTAINED_GNOSIS(1216),

    /**
     * ID: 1217<br>
     * Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Strife.
     */
    DUSK_OBTAINED_STRIFE(1217),

    /**
     * ID: 1218<br>
     * Message: Seven Signs: The Seal Validation period has begun.
     */
    SEAL_VALIDATION_PERIOD_BEGUN(1218),

    /**
     * ID: 1219<br>
     * Message: Seven Signs: The Seal Validation period has ended.
     */
    SEAL_VALIDATION_PERIOD_ENDED(1219),
    
    /**
     * ID: 1235<br>
     * Message: Do you wish to delete all your friends?
     */
    DO_YOU_WISH_TO_DELETE_FRIENDLIST(1235),
    
    /**
     * ID: 1240<br>
     * Message: Seven Signs: The Revolutionaries of Dusk have won.
     */
    DUSK_WON(1240),
    
    /**
     * ID: 1241<br>
     * Message: Seven Signs: The Lords of Dawn have won.
     */
    DAWN_WON(1241),
    
    /**
     * ID: 1260<br>
     * Message: Seven Signs: Preparations have begun for the next quest event.
     */
    PREPARATIONS_PERIOD_BEGUN(1260),

    /**
     * ID: 1261<br>
     * Message: Seven Signs: The quest event period has begun.
     * Speak with a Priest of Dawn or Dusk Priestess if you wish to participate in the event.
     */
    COMPETITION_PERIOD_BEGUN(1261),

    /**
     * ID: 1262<br>
     * Message: Seven Signs: Quest event has ended. Results are being tallied.
     */
    RESULTS_PERIOD_BEGUN(1262),

    /**
     * ID: 1263<br>
     * Message: Seven Signs: This is the seal validation period. A new quest event period begins next Monday.
     */
    VALIDATION_PERIOD_BEGUN(1263),

    /**
     * ID: 1267<br>
     * Message: Your contribution score is increased by $s1.
     */
    CONTRIB_SCORE_INCREASED(1267),
    
    /**
     * ID: 1269<br>
     * Message: The new sub class has been added.
     */
    ADD_NEW_SUBCLASS(1269),
    
    /**
     * ID: 1270<br>
     * Message: The transfer of sub class has been completed.
     */
    SUBCLASS_TRANSFER_COMPLETED(1270),
    
    /**
     * ID: 1273<br>
     * Message: You will participate in the Seven Signs as a member of the Lords of Dawn.
     */
    SEVENSIGNS_PARTECIPATION_DAWN(1273),

    /**
     * ID: 1274<br>
     * Message: You will participate in the Seven Signs as a member of the Revolutionaries of Dusk.
     */
    SEVENSIGNS_PARTECIPATION_DUSK(1274),

    /**
     * ID: 1275<br>
     * Message: You've chosen to fight for the Seal of Avarice during this quest event period.
     */
    FIGHT_FOR_AVARICE(1275),

    /**
     * ID: 1276<br>
     * Message: You've chosen to fight for the Seal of Gnosis during this quest event period.
     */
    FIGHT_FOR_GNOSIS(1276),

    /**
     * ID: 1277<br>
     * Message: You've chosen to fight for the Seal of Strife during this quest event period.
     */
    FIGHT_FOR_STRIFE(1277),
    
    /**
     * ID: 1278<br>
     * Message: The NPC server is not operating at this time.
     */
    NPC_SERVER_NOT_OPERATING(1278),


    /**
     * ID: 1279<br>
     * Message: Contribution level has exceeded the limit. You may not continue.
     */
    CONTRIB_SCORE_EXCEEDED(1279),

	/**
	 * ID: 1280<br>
	 * Message: Magic Critical Hit!
	 */
	CRITICAL_HIT_MAGIC(1280),
	
	/**
	 * ID: 1281<br>
	 * Message: Your excellent shield defense was a success!
	 */
	YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS(1281),

	/**
	 * ID: 1282<br>
	 * Message: Your Karma has been changed to $s1
	 */
	YOUR_KARMA_HAS_BEEN_CHANGED_TO(1282),
	
	 /**
     * ID: 1286<br>
     * Message: (Until next Monday at 6:00 p.m.)
     */
    UNTIL_MONDAY_6PM(1286),

    /**
     * ID: 1287<br>
     * Message: (Until today at 6:00 p.m.)
     */
    UNTIL_TODAY_6PM(1287),

    ///**
    // * ID: 1288<br>
    // * Message: If trends continue, $s1 will win and the seal will belong to:
    // */
    // S1_WILL_WIN_COMPETITION(1288),

    /**
	 * ID: 1289<br>
	 * Message: Since the seal was owned during the previous period and 10 percent or more people have voted.
	 */
	SEAL_OWNED_10_MORE_VOTED(1289),
	
    /**
     * ID: 1290<br>
     * Message: Although the seal was not owned, since 35 percent or more people have voted.
     */
    SEAL_NOT_OWNED_35_MORE_VOTED(1290),

    /**
     * ID: 1291<br>
     * Message: Although the seal was owned during the previous period,
     * because less than 10 percent of people have voted.
     */
    SEAL_OWNED_10_LESS_VOTED(1291),

    /**
     * ID: 1292<br>
     * Message: Since the seal was not owned during the previous period,
     * and since less than 35 percent of people have voted.
     */
    SEAL_NOT_OWNED_35_LESS_VOTED(1292),

    ///**
    // * ID: 1293<br>
    // * Message: If current trends continue, it will end in a tie.
    // */
    // COMPETITION_WILL_TIE(1293),

    /**
     * ID: 1294<br>
     * Message: The competition has ended in a tie. Therefore, nobody has been awarded the seal.
     */
    COMPETITION_TIE_SEAL_NOT_AWARDED(1294),
	
    /**
     * ID: 1295<br>
     * Message: Sub classes may not be created or changed while a skill is in use.
     */
    SUBCLASS_NO_CHANGE_OR_CREATE_WHILE_SKILL_IN_USE(1295),
    
    /**
     * ID: 1301<br>
     * Message: Only a Lord of Dawn may use this.
     */
    CAN_BE_USED_BY_DAWN(1301),

    /**
     * ID: 1302<br>
     * Message: Only a Revolutionary of Dusk may use this.
     */
    CAN_BE_USED_BY_DUSK(1302),
    
    /**
     * ID: 1308<br>
     * Message: Congratulations - You've completed a class transfer!
     */
    CLASS_TRANSFER(1308),

	/**
	 * ID: 1384<br>
	 * Message: $s1 has become the party leader.
	 */
	S1_HAS_BECOME_A_PARTY_LEADER(1384),
	
	/**
	 * ID: 1399<br>
	 * Message: Only the leader of the party can transfer party leadership to another player.
	 */
	ONLY_A_PARTY_LEADER_CAN_TRANSFER_ONES_RIGHTS_TO_ANOTHER_PLAYER(1399),
	
	/**
	 * ID: 1400<br>
	 * Message: Please select the person you wish to make the party leader.
	 */
	PLEASE_SELECT_THE_PERSON_TO_WHOM_YOU_WOULD_LIKE_TO_TRANSFER_THE_RIGHTS_OF_A_PARTY_LEADER(1400),

	/**
	 * ID: 1401<br>
	 * Message: Slow down.you are already the party leader.
	 */
	YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF(1401),

	/**
	 * ID: 1402<br>
	 * Message: You may only transfer party leadership to another member of the party.
	 */
	YOU_CAN_TRANSFER_RIGHTS_ONLY_TO_ANOTHER_PARTY_MEMBER(1402),

	/**
	 * ID: 1403<br>
	 * Message: You have failed to transfer the party leadership.
	 */
	YOU_HAVE_FAILED_TO_TRANSFER_THE_PARTY_LEADER_RIGHTS(1403),
	
	/**
     * ID: 1405<br>
     * Message: $s1 CPs have been restored.
     */
    S1_CP_WILL_BE_RESTORED(1405),
    
    /**
     * ID: 1433<br>
     * Message: You will now automatically apply $s1 to your target.
     */
    USE_OF_S1_WILL_BE_AUTO(1433),
    
    /**
     * ID: 1434<br>
     * Message: You will no longer automatically apply $s1 to your weapon.
     */
	AUTO_USE_OF_S1_CANCELLED(1434),
	
	 /**
     * ID: 1438<br>
     * Message: There is no skill that enables enchant.
     */
    THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT(1438),

    /**
     * ID: 1439<br>
     * Message: You do not have all of the items needed to enchant that skill.
     */
    YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL(1439),

    /**
     * ID: 1440<br>
     * Message: You have succeeded in enchanting the skill $s1.
     */
    YOU_HAVE_SUCCEEDED_IN_ENCHANTING_THE_SKILL_S1(1440),

    /**
     * ID: 1441<br>
     * Message: You have failed to enchant the skill $s1.
     */
    YOU_HAVE_FAILED_TO_ENCHANT_THE_SKILL_S1(1441),

    /**
     * ID: 1443<br>
     * Message: You do not have enough SP to enchant that skill.
     */
    YOU_DONT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL(1443),

    /**
     * ID: 1444<br>
     * Message: You do not have enough experience (Exp) to enchant that skill.
     */
    YOU_DONT_HAVE_ENOUGH_EXP_TO_ENCHANT_THAT_SKILL(1444),
	
	/**
     * ID: 1447<br>
     * Message: You cannot do that while fishing.
     */
    CANNOT_DO_WHILE_FISHING_1(1447),

    /**
     * ID: 1448<br>
     * Message: Only fishing skills may be used at this time.
     */
    ONLY_FISHING_SKILLS_NOW(1448),

    /**
     * ID: 1449<br>
     * Message: You've got a bite!
     */
    GOT_A_BITE(1449),

    /**
     * ID: 1450<br>
     * Message: That fish is more determined than you are - it spit the hook!
     */
    FISH_SPIT_THE_HOOK(1450),

    /**
     * ID: 1451<br>
     * Message: Your bait was stolen by that fish!
     */
    BAIT_STOLEN_BY_FISH(1451),

    /**
     * ID: 1452<br>
     * Message: Baits have been lost because the fish got away.
     */
    BAIT_LOST_FISH_GOT_AWAY(1452),

    /**
     * ID: 1453<br>
     * Message: You do not have a fishing pole equipped.
     */
    FISHING_POLE_NOT_EQUIPPED(1453),

    /**
     * ID: 1454<br>
     * Message: You must put bait on your hook before you can fish.
     */
    BAIT_ON_HOOK_BEFORE_FISHING(1454),

    /**
     * ID: 1455<br>
     * Message: You cannot fish while under water.
     */
    CANNOT_FISH_UNDER_WATER(1455),

    /**
     * ID: 1456<br>
     * Message: You cannot fish while riding as a passenger of a boat - it's against the rules.
     */
    CANNOT_FISH_ON_BOAT(1456),

    /**
     * ID: 1457<br>
     * Message: You can't fish here.
     */
    CANNOT_FISH_HERE(1457),

    /**
     * ID: 1458<br>
     * Message: Your attempt at fishing has been cancelled.
     */
    FISHING_ATTEMPT_CANCELLED(1458),

    /**
     * ID: 1459<br>
     * Message: You do not have enough bait.
     */
    NOT_ENOUGH_BAIT(1459),

    /**
     * ID: 1460<br>
     * Message: You reel your line in and stop fishing.
     */
    REEL_LINE_AND_STOP_FISHING(1460),

    /**
     * ID: 1461<br>
     * Message: You cast your line and start to fish.
     */
    CAST_LINE_AND_START_FISHING(1461),

    /**
     * ID: 1462<br>
     * Message: You may only use the Pumping skill while you are fishing.
     */
    CAN_USE_PUMPING_ONLY_WHILE_FISHING(1462),

    /**
     * ID: 1463<br>
     * Message: You may only use the Reeling skill while you are fishing.
     */
    CAN_USE_REELING_ONLY_WHILE_FISHING(1463),

    /**
     * ID: 1464<br>
     * Message: The fish has resisted your attempt to bring it in.
     */
    FISH_RESISTED_ATTEMPT_TO_BRING_IT_IN(1464),

    /**
     * ID: 1465<br>
     * Message: Your pumping is successful, causing $s1 damage.
     */
    PUMPING_SUCCESFUL_S1_DAMAGE(1465),

    /**
     * ID: 1466<br>
     * Message: You failed to do anything with the fish and it regains $s1 HP.
     */
    FISH_RESISTED_PUMPING_S1_HP_REGAINED(1466),

    /**
     * ID: 1467<br>
     * Message: You reel that fish in closer and cause $s1 damage.
     */
    REELING_SUCCESFUL_S1_DAMAGE(1467),

    /**
     * ID: 1468<br>
     * Message: You failed to reel that fish in further and it regains $s1 HP.
     */
    FISH_RESISTED_REELING_S1_HP_REGAINED(1468),

    /**
     * ID: 1469<br>
     * Message: You caught something!
     */
    YOU_CAUGHT_SOMETHING(1469),

    /**
     * ID: 1470<br>
     * Message: You cannot do that while fishing.
     */
    CANNOT_DO_WHILE_FISHING_2(1470),

    /**
     * ID: 1471<br>
     * Message: You cannot do that while fishing.
     */
    CANNOT_DO_WHILE_FISHING_3(1471),

    /**
     * ID: 1472<br>
     * Message: You look oddly at the fishing pole in disbelief and
     * realize that you can't attack anything with this.
     */
    CANNOT_ATTACK_WITH_FISHING_POLE(1472),
	
    /**
     * ID: 1479<br>
     * Message: That is the wrong grade of soulshot for that fishing pole.
     */
    WRONG_FISHINGSHOT_GRADE(1479),
    
    /**
     * ID: 1490<br>
     * Message: Traded $s2 of crop $s1.
     */
    TRADED_S2_OF_CROP_S1(1490),

    /**
     * ID: 1491<br>
     * Message: Failed in trading $s2 of crop $s1.
     */
    FAILED_IN_TRADING_S2_OF_CROP_S1(1491),

	 /**
     * ID: 1492<br>
     * Message: You will be moved to the Olympiad Stadium in $s1 second(s).
     */
    YOU_WILL_ENTER_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S(1492),

    /**
     * ID: 1493<br>
     * Message: Your opponent made haste with their tail between their legs), the match has been cancelled.
     */
    THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME(1493),

    /**
     * ID: 1494<br>
     * Message: Your opponent does not meet the requirements to do battle), the match has been cancelled.
     */
    THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME(1494),

    /**
     * ID: 1495<br>
     * Message: The Grand Olympiad match will start in $s1 second(s).
     */
    THE_GAME_WILL_START_IN_S1_SECOND_S(1495),

    /**
     * ID: 1496<br>
     * Message: The match has started, fight!
     */
    STARTS_THE_GAME(1496),

    /**
     * ID: 1497<br>
     * Message: Congratulations $s1, you win the match!
     */
    S1_HAS_WON_THE_GAME(1497),

    /**
     * ID: 1498<br>
     * Message: There is no victor), the match ends in a tie.
     */
    THE_GAME_ENDED_IN_A_TIE(1498),

    /**
     * ID: 1499<br>
     * Message: You will be moved back to town in $s1 second(s).
     */
    YOU_WILL_GO_BACK_TO_THE_VILLAGE_IN_S1_SECOND_S(1499),

    /**
     * ID: 1500<br>
     * Message: You cannot participate in the Grand Olympiad Games with a 
     * character in their subclass.
     */
    YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER(1500),

    /**
     * ID: 1501<br>
     * Message: Only Noblesse can participate in the Olympiad.
     */
    ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD(1501),

    /**
     * ID: 1502<br>
     * Message: You have already been registered in a waiting list of an event.
     */
    YOU_HAVE_ALREADY_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_AN_EVENT(1502),

    /**
     * ID: 1503<br>
     * Message: You have been registered in the Grand Olympiad Games waiting list for a class specific match.
     */
    YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES(1503),

    /**
     * ID: 1504<br>
     * Message: You have been registered in the Grand Olympiad Games waiting list for a non-class specific match.
     */
    YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES(1504),

    /**
     * ID: 1505<br>
     * Message: You have been removed from the Grand Olympiad Games waiting list.
     */
    YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME(1505),

    /**
     * ID: 1506<br>
     * Message: You are not currently registered on any Grand Olympiad Games waiting list.
     */
    YOU_HAVE_NOT_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_A_GAME(1506),

    /**
     * ID: 1507<br>
     * Message: You cannot equip that item in a Grand Olympiad Games match.
     */
    THIS_ITEM_CANT_BE_EQUIPPED_FOR_THE_OLYMPIAD_EVENT(1507),

    /**
     * ID: 1508<br>
     * Message: You cannot use that item in a Grand Olympiad Games match.
     */
    THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT(1508),

    /**
     * ID: 1509<br>
     * Message: You cannot use that skill in a Grand Olympiad Games match.
     */
    THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT(1509),
	
    /**
     * ID: 1510<br>
     * Message: $s1 is making an attempt at resurrection.
     * Do you want to continue with this resurrection?
     */
    RESSURECTION_REQUEST(1510),

	/**
     * ID: 1511<br>
     * Message: While a pet is attempting to resurrect, it cannot help in resurrecting its master.
     */
    MASTER_CANNOT_RES(1511),

    /**
     * ID: 1513<br>
     * Message: Resurrection has already been proposed.
     */
    RES_HAS_ALREADY_BEEN_PROPOSED(1513),
    
    /**
     * ID: 1515<br>
     * Message: A pet cannot be resurrected while it's owner is in the process of resurrecting.
     */
    PET_CANNOT_RES(1515),
    
    /**
     * ID: 1516<br>
     * Message: The target is unavailable for seeding.
     */
    THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING(1516),
    
    /**
	 * ID: 1517<br>
	 * Message: Failed in Blessed Enchant. The enchant value of the item became 0.
	 */
	BLESSED_ENCHANT_FAILED(1517),
	
	/** 
	 * ID: 1518<br> 
	 * Message: You do not meet the required condition to equip that item. 
	 */ 
	CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION(1518),

	/**
     * ID: 1527<br>
     * Message: Your pet was hungry so it ate $s1.
     */
    PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY(1527),
    
	 /**
     * ID: 1533<br>
     * Message: Attention: $s1 picked up $s2.
     */
    ATTENTION_S1_PICKED_UP_S2(1533),

    /**
     * ID: 1534<br>
     * Message: Attention: $s1 picked up +$s2 $s3.
     */
    ATTENTION_S1_PICKED_UP_S2_S3(1534),
    
    /**
     * ID: 1537<br>
     * Message: Current Location: $s1, $s2, $s3 (near Rune Village)
     */
    LOC_RUNE_S1_S2_S3(1537),

    /**
     * ID: 1538<br>
     * Message: Current Location: $s1, $s2, $s3 (near the Town of Goddard)
     */
    LOC_GODDARD_S1_S2_S3(1538),
    
    /**
     * ID: 1557<br>
     * Message: Seed price should be more than $s1 and less than $s2.
     */
    SEED_PRICE_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1557),

    /**
     * ID: 1558<br>
     * Message: The quantity of seed should be more than $s1 and less than $s2.
     */
    THE_QUANTITY_OF_SEED_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1558),

    /**
     * ID: 1559<br>
     * Message: Crop price should be more than $s1 and less than $s2.
     */
    CROP_PRICE_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1559),

    /**
     * ID: 1560<br>
     * Message: The quantity of crop should be more than $s1 and less than $s2
     */
    THE_QUANTITY_OF_CROP_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1560),
	
	/**
     * ID: 1561<br>
     * Message: The clan, $s1, has declared a Clan War.
     */
    CLAN_S1_DECLARED_WAR(1561),
	
	/**
     * ID: 1562<br>
     * Message: A Clan War has been declared against the clan, $s1.
     * If you are killed during the Clan War by members of the opposing clan,
     * you will only lose a quarter of the normal experience from death.
     */
    CLAN_WAR_DECLARED_AGAINST_S1_IF_KILLED_LOSE_LOW_EXP(1562),
	
	/**
     * ID: 1564<br>
     * Message: A Clan War can be declared only if the clan is
     * level three or above, and the number of clan members is fifteen or greater.
     */
    CLAN_WAR_DECLARED_IF_CLAN_LVL3_OR_15_MEMBER(1564),

    /**
     * ID: 1565<br>
     * Message: A Clan War cannot be declared against a clan that does not exist!
     */
    CLAN_WAR_CANNOT_DECLARED_CLAN_NOT_EXIST(1565),
    
    /**
	 * ID: 1566<br>
	 * Message: The clan, $s1, has decided to stop the war.
	 */
	CLAN_S1_HAS_DECIDED_TO_STOP(1566),
	
    /**
     * ID: 1567<br>
     * Message: The war against $s1 Clan has been stopped.
     */
    WAR_AGAINST_S1_HAS_STOPPED(1567),
    
    /**
     * ID: 1569<br>
     * Message: A declaration of Clan War against an allied clan can't be made.
     */
    CLAN_WAR_AGAINST_A_ALLIED_CLAN_NOT_WORK(1569),
    
    /**
     * ID: 1571<br>
     * Message: ======<Clans You've Declared War On>======
     */
    CLANS_YOU_DECLARED_WAR_ON(1571),

    /**
     * ID: 1572<br>
     * Message: ======<Clans That Have Declared War On You>======
     */
    CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU(1572),
    
    /**
     * ID: 1576<br>
     * Message: Pet uses the power of spirit.
     */
    PET_USE_THE_POWER_OF_SPIRIT(1576),
    
    /**
     * ID: 1598<br>
     * Message: Soulshots and spiritshots are not available for a dead pet or servitor. Sad, isn't it?
     */
    SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET(1598),
    
    /**
     * ID: 1604<br>
     * Message: While dressed in formal wear, you can't use items that require all skills and casting operations.
     */
    CANNOT_USE_ITEMS_SKILLS_WITH_FORMALWEAR(1604),
    
    /**
     * ID: 1605<br>
     * Message: * Here, you can buy only seeds of $s1 Manor.
     */
    HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR(1605),

    /**
     * ID: 1606<br>
     * Message: Congratulations - You've completed the third-class transfer quest!
     */
    THIRD_CLASS_TRANSFER(1606),
    
    /**
	 * ID: 1607<br>
	 * Message: $s1 adena has been withdrawn to pay for purchasing fees.
	 */
	S1_ADENA_HAS_BEEN_WITHDRAWN_TO_PAY_FOR_PURCHASING_FEES(1607),

	/**
	 * ID: 1611<br>
	 * Message: Leader: $s1
	 */
	PARTY_LEADER_S1(1611),

	/**
	 * ID: 1582<br>
	 * Message: You have joined the Command Channel.
	 */
	JOINED_COMMAND_CHANNEL(1582),

	/**
	 * ID: 1581<br>
	 * Message: The Command Channel has been disbanded.
	 */
	COMMAND_CHANNEL_DISBANDED(1581),

	/**
	 * ID: 1589<br>
	 * Message: Command Channel authority has been transferred to $s1.
	 */
	COMMAND_CHANNEL_LEADER_NOW_S1(1589),

	/**
     * ID: 1612<br>
     * Message: =====<War List>=====
     */
    WAR_LIST(1612),
    
    /**
     * ID: 1638<br>
     * Message: You cannot fish while using a recipe book, private manufacture or private store.
     */
    CANNOT_FISH_WHILE_USING_RECIPE_BOOK(1638),
    
    /**
     * ID: 1639<br>
     * Message: Period $s1 of the Grand Olympiad Games has started!
     */
    OLYMPIAD_PERIOD_S1_HAS_STARTED(1639),

    /**
     * ID: 1640<br>
     * Message: Period $s1 of the Grand Olympiad Games has now ended.
     */
    OLYMPIAD_PERIOD_S1_HAS_ENDED(1640),

    /**
     * ID: 1641<br>
     * Message: Sharpen your swords, tighten the stitchings in your armor,
     * and make haste to a Grand Olympiad Manager!
     * Battles in the Grand Olympiad Games are now taking place!
     */
    THE_OLYMPIAD_GAME_HAS_STARTED(1641),

    /**
     * ID: 1642<br>
     * Message: Much carnage has been left for the cleanup crew of the Olympiad Stadium.
     * Battles in the Grand Olympiad Games are now over!
     */
    THE_OLYMPIAD_GAME_HAS_ENDED(1642),

    /**
     * ID: 1651<br>
     * Message: The Grand Olympiad Games are not currently in progress.
     */
    THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS(1651),
    
    /**
     * ID: 1655<br>
     * Message: You caught something smelly and scary, maybe you should throw it back!?
     */
    YOU_CAUGHT_SOMETHING_SMELLY_THROW_IT_BACK(1655),

    /**
     * ID: 1657<br>
     * Message: $s1 has earned $s2 points in the Grand Olympiad Games.
     */
    S1_HAS_GAINED_S2_OLYMPIAD_POINTS(1657),

    /**
     * ID: 1658<br>
     * Message: $s1 has lost $s2 points in the Grand Olympiad Games.
     */
    S1_HAS_LOST_S2_OLYMPIAD_POINTS(1658),
    
    /**
     * ID: 1662<br>
     * Message: The fish are no longer biting here because you've caught
     * too many! Try fishing in another location.
     */
    FISH_NO_MORE_BITING_TRY_OTHER_LOCATION(1662),


	/**
	 * ID: 1663<br>
	 * Message: The clan crest was successfully registered. Remember, only a clan
	 * that owns a clan hall or castle can have their crest displayed.
	 */
	CLAN_EMBLEM_WAS_SUCCESSFULLY_REGISTERED(1663),
	
	  /**
     * ID: 1664<br>
     * Message: The fish is resisting your efforts to haul it in! Look at that bobber go!
     */
    FISH_RESISTING_LOOK_BOBBLER(1664),

    /**
     * ID: 1665<br>
     * Message: You've worn that fish out! It can't even pull the bobber under the water!
     */
    YOU_WORN_FISH_OUT(1665),

	/**
     * ID: 1667<br>
     * Message: Lethal Strike!
     */
    LETHAL_STRIKE(1667),

    /**
     * ID: 1668<br>
     * Message: Your lethal strike was successful!
     */
    LETHAL_STRIKE_SUCCESSFUL(1668),
    
    /**
     * ID: 1669<br>
     * Message: There was nothing found inside of that.
     */
    NOTHING_INSIDE_THAT(1669),

    /**
     * ID: 1670<br>
     * Message: Due to your Reeling and/or Pumping skill being three
     * or more levels higher than your Fishing skill, a 50 damage penalty will be applied.
     */
    REELING_PUMPING_3_LEVELS_HIGHER_THAN_FISHING_PENALTY(1670),

    /**
     * ID: 1671<br>
     * Message: Your reeling was successful! (Mastery Penalty:$s1 )
     */
    REELING_SUCCESSFUL_PENALTY_S1(1671),

    /**
     * ID: 1672<br>
     * Message: Your pumping was successful! (Mastery Penalty:$s1 )
     */
    PUMPING_SUCCESSFUL_PENALTY_S1(1672),
    
    /**
     * ID: 1673<br>
     * Message: Your current record for this Grand Olympiad is $s1 match(es), $s2 win(s) and $s3 defeat(s). You have earned $s4 Olympiad Point(s).
     */
    THE_PRESENT_RECORD_DURING_THE_CURRENT_OLYMPIAD_SESSION_IS_S1_WINS_S2_DEFEATS_YOU_HAVE_EARNED_S3_OLYMPIAD_POINTS(1673),
    
    /**
     * ID: 1675<br>
     * Message: A manor cannot be set up between 6 a.m. and 8 p.m.
     */
    A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM(1675),
    
    /**
     * ID: 1687<br>
	 * Message: This area cannot be entered while mounted atop of a Wyvern.  You will be dismounted from your Wyvern if you do not leave!
	 */
    AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN(1687),
    
    /**
     * ID: 1689<br>
     * Message: You have already joined the waiting list for a class specific match.
     */
    YOU_ARE_ALREADY_ON_THE_WAITING_LIST_TO_PARTICIPATE_IN_THE_GAME_FOR_YOUR_CLASS(1689),

    /**
     * ID: 1690<br>
     * Message: You have already joined the waiting list for a non-class specific match.
     */
    YOU_ARE_ALREADY_ON_THE_WAITING_LIST_FOR_ALL_CLASSES_WAITING_TO_PARTICIPATE_IN_THE_GAME(1690),

    /**
     * ID: 1691<br>
     * Message: You can't join a Grand Olympiad Game match with that much stuff on you!
     * Reduce your weight to below 80 percent full and request to join again!
     */
    SINCE_80_PERCENT_OR_MORE_OF_YOUR_INVENTORY_SLOTS_ARE_FULL_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD(1691),

    /**
     * ID: 1692<br>
     * Message: You have changed from your main class to a subclass
     * and therefore are removed from the Grand Olympiad Games waiting list.
     */
    SINCE_YOU_HAVE_CHANGED_YOUR_CLASS_INTO_A_SUB_JOB_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD(1692),

    /**
     * ID: 1693<br>
     * Message: You may not observe a Grand Olympiad Games match while you are on the waiting list.
     */
    WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME(1693),

	 /**
     * ID: 1700<br>
     * Message: You don't have enough spiritshots needed for a pet/servitor.
     */
    NOT_ENOUGH_SPIRITHOTS_FOR_PET(1700),
	
	/**
     * ID: 1701<br>
     * Message: You don't have enough soulshots needed for a pet/servitor.
     */
    NOT_ENOUGH_SOULSHOTS_FOR_PET(1701),


	/**
	 * ID: 1586<br>
	 * Message: You have quit the Command Channel.
	 */
	LEFT_COMMAND_CHANNEL(1586),

	/**
	 * ID: 1587<br>
	 * Message: $s1's party has left the Command Channel.
	 */
	S1_PARTY_LEFT_COMMAND_CHANNEL(1587),

	/**
	 * ID: 1575<br>
	 * Message: Command Channels can only be formed by a party leader who is also the leader of a level 5 clan.
	 */
	COMMAND_CHANNEL_ONLY_BY_LEVEL_5_CLAN_LEADER_PARTY_LEADER(1575),

	/**
	 * ID: 1529<br>
	 * Message: $s1 is inviting you to the command channel. Do you want accept?
	 */
	COMMAND_CHANNEL_CONFIRM(1529),

	/**
	 * ID: 1580<br>
	 * Message: The Command Channel has been formed.
	 */
	COMMAND_CHANNEL_FORMED(1580),

	/**
	 * ID: 1680<br>
	 * Message: $s1 has declined the channel invitation.
	 */
	S1_DECLINED_CHANNEL_INVITATION(1680),

	/**
	 * ID: 1594<br>
	 * Message: $s1's party is already a member of the Command Channel.
	 */
	S1_ALREADY_MEMBER_OF_COMMAND_CHANNEL(1594),

	/**
	 * ID: 1593<br>
	 * Message: You do not have authority to invite someone to the Command Channel.
	 */
	CANNOT_INVITE_TO_COMMAND_CHANNEL(1593),

	/**
	* ID: 1709<br>
	* Message: You are using $s1 point.
	*/
	USING_S1_PCPOINT(1709),
	
	/**
     * ID: 1714<br>
     * Message: Current Location: $s1, $s2, $s3 (Near the Town of Schuttgart)
     */
    LOC_SCHUTTGART_S1_S2_S3(1714),
	
	/**
	 * ID: 1730<br>
	 * Message: To establish a Clan Academy, your clan must be Level 5 or higher.
	 */
	YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN_ACADEMY(1730),
	
	/**
	 * ID: 1734<br>
	 * Message: To join a Clan Academy, characters must be Level 40 or below,
	 * not belong another clan and not yet completed their 2nd class transfer.
	 */
	ACADEMY_REQUIREMENTS(1734),
	
	/**
	 * ID: 1735<br>
	 * Message: $s1 does not meet the requirements to join a Clan Academy.
	 */
	S1_DOESNOT_MEET_REQUIREMENTS_TO_JOIN_ACADEMY(1735),
	
	/**
	 * ID: 1738<br>
	 * Message: Your clan has already established a Clan Academy.
	 */
	CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY(1738),
	
	/**
	 * ID: 1741<br>
	 * Message: Congratulations! The $s1's Clan Academy has been created.
	 */
	THE_S1S_CLAN_ACADEMY_HAS_BEEN_CREATED(1741),
	
	/**
	 * ID: 1748<br>
	 * Message: Clan Academy member $s1 has successfully completed the 2nd class
	 * transfer and obtained $s2 Clan Reputation points.
	 */
	CLAN_MEMBER_GRADUATED_FROM_ACADEMY(1748),

	/**
	 * ID: 1749<br>
	 * Message: Congratulations! You will now graduate from the Clan Academy and leave
	 * your current clan. As a graduate of the academy, you can immediately
	 * join a clan as a regular member without being subject to any penalties
	 */
	ACADEMY_MEMBERSHIP_TERMINATED(1749),
	
	 /**
     * ID: 1755<br>
     * Message: $s2 has been designated as the apprentice of clan member $s1.
     */
    S2_HAS_BEEN_DESIGNATED_AS_APPRENTICE_OF_CLAN_MEMBER_S1(1755),

    /**
     * ID: 1756<br>
     * Message: Your apprentice, $s1, has logged in.
     */
    YOUR_APPRENTICE_S1_HAS_LOGGED_IN(1756),

    /**
     * ID: 1757<br>
     * Message: Your apprentice, $s1, has logged out.
     */
    YOUR_APPRENTICE_S1_HAS_LOGGED_OUT(1757),

    /**
     * ID: 1758<br>
     * Message: Your sponsor, $s1, has logged in.
     */
    YOUR_SPONSOR_S1_HAS_LOGGED_IN(1758),

    /**
     * ID: 1759<br>
     * Message: Your sponsor, $s1, has logged out.
     */
    YOUR_SPONSOR_S1_HAS_LOGGED_OUT(1759),

    /**
     * ID: 1762<br>
     * Message: You do not have the right to dismiss an apprentice.
     */
    YOU_DO_NOT_HAVE_THE_RIGHT_TO_DISMISS_AN_APPRENTICE(1762),

    /**
     * ID: 1763<br>
     * Message: $s2, clan member $s1's apprentice, has been removed.
     */
    S2_CLAN_MEMBER_S1_S_APPRENTICE_HAS_BEEN_REMOVED(1763),
	
	/**
	 * ID: 1771<br>
	 * Message: Now that your clan level is above Level 5, it can accumulate clan
	 * reputation points.
	 */
	CLAN_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS(1771),

	/**
	 * ID: 1772<br>
	 * Message: Since your clan was defeated in a siege, $s1 points have been
	 * deducted from your clan's reputation score and given to the opposing clan.
	 */
	CLAN_WAS_DEFEATED_IN_SIEGE_AND_LOST_S1_REPUTATION_POINTS(1772),

	/**
	 * ID: 1773<br>
	 * Message: Since your clan emerged victorious from the siege, $s1 points
	 * have been added to your clan's reputation score.
	 */
	CLAN_VICTORIOUS_IN_SIEGE_AND_GAINED_S1_REPUTATION_POINTS(1773),

	/**
	 * ID: 1774<br>
	 * Message: Your clan's newly acquired contested clan hall has added $s1
	 * points to your clan's reputation score.
	 */
	CLAN_ACQUIRED_CONTESTED_CLAN_HALL_AND_S1_REPUTATION_POINTS(1774),

	/**
	 * ID: 1775<br>
	 * Message: Clan member $s1 was an active member of the highest-ranked party
	 * in the Festival of Darkness. $s2 points have been added to your clan's reputation score.
	 */
	CLAN_MEMBER_S1_WAS_IN_HIGHEST_RANKED_PARTY_IN_FESTIVAL_OF_DARKNESS_AND_GAINED_S2_REPUTATION(1775),

	/**
	 * ID: 1776<br>
	 * Message: Clan member $s1 was named a hero. $2s points have been added to your
	 * clan's reputation score.
	 */
	CLAN_MEMBER_S1_BECAME_HERO_AND_GAINED_S2_REPUTATION_POINTS(1776),

	/**
	 * ID: 1777<br>
	 * Message: You have successfully completed a clan quest. $s1 points have been added to your
	 * clan's reputation score.
	 */
	CLAN_QUEST_COMPLETED_AND_S1_POINTS_GAINED(1777),

	/**
	 * ID: 1778<br>
	 * Message: An opposing clan has captured your clan's contested clan hall. $s1 points have
	 * been deducted from your clan's reputation score.
	 */
	OPPOSING_CLAN_CAPTURED_CLAN_HALL_AND_YOUR_CLAN_LOSES_S1_POINTS(1778),

	/**
	 * ID: 1779<br>
	 * Message: After losing the contested clan hall, 300 points have been deducted from your
	 * clan's reputation score.
	 */
	CLAN_LOST_CONTESTED_CLAN_HALL_AND_300_POINTS(1779),

	/**
	 * ID: 1780<br>
	 * Message: Your clan has captured your opponent's contested clan hall. $s1 points have
	 * been deducted from your opponent's clan reputation score.
	 */
	CLAN_CAPTURED_CONTESTED_CLAN_HALL_AND_S1_POINTS_DEDUCTED_FROM_OPPONENT(1780),

	/**
	 * ID: 1781<br>
	 * Message: Your clan has added $1s points to its clan reputation score.
	 */
	CLAN_ADDED_S1S_POINTS_TO_REPUTATION_SCORE(1781),

	/**
	 * ID: 1782<br>
	 * Message: Your clan member $s1 was killed. $s2 points have been deducted from
	 * your clan's reputation score and added to your opponent's clan reputation score.
	 */
	CLAN_MEMBER_S1_WAS_KILLED_AND_S2_POINTS_DEDUCTED_FROM_REPUTATION(1782),

	/**
	 * ID: 1783<br>
	 * Message: For killing an opposing clan member, $s1 points have been deducted
	 * from your opponents' clan reputation score.
	 */
	FOR_KILLING_OPPOSING_MEMBER_S1_POINTS_WERE_DEDUCTED_FROM_OPPONENTS(1783),

	/**
	 * ID: 1784<br>
	 * Message: Your clan has failed to defend the castle. $s1 points have been
	 * deducted from your clan's reputation score and added to your opponents'.
	 */
	YOUR_CLAN_FAILED_TO_DEFEND_CASTLE_AND_S1_POINTS_LOST_AND_ADDED_TO_OPPONENT(1784),

	/**
	 * ID: 1785<br>
	 * Message: The clan you belong to has been initialized. $s1 points have been
	 * deducted from your clan reputation score.
	 */
	YOUR_CLAN_HAS_BEEN_INITIALIZED_AND_S1_POINTS_LOST(1785),

	/**
	 * ID: 1786<br>
	 * Message: Your clan has failed to defend the castle. $s1 points have been
	 * deducted from your clan's reputation score.
	 */
	YOUR_CLAN_FAILED_TO_DEFEND_CASTLE_AND_S1_POINTS_LOST(1786),

	/**
     * ID: 1787<br>
     * Message: $s1 points have been deducted from the clan's reputation score.
     */
    S1_DEDUCTED_FROM_CLAN_REP(1787),

    /**
     * ID: 1788<br>
     * Message: The clan skill $s1 has been added.
     */
    CLAN_SKILL_S1_ADDED(1788),

	/**
	 * ID: 1789<br>
	 * Message: Since the Clan Reputation Score has dropped to 0 or lower, your
	 * clan skill(s) will be de-activated.
	 */
	REPUTATION_POINTS_0_OR_LOWER_CLAN_SKILLS_DEACTIVATED(1789),
	
	/**
	 * ID: 1790<br>
	 * Message: The conditions necessary to increase the clan's level have not been met.
	 */
	FAILED_TO_INCREASE_CLAN_LEVEL(1790),
	
	/**
	 * ID: 1791<br>
	 * Message: The conditions necessary to create a military unit have not been met.
	 */
	YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_MILITARY_UNIT(1791),
	
	/**
	 * ID: 1793<br>
	 * Message: The Royal Guard of $s1 have been created.
	 */
	S1_HAS_BEEN_SELECTED_AS_CAPTAIN_OF_S2(1793),
	
	/**
	 * ID: 1794<br>
	 * Message: The Knights of $s1 have been created.
	 */
	THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED(1794),

	/**
	 * ID: 1795<br>
	 * Message: The Royal Guard of $s1 have been created.
	 */
	THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED(1795),
	
	/**
	 * ID: 1798<br>
	 * Message: Clan lord privileges have been transferred to $s1.
	 */
	CLAN_LEADER_PRIVILEGES_HAVE_BEEN_TRANSFERRED_TO_S1(1798),
	
    /**
     * ID: 1813<br>
     * Message: $s1 has $s2 hour(s) of usage time remaining.
     */
    THERE_IS_S1_HOUR_AND_S2_MINUTE_LEFT_OF_THE_FIXED_USAGE_TIME(1813),

    /**
     * ID: 1814<br>
     * Message: $s1 has $s2 minute(s) of usage time remaining.
     */
    S2_MINUTE_OF_USAGE_TIME_ARE_LEFT_FOR_S1(1814),

    /**
     * ID: 1815<br>
     * Message: $s2 was dropped in the $s1 region.
     */
    S2_WAS_DROPPED_IN_THE_S1_REGION(1815),

    /**
     * ID: 1816<br>
     * Message: The owner of $s2 has appeared in the $s1 region.
     */
    THE_OWNER_OF_S2_HAS_APPEARED_IN_THE_S1_REGION(1816),

    /**
     * ID: 1817<br>
     * Message: $s2's owner has logged into the $s1 region.
     */
    S2_OWNER_HAS_LOGGED_INTO_THE_S1_REGION(1817),

    /**
     * ID: 1818<br>
     * Message: 	$s1 has disappeared.
     */
    S1_HAS_DISAPPEARED(1818),
	
	/**
	 * ID: 1835<br>
	 * Message: #$s1 is full and cannot accept additional clan members at this time.
	 */
	S1_CLAN_IS_FULL(1835),
	
	/**
     * ID: 1842<br>
     * Message: $s1 wishes to summon you from $s2. Do you accept?
     */
    S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT(1842),

    /**
     * ID: 1843<br>
     * Message: $s1 is engaged in combat and cannot be summoned.
     */
    S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED(1843),

	/**
     * ID: 1844<br>
     * Message: $s1 is dead at the moment and cannot be summoned.
     */
    S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED(1844),
	
	/**
	 * ID: 1850<br>
	 * Message: The Captain of the Order of Knights cannot be appointed.
	 */
	CAPTAIN_OF_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED(1850),
	
	/**
	 * ID: 1851<br>
	 * Message: The Captain of the Royal Guard cannot be appointed.
	 */
	CAPTAIN_OF_ROYAL_GUARD_CANNOT_BE_APPOINTED(1851),
	
	/**
     * ID: 1852<br>
     * Message: The attempt to acquire the skill has failed because of an insufficient Clan Reputation Score.
     */
    ACQUIRE_SKILL_FAILED_BAD_CLAN_REP_SCORE(1852),
	
	/**
	 * ID: 1855<br>
	 * Message: Another military unit is already using that name. Please enter a different name.
	 */
	ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME(1855),
		
	/**
	 * ID: 1860<br>
	 * Message: The Clan Reputation Score is too low.
	 */
	CLAN_REPUTATION_SCORE_IS_TOO_LOW(1860),
	
	/**
	 * ID: 1861<br>
	 * Message: The clan's crest has been deleted.
	 */
	CLAN_CREST_HAS_BEEN_DELETED(1861),
	
	/**
	 * ID: 1862<br>
	 * Message: Clan skills will now be activated since the clan's reputation score is 0 or higher.
	 */
	CLAN_SKILLS_WILL_BE_ACTIVATED_SINCE_REPUTATION_IS_0_OR_HIGHER(1862),

    /**
     * ID: 1867<br>
     * Message: Your opponent's MP was reduced by $s1.
     */
    YOUR_OPPONENTS_MP_WAS_REDUCED_BY_S1(1867),
    
    /**
	 * ID: 1895<br>
	 * Message: $s1 is in an area which blocks summoning.
	 */
	S1_IN_SUMMON_BLOCKING_AREA(1895),
	/**
	 * ID: 1896<br>
	 * Message: $s1 has already been summoned.
	 */
	S1_ALREADY_SUMMONED(1896),
	/**
	 * ID: 1897<br>
	 * Message: $s1 is required for summoning.
	 */
	S1_REQUIRED_FOR_SUMMONING(1897),

    /**
     * ID: 1898<br>
     * Message:  $s1 is currently trading or operating a private store and cannot be summoned.
     */
    S1_CURRENTLY_TRADING_OR_OPERATING_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED(1898),
    
    /**
     * ID: 1899<br>
     * Message: Your target is in an area which blocks summoning.
     */
    YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING(1899),
    
    /**
     * ID: 1902<br>
     * Message: Incompatible item grade. This item cannot be used.
     */
    INCOMPATIBLE_ITEM_GRADE(1902),
    
    /**
     * ID: 1911<br>
     * Message: You cannot summon players who are currently participating in the Grand Olympiad.
     */
    YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_IN_OLYMPIAD(1911),
    
    /**
     * ID: 1916<br>
     * Message: Your Death Penalty is now level $s1.
     */
    DEATH_PENALTY_LEVEL_S1_ADDED(1916),

    /**
     * ID: 1917<br>
     * Message: Your Death Penalty has been lifted.
     */
    DEATH_PENALTY_LIFTED(1917),

    /**
     * ID: 1924<br>
     * Message: Current Location: $s1, $s2, $s3 (near the Primeval Isle)
     */
    LOC_PRIMEVAL_ISLE_S1_S2_S3(1924),
    
    /**
     * ID: 1926<br>
     * Message: There is no opponent to receive your challenge for a duel.
     */
    THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL(1926),

    /**
     * ID: 1927<br>
     * Message: $s1 has been challenged to a duel.
     */
    S1_HAS_BEEN_CHALLENGED_TO_A_DUEL(1927),

    /**
     * ID: 1928<br>
     * Message: $s1's party has been challenged to a duel.
     */
    S1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL(1928),

    /**
     * ID: 1929<br>
     * Message: $s1 has accepted your challenge to a duel. The duel will begin in a few moments.
     */
    S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS(1929),

    /**
     * ID: 1930<br>
     * Message: You have accepted $s1's challenge to a duel. The duel will begin in a few moments.
     */
    YOU_HAVE_ACCEPTED_S1S_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS(1930),

    /**
     * ID: 1931<br>
     * Message: $s1 has declined your challenge to a duel.
     */
    S1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL(1931),

    /**
     * ID: 1933<br>
     * Message: You have accepted $s1's challenge to a party duel. The duel will begin in a few moments.
     */
    YOU_HAVE_ACCEPTED_S1S_CHALLENGE_TO_A_PARTY_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS(1933),

    /**
     * ID: 1934<br>
     * Message: $s1 has accepted your challenge to duel against their party. The duel will begin in a few moments.
     */
    S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS(1934),

    /**
     * ID: 1936<br>
     * Message: The opposing party has declined your challenge to a duel.
     */
    THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL(1936),

    /**
     * ID: 1937<br>
     * Message: Since the person you challenged is not currently in a party, they cannot duel against your party.
     */
    SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY(1937),

    /**
     * ID: 1938<br>
     * Message: $s1 has challenged you to a duel.
     */
    S1_HAS_CHALLENGED_YOU_TO_A_DUEL(1938),

    /**
     * ID: 1939<br>
     * Message: $s1's party has challenged your party to a duel.
     */
    S1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL(1939),

    /**
     * ID: 1940<br>
     * Message: You are unable to request a duel at this time.
     */
    YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME(1940),

    /**
     * ID: 1942<br>
     * Message: The opposing party is currently unable to accept a challenge to a duel.
     */
    THE_OPPOSING_PARTY_IS_CURRENTLY_UNABLE_TO_ACCEPT_A_CHALLENGE_TO_A_DUEL(1942),

    /**
     * ID: 1944<br>
     * Message: In a moment, you will be transported to the site where the duel will take place.
     */
    IN_A_MOMENT_YOU_WILL_BE_TRANSPORTED_TO_THE_SITE_WHERE_THE_DUEL_WILL_TAKE_PLACE(1944),
    THE_DUEL_WILL_BEGIN_IN_S1_SECONDS(1945),

    /**
     * ID: 1949<br>
     * Message: Let the duel begin!
     */
    LET_THE_DUEL_BEGIN(1949),

    /**
     * ID: 1950<br>
     * Message: $s1 has won the duel.
     */
    S1_HAS_WON_THE_DUEL(1950),

    /**
     * ID: 1951<br>
     * Message: $s1's party has won the duel.
     */
    S1S_PARTY_HAS_WON_THE_DUEL(1951),

    /**
     * ID: 1952<br>
     * Message: The duel has ended in a tie.
     */
    THE_DUEL_HAS_ENDED_IN_A_TIE(1952),

    /**
     * ID: 1955<br>
     * Message: Since $s1 withdrew from the duel, $s2 has won.
     */
    SINCE_S1_WITHDREW_FROM_THE_DUEL_S2_HAS_WON(1955),

    /**
     * ID: 1956<br>
     * Message: Since $s1's party withdrew from the duel, $s2's party has won.
     */
    SINCE_S1S_PARTY_WITHDREW_FROM_THE_DUEL_S1S_PARTY_HAS_WON(1956),
    
    /**
     * ID: 1957<br>
     * Message: Select the item to be augmented.
     */
    SELECT_THE_ITEM_TO_BE_AUGMENTED(1957),

    /**
     * ID: 1958<br>
     * Message: Select the catalyst for augmentation.
     */
    SELECT_THE_CATALYST_FOR_AUGMENTATION(1958),

    /**
     * ID: 1959<br>
     * Message: Requires $s1 $s2.
     */
    REQUIRES_S1_S2(1959),

    /**
     * ID: 1960<br>
     * Message: This is not a suitable item.
     */
    THIS_IS_NOT_A_SUITABLE_ITEM(1960),

    /**
     * ID: 1961<br>
     * Message: Gemstone quantity is incorrect.
     */
    GEMSTONE_QUANTITY_IS_INCORRECT(1961),

    /**
     * ID: 1962<br>
     * Message: The item was successfully augmented!
     */
    THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED(1962),

    /**
     * ID : 1963<br>
     * Message: Select the item from which you wish to remove augmentation.
     */
    SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION(1963),

    /**
     * ID: 1964<br>
     * Message: Augmentation removal can only be done on an augmented item.
     */
    AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM(1964),

    /**
     * ID: 1965<br>
     * Message: Augmentation has been successfully removed from your $s1.
     */
    AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1(1965),
    
    /**
     * ID: 1970<br>
     * Message: Once an item is augmented, it cannot be augmented again.
     */
    ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN(1970),

    /**
     * ID: 1972<br>
     * Message: You cannot augment items while a private store or private workshop is in operation.
     */
    YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION(1972),

    /**
     * ID: 1974<br>
     * Message: You cannot augment items while dead.
     */
    YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD(1974),

    /**
     * ID: 1976<br>
     * Message: You cannot augment items while paralyzed.
     */
    YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED(1976),

    /**
     * ID: 1977<br>
     * Message: You cannot augment items while fishing.
     */
    YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING(1977),

    /**
     * ID: 1978<br>
     * Message: You cannot augment items while sitting down.
     */
    YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN(1978),
    
    /**
     * ID: 1979<br>
     * Message: $s1's remaining Mana is now 10.
     */
    S1S_REMAINING_MANA_IS_NOW_10(1979),

    /**
     * ID: 1980<br>
     * Message: $s1's remaining Mana is now 5.
     */
    S1S_REMAINING_MANA_IS_NOW_5(1980),

    /**
     * ID: 1981<br>
     * Message: $s1's remaining Mana is now 1. It will disappear soon.
     */
    S1S_REMAINING_MANA_IS_NOW_1(1981),

    /**
     * ID: 1982<br>
     * Message: $s1's remaining Mana is now 0, and the item has disappeared.
     */
    S1S_REMAINING_MANA_IS_NOW_0(1982),
    
    /**
     * ID: 1984<br>
     * Message: Press the Augment button to begin.
     */
    PRESS_THE_AUGMENT_BUTTON_TO_BEGIN(1984),

    /**
     * ID: 2001<br>
     * Message: Augmentation failed due to inappropriate conditions.
     */
    AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS(2001),
    
    /**
     * ID: 2011<br>
     * Message: The augmented item cannot be discarded.
     */
    AUGMENTED_ITEM_CANNOT_BE_DISCARDED(2011),
    
    /**
     * ID: 2013<br>
     * Message: Your seed or remaining purchase amount is inadequate.
     */
    YOUR_SEED_OR_REMAINING_PURCHASE_AMOUNT_IS_INADEQUATE(2013),

    /**
     * ID: 2017<br>
     * Message: $s1 cannot duel because $s1 is currently engaged in a private store or manufacture.
     */
    S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE(2017),

    /**
     * ID: 2018<br>
     * Message: $s1 cannot duel because $s1 is currently fishing.
     */
    S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_FISHING(2018),

    /**
     * ID: 2019<br>
     * Message: $s1 cannot duel because $s1's HP or MP is below 50 percent.
     */
    S1_CANNOT_DUEL_BECAUSE_S1S_HP_OR_MP_IS_BELOW_50_PERCENT(2019),

    /**
     * ID: 2020<br>
     * Message: $s1 cannot make a challenge to a duel because $s1 is currently
     * in a duel-prohibited area (Peaceful Zone / Seven Signs Zone / Near Water / Restart Prohibited Area).
     */
    S1_CANNOT_MAKE_A_CHALLANGE_TO_A_DUEL_BECAUSE_S1_IS_CURRENTLY_IN_A_DUEL_PROHIBITED_AREA(2020),

    /**
     * ID: 2021<br>
     * Message: $s1 cannot duel because $s1 is currently engaged in battle.
     */
    S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_ENGAGED_IN_BATTLE(2021),

    /**
     * ID: 2022<br>
     * Message: $s1 cannot duel because $s1 is already engaged in a duel.
     */
    S1_CANNOT_DUEL_BECAUSE_S1_IS_ALREADY_ENGAGED_IN_A_DUEL(2022),

    /**
     * ID: 2023<br>
     * Message: $s1 cannot duel because $s1 is in a chaotic state.
     */
    S1_CANNOT_DUEL_BECAUSE_S1_IS_IN_A_CHAOTIC_STATE(2023),

    /**
     * ID: 2024<br>
     * Message: $s1 cannot duel because $s1 is participating in the Olympiad.
     */
    S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_THE_OLYMPIAD(2024),

    /**
     * ID: 2025<br>
     * Message: $s1 cannot duel because $s1 is participating in a clan hall war.
     */
    S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_A_CLAN_HALL_WAR(2025),

    /**
     * ID: 2026<br>
     * Message: $s1 cannot duel because $s1 is participating in a siege war.
     */
    S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_A_SIEGE_WAR(2026),

    /**
     * ID: 2027<br>
     * Message: $s1 cannot duel because $s1 is currently riding a boat, wyvern, or strider.
     */
    S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_RIDING_A_BOAT_WYVERN_OR_STRIDER(2027),

    /**
     * ID: 2028<br>
     * Message: $s1 cannot receive a duel challenge because $s1 is too far away.
     */
    S1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_S1_IS_TOO_FAR_AWAY(2028),
    
    /**
     * ID: 2029<br>
     * Message: $s1 don't do it again.
     */
    DONT_DO_IT_AGAIN(2029),

    /**
     * ID: 2030<br>
     * Message: the clan reputation score is too low!.
     */
    THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW(2030),

	/**
	* ID: 2031<br>
	* Message: Court Magician: The portal has been created!
	*/
	COURT_MAGICIAN_CREATED_PORTAL(2031),

	/**
	* ID: 2032<br>
	* Message: TIME LIMETED ITEM DELETED!
	*/
	TIME_LIMITED_ITEM_DELETED(2032),

	 /**
     * ID: 3001<br>
     * Message: DOUBLE PONTS YOU GOT $51 GLASS PS!.
     */
	DOUBLE_POINTS_YOU_GOT_$51_GLASSES_PC(3001),
    
    	 /**
     * ID: 3002<br>
     * Message: YOU RECEVID $51 GLASSES PC!.
     */
	YOU_RECEVIED_$51_GLASSES_PC(3002),

	/**
	 * ID: 2030<br>
	 * Message: $s1.
	 */
	S1(3003);
	
    private int _id;

    private SystemMessageId(int id)
    {
    	_id = id;
    }

    public int getId()
    {
    	return _id;
    }
}