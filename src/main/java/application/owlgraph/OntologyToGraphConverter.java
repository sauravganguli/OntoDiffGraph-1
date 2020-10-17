package application.owlgraph;

import application.diff.DiffGroup;
import application.diff.GroupDiffType;
import application.diff.OntologyDiff;
import application.graph.GraphPane;
import application.graph.edge.GraphEdge;
import application.graph.vertex.GraphVertex;
import application.util.Tuple;
import application.util.Utils;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OntologyToGraphConverter {

	private final OWLOntology ontology;
	private final OntologyDiff diff;

	private final GraphPane graph;

	private final HashMap<IRI,OWLClass> classes = new HashMap<>();
	private final HashMap<IRI,OWLObjectProperty> objectProperties = new HashMap<>();
	private final HashMap<IRI,OWLDataProperty> dataProperties = new HashMap<>();
	private final HashMap<IRI,OWLAnnotationProperty> annotationProperties = new HashMap<>();

	private final HashMap<IRI,GraphVertex> classesVertices = new HashMap<>();
	private final HashMap<IRI,GraphVertex> objVertices = new HashMap<>();
	private final HashMap<IRI,GraphVertex> dataVertices = new HashMap<>();
	private final HashMap<IRI,GraphVertex> annotationVertices = new HashMap<>();

	//Vertex -> Object Map (for click events)
	private final HashMap<GraphVertex,OWLObject> owlElementsByVertex = new HashMap<>();

	public OntologyToGraphConverter(OWLOntology ontology, GraphPane graph, OntologyDiff diff){
		this.ontology = ontology;
		this.diff = diff;
		this.graph = graph;

		createMapElements(diff);
		createGraph(diff);
		generateVertexObjectMap();
	}

	private void createMapElements(OntologyDiff diff){

		diff.getClassDiff().getAllValues().forEach(owlClass -> {
			classes.put(owlClass.getIRI(),owlClass);
		});
		diff.getObjDiff().getAllValues().forEach(owlObjectProperty -> {
			objectProperties.put(owlObjectProperty.getIRI(),owlObjectProperty);
		});
		diff.getDataDiff().getAllValues().forEach(owlDataProperty -> {
			dataProperties.put(owlDataProperty.getIRI(),owlDataProperty);
		});
		diff.getAnnotationsDiff().getAllValues().forEach(annotationProperty -> {
			annotationProperties.put(annotationProperty.getIRI(),annotationProperty);
		});


	}

	private void createGraph(OntologyDiff diff){

		diff.getClassDiff().getAllValuesWithDiff().forEach(tuple -> {
			IRI iri = tuple.getFirst().getIRI();
			GraphVertex vertex;

			try{
				if(Utils.isImported(diff.getCorrectElementOntology(tuple.getSecond()),tuple.getFirst())){
					vertex = GraphElements.createExternalClassVertex(iri.getShortForm(),tuple.getSecond());
				}
				else{
					vertex = GraphElements.createClassVertex(iri.getShortForm(),tuple.getSecond());
				}

				classesVertices.put(iri,vertex);
				graph.addVertex(vertex);
			}
			catch(IllegalArgumentException e){
				System.out.println("URI Syntax Exception on "+iri.toString()+", adding as internal");

				vertex = GraphElements.createClassVertex(iri.getShortForm(),tuple.getSecond());
				classesVertices.put(iri,vertex);
				graph.addVertex(vertex);
			}
		});

		processClassExpressionAxioms();

		diff.getObjDiff().getAllValuesWithDiff().forEach(tuple -> {
			IRI iri = tuple.getFirst().getIRI();
			GraphVertex vertex;

			try{
				if(Utils.isImported(diff.getCorrectElementOntology(tuple.getSecond()),tuple.getFirst())){
					vertex = GraphElements.createExternalPropVertex(iri.getShortForm(),tuple.getSecond());
				}
				else{
					vertex = GraphElements.createObjPropVertex(iri.getShortForm(),tuple.getSecond());
				}

				objVertices.put(iri,vertex);
				graph.addVertex(vertex);
			}
			catch(IllegalArgumentException e){
				System.out.println("URI Syntax Exception on "+iri.toString()+", adding as internal");

				vertex = GraphElements.createObjPropVertex(iri.getShortForm(),tuple.getSecond());
				objVertices.put(iri,vertex);
				graph.addVertex(vertex);
			}
		});

		processObjectPropertyAxioms();

		diff.getDataDiff().getAllValuesWithDiff().forEach(tuple -> {
			IRI iri = tuple.getFirst().getIRI();
			GraphVertex vertex;

			try{
				if(Utils.isImported(diff.getCorrectElementOntology(tuple.getSecond()),tuple.getFirst())){
					vertex = GraphElements.createExternalPropVertex(iri.getShortForm(),tuple.getSecond());
				}
				else{
					vertex = GraphElements.createDataPropVertex(iri.getShortForm(),tuple.getSecond());
				}

				dataVertices.put(iri,vertex);
				graph.addVertex(vertex);
			}
			catch(IllegalArgumentException e){
				System.out.println("URI Syntax Exception on "+iri.toString()+", adding as internal");

				vertex = GraphElements.createDataPropVertex(iri.getShortForm(),tuple.getSecond());
				dataVertices.put(iri,vertex);
				graph.addVertex(vertex);
			}
		});

		processDataPropertyAxioms();

		diff.getAnnotationsDiff().getAllValuesWithDiff().forEach(tuple -> {
			IRI iri = tuple.getFirst().getIRI();
			GraphVertex vertex;

			try{
				if(Utils.isImported(diff.getCorrectElementOntology(tuple.getSecond()),tuple.getFirst())){
					vertex = GraphElements.createExternalPropVertex(iri.getShortForm(),tuple.getSecond());
				}
				else{
					vertex = GraphElements.createAnnotationPropertyVertex(iri.getShortForm(),tuple.getSecond());
				}

				annotationVertices.put(iri,vertex);
				graph.addVertex(vertex);
			}
			catch(IllegalArgumentException e){
				System.out.println("URI Syntax Exception on "+iri.toString()+", adding as internal");

				vertex = GraphElements.createAnnotationPropertyVertex(iri.getShortForm(),tuple.getSecond());
				annotationVertices.put(iri,vertex);
				graph.addVertex(vertex);
			}
		});

		processAnnotationsAxioms();
	}

	private void generateVertexObjectMap(){
		classes.keySet().forEach(iri -> owlElementsByVertex.put(classesVertices.get(iri),classes.get(iri)));
		objectProperties.keySet().forEach(iri -> owlElementsByVertex.put(objVertices.get(iri),objectProperties.get(iri)));
		dataProperties.keySet().forEach(iri -> owlElementsByVertex.put(dataVertices.get(iri),dataProperties.get(iri)));
		annotationProperties.keySet().forEach(iri -> owlElementsByVertex.put(annotationVertices.get(iri),annotationProperties.get(iri)));
	}

	private void processClassExpressionAxioms(){
		diff.getAxiomDiff(AxiomType.SUBCLASS_OF).getAllValuesWithDiff().forEach(tuple -> {
			OWLClassExpression subClass = tuple.getFirst().getSubClass();
			OWLClassExpression superClass = tuple.getFirst().getSuperClass();

			GraphVertex subClassVertex = processExpression(subClass,tuple.getSecond());
			GraphVertex superClassVertex = processExpression(superClass,tuple.getSecond());

			GraphEdge ge = GraphElements.createLabeledEdge(subClassVertex,superClassVertex,"subClassOf",tuple.getSecond());
			graph.addEdge(ge);
		});

		diff.getAxiomDiff(AxiomType.EQUIVALENT_CLASSES).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex eqClass = GraphElements.createEquivalentClassVertex(tuple.getSecond());
			graph.addVertex(eqClass);

			tuple.getFirst().classExpressions()
					.map(classExpression -> processExpression(classExpression,tuple.getSecond()))
					.forEach(vertex -> connectVertices(eqClass,vertex,tuple.getSecond()));
		});

		diff.getAxiomDiff(AxiomType.DISJOINT_CLASSES).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex disjClass = GraphElements.createDisjointClassVertex(tuple.getSecond());
			graph.addVertex(disjClass);
			tuple.getFirst().classExpressions()
					.map(classExpression -> processExpression(classExpression,tuple.getSecond()))
					.forEach(vertex -> connectVertices(disjClass,vertex,tuple.getSecond()));
		});

		diff.getAxiomDiff(AxiomType.DISJOINT_UNION).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex classVertex = classesVertices.get(tuple.getFirst().getOWLClass().getIRI());
			GraphVertex unionVertex = GraphElements.createUnionVertex();
			GraphEdge edge = GraphElements.createLabeledEdge(classVertex,unionVertex,"disjointUnion",tuple.getSecond());

			graph.addVertex(unionVertex);
			graph.addEdge(edge);

			tuple.getFirst().classExpressions()
					.map(classExpression -> processExpression(classExpression,tuple.getSecond()))
					.forEach(vertex -> connectVertices(unionVertex,vertex,tuple.getSecond()));
		});
	}
	private void processObjectPropertyAxioms(){

		//DOMAIN and RANGE
		diff.getObjDiff().getAllValuesWithDiff().forEach(tuple -> {

			GraphVertex objVertex = objVertices.get(tuple.getFirst().getIRI());
			DiffGroup<OWLClassExpression> domain = diff.getObjDomainDiff(tuple.getFirst());
			DiffGroup<OWLClassExpression> range = diff.getObjRangeDiff(tuple.getFirst());

			GraphVertex endDomain = processClassExpressionGroup(domain);
			GraphVertex endRange = processClassExpressionGroup(range);

			if(endDomain != null){
				GraphEdge ge = GraphElements.createLabeledEdge(objVertex,endDomain,"ObjPropDomain",domain.getGeneralDiff());
				graph.addEdge(ge);
			}
			if(endRange != null){
				GraphEdge ge = GraphElements.createLabeledEdge(objVertex,endRange,"ObjPropRange",range.getGeneralDiff());
				graph.addEdge(ge);
			}
		});

		diff.getAxiomDiff(AxiomType.SUB_OBJECT_PROPERTY).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex subClassVertex = processExpression(tuple.getFirst().getSubProperty(),tuple.getSecond());
			GraphVertex superClassVertex = processExpression(tuple.getFirst().getSuperProperty(),tuple.getSecond());

			GraphEdge ge = GraphElements.createLabeledEdge(subClassVertex,superClassVertex,"subObjPropOf",tuple.getSecond());
			graph.addEdge(ge);
		});

		diff.getAxiomDiff(AxiomType.EQUIVALENT_OBJECT_PROPERTIES).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex eqVertex = GraphElements.createEquivalentObjPropVertex(tuple.getSecond());
			graph.addVertex(eqVertex);
			tuple.getFirst().properties()
					.map(objectPropertyExpression -> processExpression(objectPropertyExpression,tuple.getSecond()))
					.forEach(vertex -> connectVertices(eqVertex,vertex,tuple.getSecond()));
		});

		diff.getAxiomDiff(AxiomType.DISJOINT_OBJECT_PROPERTIES).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex disjointVertex = GraphElements.createDisjointObjPropVertex(tuple.getSecond());
			graph.addVertex(disjointVertex);
			tuple.getFirst().properties()
					.map(objectPropertyExpression -> processExpression(objectPropertyExpression,tuple.getSecond()))
					.forEach((vertex -> connectVertices(disjointVertex,vertex,tuple.getSecond())));
		});

		diff.getAxiomDiff(AxiomType.INVERSE_OBJECT_PROPERTIES).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex originalVertex = processExpression(tuple.getFirst().getFirstProperty(),tuple.getSecond());
			GraphVertex inverseVertex = processExpression(tuple.getFirst().getSecondProperty(),tuple.getSecond());

			GraphEdge ge = GraphElements.createLabeledEdge(originalVertex,inverseVertex,"inverseObjProp",tuple.getSecond());
			graph.addEdge(ge);
		});

		diff.getAxiomDiff(AxiomType.FUNCTIONAL_OBJECT_PROPERTY).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex origin = processExpression(tuple.getFirst().getProperty(),tuple.getSecond());
			processAttribute(origin,tuple.getSecond(),"Functional");
		});
		diff.getAxiomDiff(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex origin = processExpression(tuple.getFirst().getProperty(),tuple.getSecond());
			processAttribute(origin,tuple.getSecond(),"InverseFunctional");
		});
		diff.getAxiomDiff(AxiomType.REFLEXIVE_OBJECT_PROPERTY).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex origin = processExpression(tuple.getFirst().getProperty(),tuple.getSecond());
			processAttribute(origin,tuple.getSecond(),"Reflexive");
		});
		diff.getAxiomDiff(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex origin = processExpression(tuple.getFirst().getProperty(),tuple.getSecond());
			processAttribute(origin,tuple.getSecond(),"Irreflexive");
		});
		diff.getAxiomDiff(AxiomType.SYMMETRIC_OBJECT_PROPERTY).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex origin = processExpression(tuple.getFirst().getProperty(),tuple.getSecond());
			processAttribute(origin,tuple.getSecond(),"Symmetric");
		});
		diff.getAxiomDiff(AxiomType.ASYMMETRIC_OBJECT_PROPERTY).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex origin = processExpression(tuple.getFirst().getProperty(),tuple.getSecond());
			processAttribute(origin,tuple.getSecond(),"Asymmetric");
		});
		diff.getAxiomDiff(AxiomType.TRANSITIVE_OBJECT_PROPERTY).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex origin = processExpression(tuple.getFirst().getProperty(),tuple.getSecond());
			processAttribute(origin,tuple.getSecond(),"Transitive");
		});
	}

	private void processDataPropertyAxioms(){
		diff.getDataDiff().getAllValuesWithDiff().forEach((tuple) -> {

			GraphVertex dataVertex = dataVertices.get(tuple.getFirst().getIRI());
			DiffGroup<OWLClassExpression> domain = diff.getDataDomainDiff(tuple.getFirst());
			DiffGroup<OWLDataRange> range = diff.getDataRangeDiff(tuple.getFirst());

			GraphVertex endDomain = processClassExpressionGroup(domain);
			GraphVertex endRange = processDataRangeGroup(range);

			if(endDomain != null){
				GraphEdge ge = GraphElements.createLabeledEdge(dataVertex,endDomain,"DataPropDomain",domain.getGeneralDiff());
				graph.addEdge(ge);
			}
			if(endRange != null){
				GraphEdge ge = GraphElements.createLabeledEdge(dataVertex,endRange,"DataPropRange",range.getGeneralDiff());
				graph.addEdge(ge);
			}
		});

		diff.getAxiomDiff(AxiomType.SUB_DATA_PROPERTY).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex subDataPropVertex = processExpression(tuple.getFirst().getSubProperty(),tuple.getSecond());
			GraphVertex superDataPropVertex = processExpression(tuple.getFirst().getSuperProperty(),tuple.getSecond());

			GraphEdge ge = GraphElements.createLabeledEdge(subDataPropVertex,superDataPropVertex,"subDataPropOf",tuple.getSecond());
			graph.addEdge(ge);
		});

		diff.getAxiomDiff(AxiomType.EQUIVALENT_DATA_PROPERTIES).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex eqVertex = GraphElements.createEquivalentDataPropVertex(tuple.getSecond());
			graph.addVertex(eqVertex);
			tuple.getFirst().properties()
					.map(dataPropertyExpression -> processExpression(dataPropertyExpression,tuple.getSecond()))
					.forEach(vertex -> connectVertices(eqVertex,vertex,tuple.getSecond()));
		});

		diff.getAxiomDiff(AxiomType.DISJOINT_DATA_PROPERTIES).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex disjointVertex = GraphElements.createDisjointDataPropVertex(tuple.getSecond());
			graph.addVertex(disjointVertex);
			tuple.getFirst().properties()
					.map(dataPropertyExpression -> processExpression(dataPropertyExpression,tuple.getSecond()))
					.forEach((vertex -> connectVertices(disjointVertex,vertex,tuple.getSecond())));
		});

		diff.getAxiomDiff(AxiomType.FUNCTIONAL_DATA_PROPERTY).getAllValuesWithDiff()
				.forEach(tuple -> {
					GraphVertex origin = processExpression(tuple.getFirst().getProperty(),tuple.getSecond());
					processAttribute(origin,tuple.getSecond(),"Functional");
				});
	}

	private void processAnnotationsAxioms(){

		diff.getAnnotationsDiff().getAllValuesWithDiff().forEach(tuple -> {

			GraphVertex annVertex = annotationVertices.get(tuple.getFirst().getIRI());
			DiffGroup<IRI> domain = diff.getAnnotationDomainDiff(tuple.getFirst());
			DiffGroup<IRI> range = diff.getAnnotationRangeDiff(tuple.getFirst());

			GraphVertex endDomain = processIRIGroup(domain);
			GraphVertex endRange = processIRIGroup(range);

			if(endDomain != null){
				GraphEdge ge = GraphElements.createLabeledEdge(annVertex,endDomain,"AnnPropDomain",domain.getGeneralDiff());
				graph.addEdge(ge);
			}
			if(endRange != null){
				GraphEdge ge = GraphElements.createLabeledEdge(annVertex,endRange,"AnnPropRange",range.getGeneralDiff());
				graph.addEdge(ge);
			}
		});

		diff.getAxiomDiff(AxiomType.SUB_ANNOTATION_PROPERTY_OF).getAllValuesWithDiff().forEach(tuple -> {
			GraphVertex subAnnPropVertex = annotationVertices.get(tuple.getFirst().getSubProperty().getIRI());
			GraphVertex superAnnPropVertex = annotationVertices.get(tuple.getFirst().getSuperProperty().getIRI());

			GraphEdge ge = GraphElements.createLabeledEdge(subAnnPropVertex,superAnnPropVertex,"subAnnPropOf",tuple.getSecond());
			graph.addEdge(ge);
		});
	}

	private void processAttribute(GraphVertex originVertex, GroupDiffType diffType, String label){
		GraphVertex attributeVertex = GraphElements.createAttributeVertex(label, diffType);
		GraphEdge ge = GraphElements.createUnlabeledEdge(originVertex,attributeVertex,diffType);
		graph.addVertex(attributeVertex);
		graph.addEdge(ge);
	}

	private void connectVertices(GraphVertex origin, GraphVertex end, GroupDiffType diffType){
		GraphEdge edge = GraphElements.createUnlabeledEdge(origin,end,diffType);
		graph.addEdge(edge);
	}

	private GraphVertex processExpression(IsAnonymous expression, GroupDiffType diffType){
		if(expression instanceof OWLClassExpression){
			return processClassExpression((OWLClassExpression) expression,diffType);
		}
		else if(expression instanceof OWLObjectPropertyExpression) {
			return processObjectPropertyExpression((OWLObjectPropertyExpression) expression,diffType);
		}
		else if(expression instanceof OWLDataPropertyExpression){
			return processDataPropertyExpression((OWLDataPropertyExpression) expression,diffType);
		}
		else{
			System.out.println("Unknown expressions");
			return null;
		}
	}

	private GraphVertex processClassExpression(OWLClassExpression classExpression, GroupDiffType diffType){
		GraphVertex vertex;
		if(classExpression.isAnonymous()){
			vertex = GraphElements.createAnonClassVertex(diffType);
			graph.addVertex(vertex);
			owlElementsByVertex.put(vertex,classExpression);
		}
		else{
			vertex = classesVertices.get(classExpression.asOWLClass().getIRI());
		}
		return vertex;
	}

	private GraphVertex processObjectPropertyExpression(OWLObjectPropertyExpression objectPropertyExpression, GroupDiffType diffType){
		GraphVertex vertex;
		if(objectPropertyExpression.isAnonymous()){
			vertex = GraphElements.createAnonObjPropVertex(diffType);
			graph.addVertex(vertex);
			owlElementsByVertex.put(vertex,objectPropertyExpression);
		}
		else{
			vertex = objVertices.get(objectPropertyExpression.asOWLObjectProperty().getIRI());
		}
		return vertex;
	}

	private GraphVertex processDataPropertyExpression(OWLDataPropertyExpression dataPropertyExpression, GroupDiffType diffType){
		GraphVertex vertex;
		if(dataPropertyExpression.isAnonymous()){
			vertex = GraphElements.createAnonDataPropVertex(diffType);
			graph.addVertex(vertex);
			owlElementsByVertex.put(vertex,dataPropertyExpression);
		}
		else{
			vertex = dataVertices.get(dataPropertyExpression.asOWLDataProperty().getIRI());
		}
		return vertex;
	}

	private GraphVertex processClassExpressionGroup(DiffGroup<OWLClassExpression> classExpressions){
		GraphVertex end;
		if(classExpressions.getSize() == 0){
			return null;
		}
		else if(classExpressions.getSize() == 1){

			Tuple<OWLClassExpression,GroupDiffType> tuple = classExpressions.getAllValuesWithDiff().get(0);
			OWLClassExpression classExpression = tuple.getFirst();

			if(classExpression.isAnonymous()){
				end = GraphElements.createAnonClassVertex(tuple.getSecond());
				graph.addVertex(end);
				owlElementsByVertex.put(end,classExpression);
			}
			else{
				end = classesVertices.get(classExpression.asOWLClass().getIRI());
			}

		}
		else{

			end = GraphElements.createUnionVertex();
			classExpressions.getAllValuesWithDiff().forEach(tuple -> {
				GraphVertex gv;
				if(tuple.getFirst().isAnonymous()){
					gv = GraphElements.createAnonClassVertex(tuple.getSecond());
					graph.addVertex(gv);
					owlElementsByVertex.put(gv,tuple.getFirst());
				}
				else{
					gv = classesVertices.get(tuple.getFirst().asOWLClass().getIRI());
				}

				GraphEdge ge = GraphElements.createUnlabeledEdge(end,gv,tuple.getSecond());
				graph.addEdge(ge);
			});

			graph.addVertex(end);

		}

		return end;
	}
	private GraphVertex processDataRangeGroup(DiffGroup<OWLDataRange> dataRanges){
		GraphVertex end;
		if(dataRanges.getSize() == 0){
			return null;
		}
		else if(dataRanges.getSize() == 1){

			Tuple<OWLDataRange,GroupDiffType> tuple = dataRanges.getAllValuesWithDiff().get(0);
			OWLDataRange dataRange = tuple.getFirst();

			if(dataRange.isOWLDatatype()){
				end = GraphElements.createDatatypeVertex(dataRange.asOWLDatatype().getIRI().getShortForm());
			}
			else{
				end = GraphElements.createDatatypeVertex("");
			}
			graph.addVertex(end);
		}
		else{

			end = GraphElements.createUnionVertex();
			dataRanges.getAllValuesWithDiff().forEach(tuple -> {
				GraphVertex gv;
				if(tuple.getFirst().isOWLDatatype()){
					gv = GraphElements.createDatatypeVertex(tuple.getFirst().asOWLDatatype().getIRI().getShortForm());
				}
				else{
					gv = GraphElements.createDatatypeVertex("");
				}
				graph.addVertex(gv);

				GraphEdge ge = GraphElements.createUnlabeledEdge(end,gv,tuple.getSecond());
				graph.addEdge(ge);
			});

			graph.addVertex(end);
		}

		return end;
	}

	private GraphVertex processIRIGroup(DiffGroup<IRI> iris){
		GraphVertex end;
		if(iris.getSize() == 0){
			return null;
		}
		else if(iris.getSize() == 1){

			Tuple<IRI,GroupDiffType> tuple = iris.getAllValuesWithDiff().get(0);

			if(OWL2Datatype.isBuiltIn(tuple.getFirst())){
				end = GraphElements.createDatatypeVertex(tuple.getFirst().getShortForm());
				graph.addVertex(end);
			}
			else{
				end = getVertexByIRI(tuple.getFirst());
			}
		}
		else{
			end = GraphElements.createUnionVertex();
			iris.getAllValuesWithDiff().forEach(tuple -> {
				GraphVertex gv;
				if(OWL2Datatype.isBuiltIn(tuple.getFirst())){
					gv = GraphElements.createDatatypeVertex(tuple.getFirst().getShortForm());
					graph.addVertex(gv);
				}
				else{
					gv = getVertexByIRI(tuple.getFirst());
				}

				GraphEdge ge = GraphElements.createUnlabeledEdge(end,gv,tuple.getSecond());
				graph.addEdge(ge);
			});

			graph.addVertex(end);
		}

		return end;
	}

	private GraphVertex getVertexByIRI(IRI iri){
		GraphVertex gv;

		gv = getOWLClassVertex(iri);
		if(gv != null) return gv;

		gv = getOWLObjPropVertex(iri);
		if(gv != null) return gv;

		gv = getOWLDataPropertyVertex(iri);
		if(gv != null) return gv;

		gv = getOWLAnnotationPropertyVertex(iri);
		if(gv != null) return gv;

		return null;
	}

	public Collection<OWLClass> getClasses(){
		return classes.values();
	}

	public Collection<OWLObjectProperty> getObjectProperties(){
		return objectProperties.values();
	}

	public Collection<OWLDataProperty> getDataProperties(){
		return dataProperties.values();
	}

	public Collection<OWLAnnotationProperty> getAnnotationProperties(){
		return annotationProperties.values();
	}

	public Map<IRI,GraphVertex> getVertices(){ return classesVertices; }

	public Map<GraphVertex,OWLObject> getVertexMap(){ return owlElementsByVertex; }

	public GraphVertex getOWLClassVertex(IRI iri){
		return classesVertices.get(iri);
	}
	public GraphVertex getOWLObjPropVertex(IRI iri){
		return objVertices.get(iri);
	}
	public GraphVertex getOWLAnnotationPropertyVertex(IRI iri){
		return annotationVertices.get(iri);
	}

	public GraphVertex getOWLDataPropertyVertex(IRI iri){
		return dataVertices.get(iri);
	}
	public OWLClass getOWLClass(IRI iri){
		return classes.get(iri);
	}
	public OWLObjectProperty getOWLObjectProperty(IRI iri){
		return objectProperties.get(iri);
	}
	public OWLDataProperty getOWLDataProperty(IRI iri){
		return dataProperties.get(iri);
	}

	public OWLOntology getOntology(){
		return ontology;
	}

	public OntologyDiff getDiff(){
		return diff;
	}
}
