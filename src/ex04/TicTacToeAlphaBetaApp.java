package ex04;

import my.Cons;

// TicTacToeアルファベータ刈りバージョンクラス（ミニマックスバージョンから派生）
class TicTacToeAlphaBeta extends TicTacToeMinMax {
	public TicTacToeAlphaBeta(TicTacToeAlphaBetaApp app) {
		super(app);
	}

	Cons searchAB(char p, char psw, int level, int alphaOrBeta) {
		boolean myTurn = psw == p;
		Cons minmax = Cons.of(0, 0, myTurn ? Integer.MIN_VALUE: Integer.MAX_VALUE);
		int count = 0;
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				if (bd[r][c] == '　') {							// 空のマスのみ調べる
					bd[r][c] = psw;								// マスに駒を置く（シミュレーション開始）
					int v;
					if (level == 1 || goal(psw) || fin()) {
						count += 1;								// 探索回数のカウント
						v = eval(p);									// 評価値計算
					} else {
						// 再帰的に探索
						Cons v1 = searchAB(p, turn(psw), level-1, minmax.getI(2));
						count += v1.getI(1);					// 再帰的なカウント加算
						v = v1.getC(0).getI(2);				// 再帰的な評価値
					}
					bd[r][c] = '　';								// マスを空に戻す（シミュレーション終了）
					if (myTurn && v >= alphaOrBeta || !myTurn && v <= alphaOrBeta) {
						// これ以上の探索を打ち切って結果を返す
						return Cons.of(Cons.of(r, c, v), count);
					}
					if (myTurn && v > minmax.getI(2) || !myTurn && v < minmax.getI(2)) {
						minmax = Cons.of(r, c, v);		// 評価値が良ければ，最良の手にする
					}
				}
			}
		}
		return Cons.of(minmax, count);				// 最良の手と探索回数を返す
	}

	void computer(char p) {			// コンピュータの手（最良の手を探索，最適な探索回数で）
		Cons s = searchAB(p, p, 3, Integer.MAX_VALUE); 	// 3手先まで読む
		computerResult(p, s);
	}
}

// グラフィックスウィンドウアプリケーションクラス
public class TicTacToeAlphaBetaApp extends TicTacToeMinMaxApp {
	public static void main(String[] args) {
		launch(args);
	}

	public void initGame() {
		game = new TicTacToeAlphaBeta(this);			// ゲームオブジェクトを生成
	}
}
