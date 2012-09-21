package models;

import play.*;
import utilities.*;
import org.codehaus.jackson.JsonNode;

public class StandingsManager {

	static final String STANDINGS_KEY = "standings";

	//Fetch from cache, or from SFDC 
	public static JsonNode getAllStandingsJSON() {
		JsonNode standingsJson = null;
		standingsJson = (JsonNode)play.cache.Cache.get(STANDINGS_KEY);
		if (standingsJson != null){
			Logger.debug("standings pulled from cache");
			return standingsJson;
		}
		else {
			//call out to SFDC to get the standings
			standingsJson = fetchStandingsFromSFDC();
			if (standingsJson != null){
				Logger.debug("standings fetched, adding to cache");
				play.cache.Cache.set(STANDINGS_KEY,standingsJson,30);
				return standingsJson;
			}
			else {
				return null;
			}
		}
	}

	//get json directly from SDFC via a custom rest api call
	private static JsonNode fetchStandingsFromSFDC(){
		CustomForceApi api = AuthHelper.getPublicCustomApi();
		return api.getAsJsonNode("standings/current");
	}

}
