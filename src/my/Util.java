package my;

import java.util.function.Supplier;

public class Util {
	public static int level = 0;

	// 再帰呼び出し過程を追跡出力するメソッド
	public static <T> T trace(String fname, String[] args,  Supplier<T> fun) {
		String s = new String(new char[level]).replace("\0", "- ") + level + ": " + fname;
		System.out.println(s + " (" + String.join(",", args) + ")");
		level ++;
		T ret = fun.get();
		level --;
		System.out.println(s + " =" + ret);
		return ret;
	}

	public interface SimpleFun { void action(); }

	public static void time(String s, SimpleFun fun) {						// 時間計測用
		long start = System.currentTimeMillis();
		fun.action();
		long end = System.currentTimeMillis();
		System.out.printf("%s: %d[ms]\n", s, end - start);
	}
}