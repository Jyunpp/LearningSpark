package jyunpp.study.ch4;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.Tuple2;

import java.util.Arrays;

public class PairRDDPractice {

	private static SparkConf sparkConf;
	private static JavaSparkContext javaSparkContext;

	@BeforeClass
	public static void setUp() {
		sparkConf = new SparkConf().setMaster("local").setAppName("PairRDD practice");
		javaSparkContext = new JavaSparkContext(sparkConf);
	}

	@Test
	public void PiarRDD_연습_특정_키값_필터() {
		JavaRDD<String> inputRDD = getResource("pairRDDFilterText");

		// functional interface PairFunction<T, K, V> extends Serializable  {...} T -> K key, V value
		// 구분자 "," 첫문자열을 key, 전체를 value로 하는 pair 생성해주는 PairFunction
		PairFunction<String, String, String> createPairWithFirstStringAsKey = s -> new Tuple2<>(s.split(",")[0],s);
		// 그 PairFunction을 인자로받는 mapToPair
		JavaPairRDD<String, String> pairs = inputRDD.mapToPair(createPairWithFirstStringAsKey);
		/** 한 줄로 쓰면 다음과 같다. */
		/** JavaPairRDD<String, String> pairs = inputRDD.mapToPair(s -> new Tuple2<>(s.split(",")[0],s)); */

		// 키 값이 pass 면 true 반환하는 Function
		Function<Tuple2<String,String>, Boolean> pass = pair -> pair._1().equals("pass");
		// 그 Function으로 filter 후 count()
		System.out.println("count : " + pairs.filter(pass).count());
		/** 한 줄로 쓰면 다음과 같다. */
		/** System.out.println("count : " + pairs.filter(pair -> pair._1().equals("pass")).count()); */
	}

	@Test
	public void PairRDD_연습_단어별로_개수세기() {
		JavaRDD<String> inputRDD = getResource("pairRDDCountWordText");
		keyToCount(inputRDD).foreach(pair -> System.out.println(pair._1().concat(" : ".concat(pair._2().toString()))));
	}

	@Test
	public void PairRDD_연습_combineByKey_키별_평균() {
		JavaRDD<String> inputRDD = getResource("pairRDDCombineByKeyText");

		// 작성 중

	}


	public JavaPairRDD<String, Integer> keyToCount(JavaRDD<String> input) {
		JavaRDD<String> words = input.flatMap( s -> Arrays.asList(s.split(" ")));
		return words.mapToPair( s -> new Tuple2<>(s, 1)).reduceByKey(Math::addExact);
	}

	public JavaRDD<String> getResource(String fileName) {
		return javaSparkContext.textFile(getClass().getClassLoader().getResource(fileName).getFile());
	}
}
