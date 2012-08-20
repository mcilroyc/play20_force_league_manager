package models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import com.typesafe.plugin.RedisPlugin;
import redis.clients.jedis.*;

public class PlayerSearcher {

	List<Player> players;

	public PlayerSearcher(List<Player> players) {
		this.players = players;
		indexPlayers();
	}

	private void indexPlayers() {
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

}
