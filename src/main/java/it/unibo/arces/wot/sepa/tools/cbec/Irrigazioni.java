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
import it.unibo.arces.wot.sepa.commons.response.Response;
import it.unibo.arces.wot.sepa.pattern.Producer;
import it.unibo.arces.wot.sepa.tools.cbec.producer.Cache;
import it.unibo.arces.wot.sepa.tools.cbec.producer.CanalRecord;
import it.unibo.arces.wot.sepa.tools.cbec.producer.CropRecord;
import it.unibo.arces.wot.sepa.tools.cbec.producer.FarmerRecord;
import it.unibo.arces.wot.sepa.tools.cbec.producer.FieldRecord;
import it.unibo.arces.wot.sepa.tools.cbec.producer.IrrigationRequestRecord;

public class Irrigazioni extends CBECAdapter {
	protected static final Logger logger = LogManager.getLogger();

	boolean irrigation;
	boolean farmer;
	boolean field;
	boolean crop;
	boolean canal;

	private Cache cache;

	public Irrigazioni(boolean irrigation, boolean farmer, boolean field, boolean crop, boolean canal)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super("https://dati.emiliacentrale.it/rest/irrigazioni/", "swamp", "swamppass");

		this.irrigation = irrigation;
		this.farmer = farmer;
		this.field = field;
		this.crop = crop;
		this.canal = canal;

		cache = new Cache();
	}

	@Override
	protected void parseAndUpdate(JsonObject json)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException {
		JsonObject irrigazioni = json.getAsJsonObject("irrigazioni");

		int n = irrigazioni.entrySet().size();
		logger.info("*** Numero totale di richieste: " + n + " ***");

		Set<Producer> requests = new HashSet<>();

		for (Map.Entry<String, JsonElement> id : irrigazioni.entrySet()) {
			try {
				JsonObject obj = id.getValue().getAsJsonObject();
				if (irrigation) {
					IrrigationRequestRecord record = new IrrigationRequestRecord(obj);
					if (!cache.containsIrrigationRequest(record)) {
						requests.add(record);
						cache.addIrrigationRequest(record);
						logger.info(record);
					}
				}

				if (farmer) {
					requests.add(new FarmerRecord(obj));
				}

				if (field) {
					FieldRecord record = new FieldRecord(obj);
					if (!cache.containsField(record)) {
						requests.add(record);
						cache.addField(record);
						logger.info(record);
					}
				}

				if (crop) {
					CropRecord record = new CropRecord(obj);
					if (!cache.containsCrop(record)) {
						requests.add(record);
						cache.addCrop(record);
						logger.info(record);
					}
				}

				if (canal) {
					CanalRecord record = new CanalRecord(obj);
					if (!cache.containsCanal(record)) {
						requests.add(record);
						cache.addCanal(record);
						logger.info(record);
					}
				}
			} catch (IllegalArgumentException | ParseException e) {
				logger.error(e.getMessage());
			}
		}

		for (Producer req : requests) {
			try {
				Response ret = req.update();
				int nRetry = 5;
				while (ret.isError() && nRetry > 0) {
					logger.error("Update failed. Retry. " + nRetry + " " + ret);
					ret = req.update();
					nRetry = nRetry - 1;
				}
			} catch (SEPABindingsException | SEPASecurityException | SEPAProtocolException
					| SEPAPropertiesException e) {
				logger.error(e.getMessage());
			}
		}

		logger.info("Numero totale di richieste inserite: " + requests.size());
	}
}
