package subClass;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;
import jp.takedarts.reversi.Processor;
import nuralNetwork.ReversiPerceptron;

/**
 * Reversi人工知能のサンプルプログラム。
 *
 * @author Atsushi TAKEDA
 */
public class SampleProcessor extends Processor {

	/**
	 * 手番が来たときに、次の手を決定するメソッド。
	 *
	 * @param board
	 *            盤面の状態
	 * @param piece
	 *            自分が打つ駒
	 * @param thinkingTime
	 *            思考時間
	 * @return 次の手を置く場所
	 */
	@Override
	public Position nextPosition(Board board, Piece piece, long thinkingTime) {
		// 次に置ける場所の中で、もっとも評価の高い場所を探す
		int x = -1;
		int y = -1;
		double arg = -1;
		double arg2 = -1;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				// 置けるかどうかを確認し、置けないのなら何もしない
				if (!board.isEnablePosition(i, j, piece)) {
					continue;
				}

				// 候補手をコンソールに出力
				System.out.println(i + "," + j);

				// 同じ盤面を表すオブジェクトを作成し、自分の駒を置く(１手目)
				Board next_board = new Board(board.getBoard());

				next_board.putPiece(i, j, piece);

				// 駒を置いた後の盤面に、さらに相手が駒を置いた場合の最大評価値を計算する
				arg = _getMaxValue(next_board, piece);

				System.out.println(arg);

				// 求めた盤面の最小評価値が最大となる駒の置き場所を保存する
				if (arg > arg2) {
					x = i;
					y = j;

					arg2 = arg;

				}
			}
		}
		// 置く場所をPositionオブジェクトに変換して返す
		return new Position(x, y);
	}

	private double _getMaxValue(Board board, Piece piece) {

		// 次に置ける場所の中で、もっとも評価の高い場所を探す
		Piece enemy = Piece.opposite(piece);
		Board playBoard1 = board;
		Board playBoard2 = board;
		Board tryBoard = board;
		ReversiPerceptron reversiPerceptron = new ReversiPerceptron();
		double pervalue = -1;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				// 相手の候補手を確認し、置ける場合は置いてみる（２手目）
				if (tryBoard.isEnablePosition(i, j, enemy)) {
					tryBoard.putPiece(i, j, enemy);

					// 相手が置いた盤面を別変数に格納
					playBoard1 = tryBoard;

					for (int k = 0; k < 8; k++) {
						for (int l = 0; l < 8; l++) {

							// 自分の候補手を確認し、置ける場合は置いてみる（３手目）
							if (tryBoard.isEnablePosition(k, l, piece)) {
								tryBoard.putPiece(k, l, piece);

								// 自分が置いた盤面の評価値をニューラルネットワークにより算出
								pervalue = reversiPerceptron.ReversiPerceptronCreate(tryBoard);

								// より評価値の高い値で更新していく
								if (pervalue < reversiPerceptron.ReversiPerceptronCreate(tryBoard)) {
									pervalue = reversiPerceptron.ReversiPerceptronCreate(tryBoard);
								}
							}

							tryBoard = playBoard1;
						}
					}

				}

				tryBoard = playBoard2;

			}
		}

		return pervalue;

	}

	/**
	 * この人工知能の名前を返す。
	 *
	 * @return 人工知能の名前
	 */
	@Override
	public String getName() {
		return "AI+MinMax";
	}
}
