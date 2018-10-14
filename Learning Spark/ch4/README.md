# 페어 RDD 트랜스포메이션

 - 전체 노드 위에서 페어 RDD의 구조를 사용자가 제어할 수 있는 **파티셔닝**
 - 분산 데이터세트에서 올바른 파티셔닝 방법을 사용하는 것은 로컬 애플리케이션에서 올바른 자료구조를 선택하는 것과 비슷하게 중요하다.
 - 자바는 내장 튜플 타입이 없으므로 슻파크의 자바 API에서 scala.Tuple2 클래스를 써서 튜플을 만들 수 있도록 해준다.
   - ._1(), ._2()
   - mapToPair()를 통해 페어 RDD를 만듬
   - 메모리 데이터를 통해서 페어 RDD를 만들 땐 SparkContext.parallelizePairs()
 - 페어RDD도 기본 RDD에서 지원하는 함수는 그대로 사용가능.
  - ...

``` java
public class PairRddPractice {
  public void test(){
    SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Filter Test");
    JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
    JavaRDD<String> inputRDD = javaSparkContext.textFile(getClass().getClassLoader().getResource("pairRddPrac").getFile());

    /** functional interface PairFunction<T, K, V> extends Serializable  {...} */
    PairFunction<String, String, String> keyData = s -> new Tuple2<>(s.split(",")[0],s);

    JavaPairRDD<String, String> pairs = inputRDD.mapToPair(keyData);
    Function<Tuple2<String,String>, Boolean> pass = pair -> pair._1().equals("pass");

    System.out.println("count : " + pairs.filter(pass).count());
  }
}
```