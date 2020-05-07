package it.unibo.arces.wot.sepa.tools.cbec.producer;

import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.JSAP;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class FieldRecord extends Producer {
	protected static final Logger logger = LogManager.getLogger();
	
	JsonObject geometry;
	String code;
	String cropCode;
	String canalCode;
	String farmerCode;
	
	public FieldRecord(JsonObject jsonObject) throws IllegalArgumentException, ParseException, SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException{
		super(new JSAP("swamp.jsap"), "FIELD", null);
		
		if (!jsonObject.has("CODICEAPP")) throw new IllegalArgumentException("CODICEAPP is missing");
		if (!jsonObject.has("COLTURA_COD")) throw new IllegalArgumentException("COLTURA_COD is missing");
		if (!jsonObject.has("CANALECOD")) throw new IllegalArgumentException("CANALECOD is missing");
		if (!jsonObject.has("CODICEUTENTE")) throw new IllegalArgumentException("CODICEUTENTE is missing");
		
		if (jsonObject.has("geometry")) {
			geometry = new JsonParser().parse(jsonObject.get("geometry").getAsString()).getAsJsonObject(); 	
		}
		
		code = jsonObject.get("CODICEAPP").getAsString().trim();
		cropCode = jsonObject.get("COLTURA_COD").getAsString().trim();
		canalCode = jsonObject.get("CANALECOD").getAsString().trim();
		farmerCode = jsonObject.get("CODICEUTENTE").getAsString().trim();
		
		setUpdateBindingValue("fieldUri", new RDFTermURI("http://swamp-project.org/cbec/field_"+code));
		setUpdateBindingValue("geometry", new RDFTermLiteral(geometry.toString()));
		setUpdateBindingValue("canalUri", new RDFTermURI("http://swamp-project.org/cbec/canal_"+canalCode));
		setUpdateBindingValue("cropUri", new RDFTermURI("http://swamp-project.org/cbec/crop_"+cropCode));
		setUpdateBindingValue("farmerUri", new RDFTermURI("http://swamp-project.org/cbec/farmer_"+farmerCode));
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }	        

        if (!FieldRecord.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        
        FieldRecord cmp = (FieldRecord) obj;
        
        return cmp.hashCode() == this.hashCode();
	}
	
	@Override
    public int hashCode() {
		return code.hashCode();
	}
}
