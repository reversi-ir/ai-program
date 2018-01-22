package nuralNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;

public class LerningPerceptron {
	/**
	 * 多層パーセプトロンの実装
	 *
	 * @author Kamata
	 * @param args
	 */
	public static void main(String[] args) {
		new LerningPerceptron();
	}

	/**
	 * 処理関数
	 */
	public LerningPerceptron() {

		// 入力データ配列 （xPotision ,yPosition)=(x軸,y軸)の配列と,色データ配列 color,正解データ配列 answer

		String[] csvAll;
		List<InputData> InputDataList = new ArrayList<InputData>();
		InputData inputData = new InputData();
		FileWriter fwMiddle = null;
		FileWriter fwOutput = null;

		// パーセプトロンの動作確認
		try {

			// 標準出力をファイルに関連付ける

			String fileName = System.getProperty("user.dir") + "/" + "TestMultiLayerPerceptron.log";
			PrintWriter logOut = new PrintWriter(fileName);

			// 教師データの指定
			String answerFileName = System.getProperty("user.dir") + "/" + "koutou_278042_test.csv";
			// String answerFileName
			// ="C:/Users/kamat/Desktop/GGFConvert/teacher_280844_ver2.csv";

			// 教師データ読み込み
			FileReader fr = new FileReader(answerFileName);
			BufferedReader br = new BufferedReader(fr);

			// 多層パーセプトロンの作成
			MultiLayerPerceptron mlp = new MultiLayerPerceptron(64, 128, 1);

			// 読み込んだファイルを１行ずつ処理する
			String line;
			int fileRowNum = 0;

			while ((line = br.readLine()) != null) {

				fileRowNum = +fileRowNum + 1;
				logOut.println(String.format("[RowNum] %d", fileRowNum));

				// 区切り文字","で分割する
				csvAll = line.split(",", 0); // 行をカンマ区切りで配列に変換

				for (int i = 0; i < csvAll.length; i += 4) {

					// 1手ずつ情報をリストへ格納していく
					inputData.setColor(csvAll[i]);
					inputData.setxPotision(Integer.parseInt(csvAll[i + 1]));
					inputData.setyPotision(Integer.parseInt(csvAll[i + 2]));
					inputData.setAnswer(Double.parseDouble(csvAll[i + 3]));

					InputDataList.add(inputData);

					// 初期化
					inputData = new InputData();

				}

				// 学習
				mlp.learn(InputDataList, logOut, fwMiddle, fwOutput);

				// 配列のクリア
				InputDataList.clear();

				// 出力の書き込み
				logOut.flush();

			}

			// 読み込み終了
			br.close();

			// ファイルを閉じる
			logOut.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}

/**
 * 入力値を格納するクラス
 *
 *
 * @author Kamata
 *
 */
class InputData {

	public int xPotision;
	public int yPotision;
	public String color;
	public double answer;

	public int getxPotision() {
		return xPotision;
	}

	public void setxPotision(int xPotision) {
		this.xPotision = xPotision;
	}

	public int getyPotision() {
		return yPotision;
	}

	public void setyPotision(int yPotision) {
		this.yPotision = yPotision;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double getAnswer() {
		return answer;
	}

	public void setAnswer(double answer) {
		this.answer = answer;
	}

}

/**
 * 多層パーセプトロンを表すクラス
 *
 * x:入力 H,o:出力 v:結合加重 θ:閾値 誤差逆伝播学習則(バックプロパゲーション)を利用
 *
 * @author Kamata
 *
 */
class MultiLayerPerceptron {
	// 定数
	protected static final int MAX_TRIAL = 10000; // 最大試行回数
	protected static final double MAX_GAP = 0.001f; // 出力値で許容する誤差の最大値

	// プロパティ
	protected int inputNumber = 0;
	protected int middleNumber = 0;
	protected int outputNumber = 0;
	protected Neuron[] middleNeurons = null; // 中間層のニューロン
	protected Neuron[] outputNeurons = null; // 出力層のニューロン
	public static double middleThreshold = 1; // 閾値θ
	public static double outputThreshold = 1; // 閾値θ

	// ロガー
	protected Logger logger = Logger.getAnonymousLogger(); // ログ出力
	public Object[] inputWeights;
	public double eater;
	public double[] inputValues;
	public double delta;

	/**
	 * 三層パーセプトロンの初期化
	 *
	 * @param input
	 *            入力層のニューロン数
	 * @param middle
	 *            中間層のニューロン数
	 * @param output
	 *            出力層のニューロン数
	 */
	public MultiLayerPerceptron(int input, int middle, int output) {
		// 内部変数の初期化
		this.inputNumber = input;
		this.middleNumber = middle;
		this.outputNumber = output;
		this.middleNeurons = new Neuron[middle];
		this.outputNeurons = new Neuron[output];

		// 中間層のニューロン作成
		for (int i = 0; i < middle; i++) {
			middleNeurons[i] = new Neuron(input, i);
		}

		// 出力層のニューロン作成
		for (int i = 0; i < output; i++) {
			outputNeurons[i] = new Neuron(middle, i);
		}

	}

	/**
	 * 学習
	 *
	 * @param x
	 * @param answer
	 * @throws IOException
	 */
	public void learn(List<InputData> InputDataList, PrintWriter outOut, FileWriter fwMiddle, FileWriter fwOutput)
			throws IOException {
		// 変数初期化

		double[] in = null; // i回目の試行で利用する教師入力データ
		double ans = 0; // i回目の試行で利用する教師出力データ
		double ansMax = -64; // 教師出力データの最大値
		double ansMin = 64; // 教師出力データの最小値
		double ansSum = 0; // i回目の試行で利用する教師出力データの合計
		double[] h = new double[middleNumber]; // 中間層の出力
		double[] o = new double[outputNumber]; // 出力層の出力
		String BoardValue = null; // 盤面の値を一時的に格納する文字列
		String[] BoardValueArry = null; // 盤面の値を一時的に格納する文字型配列
		double delta = 0;
		double sumDelta = 0;
		double loss = 0;
		double outputSum = 0;

		// 初期盤面の作成
		Board testBoard = new Board();

		// 教師データ中の最大値を取得
		for (int num = 0; num < InputDataList.size(); num++) {

			if (InputDataList.get(num).getAnswer() > ansMax) {
				ansMax = InputDataList.get(num).getAnswer();
			}
			if (InputDataList.get(num).getAnswer() < ansMin) {
				ansMin = InputDataList.get(num).getAnswer();
			}
		}

		// 対局の再現
		for (int num = 0; num < InputDataList.size(); num++) {

			// 配列に格納した座標を盤面にセット
			if (InputDataList.get(num).getColor().equals("B")) {
				testBoard.putPiece(InputDataList.get(num).getxPotision(), InputDataList.get(num).getyPotision(),
						Piece.BLACK);
			} else {
				testBoard.putPiece(InputDataList.get(num).getxPotision(), InputDataList.get(num).getyPotision(),
						Piece.WHITE);
			}
			;

			// 更新後の盤面を取得
			BoardValue = testBoard.getBoardString();

			// 文字列配列化
			BoardValueArry = BoardValue.split(",", 0);

			// double型の配列へ変換
			in = new double[BoardValueArry.length];

			for (int intCnt = 0; intCnt < BoardValueArry.length; intCnt++) {
				in[intCnt] = Double.parseDouble(BoardValueArry[intCnt]);
			}

			// 答えの設定
			// 0～1の範囲で正規化する
			ans = (InputDataList.get(num).getAnswer() - ansMin) / (ansMax - ansMin);
			ansSum += ans;

			// 出力値を推定：中間層の出力計算
			for (int j = 0; j < middleNumber; j++) {
				h[j] = middleNeurons[j].outputMiddle(in);
			}

			// 出力値を推定：出力層の出力計算
			for (int j = 0; j < outputNumber; j++) {

				o[j] = outputNeurons[j].output(h);
				outputSum += o[j];

				// 損失関数を計算（2乗誤差）
				loss = loss + (double) Math.pow(o[j] - ans, 2.0f) / 2;

				// δ計算
				delta = delta + (ans - o[j]) * o[j] * (1.0f - o[j]);

			}

		}
		// 学習
		for (int i = 0; i < MAX_TRIAL; i++) {

			// データ全体の損失関数を計算
			loss = loss / InputDataList.size();

			// データ全体のδを計算
			delta = delta / InputDataList.size();

			outOut.println(String.format(" Trial:%d", i));
			outOut.println(String.format("  [loss] %f", loss));
			outOut.println(String.format("  [delta] %f", delta));
			outOut.println(String.format("  [answer] %f", ansSum));
			outOut.println(String.format("  [output] %f", outputSum));
			outOut.println(String.format("  [sumLoss] %f", Math.pow(ansSum - outputSum, 2.0f)));

			// 評価・判定
			// 損失関数が十分小さい場合は次の処理へ
			// そうでなければ正解フラグを初期化
			if (Math.pow(ansSum - outputSum, 2.0f) < MAX_GAP) {
				break;
			}

			// 学習
			for (int j = 0; j < outputNumber; j++) {
				outputNeurons[j].learn(delta, h, "output");
			}

			// 中間層の更新
			for (int j = 0; j < middleNumber; j++) {
				// 中間層ニューロンの学習定数δを計算
				for (int k = 0; k < outputNumber; k++) {
					Neuron n = outputNeurons[k];
					sumDelta += n.getInputWeightIndexOf(j) * n.getDelta();
				}
				delta = h[j] * (1.0f - h[j]) * sumDelta;

				// 学習
				middleNeurons[j].learn(delta, in, "middle");

			}

			loss = 0;
			delta = 0;
			sumDelta = 0;
			outputSum = 0;
			testBoard = new Board();

			// 再計算
			for (int num = 0; num < InputDataList.size(); num++) {

				// 配列に格納した座標を盤面にセット
				if (InputDataList.get(num).getColor().equals("B")) {
					testBoard.putPiece(InputDataList.get(num).getxPotision(), InputDataList.get(num).getyPotision(),
							Piece.BLACK);
				} else {
					testBoard.putPiece(InputDataList.get(num).getxPotision(), InputDataList.get(num).getyPotision(),
							Piece.WHITE);
				}
				;
				// 更新後の盤面を取得
				BoardValue = testBoard.getBoardString();

				// 文字列配列化
				BoardValueArry = BoardValue.split(",", 0);

				// double型の配列へ変換
				in = new double[BoardValueArry.length];

				for (int intCnt = 0; intCnt < BoardValueArry.length; intCnt++) {
					in[intCnt] = Double.parseDouble(BoardValueArry[intCnt]);
				}

				// 答えの設定
				// 0～1の範囲で正規化する
				ans = (InputDataList.get(num).getAnswer() - ansMin) / (ansMax - ansMin);

				// 出力値を推定：中間層の出力計算
				for (int j = 0; j < middleNumber; j++) {
					h[j] = middleNeurons[j].outputMiddle(in);
				}

				// 出力値を推定：出力層の出力計算
				for (int j = 0; j < outputNumber; j++) {

					o[j] = outputNeurons[j].output(h);
					outputSum += o[j];

					// 損失関数を計算（2乗誤差）
					loss = loss + (double) Math.pow(o[j] - ans, 2.0f) / 2;

					// δ計算
					delta = delta + (ans - o[j]) * o[j] * (1.0f - o[j]);

				}

			}
		}

		// 結合加重をCSVファイルへ出力する。
		fwMiddle = new FileWriter(System.getProperty("user.dir") + "/" + "resultMiddle.csv", false);

		PrintWriter pwMiddle = new PrintWriter(new BufferedWriter(fwMiddle));
		fwOutput = new FileWriter(System.getProperty("user.dir") + "/" + "resultOutput.csv", false);
		PrintWriter pwoutPut = new PrintWriter(new BufferedWriter(fwOutput));

		// 入力→中間時の結合加重を出力
		for (Neuron n : middleNeurons) {
			pwMiddle.print(n);
		}

		pwMiddle.println();

		// 入力→中間時の閾値を出力

		pwMiddle.print(middleThreshold);

		// 中間→出力の結合加重を出力
		for (Neuron n : outputNeurons) {
			pwoutPut.print(n);
		}

		pwoutPut.println();

		// 中間→出力の閾値を出力

		pwoutPut.print(outputThreshold);

		// 出力
		pwMiddle.close();
		pwoutPut.close();

	}

	@Override
	public String toString() {
		// 戻り値変数
		String str = "";

		// 中間層ニューロン出力
		str += " middle neurons ( ";
		for (Neuron n : middleNeurons) {
			str += n;
		}
		str += ") ";

		// 出力層ニューロン出力
		str += " output neurons ( ";
		for (Neuron n : outputNeurons) {
			str += n;
		}
		str += ") ";

		return str;
	}

	/**
	 * 多層パーセプトロン内部で利用するニューロン
	 *
	 * @author Kamata
	 */
	class Neuron {

		// 内部変数
		protected int inputNeuronNum = 0; // 入力の数
		protected double[] inputWeights = null; // 入力ごとの結合加重
		protected double delta = 0; // 学習定数δ
		// protected double threshold = 0.01f; // 閾値θ
		protected double eater = 0.00001d; // 学習係数η

		/**
		 * 初期化
		 *
		 * @param inputNeuronNum
		 *            入力ニューロン数
		 * @param MiddleNeuronNum
		 *            初期化する中間層ニューロンの番号
		 */
		public Neuron(int inputNeuronNum, int middleNeuronNum) {
			// 変数初期化
			Random r = new Random();
			this.inputNeuronNum = inputNeuronNum;
			this.inputWeights = new double[inputNeuronNum];
			String[] middleWeightsAll = null;
			String[] middlethreshold = null;
			String[] outputWeightsAll = null;
			String[] outputthreshold = null;

			// 中間層結合加重ファイルの読み込み
			try {
				String middleFileName = System.getProperty("user.dir") + "/" + "resultMiddle.csv";
				FileReader frMiddle = new FileReader(middleFileName);
				BufferedReader brMiddle = new BufferedReader(frMiddle);

				String OutputFileName = System.getProperty("user.dir") + "/" + "resultOutput.csv";
				FileReader frOutput = new FileReader(OutputFileName);
				BufferedReader brOutput = new BufferedReader(frOutput);

				// 読み込んだファイルを１行ずつ処理する
				String lineMiddle;
				lineMiddle = brMiddle.readLine();

				for (int i = 0; i < 2; i++) {
					if (i == 0) {
						// 区切り文字","で分割する
						middleWeightsAll = lineMiddle.split(",", 0); // 行をカンマ区切りで配列に変換
					} else {
						// 区切り文字","で分割する
						middlethreshold = lineMiddle.split(",", 0); // 行をカンマ区切りで配列に変換
					}

					lineMiddle = brMiddle.readLine();
				}

				// 中間層結合加重ファイル読み込み終了
				brMiddle.close();

				// 出力層結合加重ファイルの読み込み

				// 読み込んだファイルを１行ずつ処理する
				String lineOutput;
				lineOutput = brOutput.readLine();

				for (int i = 0; i < 2; i++) {
					if (i == 0) {
						// 区切り文字","で分割する
						outputWeightsAll = lineOutput.split(",", 0); // 行をカンマ区切りで配列に変換
					} else {
						// 区切り文字","で分割する
						outputthreshold = lineOutput.split(",", 0); // 行をカンマ区切りで配列に変換
					}
					lineOutput = brOutput.readLine();
				}

				// 出力層結合加重ファイル読み込み終了
				brOutput.close();

			} catch (Exception e) {

				e.printStackTrace();
			}

			int weightNumber = 0;

			if (middleNeuronNum != 0) {
				weightNumber = middleNeuronNum * 64;
			}

			// 結合加重を設定
			// 中間層の初期化の場合
			if (inputNeuronNum == 64) {
				for (int i = 0; i < inputWeights.length; i++) {

					// 調整済みの重み
					if (weightNumber + i < middleWeightsAll.length - 1) {
						this.inputWeights[i] = Double.parseDouble(middleWeightsAll[weightNumber + i]);
					} else {
						this.inputWeights[i] = r.nextDouble();
					}
				}
				// 閾値の設定
				middleThreshold = Double.parseDouble(middlethreshold[0]);

			} else if (inputNeuronNum == 120) {
				for (int i = 0; i < inputWeights.length; i++) {
					this.inputWeights[i] = Double.parseDouble(outputWeightsAll[i]);
				}
				// 閾値の設定
				outputThreshold = Double.parseDouble(outputthreshold[0]);
			}

		}

		/**
		 * 学習（バックプロパゲーション学習）
		 *
		 * @param inputValues
		 *            入力データ
		 * @param delta
		 *            δ
		 */
		public void learn(double delta, double[] inputValues, String layer) {

			// 内部変数の更新
			this.delta = delta;

			// 結合加重の更新
			for (int i = 0; i < inputWeights.length; i++) {
				// バックプロパゲーション学習
				inputWeights[i] = inputWeights[i] - (eater * delta * inputValues[i]);

			}
			// 閾値の更新
			if (layer.equals("middle")) {
				middleThreshold -= eater * delta;
			} else if (layer.equals("output")) {
				outputThreshold -= eater * delta;
			}
		}

		/**
		 * 計算
		 *
		 * @param inputValues
		 *            入力ニューロンからの入力値
		 * @return 推定値
		 */
		public double outputMiddle(double[] inputValues) {

			// 入力値の総和を計算
			double sum = -middleThreshold;
			for (int i = 0; i < inputNeuronNum; i++) {
				sum += inputValues[i] * inputWeights[i];
			}

			// 活性化関数を適用して、出力値を計算
			double out = activationReLU(sum);

			return out;
		}

		/**
		 * 計算
		 *
		 * @param inputValues
		 *            中間ニューロンからの入力値
		 * @return 推定値
		 */
		public double output(double[] inputValues) {
			// 入力値の総和を計算
			double sum = -outputThreshold;
			for (int i = 0; i < inputNeuronNum; i++) {
				sum += inputValues[i] * inputWeights[i];
			}

			// 活性化関数を適用して、出力値を計算
			double out = activationKoutou(sum);

			return out;
		}

		/**
		 * 活性化関数（ReLU関数）
		 *
		 * @param x
		 * @return
		 */
		protected double activationReLU(double x) {
			return Math.max(0, x);
		}

		/**
		 * 活性化関数（RReLU関数）
		 *
		 * @param x
		 * @return
		 */
		protected double activationLReL(double x) {
			return (double) Math.max(0.01 * x, x);
		}

		/**
		 * 活性化関数（双曲線正接関数）
		 *
		 * @param x
		 * @return
		 */
		protected double activationtanh(double x) {
			return (double) Math.tanh(x);
		}

		/**
		 * 活性化関数（恒等関数）
		 *
		 * @param x
		 * @return
		 */
		protected double activationKoutou(double x) {
			return x;
		}

		/**
		 * 活性化関数（シグモイド関数）
		 *
		 * @param x
		 * @return
		 */
		protected double activationSigmoid(double x) {
			return (double) (1.0f / (1.0f + Math.pow(Math.E, -x)));
		}

		/**
		 *
		 * 入力iに対する結合加重を取得
		 *
		 * @param i
		 * @return
		 */
		public double getInputWeightIndexOf(int i) {
			if (i >= inputNumber) {
				new RuntimeException("outbound of index");
			}
			return inputWeights[i];
		}

		/**
		 * 学習定数δの取得
		 *
		 * @return 学習定数δ
		 */
		public double getDelta() {
			return delta;
		}

		/**
		 * クラス内部確認用の文字列出力
		 */
		@Override
		public String toString() {
			// 出力文字列の作成
			String output = "";
			for (int i = 0; i < inputNeuronNum; i++) {
				output += inputWeights[i] + " , ";
			}

			return output;

		}

	}

}
