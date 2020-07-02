package it.unibo.arces.wot.sepa.tools.cbec.producer;

import java.text.ParseException;

import com.google.gson.JsonObject;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.JSAP;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class CropRecord extends Producer {	
	String COLTURA_COD;
	String COLTURA_DES;

	
	public CropRecord(JsonObject jsonObject) throws IllegalArgumentException, ParseException, SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException{
		super(new JSAP("swamp.jsap"), "CROP", null);
		
		if (!jsonObject.has("COLTURA_COD")) throw new IllegalArgumentException("COLTURA_COD is missing");
		if (!jsonObject.has("COLTURA_DES")) throw new IllegalArgumentException("COLTURA_DES is missing");
				
		COLTURA_COD = jsonObject.get("COLTURA_COD").getAsString().trim();
		COLTURA_DES = jsonObject.get("COLTURA_DES").getAsString().trim();
				
		setUpdateBindingValue("crop", new RDFTermURI("http://swamp-project.org/cbec/crop_"+COLTURA_COD));
		setUpdateBindingValue("code", new RDFTermLiteral(COLTURA_COD));
		setUpdateBindingValue("label", new RDFTermLiteral(COLTURA_DES));
	}
	
	public String getCode() {
		return COLTURA_COD;
	}
	
	public String getDescription() {
		return COLTURA_DES;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }	        

        if (!CropRecord.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        
        CropRecord cmp = (CropRecord) obj;
        
        return cmp.getCode().equals(this.getCode());
	}
	
	public String toString() {
		return "Crop code:"+COLTURA_COD+" descr:"+COLTURA_DES;
	}


	@Override
    public int hashCode() {
		return COLTURA_COD.hashCode();
	}

}
