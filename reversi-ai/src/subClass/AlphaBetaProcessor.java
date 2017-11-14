package subClass;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;
import jp.takedarts.reversi.Processor;

/**
 * Reversi人工知能のサンプルプログラム。
 *
 * @author Nakanishi
 */
public class AlphaBetaProcessor extends Processor {

	// コンストラクタ
	AlphaBeta alphaBeta;
	AlphaBetaLylarTwo alphaBetaLylarTwo;

	public AlphaBetaProcessor() {
		this.alphaBeta = new AlphaBeta();
		this.alphaBetaLylarTwo = new AlphaBetaLylarTwo();
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
		boolean lastWord = false;
		boolean enemyCantPut = true;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				// 置けるかどうかを確認し、置けないのなら何もしない
				if (!board.isEnablePosition(i, j, piece)) {
					continue;
				}

				// 候補手をコンソールに出力
				System.out.println(i + "," + j);

				// 残りの置けるマス目が５マス以下の場合
				if (board.countPiece(piece) + board.countPiece(Piece.opposite(piece)) >= 60) {

					if (board.countPiece(piece) + board.countPiece(Piece.opposite(piece)) >= 62) {

						Board next_board = new Board(board.getBoard());

						next_board.putPiece(i, j, piece);

						Board next_boar1 = new Board(next_board.getBoard());

						for (int k = 0; k < 8; k++) {
							for (int l = 0; l < 8; l++) {
								if (!next_boar1.isEnablePosition(k, l, Piece.opposite(piece))) {
									continue;
								}

								Board next_board2 = new Board(next_board.getBoard());

								next_board2.putPiece(k, l, Piece.opposite(piece));

								enemyCantPut=false;

								Board next_board3 = new Board(next_board2.getBoard());

								int tempMaxTotalCount = 64
										- next_board3.countPiece(Piece.opposite(piece));

								if (finalValue < tempMaxTotalCount) {
									finalValue = tempMaxTotalCount;
									x = i;
									y = j;
								}
							}
						}

						if(enemyCantPut) {

							Board next_board2 = new Board(next_board.getBoard());

							next_board2.putPiece(i, j, piece);

							Board next_board3 = new Board(next_board2.getBoard());

							finalValue = 63
									- next_board3.countPiece(Piece.opposite(piece));
							x = i;
							y = j;
						}

						// 最後の手だったらlogの文言を「評価値」から「駒数」に変える。
						lastWord = true;
					} else {

						// 同じ盤面を表すオブジェクトを作成し、自分の駒を置く(１手目)
						Board next_board = new Board(board.getBoard());

						next_board.putPiece(i, j, piece);

						// 駒を置いた後の盤面に、さらに相手が駒を置いた場合の最大評価値を計算する
						arg = alphaBetaLylarTwo._getMaxValue(next_board, piece);

						System.out.println(arg);
						log(String.format("(%d, %d)に置いた時の評価値は%f", i, j, finalValue));

						// 求めた盤面の最小評価値が最大となる駒の置き場所を保存する
						if (arg > finalValue) {
							x = i;
							y = j;

							finalValue = arg;
						}

					}

				} else {
					// 同じ盤面を表すオブジェクトを作成し、自分の駒を置く(１手目)
					Board next_board = new Board(board.getBoard());

					next_board.putPiece(i, j, piece);

					// 駒を置いた後の盤面に、さらに相手が駒を置いた場合の最大評価値を計算する
					arg = alphaBeta._getMaxValue(next_board, piece);

					System.out.println(arg);
					log(String.format("(%d, %d)に置いた時の評価値は%f", i, j, finalValue));

					// 求めた盤面の最小評価値が最大となる駒の置き場所を保存する
					if (arg > finalValue) {
						x = i;
						y = j;

						finalValue = arg;

					}
				}

			}
		}

		if (lastWord) {
			System.out.println("最多駒数は" + (int) finalValue);
			log(String.format("next -> (%d, %d) : 最多駒数%d", x, y, (int) finalValue));
		} else {
			System.out.println("最評価値は" + finalValue);
			log(String.format("next -> (%d, %d) : 最高評価値%f", x, y, finalValue));
		}

		return new Position(x, y);

	}

	/**
	 * この人工知能の名前を返す。
	 *
	 * @return 人工知能の名前
	 */
	@Override
	public String getName() {
		return "AlphaBeta+ニューラルネットワークv1.1";
	}
}
