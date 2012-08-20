package models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Player {

	@JsonProperty(value="Id")
		String id;
	@JsonProperty(value="Name")
		String name;
	@JsonProperty(value="Nights_Available__c")
		String nightsAvailable;
	@JsonProperty(value="Positions__c")
		String positions;

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getNightsAvailable() { return nightsAvailable; }
	public void setNightsAvailable(String nightsAvailable) { this.nightsAvailable = nightsAvailable; }
	public String getPositions() { return positions; }
	public void setPositions(String positions) { this.positions = positions; }


	public Set<String> getPositionsAsSet() {
		Set<String> s = new HashSet<String>();
		if (positions != null){
			Collections.addAll(s, positions.split(";"));
		}
		return s;
	}

	public Set<String> getNightsAvailableAsSet() {
		Set<String> s = new HashSet<String>();
		if (nightsAvailable != null){
			Collections.addAll(s, nightsAvailable.split(";"));
		}
		return s;
	}
}
