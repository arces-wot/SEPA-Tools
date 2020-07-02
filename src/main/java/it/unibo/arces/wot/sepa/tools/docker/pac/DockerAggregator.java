package it.unibo.arces.wot.sepa.tools.docker.pac;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.security.ClientSecurityManager;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.BindingsResults;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.Aggregator;
import it.unibo.arces.wot.sepa.pattern.JSAP;

/**
 * Hello world!
 *
 */
public class DockerAggregator extends Aggregator {
	private static final Logger logger = LogManager.getLogger();

	private long sum = 0;

	public DockerAggregator(JSAP appProfile, String subscribeID, String updateID, ClientSecurityManager sm)
			throws SEPAProtocolException, SEPASecurityException {
		super(appProfile, subscribeID, updateID, sm);
	}

	public void onAddedResults(BindingsResults results) {
		for (Bindings b : results.getBindings()) {
			long count = Long.parseLong(b.getValue("count"));
			logger.info("New counter: " + b.getValue("subject") + " count: " + count);
			sum += count;

			try {
				setUpdateBindingValue("sum", new RDFTermLiteral(String.format("%d", sum)));
			} catch (SEPABindingsException e) {
				logger.error(e.getMessage());
				continue;
			}
			try {
				setUpdateBindingValue("subject", new RDFTermURI(b.getValue("subject")));
			} catch (SEPABindingsException e) {
				logger.error(e.getMessage());
				continue;
			}
			try {
				logger.info("Update sum: " + b.getValue("subject") + " sum: " + sum);
				update();
			} catch (SEPASecurityException e) {
				logger.error(e.getMessage());
			} catch (SEPAProtocolException e) {
				logger.error(e.getMessage());
			} catch (SEPAPropertiesException e) {
				logger.error(e.getMessage());
			} catch (SEPABindingsException e) {
				logger.error(e.getMessage());
			}
		}
	}

	public static void main(String[] args) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException,
			SEPABindingsException, IOException {
		logger.info("Create aggregator");
		DockerAggregator app = new DockerAggregator(new JSAP("docker.jsap"), "COUNTER", "SUM", null);
		app.subscribe();

		synchronized (app) {
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
