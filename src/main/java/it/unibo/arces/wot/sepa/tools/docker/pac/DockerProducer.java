package it.unibo.arces.wot.sepa.tools.docker.pac;

import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.JSAP;
import it.unibo.arces.wot.sepa.pattern.Producer;

/**
 * Hello world!
 *
 */
public class DockerProducer 
{
	private static final Logger logger = LogManager.getLogger();
	private static String subject;
	
    public static void main( String[] args ) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException, IOException
    {
    	logger.info("Create producer");
    	Producer app = new Producer(new JSAP("docker.jsap"),"COUNTER",null);
    	
    	logger.info("Set UUID");
    	subject = "docker:Producer_"+UUID.randomUUID().toString().replace("-", "_");
    	app.setUpdateBindingValue("subject", new RDFTermURI(subject));
    	    	
    	Thread th = new Thread() {
    		public void run() {
    			long count = 0;
    			
    			while(true) {
    	    		try {
    	    			logger.info(subject+" update counter "+count);
    	    			app.setUpdateBindingValue("count", new RDFTermLiteral(String.format("%d", count++)));
						app.update();
    	    		} catch (SEPABindingsException | SEPASecurityException | SEPAProtocolException | SEPAPropertiesException e) {
						logger.warn(e.getMessage());
    	    			return;
					}
    	    		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						logger.warn(e.getMessage());
						return;
					}
    	    	}	
    		}
    	};
    	
    	th.start();
    	
    	try {
			synchronized(th) {
				th.wait();
			}
		} catch (InterruptedException e) {
			logger.warn(e.getMessage());
		}
    	
    	app.close(); 
    	
    	logger.info("Bye bye");
    }
}
