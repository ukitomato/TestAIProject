package ex05;

import my.Cons;

// 後ろ向き推論エンジンクラス（前向き推論エンジンクラスを継承）
public class InferenceEngineBackward extends InferenceEngineForward {
	// パターンマッチ（p1，p2両方に変数可）
	Cons patMatchDual(Cons p1, Cons p2, Cons env, Cons sol) {
		// 両方が空ならマッチング成功
		if (p1 == Cons.Nil && p2 == Cons.Nil) return Cons.of(env, sol);
		// どちらか空なら失敗
		else if (p1 == Cons.Nil || p2 == Cons.Nil) return Cons.of(null, null);
		else {
			String a = p1.getS(0), b = p2.getS(0);
			Cons aa = p1.tail, bb = p2.tail;
			if (b.charAt(0) == '$') {							// p2側に変数がある場合
				String val = getEnv(sol, b);				// 解答変数を参照
				if (val != null) {									// 解答に存在すれば値を取り出し比較
					return a.equals(val) ? patMatchDual(aa, bb, env, sol) : null;
				} else {													// 解答に存在しなければ追加
					return patMatchDual(aa, bb, env, new Cons(Cons.of(b, a), sol));
				}
			} else if (a.charAt(0) == '$') {				// p1側に変数がある場合
				String val = getEnv(env, b);				// 環境変数を参照
				if (val != null) {									// 環境に存在すれば値を取り出し比較
					return b.equals(val) ? patMatchDual(aa, bb, env, sol) : null;
				} else {													// 環境に存在しなければ追加
					return patMatchDual(aa, bb, new Cons(Cons.of(a, b), env), sol);
				}
			} else if (a.equals(b)) return patMatchDual(aa, bb, env, sol);	// 文字列比較
			else return Cons.of(null, null);
		}
	}

	Cons actionMatch(Cons actions, Cons pat, Cons env, Cons sol) {
		if (actions == Cons.Nil) return Cons.of(null, null);
		else {	// patMatchDualの結果を返す，結果がnullならactionMatchの値を返す
			Cons ret = patMatchDual(actions.getC(0), pat, env, sol);
			return !ret.equals(Cons.of(null, null)) ? ret
										: actionMatch(actions.tail, pat, env, sol);
		}
	}

	Cons applyVal(Cons sol, Cons env) {
		// 解答変数の値が変数名ならenvから値を取得して置き換える
		return sol.map((Cons s) -> s.getS(1).charAt(0) == '$' ?
									Cons.of(s.getS(0), getEnv(env, s.getS(1))) : s);
	}

	Cons deduceFact(Cons pat, Cons pSet, Cons env) {		// 事実が導けるか調べる
		Cons sols = Cons.Nil;													// 解答変数のリスト
		sols = rules.flatMap((Cons r) -> {
			Cons conds = r.getC(0), acts = r.getC(1); // ruleから条件部とアクション部を取り出す
			Cons ret = actionMatch(acts, pat, Cons.Nil, Cons.Nil);	 // アクション部とマッチ試す
			Cons env1 = ret.getC(0), var1 = ret.getC(1);
			if (env1 != null && var1 != null &&
				!(env1.equals(Cons.of(null, null)) && var1.equals(Cons.of(null, null)))) {
				// マッチすれば条件部に対してさらに後ろ向き推論
				return backwardMatch(conds.map((Cons x) -> applyEnv(x, env1)),
									pSet, Cons.Nil)
									.map((Cons y) -> applyVal(var1.append(env), y))
									.map((Cons z) -> applyVal(z, env1));
			} else return Cons.Nil;
		});
		return sols;
	}

	Cons backwardMatch1(Cons pat, Cons pSet, Cons env) {
		if (pSet.contains(pat)) return Cons.Nil;		// 同じルールによるループ防止
		Cons pat1 = applyEnv(pat, env);					// 環境変数を適用しておく
		// 事実が存在するか調べる
		Cons sols1 = facts.map((Cons x) -> patMatch(pat1, x, env))
												.filter(y -> y != null);
		// 事実が導けるか調べる
		Cons sols2 = deduceFact(pat1, pSet.add(pat1), env);
		return sols1.append(sols2);							// それらの解を連結
	}

	Cons backwardMatch(Cons patterns, Cons pSet, Cons env) {
		if (patterns == Cons.Nil) return Cons.of(env);
		Cons pat = patterns.getC(0), tail = patterns.tail;
		// 1つのパターン（質問）に対して後ろ向き推論
		return backwardMatch1(pat, pSet, env).flatMap((Cons x) ->
			backwardMatch(tail, pSet, x));				// 残りパターンも後ろ向き推論
	}

	void backward(String[][] ruleString, String[] factString, String[] s) {	// 後ろ向き推論実行
		System.out.println("--- 質問 -------------------------------");
		System.out.println(Cons.fromArray(s).mkString(" "));
		rules = ruleReader(ruleString);
		facts = factReader(factString);
		Cons patterns = Cons.of(Cons.fromArray(s));
		Cons solutions = backwardMatch(patterns, Cons.Nil, Cons.Nil);
		System.out.println();

		System.out.println("--- 導き出された解 -------------------------");
		solutions.foreach((Cons x) -> x.println());
		System.out.println("--- 導き出された事実 -----------------------");
		solutions.foreach((Cons x) -> patterns.foreach((Cons y) -> applyEnv(y, x).println()));
	}

	static String[][] ruleData = {		// ルール
		{
			"$x are mammals",						// x は哺乳類である
			"mammals have lungs",				// 哺乳類は肺を持つ
			"->",												// ならば
			"$x have lungs"								// x は肺を持つ
		}};

	static String[] factData = {					// 事実
		"mammals have lungs",					// 哺乳類は肺を持つ
		"whales are mammals"						// クジラは哺乳類である
	};
	public static void main(String[] args) {		// 動作テスト
		new InferenceEngineBackward().backward(ruleData, factData,
				new String[] { "whales", "have", "$what" });		// 後ろ向き推論実行
	}
}
