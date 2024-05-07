package io.github.burukeyou.dataframe.iframe.window.round;

/**
 * @author caizhihao
 */
public class Round {

    private Round(){}

    public static final WindowRound START_ROW = new StartRowRound();

    public static final WindowRound CURRENT_ROW = new CurrentRowRound();


    public static final WindowRound END_ROW = new EndRowRound();


    public static WindowRound BEFORE(int n){
        return new BeforeRound(n);
    }

    public static WindowRound AFTER(int n){
        return new AfterRound(n);
    }

}