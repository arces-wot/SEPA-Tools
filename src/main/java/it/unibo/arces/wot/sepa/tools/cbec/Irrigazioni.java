package it.unibo.arces.wot.sepa.tools.cbec;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.pattern.Producer;
import it.unibo.arces.wot.sepa.tools.cbec.producer.FarmerRecord;
import it.unibo.arces.wot.sepa.tools.cbec.producer.FieldRecord;
import it.unibo.arces.wot.sepa.tools.cbec.producer.IrrigationRequestRecord;

public class Irrigazioni extends CBECAdapter {
	protected static final Logger logger = LogManager.getLogger();
	
	boolean irrigation;
	boolean farmer; 
	boolean field;
	
	public Irrigazioni(Boolean init,boolean irrigation,boolean farmer,boolean field) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super("https://dati.emiliacentrale.it/rest/irrigazioni/", "swamp", "swamppass");
		
		this.irrigation = irrigation;
		this.farmer = farmer;
		this.field = field;
	} 

	@Override
	protected void parseAndUpdate(JsonObject json) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException {
		JsonObject irrigazioni = json.getAsJsonObject("irrigazioni");
		
		int n = irrigazioni.entrySet().size();
		logger.info("Numero totale di richieste: "+n);
		
		Set<Producer> requests = new HashSet<>();
		
		for (Map.Entry<String, JsonElement> id : irrigazioni.entrySet()) {
			try{
				if (irrigation) requests.add(new IrrigationRequestRecord(id.getValue().getAsJsonObject()));
				if (farmer) requests.add(new FarmerRecord(id.getValue().getAsJsonObject()));
				if (field) requests.add(new FieldRecord(id.getValue().getAsJsonObject()));
			}
			catch(IllegalArgumentException | ParseException e) {
				logger.error(e.getMessage());
			}
		}
		
		for (Producer req: requests) {
			try {
				req.update();
			} catch (SEPABindingsException | SEPASecurityException | SEPAProtocolException | SEPAPropertiesException  e) {
				logger.error(e.getMessage());
			}
		}
		
		logger.info("Numero totale di richieste inserite: "+requests.size());
	}
}
