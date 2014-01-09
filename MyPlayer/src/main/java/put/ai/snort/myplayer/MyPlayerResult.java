package put.ai.snort.myplayer;
import put.ai.snort.game.Move;

public class MyPlayerResult {
	private Move move;
	private int value;
	
	public MyPlayerResult(int value, Move move) {
		this.move = move;
		this.value = value;
	}
	
	public int returnValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public Move returnMove() {
		return this.move;
	}
	
	public void setMove(Move move) {
		this.move = move;
	}
	
	public void update(int value, Move move) {
		this.move = move;
		this.value = value;
	}

}
