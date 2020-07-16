package application.graph;

import application.graph.edge.GraphEdge;
import application.graph.vertex.GraphVertex;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GraphPane extends Pane{

	private final ResizableCanvas canvas = new ResizableCanvas();

	private final List<GraphVertex> graphVertices = new ArrayList<>();
	private final List<GraphEdge> graphEdges = new ArrayList<>();

	public GraphPane(){
		super();

		canvas.widthProperty().bind(widthProperty());
		canvas.heightProperty().bind(heightProperty());
		//GRAY makes the text look really bad
		canvas.getGraphicsContext2D().setFontSmoothingType(FontSmoothingType.LCD);
		canvas.getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
		canvas.getGraphicsContext2D().setTextBaseline(VPos.CENTER);
		canvas.getGraphicsContext2D().setFont(new Font("System",12.0));

		getChildren().add(canvas);
	}

	public void setBackgroundColor(Paint color){
		setBackground(new Background(new BackgroundFill(color,CornerRadii.EMPTY,Insets.EMPTY)));
	}

	public ResizableCanvas getCanvas(){
		return this.canvas;
	}

	public void addVertex(GraphVertex gv){
		this.graphVertices.add(gv);
	}

	public void addAllVertex(Collection<GraphVertex> vertices){
		this.graphVertices.addAll(vertices);
	}

	public void addEdge(GraphEdge ge){
		this.graphEdges.add(ge);
	}

	public void addAllEdges(Collection<GraphEdge> edges){
		this.graphEdges.addAll(edges);
	}

	public List<GraphVertex> getVertices(){
		return this.graphVertices;
	}

	public List<GraphEdge> getEdges(){
		return this.graphEdges;
	}

	public void draw(){
		//System.out.println("ReDrawing");

		IntegerProperty counterV = new SimpleIntegerProperty(0);
		IntegerProperty counterE = new SimpleIntegerProperty(0);

		Rectangle2D visibleArea = canvas.getVisibleArea();

		canvas.clearCanvas();
		graphEdges.stream()
				.filter(edge -> edge.isVisible() && edge.intersects(visibleArea))
				.forEach(edge -> {
					edge.draw(canvas);
					//counterE.set(counterE.get()+1);
				});

		graphVertices.stream()
				.filter(vertex -> vertex.isVisible() && vertex.getShape().intersects(visibleArea))
				.forEach(vertex -> {
					vertex.draw(canvas);
					//counterV.set(counterV.get()+1);
				});


		//System.out.println("-> V: "+counterV.get()+" E: "+counterE.get());
	}

	public void reset(){
		graphEdges.clear();
		graphVertices.clear();

		resetAffine();
	}

	public void resetAffine(){
		canvas.getGraphicsContext2D().setTransform(new Affine());
	}
}
