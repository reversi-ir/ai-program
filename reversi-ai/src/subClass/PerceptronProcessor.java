package subClass;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;
import jp.takedarts.reversi.Processor;
import nuralNetwork.ReversiPerceptron;

public class PerceptronProcessor extends Processor {

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
		double value = -100;
		double max = -100;
		int x = -1;
		int y = -1;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				// 置けるかどうかを確認し、置けないのなら何もしない
				if (!board.isEnablePosition(i, j, piece)) {
					continue;
				}

				// 同じ盤面を表すオブジェクトを作成し、自分の駒を置く
				Board next_board = new Board(board.getBoard());

				next_board.putPiece(i, j, piece);

				ReversiPerceptron reversiPerceptron =new ReversiPerceptron();

				value=reversiPerceptron.ReversiPerceptronCreate(next_board);

				// 求めた盤面の最小評価値が最大となる駒の置き場所を保存する
				if (value > max) {
					max = value;
					x = i;
					y = j;
				}
			}
		}

		// 置く場所をログに出力
		log(String.format("next -> (%d, %d) : %f", x, y, max));
		System.out.println("評価値：" + max);

		// 置く場所をPositionオブジェクトに変換して返す
		return new Position(x, y);
	}

	/**
	 * この人工知能の名前を返す。
	 *
	 * @return 人工知能の名前
	 */
	@Override
	public String getName() {
		return "ニューラルネットワークによる評価値を使ったプログラム";
	}

}
