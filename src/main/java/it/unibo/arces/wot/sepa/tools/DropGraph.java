package it.unibo.arces.wot.sepa.tools;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.security.ClientSecurityManager;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.JSAP;
import it.unibo.arces.wot.sepa.pattern.Producer;

public class DropGraph extends Producer {
	
	public DropGraph(JSAP appProfile, ClientSecurityManager sm)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super(appProfile, "DROP_GRAPH", sm);
	}

	public void drop(String uri) throws SEPABindingsException, SEPASecurityException, SEPAProtocolException, SEPAPropertiesException {
		setUpdateBindingValue("graph", new RDFTermURI(uri));
		update();
	}
}
