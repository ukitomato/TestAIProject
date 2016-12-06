// 簡易リスト処理ライブラリ（コンスセル）

package my;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

// Nilクラス（空リストを表す）
class Nil extends Cons {
	public boolean equals(Object x) { return x instanceof Nil ? true : false; }

	public String toString() { return  "()"; }
}

// Consクラス（リストを構成するコンスセル）
public class Cons implements Comparable<Cons> {
	public static Cons Nil = new Nil();		// Nil: 空リストを表す
	public Object head = null;					// head: 先頭要素
	public Cons tail = Nil;							// tail: 先頭以外の残りのリスト

	public Cons() { }

	// Cons:	コンストラクタ，リストの作成
	//		a => (7, 8, 9)
	//		new Cons(1)			=> (1)
	//		new Cons(1, a)		=> (1, 7, 8, 9)
	//		new Cons(1, 2, a)	=> (1, 2, 7, 8, 9)
	public Cons(Object... x) {
		this();
		head = x[0];
		Cons cons = this;
		int n = x.length;
		for (int i = 1; i < n-1; i++) {
			cons.tail = new Cons();
			cons = cons.tail;
			cons.head = x[i];
		}
		if (n > 1) cons.tail = (Cons)x[n-1];
	}

	// Cons.of:	リストの作成
	//		Cons.of(1, 2, 3)	 => (1, 2, 3)
	public static Cons of(Object... x) {
		int n = x.length;
		Cons cons = Nil;
		for (int i = n-1; i >= 0; i--) {
			cons = new Cons(x[i], cons);
		}
		return cons;
	}

	// Cons.range:	連続数値リストの作成
	//		Cons.range(1, 4) => (1, 2, 3)
	public static Cons range(int from, int to) {
		return from == to ? Nil : new Cons(from, range(from + 1, to));
	}

	// Cons.fill:	初期値リストの作成
	//		Cons.fill(3, 0) => (0, 0, 0)
	public static Cons fill(Object x, int n) {
		if (n == 0) return Cons.Nil;
		else return new Cons(x, Cons.fill(x, n - 1));
	}

	// makeIntArray2:		二次元配列（整数）の作成
	//		Cons.makeIntArray2(3, 3, 0) => { {0, 0, 0}, {0, 0, 0}, {0, 0, 0} }
	public static Integer[][] makeIntArray2(int n, int m, Integer val) {
		Integer[][] a = Stream.generate(() ->
								new Integer[m]).limit(n).toArray(Integer[][]::new);
		for (int i = 0; i < n; i++) Arrays.fill(a[i], val);
		return a;
	}

	// makeIntArray2:		二次元配列（文字）の作成
	//		Cons.makeIntArray2(3, 3, 'a') => { {'a', 'a', 'a'}, {'a', 'a', 'a'}, {'a', 'a', 'a'} }
	public static Character[][] makeCharArray2(int n, int m, Character val) {
		Character[][] a = Stream.generate(() ->
								new Character[m]).limit(n).toArray(Character[][]::new);
		for (int i = 0; i < n; i++) Arrays.fill(a[i], val);
		return a;
	}

	// Cons.fromArray:	配列からの変換
	//		array => {1, 2, 3}
	//		Cons.fromArray(list) => (1, 2, 3)
	public static Cons fromArray(Object[] x) {
		int n = x.length;
		Cons cons = Nil;
		for (int i = n-1; i >= 0; i--) {
			cons = new Cons(x[i], cons);
		}
		return cons;
	}

	// Cons.fromList:	List型からの変換
	//		list => [1, 2, 3]
	//		Cons.fromList(list) => (1, 2, 3)
	public static Cons fromList(List<?> x) {
		int n = x.size();
		Cons cons = Nil;
		for (int i = n-1; i >= 0; i--) {
			cons = new Cons(x.get(i), cons);
		}
		return cons;
	}

	// fromStream:		Stream型からの変換
	//		stream => [1, 2, 3]
	//		Cons.fromStream(stream) => (1, 2, 3)
	public static Cons fromStream(Stream<?> s) {
		return Cons.fromArray(s.toArray());
	}

	// toArray:	配列への変換
	//		a => (1, 2, 3)
	//		a.toArray() => {1, 2, 3}
	public Object[] toArray() {
		return (Object[])toList().toArray(new Object[0]);
	}

	// toList:	List型への変換
	//		a => (1, 2, 3)
	//		a.toList() => (1, 2, 3)
	public List<Object> toList() {
		List<Object> list = new ArrayList<Object>();
		Cons c = this;
		while (c != Nil) {
			list.add(c.head);
			c = c.tail;
		}
		return list;
	}

