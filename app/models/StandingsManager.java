package models;

//import java.util.HashSet;
//import java.util.Set;
//import java.util.List;
//import redis.clients.jedis.*;
//import com.force.api.*;
import play.*;
import utilities.*;
import org.codehaus.jackson.JsonNode;

public class StandingsManager {

	static final String STANDINGS_KEY = "standings";

	/* 
	 * Fetch from cache, or from SFDC 
	 */
	public static JsonNode getAllStandingsJSON() {
		JsonNode standingsJson = null;
		standingsJson = (JsonNode)play.cache.Cache.get(STANDINGS_KEY);
		if (standingsJson != null){
			Logger.debug("standings pulled from cache");
			return standingsJson;
		}
		else {
			//TODO use getOrElse???
			standingsJson = fetchStandingsFromSFDC();
			if (standingsJson != null){
				Logger.debug("standings fetched, adding to cache");
				play.cache.Cache.set(STANDINGS_KEY,standingsJson,120);
				return standingsJson;
			}
			else {
				return null;
			}
		}
	}

	/* 
	 * get json directly from SDFC via a custom rest api call
	 */
	private static JsonNode fetchStandingsFromSFDC(){
		CustomForceApi api = AuthHelper.getPublicCustomApi();
		return api.getAsJsonNode("standings/current");
	}

	/*
	   private final String PLAYER_QUERY_ROOT = "select id, name, Nights_Available__c, Positions__c from Player__c";
	   private final String ALL_PLAYERS_CACHE_KEY = "all_players";

	   private static List<Players> getAllPlayersFromSFDC() {
	//get the authentication from the session
	ForceApi api = AuthHelper.getPrivateForceApi();
	QueryResult<Player> results = 	
	api.query(PLAYER_QUERY_ROOT + " where Willing_to_Substitute__c = true", Player.class);
	if (results != null) {
	return results.getRecords();
	}
	else {
	return null;
	}
	}
	public static List<Player> getAllPlayers() {
	List<Players> allPlayers = (List<Players>)play.cache.Cache.get(ALL_PLAYERS_CACHE_KEY);
	if (allPlayers == null) {
	getAllPlayersFromSFDC();	
	}	
	//String response = "welcome to the adiabats manager with this many Players: " + results.getTotalSize();
	//response += "\n first name" + results.getRecords().get(0).getName();
	return results.getRecords();	
	}

	public static List<Player> findPlayers(String[] positions, String[] nightsAvailable) {
	QueryResult<Player> results = 	
	api.query(PLAYER_QUERY_ROOT + " where Willing_to_Substitute__c = true", Player.class);
	//String response = "welcome to the adiabats manager with this many Players: " + results.getTotalSize();
	//response += "\n first name" + results.getRecords().get(0).getName();
	return results.getRecords();	
	}

	public static boolean indexPlayers(List<Player) players) {
	Jedis jedis = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
	//jedis.multi();
	Set<String> volatileKeys = jedis.smembers("volatileKeys");
	if (volatileKeys.size() > 0){
	jedis.del(volatileKeys.toArray(new String[0]));
	}
	for (Player player : players) {
	String playerId = player.getId();
	jedis.set("player:" + playerId + ":name", player.getName());
	for (String position : 	player.getPositionsAsSet()) {
	String positionKey = "position:" + position + ":members";
	jedis.sadd(positionKey, playerId);
	jedis.sadd("volatileKeys", positionKey);			
	}
	for (String nightAvailable : 	player.getNightsAvailableAsSet()) {
	String nightAvailableKey = "nightAvailable:" + nightAvailable + ":members";
	jedis.sadd(nightAvailableKey, playerId);
	jedis.sadd("volatileKeys", nightAvailableKey);			
	}
	}
	//jedis.exec();
	}

	public static String[] getPitchersOnWednesday() {
	Jedis jedis = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
	Set<String> players = jedis.sinter("position:Pitcher:members", "nightAvailable:Wednesday:members");
	HashSet<String> playerNames = new HashSet<String>();
	for (String playerId : players) {
	playerNames.add(jedis.get("player:"+playerId+":name"));	
	}		
	return playerNames.toArray(new String[0]);
	}


	 */
}
