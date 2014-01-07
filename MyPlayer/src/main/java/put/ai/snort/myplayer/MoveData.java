package put.ai.snort.myplayer;

import put.ai.snort.game.Move;

public class MoveData {
		
	private Move bestMove;
	
	private int bestValue;
	
	public Move getBestMove() {
		return this.bestMove;
	}
	
	public int getBestValue() {
		return this.bestValue;
	}

	public MoveData(int _bestValue, Move _bestMove) {
		this.bestMove = _bestMove;
		this.bestValue = _bestValue;
	}
	
}
