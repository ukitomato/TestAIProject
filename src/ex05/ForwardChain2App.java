package ex05;

public class ForwardChain2App extends InferenceEngineForward {
	static String[][] ruleData = {
		{
			"$x は $y である",
			"$y は $z である",
			"->",
			"$x は $z である"
		}, {
			"$x は 羽 を持つ",
			"->",
			"$x は 鳥 である"
		}};

	static String[] factData = {
		"ピヨ は 羽 を持つ",
		"鳥 は 動物 である"
	};

	public static void main(String[] args) {
		new ForwardChain2App().forward(ruleData, factData);		// 前向き推論実行
	}
}