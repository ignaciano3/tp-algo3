package modelo;

import java.util.Objects;

public class Move {
	
	public enum Flag {
		NONE,
		EN_PASSANT,
		PAWN_TWO_FORWARD,
		CASTLING_KINGSIDE,
		CASTLING_QUEENSIDE,
		REMOVES_CASTLING,
		PROMOTE_QUEEN,
		PROMOTE_KNIGHT,
		PROMOTE_BISHOP,
		PROMOTE_ROOK
	}
	
	private int pos_from;
	private int pos_to;
	private Flag flag = Flag.NONE;
	
	public Move(int fil_from, int col_from, int fil_to, int col_to) {
		this.pos_from = fil_from + col_from*8;
		this.pos_to = fil_to + col_to*8;
	}

	public Move(int pos_from, int pos_to) {
		this.pos_from = pos_from;
		this.pos_to = pos_to;
	}

	public int getPos_from() {
		return pos_from;
	}

	public int getPos_to() {
		return pos_to;
	}
	
	public int getX_from() {
		return pos_from%8;
	}

	public int getY_from() {
		return (int)pos_from/8;
	}

	public int getX_to() {
		return pos_to%8;
	}

	public int getY_to() {
		return (int)pos_to/8;
	}
	
	@Override
	public String toString() {
		// Para guardar los movimientos
		char colName = (char) (getX_to() + 97);
		return String.valueOf(colName) + (getY_to() + 1) + " ";
	}

	@Override
	public int hashCode() {
		return Objects.hash(pos_from, pos_to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Move other = (Move) obj;
		return pos_from == other.pos_from && pos_to == other.pos_to;
	}

	public Flag getFlag() {
		return flag;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}
	
	
}
