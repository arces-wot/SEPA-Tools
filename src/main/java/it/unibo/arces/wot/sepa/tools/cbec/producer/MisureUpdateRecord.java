package it.unibo.arces.wot.sepa.tools.cbec.producer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.JSAP;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class MisureUpdateRecord extends Producer {
	String observation;
	
	public MisureUpdateRecord(JsonObject fields)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException {
		super(new JSAP("swamp.jsap"), "UPDATE_OBSERVATION_VALUE", null);
		
		if (!fields.has("TAG")) throw new IllegalArgumentException("TAG is missing");
		if (!fields.has("VALORE")) throw new IllegalArgumentException("VALORE is missing");
		if (!fields.has("LASTREAD")) throw new IllegalArgumentException("LASTREAD is missing");
		
		observation = appProfile.getExtendedData().get("observationBaseURI").getAsString()
					+ fields.get("TAG").getAsString().trim();
		
		String value = fields.get("VALORE").getAsString().trim();
		
		String timestamp = "1974-13-10T00:00:00Z";
		SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		Date parsed = null;
		try {
			parsed = format.parse(fields.get("LASTREAD").getAsString());
		} catch (ParseException e) {
			throw new IllegalArgumentException("LASTREAD parsing exception "+e.getMessage());
		}
		timestamp = format.format(parsed);
		
		setUpdateBindingValue("observation", new RDFTermURI(observation));
		setUpdateBindingValue("value", new RDFTermLiteral(value));
		setUpdateBindingValue("timestamp", new RDFTermLiteral(timestamp));
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }	        

        if (!MisureUpdateRecord.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        
        MisureUpdateRecord cmp = (MisureUpdateRecord) obj;
        
        return cmp.hashCode() == hashCode();
	}
	
	@Override
    public int hashCode() {
		return observation.hashCode();
	}
}
