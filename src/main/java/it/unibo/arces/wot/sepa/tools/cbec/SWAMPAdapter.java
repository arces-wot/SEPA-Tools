package it.unibo.arces.wot.sepa.tools.cbec;

import java.io.IOException;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class SWAMPAdapter {
	public static void main(String[] args) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, IOException, SEPABindingsException {
		CBECAdapter adapter = null;
		
		boolean init = false;
		boolean misure = false;
		
		boolean irrigation = false;
		boolean field = false;
		boolean farmer = false;
		
		if (args.length == 0) return;
		
		for (String arg : args) {
			if (arg.equals("-init")) init = true;
			
			if (arg.equals("misure")) misure = true;
			
			if (arg.equals("irrigazioni")) irrigation = true;
			
			if (arg.equals("campi")) field = true;
			
			if (arg.equals("conduttori")) farmer = true;
		}
		
		if (misure) adapter = new Misure(init);
		else adapter = new Irrigazioni(init,irrigation,farmer,field);
			
		adapter.refresh();	
	}
}
