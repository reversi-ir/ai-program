package subClass;

<<<<<<< HEAD
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
=======
>>>>>>> refs/remotes/origin/master
import java.util.Random;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;
import jp.takedarts.reversi.Processor;

/**
 * Reversi人工知能のサンプルプログラム。
 *
 * @author Atushi TAKEDA
 */
public class MonteCarloProcessor extends Processor {

	/*
	 * 乱数を発生させるオブジェクト。
	 */

	private Random _random = new Random(System.currentTimeMillis());
	Random myRandom = new Random();
	Random opponentRandom = new Random();

	/**
	 * 手番が来たときに、次の手を決定するメソッド。<br>
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

<<<<<<< HEAD
		long to; // 処理時間を所持
		long time; // 実行時間を所持

		to = System.currentTimeMillis();

=======
>>>>>>> refs/remotes/origin/master
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

		// それぞれの手で評価値（勝った回数or自分の石-相手の石の数）を格納
		int[] winCount = new int[count];

		// 相手の石の色
		Piece opponentPiece = Piece.opposite(piece);
<<<<<<< HEAD
		try {

			// 出力先を作成する
			FileOutputStream fos = new FileOutputStream("C:\\Users\\\\1516833\\Desktop\\AI-reversi\\result_monte.csv",
					true);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "SJIS");
			BufferedWriter fw = new BufferedWriter(osw);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
=======
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
			pw.print("自分の駒数(置く前)");
			pw.print(",");
			pw.print("相手の駒数");
			pw.print(",");
			pw.print("次の手(座標)");
			pw.print(",");
			pw.print("次の手の評価値(max)");
			pw.print(",");
			pw.print("処理時間(ms)");
			pw.print(",");
			pw.print("選択出来る手(" + count + "手）");
			// pw.print(",");
			pw.println();
=======
		// 次に置ける場所全ての勝率を求める
		for (int t = 0; t < count; t++) {
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
			// 次に置ける場所全ての勝率を求める
			for (int t = 0; t < count; t++) {
=======
			// 次における場所に置いた想定の盤面
			Board nextBoard = new Board(board.getBoard());
			nextBoard.putPiece(positions[t][0], positions[t][1], piece);
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
				// 次における場所に置いた想定の盤面
				Board nextBoard = new Board(board.getBoard());
				nextBoard.putPiece(positions[t][0], positions[t][1], piece);
=======
			// プレイアウトの結果を保持する盤面
			Board playBoard = new Board(nextBoard.getBoard());
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
				// プレイアウトの結果を保持する盤面
				Board playBoard = new Board(nextBoard.getBoard());
=======
			// //判定フラグ（次駒を置くのが自分か相手か(自分の場合:0 /相手の場合：1)）
			// int playFlag = 1;
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
				// //判定フラグ（次駒を置くのが自分か相手か(自分の場合:0 /相手の場合：1)）
				// int playFlag = 1;
=======
			// 評価値（勝った回数or駒の最終獲得数）
			int value = 0;
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
				// 評価値（勝った回数or駒の最終獲得数）
				int value = 0;
=======
			// 次の一手を置いたと仮定し、その後XX回プレイアウト s:プレイアウト回数
			for (int s = 0; s < 500; s++) {
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
				// 次の一手を置いたと仮定し、その後XX回プレイアウト s:プレイアウト回数
				for (int s = 0; s < 2000; s++) {
=======
				// 1回プレイアウト(ランダム)
				while (playBoard.hasEnablePositions(piece) || playBoard.hasEnablePositions(opponentPiece)) {
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
					// 1回プレイアウト(ランダム)
					while (playBoard.hasEnablePositions(piece) || playBoard.hasEnablePositions(opponentPiece)) {
=======
					// 相手ターン
					if (playBoard.hasEnablePositions(opponentPiece)) {
>>>>>>> refs/remotes/origin/master

<<<<<<< HEAD
						// 相手ターン
						if (playBoard.hasEnablePositions(opponentPiece)) {
=======
						// 次に置ける場所の一覧を探す
						int[][] opponentPositions = new int[64][2];
						int opponentCount = 0;
>>>>>>> refs/remotes/origin/master

						for (int p = 0; p < 8; p++) {
							for (int q = 0; q < 8; q++) {
								if (playBoard.isEnablePosition(p, q, opponentPiece)) {
									opponentPositions[opponentCount][0] = p;
									opponentPositions[opponentCount][1] = q;
									opponentCount++;

								}
							}
						}

<<<<<<< HEAD
						// 自分のターン
						if (playBoard.hasEnablePositions(piece)) {
=======
						// 次に置く場所をランダムに決定する
						int opponentIndex = opponentRandom.nextInt(opponentCount);
>>>>>>> refs/remotes/origin/master

						int a = opponentPositions[opponentIndex][0];
						int b = opponentPositions[opponentIndex][1];

						playBoard.putPiece(a, b, opponentPiece);
					}

					// 自分のターン
					if (playBoard.hasEnablePositions(piece)) {

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
						int myIndex = myRandom.nextInt(myCount);

						int c = myPositions[myIndex][0];
						int d = myPositions[myIndex][1];

						playBoard.putPiece(c, d, piece);

					}

<<<<<<< HEAD
					// プレイアウト後の盤面を基に評価値を更新（ここでは自分の石 - 相手の石の数）
					//value += playBoard.countPiece(piece) - playBoard.countPiece(opponentPiece);

					 int countPiece = playBoard.countPiece(piece);
					 int countOpponentPiece = playBoard.countPiece(opponentPiece);

					// プレイアウト後の盤面を基に評価値を更新（ここでは勝利した回数）
					 if (countPiece > countOpponentPiece) {
					 value += 1;
					 }

					playBoard = new Board(nextBoard.getBoard());

=======
>>>>>>> refs/remotes/origin/master
				}

<<<<<<< HEAD
				// 評価値を格納
				winCount[t] = value;
=======
				// プレイアウト後の盤面を基に評価値を更新（ここでは自分の石 - 相手の石の数）
				// value += playBoard.countPiece(piece) - playBoard.countPiece(opponentPiece);
>>>>>>> refs/remotes/origin/master

				int countPiece = playBoard.countPiece(piece);
				int countOpponentPiece = playBoard.countPiece(opponentPiece);

<<<<<<< HEAD
			// 評価値配列の中の最大値を計算
			int maxValue = 0;
			int maxIndex = 0;
			maxValue = winCount[0];

			for (int k = 0; k < count; k++) {
				if (maxValue < winCount[k]) {
					maxValue = winCount[k];
					maxIndex = k;
=======
				// プレイアウト後の盤面を基に評価値を更新（ここでは勝利した回数）
				if (countPiece > countOpponentPiece) {
					value += 1;
>>>>>>> refs/remotes/origin/master
				}

<<<<<<< HEAD
			// 次に置く場所を評価値から決定する
			int x = positions[maxIndex][0];
			int y = positions[maxIndex][1];

			// ファイルに書き出す

			time = System.currentTimeMillis() - to;

			pw.print(board.countPiece(piece));
			pw.print(",");
			pw.print(board.countPiece(opponentPiece));
			pw.print(",");
			pw.print("\"(" + x + "," + y + ")\"");
			pw.print(",");
			pw.print(maxValue);
			pw.print(",");
			pw.print(time);
			pw.print(",");

			for (int r = 0; r < count; r++) {

				pw.print("\"(" + positions[r][0] + "," + positions[r][1] + ")\"" + "：" + winCount[r]);
				if (r != count - 1) {
					pw.print(",");
				}
=======
				playBoard = new Board(nextBoard.getBoard());
>>>>>>> refs/remotes/origin/master

			}

			// 評価値を格納
			winCount[t] = value;

<<<<<<< HEAD
			// 置く場所をログに出力
			log(String.format("next -> (%d, %d)", x, y));
			log(String.format("評価値 -> %d", maxValue));

			// System.out.println("評価値：" + maxValue);

			// 置く場所をPositionオブジェクトに変換して返す
			return new Position(x, y);

		} catch (IOException ex) {
			// 例外時処理
			ex.printStackTrace();

			// 仮に定義
			int x = 0;
			int y = 0;

			// 置く場所をPositionオブジェクトに変換して返す
			return new Position(x, y);
=======
>>>>>>> refs/remotes/origin/master
		}

		// 評価値配列の中の最大値を計算
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

		// System.out.println("評価値：" + maxValue);

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
		return "原始モンテカルロ法プログラム";
	}

}