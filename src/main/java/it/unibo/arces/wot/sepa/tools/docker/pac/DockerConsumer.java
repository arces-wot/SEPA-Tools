package it.unibo.arces.wot.sepa.tools.docker.pac;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.security.ClientSecurityManager;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import it.unibo.arces.wot.sepa.pattern.Consumer;
import it.unibo.arces.wot.sepa.pattern.JSAP;

/**
 * Hello world!
 *
 */
public class DockerConsumer extends Consumer { 
	private static final Logger logger = LogManager.getLogger();
		
		public DockerConsumer(JSAP appProfile, String subscribeID, ClientSecurityManager sm)
				throws SEPAProtocolException, SEPASecurityException {
			super(appProfile, subscribeID, sm);
		}

		public void onAddedResults(BindingsResults results) {
			logger.info(results.toString());
		}
		
	
    public static void main( String[] args ) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException, IOException
    {
    	logger.info("Create consumer");
    	DockerConsumer app = new DockerConsumer(new JSAP("docker.jsap"),"SUM",null);
    	app.subscribe();
    	
    	synchronized(app) {
    		try {
				app.wait();
			} catch (InterruptedException e) {
				logger.warn(e.getMessage());
			}
    	}
    	
    	app.close();
    	    	
    	logger.info("Bye bye");
    }
}
