package ex05;

public class  BackwardChain1App extends InferenceEngineBackward {
	static String[][] ruleData = {
		{
			"$x parent $y",							// xの親はyである
			"$y is-a $z",								// yはzである
			"->",											// ならば
			"$x is-a $z"									// xはzである
		}, {
			"$x has claws",							// xは鋭い爪を持つ
			"$x has forward_eyes",				// xは目が正面についている
			"->",											// ならば
			"$x is-a carnivore"					// xは肉食動物である
		}, {
			"$x is-a carnivore",					// xは肉食動物である
			"$x has black_stripes",				// xは黒い縞を持つ
			"$x says gaooooo",					// xはガオーと鳴く
			"->",											// ならば
			"$x is-a tiger	"							// xはトラである
		}, {
			"$x is-a carnivore",					// xは肉食動物である
			"$x has black_stripes",				// xは黒い縞を持つ
			"$x says nyaa",							// xはニャーと鳴く
			"->",											// ならば
			"$x is-a tabby"							// xはトラネコである
		}};

	static String[] factData = {
		"momo has claws",
		"momo has forward_eyes",
		"momo has black_stripes",
		"momo says nyaa",
		"mimi parent momo"
	};

	public static void main(String[] args) {
		new BackwardChain1App().backward(ruleData, factData,
					new String[] { "$who", "is-a", "$animal" });		// 後ろ向き推論実行
	}
}