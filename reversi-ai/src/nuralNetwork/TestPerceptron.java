package nuralNetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
public class TestPerceptron
{
    /**
     * 多層パーセプトロンの実装
     * @author Kamata
     * @param args
     */
    public static void main(String[] args)
    {
        new TestPerceptron();
    }

    /**
     * 処理関数
     */
    public TestPerceptron()
    {

        // 入力データ配列 x =(x軸,y軸)の配列と,正解データ配列 answer
    	String[] csvAll;
    	List<Integer> xPosition=new ArrayList<Integer>();
    	List<Integer> yPosition=new ArrayList<Integer>();
    	List<String> color=new ArrayList<String>();
    	List<Double> answer=new ArrayList<Double>();


        // パーセプトロンの動作確認
        try
        {
        	// 教師データ読み込み
        	 FileReader fr = new FileReader("C:\\Users\\kamat\\Desktop\\GGFConvert\\test.ggf.csv");
             BufferedReader br = new BufferedReader(fr);

           //読み込んだファイルを１行ずつ処理する
             String line;
             while ((line = br.readLine()) != null) {
        	 	//区切り文字","で分割する
            	csvAll = line.split(",", 0); // 行をカンマ区切りで配列に変換

            	for (int i=0 ;i<csvAll.length;i+=4) {

            		//1手ずつ情報を配列へ格納していく
	            	color.add(csvAll[i]);
	            	xPosition.add(Integer.parseInt(csvAll[i+1].replace("[",""))-1);
	            	yPosition.add(Integer.parseInt(csvAll[i+2].replace("]","").trim())-1);
	            	answer.add(Double.parseDouble(csvAll[i+3]));

            	}

				// 多層パーセプトロンの作成
				MultiLayerPerceptron    mlp = new MultiLayerPerceptron( 64 , 40 , 1 );
				mlp.learn( xPosition , yPosition,color,answer );

         	 }

			 //読み込み終了
			 br.close();


			 // 標準出力をファイルに関連付ける
		    String      fileName    = System.getProperty( "user.dir" )
		                              + "/"
		                              + "TestMultiLayerPerceptron.log";
		    PrintStream out         = new PrintStream( fileName );
		    System.setOut( out );


            // ファイルを閉じる
            out.close();


        }catch( Exception e ){

        	e.printStackTrace();
        }
    }
}


/**
 * 多層パーセプトロンを表すクラス
 *
 * ■x1 → ■m1 → H1
 * 　   　 θ   　 　
 * 　   × 　   　 　 ■ → o1
 * 　   　 　   　 　 θ
 * ■x2 → ■m2 → H2
 * 　   　 θ   　 　
 *
 * x:入力
 * H,o:出力
 * v:結合加重
 * θ:閾値
 * 誤差逆伝播学習則(バックプロパゲーション)を利用
 * @author Kamata
 *
 */
class MultiLayerPerceptron
{
    // 定数
    protected static final int      MAX_TRIAL   = 100000;       // 最大試行回数
    protected static final double   MAX_GAP     = 0.1;          // 出力値で許容する誤差の最大値

    // プロパティ
    protected int   inputNumber     = 0;
    protected int   middleNumber    = 0;
    protected int   outputNumber    = 0;
    protected Neuron[]    middleNeurons   = null;     // 中間層のニューロン
    protected Neuron[]    outputNeurons   = null;     // 出力層のニューロン

    // ロガー
    protected Logger      logger    = Logger.getAnonymousLogger();  // ログ出力

    /**
     * 三層パーセプトロンの初期化
     * @param input  入力層のニューロン数
     * @param middle 中間層のニューロン数
     * @param output 出力層のニューロン数
     */
    public MultiLayerPerceptron( int input , int middle , int output )
    {
        // 内部変数の初期化
        this.inputNumber    = input;
        this.middleNumber   = middle;
        this.outputNumber   = output;
        this.middleNeurons  = new Neuron[middle];
        this.outputNeurons  = new Neuron[output];

        // 中間層のニューロン作成
        for( int i=0 ; i<middle ; i++ ){ middleNeurons[i] = new Neuron( input ); }

        // 出力層のニューロン作成
        for( int i=0 ; i<output ; i++ ){ outputNeurons[i] = new Neuron( input ); }

        // 確認メッセージ
        System.out.println( "[init]  " + this );
    }

