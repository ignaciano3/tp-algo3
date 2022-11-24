package controlador;

import java.io.FileNotFoundException;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import modelo.Board;
import modelo.Move;
import modelo.Move.Flag;
import modelo.Piece;
import vista.Vista;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class BoardController {
	
	private int color_self;
	private Player other_player;
	private Board b;
	
	private AudioClip moveAudio;
	private AudioClip captureAudio;
	private AudioClip castlingAudio;
	private AudioClip inCheckAudio;
	private AudioClip checkMateAudio;
	
	private Boolean pieceSelected = false;
	private int pieceSelectedPos;
	private int rectSelectedPos;
	
	@FXML GridPane root;
	@FXML Rectangle rectSelected;
	@FXML Rectangle rectMoved;
	
	public void initialize() throws FileNotFoundException {
		
		new AudioClip(Vista.class.getResource("sounds/game_start.mp3").toExternalForm()).play();
		moveAudio = new AudioClip(Vista.class.getResource("sounds/move-self.mp3").toExternalForm());
		captureAudio = new AudioClip(Vista.class.getResource("sounds/capture.mp3").toExternalForm());
		castlingAudio = new AudioClip(Vista.class.getResource("sounds/castling.mp3").toExternalForm());
		inCheckAudio = new AudioClip(Vista.class.getResource("sounds/in_check.mp3").toExternalForm());
		checkMateAudio = new AudioClip(Vista.class.getResource("sounds/checkmate.mp3").toExternalForm());
		
		b = new Board();
		addPieces();
		
	}
	
	public void setParameters(Player other_player, int color_self) {
		this.other_player = other_player;
		other_player.setBoard(b);
		this.color_self = color_self;
		if (b.colorToMove != color_self) {
			otherPlayerMove();
		}
	}
	
	private void otherPlayerMove() {
		Move other_move = other_player.choseMove();
		if(other_move == null) return;
		int other_pos = other_move.getPos_from();
		ImageView other_piece = (ImageView) root.lookup("#" + other_pos);
		highlightPiece(other_piece);
		Boolean x = b.makeMove(other_move);
		handleMove();
	}
	
	public void dragOverTarget(DragEvent event) {
		event.acceptTransferModes(TransferMode.ANY);
		event.consume();
	}
	
	private void tryMove(InputEvent event) {
		Node target = (Node) event.getTarget();
		String id = target.getId();
		id = id.replace("rect", "");
		rectSelectedPos = Integer.parseInt(id);
		
		if (pieceSelected) {
			pieceSelected = false;
			Boolean pieceMove = b.makeMove(new Move(pieceSelectedPos, rectSelectedPos));
			if (pieceMove) {
				handleMove();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					otherPlayerMove();
					
				}
			}
		}
		event.consume();
	}
	
	public void rectSelected(MouseEvent event) {
		tryMove(event);
	}
	
	public void dragDroppedTarget(DragEvent event) {
		tryMove(event);
	}
	
	private void handleMove() {
		var lastMoveUpdate = b.lastMoveUpdate;
		
		if(lastMoveUpdate.gameStatus() > 0) {
			checkMateAudio.play();
			gameOver(lastMoveUpdate.gameStatus());
		}
		
		int typeMove = 0; // 0->normal, 1->capture, 2->castle, 3->check
		
		int pos_to = 0;
		for (var move : lastMoveUpdate.lastMoves()) {
			var pos_from = move.getPos_from();
			ImageView movedPiece = (ImageView) root.lookup("#" + pos_from);
			
			pos_to = move.getPos_to();
			if (root.lookup("#" + pos_to) != null) {
				root.getChildren().remove(root.lookup("#" + pos_to));
				typeMove = 1;
			}
			movedPiece.setId(""+pos_to);
			GridPane.setRowIndex(movedPiece, 7-(int)pos_to/8);
			GridPane.setColumnIndex(movedPiece, pos_to%8);
			
			rectMoved.setVisible(true);
			GridPane.setRowIndex(rectMoved, 7-(int)pos_to/8);
			GridPane.setColumnIndex(rectMoved, pos_to%8);
		}
		
		if (lastMoveUpdate.flag() != null &&
				lastMoveUpdate.flag() != Flag.PROMOTE_PAWN) {
			typeMove=2;
		}
		
		if (lastMoveUpdate.flag() == Flag.EN_PASSANT) {
			typeMove=1;
		}
		
		if (lastMoveUpdate.flag() == Flag.PROMOTE_PAWN) {
			root.getChildren().remove(root.lookup("#" + pos_to));
			setPiece(b.getPiece(pos_to));
		}
		
		if (lastMoveUpdate.inCheck()) {
			typeMove=3;
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
		case 3:
			inCheckAudio.play();
			break;
		}
		
	}
	
	private void setPiece(Piece p) {
		
		String url = "pieces/" + p.toString() + ".png";
		var image = new Image(Vista.class.getResourceAsStream(url));
		var imageView = new ImageView(image);
		
		imageView.setFitHeight(100);
		imageView.setFitWidth(100);
		imageView.setCursor(Cursor.HAND);
		imageView.setCache(true);
		int pos = p.getCol()*8 + p.getFil();
		imageView.setId(""+pos);
		root.add(imageView, p.getFil(), 7-p.getCol());
		
		imageView.setOnMouseClicked(
				event -> {
					highlightPiece(imageView);
				}
		);
		
		imageView.setOnMouseReleased(event -> rectSelected.setVisible(false));
		
		imageView.setOnDragDetected(
				event -> {
					highlightPiece(imageView);
					
					Dragboard db = imageView.startDragAndDrop(TransferMode.MOVE);
					ClipboardContent content = new ClipboardContent();
					content.putImage(imageView.getImage());
			        db.setContent(content);  
				});
		
		imageView.setOnDragOver(
				event -> event.acceptTransferModes(TransferMode.ANY));
		
		imageView.setOnDragDropped(event ->  tryMove(event));
	}
	
	private void addPieces() throws FileNotFoundException {
		for (int i = 0; i < 64; i++) {
			Piece p = b.getPiece(i);
			if (p == null) {
				continue;
			}
			setPiece(p);
		}
	}
	
	private void highlightPiece(ImageView piece) {
		pieceSelected = true;
		pieceSelectedPos = Integer.parseInt(piece.getId());
		GridPane.setRowIndex(rectSelected, 7-(int)pieceSelectedPos/8);
		GridPane.setColumnIndex(rectSelected, pieceSelectedPos%8);
		rectSelected.setVisible(true);
	}
	
	private void gameOver(int gameStatus) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Game Over");
		
		if (color_self+1 == gameStatus) {
			alert.setContentText("You lose");
		} else if (gameStatus == 3) {
			alert.setContentText("Draw");
		} else {
			alert.setContentText("You win");
		}
		
		alert.showAndWait();
		System.exit(0);
		
	}
	
}
