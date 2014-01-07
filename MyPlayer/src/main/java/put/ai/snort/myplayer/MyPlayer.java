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
	
	private Random random=new Random(0xdeadbeef);
	
    @Override
    public String getName() {
        return "min max";
    }

    public static void main(String args[]) {	
    }
    
    @Override
    public Move nextMove(Board b) {
        
    	//lista dostepnych ruchow
    	List<Move> moves = b.getMovesFor(getColor());

    	//szukanie ruchu o maksymalnej wartosci funkcji oceny wsrod ruchow gracza min
    	int level = 2, //ilosc iteracji
    		tmp, 
    		bestValue = minMove(moves.get(0), b, level);
		
    	//zeby nie wybierac ciagle tego samego ruchu na poczatku gry, brany jest losowy ruch wsrod najlepszych
    	List<Move> bestMoves = new ArrayList<Move>(); 
    	bestMoves.add(moves.get(0));
    	
    	for (int i=1; i<moves.size(); ++i) {
			tmp = minMove(moves.get(i), b, level);
			if(tmp == bestValue)
				bestMoves.add(moves.get(i));
			else if (tmp > bestValue) {
				bestMoves.clear();
				bestMoves.add(moves.get(i));
				bestValue = tmp;
    		}
		}
    	
        return bestMoves.get(random.nextInt(bestMoves.size()));
    }
    
    /** 
     * Funkcja zwraca ocene ruchu gracza min
     */
    public int minMove(Move move, Board board, int level) {
    	Board b = board.clone();
    	b.doMove(move);
    	List<Move> moves = b.getMovesFor(getOpponent(getColor()));
    	
    	//jesli nie ma juz pionkow
    	if (moves.size() == 0)
    		if(this.count(getColor(), board) == 0)
    			return Integer.MIN_VALUE;
    		else
    			return Integer.MAX_VALUE;
    	
    	int tmp, 
    		bestValue;
    	
    	//szukanie minimalnej wartosci wsrod ruchow gracza max
    	if (level > 0) { 
    		bestValue = maxMove(moves.get(0), b, level-1);
    		for (int i=1; i<moves.size(); ++i) {
    			tmp = maxMove(moves.get(i), b, level-1);
    			if (tmp < bestValue) {
        			bestValue = tmp;
        		}
    		}
    	}
    	else { //jesli to juz ostatni poziom przeszukiwania
        	bestValue = this.evaluateMove(moves.get(0), b);
        	for (int i=1; i<moves.size(); ++i) {
        		tmp = this.evaluateMove(moves.get(i), b);
        		if (tmp < bestValue) {
        			bestValue = tmp;
        		}
        	}
    	}
    	return bestValue;
    }
    
    
    /** 
     * Funkcja zwraca ocene ruchu gracza max
     */
    public int maxMove(Move move, Board board, int level) {
    	Board b = board.clone();
    	b.doMove(move);
    	List<Move> moves = b.getMovesFor(getColor());
    	    	
    	int tmp, 
    		bestValue;
    	
    	//jesli nie ma juz pionkow
    	if (moves.size() == 0)
    		if(this.count(getColor(), board) == 0)
    			return Integer.MIN_VALUE;
    		else
    			return Integer.MAX_VALUE;
    	
    	
    	//szukanie maksymalnej wartosci wsrod ruchow gracza min
    	if (level > 0) { 
    		bestValue = minMove(moves.get(0), b, level-1);
    		for (int i=1; i<moves.size(); ++i) {
    			tmp = minMove(moves.get(i), b, level-1);
    			if (tmp > bestValue) {
        			bestValue = tmp;
        		}
    		}
    	}
    	else { //jesli to juz ostatni poziom przeszukiwania
        	bestValue = this.evaluateMove(moves.get(0), b);
        	for (int i=1; i<moves.size(); ++i) {
        		tmp = this.evaluateMove(moves.get(i), b);
        		if (tmp > bestValue) {
        			bestValue = tmp;
        		}
        	}
    	}
    	return bestValue;
       }
    
    /**
     * Zwraca liczbe pionkow na planszy w danym kolorze
     */
    public int count(Color c, Board b) {
    	int amount = 0;
    	int sizeOfBoard = b.getSize();
    	for (int x=0; x<sizeOfBoard; ++x)
    		for (int y=0; y<sizeOfBoard; ++y)
    			if (b.getState(x,y) == c)
    				++amount;
    	return amount;
    }
    
    /** 
     * Funkcja zwraca liczbę zbitych pionow 
     * gracza o okreslonym kolorze w danym ruchu
     */
    public int lostPawns(Move move, Board board, Color color) {
    	
    	int beforeMove = this.count(color, board);
    	
    	Board b = board.clone(); 
    	b.doMove(move);
    	
    	int afterMove = this.count(color, b);
    	
    	return beforeMove - afterMove;
    }
    
    /**
     * Funkcja zwraca roznice pomiedzy iloscia zbitych pionkow
     * przeciwnika i sztucznej inteligencji
     */
    public int evaluateMove(Move move, Board board) {
    	int mySpanked = lostPawns(move, board, getColor());
    	int opponentSpanked = lostPawns(move, board, getOpponent(getColor()));    	
    	return opponentSpanked - mySpanked;
    }
    
}
