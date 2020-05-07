package it.unibo.arces.wot.sepa.tools.covid19.producers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.google.gson.JsonArray;

import it.unibo.arces.wot.sepa.commons.exceptions.SEPABindingsException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAPropertiesException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPAProtocolException;
import it.unibo.arces.wot.sepa.commons.exceptions.SEPASecurityException;
import it.unibo.arces.wot.sepa.pattern.JSAP;
import it.unibo.arces.wot.sepa.tools.DropGraph;
import it.unibo.arces.wot.sepa.tools.covid19.AddObservations;

public class ProtezioneCivile {

	private static String observationGraph = "http://covid19/observation";
	private static String historyGraph = "http://covid19/observation/history";
	private static String hostJsap = "covid19.jsap";

	public static void main(String[] args) throws FileNotFoundException, SEPAProtocolException, SEPASecurityException,
			SEPAPropertiesException, IOException, SEPABindingsException {
		
		JSAP jsap = new JSAP(hostJsap);
		
		liveUpdate(jsap);
		
		//fix(jsap);
	}
	
	static void liveUpdate(JSAP jsap) throws SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, SEPABindingsException, IOException {
		// Drop observation graph
		DropGraph agentDropGraphs = new DropGraph(jsap,null);
		agentDropGraphs.drop(observationGraph);
		agentDropGraphs.close();
		
		AddObservations agentObservations = new AddObservations(jsap,null);
		
		JsonArray set = AddObservations.loadJsonArray("dpc-covid19-ita-andamento-nazionale-latest.json");
		
		agentObservations.addNationalObservations(observationGraph,set);
		agentObservations.addNationalObservations(historyGraph,set);
		
		set = AddObservations.loadJsonArray("dpc-covid19-ita-regioni-latest.json");
		
		agentObservations.addRegionObservations(observationGraph,set);
		agentObservations.addRegionObservations(historyGraph,set);
		
		set = AddObservations.loadJsonArray("dpc-covid19-ita-province-latest.json");
		
		agentObservations.addProvinceObservations(observationGraph,set);
		agentObservations.addProvinceObservations(historyGraph,set);
		
		agentObservations.close();	
	}
	
	static void fix(JSAP jsap) throws FileNotFoundException, SEPAProtocolException, SEPASecurityException, SEPAPropertiesException, IOException, SEPABindingsException {
		AddObservations agentObservations = new AddObservations(jsap,null);
		
		Calendar date = new GregorianCalendar();
		date.set(2020, 1, 27);	
		
		Calendar end = new GregorianCalendar();
		end.set(2020, 3,28);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		while(date.getTime().before(end.getTime())) {
			String csv = "dpc-covid19-ita-regioni-"+ format.format(date.getTime())+".csv";		
			JsonArray set = AddObservations.loadCSVRegioni(csv);
			
			agentObservations.addRegionObservations(observationGraph,set);
			agentObservations.addRegionObservations(historyGraph,set);
			
			csv = "dpc-covid19-ita-province-"+ format.format(date.getTime())+".csv";
			set = AddObservations.loadCSVProvince(csv);
			
			agentObservations.addProvinceObservations(observationGraph,set);
			agentObservations.addProvinceObservations(historyGraph,set);
			
			date.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		agentObservations.close();
	}

}
