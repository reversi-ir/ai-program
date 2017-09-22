import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;
import jp.takedarts.reversi.Processor;

/**
 * Reversi人工知能のサンプルプログラム。
 *
 * @author Atushi TAKEDA
 */
public class SampleProcessor
  extends Processor
{
  /**
   * 手番が来たときに、次の手を決定するメソッド。<br>
   *
   * @param board 盤面の状態
   * @param piece 自分が打つ駒
   * @param thinkingTime 思考時間
   * @return 次の手を置く場所
   */
  @Override
  public Position nextPosition(Board board, Piece piece, long thinkingTime)
  {
    // 次に置ける場所を探す
    int x = -1;
    int y = -1;

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (board.isEnablePosition(i, j, piece)) {
          x = i;
          y = j;
        }
      }
    }

    // 置く場所をログに出力
    log(String.format("next -> (%d, %d)", x, y));

    // 置く場所をPositionオブジェクトに変換して返す
    return new Position(x, y);
  }

  /**
   * この人工知能の名前を返す。
   *
   * @return 人工知能の名前
   */
  @Override
  public String getName()
  {
    return "サンプルプログラム";
  }

}
