package application.graph.vertex;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class NodeText{
	private String fullText;
	private String shortText;

	private double width;
	private double height;

	private double widthLimit;

	public NodeText(String text, double widthLimit){
		setText(text,widthLimit);
	}

	public String getFullText(){
		return fullText;
	}

	public String getShortText(){
		return shortText;
	}

	public double getWidth(){
		return width;
	}

	public double getHeight(){
		return height;
	}

	public void setText(String text, double widthLimit){
		this.fullText = text;
		this.widthLimit = widthLimit;
		createShortText();
	}

	private void createShortText(){
		Text txt = new Text(fullText);
		txt.applyCss();

		if(txt.getLayoutBounds().getWidth() <= widthLimit){
			shortText = fullText;
		}
		else{
			String tmp = fullText;
			do{
				tmp = tmp.substring(0,tmp.length() - 1);
				txt = new Text(tmp + "...");
				txt.applyCss();
			} while(tmp.length() > 1 && txt.getLayoutBounds().getWidth() > widthLimit);
			shortText = tmp + "...";
		}
		width = txt.getLayoutBounds().getWidth();
		height = txt.getLayoutBounds().getHeight();
	}

	public void draw(GraphicsContext gc, double x, double y){
		gc.setFill(Color.BLACK);
		gc.fillText(shortText,x,y);
	}
}
