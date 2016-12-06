package ex03;

import my.Cons;

// 宣教師とモンスターの川渡り問題クラス
class MissionariesAndCannibals  {
	Cons opAll = Cons.Nil;									// 可能な乗船パターン

	Cons move(Cons from, Cons to, Cons op) {
	 	Cons from1 = from.diff(op);					// 乗船者を取り除いた残り
		if (from1.length() == from.length() - op.length()) return Cons.of(from1, op.append(to));
		else return Cons.of(from, to);
	}

	boolean goal(Cons st) {								// この移動結果はゴールか？
		return st.head == Cons.Nil;						// 左岸が空
	}

	boolean safe(Cons st) {								// この移動結果は安全な状態か？
		return st.forall((Cons x) -> x.count("宣") == 0 || x.count("宣") >= x.count("モ"));
	}

	Cons solve(Cons st, Cons ops, int boat, Cons history) {	// 移動記録を返す
		if (ops == Cons.Nil) return Cons.Nil; 		// もう試すパターンが無いので失敗
		else {
			Cons op = (Cons)ops.head;
			Cons opTail = ops.tail;
			Cons dir, stNew;									// 移動記録作成 （方向，新たな状態）
			if (boat == -1) {
				dir = Cons.of('→');
				stNew = move(st.getC(0), st.getC(1), op).map((Cons x) -> x.sorted());
			} else {
				dir = Cons.of('←');
				stNew = move(st.getC(1), st.getC(0), op).reverse().map((Cons x) -> x.sorted());
			}
			if (goal(stNew)) {									// ゴールなら成功
				return new Cons(new Cons(op, dir, stNew), history);
			} else if (stNew.equals(st) || !safe(stNew) ||
						history.exists((Cons x) -> x.tail.equals(new Cons(dir, stNew)))) {
				// 無変化，移動不可，過去状態に戻ると失敗
				return solve(st, opTail, boat, history);							// 残りの操作を試す
			} else {																					// 移動成功
				Cons ret = solve(stNew, opAll, -boat,
					 new Cons(new Cons(op, dir, stNew), history));	// 新たな状態から進める
				if (ret != Cons.Nil) return ret;										// 成功ならそれを返す
				else return solve(st, opTail, boat, history);					// 失敗なら残り操作試す
			}
		}
	}

	void start() {
		// 可能な乗船パターン
		opAll = Cons.of(	Cons.of("宣","宣"), Cons.of("宣","モ"), Cons.of("モ","モ"),
									Cons.of("宣"), Cons.of("モ"))
						.map((Cons x) -> x.sorted());
		// 岸の初期状態
		Cons st = Cons.of(Cons.of("宣", "宣", "宣", "モ", "モ", "モ").sorted(), Cons.Nil);
		// 移動記録初期状態
		Cons history = Cons.of(new Cons(Cons.Nil, new Cons(Cons.of('←'), st)));
		// 問題解決
		Cons solution = solve(st, opAll, -1, history);

		System.out.println("移動者\t\t移動方向\t\t結果状態（左岸）\t結果状態（右岸）");
		solution.reverse().foreach((Cons x) -> System.out.println(
				x.map((Cons y) -> y.mkString("")).mkString("\t\t")));			// 移動記録表示
	}
}

// アプリケーション起動クラス
public class MissionariesAndCannibalsApp {
	public static void main(String[] args) {
		new MissionariesAndCannibals().start();
	}
}
