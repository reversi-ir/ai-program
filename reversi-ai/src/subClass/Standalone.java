package subClass;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;

public class Standalone {

	public static void main(String[] args) {

		Board testBoard = new Board();

		testBoard.putPiece(3, 3, Piece.WHITE);
		testBoard.putPiece(4, 4, Piece.WHITE);

		testBoard.putPiece(3, 4, Piece.BLACK);
		testBoard.putPiece(4, 3, Piece.BLACK);

		//自分(black)　←ここを更新
		PerceptronProcessor myProcessor = new PerceptronProcessor();
		Piece piece = Piece.BLACK;

		//相手(white)　←ここを更新
		TableProcessor opponentProcessor = new TableProcessor();
		Piece opponentPiece = Piece.WHITE;

		System.out.println("AI(BLACK)(自分)：　" + myProcessor.getName());
		System.out.println("AI(WHITE)(相手)：　" + opponentProcessor.getName());
		System.out.println("");

		System.out.println(testBoard);
		System.out.println("");

		while (testBoard.hasEnablePositions(piece) || testBoard.hasEnablePositions(opponentPiece)) {

			//自分の手を置く
			if (testBoard.hasEnablePositions(piece)) {

				Position myPosition = myProcessor.nextPosition(testBoard, piece, 30000);
				testBoard.putPiece(myPosition, piece);

				System.out.println(testBoard);
				System.out.println("");

			} else if (!testBoard.hasEnablePositions(piece)) {

				System.out.println(piece + "：　パス");
				System.out.println("");
			}

			if (testBoard.hasEnablePositions(opponentPiece)) {

				Position opponentPosition = opponentProcessor.nextPosition(testBoard, opponentPiece, 30000);
				testBoard.putPiece(opponentPosition, opponentPiece);
				System.out.println("");

			} else if (!testBoard.hasEnablePositions(opponentPiece)) {

				System.out.println(opponentPiece + "：　パス");
				System.out.println("");

			}

			System.out.println(testBoard);
			System.out.println("");
		}

		System.out.println("黒の数：　" + testBoard.countPiece(piece));
		System.out.println("白の数：　" + testBoard.countPiece(opponentPiece));

	}

}
