package application.graph.shape;

import application.graph.ResizableCanvas;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public abstract class GraphShape {

	protected Paint fillColor = Color.WHITE;
	protected Paint strokeColor = Color.BLACK;

	protected double x = 0.0;
	protected double y = 0.0;

	public void setPosition(double x, double y){
		this.x = x;
		this.y = y;
	}

	public void movePosition(double x, double y){
		this.x += x;
		this.y += y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setFillColor(Paint color){
		this.fillColor = color;
	}

	public void setStrokeColor(Paint color){
		this.strokeColor = color;
	}

	public Paint getFillColor(){
		return fillColor;
	}

	public Paint getStrokeColor(){
		return strokeColor;
	}

	public abstract double getLabelWidthLimit();
	public abstract Point2D getCenter();
	public abstract boolean containsPoint(double x,double y);
	public abstract boolean intersects(Rectangle2D rectangle);
	public abstract Point2D getPointIntersection(Point2D from);
	public abstract void draw(ResizableCanvas canvas);
}
