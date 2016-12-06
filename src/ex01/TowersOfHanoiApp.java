package ex01;

import my.Cons;

// ハノイの塔クラス
class TowersOfHanoi {
	int n;				// n: 円盤の数

	void hanoi(int m, int from, int to, int work, Cons[] tower) {
		if (m == 1) {													// 移動対象が1枚なら，
			tower[to] = new Cons(tower[from].head, tower[to]);	// from から to へ 1枚移動
			tower[from] = tower[from].tail;				// from から移動したぶん減らす
			System.out.println(disp(Cons.fromArray(tower).map((Cons x) -> x.reverse())));
		} else {																// 移動対象が複数枚なら，副問題を解く
			hanoi(m-1, from, work, to, tower);			// from から work へ移動
			hanoi(1, from, to, work, tower);				// from から to へ移動
			hanoi(m-1, work, to, from, tower);			// work から to へ移動
		}
	}

	String rep(String s, int n) {								// 文字列をn回繰り返す
		return new String(new char[n]).replace("\0", s);
	}

	String disp(Cons s) {											// 状態表示
		if (s.forall(x -> x == Cons.Nil)) {
			return rep("－", n * 2 * 3) + "\n";
		} else {
			return	disp(s.map((Cons x) -> (x == Cons.Nil) ? Cons.Nil : x.tail)) + "\n"	+
						s.map((Cons x) -> (x == Cons.Nil) ? 0 : x.head)
							.map((Integer x) -> rep("　", n - x) + rep("■■", x) + rep("　", n - x)).mkString("");
		}
	}

	void start(int n) {
		this.n = n;
		Cons[] tower = new Cons[] { Cons.range(1, n+1), Cons.Nil, Cons.Nil };
		hanoi(n, 0, 2, 1, tower);								// n枚，from=0，to=2，work=1 で実行
	}
}

// アプリケーション起動クラス
public class TowersOfHanoiApp {
	public static void main(String[] args) {
		new TowersOfHanoi().start(4);					// 枚数を設定してハノイの塔を作成・開始
	}
}
