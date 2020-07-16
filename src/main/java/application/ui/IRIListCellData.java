package application.ui;

import org.semanticweb.owlapi.model.HasIRI;

public class IRIListCellData{

	private HasIRI value;
	private String cssColorClass;

	public IRIListCellData(HasIRI value,String cssColorClass){
		this.value = value;
		this.cssColorClass = cssColorClass;
	}

	public HasIRI getValue(){
		return value;
	}

	public String getCssColorClass(){
		return cssColorClass;
	}
}
