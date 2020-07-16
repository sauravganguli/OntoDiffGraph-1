package application.ui;

import org.semanticweb.owlapi.model.OWLAxiom;

public class AxiomDiffListCellData{

	private OWLAxiom axiom;
	private String cssColorClass;

	public AxiomDiffListCellData(OWLAxiom axiom,String cssColorClass){
		this.axiom = axiom;
		this.cssColorClass = cssColorClass;
	}

	public OWLAxiom getAxiom(){
		return axiom;
	}

	public String getCssColorClass(){
		return cssColorClass;
	}
}
