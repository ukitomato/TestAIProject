package ex05;

public class BackwardChain2App extends InferenceEngineBackward {
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
			new BackwardChain2App().backward(ruleData, factData,
						new String[] { "ピヨ", "は", "$何", "である" });		// 後ろ向き推論実行
		}
	
}