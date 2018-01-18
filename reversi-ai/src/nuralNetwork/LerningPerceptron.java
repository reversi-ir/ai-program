package nuralNetwork;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
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
		List<Integer> xPosition = new ArrayList<Integer>();
		List<Integer> yPosition = new ArrayList<Integer>();
		List<String> color = new ArrayList<String>();
		List<Float> answer = new ArrayList<Float>();
		FileWriter fwMiddle = null;
		FileWriter fwOutput = null;

		// パーセプトロンの動作確認
		try {

			// 標準出力をファイルに関連付ける

			String fileName = System.getProperty("user.dir") + "/" + "TestMultiLayerPerceptron.log";
			PrintWriter logOut = new PrintWriter(fileName);

			// 教師データの指定
			String answerFileName = System.getProperty("user.dir") + "/" + "teacher_280844_ver2.csv";
			// String answerFileName
			// ="C:/Users/kamat/Desktop/GGFConvert/teacher_280844_ver2.csv";

			// 教師データ読み込み
			FileReader fr = new FileReader(answerFileName);
			BufferedReader br = new BufferedReader(fr);

			// 多層パーセプトロンの作成
			MultiLayerPerceptron mlp = new MultiLayerPerceptron(64, 120, 1);

			// 読み込んだファイルを１行ずつ処理する
			String line;
			int fileRowNum = 0;

			while ((line = br.readLine()) != null) {

				fileRowNum = +fileRowNum + 1;
				logOut.println(String.format("[RowNum] %d", fileRowNum));

				// 区切り文字","で分割する
				csvAll = line.split(",", 0); // 行をカンマ区切りで配列に変換

				for (int i = 0; i < csvAll.length; i += 4) {

					// 1手ずつ情報を配列へ格納していく
					color.add(csvAll[i]);
					xPosition.add(Integer.parseInt(csvAll[i + 1].replace("[", "")) - 1);
					yPosition.add(Integer.parseInt(csvAll[i + 2].replace("]", "").trim()) - 1);

					answer.add(Float.parseFloat(csvAll[i + 3]));

				}

				// 学習
				mlp.learn(xPosition, yPosition, color, answer, logOut, fwMiddle, fwOutput);

				// 配列のクリア
				xPosition.clear();
				yPosition.clear();
				color.clear();
				answer.clear();

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
	protected static final float MAX_GAP = 0.00001f; // 出力値で許容する誤差の最大値

	// プロパティ
	protected int inputNumber = 0;
	protected int middleNumber = 0;
	protected int outputNumber = 0;
	protected Neuron[] middleNeurons = null; // 中間層のニューロン
	protected Neuron[] outputNeurons = null; // 出力層のニューロン

	// ロガー
	protected Logger logger = Logger.getAnonymousLogger(); // ログ出力
	public Object[] inputWeights;
	public float eater;
	public float[] inputValues;
	public float delta;

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
	public void learn(List<Integer> xPosition, List<Integer> yPosition, List<String> color, List<Float> answer,
			PrintWriter outOut, FileWriter fwMiddle, FileWriter fwOutput) throws IOException {
		// 変数初期化

		float[] in = null; // i回目の試行で利用する教師入力データ
		float ans = 0; // i回目の試行で利用する教師出力データ
		float ansMax = 0; // 教師出力データの最大値
		float ansSum = 0; // i回目の試行で利用する教師出力データの合計
		float[] h = new float[middleNumber]; // 中間層の出力
		float[] o = new float[outputNumber]; // 出力層の出力
		String BoardValue = null; // 盤面の値を一時的に格納する文字列
		String[] BoardValueArry = null; // 盤面の値を一時的に格納する文字型配列
		float delta = 0;
		float sumDelta = 0;
		float loss = 0;
		float sumLoss = 0;
		float outputSum = 0;
		int trialCount = 0;

		// 初期盤面の作成
		Board testBoard = new Board();

		// 教師データ中の最大値を取得
		for (int num = 0; num < answer.size(); num++) {

			if (Math.abs(answer.get(num)) > ansMax) {
				ansMax = Math.abs(answer.get(num));
			}
		}

		// 学習
		for (int num = 0; num < answer.size(); num++) {

			// 配列に格納した座標を盤面にセット
			if (color.get(num).equals("B")) {
				testBoard.putPiece(xPosition.get(num), yPosition.get(num), Piece.BLACK);
			} else {
				testBoard.putPiece(xPosition.get(num), yPosition.get(num), Piece.WHITE);
			}
			;

			// 更新後の盤面を取得
			BoardValue = testBoard.getBoardString();

			// 文字列配列化
			BoardValueArry = BoardValue.split(",", 0);

			// float型の配列へ変換
			in = new float[BoardValueArry.length];

			for (int intCnt = 0; intCnt < BoardValueArry.length; intCnt++) {
				in[intCnt] = Float.parseFloat(BoardValueArry[intCnt]);
			}

			// 答えの設定
			// 0～1の範囲で正規化する

			ans = answer.get(num) / ansMax;
			ansSum += ans;

			// 出力値を推定：中間層の出力計算
			for (int j = 0; j < middleNumber; j++) {
				h[j] = middleNeurons[j].outputMiddle(in);
			}

			// 出力値を推定：出力層の出力計算
			for (int j = 0; j < outputNumber; j++) {

				o[j] = outputNeurons[j].output(h);
				outputSum += o[j];

				// 損失関数を計算
				loss = +(float) Math.pow((ans - o[j]), 2.0f);

			}

		}

		for (int i = 0; i < MAX_TRIAL; i++) {

			// データ全体の損失関数を計算
			sumLoss = loss / answer.size();

			// データ全体の勾配を計算
			delta = (ansSum - outputSum) * outputSum * (1.0f - outputSum);

			if (ansSum < outputSum) {
				delta = delta * -1;
			}

			outOut.print(String.format("Trial:%d", i));
			outOut.println(String.format("[sumLoss] %f", sumLoss));
			outOut.println(String.format("[answer] %f", answer.get(0) / ansMax));
			outOut.println(String.format("[output] %f", o[0]));

			// 評価・判定
			// 損失関数が十分小さい場合は次の処理へ
			// そうでなければ正解フラグを初期化
			if (Math.abs(sumLoss) < MAX_GAP) {
				break;
			} else {
				trialCount += 1;
			}

			// 学習
			for (int j = 0; j < outputNumber; j++) {
				outputNeurons[j].learn(delta, h);
			}

			// 中間層の更新
			for (int j = 0; j < middleNumber; j++) {
				// 中間層ニューロンの学習定数δを計算
				for (int k = 0; k < outputNumber; k++) {
					Neuron n = outputNeurons[k];
					sumDelta += n.getInputWeightIndexOf(j) * n.getDelta();
				}
				delta = h[j] * (1.0f - h[j]) * sumDelta;

				if (ans < h[j]) {
					delta = delta * -1;
				}

				// 学習
				middleNeurons[j].learn(delta, in);

			}

			loss = 0;
			sumLoss = 0;
			delta = 0;
			sumDelta = 0;
			outputSum = 0;

			// 出力値を推定：中間層の出力計算
			for (int j = 0; j < middleNumber; j++) {
				h[j] = middleNeurons[j].outputMiddle(in);
			}

			// 出力値を推定：出力層の出力計算
			for (int j = 0; j < outputNumber; j++) {
				o[j] = outputNeurons[j].output(h);

				outputSum += o[j];

				// 損失関数を計算
				loss = +(float) Math.pow((ans - o[j]), 2.0f);

			}
		}

		// 連続成功回数による終了判定

		// outOut.print(String.format("Trial:%d", trialCount));
		// outOut.println(String.format("[output] %f", o[0]));

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
		for (Neuron n : middleNeurons) {
			pwMiddle.print(n.threshold + " , ");
		}

		// 中間→出力の結合加重を出力
		for (Neuron n : outputNeurons) {
			pwoutPut.print(n);
		}

		pwoutPut.println();

		// 中間→出力の閾値を出力
		for (Neuron n : outputNeurons) {
			pwoutPut.print(n.threshold + " , ");
		}

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
		protected float[] inputWeights = null; // 入力ごとの結合加重
		protected float delta = 0; // 学習定数δ
		protected float threshold = 1; // 閾値θ
		protected float eater = 0.01f; // 学習係数η

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
			// Random r = new Random();
			this.inputNeuronNum = inputNeuronNum;
			this.inputWeights = new float[inputNeuronNum];
			// this.threshold = r.nextFloat(); // 閾値をランダムに生成
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
					this.inputWeights[i] = Float.parseFloat(middleWeightsAll[weightNumber + i]);
				}
				// 閾値の設定
				this.threshold = Float.parseFloat(middlethreshold[middleNeuronNum]);

			} else if (inputNeuronNum == 120) {
				for (int i = 0; i < inputWeights.length; i++) {
					this.inputWeights[i] = Float.parseFloat(outputWeightsAll[i]);
				}
				// 閾値の設定
				this.threshold = Float.parseFloat(outputthreshold[middleNeuronNum]);
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
		public void learn(float delta, float[] inputValues) {

			// 内部変数の更新
			this.delta = delta;

			// 結合加重の更新
			for (int i = 0; i < inputWeights.length; i++) {
				// バックプロパゲーション学習
				inputWeights[i] += eater * delta * inputValues[i];
			}
			// 閾値の更新
			threshold -= eater * delta;
		}

		/**
		 * 計算
		 *
		 * @param inputValues
		 *            入力ニューロンからの入力値
		 * @return 推定値
		 */
		public float outputMiddle(float[] inputValues) {

			// 入力値の総和を計算
			float sum = -threshold;
			for (int i = 0; i < inputNeuronNum; i++) {
				sum += inputValues[i] * inputWeights[i];
			}

			// 活性化関数を適用して、出力値を計算
			float out = activationtanh(sum);

			return out;
		}

		/**
		 * 計算
		 *
		 * @param inputValues
		 *            中間ニューロンからの入力値
		 * @return 推定値
		 */
		public float output(float[] inputValues) {
			// 入力値の総和を計算
			float sum = -threshold;
			for (int i = 0; i < inputNeuronNum; i++) {
				sum += inputValues[i] * inputWeights[i];
			}

			// 活性化関数を適用して、出力値を計算
			float out = activationKoutou(sum);

			return out;
		}

		/**
		 * 活性化関数（ReLU関数）
		 *
		 * @param x
		 * @return
		 */
		protected float activationReLU(float x) {
			return Math.max(0, x);
		}

		/**
		 * 活性化関数（RReLU関数）
		 *
		 * @param x
		 * @return
		 */
		protected float activationLReL(float x) {
			return (float) Math.max(0.01 * x, x);
		}

		/**
		 * 活性化関数（双曲線正接関数）
		 *
		 * @param x
		 * @return
		 */
		protected float activationtanh(float x) {
			return (float) Math.tanh(x);
		}

		/**
		 * 活性化関数（恒等関数）
		 *
		 * @param x
		 * @return
		 */
		protected float activationKoutou(float x) {
			return x;
		}

		/**
		 * 活性化関数（シグモイド関数）
		 *
		 * @param x
		 * @return
		 */
		protected float activationSigmoid(double x) {
			return (float) (1.0f / (1.0f + Math.pow(Math.E, -x)));
		}

		/**
		 *
		 * 入力iに対する結合加重を取得
		 *
		 * @param i
		 * @return
		 */
		public float getInputWeightIndexOf(int i) {
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
		public float getDelta() {
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
