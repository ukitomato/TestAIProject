package ex09;

public class AEApp {
	int patN						= 60000;			// パターン数
	int inputN					= 784;				// 入力層ユニット数
	int middleN				= 400;				// 中間層ユニット数
	int trainN					= 10;				// 学習回数
	int batchSize				= 20;				// バッチサイズ
	double learnRate		= 0.01;				// 学習率

	public void run() {
		// 学習画像データ読み込み
		double[][] trainData = Data.readImage("train-images.idx3-ubyte", patN);
		
		// オートエンコーダ生成
		AE ae = new AE(inputN, middleN);
		// 学習
		ae.train(trainData, patN, trainN, batchSize, learnRate);
	
		// 表示用入出力データの用意（最初の100件を対象）
		int n = 100;
		double[][] out = new double[n][];
		// 出力の構築
		for (int i = 0; i < n; i++) {
			ae.reconstruct(trainData[i]);
			out[i] = ae.z.clone();
		}
		// 入力画像表示
		new Visualizer(28, 28, 10, 10, 2).dispDataImage(trainData, n);	
		// 出力画像表示
		new Visualizer(28, 28, 10, 10, 2).dispDataImage(out, n);	
		// 重みの可視化
		new Visualizer(28, 28, 20, 20, 2).dispWeightImage(ae.w);
	}
	
	public static void main(String[] args) {
		new AEApp().run();
	}		
}