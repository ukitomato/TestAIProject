package ex04;

import my.Cons;

// TicTacToeミニマックスバージョンクラス(TicTacToeGraphicsから派生)
class TicTacToeMinMax extends TicTacToeGraphics {
	public TicTacToeMinMax(TicTacToeMinMaxApp app) {
		super(app);
	}

	int f(char p, Cons t) {										// 評価値計算
		int self = t.count((Cons a) -> bd[a.getI(0)][a.getI(1)] == p);
		int free = t.count((Cons a) -> bd[a.getI(0)][a.getI(1)] == '　');
		int other = 3 - self - free;
		if (self > 0 && other == 0) return (int)Math.pow(3,self);
		else if (other > 0 && self == 0) return -(int)Math.pow(3,other);
		else return 0;
	}

	int eval(char p) {												// 全パターンの評価値合計
		return pat.map((Cons t) -> f(p, t)).sum();
	}

	Cons search(char p, char psw, int level) {
		boolean myTurn = psw == p;
		Cons minmax = Cons.of(0, 0, myTurn ? Integer.MIN_VALUE: Integer.MAX_VALUE);
		int count = 0;
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				if (bd[r][c] == '　') {								// 空のマスのみ調べる
					bd[r][c] = psw;									// マスに駒を置く（シミュレーション開始）
					int v;
					if (level == 1 || goal(psw) || fin()) {
						count += 1;									// 探索回数のカウント
						v = eval(p);										// 評価値計算
					} else {													// 再帰的に探索
						Cons v1 = search(p, turn(psw), level-1);
						count += v1.getI(1);						// 再帰的なカウント加算
						v = v1.getC(0).getI(2);					// 再帰的な評価値
					}
					bd[r][c] = '　';									// マスを空に戻す（シミュレーション終了）
					if (myTurn && v > minmax.getI(2) || !myTurn && v < minmax.getI(2)) {
						minmax = Cons.of(r, c, v);			// 評価値が良ければ，最良の手にする
					}
				}
			}
		}
		return Cons.of(minmax, count);					// 最良の手と探索回数を返す
	}

	void computer(char p) {									// コンピュータの手（最良の手を探索）
		Cons s = search(p, p, 3); 								// 3手先まで読む
		computerResult(p, s);
	}

	void computerResult(char p, Cons s) {			// コンピュータの手の結果設定
		Cons mx = s.getC(0);
		int cnt = s.getI(1), r = mx.getI(0), c = mx.getI(1);
		bd[r][c] = p;
		System.out.println("computer:" + p + " = " + r + "," + c + " search = " + cnt);
	}
}

// グラフィックスウィンドウアプリケーションクラス
public class TicTacToeMinMaxApp extends TicTacToeGraphicsApp {
	public static void main(String[] args) {
		launch(args);
	}

	public void initGame() {
		game = new TicTacToeMinMax(this);			// ゲームオブジェクトを生成
	}
}

