package it.unibo.arces.wot.sepa.tools.cbec.producer;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.pattern.JSAP;

public class Cache {
	protected static final Logger logger = LogManager.getLogger();
	
	final JSAP ca;
	final JsonObject irrigation;
	final JsonObject crop;
	final JsonObject field;
	final JsonObject canal;
	
	final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public Cache() throws SEPAPropertiesException, SEPASecurityException {
		ca = new JSAP("cache.jsap");
		
		if (!ca.getExtendedData().has("irrigazioni")) ca.getExtendedData().add("irrigazioni", new JsonObject());
		if (!ca.getExtendedData().has("crop")) ca.getExtendedData().add("crop", new JsonObject());
		if (!ca.getExtendedData().has("field")) ca.getExtendedData().add("field", new JsonObject());
		if (!ca.getExtendedData().has("canal")) ca.getExtendedData().add("canal", new JsonObject());
		
//		irrigation = ca.getExtendedData().getAsJsonObject("irrigazioni");
//		irrigation = fixTimestamp();
//		ca.getExtendedData().add("irrigazioni", fixTimestamp());
//		ca.write();
		
		irrigation = ca.getExtendedData().getAsJsonObject("irrigazioni");
		crop = ca.getExtendedData().getAsJsonObject("crop");
		field = ca.getExtendedData().getAsJsonObject("field");
		canal = ca.getExtendedData().getAsJsonObject("canal");
	}
	
//	private JsonObject fixTimestamp() {
//		JsonObject temp = ca.getExtendedData().getAsJsonObject("irrigazioni");
//		JsonObject fix = new JsonObject();
//		
//		for(Entry<String, JsonElement> farmer : temp.entrySet()) {
//			JsonObject requests = farmer.getValue().getAsJsonObject();
//			JsonObject reqFix = new JsonObject();
//			
//			for(Entry<String, JsonElement> request : requests.entrySet()) {
//				JsonElement value = request.getValue();
//				reqFix.add(request.getKey().split("T")[0], value);
//			}
//			
//			fix.add(farmer.getKey(), reqFix);
//		}
//		return fix;
//	}
	
	public boolean containsField(FieldRecord rec) {
		if (!field.has(rec.getCode())) {
			logger.debug("Cache MISS: "+rec.getCode());
			return false;
		}
		
		logger.debug("Cache HIT: "+rec.getCode());
		return true;
	}
	
	public boolean addField(FieldRecord rec) throws SEPAPropertiesException {
		if (field.has(rec.getCode())) return false;
		
		field.add(rec.getCode(), new JsonPrimitive(rec.getCropCode()));
		ca.write();
		
		return true;		
	}
	
	public boolean containsCrop(CropRecord rec) {
		if (!crop.has(rec.getCode())) {
			logger.debug("Cache MISS: "+rec.getCode());
			return false;
		}
		
		logger.debug("Cache HIT: "+rec.getCode());
		return true;
	}
	
	public boolean addCrop(CropRecord rec) throws SEPAPropertiesException {
		if (crop.has(rec.getCode())) return false;
		
		crop.add(rec.getCode(), new JsonPrimitive(rec.getDescription()));
		ca.write();
		
		return true;		
	}
	
	public boolean containsIrrigationRequest(IrrigationRequestRecord rec) throws ParseException {		
		if (!irrigation.has(rec.getFarmerCode())) {
			logger.debug("Cache MISS: "+rec.getFarmerCode());
			return false;
		}
		
		String timestamp = timestampFormat.format(rec.getReservationDate());
		if (!irrigation.getAsJsonObject(rec.getFarmerCode()).has(timestamp)) {
			logger.debug("Cache MISS: "+rec.getFarmerCode()+" "+timestamp);
			return false;
		}
		
		logger.debug("Cache HIT: "+rec.getFarmerCode()+" "+timestamp);
		
		return true;
	}
	
	public boolean addIrrigationRequest(IrrigationRequestRecord rec) throws ParseException, SEPAPropertiesException {		
		String timestamp = timestampFormat.format(rec.getReservationDate());
		
		JsonObject obj = new JsonObject();
		obj.addProperty("reservationNumber", rec.getReservationNumber());
		obj.addProperty("requestNumber", rec.getRequestNumber());
				
		if (!irrigation.has(rec.getFarmerCode())) {	
			JsonObject date = new JsonObject();
			date.add(timestamp, obj);
			irrigation.add(rec.getFarmerCode(), date);
			ca.write();
			return true;
		}
		
		if (!irrigation.getAsJsonObject(rec.getFarmerCode()).has(timestamp)) {
			irrigation.getAsJsonObject(rec.getFarmerCode()).add(timestamp, obj);
			ca.write();
			return true;
		}
		
		return false;
	}

	public boolean containsCanal(CanalRecord rec) {
		if (!canal.has(rec.getCode())) {
			logger.debug("Cache MISS: "+rec.getCode());
			return false;
		}
		
		logger.debug("Cache HIT: "+rec.getCode());
		return true;
	}

	public boolean addCanal(CanalRecord rec) throws SEPAPropertiesException {
		if (canal.has(rec.getCode())) return false;
		
		canal.add(rec.getCode(), new JsonPrimitive(rec.getDescription()));
		ca.write();
		
		return true;	
		
	}
}
