package vista;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

import controlador.AIPlayer;
import controlador.BoardController;
import controlador.HumanPlayer;
import controlador.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;

public class SceneController {
	private Stage stage;
	private Scene scene;
	private Parent root;
	private int color;
	private Player player;
	
	public void white() {
		color = 0;
	}
	
	public void black() {
		color = 1;
	}
	
	public void AI(ActionEvent event) throws IOException {
		player = new AIPlayer(1-color);
		switchToGame(event);
	}
	
	public void Human(ActionEvent event) throws IOException {
		player = new HumanPlayer(1-color);
		switchToGame(event);
	}
	
	public void switchToGame(ActionEvent event) throws IOException {		
		var loader = switchScene(event, "boardVista");
		BoardController boardController = loader.getController();
		boardController.setParameters(player, color);
	}
	
	public void mostrarNotas(ActionEvent event) throws IOException {
		switchScene(event, "Notas");
	}
	
	public void mostrarMenu(ActionEvent event) throws IOException {
		switchScene(event, "menu");
	}
	
	private FXMLLoader switchScene(ActionEvent event, String switch_to) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(switch_to+".fxml"));
		root = loader.load();
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Notas");
		stage.show();
		return loader;
	}
	
	public void endProgram() {
		System.exit(0);
	}
}
