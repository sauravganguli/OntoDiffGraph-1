package application.graph.functionality;

import application.controllers.MainWindowController;
import application.diff.GroupDiffType;
import application.graph.GraphPane;
import application.graph.ResizableCanvas;
import application.graph.vertex.GraphVertex;
import application.util.Utils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.semanticweb.owlapi.model.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class VertexClickFunctionality implements GraphFunctionality{

	private final MainWindowController scene;
	private final GraphPane graph;
	private final GridPane elementData;
	private final MouseButton button = MouseButton.PRIMARY;

	private EventHandler<MouseEvent> clickEventHandler;

	public VertexClickFunctionality(MainWindowController scene,GraphPane graph,GridPane elementData){
		this.scene = scene;
		this.graph = graph;
		this.elementData = elementData;

		ColumnConstraints keyColumnConstraints = elementData.getColumnConstraints().get(0);
		keyColumnConstraints.setPrefWidth(Control.USE_COMPUTED_SIZE);
		keyColumnConstraints.hgrowProperty().setValue(Priority.NEVER);
	}

	@Override public void applyFunctionality(){
		ResizableCanvas canvas = graph.getCanvas();

		clickEventHandler = event -> {
			if(event.getButton() != button || scene.getOntologyConverter() == null){
				return;
			}
			Point2D p = canvas.screenCoordToCanvas(event.getX(),event.getY());
			Optional<GraphVertex> clickedNode = graph.getVertices().stream().filter(graphVertex -> graphVertex.getShape().containsPoint(p.getX(),p.getY())).findFirst();

			if(clickedNode.isPresent()){
				OWLObject obj = scene.getOntologyConverter().getVertexMap().get(clickedNode.get());

				clearElementData();
				if(obj != null){

					if(obj instanceof OWLClass){
						displayOWLClass((OWLClass) obj);
					}
					else if(obj instanceof OWLObjectProperty){
						displayOWLObjectProperty((OWLObjectProperty) obj);
					}
					else if(obj instanceof OWLDataProperty){
						displayOWLDataProperty((OWLDataProperty) obj);
					}
					else if(obj instanceof OWLAnnotationProperty){
						displayOWLAnnotationProperty((OWLAnnotationProperty) obj);
					}
					else if(obj instanceof OWLClassExpression){
						displayOWLClassExpression((OWLClassExpression) obj);
					}
					else if(obj instanceof OWLObjectPropertyExpression){
						displayOWLObjectPropertyExpression((OWLObjectPropertyExpression) obj);
					}
					else if(obj instanceof OWLDataPropertyExpression){
						displayOWLDataPropertyExpression((OWLDataPropertyExpression) obj);
					}
					else{
						System.out.println("Unknown node clicked "+obj.toString());
					}

				}

			}
		};

		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,clickEventHandler);
	}

	@Override public void removeFunctionality(){
		graph.getCanvas().removeEventHandler(MouseEvent.MOUSE_PRESSED,clickEventHandler);
	}

	public void clearElementData(){
		elementData.getChildren().clear();
	}

	public void displayOWLClass(OWLClass owlClass){
		final AtomicInteger i = new AtomicInteger(0);

		addRow(i.getAndIncrement(),"Name",owlClass.getIRI().getShortForm());
		addRow(i.getAndIncrement(),"Namespace",owlClass.getIRI().getNamespace());
		addRow(i.getAndIncrement(),"Type",owlClass.getEntityType().toString());

		addRow(i.getAndIncrement(),"",null);
		addRow(i.getAndIncrement(),"Annotations:",null);
		scene.getOntologyConverter().getDiff().getEntityAnnotationDiff(owlClass).getAllValuesWithDiff().forEach(tuple -> {
			addRow(i.getAndIncrement(),tuple.getFirst().getAnnotation().getProperty().toString(),tuple.getFirst().getValue().toString(),tuple.getSecond());
		});
		/*
		addRow(i.getAndIncrement(),"",null);
		addRow(i.getAndIncrement(),"Related Axioms:",null);
		scene.getOntologyConverter().getOntology().axioms(owlClass).forEach(owlClassAxiom -> {
			addRow(i.getAndIncrement(),owlClassAxiom.getAxiomType().toString(),owlClassAxiom.toString());
		});

		scene.getOntologyConverter().getOntology().annotationAssertionAxioms(owlClass.getIRI()).forEach(owlClassAxiom -> {
			addRow(i.getAndIncrement(),owlClassAxiom.getAxiomType().toString(),owlClassAxiom.toString());
		});
		*/
	}
	public void displayOWLObjectProperty(OWLObjectProperty objectProperty){
		int i = 0;

		addRow(i++,"Name",objectProperty.getIRI().getShortForm());
		addRow(i++,"Namespace",objectProperty.getIRI().getNamespace());
		addRow(i++,"Type",objectProperty.getEntityType().toString());
	}
	public void displayOWLDataProperty(OWLDataProperty dataProperty){
		int i = 0;

		addRow(i++,"Name",dataProperty.getIRI().getShortForm());
		addRow(i++,"Namespace",dataProperty.getIRI().getNamespace());
		addRow(i++,"Type",dataProperty.getEntityType().toString());
	}
	public void displayOWLAnnotationProperty(OWLAnnotationProperty annotationProperty){
		int i = 0;

		addRow(i++,"Name",annotationProperty.getIRI().getShortForm());
		addRow(i++,"Namespace",annotationProperty.getIRI().getNamespace());
		addRow(i++,"Type",annotationProperty.getEntityType().toString());
	}

	private void displayOWLClassExpression(OWLClassExpression classExpression){
		int i = 0;

		addRow(i++,"Expression",classExpression.toString());
	}

	private void displayOWLObjectPropertyExpression(OWLObjectPropertyExpression objectPropertyExpression){
		int i = 0;

		addRow(i++,"Expression",objectPropertyExpression.toString());
	}

	private void displayOWLDataPropertyExpression(OWLDataPropertyExpression dataPropertyExpression){
		int i = 0;

		addRow(i++,"Expression",dataPropertyExpression.toString());
	}

	private void addRow(int rowIndex,String key,String value){
		Label keyLabel = new Label(key);
		keyLabel.setMaxWidth(Double.MAX_VALUE);
		keyLabel.setMaxHeight(Double.MAX_VALUE);

		this.elementData.add(keyLabel,0,rowIndex);
		if(value != null){
			Label valueLabel = new Label(value);
			valueLabel.setMaxWidth(Double.MAX_VALUE);
			valueLabel.setMaxHeight(Double.MAX_VALUE);

			valueLabel.setTooltip(new Tooltip(value));
			this.elementData.add(valueLabel,1,rowIndex);
		}
	}
	private void addRow(int rowIndex,String key,String value,GroupDiffType diffType){
		Color color = Utils.getUIColorFromDiff(diffType);
		Label keyLabel = new Label(key);
		keyLabel.setMaxWidth(Double.MAX_VALUE);
		keyLabel.setMaxHeight(Double.MAX_VALUE);

		if(color != null) keyLabel.setBackground(new Background(new BackgroundFill(color,CornerRadii.EMPTY,Insets.EMPTY)));

		this.elementData.add(keyLabel,0,rowIndex);
		if(value != null){
			Label valueLabel = new Label(value);
			valueLabel.setMaxWidth(Double.MAX_VALUE);
			valueLabel.setMaxHeight(Double.MAX_VALUE);

			if(color != null) valueLabel.setBackground(new Background(new BackgroundFill(color,CornerRadii.EMPTY,Insets.EMPTY)));
			valueLabel.setTooltip(new Tooltip(value));
			this.elementData.add(valueLabel,1,rowIndex);
		}
	}

}
