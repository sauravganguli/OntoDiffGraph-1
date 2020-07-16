package application;

import application.controllers.MainWindowController;
import application.util.Vars;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main extends Application{

	@Override public void start(Stage primaryStage) throws Exception{
		primaryStage.setTitle(Vars.TITLE);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainWindow.fxml"));
		Parent root = loader.load();
		MainWindowController controller = loader.getController();

		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		controller.afterInitialize();
	}

	public static void main(String[] args){
		//System.setProperty("javafx.pulseLogger", "true");
		//System.setProperty("prism.verbose", "true");
		//System.setProperty("prism.forceGPU", "true");
		//System.setProperty("prism.debug", "true");
		//System.setProperty("quantum.verbose", "true");
		//System.setProperty("quantum.debug", "true");

		launch(args);
	}
}
