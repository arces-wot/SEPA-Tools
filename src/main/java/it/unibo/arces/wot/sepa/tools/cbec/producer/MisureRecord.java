package it.unibo.arces.wot.sepa.tools.cbec.producer;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.JSAP;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class MisureRecord extends Producer{
	String observation;
	
	public MisureRecord(JsonObject fields)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super(new JSAP("swamp.jsap"), "ADD_OBSERVATION", null);
		
		if (!fields.has("TAG")) throw new IllegalArgumentException("TAG is missing");
		
		String tag = fields.get("TAG").getAsString().trim();
		
		String comment = "Retrieved from: https://dati.emiliacentrale.it/rest/misure/";		
		String location = appProfile.getExtendedData().get("defaultLocationURI").getAsString();		
		observation = appProfile.getExtendedData().get("observationBaseURI").getAsString() + tag;
		
		String label = null;
		try {
			label = tag + " (" + fields.get("DESCRIZIONE").getAsString().trim() + ")";
		} catch (Exception e) {
			logger.error("label " + fields);
			label = tag;
		}
		
		String unit = "unit:null";
		try {
			String unimis = fields.get("UNIMIS").getAsString().trim();
			
			if (appProfile.getExtendedData().getAsJsonObject("units").has(unimis))
				unit = appProfile.getExtendedData().getAsJsonObject("units").get(unimis).getAsString();
			else {
				appProfile.getExtendedData().getAsJsonObject("units").add(unimis,
						new JsonPrimitive("unit:???"));
			}					
		} catch (Exception e) {
			logger.error("Unit is null " + tag);
		}

		try {
			setUpdateBindingValue("observation", new RDFTermURI(observation));
			setUpdateBindingValue("comment", new RDFTermLiteral(comment));
			setUpdateBindingValue("location", new RDFTermURI(location));
			setUpdateBindingValue("label", new RDFTermLiteral(label));
			setUpdateBindingValue("unit", new RDFTermURI(unit));
			update();
		} catch (SEPASecurityException | SEPAProtocolException | SEPAPropertiesException
				| SEPABindingsException e) {
			logger.error(e.getMessage());
		}
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }	        

        if (!MisureRecord.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        
        MisureRecord cmp = (MisureRecord) obj;
        
        return cmp.hashCode() == hashCode();
	}
	
	@Override
    public int hashCode() {
		return observation.hashCode();
	}

}
