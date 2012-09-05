package utilities;

import java.net.URI;
import java.net.URISyntaxException;
import redis.clients.jedis.*;

public class RedisHelper {

	private static JedisPool pool;
	static {
		try{
		URI redisURI = new URI(System.getenv("REDISTOGO_URL"));
		pool = new JedisPool(new JedisPoolConfig(),
				redisURI.getHost(),
				redisURI.getPort(),
				Protocol.DEFAULT_TIMEOUT,
				redisURI.getUserInfo().split(":",2)[1]);
		} catch (URISyntaxException e) {
			pool =  null;
		}
	}

	public static JedisPool getPool() {
		return RedisHelper.pool;
	}

	public static String buildSetMembersKey(String setType, String setKey){
		return setType + ":" + setKey + ":members";
	}

	public static String buildAllIndexKey(String setType){
		return setType + ":all";
	}

	//helper for buidling object:id:attribute style Redis "get" calls
	public static String buildObjectKey(String objectType, String objectId, String attributeName){
		return objectType + ":" + objectId + ":" + attributeName;
	}

	public static void addItemToSet(Jedis jedis, String setType, String setKey, String itemId) {
		addItemToSet(jedis, setType, setKey, itemId, null);
	}

	public static void addItemToSet(Jedis jedis, String setType, String setKey, String itemId, String indexSetName) {
		jedis.sadd(buildSetMembersKey(setType, setKey), itemId);
		if (indexSetName != null) {
			jedis.sadd(buildAllIndexKey(indexSetName), setKey);
		}	
	}
}
