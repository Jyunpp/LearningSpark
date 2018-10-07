# RDD로 프로그래밍하기

스파크는 자동으로 RDD에 있는 데이터들을 클러스터에 분배하며, 클러스터 위에서 수행하는 연산들을 병렬화한다.

RDD는 분산되어있는 변경 불가느한 객체 모음이다. 각 RDD는 클러스터의 서로 다른 노드들에서 연산 가능하도록 여러 개의 파티션으로 나뉜다.

RDD는 외부 데이터세트를 로드하거나 객체 컬렉션을 분산시키는 두 가지 방법 중 하나로 만들 수 있다.

RDD는 **Transformation**과 **Action** 두 가지 타입의 연산을 지원한다.

- **Transformation** 은 존재하는 RDD에서 새로운 RDD를 만들어낸다.
- **Action**은 결과 값을 계산하여 드라이버 프로그램에 되돌려 주거나 외부 스토리지에 저장하기도한다(ex: HDFS)
- 두 연산 중 무슨 연산인지 헷갈리다면 반환타입이 RDD인지 아닌지를 보라..

RDD는 **lazy evaluation**으로 처음 액션을 사용하는 시점에 처리된다.

RDD는 매번 새로 연산을 한다. 재사용하고싶다면 RDD.persist()

첫 연산 후 RDD의 내용을 메모리에 저장하며 디스크에도 저장할 수 있다.

dependency 추가

``` gradle
compile group: 'org.apache.spark', name: 'spark-core_2.10', version: '1.3.1'
```

filter 테스트

``` java
public class Filter {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Filter Test");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        
        JavaRDD<String> inputRDD = sc.textFile("/Users/johnnykim/STUDY/spark/filterTest");
        
        //JavaRDD<String> errorRDD = inputRDD.filter(s-> s.contains("error"));
        JavaRDD<String> errorRDD = inputRDD.filter(
                new Function<String, Boolean>() {
                    @Override 
                    public Boolean call(String s) throws Exception { 
                        return s.contains("error"); 
                    }
                });

        int numOfErrors = (int) errorRDD.count();
        System.out.println("Input had " + numOfErrors + " erros");
        for(String line : errorRDD.take(numOfErrors)){
            System.out.println(line);
        }
    }
}
```

filter() 연산은 이미 존재하는 inputRDD를 변경하지 않는다. (앞서 RDD는 '변경 불가능한...' 이라고 설명) transformation 과정에서 RDD 자체를 변경하는 것이 아니라 완전히 새로운 RDD를 만들어내는 것.

take() : 드라이버 프로그램에서 RDD의 데이터 **일부**를 가져오기 위해 사용

collect() : **전체** 데이터 세트를 단일 컴퓨터 메모리에 올릴 때. 메모리 크기가 데이터 세트보다 작으면 collect()를 사용할 수 없다.

**lazy evaluation** : transformation 요청이 들어오면 바로 수행이 아니라, 스파크의 metadata에 요청이 들어왔다는 사실만을 기록. 데이터 로드도 마찬가지 방식으로. sc.textFile() 호출 시 실제 필요한 시점이 되기 전까지는 데이터 로딩이 되지않음.

=> 연산들을 그룹지어서 데이터를 전달해야하는 횟수를 줄일 수 있다.

(cf. Hadoop MapReduce 는 데이터 전달 횟수를 줄이기 위해 연산들을 어떤 식으로 그룹화할지 개발자가 직접 고민해야함.(?))