	// stream:		Stream型への変換
	//		a => (1, 2, 3)
	//		a.toStream() => [1, 2, 3]
	public Stream<Object> toStream() {
		List<Object> list = new ArrayList<Object>();
		Cons c = this;
		while (c != Nil) {
			list.add(c.head);
			c = c.tail;
		}
		return list.stream();
	}

	// equals:		等しいか
	//		a => (1, 2, (3)),	b => (1, 2, (3))
	//		a.equals(b) => true
	public boolean equals(Object x) {
		if (x == null) return false;
		else if (this instanceof Nil && x instanceof Nil) return true;
		else if (this instanceof Nil || x instanceof Nil) return false;
		else if (this instanceof Cons && x instanceof Cons) {
			Cons a = this;
			Cons b = (Cons)x;
			if (a.head == null && b.head == null || a.head.equals(b.head)) {
				return a.tail.equals(b.tail);
			}
		}
		return false;
	}

	// compareTo:		比較
	//		a => (1, 2, 3),	b => (1, 2, 4)
	//		a.compareTo(b) => -1,		b.compareTo(a) => 1,	a.compareTo(a) => 0
	public int compareTo(Cons x) {
		if (x == null) return 1;
		else if (this instanceof Nil && x instanceof Nil) return 0;
		else if (this instanceof Nil) return -1;
		else if (x instanceof Nil) return 1;
		else if (this instanceof Cons && x instanceof Cons) {
			Cons a = this;
			Cons b = (Cons)x;
			if (a.head == null && b.head == null || a.head.equals(b.head)) {
				return a.tail.compareTo(b.tail);
			} else if (a.head instanceof Number &&  b.head instanceof Number) {
				Number aa = ((Number)a.head);
				Number bb = ((Number)b.head);
				return aa.intValue() < bb.intValue() ? -1 : aa.intValue() > bb.intValue() ? 1 : a.tail.compareTo(b.tail);
			} else if (a.head instanceof String &&  b.head instanceof String) {
				String aa = ((String)a.head);
				String bb = ((String)b.head);
				int cmp = aa.compareTo(bb);
				return  cmp < 0 ? -1 : cmp > 0 ? 1 : a.tail.compareTo(b.tail);
			} else {
				int cmp = a.head.toString().compareTo(b.head.toString());
				return  cmp < 0 ? -1 : cmp > 0 ? 1 : a.tail.compareTo(b.tail);
			}
		}
		return 0;
	}

	// toString:		文字列化
	//		a => (1, 2, (3))
	//		a.toString() => "(1, 2, (3))"
	public String toString() {
		return  "(" + toString1() + ")";
	}
	String toString1() {
		return  (head instanceof Cons ? head.toString()
						: head instanceof String ? "\"" + head.toString() + "\"" : head)
				+  (tail != Nil ? ", " + tail.toString1() : "");
	}

	// mkString:		区切り付き文字列化
	//		a => (1, 2, 3)
	//		a.mkString(":") => "1:2:3"
	public String mkString(String delim) {
		StringBuilder sb = new StringBuilder();
		Cons c = this;
		while (c != Nil) {
			if (c != this) sb.append(delim);
			sb.append(c.head);
			c = c.tail;
		}
		return sb.toString();
	}

	// split:		区切り文字で文字列を分割しリストにする
	//		Cons.split("a,b,c", ",") => ("1", "2", "3")
	public static Cons split(String s, String delim) {
		return Cons.fromArray(s.split(delim, 0));
	}

	// print, println:	コンソール出力
	//		a => (1, 2, (3))
	//		a.print() => 出力: (1, 2, (3))
	//		a.println() => 出力: (1, 2, (3)) 改行
	public void print() {
		System.out.print(toString());
	}
	public void println() {
		System.out.println(toString());
	}

	// length:		リストの長さ
	//		a => (1, 2, 3)
	//		a.length() => 3
	public int length() {
		if (this == Nil) return 0;
		else return 1 + tail.length();
	}

	// get:		要素アクセス（Object型で返す，結果利用はキャストが必要）
	//		a => (1, 2, 3)
	//		a.get(0) => 1	・・・ Object型，x = 5 + (Integer)a.get(0)
	public Object get(int i) {
		if (i == 0) return head;
		else return tail.get(i - 1);
	}

	// getI:		要素アクセス（Integer型で返す）
	//		a => (1, 2, 3)
	//		a.getI(0) => 1	・・・ Integer型，x = 5 + a.get(0)
	public Integer getI(int i) {
		if (i == 0) return (Integer)head;
		else return tail.getI(i - 1);
	}

	// getS:		要素アクセス（String型で返す）
	//		a => ("1", "2", "3")
	//		a.getS(0) => "1"	・・・ String型
	public String getS(int i) {
		if (i == 0) return (String)head;
		else return tail.getS(i - 1);
	}