    /**
     * 学習
     * @param x
     * @param answer
     */
    public void learn( List<Integer> xPosition , List<Integer> yPosition, List<String> color,
    		List<Double> answer)
    {
        // 変数初期化
        double[]    in      = null;                           // i回目の試行で利用する教師入力データ
        double      ans     = 0;                              // i回目の試行で利用する教師出力データ
        double[]    h       = new double[ middleNumber ];     // 中間層の出力
        double[]    o       = new double[ outputNumber ];     // 出力層の出力

        //初期盤面の作成
        Board testBoard = new Board();
        System.out.println(testBoard);

		testBoard.putPiece(3, 3, Piece.WHITE);
		testBoard.putPiece(4, 4, Piece.WHITE);

		testBoard.putPiece(3, 4, Piece.BLACK);
		testBoard.putPiece(4, 3, Piece.BLACK);
		System.out.println(testBoard);

        // 学習
        for( int num=0 ; num<answer.size() ; num++ )
        {

	        int succeed = 0;        // 連続正解回数を初期化
	        for( int i=0 ; i<MAX_TRIAL ; i++ )
	        {
	            // 行間を空ける
	            System.out.println();
	            System.out.println( String.format( "Trial:%d" , i ) );


	            //配列に格納した座標を盤面にセット
	            if (color.get(num).equals("B"))
	            {
	            	testBoard.putPiece(xPosition.get(num), yPosition.get(num), Piece.BLACK);
	            }
            	else
            	{
            		testBoard.putPiece(xPosition.get(num), yPosition.get(num), Piece.WHITE);
	            };

	            //System.out.println(getBoardInteger());


	            //in  = x[ i % answer.size() ];
	            ans = answer.get(num);

	            // 出力値を推定：中間層の出力計算
	            for( int j=0 ; j<middleNumber ; j++ )
	            {
	                h[j] = middleNeurons[j].output( in );
	            }

	            // 出力値を推定：出力層の出力計算
	            for( int j=0 ; j<outputNumber ; j++ )
	            {
	                o[j] = outputNeurons[j].output( h );
	            }
	            System.out.println( String.format( "[input] %f , %f" , in[0] , in[1] ) );
	            System.out.println( String.format( "[answer] %f" , ans ) );
	            System.out.println( String.format( "[output] %f" , o[0] ) );
	            System.out.println( String.format( "[middle] %f , %f" , h[0] , h[1] ) );

	            // 評価・判定
	            boolean successFlg  = true;
	            for( int j=0 ; j<outputNumber ; j++ )
	            {
	                // 出力層ニューロンの学習定数δを計算
	                double delta = ( ans - o[j] ) * o[j] * ( 1.0d - o[j] );

	                // 教師データとの誤差が十分小さい場合は次の処理へ
	                // そうでなければ正解フラグを初期化
	                if( Math.abs( ans - o[j] ) < MAX_GAP ){ continue; }
	                                                  else{ successFlg = false; }

	                // 学習
	                System.out.println( "[learn] before o :" + outputNeurons[j] );
	                outputNeurons[j].learn( delta , h );
	                System.out.println( "[learn] after o  :" + outputNeurons[j] );

	            }

	            // 連続成功回数による終了判定
	            if( successFlg )
	            {
	                // 連続成功回数をインクリメントして、
	                // 終了条件を満たすか確認
	                succeed++;
	                if( succeed >= answer.size() ){ break; }else{ continue; }
	            }else{
	                succeed = 0;
	            }

	            // 中間層の更新
	            for( int j=0 ; j<middleNumber ; j++ )
	            {
	                // 中間層ニューロンの学習定数δを計算
	                double sumDelta = 0;
	                for( int k=0 ; k<outputNumber ; k++ )
	                {
	                    Neuron  n    = outputNeurons[k];
	                    sumDelta    += n.getInputWeightIndexOf(j) * n.getDelta();
	                }
	                double delta = h[j] * ( 1.0d - h[j] ) * sumDelta;

	                // 学習
	                System.out.println( "[learn] before m :" + middleNeurons[j] );
	                middleNeurons[j].learn( delta , in );
	                System.out.println( "[learn] after m  :" + middleNeurons[j] );
	            }


	            // 再度出力
	            // 出力値を推定：中間層の出力計算
	            for( int j=0 ; j<middleNumber ; j++ )
	            {
	                h[j] = middleNeurons[j].output( in );
	            }

	            // 出力値を推定：出力層の出力計算
	            for( int j=0 ; j<outputNumber ; j++ )
	            {
	                o[j] = outputNeurons[j].output( h );
	            }
	            System.out.println( String.format( "[input] %f , %f" , in[0] , in[1] ) );
	            System.out.println( String.format( "[output] %f" , o[0] ) );
	            System.out.println( String.format( "[middle] %f , %f" , h[0] , h[1] ) );


	        }
    	}

        // すべての教師データで正解を出すか
        // 収束限度回数を超えた場合に終了
        System.out.println( "[finish] " + this );
    }

