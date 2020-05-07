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

public class FarmerRecord extends Producer {

	String CODICEUTENTE;
	String CND_DCNDCOGN;
	String CND_DCNDNOME;
	String CND_TELEFONO;
	String CINDIRIZZO;
	String CCOMUNE;
	String CCAP;
	
	public FarmerRecord(JsonObject jsonObject) throws IllegalArgumentException, ParseException, SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException{
		super(new JSAP("swamp.jsap"), "FARMER", null);
		
		if (!jsonObject.has("CODICEUTENTE")) throw new IllegalArgumentException("CODICEUTENTE is missing");
		if (!jsonObject.has("CND_DCNDCOGN")) throw new IllegalArgumentException("CND_DCNDCOGN is missing");
		if (!jsonObject.has("CND_DCNDNOME")) throw new IllegalArgumentException("CND_DCNDNOME is missing");
		if (!jsonObject.has("CND_TELEFONO")) throw new IllegalArgumentException("CND_TELEFONO is missing");
		if (!jsonObject.has("CINDIRIZZO")) throw new IllegalArgumentException("CINDIRIZZO is missing");
		if (!jsonObject.has("CCOMUNE")) throw new IllegalArgumentException("CCOMUNE is missing");
		if (!jsonObject.has("CCAP")) throw new IllegalArgumentException("CCAP is missing");
		
		CODICEUTENTE = jsonObject.get("CODICEUTENTE").getAsString().trim();
		CND_DCNDCOGN = jsonObject.get("CND_DCNDCOGN").getAsString().trim();
		CND_DCNDNOME = jsonObject.get("CND_DCNDNOME").getAsString().trim();
		CND_TELEFONO = jsonObject.get("CND_TELEFONO").getAsString().trim();
		CINDIRIZZO = jsonObject.get("CINDIRIZZO").getAsString().trim();
		CCOMUNE = jsonObject.get("CCOMUNE").getAsString().trim();
		CCAP = jsonObject.get("CCAP").getAsString().trim();
		
		setUpdateBindingValue("farmerUri", new RDFTermURI("http://swamp-project.org/cbec/farmer_"+CODICEUTENTE));
		setUpdateBindingValue("name", new RDFTermLiteral(CND_DCNDNOME));
		setUpdateBindingValue("surname", new RDFTermLiteral(CND_DCNDCOGN));
	}
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }	        

        if (!FarmerRecord.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        
        FarmerRecord cmp = (FarmerRecord) obj;
        
        return cmp.hashCode() == this.hashCode();
	}


	@Override
    public int hashCode() {
		return CODICEUTENTE.hashCode();
	}
}
