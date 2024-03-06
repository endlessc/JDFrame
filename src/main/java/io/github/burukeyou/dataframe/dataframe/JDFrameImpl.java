package io.github.burukeyou.dataframe.dataframe;


import io.github.burukeyou.dataframe.JDFrame;
import io.github.burukeyou.dataframe.dataframe.item.FT2;
import io.github.burukeyou.dataframe.dataframe.item.FT3;
import io.github.burukeyou.dataframe.dataframe.item.FT4;
import io.github.burukeyou.dataframe.util.CollectorsPlusUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;


/**
 * @author caizhihao
 */
public class JDFrameImpl<T> extends AbstractDataFrame<T> implements JDFrame<T> {

    public List<T> dataList;

    public JDFrameImpl(List<T> list) {
        dataList = list;
    }

    @Override
    public  Stream<T> stream(){
        return dataList.stream();
    }

    public <R> JDFrameImpl<R> read(Stream<R> stream){
        return read(stream.collect(toList()));
    }

    public <R> JDFrameImpl<R> read(List<R> list) {
        return new JDFrameImpl<>(list);
    }

    /**
     * ===========================    =====================================
     **/
    public List<T> toLists() {
        return dataList;
    }

    /**
     * ===========================   排序相关  =====================================
     **/

    @Override
    public JDFrameImpl<T> sortDesc(Comparator<T> comparator) {
        return read(stream().sorted(comparator.reversed()));
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> sortDesc(Function<T, R> function) {
        sortDesc(Comparator.comparing(function));
        return this;
    }

    @Override
    public JDFrameImpl<T> sortAsc(Comparator<T> comparator) {
        return read(stream().sorted(comparator));
    }

    @Override
    public <R extends Comparable<R>> JDFrameImpl<T> sortAsc(Function<T, R> function) {
        return sortAsc(Comparator.comparing(function));
    }

    /** ===========================   截取相关  ===================================== **/

    /**
     * 截取前n个
     */
    @Override
    public JDFrameImpl<T> first(int n) {
        DFList<T> first = new DFList<>(toLists()).first(n);
        return read(first.build());
    }

    /**
     * ===========================   筛选相关  =====================================
     **/
    public <R> JDFrame<T> whereNotNull(Function<T, R> function) {
        return returnDF(whereNotNullStream(function));
    }

    public <R extends Comparable<R>> JDFrame<T> whereBetween(Function<T, R> function, R start, R end) {
        if (start == null && end == null) {
            return this;
        }
        return returnDF(whereBetweenStream(function,start,end));
    }


    public <R extends Comparable<R>> JDFrame<T> whereBetweenR(Function<T, R> function, R start, R end) {
        // 筛选条件都不存在默认不筛选
        if (start == null && end == null) {
            return this;
        }
        return returnDF(whereBetweenRStream(function,start,end));
    }


    public <R extends Comparable<R>> JDFrame<T> whereNotBetween(Function<T, R> function, R start, R end) {
        if (start == null || end == null) {
            return this;
        }
        return returnDF(whereNotBetweenStream(function,start,end));
    }


    public <R> JDFrame<T> whereIn(Function<T, R> function, List<R> list) {
        if (list == null || list.isEmpty()) {
            return this;
        }
        return returnDF(whereInStream(function,list));
    }


    public <R> JDFrame<T> whereNotIn(Function<T, R> function, List<R> list) {
        if (list == null || list.isEmpty()) {
            return this;
        }
        return returnDF(whereNotInStream(function,list));
    }

    public JDFrame<T> whereTrue(Predicate<T> predicate) {
        return returnDF(stream().filter(predicate));
    }


    public JDFrame<T> whereNotTrue(Predicate<T> predicate) {
        return whereTrue(predicate.negate());
    }


    public <R> JDFrame<T> whereEq(Function<T, R> function, R value) {
        if (null == value) {
            return this;
        }
        return  returnDF(whereEqStream(function,value));
    }


    public <R> JDFrame<T> whereNotEq(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return returnDF(whereNotEqStream(function,value));
    }


    public <R extends Comparable<R>> JDFrame<T> whereGt(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return returnDF(whereGtStream(function,value));
    }


    public <R extends Comparable<R>> JDFrame<T> whereGe(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return returnDF(whereGeStream(function,value));
    }


    public <R extends Comparable<R>> JDFrame<T> whereLt(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return returnDF(whereLtStream(function,value));
    }


    public <R extends Comparable<R>> JDFrame<T> whereLe(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return returnDF(whereLeStream(function,value));
    }


    public <R> JDFrame<T> whereLike(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return returnDF(whereLikeStream(function,value));
    }


    public <R> JDFrame<T> whereNotLike(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return returnDF(whereNotLikeStream(function,value));
    }


    public <R> JDFrame<T> whereLikeLeft(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return returnDF(whereLikeLeftStream(function,value));
    }


    public <R> JDFrame<T> whereLikeRight(Function<T, R> function, R value) {
        if (value == null) {
            return this;
        }
        return returnDF(whereLikeRightStream(function,value));
    }







    /** ===========================   分组相关  ===================================== **/


    public <K> JDFrame<FT2<K, BigDecimal>> groupBySum(Function<T, K> K,
                                                     ToBigDecimalFunction<T> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimal(value);
        List<FT2<K, BigDecimal>> collect = group(K, tBigDecimalCollector);
        return returnDF(collect);
    }


    public <K, J> JDFrame<FT3<K, J, BigDecimal>> groupBySum(Function<T, K> K,
                                                           Function<T, J> K2,
                                                           ToBigDecimalFunction<T> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimal(value);
        List<FT3<K, J, BigDecimal>> collect = group(K, K2, tBigDecimalCollector);
        return returnDF(collect);
    }



    public <K, J, H> JDFrame<FT4<K, J, H, BigDecimal>> groupBySum(Function<T, K> K,
                                                                 Function<T, J> J,
                                                                 Function<T, H> H,
                                                                 ToBigDecimalFunction<T> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimal(value);
        List<FT4<K, J, H, BigDecimal>> collect = group(K, J, H, tBigDecimalCollector);
        return returnDF(collect);
    }


    public <K> JDFrame<FT2<K, Long>> groupByCount(Function<T, K> K) {
        Collector<Object, ?, Long> counting = counting();
        Map<K, Long> collect = stream().collect(groupingBy(K, counting));
        return returnDF(convertToDataFrameItem2(collect));
    }


    public <K, J> JDFrame<FT3<K, J, Long>> groupByCount(Function<T, K> K,
                                                       Function<T, J> J) {
        Collector<Object, ?, Long> counting = counting();
        Map<K, Map<J, Long>> collect = stream().collect(groupingBy(K, groupingBy(J, counting)));
        return returnDF(convertToDataFrameItem3(collect));
    }


    public <K, J, H> JDFrame<FT4<K, J, H, Long>> groupByCount(Function<T, K> K,
                                                             Function<T, J> J,
                                                             Function<T, H> H) {
        Collector<Object, ?, Long> counting = counting();
        Map<K, Map<J, Map<H, Long>>> collect = stream().collect(groupingBy(K, groupingBy(J, groupingBy(H, counting))));
        return returnDF(convertToDataFrameItem4(collect));
    }


    public <K> JDFrame<FT3<K, BigDecimal,Long>> groupBySumCount(Function<T, K> K, ToBigDecimalFunction<T> value) {
        List<T> dataList = toLists();
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimal(value);
        List<FT2<K, BigDecimal>> sumList = returnDF(dataList).group(K, tBigDecimalCollector);
        List<FT2<K, Long>> countList =  read(dataList).groupByCount(K).toLists();
        Map<K, Long> countMap = countList.stream().collect(toMap(FT2::getC1, FT2::getC2));
        List<FT3<K, BigDecimal, Long>> collect = sumList.stream().map(e -> new FT3<>(e.getC1(), e.getC2(), countMap.get(e.getC1()))).collect(Collectors.toList());
        return returnDF(collect);
    }


    public <K, J> JDFrame<FT4<K, J, BigDecimal, Long>> groupBySumCount(Function<T, K> K,
                                                                      Function<T, J> J,
                                                                      ToBigDecimalFunction<T> value) {
        List<T> dataList = toLists();
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.summingBigDecimal(value);
        List<FT3<K, J, BigDecimal>> sumList = returnDF(dataList).group(K, J, tBigDecimalCollector);
        List<FT3<K, J, Long>> countList =  read(dataList).groupByCount(K, J).toLists();
        // 合并sum和count字段
        Map<String, FT3<K, J, Long>> countMap = countList.stream().collect(toMap(e -> e.getC1() + "_" + e.getC2(), Function.identity()));
        List<FT4<K, J, BigDecimal, Long>> collect = sumList.stream().map(e -> {
            FT3<K, J, Long> countItem = countMap.get(e.getC1() + "_" + e.getC2());
            return new FT4<>(e.getC1(), e.getC2(), e.getC3(), countItem.getC3());
        }).collect(Collectors.toList());
        return returnDF(collect);
    }


    public <K> JDFrame<FT2<K, BigDecimal>> groupByAvg(Function<T, K> K,
                                                     ToBigDecimalFunction<T> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.averagingBigDecimal(value, 2, BigDecimal.ROUND_HALF_UP);
        List<FT2<K, BigDecimal>> collect = group(K, tBigDecimalCollector);
        return returnDF(collect);
    }


    public <K, J> JDFrame<FT3<K, J, BigDecimal>> groupByAvg(Function<T, K> K,
                                                           Function<T, J> J,
                                                           ToBigDecimalFunction<T> value) {

        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.averagingBigDecimal(value, 2, BigDecimal.ROUND_HALF_UP);
        List<FT3<K, J, BigDecimal>> collect = group(K, J, tBigDecimalCollector);
        return returnDF(collect);
    }


    public <K, J, H> JDFrame<FT4<K, J, H, BigDecimal>> groupByAvg(Function<T, K> K,
                                                                 Function<T, J> J,
                                                                 Function<T, H> H,
                                                                 ToBigDecimalFunction<T> value) {
        Collector<T, ?, BigDecimal> tBigDecimalCollector = CollectorsPlusUtil.averagingBigDecimal(value, 2, BigDecimal.ROUND_HALF_UP);
        List<FT4<K, J, H, BigDecimal>> collect = group(K, J, H, tBigDecimalCollector);
        return returnDF(collect);
    }



    public <K, V extends Comparable<V>> JDFrame<FT2<K, T>> groupByMax(Function<T, K> K,
                                                                     Function<T, V> value) {
        Map<K, T> collect = stream().collect(groupingBy(K, collectingAndThen(toList(), e -> e.stream().min(Comparator.comparing(value)).orElse(null))));
        return returnDF(convertToDataFrameItem2(collect));
    }


    public <K, V extends Comparable<V>> JDFrame<FT2<K, T>> groupByMin(Function<T, K> K,
                                                                     Function<T, V> value) {
        Map<K, T> collect = stream().collect(groupingBy(K, collectingAndThen(toList(), e -> e.stream().min(Comparator.comparing(value)).orElse(null))));
        return returnDF(convertToDataFrameItem2(collect));
    }


    public <K, V extends Comparable<V>> JDFrame<FT2<K, MaxMin<V>>> groupByMaxAndMinValue(Function<T, K> K,
                                                                                        Function<T, V> value) {
        Map<K, MaxMin<V>> map = stream().collect(groupingBy(K, collectingAndThen(toList(), getListGroupMaxMinValueFunction(value))));
        return returnDF(convertToDataFrameItem2(map));
    }


    public <K, J, V extends Comparable<V>> JDFrame<FT3<K, J, MaxMin<V>>> groupByMaxAndMinValue(Function<T, K> K,
                                                                                              Function<T, J> J,
                                                                                              Function<T, V> value) {
        Map<K, Map<J, MaxMin<V>>> map = stream().collect(groupingBy(K, groupingBy(J, collectingAndThen(toList(), getListGroupMaxMinValueFunction(value)))));
        return returnDF(convertToDataFrameItem3(map));
    }


    public <K, V extends Comparable<V>> JDFrame<FT2<K, MaxMin<T>>> groupByMaxAndMin(Function<T, K> K,
                                                                                   Function<T, V> value) {
        Map<K, MaxMin<T>> map = stream().collect(groupingBy(K, collectingAndThen(toList(), getListGroupMaxMinFunction(value))));
        return returnDF(convertToDataFrameItem2(map));
    }


    public <K, J, V extends Comparable<V>> JDFrame<FT3<K, J, MaxMin<T>>> groupByMaxAndMin(Function<T, K> K,
                                                                                         Function<T, J> J,
                                                                                         Function<T, V> value) {
        Map<K, Map<J, MaxMin<T>>> map = stream().collect(groupingBy(K, groupingBy(J, collectingAndThen(toList(), getListGroupMaxMinFunction(value)))));
        return returnDF(convertToDataFrameItem3(map));
    }

    private <V extends Comparable<V>> Function<List<T>, MaxMin<V>> getListGroupMaxMinValueFunction(Function<T, V> value) {
        return list -> {
            if (list == null || list.isEmpty()) {
                return null;
            }
            MaxMin<V> maxMin = new MaxMin<>();
            maxMin.setMax(list.stream().max(Comparator.comparing(value)).map(value).orElse(null));
            maxMin.setMin(list.stream().min(Comparator.comparing(value)).map(value).orElse(null));
            return maxMin;
        };
    }

    private <V extends Comparable<V>> Function<List<T>, MaxMin<T>> getListGroupMaxMinFunction(Function<T, V> value) {
        return list -> {
            if (list == null || list.isEmpty()) {
                return new MaxMin<>();
            }
            MaxMin<T> maxMin = new MaxMin<>();
            maxMin.setMax(list.stream().max(Comparator.comparing(value)).orElse(null));
            maxMin.setMin(list.stream().min(Comparator.comparing(value)).orElse(null));
            return maxMin;
        };
    }

    protected <R> JDFrame<R> returnDF(Stream<R> stream) {
        return read(stream);
    }

    protected <R> JDFrameImpl<R> returnDF(List<R> dataList) {
        return read(dataList);
    }
}
