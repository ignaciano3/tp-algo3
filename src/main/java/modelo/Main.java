package modelo;


import modelo.Move.Flag;

public class Main {

	public static void main(String[] args) {
		
		Board b = new Board("3kq3/8/8/8/8/8/8/2QK4 w - - 0 1");
		b.makeMove(new Move(2,0, 3,1));
		
		b.makeMove(new Move(3,7, 2,7));
		b.makeMove(new Move(3,1, 4,1));
		
		b.makeMove(new Move(4,7, 3,6));
		System.out.println(b.in_check);
		
		
		
		System.out.print(b.toString());
		
	}
}
