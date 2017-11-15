package subClass;

import java.util.ArrayList;

import jp.takedarts.reversi.Board;
import jp.takedarts.reversi.Piece;

public class AlphaBeta {

	// コンストラクタ
	AlphaBetaLylarTwo alphaBetaLylarTwo;

	public AlphaBeta() {
		this.alphaBetaLylarTwo = new AlphaBetaLylarTwo();
	}

	public Bean _getMaxValue(Board board, Piece piece) {

		// 次に置ける場所の中で、もっとも評価の高い場所を探す
		Piece enemy = Piece.opposite(piece);
		Board playBoard1;
		Board playBoard2 = new Board(board.getBoard());
		Board playBoard3;
		Board tryBoard = new Board(board.getBoard());
		boolean loop1 = true;
		boolean loop2 = true;
		boolean enemnycantputFlag = true;
		double pervalueThird = -100;
		double pervalueJudge = -100;
		double pervalueSecond = 100;
		ArrayList<String> messagesList = new ArrayList<String>();

		Bean bean = new Bean();

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {

				// 置けるかどうかを確認し、置けないのなら何もしない(２手目)
				if (tryBoard.isEnablePosition(i, j, enemy)) {

					enemnycantputFlag = false;

					tryBoard.putPiece(i, j, enemy);

					// 相手が置いた盤面を別変数に格納
					playBoard1 = new Board(tryBoard.getBoard());
					playBoard3 = new Board(tryBoard.getBoard());

					pervalueThird = -100;

					loop1: if (loop1) {
						for (int k = 0; k < 8; k++) {
							for (int l = 0; l < 8; l++) {

								// 置けるかどうかを確認し、置けないのなら何もしない（３手目）
								if (playBoard1.isEnablePosition(k, l, piece)) {

									playBoard1.putPiece(k, l, piece);

									Board boardcal = new Board(playBoard1.getBoard());

									// 自分が置いた後の盤面の評価値をニューラルネットワークにより算出
									pervalueJudge = alphaBetaLylarTwo._getMaxValue(boardcal, piece).getPervalueSecond();

									if ((pervalueSecond != 100) && (pervalueJudge >= pervalueSecond)) {
										loop1 = false;
										loop2 = false;
										break loop1;
									}

									// より評価値の高い値で更新していく
									if (pervalueThird < pervalueJudge) {

										pervalueThird = pervalueJudge;

									}
									playBoard1 = new Board(playBoard3.getBoard());
								}
							}
						}
					}

					loop1 = true;

					if ((pervalueSecond > pervalueThird) && (loop2) && pervalueThird != -100) {

						pervalueSecond = pervalueThird;
					}
					if (pervalueSecond == 100 && !enemnycantputFlag) {
						System.out.println("暫定pervalueSecond" + "自分がpassする可能性が高い（worst）");
						messagesList.add("暫定pervalueSecond" + "自分がpassする可能性が高い（worst）");
						pervalueSecond = -100;
					} else if (pervalueSecond == 100 && enemnycantputFlag) {
						System.out.println("暫定pervalueSecond" + "相手passする可能性の高い（best）");
						messagesList.add("暫定pervalueSecond" + "相手passする可能性の高い（best）");
						pervalueSecond = 100;
					} else if (pervalueSecond != 100 && pervalueSecond!=-100) {
						System.out.println("暫定pervalueSecond" + pervalueSecond);
						messagesList.add("暫定pervalueSecond" + pervalueSecond);
					}

					tryBoard = new Board(playBoard2.getBoard());
				}
				loop2 = true;
			}

			bean.setMessagesList(messagesList);
			bean.setPervalueSecond(pervalueSecond);

		}
		return bean;

	}

}
