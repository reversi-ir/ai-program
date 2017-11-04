
import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;
import jp.takedarts.reversi.Processor;
import subClass.AlphaBeta;

/**
 * Reversi人工知能のサンプルプログラム。
 *
 * @author Nakanishi
 */
public class AIProcessor extends Processor {


	// コンストラクタ
	AlphaBeta alphaBeta;

	public AIProcessor() {
		this.alphaBeta = new AlphaBeta();
	}

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
		double arg = -1000;
		double finalValue = -1000;

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
				arg = alphaBeta._getMaxValue(next_board, piece);

				System.out.println(arg);

				// 求めた盤面の最小評価値が最大となる駒の置き場所を保存する
				if (arg > finalValue) {
					x = i;
					y = j;

					finalValue = arg;

				}

				/**
				 * else if((arg<=finalValue)&&(arg == -1)) { x = i; y = j; }
				 */

			}
		}
		System.out.println("最終評価値は" + finalValue);
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
		return "AlphaBeta+ニューラルネットワーク";
	}
}
