package it.unibo.arces.wot.sepa.tools.cbec.producer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.JSAP;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class IrrigationRequestRecord extends Producer {
	protected static final Logger logger = LogManager.getLogger();
	
	private String numPren;
	private String numRichiesta;	
	private Date dataPren;		
	private String codiceApp;
	
	public IrrigationRequestRecord(JsonObject jsonObject) throws IllegalArgumentException, ParseException, SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException{
		super(new JSAP("swamp.jsap"), "UPDATE_OBSERVATION_VALUE", null);
		
		if (!jsonObject.has("NUMPREN")) throw new IllegalArgumentException("NUMPREN is missing");
		if (!jsonObject.has("NUMRICHIESTA")) throw new IllegalArgumentException("NUMPREN is missing");
		if (!jsonObject.has("DATARICHIESTA")) throw new IllegalArgumentException("DATAPREN is missing");
		if (!jsonObject.has("CODICEAPP")) throw new IllegalArgumentException("CODICEAPP is missing");
		
		numPren = jsonObject.get("NUMPREN").getAsString().trim();
		numRichiesta = jsonObject.get("NUMRICHIESTA").getAsString().trim();
		codiceApp = jsonObject.get("CODICEAPP").getAsString().trim();
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		dataPren = format.parse(jsonObject.get("DATARICHIESTA").getAsString().split(" ")[0]);
		SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		
		setUpdateBindingValue("fieldUri", new RDFTermURI("http://swamp-project.org/cbec/field_"+codiceApp));
		setUpdateBindingValue("requestNumber", new RDFTermLiteral(numRichiesta));
		setUpdateBindingValue("reservationNumber", new RDFTermLiteral(numPren));
		setUpdateBindingValue("timestamp", new RDFTermLiteral(timestamp.format(dataPren)));
	}
	
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }	        

        if (!IrrigationRequestRecord.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        
        IrrigationRequestRecord cmp = (IrrigationRequestRecord) obj;
        
        return cmp.hashCode() == hashCode();
	}
	
	@Override
    public int hashCode() {
		String codeString = numPren+numRichiesta+codiceApp+dataPren;
		return codeString.hashCode();
	}
}
