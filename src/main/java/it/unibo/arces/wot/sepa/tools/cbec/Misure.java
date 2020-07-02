package it.unibo.arces.wot.sepa.tools.cbec;

import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.Response;
import it.unibo.arces.wot.sepa.pattern.JSAP;
import it.unibo.arces.wot.sepa.pattern.Producer;
import it.unibo.arces.wot.sepa.tools.cbec.producer.MisureRecord;
import it.unibo.arces.wot.sepa.tools.cbec.producer.MisureUpdateRecord;

public class Misure extends CBECAdapter {

	private boolean init;
	private boolean fixedtime;
	private Set<String> filter = null;
	
	public Misure(boolean init,boolean fixedtime) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super("https://dati.emiliacentrale.it/rest/misure/", "swamp", "swamppass");
		
		this.init = init;
		this.fixedtime = fixedtime;
		
		JSAP jsap = new JSAP("swamp.jsap");
		if (jsap.getExtendedData() != null) {
			if (jsap.getExtendedData().has("misure")) {
				if (jsap.getExtendedData().getAsJsonObject("misure").has("filter")) {
					JsonArray filterArray = jsap.getExtendedData().getAsJsonObject("misure").getAsJsonArray("filter");
					filter = new HashSet<>();
					for (JsonElement fElement : filterArray) filter.add(fElement.getAsString());
				}
			}
		}
	}

	@Override
	protected void parseAndUpdate(JsonObject jsonObject) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException {
		if (jsonObject.get("success").getAsString().equals("true")) {
			JsonObject measures = jsonObject.getAsJsonObject("temporeale");

			logger.info("Number of measures: " + measures.entrySet().size());

			Set<Producer> producers = new HashSet<>();
			
			Date now = new Date();
			
			for (Entry<String, JsonElement> measure : measures.entrySet()) {
				JsonObject fields = measure.getValue().getAsJsonObject();
				
				if (!fields.has("TAG")) continue;
				if (filter != null) {
					if (!filter.contains(fields.get("TAG").getAsString().trim())) continue;
				}
				
				if (init) producers.add(new MisureRecord(fields));
				else {
					if (!fixedtime) producers.add(new MisureUpdateRecord(fields));
					else producers.add(new MisureUpdateRecord(fields,now));
				}
			}
			
			logger.info("Number of UNIQUE measures: " + producers.size());
			
			int n = 1;
			for (Producer producer : producers) {
				logger.info("Update "+n+" of "+producers.size());
				Response ret = producer.update();
				int nRetry = 5;
				while(ret.isError() && nRetry > 0) {
					logger.error("Update FAILED. Retry: "+nRetry+" "+ret);
					ret = producer.update();
					nRetry = nRetry -1;
				}
				
				n++;
			}
			
			
		} else
			logger.error("Success: " + jsonObject.get("success").getAsString());
	}

}
