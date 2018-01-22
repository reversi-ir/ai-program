package nuralNetwork;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;

public class ReversiPerceptron {
	/**
	 * 多層パーセプトロンの実装
	 *
	 * @author Kamata
	 * @param args
	 */

	// 多層パーセプトロン
	MultiLayerPerceptron mlp;

	// コンストラクタ
	public ReversiPerceptron() {
		this.mlp = new MultiLayerPerceptron(64, 128, 1);
	}

	/**
	 * 処理関数
	 *
	 * @return
	 */
	public double ReversiPerceptronCreate(Board board, Piece piece) {

		double answer;

		// 評価値計算
		answer = EvaluationValueCalculation(120, 1, board, mlp);

		if (piece.equals(Piece.WHITE)) {
			answer = 1-answer;
		}

		return EvaluationValueCalculation(120, 1, board, mlp);

	}

	/**
	 * 評価値算出
	 *
	 * @param x
	 * @param answer
	 * @return
	 */
	public double EvaluationValueCalculation(int middleNumber, int outputNumber, Board board,
			MultiLayerPerceptron mlp) {

		double[] in = null; // 盤面を保持する配列
		String BoardValue = null; // 盤面の値を一時的に格納する文字列
		String[] BoardValueArry = null; // 盤面の値を一時的に格納する文字型配列
		double[] h = new double[middleNumber]; // 中間層の出力
		double[] o = new double[outputNumber]; // 出力層の出力

		// 盤面の状態を取得
		BoardValue = board.getBoardString();

		// 文字列配列化
		BoardValueArry = BoardValue.split(",", 0);

		// double型の配列へ変換
		in = new double[BoardValueArry.length];

		for (int intCnt = 0; intCnt < BoardValueArry.length; intCnt++) {
			in[intCnt] = Double.parseDouble(BoardValueArry[intCnt]) / 10;
		}

		// 出力値を推定：中間層の出力計算
		for (int j = 0; j < middleNumber; j++) {
			h[j] = mlp.middleNeurons[j].outputMiddle(in);
		}

		// 出力値を推定：出力層の出力計算
		for (int j = 0; j < outputNumber; j++) {
			o[j] = mlp.outputNeurons[j].output(h);
		}

		return o[0];

	}

}
