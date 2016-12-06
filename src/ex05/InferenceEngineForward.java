package ex05;

import my.Cons;

// 前向き推論エンジンクラス
public class InferenceEngineForward {
	Cons rules;					// ルール
	Cons facts;					// 事実

	Cons ruleReader(String[][] s) {				// ルールデータの読み取り
		return Cons.fromArray(s).map((String[] x) ->
						Cons.fromArray(x).map((String y) ->
							Cons.fromArray(y.split(" ")))
						.split((Cons z) -> z.equals(Cons.of("->"))));
	}

	Cons factReader(String[] s) {						// 事実データの読み取り
		return Cons.fromArray(s).map((String x) -> Cons.fromArray(x.split(" ")));
	}

	String getEnv(Cons env, String var) {			// 環境変数から値を参照
		if (env == Cons.Nil) return null;				// 最後まで見つからなければnullを返す
		else if (env.getC(0).head.equals(var))
			return env.getC(0).getS(1);					// 見つかったら変数の値を返す
		else return getEnv(env.tail, var);				// 残りの環境変数リストを調べる
	}

	Cons patMatch(Cons p1, Cons p2, Cons env) {		// パターンマッチ
		if (p1 == Cons.Nil && p2 == Cons.Nil)
			return env;										// 両方が空ならマッチング成功
		else if (p1 == Cons.Nil  || p2 == Cons.Nil)
			return null;										// どちらか空なら失敗
		else {
			String a = p1.getS(0); 					// 各パターンの先頭をa，bにセット
			String b = p2.getS(0);
			Cons aa = p1.tail; 							// 各パターンの残りをaa，bbにセット
			Cons bb = p2.tail;
			if (a.charAt(0) == '$') {					// 環境変数なら
				String val = getEnv(env, a);
				if (val != null) {							// 環境に存在すれば値を取り出し比較
					return b.equals(val) ? patMatch(aa, bb, env) : null;
				} else {											// 環境に存在しなければ追加
					return patMatch(aa, bb, new Cons(Cons.of(a, b), env));
				}
			} else if (a.equals(b)) return patMatch(aa, bb, env);
																	// 文字列比較
			else return null;
		}
	}

	String replaceVar(String s, Cons env) {
		if (s.charAt(0) == '$') {						// sが環境変数なら値を返す
			String val = getEnv(env, s);
			return val != null ? val : s;				// 存在しなければsを返す
		} else {
			return s;											// そのままsを返す
		}
	}

	Cons applyEnv(Cons action, Cons env) {		// アクションから事実を生成
		if (action == Cons.Nil) return Cons.Nil;
		else return new Cons(replaceVar(action.getS(0), env), applyEnv(action.tail, env));
	}

	Cons newFacts(Cons actions, Cons env) {		// 新事実生成
		if (actions == Cons.Nil) return Cons.Nil;
		else {
			Cons f = applyEnv(actions.getC(0), env);					// 一つのアクションから事実生成
			if (!facts.contains(f)) {
				return new Cons(f, newFacts(actions.tail, env));	// 新事実なら追加
			} else {
				return newFacts(actions.tail, env); 							// 残りのアクションも再帰処理
			}
		}
	}

	Cons ruleMatch(Cons patterns, Cons env) {
		if (patterns == Cons.Nil) return Cons.of(env);
		else 	// 前提条件の一つにマッチすれば，残りも調べ，環境変数の組み合わせを生成
			return facts.map((Cons x) -> patMatch(patterns.getC(0), x, env))
												.filter((Cons y) -> y != null)
												.flatMap((Cons z) -> ruleMatch(patterns.tail, z));
	}

	void forward(String[][] ruleString, String[] factString) {		// 前向き推論実行
		rules = ruleReader(ruleString);
		facts = factReader(factString);
		System.out.println("--- 生成された事実 -------------------------");
		while (true) {
			// 環境変数の組み合わせを適用する
			if (!rules.exists((Cons r) ->
				ruleMatch(r.getC(0), Cons.Nil).exists((Cons e) -> {
					Cons fact = newFacts(r.tail.getC(0), e);					// 新事実生成を試みる
					if (fact != Cons.Nil) {
						fact.println();
						facts = facts.append(fact);									// 新事実をリストに追加
						return true;
					} else {
						return false;
					}
				}))
			) return;						// 新事実生成がなければ推論終了
		}
	}

	static String[][] ruleData = {				// ルール
		{
			"$x are mammals",					// x は哺乳類である
			"mammals have lungs",			// 哺乳類は肺を持つ
			"->",											// ならば
			"$x have lungs"							// x は肺を持つ
		}};

	static String[] factData = {				// 事実
		"mammals have lungs",				// 哺乳類は肺を持つ
		"whales are mammals"					// クジラは哺乳類である
	};

	public static void main(String[] args) {												// 動作テスト
		new InferenceEngineForward().forward(ruleData, factData);		// 前向き推論実行
	}
}