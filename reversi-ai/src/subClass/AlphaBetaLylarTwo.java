package subClass;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import nuralNetwork.ReversiPerceptron;

public class AlphaBetaLylarTwo {

	public double _getMaxValue(Board board, Piece piece) {

		// 次に置ける場所の中で、もっとも評価の高い場所を探す
		Piece enemy = Piece.opposite(piece);
		Board playBoard1 = board;
		Board playBoard2 = board;
		Board playBoard3 = board;
		Board tryBoard = board;
		ReversiPerceptron reversiPerceptron = new ReversiPerceptron();
		boolean loop1 = true;
		boolean loop2 = true;
		double pervalueThird = -1;
		double pervalueJudge = -1;
		double pervalueSecond = 1;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				// 置けるかどうかを確認し、置けないのなら何もしない(4手目)
				if (!tryBoard.isEnablePosition(i, j, enemy)) {
					continue;
				}
				tryBoard.putPiece(i, j, enemy);

				// 相手が置いた盤面を別変数に格納
				playBoard1 = new Board(tryBoard.getBoard());
				playBoard3 = new Board(tryBoard.getBoard());

				pervalueThird = -1;

				loop1: if (loop1) {
					for (int k = 0; k < 8; k++) {
						for (int l = 0; l < 8; l++) {

							// 置けるかどうかを確認し、置けないのなら何もしない（5手目）
							if (!playBoard1.isEnablePosition(k, l, piece)) {
								continue;
							}
							// 末端
							playBoard1.putPiece(k, l, piece);

							Board boardcal = new Board(playBoard1.getBoard());

							// 自分が置いた後の盤面の評価値をニューラルネットワークにより算出
							pervalueJudge = reversiPerceptron.ReversiPerceptronCreate(boardcal);
							// System.out.println("pervalueJudge"+pervalueJudge);

							if ((pervalueSecond != 10) && (pervalueJudge >= pervalueSecond)) {
								loop1 = false;
								loop2 = false;
								break loop1;
							}
							System.out.println("pervalueThird" + pervalueJudge);

							// より評価値の高い値で更新していく
							if (pervalueThird < pervalueJudge) {
								// System.out.println("pervalueJudge" + pervalueJudge);

								pervalueThird = pervalueJudge;
								// System.out.println("pervalueThird"+pervalueThird);
							}
							playBoard1 = playBoard3;
						}
					}
				}

				loop1 = true;

				// System.out.println("pervalueThird" + pervalueThird);
				// System.out.println("pervalueThird"+pervalueThird);

				if ((pervalueSecond > pervalueThird) && (loop2)) {
					// System.out.println("pervalueThird"+pervalueThird);

					pervalueSecond = pervalueThird;
				}
				// System.out.println("pervalueSecond下段"+pervalueSecond);

				tryBoard = playBoard2;
				System.out.println("pervalueSecond" + pervalueSecond);

			}
		}
		System.out.println("bbb" + pervalueSecond);

		return pervalueSecond;

	}

}
