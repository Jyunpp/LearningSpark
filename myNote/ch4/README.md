# 페어 RDD 트랜스포메이션

### 페어 RDD 만들기

 - 전체 노드 위에서 페어 RDD의 구조를 사용자가 제어할 수 있는 **파티셔닝**
 - 분산 데이터세트에서 올바른 파티셔닝 방법을 사용하는 것은 로컬 애플리케이션에서 올바른 자료구조를 선택하는 것과 비슷하게 중요하다.
 - 자바는 내장 튜플 타입이 없으므로 스파크의 자바 API에서 scala.Tuple2 클래스를 써서 튜플을 만들 수 있도록 해준다.
   - ._1(), ._2()
   - mapToPair()를 통해 페어 RDD를 만듬
   - 메모리 데이터를 통해서 페어 RDD를 만들 땐 SparkContext.parallelizePairs()
 - 페어RDD도 기본 RDD에서 지원하는 함수는 그대로 사용가능.

``` java
// PiarRDD_연습_특정_키값_필터
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

### 집합 연산

 - reduceByKey() : reduce() 
 - foldByKey() : fold() 

``` java
// PairRDD_연습_단어별로_개수세기
public JavaPairRDD<String, Integer> keyToCount(JavaRDD<String> input) {
    JavaRDD<String> words = input.flatMap( s -> Arrays.asList(s.split(" ")));
    return words.mapToPair( s -> new Tuple2<>(s, 1)).reduceByKey(Math::addExact);
}
```

 - combineByKey()
   - 대부분의 다른 키별 컴바이너들은 이를 기반으로 구현되어 있음.
   - 한 파티션 내의 데이터들을 하나씩 처리하며 각 데이터는 이전에 나온 적이 없는 키를 갖고 있을 수도 있고, 이전 데이터와 같은 키를 가질 수도 있다.
   - 만약 새로운 키라면 넘겨준 createCombiner() 함수를 써서 해당 키에 대한 accumulator의 초깃값을 만든다.
     - accumulator : 스파크에서는 계속 관리될 필요가 있는 값. (ex. 현재까지의 합, 혹은 최댓값) 을 저장해 두는 공유 변수를 뜻한다.
   - 출현한 적이 있는 키라면 mergeValue() 함수를 해당 키에 대한 accumulator의 현재 값과 새로운 값에 적용해서 합친다.
   - **각 파티션이 독립적으로 작업이 이루어지므로 동일 키에 대해 여러 개의 accumulator를 가질 수도 있다.
    각 파티션의 결과를 최종적으로 합칠 때, mergeCombiners()를 써서 합쳐지게 된다.**

``` java
// PairRDD_연습_combineByKey_키별_평균

``` 