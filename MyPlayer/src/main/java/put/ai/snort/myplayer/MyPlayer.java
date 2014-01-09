/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.snort.myplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import put.ai.snort.game.Board;
import put.ai.snort.game.Move;
import put.ai.snort.game.Player;

public class MyPlayer extends Player {

	private Random random = new Random(0xdeadbeef);

	private List <Integer> values;

	private int INF = 1000000;
	private int time_stock = 300;
	
	private Color my_color;
	private Color opponent_color;
	
	public MyPlayer() {
		values = new ArrayList <Integer>();
	}

	@Override
	public String getName() {
		return "alfa beta";
	}

	public static void main(String args[]) {
	}

	@Override
	public Move nextMove(Board b) {

		MyPlayerTimer.initTimer(getTime());
		

		int current_level = 1; // glebokosc przegladania
		int result;
		
		MyPlayerResult current_best;

		long iter_start;
		long last_iter_duration = 0;
		
		Move best_move = null;
		/*
		while(MyPlayerTimer.timeLeft() > time_stock) {
			iter_start = MyPlayerTimer.timeLeft();
			result = maxMove(b, current_level, -INF, INF, true);
			last_iter_duration = iter_start - MyPlayerTimer.timeLeft();
			if(current_level <= b.getSize() * b.getSize() && MyPlayerTimer.timeLeft() > time_stock) {
				best_move = getOneOfBest(b, result);
				System.out.print(
						"Finished iteration at level: [" + current_level +
						"] it took: [" + last_iter_duration +
						"]ms result: [" + result +
						"]\n");
			}
			current_level++;
		}
		*/
		
		initColors();
		current_best = alphaBeta(b, 4, -INF, INF, true, my_color);
		
		System.out.print("result: " + current_best.returnValue() + "\n");
		
		return current_best.returnMove();
	}
	
	public void initColors() {
		my_color = getColor();
		opponent_color = getOpponent(my_color);
	}
	
	

	/**
	 * Funkcja zwraca losowy sposrod najlepszych wynikow
	 */
	public Move getOneOfBest(Board b, int result) {
		List<Move> moves = b.getMovesFor(getColor());

		List<Move> bestMoves = new ArrayList<Move>();
		for (int i=0; i<values.size(); ++i) {
			if(values.get(i) == result)
				bestMoves.add(moves.get(i));
		}

		return bestMoves.get(random.nextInt(bestMoves.size()));
	}
	
	public MyPlayerResult alphaBeta(Board board, int level, int alpha, int beta, boolean initial, Color current_color) {

		MyPlayerResult result = new MyPlayerResult(-INF, null);
		MyPlayerResult tmp_result;
		int value;
		
		// jak nie przerwiemy, to nie zdazymy nic zwrocic
		// zwrócona wartość nie ma znaczenia, bo i tak ją odrzucimy
		//if (MyPlayerTimer.timeLeft() < time_stock)
		//	return result;
		
		List<Move> moves = board.getMovesFor(current_color);
		level--;
		
		// jesli nie ma juz pionkow
		if (moves.size() == 0) {
			if (current_color == my_color) result.update(-INF, null);
			else result.update(INF, null);
			return result;
		}
				
		for (int i = 0; i < moves.size(); ++i) {
			Board b = board.clone();
			b.doMove(moves.get(i));
			
			if (level < 1) { // to już ostatni poziom
				value = this.evaluateMove(board, b);
				result.setValue(value);
				return result;
			}
			else { // idziemy dalej
			
				if (current_color == my_color) { // MAX type
					tmp_result = alphaBeta(b, level, alpha, beta, false, opponent_color);
					
					if(tmp_result.returnValue() > alpha) {
						alpha = tmp_result.returnValue();
						result.update(alpha, moves.get(i));
					}
						
				}
				else { // MIN TYPE
					tmp_result = alphaBeta(b, level, alpha, beta, false, my_color);
					
					if(tmp_result.returnValue() < beta) {
						beta = tmp_result.returnValue();
						result.update(beta, moves.get(i));
					}
					
				}
				
				if (alpha >= beta) // odciecie
					return result;
			}
		}
		return result;
	}


