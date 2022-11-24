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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("boardVista.fxml"));
		root = loader.load();
		
		BoardController boardController = loader.getController();
		boardController.setParameters(player, color);
		
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setY(100);
		stage.setTitle("Chess");
		stage.show();
	}
	
	public void mostrarNotas(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Notas.fxml"));
		root = loader.load();
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Notas");
		stage.show();
	}
	
	public void endProgram() {
		System.exit(0);
	}
}
