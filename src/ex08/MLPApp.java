package ex08;

// ニューラルネットワークアプリケーションクラス
public class MLPApp {
	int patN						= 4;				// パターン数
	int trainN					= 10000;		// 学習回数
	double learnRate		= 0.1;	 		// 学習率
	double wMax			= 1.0; 			// 重み初期化用

	double[][] trainData = {				// 学習データ(XOR， 線形分離不可能パターン)
			{ 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 }
	};

	double[][] teachData= {				// 教師データ
			{ 0 }, { 1 }, { 1 }, { 0 }
	};

	double[][] testData = {					// テストデータ
			{ 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 }
	};

	public void run() {
		//ネットワーク生成（層数，各層ユニット数）
		MLP mlp = new MLP(3, new int[] { 2, 2, 1 }, wMax);

		// 学習実行
		mlp.train(trainData, teachData, patN, trainN, learnRate,
				(t, err) -> {
					if (t % 1000 == 0) System.out.printf("%5d %f\n", t, err);
				});

		// 認識テスト
		System.out.println("--- 認識テスト ----------------\n入力\t\t出力");
		for (double[] d : testData) {
			mlp.forward(d);
			for (int i = 0; i < d.length; i++) {
				System.out.printf("%.0f\t", d[i]);
			}
			for (int j = 0; j < mlp.unit[2].length; j++) {
				System.out.printf("%.2f\t", mlp.unit[2][j]);
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		new MLPApp().run();
	}
}
