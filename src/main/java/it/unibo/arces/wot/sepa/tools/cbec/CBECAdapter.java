package it.unibo.arces.wot.sepa.tools.cbec;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.security.Credentials;
import it.unibo.arces.wot.sepa.commons.security.SSLManager;

public abstract class CBECAdapter {
	protected static final Logger logger = LogManager.getLogger();
	
	/** The http client. */
	protected final CloseableHttpClient httpsClient;
	
	protected final String url;
	protected final String user;
	protected final String pass;
	
	public CBECAdapter(String url,String user, String pass)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		httpsClient = new SSLManager().getSSLHttpClientTrustAllCa("TLS");
		
		this.url = url;
		this.pass = pass;
		this.user = user;
	}
	
	protected abstract void parseAndUpdate(JsonObject json) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException;
	
	public final boolean refresh() throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException {
		JsonObject jsonObject = get(url, user, pass);
		
		if (jsonObject != null) parseAndUpdate(jsonObject);
		
		return jsonObject != null;
	}
	
	protected JsonObject get(String url,String user,String pass) {
		Credentials cred = new Credentials(user, pass);
		
		HttpGet get;
		get = new HttpGet(url);
		try {
			get.setHeader("Authorization", cred.getBasicAuthorizationHeader());
		} catch (SEPASecurityException e) {
			logger.error(e.getMessage());
			return null;
		}
		
		CloseableHttpResponse ret = null;
		HttpEntity responseEntity;
		try {
			ret = httpsClient.execute(get);
			responseEntity = ret.getEntity();
			String responseBody = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
			JsonObject json = new JsonParser().parse(responseBody).getAsJsonObject();
			return json;
		} catch (IOException e) {
			logger.error(e.getMessage());
			return null;
		} finally {
			try {
				if (ret != null)
					ret.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				return null;
			}

			responseEntity = null;
		}
	}

}
