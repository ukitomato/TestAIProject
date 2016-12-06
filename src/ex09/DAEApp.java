package ex09;

import java.util.Arrays;

public class DAEApp {
	int patN						= 60000;			// パターン数
	int inputN					= 784;				// 入力層ユニット数
	int middleN				= 400;				// 中間層ユニット数
	int trainN					= 10;				// 学習回数
	int batchSize				= 20;				// バッチサイズ
	double learnRate		= 0.01;				// 学習率
	double noiseRate		= 0.5;				// ノイズ率
	double wgtDec			= 0.0002;			// 荷重減衰率

	void run() {
		// 学習画像データ読み込み
		double[][] trainData = Data.readImage("train-images.idx3-ubyte", patN);

		// デノイジングオートエンコーダ生成
		DAE ae = new DAE(inputN, middleN);
		// 学習
		ae.train(trainData, patN, trainN, batchSize, learnRate, noiseRate, wgtDec);

		// 表示用入出力データの用意（最初の100件を対象）
		int n = 100;
		double[][] in = 	Arrays.copyOf(trainData, n);	//	入力
		double[][] noized = new double[n][];				// ノイズ付き入力
		double[][] out = new double[n][];						// 出力
		// 出力の構築
		for (int i = 0; i < n; i++) {
			ae.addNoise(in[i], ae.nx, noiseRate);
			noized[i] = ae.nx.clone();
			ae.reconstruct(in[i]);
			out[i] = ae.z.clone();
		}

		new Visualizer(28, 28, 10, 10, 2).dispDataImage(noized);		// ノイズ画像表示
		new Visualizer(28, 28, 10, 10, 2).dispDataImage(in);				// 入力画像表示
		new Visualizer(28, 28, 10, 10, 2).dispDataImage(out);			// 出力画像表示
		new Visualizer(28, 28, 20, 20, 2).dispWeightImage(ae.w);	// 重み可視化
	}

	public static void main(String[] args) {
		new DAEApp().run();
	}
}
