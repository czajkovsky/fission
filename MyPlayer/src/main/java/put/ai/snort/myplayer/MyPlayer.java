/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package put.ai.snort.myplayer;

import java.util.List;

import put.ai.snort.game.Board;
import put.ai.snort.game.Move;
import put.ai.snort.game.Player;

public class MyPlayer extends Player {

        private int INF = 1000000;
        private int time_stock = 300;
        
        private Color my_color;
        private Color opponent_color;

        @Override
        public String getName() {
                return "Mateusz Czajka 106596, Agnieszka Czechumska 106594 (alfa beta z iteracyjnym pogłębianiem)";
        }

        public static void main(String args[]) {
        }

        @Override
        public Move nextMove(Board b) {

                MyPlayerTimer.initTimer(getTime());
                initColors();
                
                int current_level = 1; // glebokosc przegladania     
                int board_size = b.getSize() * b.getSize();
                
                MyPlayerResult tmp_result, final_result = new MyPlayerResult(-INF, null);

                while(MyPlayerTimer.timeLeft() > time_stock) {
                        tmp_result = alphaBeta(b, current_level, -INF, INF, my_color);
                        if(current_level <= board_size && MyPlayerTimer.timeLeft() > time_stock) {
                                final_result = tmp_result;
                                if (final_result.returnValue() > board_size) 
                                        return final_result.returnMove();                             
                        }
                        current_level++;
                }
                return final_result.returnMove();
        }
        
        public void initColors() {
                my_color = getColor();
                opponent_color = getOpponent(my_color);
        }

        public MyPlayerResult alphaBeta(Board board, int level, int alpha, int beta, Color current_color) {

                MyPlayerResult result = new MyPlayerResult(INF - 1, null);
                
                // jak nie przerwiemy, to nie zdazymy nic zwrocic
                // zwrócona wartość nie ma znaczenia, bo i tak ją odrzucimy
                if (MyPlayerTimer.timeLeft() < time_stock)
                        return result;
                
                if (current_color == my_color)
                        result.setValue(-INF + 1);
                else
                        result.setValue(INF - 1);
                
                List<Move> moves = board.getMovesFor(current_color);
                
                // jesli nie ma juz pionkow
                if (moves.size() == 0) {
                        return result;
                }
                
                MyPlayerResult tmp_result;
                int value;
                level--;
                
                for (int i = 0; i < moves.size(); ++i) {
                        Board b = board.clone();
                        b.doMove(moves.get(i));
                        
                        if (level < 1) { // to już ostatni poziom
                                value = this.evaluateSituation(b);
                                result.update(value, moves.get(i));
                                return result;
                        }
                        else { // idziemy dalej
                        
                                if (current_color == my_color) { // MAX type
                                        tmp_result = alphaBeta(b, level, alpha, beta, opponent_color);
                                        
                                        if(tmp_result.returnValue() > alpha) {
                                                alpha = tmp_result.returnValue();
                                                result.update(alpha, moves.get(i));
                                        }
                                                
                                }
                                else { // MIN TYPE
                                        tmp_result = alphaBeta(b, level, alpha, beta, my_color);
                                        
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
         * Funkcja zwraca roznice pomiedzy iloscia zbitych pionkow przeciwnika i
         * sztucznej inteligencji
         */
        public int evaluateSituation(Board board) {
                int my = count(my_color, board);
                int opponent = count(opponent_color, board);
                return my - opponent;
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