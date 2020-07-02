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

public class CanalRecord extends Producer {	
	String CANALECOD;
	String CANALEDES;

	
	public CanalRecord(JsonObject jsonObject) throws IllegalArgumentException, ParseException, SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException{
		super(new JSAP("swamp.jsap"), "CANAL", null);
		
		if (!jsonObject.has("CANALECOD")) throw new IllegalArgumentException("CANALECOD is missing");
		if (!jsonObject.has("CANALEDES")) throw new IllegalArgumentException("CANALEDES is missing");
				
		CANALECOD = jsonObject.get("CANALECOD").getAsString().trim();
		CANALEDES = jsonObject.get("CANALEDES").getAsString().trim();
				
		setUpdateBindingValue("canal", new RDFTermURI("http://swamp-project.org/cbec/canal_"+CANALECOD));
		setUpdateBindingValue("code", new RDFTermLiteral(CANALECOD));
		setUpdateBindingValue("label", new RDFTermLiteral(CANALEDES));
	}
	
	public String getCode() {
		return CANALECOD;
	}
	
	public String getDescription() {
		return CANALEDES;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }	        

        if (!CanalRecord.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        
        CanalRecord cmp = (CanalRecord) obj;
        
        return cmp.getCode().equals(this.getCode());
	}
	
	public String toString() {
		return "Canal code:"+CANALECOD+" descr:"+CANALEDES;
	}


	@Override
    public int hashCode() {
		return CANALECOD.hashCode();
	}

}
