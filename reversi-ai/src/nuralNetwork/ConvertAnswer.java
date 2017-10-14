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

			// 教師データ読み込み
			FileReader fr = new FileReader(answerFileName);
			BufferedReader br = new BufferedReader(fr);

			// 読み込んだファイルを１行ずつ処理する
			String line;
			int fileRowNum = 0;
			FileWriter fw = null;
			fw = new FileWriter("C:/Users/kamat/Desktop/GGFConvert/teacher.csv", true);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			while ((line = br.readLine()) != null) {

				fileRowNum = +fileRowNum + 1;
				System.out.println(String.format("[RowNum] %d", fileRowNum));

				// 区切り文字","で分割する
				csvAll = line.split(",", 0); // 行をカンマ区切りで配列に変換

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
				int value = 0;

				// 最終局面まで進める
				for (int num = 0; num < answer.size(); num++) {

					// 配列に格納した座標を盤面にセット
					if (color.get(num).equals("B")) {
						testBoard.putPiece(xPosition.get(num), yPosition.get(num), Piece.BLACK);
					} else {
						testBoard.putPiece(xPosition.get(num), yPosition.get(num), Piece.WHITE);
					}
				}

				//評価値の設定
				value = testBoard.countPiece(Piece.BLACK) - testBoard.countPiece(Piece.WHITE);

				// 配列の出力
				for (int outNum = 0; outNum < answer.size(); outNum++) {

					pw.print(color.get(outNum) + ",");
					pw.print(xPosition.get(outNum)+ ",");
					pw.print(yPosition.get(outNum)+ ",");

					if (outNum < answer.size() - 1) {
						pw.print(value+",");
					} else {
						pw.print(value);
					}
				}
				
				xPosition = new ArrayList<Integer>();
				yPosition = new ArrayList<Integer>();
				color = new ArrayList<String>();
				answer = new ArrayList<Double>();

				pw.println("");

			}

			// ファイルに書き出す
			pw.close();

			// 読み込み終了
			br.close();


		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
