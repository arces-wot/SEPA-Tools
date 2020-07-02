package it.unibo.arces.wot.sepa.tools;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.commons.response.QueryResponse;
import it.unibo.arces.wot.sepa.commons.response.Response;
import it.unibo.arces.wot.sepa.commons.sparql.Bindings;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermLiteral;
import it.unibo.arces.wot.sepa.commons.sparql.RDFTermURI;
import it.unibo.arces.wot.sepa.pattern.GenericClient;
import it.unibo.arces.wot.sepa.pattern.JSAP;

public class Debugging {
	private static final Logger logger = LogManager.getLogger();
	
	public static void main(String[] args) throws SEPAProtocolException, SEPAPropertiesException, SEPASecurityException, SEPABindingsException, IOException {
		debugHistoricalWeatherLogging();
	}

	public static void debugHistoricalWeatherLogging() throws SEPAPropertiesException, SEPASecurityException, SEPAProtocolException, SEPABindingsException, IOException {
		JSAP jsap = new JSAP("debug.jsap");
		
		GenericClient sepaClient = new GenericClient(jsap, null, null);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Bindings forced = new Bindings();
		forced.addBinding("observation", new RDFTermURI("arces-monitor:Current_Weather_Bertacchini_Precipitation"));
		
		Calendar end = new GregorianCalendar();
		end.set(2020, 4, 25);
		
		Calendar day = new GregorianCalendar();
		day.set(2020, 0, 1);
		
		while (day.before(end)) {
			String today = format.format(day.getTime());
			forced.addBinding("from", new RDFTermLiteral(today+"T00:00:00Z"));
			forced.addBinding("to", new RDFTermLiteral(today+"T23:59:59Z"));
			Response ret = sepaClient.query("WEATHER_DEBUG", forced);
			if (ret.isError()) {
				logger.error(today+" "+ret);
			}
			else {
				QueryResponse res = (QueryResponse) ret;
				if (res.getBindingsResults().size() != 69) logger.warn(today+" "+res.getBindingsResults().size());
				else logger.info(today+" "+res.getBindingsResults().size());
			}
			
			day.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		sepaClient.close();
		
	}
}
