package ex01;

import java.util.function.Supplier;

// 再帰呼び出しクラス
public class RecursiveCallApp {
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

	int fact(int x) {								// 階乗を求める再帰メソッド
		return trace("fact", new String[] { ""+x }, ( ) -> {
			if (x == 1) return 1;				// 1 の階乗は 1
			else return x * fact(x - 1);		// x の階乗は x * (x-1)!
		});
	}

	public static void main(String[] args) {
		RecursiveCallApp app = new RecursiveCallApp();
		System.out.println("階乗=" + app.fact(5));
	}
}