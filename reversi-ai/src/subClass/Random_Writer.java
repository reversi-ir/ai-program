package subClass;

import java.util.Random;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;

public class Random_Writer {
	/**
	   * 乱数を発生させるオブジェクト。
	   */
	  private Random _random = new Random(System.currentTimeMillis());
	 
	  /**
	   * 手番が来たときに、次の手を決定するメソッド。
	   *
	   * @param board 盤面の状態
	   * @param piece 自分が打つ駒
	   * @param thinkingTime 思考時間
	   * @return 次の手を置く場所
	   */
	  public Position nextPosition(Board board, Piece piece, long thinkingTime)
	  {
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
	 
	    // 次に置く場所をランダムに決定する
	    int index = _random.nextInt(count);
	    int x = positions[index][0];
	    int y = positions[index][1];
	 
	 
	    // 置く場所をPositionオブジェクトに変換して返す
	    return new Position(x, y);
	  }
	 
	  /**
	   * この人工知能の名前を返す。
	   *
	   * @return 人工知能の名前
	   */
	  public String getName()
	  {
	    return "改良版のサンプルプログラム";
	  }
}