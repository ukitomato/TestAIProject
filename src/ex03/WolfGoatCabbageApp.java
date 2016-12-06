package ex03;

import my.Cons;

// 農民と狼とヤギとキャベツの川渡り問題クラス（宣教師とモンスターのクラスを継承）
class WolfGoatCabbage extends MissionariesAndCannibals {
	boolean safe(Cons st) {					// この移動結果は安全な状態か？
		return !st.exists((Cons x) ->
			!x.contains("農") && x.contains("ヤ") && (x.contains("狼") || x.contains("キ")));
	}

	void start() {
		// 可能な乗船パターン
		opAll = Cons.of(	Cons.of("農", "狼"), Cons.of("農", "ヤ"), Cons.of("農", "キ"),
									Cons.of("農")).map((Cons x) -> x.sorted());
		// 初期状態
		Cons st = Cons.of(Cons.of("農", "狼", "ヤ", "キ").sorted(), Cons.Nil);
		// 移動記録の初期状態
		Cons history = Cons.of(new Cons(Cons.Nil, Cons.of("←"), st));
		// 問題解決
		Cons solution = solve(st, opAll, -1, history);

		System.out.println("移動者\t\t移動方向\t\t結果状態（左岸）\t結果状態（右岸）");
		solution.reverse().foreach((Cons x) -> System.out.println(x.map((Cons y) ->
													y.mkString("")).mkString("\t\t")));	// 結果表示
	}
}

// アプリケーション起動クラス
public class WolfGoatCabbageApp {
	public static void main(String[] args) {
		new WolfGoatCabbage().start();
	}
}
