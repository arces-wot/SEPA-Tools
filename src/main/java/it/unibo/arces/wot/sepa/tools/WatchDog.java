package it.unibo.arces.wot.sepa.tools;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.security.ClientSecurityManager;
import it.unibo.arces.wot.sepa.pattern.Aggregator;
import it.unibo.arces.wot.sepa.pattern.JSAP;

public class WatchDog extends Aggregator {
	private Boolean running = new Boolean(false);
	private Boolean warning = new Boolean(false);
	
	public WatchDog(JSAP jsap, ClientSecurityManager sm)
			throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException {
		super(jsap, "WATCHDOG_IN", "WATCHDOG_OUT", sm);
		
		appProfile.read("tools.jsap");
		
		appProfile.setAutoReconnect(true);
	}
	
	public void start(int period) throws SEPASecurityException, SEPAPropertiesException, SEPAProtocolException, SEPABindingsException {
		subscribe(5000);
		
		new Thread() {
			public void run() {
				while(running) {
					warning = true;
					try {
						running.wait(period);
					} catch (InterruptedException e) {
						return;
					}
					
				}
			}
		}.start();
	}

}
