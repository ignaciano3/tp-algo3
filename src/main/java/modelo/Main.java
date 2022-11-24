package modelo;


import modelo.Move.Flag;

public class Main {

	public static void main(String[] args) {
		
		Board b = new Board("3kq3/8/8/8/8/8/8/2QK4 w - - 0 1");
		
		
		System.out.print(b.toString());
		
	}
}
