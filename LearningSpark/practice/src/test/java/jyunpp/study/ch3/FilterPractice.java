package jyunpp.study.ch3;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.junit.Test;

public class FilterPractice {

	@Test
	public void 필터_연습_error_포함_줄_개수() {
		SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Filter Practice");
		JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
		JavaRDD<String> inputRDD = javaSparkContext.textFile(getClass().getClassLoader().getResource("filterText").getFile());

		Function<String, Boolean> isContainError = s -> s.contains("error");
		JavaRDD<String> errorRDD = inputRDD.filter(isContainError);
		/** 한 줄로 쓰면 다음과 같다. */
		/** JavaRDD<String> errorRDD = inputRDD.filter(s -> s.contains("error")); */

		int numOfErrors = (int) errorRDD.count();
		System.out.println("Input had " + numOfErrors + " erros");
		for (String line : errorRDD.take(numOfErrors)) {
			System.out.println(line);
		}
	}

}
