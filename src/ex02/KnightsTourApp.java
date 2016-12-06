package ex02;

import my.Cons;

// 騎士の巡回問題クラス
class KnightsTour {
	int n;							// n: チェス盤サイズ
	Integer[][] bd;			// n×nチェス盤，2次元配列
	Cons[] pat;					// ナイトの移動パターン生成

	public KnightsTour(int n) {
		this.n = n;
		bd = Cons.makeIntArray2(n, n, 0);
		pat = new Cons[] {	Cons.of(1,2), Cons.of(1,-2), Cons.of(-1,2), Cons.of(-1,-2),
										Cons.of(2,1), Cons.of(2,-1), Cons.of(-2,1), Cons.of(-2,-1)	};
	}

	// r,cへの移動を試す
	public Cons knight(int r, int c, int cnt, Cons route) {
		if (r >= 0 && r < n && c >= 0 && c < n && bd[r][c] == 0) {
			bd[r][c] = cnt;																	// マスに移動数を代入
			if (cnt == n*n) return new Cons(Cons.of(r,c), route);	// 最終位置到達，結果を返す
			for (Cons p : pat) {
				Cons rt = knight(r+p.getI(0), c+p.getI(1), cnt+1,
									new Cons(Cons.of(r,c), route));				// 次の移動を試す
				if (rt != Cons.Nil) return rt;											// ルート探索成功，結果を返す
			}
			bd[r][c] = 0;																		// 失敗したのでマスを空に戻す
		}
		return Cons.Nil;																		// 失敗したのでNilを返す
	}

	void start(int r, int c) {									// r,cから開始して巡回ルートとチェス盤表示
		System.out.println(knight(r, c, 1, Cons.Nil));					// 巡回ルートを求めて表示
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) System.out.printf("%02d ", bd[i][j]);
			System.out.print("\n");
		}
	}
}

// アプリケーション起動クラス
public class KnightsTourApp {
	public static void main(String[] args) {
		new KnightsTour(5).start(0, 0);				// チェス盤サイズ，初期位置を指定して開始
	}
}
