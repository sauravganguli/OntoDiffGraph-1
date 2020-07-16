package application.owlgraph.diff;

import application.graph.ResizableCanvas;
import application.graph.shape.GraphShape;
import application.graph.vertex.GraphVertex;

public class DiffVertex<T extends GraphShape> extends GraphVertex {

	private T borderShape;

	public DiffVertex(T shape, T borderShape, int borderSize){
		super(shape);

		this.borderShape = borderShape;
		shape.setPosition(
				borderShape.getX()+borderSize/2.0,
				borderShape.getY()+borderSize/2.0
		);
	}
	public DiffVertex(T shape, T borderShape, String label, int borderSize){
		super(shape,label);

		this.borderShape = borderShape;
		shape.setPosition(
				borderShape.getX()+borderSize/2.0,
				borderShape.getY()+borderSize/2.0
		);
	}

	@Override public void move(double x, double y){
		borderShape.movePosition(x,y);
		super.move(x,y);
	}

	@Override public GraphShape getShape(){
		return borderShape;
	}

	@Override public void draw(ResizableCanvas canvas){
		borderShape.draw(canvas);
		super.draw(canvas);
	}


}