    @Override
    public String toString()
    {
        // 戻り値変数
        String str  = "";

        // 中間層ニューロン出力
        str += " middle neurons ( ";
        for( Neuron n : middleNeurons ){ str += n; }
        str += ") ";

        // 出力層ニューロン出力
        str += " output neurons ( ";
        for( Neuron n : outputNeurons ){ str += n; }
        str += ") ";

        return str;
    }



    /**
     * 多層パーセプトロン内部で利用するニューロン
     * @author Kamata
     */
    class Neuron
    {

        // 内部変数
        protected int         inputNeuronNum  = 0;         // 入力の数
        protected double[]    inputWeights    = null;      // 入力ごとの結合加重
        protected double      delta           = 0;         // 学習定数δ
        protected double      threshold       = 0;         // 閾値θ
        protected double      eater           = 1.0d;      // 学習係数η

        /**
         * 初期化
         * @param inputNeuronNum 入力ニューロン数
         */
        public Neuron( int inputNeuronNum )
        {
            // 変数初期化
            Random r = new Random();
            this.inputNeuronNum = inputNeuronNum;
            this.inputWeights   = new double[ inputNeuronNum ];
            this.threshold      = r.nextDouble();               // 閾値をランダムに生成

            // 結合加重を乱数で初期化
            for( int i=0 ; i<inputWeights.length ; i++ )
            { this.inputWeights[i] = r.nextDouble(); }
        }

        /**
         * 学習（バックプロパゲーション学習）
         * @param inputValues 入力データ
         * @param delta       δ
         */
        public void learn( double delta , double[] inputValues  )
        {
            // 内部変数の更新
            this.delta  = delta;

            // 結合加重の更新
            for( int i=0 ; i<inputWeights.length ; i++ )
            {
                // バックプロパゲーション学習
                inputWeights[i] += eater * delta * inputValues[i];
            }

            // 閾値の更新
            threshold -= eater * delta;
        }

        /**
         * 計算
         * @param  inputValues 入力ニューロンからの入力値
         * @return 推定値
         */
        public double output( double[] inputValues )
        {
            // 入力値の総和を計算
            double  sum = -threshold;
            for( int i=0 ; i<inputNeuronNum ; i++ ){ sum += inputValues[i] * inputWeights[i]; }

            // 活性化関数を適用して、出力値を計算
            double  out = activation( sum );

            return out;
        }

        /**
         * 活性化関数（シグモイド関数）
         * @param x
         * @return
         */
        protected double activation( double x )
        {
            return 1 / ( 1 + Math.pow( Math.E , -x ) );
        }

        /**
         *
         * 入力iに対する結合加重を取得
         *
         * @param i
         * @return
         */
        public double getInputWeightIndexOf( int i )
        {
            if( i>=inputNumber ){ new RuntimeException("outbound of index"); }
            return inputWeights[i];
        }

        /**
         * 学習定数δの取得
         * @return 学習定数δ
         */
        public double getDelta()
        {
            return delta;
        }

        /**
         * クラス内部確認用の文字列出力
         */
        @Override
        public String toString()
        {
            // 出力文字列の作成
            String output = "weight : ";
            for( int i=0 ; i<inputNeuronNum ; i++ ){ output += inputWeights[i] + " , "; }

            return output;

        }

    }

}
