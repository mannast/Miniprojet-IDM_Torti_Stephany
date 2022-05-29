package SimplePDLToPetriNet;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;


public class SimplePDLToPetriNetEMF {

	public static Process loadProcess(String modelPath) {
		// Chargement du package SimplePDL 
		SimplepdlPackage packageInstance = SimplepdlPackage.eINSTANCE;
		
		// Enregistrer l'extension ".xmi" comme devant être ouverte à
		// l'aide d'un objet "XMIResourceFactoryImpl"
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
		
		// Créer un objet resourceSetImpl 
		ResourceSet resSet = new ResourceSetImpl();

		// Charger la ressource 
		URI modelURI = URI.createURI(modelPath);
		Resource resource = resSet.getResource(modelURI, true);
		
		// Récupérer le premier élément du modèle 
		return (Process) resource.getContents().get(0);
	}
	
	public static void transformToPetriNet(Process process, String modelPath) {
		// Charger le package PetriNet afin de l'enregistrer dans le registre d'Eclipse.
		PetrinetPackage packageInstance = PetrinetPackage.eINSTANCE;
		
		// Créer un objet resourceSetImpl qui contiendra une ressource EMF (le modèle)
		ResourceSet resSet = new ResourceSetImpl();
		
		// Définir la ressource (le modèle)
		URI modelURI = URI.createURI(modelPath);
		Resource resource = resSet.createResource(modelURI);
		
		// La fabrique pour fabriquer les éléments de PetriNet
	    PetrinetFactory myFactory = PetrinetFactory.eINSTANCE;
	    
		 // Créer un élément PetriNet
 		PetriNet petrinet = myFactory.createPetriNet();
 		petrinet.setName(process.getName());
 		
 		resource.getContents().add(petrinet);
 		
 		process.getProcessElements().stream().forEach(element -> {
 			// WorkDefinition
 			if (element instanceof WorkDefinition) {
 				WorkDefinition wd = (WorkDefinition) element;
 				
 				// WorkDefinition Places
 				Place ready = myFactory.createPlace();
 	 			ready.setName(wd.getName() + "_ready");
 	 			ready.setMarking(1);
 	 			ready.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(ready);
 	 			
 	 			Place running = myFactory.createPlace();
 	 			running.setName(wd.getName() + "_running");
 	 			running.setMarking(0);
 	 			running.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(running);
 	 			
 	 			Place started = myFactory.createPlace();
 	 			started.setName(wd.getName() + "_started");
 	 			started.setMarking(0);
 	 			started.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(started);
 	 			
 	 			Place finished = myFactory.createPlace();
 	 			finished.setName(wd.getName() + "_finished");
 	 			finished.setMarking(0);
 	 			finished.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(finished);
 	 			
 	 			// WorkDefinition Transitions
 	 			Transition start = myFactory.createTransition();
 	 			start.setName(wd.getName() + "_start");
 	 			start.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(start);
 	 			
 	 			Transition finish = myFactory.createTransition();
 	 			finish.setName(wd.getName() + "_finish");
 	 			finish.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(finish);
 	 			
 	 			// WorkDefinition Arcs
 	 			Arc ready2start = myFactory.createArc();
 	 			ready2start.setWeight(1);
 	 			ready2start.setIsReadArc(false);
 	 			ready2start.setSource(ready);
 	 			ready2start.setTarget(start);
 	 			ready2start.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(ready2start);
 	 			
 	 			Arc start2running = myFactory.createArc();
 	 			start2running.setWeight(1);
 	 			start2running.setIsReadArc(false);
 	 			start2running.setSource(start);
 	 			start2running.setTarget(running);
 	 			start2running.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(start2running);
 	 			
 	 			Arc start2started = myFactory.createArc();
 	 			start2started.setWeight(1);
 	 			start2started.setIsReadArc(false);
 	 			start2started.setSource(start);
 	 			start2started.setTarget(started);
 	 			start2started.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(start2started);
 	 			
 	 			Arc running2finish = myFactory.createArc();
 	 			running2finish.setWeight(1);
 	 			running2finish.setIsReadArc(false);
 	 			running2finish.setSource(running);
 	 			running2finish.setTarget(finish);
 	 			running2finish.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(running2finish);
 	 			
 	 			Arc finish2finished = myFactory.createArc();
 	 			finish2finished.setWeight(1);
 	 			finish2finished.setIsReadArc(false);
 	 			finish2finished.setSource(finish);
 	 			finish2finished.setTarget(finished);
 	 			finish2finished.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(finish2finished);	 		
	
 			// WorkSequence
 			} else if (element instanceof WorkSequence) {
 				WorkSequence ws = (WorkSequence) element;
 				
 				Arc arcws = myFactory.createArc();
 	 			arcws.setWeight(1);
 	 			arcws.setIsReadArc(true);
 	 			arcws.setSource((Node) petrinet.getPetriElements().stream().filter(p -> p instanceof Node).filter(n -> ((Node) n).getName()
 	 					.equals(ws.getPredecessor().getName() + (ws.getLinkType() == WorkSequenceType.START_TO_START
 	 					|| ws.getLinkType() == WorkSequenceType.START_TO_FINISH ? "_started" : "_finished")) ).toArray()[0]);
 	 			arcws.setTarget((Node) petrinet.getPetriElements().stream().filter(p -> p instanceof Node).filter(n -> ((Node) n).getName()
 	 					.equals(ws.getSuccessor().getName() + (ws.getLinkType() == WorkSequenceType.START_TO_START
 	 					|| ws.getLinkType() == WorkSequenceType.FINISH_TO_START ? "_start" : "_finish")) ).toArray()[0]);
 	 			arcws.setPetrinet(petrinet);
 	 			petrinet.getPetriElements().add(arcws);
 	 		// Resources
 			} else if (element instanceof simplepdl.Resource) {
 				simplepdl.Resource rs = (simplepdl.Resource) element;
 				
 				Place placeR = myFactory.createPlace();
 				placeR.setName(rs.getName() + "resource");
 				placeR.setMarking(rs.getNbAvailableResources());
 				placeR.setPetrinet(petrinet);
 				petrinet.getPetriElements().add(placeR);
 				
 				// Needs
 				for (Need ne : rs.getNeeds()) {
 					Arc anLoad = myFactory.createArc();
 	 				anLoad.setWeight(ne.getNbResources());
 	 				anLoad.setIsReadArc(false);
 	 				anLoad.setSource(placeR);
 	 				anLoad.setTarget((Node) petrinet.getPetriElements().stream().filter(p -> p instanceof Node).filter(n -> ((Node) n).getName()
 	 	 					.equals(ne.getWorkdefinition().getName() + "_start")).toArray()[0]);
 	 				anLoad.setPetrinet(petrinet);
 	 				petrinet.getPetriElements().add(anLoad);
 	 				
 	 				Arc anRelease = myFactory.createArc();
 	 				anRelease.setWeight(ne.getNbResources());
 	 				anRelease.setIsReadArc(false);
 	 				anRelease.setSource((Node) petrinet.getPetriElements().stream().filter(p -> p instanceof Node).filter(n -> ((Node) n).getName()
 	 	 					.equals(ne.getWorkdefinition().getName() + "_finish")).toArray()[0]);
 	 				anRelease.setTarget(placeR);
 	 				anRelease.setPetrinet(petrinet);
 	 				petrinet.getPetriElements().add(anRelease);
 				}
 			}
 		});
 		
 		// Sauver la ressource
	    try {
	    	resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
 		
	}
	
	public static void main(String[] args) {
		if (args.length != 2)
			throw new IllegalArgumentException("Usage: java SimplePDL2PetriNetEMF <simplepdl_model_path> <petrinet_model_path>");
		
		Process process = loadProcess(args[0]);
		
		transformToPetriNet(process, args[1]);
	}

}
