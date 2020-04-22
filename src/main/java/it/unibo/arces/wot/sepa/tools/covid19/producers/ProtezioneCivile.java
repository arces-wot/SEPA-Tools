package it.unibo.arces.wot.sepa.tools.covid19.producers;

import java.io.FileNotFoundException;
import java.io.IOException;

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

}
