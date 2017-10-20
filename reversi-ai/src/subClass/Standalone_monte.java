package subClass;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;

/**
 * モンテカルロ法の検証用ツール
 * （出力結果(黒の駒数、白の駒数、処理時間)をcsvファイルへ書き出し）
 * @author 1517643
 *
 */
public class Standalone_monte {

	public static void main(String[] args) {

		try {

			//出力先を作成する
			FileOutputStream fos = new FileOutputStream("C:\\tmp\\result.csv",false);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "SJIS");
			BufferedWriter fw = new BufferedWriter(osw);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			FileOutputStream fos_board = new FileOutputStream("C:\\tmp\\resultBoard.csv",false);
			OutputStreamWriter osw_board = new OutputStreamWriter(fos_board, "SJIS");
			BufferedWriter fw_board = new BufferedWriter(osw_board);
			PrintWriter pw_board = new PrintWriter(new BufferedWriter(fw_board));

			Board testBoard = new Board();

			testBoard.putPiece(3, 3, Piece.WHITE);
			testBoard.putPiece(4, 4, Piece.WHITE);

			testBoard.putPiece(3, 4, Piece.BLACK);
			testBoard.putPiece(4, 3, Piece.BLACK);

			Board playBoard = new Board(testBoard.getBoard());


			//black　←ここを更新
			MinMaxProcessor opponentProcessor = new MinMaxProcessor();
			Piece piece = Piece.BLACK;

			//white　←ここを更新
			MonteCarloProcessor myProcessor = new MonteCarloProcessor();
			Piece opponentPiece = Piece.WHITE;

			//System.out.println("AI(BLACK)(自分)：　" + myProcessor.getName());
			//System.out.println("AI(WHITE)(相手)：　" + opponentProcessor.getName());
			//System.out.println("");

			//System.out.println(testBoard);
			//System.out.println("");

			pw.print("AI(BLACK)：");
			pw.print(",");
			pw.print(opponentProcessor.getName());
			pw.println();

			pw.print("AI(WHITE)：");
			pw.print(",");
			pw.print(myProcessor.getName());
			pw.println();

			pw.println();

			pw.print("No");
			pw.print(",");
			pw.print("黒の駒数");
			pw.print(",");
			pw.print("白の駒数");
			pw.print(",");
			pw.print("処理時間(ms)");
			pw.println();

//			pw_board.print(testBoard);
//			pw_board.println();

			for (int test = 1; test <= 100; test++) { //test:対戦回数

				long to; //処理時間を所持
				long time; //実行時間を所持

				System.out.println(test+"回目" );

				to = System.currentTimeMillis();

				while (playBoard.hasEnablePositions(piece) || playBoard.hasEnablePositions(opponentPiece)) {

					//自分の手を置く
					if (playBoard.hasEnablePositions(piece)) {

						Position myPosition = myProcessor.nextPosition(playBoard, piece, 30000);

						playBoard.putPiece(myPosition, piece);

						//System.out.println(playBoard);
						//System.out.println("");

					} else if (!playBoard.hasEnablePositions(piece)) {

						System.out.println(piece + "：　パス");
						System.out.println("");
					}

					if (playBoard.hasEnablePositions(opponentPiece)) {

						Position opponentPosition = opponentProcessor.nextPosition(playBoard, opponentPiece, 30000);
						playBoard.putPiece(opponentPosition, opponentPiece);
						//System.out.println("");

					} else if (!playBoard.hasEnablePositions(opponentPiece)) {

						System.out.println(opponentPiece + "：　パス");
						System.out.println("");

					}

					//System.out.println(playBoard);
					//System.out.println("");

				}

				//System.out.println("黒の数：　" + playBoard.countPiece(piece));
				//System.out.println("白の数：　" + playBoard.countPiece(opponentPiece));

				time = System.currentTimeMillis() - to;

				pw.print(test);
				pw.print(",");
				pw.print(playBoard.countPiece(piece));
				pw.print(",");
				pw.print(playBoard.countPiece(opponentPiece));
				pw.print(",");
				pw.print(time);
				pw.println();


				pw_board.print(test);
				pw_board.println();
				pw_board.print(playBoard);
				pw_board.println();

				playBoard = new Board(testBoard.getBoard());

			}

			//ファイルに書き出す
			pw.close();
			pw_board.close();

		} catch (IOException ex) {
			//例外時処理
			ex.printStackTrace();
		}

	}

}