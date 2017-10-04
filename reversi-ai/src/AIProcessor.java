import java.util.Random;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;
import jp.takedarts.reversi.Processor;

/**
 * Reversi 原始モンテカルロ法プログラム
 *
 * @author IR 若手B-Team
 */
public class AIProcessor
		extends Processor {

	/*
	 * 乱数を発生させるオブジェクト。
	 */

	private Random _random = new Random(System.currentTimeMillis());

	/**
	 * 手番が来たときに、次の手を決定するメソッド。<br>
	 *
	 * @param board 盤面の状態
	 * @param piece 自分が打つ駒
	 * @param thinkingTime 思考時間
	 * @return 次の手を置く場所
	 */
	@Override

	public Position nextPosition(Board board, Piece piece, long thinkingTime) {

		// 次に置ける場所の一覧を探す
		int[][] positions = new int[64][2];
		int count = 0;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board.isEnablePosition(i, j, piece)) {
					positions[count][0] = i;
					positions[count][1] = j;
					count++;
				}
			}
		}

		//それぞれの手で評価値（勝った回数or自分の石-相手の石の数）を格納
		int[] winCount = new int[count];

		//相手の石の色
		Piece opponentPiece = Piece.opposite(piece);

		//次に置ける場所全ての勝率を求める
		for (int t = 0; t < count; t++) {

			//次における場所に置いた想定の盤面
			Board nextBoard = new Board(board.getBoard());
			nextBoard.putPiece(positions[t][0], positions[t][1], piece);

			//プレイアウトの結果を保持する盤面
			Board playBoard = new Board(nextBoard.getBoard());

			//			//判定フラグ（次駒を置くのが自分か相手か(自分の場合:0 /相手の場合：1)）
			//			int playFlag = 1;

			//評価値（勝った回数or駒の最終獲得数）
			int value = 0;

			//次の一手を置いたと仮定し、その後1000回プレイアウト
			for (int s = 0; s < 1000; s++) {

				//1回プレイアウト(ランダム)
				while (playBoard.hasEnablePositions(piece) || playBoard.hasEnablePositions(opponentPiece)) {

					//相手ターン
					if (playBoard.hasEnablePositions(opponentPiece)) {

						// 次に置ける場所の一覧を探す
						int[][] opponentPositions = new int[64][2];
						int opponentCount = 0;

						for (int p = 0; p < 8; p++) {
							for (int q = 0; q < 8; q++) {
								if (playBoard.isEnablePosition(p, q, opponentPiece)) {
									opponentPositions[opponentCount][0] = p;
									opponentPositions[opponentCount][1] = q;
									opponentCount++;

								}
							}
						}

						// 次に置く場所をランダムに決定する
						Random opponentRandom = new Random();
						int opponentIndex = opponentRandom.nextInt(opponentCount);

						int a = opponentPositions[opponentIndex][0];
						int b = opponentPositions[opponentIndex][1];

						playBoard.putPiece(a, b, opponentPiece);
					} else {

						//自分のターン

						// 次に置ける場所の一覧を探す
						int[][] myPositions = new int[64][2];
						int myCount = 0;

						for (int n = 0; n < 8; n++) {
							for (int m = 0; m < 8; m++) {
								if (playBoard.isEnablePosition(n, m, piece)) {
									myPositions[myCount][0] = n;
									myPositions[myCount][1] = m;
									myCount++;

								}
							}
						}

						// 次に置く場所をランダムに決定する
						Random myRandom = new Random();

						int myIndex = myRandom.nextInt(myCount);

						int c = myPositions[myIndex][0];
						int d = myPositions[myIndex][1];

						playBoard.putPiece(c, d, piece);


					}


				}

				//プレイアウト後の盤面を基に評価値を更新（ここでは自分の石 - 相手の石の数）
				//	    		value += playBoard.countPiece(piece) - playBoard.countPiece(opponentPiece);

				int countPiece = playBoard.countPiece(piece);
				int countOpponentPiece = playBoard.countPiece(opponentPiece);

				//プレイアウト後の盤面を基に評価値を更新（ここでは勝利した回数）
				if (countPiece > countOpponentPiece) {
					value += 1;
				}
				//	    		System.out.println(s);
				//	    		System.out.println(value);

				playBoard = new Board(nextBoard.getBoard());

			}
//			System.out.println(value);

			winCount[t] = value;

			//			System.out.println(winCount);

		}

		//評価値配列の中の最大値を計算
		int maxValue = 0;
		int maxIndex = 0;
		maxValue = winCount[0];

		for (int k = 0; k < count; k++) {
			if (maxValue < winCount[k]) {
				maxValue = winCount[k];
				maxIndex = k;
			}
		}

		// 次に置く場所を評価値から決定する
		int x = positions[maxIndex][0];
		int y = positions[maxIndex][1];

		// 置く場所をログに出力
		log(String.format("next -> (%d, %d)", x, y));
		log(String.format("評価値 -> %d", maxValue));

		System.out.println("評価値：" + maxValue);

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
		return "チームB MasterProgram";
	}

}
