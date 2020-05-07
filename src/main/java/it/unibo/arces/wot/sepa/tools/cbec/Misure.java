package it.unibo.arces.wot.sepa.tools.cbec;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.pattern.Producer;
import it.unibo.arces.wot.sepa.tools.cbec.producer.MisureRecord;
import it.unibo.arces.wot.sepa.tools.cbec.producer.MisureUpdateRecord;

public class Misure extends CBECAdapter {

	private boolean init;
	
	public Misure(boolean init) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super("https://dati.emiliacentrale.it/rest/misure/", "swamp", "swamppass");
		
		this.init = init;
	}

	@Override
	protected void parseAndUpdate(JsonObject jsonObject) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException {
		if (jsonObject.get("success").getAsString().equals("true")) {
			JsonObject measures = jsonObject.getAsJsonObject("temporeale");

			logger.info("Number of measures: " + measures.entrySet().size());

			Set<Producer> producers = new HashSet<>();
			
			for (Entry<String, JsonElement> measure : measures.entrySet()) {
				JsonObject fields = measure.getValue().getAsJsonObject();
				
				if (init) producers.add(new MisureRecord(fields));
				else producers.add(new MisureUpdateRecord(fields));
			}
			
			logger.info("Number of UNIQUE measures: " + producers.size());
			
			for (Producer producer : producers) producer.update();
			
			
		} else
			logger.error("Success: " + jsonObject.get("success").getAsString());
	}

}
