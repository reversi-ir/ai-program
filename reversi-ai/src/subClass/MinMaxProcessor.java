package subClass;
import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;
import jp.takedarts.reversi.Position;
import jp.takedarts.reversi.Processor;

/**
 * Reversi人工知能のサンプルプログラム。
 *
 * @author Atsushi TAKEDA
 */
public class MinMaxProcessor
  extends Processor
{
  /**
   * 評価テーブル。
   */
  private static int[][] _VALUES = new int[][] {
    {
      9, 2, 8, 6, 6, 8, 2, 9
    }, {
      2, 1, 4, 5, 5, 4, 1, 2
    }, {
      8, 4, 6, 5, 5, 6, 4, 8
    }, {
      6, 5, 5, 4, 4, 5, 5, 6
    }, {
      6, 5, 5, 4, 4, 5, 5, 6
    }, {
      8, 4, 6, 5, 5, 6, 4, 8
    }, {
      2, 1, 4, 5, 5, 4, 1, 2
    }, {
      9, 2, 8, 6, 6, 8, 2, 9
    }
  };

  /**
   * 手番が来たときに、次の手を決定するメソッド。
   *
   * @param board 盤面の状態
   * @param piece 自分が打つ駒
   * @param thinkingTime 思考時間
   * @return 次の手を置く場所
   */
  @Override
  public Position nextPosition(Board board, Piece piece, long thinkingTime)
  {
    // 次に置ける場所の中で、もっとも評価の高い場所を探す
    int max = Integer.MIN_VALUE;
    int x = -1;
    int y = -1;

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        // 置けるかどうかを確認し、置けないのなら何もしない
        if (!board.isEnablePosition(i, j, piece)) {
          continue;
        }

        // 同じ盤面を表すオブジェクトを作成し、自分の駒を置く
        Board next_board = new Board(board.getBoard());

        next_board.putPiece(i, j, piece);

        // 駒を置いた後の盤面に、さらに相手が駒を置いた場合の最小評価値を計算する
        int value = _getMinValue(next_board, piece);

        // ログに出力
        log(String.format("(%d, %d) : %d", i, j, value));

        // 求めた盤面の最小評価値が最大となる駒の置き場所を保存する
        if (value > max) {
          max = value;
          x = i;
          y = j;
        }
      }
    }

    // 置く場所をログに出力
    log(String.format("next -> (%d, %d) : %d", x, y, max));

    // 置く場所をPositionオブジェクトに変換して返す
    return new Position(x, y);
  }

  /**
   * 盤面の評価値を計算する。
   *
   * @param board 盤面の状態
   * @param piece 自分の駒
   * @return 評価値
   */
  private int _getValue(Board board, Piece piece)
  {
    int value = 0;

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        if (board.getPiece(i, j) == piece) {
          value += _VALUES[i][j];
        }
        else if (board.getPiece(i, j) == Piece.opposite(piece)) {
          value -= _VALUES[i][j];
        }
      }
    }

    return value;
  }

  /**
   * 相手の駒を置いたときの最小評価値を返す。
   *
   * @param board 盤面の状態
   * @param piece 自分の駒
   * @return 最小評価値
   */
  private int _getMinValue(Board board, Piece piece)
  {
    // 相手の駒を置ける場所に駒を置いてみて、その中の評価値の最小値を求める
    Piece enemy = Piece.opposite(piece);
    int min = Integer.MAX_VALUE;

    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        // 駒を置けない場合は何もしない
        if (!board.isEnablePosition(i, j, enemy)) {
          continue;
        }

        // 盤面の状態をコピーして、相手の駒を置く
        Board next_board = new Board(board.getBoard());

        next_board.putPiece(i, j, enemy);

        // 駒を置いた後の盤面を評価し、最小の評価値を判定する
        int value = _getValue(next_board, piece);

        if (min > value) {
          min = value;
        }
      }
    }

    return min;
  }

  /**
   * この人工知能の名前を返す。
   *
   * @return 人工知能の名前
   */
  @Override
  public String getName()
  {
    return "MIN-MAX法を用いたプログラム";
  }
}