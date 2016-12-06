package ex09;

public class VisualizerApp {
	public static void main(String[] args) {
		int r = 10;
		int c = 10;
		Data.readImage("train-images.idx3-ubyte", 100);
		Data.readLabel("train-labels.idx1-ubyte", 100);
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				System.out.printf("%d ", Data.labelData[i * c + j]);
				if (j == c-1) System.out.println();
			}
		}
		new Visualizer(28, 28, r, c,2).dispDataImage(Data.getData(Data.imgData));
	}
}
