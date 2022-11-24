package vista;

import java.io.FileNotFoundException;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import modelo.Board;
import modelo.Move;
import modelo.Piece;


public class Vista {
	
	private Board b;
	
	private Color colorGuardado;
	private Color negras = Color.web("#769656");
	private Color blancas = Color.web("#eeeed2");
	private Color selfColor = Color.web("#baca44");
	
	private AudioClip moveAudio;
	private AudioClip captureAudio;
	private AudioClip castlingAudio;
	
	private GridPane root;
	private Scene scene;
	
	private Boolean pieceSelected = false;
	private int pieceSelectedPos;
	
	private int rectSelectedPos;
	
	public Vista(Stage stage, Board board) throws FileNotFoundException{
		
		moveAudio = new AudioClip(getClass().getResource("sounds/move-self.mp3").toExternalForm());
		captureAudio = new AudioClip(getClass().getResource("sounds/capture.mp3").toExternalForm());
		castlingAudio = new AudioClip(getClass().getResource("sounds/castling.mp3").toExternalForm()); 
		
		b = board;
		root = new GridPane();
		
		createBoardLayout();
		addPieces();
		
		scene = new Scene(root);
		
		new AudioClip(getClass().getResource("sounds/game_start.mp3").toExternalForm()).play();
		
		stage.setScene(scene);
		stage.setTitle("Chess");
		stage.show();
	}

	

	private void createBoardLayout() {
		root.setPadding(new Insets(10,10,10,10));
		
		Boolean white = true;
        for (int i = 0; i<8; i++) {
        	for (int j = 0; j<8; j++) {
        		var rect = new Rectangle(100,100);
        		if (white) {
        			rect.setFill(negras);
        		} else {
        			rect.setFill(blancas);
        		}
        		int rectSelectedPos = 56-(j*8-i);
        		rect.setId("rect"+rectSelectedPos);
        		// los eventos se los deberia pasar al HumanPlayer
        		rect.setOnMouseClicked(
        				event -> {
        					if (pieceSelected) {
        						pieceSelected = false;
        						Boolean pieceMove = b.makeMove(new Move(pieceSelectedPos, rectSelectedPos));
        						if (pieceMove) handleMove();
        					}
        				});
        		root.add(rect, i, j);
        		white = !white;
        	}
        	white = !white;
        }
	}
	
	private void handleMove() {
		var lastMoveUpdate = b.lastMoveUpdate;
		int typeMove = 0; // 0->normal, 1->capture, 2->castle, 3->check
		for (var move : lastMoveUpdate.lastMoves()) {
			var pos_from = move.getPos_from();
			ImageView movedPiece = (ImageView) root.lookup("#" + pos_from);
			
			var pos_to = move.getPos_to();
			if (root.lookup("#" + pos_to) != null) {
				root.getChildren().remove(root.lookup("#" + pos_to));
				typeMove = 1;
			}
			movedPiece.setId(""+pos_to);
			root.getChildren().remove(movedPiece);
			root.add(movedPiece, pos_to%8, 7-(int)pos_to/8);
		}
		
		if (lastMoveUpdate.flag() != null) {
			typeMove=2;
		}
		
		switch(typeMove) {
		case 0:
			moveAudio.play();
			break;
		case 1:
			captureAudio.play();
			break;
		case 2:
			castlingAudio.play();
			break;
		}
	}


	private void addPieces() throws FileNotFoundException {
		for (int i = 0; i < 64; i++) {
			Piece p = b.getPiece(i);
			if (p == null) {
				continue;
			}
			String url = "pieces/" + p.toString() + ".png";
			var image = new Image(getClass().getResourceAsStream(url));
			var imageView = new ImageView(image);
			setPieceBehaviour(imageView, p);
			root.add(imageView, p.getFil(), 7-p.getCol());
		}
		
	}

	private void setPieceBehaviour(ImageView imageView, Piece p) {
		imageView.setFitHeight(100);
		imageView.setFitWidth(100);
		imageView.setCursor(Cursor.HAND);
		imageView.setCache(true);
		int pos = p.getCol()*8 + p.getFil();
		imageView.setId(""+pos);
		imageView.setOnMouseClicked(
				event -> {
					pieceSelected = true;
					pieceSelectedPos = Integer.parseInt(imageView.getId());
					Rectangle selfRect = (Rectangle) root.lookup("#rect" + pieceSelectedPos);
					colorGuardado =(Color) selfRect.getFill();
					selfRect.setFill(selfColor);
				}
		);
		imageView.setOnMouseReleased(
				event -> {
					Rectangle selfRect = (Rectangle) root.lookup("#rect" + pieceSelectedPos);
					selfRect.setFill(colorGuardado);
				});
		
	}
}
