package application.diff;


import org.semanticweb.owlapi.model.*;

import java.util.stream.Collectors;

public class OntologyDiff{

	private final OWLOntology ontology1;
	private final OWLOntology ontology2;

	public OntologyDiff(OWLOntology o1, OWLOntology o2){
		this.ontology1 = o1;
		this.ontology2 = o2;
	}

	//Ontologies
	public OWLOntology getInitialOntology(){
		return ontology1;
	}

	public OWLOntology getFinalOntology(){
		return ontology2;
	}

	public OWLOntology getCorrectElementOntology(GroupDiffType diffType){
		switch(diffType){
			case ADD: return ontology2;
			case REMOVE: return ontology1;
			case UNCHANGED: return ontology1;
			default: return ontology1;
		}
	}

	public DiffGroup<OWLClass> getClassDiff(){
		return new DiffGroup<>(
				ontology1.classesInSignature().collect(Collectors.toList()),
				ontology2.classesInSignature().collect(Collectors.toList()),
				(v1,v2) -> v1.getIRI().equals(v2.getIRI())
		);
	}

	public DiffGroup<OWLObjectProperty> getObjDiff(){
		return new DiffGroup<>(
				ontology1.objectPropertiesInSignature().collect(Collectors.toList()),
				ontology2.objectPropertiesInSignature().collect(Collectors.toList()),
				(v1,v2) -> v1.getIRI().equals(v2.getIRI())
		);
	}

	public DiffGroup<OWLDataProperty> getDataDiff(){
		return new DiffGroup<>(
				ontology1.dataPropertiesInSignature().collect(Collectors.toList()),
				ontology2.dataPropertiesInSignature().collect(Collectors.toList()),
				(v1,v2) -> v1.getIRI().equals(v2.getIRI())
		);
	}

	public DiffGroup<OWLAnnotationProperty> getAnnotationsDiff() {
		return new DiffGroup<>(
				ontology1.annotationPropertiesInSignature().collect(Collectors.toList()),
				ontology2.annotationPropertiesInSignature().collect(Collectors.toList()),
				(v1,v2) -> v1.getIRI().equals(v2.getIRI())
		);
	}

	public DiffGroup<OWLClassExpression> getObjDomainDiff(OWLObjectProperty objectProperty){
		return new DiffGroup<>(
				ontology1.objectPropertyDomainAxioms(objectProperty).map(HasDomain::getDomain).collect(Collectors.toList()),
				ontology2.objectPropertyDomainAxioms(objectProperty).map(HasDomain::getDomain).collect(Collectors.toList()),
				OWLClassExpression::equals
		);
	}

	public DiffGroup<OWLClassExpression> getObjRangeDiff(OWLObjectProperty objectProperty){
		return new DiffGroup<>(
				ontology1.objectPropertyRangeAxioms(objectProperty).map(HasRange::getRange).collect(Collectors.toList()),
				ontology2.objectPropertyRangeAxioms(objectProperty).map(HasRange::getRange).collect(Collectors.toList()),
				OWLClassExpression::equals
		);
	}

	public DiffGroup<OWLClassExpression> getDataDomainDiff(OWLDataProperty dataProperty){
		return new DiffGroup<>(
				ontology1.dataPropertyDomainAxioms(dataProperty).map(HasDomain::getDomain).collect(Collectors.toList()),
				ontology2.dataPropertyDomainAxioms(dataProperty).map(HasDomain::getDomain).collect(Collectors.toList()),
				OWLClassExpression::equals
		);
	}

	public DiffGroup<OWLDataRange> getDataRangeDiff(OWLDataProperty dataProperty){
		return new DiffGroup<>(
				ontology1.dataPropertyRangeAxioms(dataProperty).map(HasRange::getRange).collect(Collectors.toList()),
				ontology2.dataPropertyRangeAxioms(dataProperty).map(HasRange::getRange).collect(Collectors.toList()),
				OWLDataRange::equals
		);
	}

	public DiffGroup<IRI> getAnnotationDomainDiff(OWLAnnotationProperty annotationProperty){
		return new DiffGroup<>(
				ontology1.annotationPropertyDomainAxioms(annotationProperty).map(HasDomain::getDomain).collect(Collectors.toList()),
				ontology2.annotationPropertyDomainAxioms(annotationProperty).map(HasDomain::getDomain).collect(Collectors.toList()),
				IRI::equals
		);
	}

	public DiffGroup<IRI> getAnnotationRangeDiff(OWLAnnotationProperty annotationProperty){
		return new DiffGroup<>(
				ontology1.annotationPropertyRangeAxioms(annotationProperty).map(HasRange::getRange).collect(Collectors.toList()),
				ontology2.annotationPropertyRangeAxioms(annotationProperty).map(HasRange::getRange).collect(Collectors.toList()),
				IRI::equals
		);
	}

	public <T extends OWLAxiom> DiffGroup<T> getAxiomDiff(AxiomType<T> type){
		return new DiffGroup<>(
				ontology1.axioms(type).collect(Collectors.toList()),
				ontology2.axioms(type).collect(Collectors.toList()),
				T::equals
		);
	}

	public DiffGroup<OWLAxiom> getAxiomsDiff(){
		return new DiffGroup<>(
				ontology1.axioms().collect(Collectors.toList()),
				ontology2.axioms().collect(Collectors.toList()),
				OWLAxiom::equals
		);
	}

	public DiffGroup<OWLAnnotationAssertionAxiom> getEntityAnnotationDiff(OWLEntity subject){
		return new DiffGroup<>(
				ontology1.annotationAssertionAxioms(subject.getIRI()).collect(Collectors.toList()),
				ontology2.annotationAssertionAxioms(subject.getIRI()).collect(Collectors.toList()),
				OWLAnnotationAssertionAxiom::equals
		);
	}
}
