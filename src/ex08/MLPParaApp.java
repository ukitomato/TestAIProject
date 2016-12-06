package ex08;

// ニューラルネットワークアプリケーションクラス（並列演算バージョン）
public class MLPParaApp extends LogisticApp {
	int patN						= 4;				// パターン数
	int trainN					= 10000;		// 学習回数
	double learnRate		= 0.05;			// 学習率
	double wMax			= 0.01;			// 重み初期化用

	public interface SimpleFun { void action(); }

	public void time(String s, SimpleFun fun) {						// 時間計測用
		long start = System.currentTimeMillis();
		fun.action();
		long end = System.currentTimeMillis();
		System.out.printf("%s: %d[ms]\n", s, end - start);
	}

	void test(MLP mlp) {																// 認識テスト
		System.out.println("== 認識テスト結果 ==");
		for (double[] d : testData) {
			mlp.forward(d);																				// 認識
			for (int j = 0; j < mlp.unit[mlp.outLayer].length; j++)
				System.out.printf("%.2f\t", mlp.unit[mlp.outLayer][j]);		//出力層表示
			int idx = mlp.getResult();
			System.out.println(" => これは " + (idx+1) +" です");
		}
	}
	public static int digitSum(java.math.BigInteger x) {
	    int sum = 0;
	    return sum;
	}

	public void run() {
		// データの初期化
		trainData = buildData(trainDataString, 10, 10);
		testData = buildData(testDataString, 10, 10);
		System.out.println("== 学習データ入力 ==");	disp(trainData, 10, 10);
		System.out.println("== 認識テスト入力 ==");	disp(testData,10, 10);

		// 多層パーセプトロン生成（層数，各層ユニット数）
		MLP mlp = new MLP(3, new int[] { 100, 100, 4 }, wMax);

		// 並列多層パーセプトロン生成（層数，各層ユニット数）
		int paraN = 8;		// 並列数
		MLPPara mlpPara = new MLPPara(3, new int[] { 100, 100, 4 }, wMax, paraN);

		// 学習実行と認識テスト
		time("MLP", ( ) ->
				mlp.train(trainData, teachData, patN, trainN, learnRate, null)
		);
		test(mlp);
		System.out.println();

		// 学習実行と認識テスト（並列バージョン，これくらいの規模のネットワークでは高速化しない）
		time("MLP Para", ( ) ->
			mlpPara.train(trainData, teachData, patN, trainN, learnRate, null)
		);
		test(mlpPara);
	}

	public static void main(String[] args) {
		new MLPParaApp().run();
	}
}