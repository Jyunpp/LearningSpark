package jyunpp.study.ch4;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.junit.Test;
import scala.Tuple2;

public class PairRDDPractice {

	@Test
	public void PiarRDD_연습_특정_키값_필터(){
		SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("PairRDD Practice");
		JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
		JavaRDD<String> inputRDD = javaSparkContext.textFile(getClass().getClassLoader().getResource("pairRDDText").getFile());

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

}
