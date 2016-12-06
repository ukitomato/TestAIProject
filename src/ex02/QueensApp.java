package ex02;

import my.Cons;

// Nクイーン問題クラス
class Queens {
	int n;										// n: クイーン数

	boolean check(Integer r, Integer c, Cons pat) {
		// 縦と斜めに重複しないかすべての配置をチェック
		return pat.forall((Cons p) ->
			c != p.get(1) && r - p.getI(0) != Math.abs(c - p.getI(1)));
	}

	Cons queen(int r) {				// 配置リストを複数返す
		if (r == 0) {
			return Cons.of(Cons.Nil);
		} else {
			return queen(r - 1).flatMap((Cons p) -> Cons.range(1, n+1)
								.filter((Integer c) -> check(r, c, p))
								.map(c -> new Cons(Cons.of(r, c), p)));
		}
	}

	String rep(String s, int n) {	// 文字列 s を n 回繰り返す
		return new String(new char[n]).replace("\0", s);
	}

	void start(int n) {					// 問題を解いて各配置リストから文字列を作成して表示
		this.n = n;
		queen(n).foreach((Cons pat) -> {
			System.out.println();
			pat.foreach((Cons p) ->
				System.out.println(rep("＋", p.getI(1)-1) + "Ｑ" + rep("＋", n-p.getI(1)))
			);
		});
	}
}

// アプリケーション起動クラス
public class QueensApp {
	public static void main(String[] args) {
		new Queens().start(8);			// クイーン数(行数)を指定して開始
	}
}
