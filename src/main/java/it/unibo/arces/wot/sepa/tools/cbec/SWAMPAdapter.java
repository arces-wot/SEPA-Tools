package it.unibo.arces.wot.sepa.tools.cbec;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;

public class SWAMPAdapter {
	public static void main(String[] args) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, IOException, SEPABindingsException {
		final Logger logger = LogManager.getLogger();
		
		CBECAdapter adapter = null;
		
		boolean init = false;
		boolean misure = false;
		
		boolean irrigation = false;
		boolean field = false;
		boolean farmer = false;
		boolean crop = false;
		boolean canal = false;
		
		long timeout = 30; //minutes
		boolean fixedtime = true;
		
		if (args.length == 0) return;
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-init")) init = true;
			
			if (args[i].equals("-sleep")) timeout = Long.parseLong(args[i+1]);
						
			if (args[i].equals("-timestamp")) fixedtime = false;
			
			if (args[i].equals("sensor")) misure = true;
			
			if (args[i].equals("water")) irrigation = true;
			
			if (args[i].equals("field")) field = true;
			
			if (args[i].equals("farmer")) farmer = true;
			
			if (args[i].equals("crop")) crop = true;
			
			if (args[i].equals("canal")) canal = true;
			
			if (args[i].equals("all")) {
				irrigation = true;
				field = true;
				crop = true;
				canal = true;
			}
		}
		
		if (misure) adapter = new Misure(init,fixedtime);
		else adapter = new Irrigazioni(irrigation,farmer,field,crop,canal);
			
		while(true) {
			adapter.refresh();
			try {
				logger.info("Sleep for "+timeout+" minutes...");
				Thread.sleep(timeout*60*1000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
