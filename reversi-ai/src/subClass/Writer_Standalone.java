package subClass;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;
/**
 * 棋譜用メインクラス
 * @author 1722549
 *
 */
public class Writer_Standalone {
	public static void main(String[] args) {

		try {

			//出力先を作成する
			FileWriter fw = new FileWriter("C:\\ocero\\log\\log.csv", true);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

			

			Board testBoard = new Board();

			testBoard.putPiece(3, 3, Piece.WHITE);
			testBoard.putPiece(4, 4, Piece.WHITE);

			testBoard.putPiece(3, 4, Piece.BLACK);
			testBoard.putPiece(4, 3, Piece.BLACK);

			Board playBoard = new Board(testBoard.getBoard());

			//自分(black)　←ここを更新
			Writer_MonteCalro myProcessor = new Writer_MonteCalro();
			Piece piece = Piece.BLACK;

			//相手(white)　←ここを更新
			Writer_Random opponentProcessor = new Writer_Random();
			Piece opponentPiece = Piece.WHITE;


			for (int test = 1; test <= 1; test++) { //test:対戦回数
				long to; //処理時間を所持
				long time; //実行時間を所持

				System.out.println("test:"+test);
				to = System.currentTimeMillis();

				while (playBoard.hasEnablePositions(piece) || playBoard.hasEnablePositions(opponentPiece)) {

					//自分の手を置く
					if (playBoard.hasEnablePositions(piece)) {

						Position myPosition = myProcessor.nextPosition(playBoard, piece, 30000);
						playBoard.putPiece(myPosition, piece);
						
						//ログ出力
						pw.print("B,");
						pw.print(myPosition.getX()+","+myPosition.getY()+",");
						pw.print(myProcessor.getValue()+",");

					} else if (!playBoard.hasEnablePositions(piece)) {
						//TODO パスの際の挙動あれば追記
					}

					if (playBoard.hasEnablePositions(opponentPiece)) {

						Position opponentPosition = opponentProcessor.nextPosition(playBoard, opponentPiece, 30000);
						playBoard.putPiece(opponentPosition, opponentPiece);
						
						//ログ出力
						pw.print("W,");
						pw.print(opponentPosition.getX()+","+opponentPosition.getY()+",");
						pw.print(myProcessor.getValue()+",");

					} else if (!playBoard.hasEnablePositions(opponentPiece)) {

						//TODO パスの際の挙動あれば追記


					}

				}


				time = System.currentTimeMillis() - to;



				playBoard = new Board(testBoard.getBoard());
				pw.print("\n");
			}

			//ファイルに書き出す
			pw.close();

		} catch (IOException ex) {
			//例外時処理
			ex.printStackTrace();
		}
	}
}
