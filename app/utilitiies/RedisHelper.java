package utilities;

public class RedisHelper {

	public static String buildSetMembersKey(String obj, String id) {
		return obj + ":" + id + ":members";
	}
}
