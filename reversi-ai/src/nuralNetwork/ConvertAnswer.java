package nuralNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;
import subClass.RandomProcessor;

public class ConvertAnswer {

	public static void main(String[] args) {
		new ConvertAnswer();
	}

	public ConvertAnswer() {
		// 入力データ配列 x =(x軸,y軸)の配列と,正解データ配列 answer

		String[] csvAll;
		List<Integer> xPosition = new ArrayList<Integer>();
		List<Integer> yPosition = new ArrayList<Integer>();
		List<String> color = new ArrayList<String>();
		List<Double> answer = new ArrayList<Double>();

		// データの変換
		try {

			// 教師データの指定
			String answerFileName = "C:/Users/kamat/Desktop/GGFConvert/test.ggf.csv";
			// String answerFileName =
			// "C:/Users/kamat/Desktop/GGFConvert/Othello.latest.278042.ggf.csv";

			// 教師データ読み込み
			FileReader fr = new FileReader(answerFileName);
			BufferedReader br = new BufferedReader(fr);

			// 読み込んだファイルを１行ずつ処理する
			String line;
			int fileRowNum = 0;
			FileWriter fw = null;
			// fw = new
			// FileWriter("C:/Users/kamat/Desktop/GGFConvert/teacher_278042_ver2.csv",
			// true);
			fw = new FileWriter("C:/Users/kamat/Desktop/GGFConvert/test_teacher.csv", true);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			while ((line = br.readLine()) != null) {

				fileRowNum = +fileRowNum + 1;
				System.out.println(String.format("[RowNum] %d", fileRowNum));

				// 区切り文字","で分割する
				csvAll = line.split(",", 0); // 行をカンマ区切りで配列に変換

				if (csvAll[0].equals("B")) {

					for (int i = 0; i < csvAll.length; i += 4) {

						// 1手ずつ情報を配列へ格納していく
						color.add(csvAll[i]);
						xPosition.add(Integer.parseInt(csvAll[i + 1].replace("[", "")) - 1);
						yPosition.add(Integer.parseInt(csvAll[i + 2].replace("]", "").trim()) - 1);
						answer.add(Double.parseDouble(csvAll[i + 3]));

					}

					// 初期盤面の作成
					Board testBoard = new Board();

					// 評価値（駒の最終獲得数）
					double winCount = 0;

					// 自分(black) ←ここを更新
					RandomProcessor myProcessor = new RandomProcessor();
					Piece piece = Piece.BLACK;

					// 相手(white) ←ここを更新
					RandomProcessor opponentProcessor = new RandomProcessor();
					Piece opponentPiece = Piece.WHITE;

					Board playBoard = new Board();

					// 最終局面まで進める
					for (int num = 0; num < answer.size(); num++) {

						// 配列に格納した座標を盤面にセット
						if (color.get(num).equals("B")) {
							testBoard.putPiece(xPosition.get(num), yPosition.get(num), Piece.BLACK);
							playBoard.putPiece(xPosition.get(num), yPosition.get(num), Piece.BLACK);

							// 1000回プレイアウト
							for (int s = 0; s < 1000; s++) {

								while (playBoard.hasEnablePositions(piece)
										|| playBoard.hasEnablePositions(opponentPiece)) {

									// 自分の手を置く
									if (playBoard.hasEnablePositions(piece)) {

										Position myPosition = myProcessor.nextPosition(playBoard, piece, 30000);
										playBoard.putPiece(myPosition, piece);

									} else if (!playBoard.hasEnablePositions(piece)) {

										// TODO パスの際の挙動あれば追記
									}

									if (playBoard.hasEnablePositions(opponentPiece)) {

										Position opponentPosition = opponentProcessor.nextPosition(playBoard,
												opponentPiece, 30000);
										playBoard.putPiece(opponentPosition, opponentPiece);

									} else if (!playBoard.hasEnablePositions(opponentPiece)) {

										// TODO パスの際の挙動あれば追記

									}

								}

								// 黒が勝った場合
								if (playBoard.countPiece(Piece.BLACK) > playBoard.countPiece(Piece.WHITE)) {
									winCount = winCount + 1;

								}

								// 盤面リセット
								playBoard = new Board(testBoard.getBoard());

							}

							// 評価値の設定
							answer.set(num, (double) winCount / 1000);
							playBoard = new Board(testBoard.getBoard());
							winCount = 0;

						} else {
							testBoard.putPiece(xPosition.get(num), yPosition.get(num), Piece.WHITE);
							playBoard.putPiece(xPosition.get(num), yPosition.get(num), Piece.WHITE);

							// 1000回プレイアウト
							for (int s = 0; s < 1000; s++) {

								while (playBoard.hasEnablePositions(piece)
										|| playBoard.hasEnablePositions(opponentPiece)) {

									// 自分の手を置く
									if (playBoard.hasEnablePositions(piece)) {

										Position myPosition = myProcessor.nextPosition(playBoard, piece, 30000);
										playBoard.putPiece(myPosition, piece);

									} else if (!playBoard.hasEnablePositions(piece)) {

										// TODO パスの際の挙動あれば追記
									}

									if (playBoard.hasEnablePositions(opponentPiece)) {

										Position opponentPosition = opponentProcessor.nextPosition(playBoard,
												opponentPiece, 30000);
										playBoard.putPiece(opponentPosition, opponentPiece);

									} else if (!playBoard.hasEnablePositions(opponentPiece)) {

										// TODO パスの際の挙動あれば追記

									}

								}

								// 黒が勝った場合
								if (playBoard.countPiece(Piece.BLACK) > playBoard.countPiece(Piece.WHITE)) {
									winCount = winCount + 1;

								}

								// 盤面リセット
								playBoard = new Board(testBoard.getBoard());

							}

							// 評価値の設定
							answer.set(num, (double) winCount / 1000);
							playBoard = new Board(testBoard.getBoard());
							winCount = 0;
						}

					}

					// 配列の出力
					for (int outNum = 0; outNum < answer.size(); outNum++) {

						pw.print(color.get(outNum) + ",");
						pw.print(xPosition.get(outNum) + ",");
						pw.print(yPosition.get(outNum) + ",");

						if (outNum < answer.size() - 1) {
							// pw.print(value + ",");
							pw.print(answer.get(outNum) + ",");
						} else {
							// pw.print(value);
							pw.print(answer.get(outNum));
						}
					}
					pw.println("");
				}

				xPosition = new ArrayList<Integer>();
				yPosition = new ArrayList<Integer>();
				color = new ArrayList<String>();
				answer = new ArrayList<Double>();

			}

			// ファイルに書き出す
			pw.close();

			// 読み込み終了
			br.close();

		} catch (

		Exception e) {

			e.printStackTrace();
		}
	}
}
