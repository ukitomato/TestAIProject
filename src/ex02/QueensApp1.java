package ex02;

import java.util.function.Supplier;
import my.Cons;

// Nクイーン問題クラス(再帰トレース付きバージョン，Queensクラスから派生)
class Queens1 extends Queens {
	int level = 0;			// 再帰レベル

	// 再帰呼び出し過程を追跡出力するメソッド
	<T> T trace(String fname, String[] args, Supplier<T> fun) {
		String s = new String(new char[level]).replace("\0", "- ") + level + ": " + fname;
		System.out.println(s + " (" + String.join(",", args) + ")");
		level++;
		T ret = fun.get();
		level--;
		System.out.println(s + " =" + ret);
		return ret;
	}

	Cons queen(int r) {								// 配置リストを複数返す
		return trace("queen", new String[] { ""+r }, ( ) -> {
			if (r == 0) {
				return Cons.of(Cons.Nil);
			} else {
				return queen(r - 1).flatMap((Cons p) -> Cons.range(1, n+1)
									.filter((Integer c) -> check(r, c, p))
									.map(c -> new Cons(Cons.of(r, c), p)));
			}
		});
	}
}

// アプリケーション起動クラス
public class QueensApp1 {
	public static void main(String[] args) {
		new Queens1().start(4);						// 4x4で実行してみる
	}
}