package ex09;

public class DAEParaApp {
	int patN						= 60000;			// パターン数
	int inputN					= 784;				// 入力層ユニット数
	int middleN				= 400;				// 中間層ユニット数
	int trainN					= 10;				// 学習回数
	int batchSize				= 20;				// バッチサイズ
	double learnRate		= 0.01;				// 学習率
	double noiseRate		= 0.5;				// ノイズ率
	double wgtDec			= 0.0002;			// 荷重減衰率

	// 時間計測対象の処理本体用の関数型インタフェース
	public interface SimpleFun { void action(); }

	public void time(String s, SimpleFun fun) {			// 時間計測用
		long start = System.currentTimeMillis();
		fun.action();															// 処理本体の実行
		long end = System.currentTimeMillis();
		System.out.printf("%s: %d[ms]\n", s, end - start);
	}

	void run() {
		// 学習画像データ読み込み
		double[][] trainData = Data.readImage("train-images.idx3-ubyte", patN);

		time("DAE\t\t", () -> {
			// デノイジングオートエンコーダ生成
			DAE ae = new DAE(inputN, middleN);
			// 学習
			ae.train(trainData, patN, trainN, batchSize, learnRate, noiseRate, wgtDec);
		});

		time("DAEPara\t", () -> {
			// 並列デノイジングオートエンコーダ生成
			int paraN = 8;			// 並列数
			DAE ae = new DAEPara(inputN, middleN, paraN);
			// 学習
			ae.train(trainData, patN, trainN, batchSize, learnRate, noiseRate, wgtDec);
		});
	}

	public static void main(String[] args) {
		new DAEParaApp().run();
	}
}