	// getC:	要素アクセス（Cons型で返す）
	//		a => ((1, 2), (3))
	//		a.getC(0) => (1, 2)	・・・ Cons型
	public Cons getC(int i) {
		if (i == 0) return (Cons)head;
		else return tail.getC(i - 1);
	}

	// append:		リストの連結
	//		a => (1, 2, 3), b => (4, 5),
	//		a.append(b) => (1, 2, 3, 4, 5)
	public Cons append(Cons x) {
		return (this == Nil) ? x : new Cons(head, tail.append(x));
	}

	// add:		リスト末尾への追加
	//		a => (1, 2, 3), b => 4,
	//		a.add(b) => (1, 2, 3, 4)
	public Cons add(Object x) {
		return append(new Cons(x));
	}

	// reverse:		リスト逆順化
	//		a => (1, 2, 3)
	//		a.reverse() => (3, 2, 1)
	public Cons reverse() {
		return (this == Nil) ? this : tail.reverse().add(head);
	}

	// sorted:		リスト整列
	//		a => (2, 1, 3)
	//		a.sorted() => (1, 2, 3)
	public Cons sorted() {
		Object[] a = toArray();
		Arrays.sort(a);
		return Cons.fromArray(a);
	}

	// diff:		リスト要素の差集合
	//		a => (1, 2, 3), b => (2, 5)
	//		a.diff(b) => (1, 3)
	public Cons diff(Cons x) {
		if (x == Nil) return this;
		else return diff1(x.head).diff(x.tail);
	}
	Cons diff1(Object x) {
		if (this == Nil) return Nil;
		else if (head.equals(x)) return tail;
		else return new Cons(head, tail.diff1(x));
	}

	// find:		リスト要素の検索
	//		a => (1, 2, 3)
	//		a.find(2) => 2,	a.find(4) => null
	public Object find(Object x) {
		if (this == Nil) return null;
		else if (head.equals(x)) return x;
		else return tail.find(x);
	}

	// contains:		要素が含まれるか
	//		a => (1, 2, 3)
	//		a.contains(2) => true
	public boolean contains(Object x) {
		if (this == Nil) return false;
		else return head.equals(x) ? true : tail.contains(x);
	}

	// count:		要素のカウント
	//		a => (1, 2, 3, 3)
	//		a.count(3) => 2
	public int count(Object x) {
		if (this == Nil) return 0;
		else return (head.equals(x) ? 1 : 0) + tail.count(x);
	}

	// sum:		要素の合計
	//		a => (1, 2, 3)
	//		a.sum() => 6
	public int sum() {
		if (this == Nil) return 0;
		else return (Integer)head + tail.sum();
	}

	// foreach:		順次処理
	//		a => (1, 2, 3)
	//		a.foreach(x -> System.out.printf("%02d ", x)) => 出力: 01 02 03
	@SuppressWarnings("unchecked")
	public <T> void foreach(Consumer<T> fun) {
		if (this == Nil) return;
		else { fun.accept((T)head); tail.foreach(fun); }
	}

	// map:		順次処理（リストで返す）
	//		a => (1, 2, 3)
	//		a.map((Integer x) -> x * 2) => (2, 4, 6)
	@SuppressWarnings("unchecked")
	public <T, R> Cons map(Function<T, R> fun) {
		if (this == Nil) return Nil;
		else return new Cons(fun.apply((T)head), tail.map(fun));
	}

	// flatMap:		順次処理（リスト要素を結合して返す）
	//		a => ((1, 2, 3), (4, 5))
	//		a.flatMap((Cons x) -> x) => (1, 2, 3, 4, 5)
	@SuppressWarnings("unchecked")
	public <T> Cons flatMap(Function<T, Cons> fun) {
		if (this == Nil) return Nil;
		else return fun.apply((T)head).append(tail.flatMap(fun));
	}

	// forall:		すべて条件を満たすか
	//		a => (1, 2, 3, 4)
	//		a.forall((Integer x) -> x < 5) => true
	@SuppressWarnings("unchecked")
	public <T> boolean forall(Predicate<T> fun) {
		if (this == Nil) return true;
		else return fun.test((T)head) ? tail.forall(fun) : false;
	}

	// exists:		ひとつでも条件を満たすか
	//		a => (1, 2, 3, 4)
	//		a.exists((Integer x) -> x > 3) => true
	@SuppressWarnings("unchecked")
	public <T> boolean exists(Predicate<T> fun) {
		if (this == Nil) return false;
		else return fun.test((T)head) ? true : tail.exists(fun);
	}

