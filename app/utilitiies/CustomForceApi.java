package utilities;

import play.Logger;

import com.force.api.*;
import com.force.api.http.Http;
import com.force.api.http.HttpRequest;
import com.force.api.http.HttpResponse;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * A class to call custom Apex Rest services.
 * Borrowed Heavily from force-rest-api
 * 
 * @author mcilroyc
 *
 */
public class CustomForceApi {

	private static final ObjectMapper jsonMapper;

	static {
		jsonMapper = new ObjectMapper();
		jsonMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
	}

	final ApiConfig config;
	ApiSession session;
	private boolean autoRenew = false;

	public CustomForceApi(ApiConfig config, ApiSession session) {
		this.config = config;
		this.session = session;
		if(session.getRefreshToken()!=null) {
			autoRenew = true;
		}
	}

	public CustomForceApi(ApiSession session) {
		this(new ApiConfig(), session);
	}

	public CustomForceApi(ApiConfig apiConfig) {
		config = apiConfig;
		session = Auth.authenticate(apiConfig);
		autoRenew  = true;

	}
	
	/*
	 * This is the meat of the custom API
	*/
	public JsonNode getAsJsonNode (String servicePath) {
		HttpRequest httpRequest = new HttpRequest()
				.url(uriBase() + servicePath)
				.method("GET")
				.header("Accept", "application/json");
		HttpResponse response = apiRequest(httpRequest);
		try{
			return jsonMapper.readValue(response.getStream(), JsonNode.class);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final String uriBase() {
		return(session.getApiEndpoint()+"/services/apexrest/");
	}
	
	private final HttpResponse apiRequest(HttpRequest req) {
		req.setAuthorization("OAuth "+session.getAccessToken());
		HttpResponse res = Http.send(req);
		if(res.getResponseCode()==401) {
			// Perform one attempt to auto renew session if possible
			if(autoRenew) {
				System.out.println("Session expired. Refreshing session...");
				if(session.getRefreshToken()!=null) {
					session = Auth.refreshOauthTokenFlow(config, session.getRefreshToken());
				} else {
					session = Auth.authenticate(config);
				}
				req.setAuthorization("OAuth "+session.getAccessToken());
				res = Http.send(req);
			}
		}
		if(res.getResponseCode()>299) {
			if(res.getResponseCode()==401) {
				throw new ApiTokenException(res.getString());
			} else {
				throw new ApiException(res.getResponseCode(), res.getString());
			}
		} else if(req.getExpectedCode()!=-1 && res.getResponseCode()!=req.getExpectedCode()) {
			throw new RuntimeException("Unexpected response from Force API. Got response code "+res.getResponseCode()+
					". Was expecing "+req.getExpectedCode());
		} else {
			return res;
		}
	}
	
}