	/**
	 * Ruch gracza max
	 */
	public int maxMove(Board board, int level, int alpha, int beta, boolean initial) {
		List<Move> moves = board.getMovesFor(getColor());

		int value;

		// jak nie przerwiemy, to nie zdazymy nic zwrocic
		if(MyPlayerTimer.timeLeft() < time_stock)
			return alpha;

		if(initial == true)
			values.clear();

		// jesli nie ma juz pionkow
		if (moves.size() == 0)
			if (this.count(getColor(), board) == 0) {
				return Integer.MIN_VALUE;
			}
			else {
				return Integer.MAX_VALUE;
			}

		// szukanie maksymalnej wartosci wsrod ruchow gracza min
		if (level > 0) {
			for (int i = 0; i < moves.size(); ++i) {
				Board b = board.clone();
				b.doMove(moves.get(i));

				value = minMove(b, level - 1, alpha, beta);
				alpha = max(value, alpha);

				if(initial == true) {
					this.values.add(value);
				}

				if (alpha >= beta) //odciecie beta
					return beta;
			}
		} else { // jesli to juz ostatni poziom przeszukiwania
			for (int i = 0; i < moves.size(); ++i) {
				Board b = board.clone();
				b.doMove(moves.get(i));

				value = this.evaluateMove(board, b);
				alpha = max(value, alpha);

				if (alpha >= beta)
					return beta;
			}
		}
		return alpha;
	}

	/**
	 * Ruch gracza min
	 */
	public int minMove(Board board, int level, int alpha, int beta) {
		List<Move> moves = board.getMovesFor(getOpponent(getColor()));

		// jak nie przerwiemy, to nie zdazymy nic zwrocic
		if(MyPlayerTimer.timeLeft() < time_stock)
			return beta;

		// jesli nie ma juz pionkow
		if (moves.size() == 0)
			if (this.count(getColor(), board) == 0) {
				return Integer.MIN_VALUE;
			}
			else {
				return Integer.MAX_VALUE;
			}
		int value;

		// szukanie minimalnej wartosci wsrod ruchow gracza max
		if (level > 0) {
			for (int i = 0; i < moves.size(); ++i) {
				Board b = board.clone();
				b.doMove(moves.get(i));

				value = maxMove(b, level - 1, alpha, beta, false);
				beta = min(value, beta);

				if (alpha >= beta)
					return alpha;
			}
		} else { // jesli to juz ostatni poziom przeszukiwania
			for (int i = 0; i < moves.size(); ++i) {
				Board b = board.clone();
				b.doMove(moves.get(i));

				value = this.evaluateMove(board, b);
				beta = min(value, beta);

				if (alpha >= beta)
					return alpha;
			}
		}
		return beta;
	}

	/**
	 * Zwraca liczbe pionkow na planszy w danym kolorze
	 */
	public int count(Color c, Board b) {
		int amount = 0;
		int sizeOfBoard = b.getSize();
		for (int x = 0; x < sizeOfBoard; ++x)
			for (int y = 0; y < sizeOfBoard; ++y)
				if (b.getState(x, y) == c)
					++amount;
		return amount;
	}

	/**
	 * Funkcja zwraca liczbę zbitych pionow gracza o okreslonym kolorze w danym
	 * ruchu
	 */
	public int lostPawns(Board board1, Board board2, Color color) {
		int beforeMove = this.count(color, board1);
		int afterMove = this.count(color, board2);

		return beforeMove - afterMove;
	}

	/**
	 * Funkcja zwraca roznice pomiedzy iloscia zbitych pionkow przeciwnika i
	 * sztucznej inteligencji
	 */
	public int evaluateMove(Board board1, Board board2) {
		int myLost = lostPawns(board1, board2, getColor());
		int opponentLost = lostPawns(board1, board2, getOpponent(getColor()));
		return opponentLost - myLost;
	}

	public int min(int a, int b) {
		if (a < b)
			return a;
		return b;
	}

	public int max(int a, int b) {
		if (a > b)
			return a;
		return b;
	}

}