	// count:	条件でカウント
	//		a => (1, 2, 3, 4)
	//		a.count((Integer x) -> x % 2 == 0) => 2
	@SuppressWarnings("unchecked")
	public <T> int count(Predicate<T> fun) {
		if (this == Nil) return 0;
		else return (fun.test((T)head) ? 1 : 0) + tail.count(fun);
	}

	// filter:		条件でフィルタリング
	//		a => (1, 2, 3, 4)
	//		a.filter((Integer x) -> x % 2 == 0) => (2, 4)
	@SuppressWarnings("unchecked")
	public <T> Cons filter(Predicate<T> fun) {
		if (this == Nil) return Nil;
		else return fun.test((T)head) ? new Cons(head, tail.filter(fun)) : tail.filter(fun);
	}

	// findPos:		条件で要素位置を検索
	//		a => (1, 2, 3, 4)
	//		a.findPos((Integer x) -> x % 3 == 0) => 2, a.findPos((Integer x) -> x == 0) => -1
	@SuppressWarnings("unchecked")
	public <T> int findPos(Predicate<T> fun) {
		if (this == Nil) return -1;
		else if (fun.test((T)head)) return 0;
		else {
			int pos = tail.findPos(fun);
			return pos == -1 ? -1 : pos + 1;
		}
	}

	// split:		条件でリストを分割
	//		a => (1, 2, 3, 4)
	//		a.split((Integer x) -> x == 3) => ((1, 2), ( 4))
	Cons splitLeft(int pos) {
		return pos <= 0 ? Nil : new Cons(head, tail.splitLeft(pos - 1));
	}
	Cons splitRight(int pos) {
		return pos <= 0 ? this : tail.splitRight(pos - 1);
	}
	public <T> Cons split(Predicate<T> fun) {
		int pos = findPos(fun);
		return Cons.of(splitLeft(pos), splitRight(pos+1));
	}


	// 動作テスト用
	public static void main(String[] args) {
		Cons a = Cons.of(1, 2, 3);
		Cons b = Cons.of(2, 2, "3");
		Cons c = Cons.of(Cons.of(1, 2, 3), Cons.of(4, 5), Cons.of(6));
		System.out.println("Nil\t= " + Nil);
		System.out.println("a\t= " + a);
		System.out.println("b\t= " + b);
		System.out.println("c\t= " + c);
		System.out.println("a.head\t= " + a.head);
		System.out.println("a.tail\t= " + a.tail);
		System.out.println("Cons.of(1, 2, 3)\t= " + Cons.of(1, 2, 3));
		System.out.println("Cons.range(1, 5)\t= " + Cons.range(1, 5));

		Integer[] e = { 1, 2, 3, };
		List<Integer> d = Arrays.asList(1, 2, 3);
		Stream<Integer> s = Stream.of(1, 2, 3);
		System.out.println("Cons.fromArray(e)\t= " + Cons.fromArray(e));
		System.out.println("Cons.fromList(d)\t= " + Cons.fromList(d));
		System.out.print("Cons to Stream\t= "); a.toStream().forEach(x -> System.out.print(x + " "));
		System.out.print("\n");
		System.out.println("Stream to Cons\t= " + Cons.fromStream(s));

		System.out.println("a.length()\t= " + a.length());
		System.out.println("a.get(0)\t= " + a.get(0));
		System.out.println("a.get(1)\t= " + a.get(1));
		System.out.println("a.append(b)\t= " + a.append(b));
		System.out.println("a.add(9)\t= " + a.add(9));
		System.out.println("a.reverse()\t= " + a.reverse());
		System.out.println("a.equals(c.head)\t= " + a.equals(c.head));
		System.out.println("c.find(a)\t= " + c.find(a));
		System.out.println("a.diff(b)\t= " + a.diff(b));
		System.out.println("b.count(2))\t= " + b.count(2));
		System.out.println("a.count((Integer x) -> x < 3)\t= " + a.count((Integer x) -> x < 3));
		System.out.println("a.forall((Integer x) -> x < 4)\t= " + a.forall((Integer x) -> x < 4));
		System.out.println("a.exists((Integer x) -> x > 1)\t= " + a.exists((Integer x) -> x > 1));
		System.out.print("a.foreach(x -> System.out.printf(\"%02d \", x))\t= ");
		a.foreach(x -> System.out.printf("%02d ", x)); System.out.println();
		System.out.println("a.map((Integer x) -> x * 2))\t= " + a.map((Integer x) -> x * 2));
		System.out.println("c.flatMap((Cons x) -> x)\t= " + c.flatMap((Cons x) -> x));
		System.out.println("a.filter((Integer x) -> x % 2 == 1))\t= " + a.filter((Integer x) -> x % 2 == 1));
		System.out.println("a.split((Integer x) -> x == 2)\t= " + a.split((Integer x) -> x == 2));
	}
}
