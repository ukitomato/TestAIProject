package ex09;

class Param {						// パラメータ用クラス
	int[] saeUnitParam;			// 多層オートエンコーダの構成パラメータ
	double[][] preParam;		// プレトレーニング(DAE)用パラメータ
	double[] fineParam;			// ファインチューニング(MLP)用パラメータ

	public Param(int[] saeUnitParam, double[][] preParam, double[] fineParam) {
		this.saeUnitParam = saeUnitParam;
		this.preParam = preParam;
		this.fineParam= fineParam;
	}
}

public class SAEApp {
	int patN							= 60000;				// パターン数
	double[][] trainData;									// 学習用イメージ
	double[][] teachLabel;								// 学習用ラベル
	double[][] testData;									// 認識テスト用イメージ
	byte[] testLabelValue;								// 認識テスト用ラベル値

	Param param = new Param(
		// 多層オートエンコーダの生成(各層次元数)
		//							入力		中間	中間	出力
		new int[]				{	784,		400,		400,		10 },

		// プレトレーニング(DAE)用パラメータ
		//							学習回数 バッチサイズ  学習率   ノイズ率  荷重減衰率
		new double[][] {	{	15,		10,			0.03,			0.3,		0.0002	},
										{	15,		10,			0.03,			0.3,		0.0002	}	},

		// ファインチューニング(MLP)用パラメータ
		//						学習回数      学習率
		new double[]		{	15,			0.1  }
	);

	public void run() {							// 実行
		init();
		train(param);
	}

	void init() {
		// 手書きイメージ学習データ読み込み
		trainData = Data.readImage("train-images.idx3-ubyte", patN);
		teachLabel = Data.readLabel("train-labels.idx1-ubyte", patN);

		// 手書きイメージテストデータ読み込み
		int n = 10000;
		testData = Data.readImage("t10k-images.idx3-ubyte", n);
		Data.readLabel("t10k-labels.idx1-ubyte", n);
		testLabelValue = Data.labelData;
	}

	void train(Param p) {
		// プレトレーニング
		System.out.println("Pre training...");
		SAE sae = new SAE(p.saeUnitParam.length, p.saeUnitParam, 0.01);
		sae.preTrain(trainData, patN, p.preParam);

		// ファインチューニング
		System.out.println("Fine tuning...");
		sae.fineTune(trainData, teachLabel, patN, p.fineParam,
			(t, err) -> {
				double rate = test(sae);			// 学習ループのたびに認識テスト
				System.out.printf("Training = %2d, Recognition = %.2f%%\n", t+1, rate);
			}
		);
		// 重み可視化
		new Visualizer(28, 28, 20, 20, 2).dispWeightImage(sae.ae[0].w);
	}

	double test(SAE sae) {						// 認識テスト
		int n = 10000;
		int count = 0;
		for (int i = 0; i < n; i++) {
			int label = testLabelValue[i];
			sae.forward(testData[i]);
			int result = sae.getResult();
			if (result == label) count++;
		}
		return count * 100.0 / n;
	}

	public static void main(String[] args) {
		new SAEApp().run();
	}
}