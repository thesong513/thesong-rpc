package org.thesong.thesongrpc.demo;

/**
 * @Author thesong
 * @Date 2020/11/24 16:59
 * @Version 1.0
 * @Describe
 */
public class ExpResponse {

    private long value;
    private long costInNanos;

    public ExpResponse() {
    }

    public ExpResponse(long value, long costInNanos) {
        this.value = value;
        this.costInNanos = costInNanos;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getCostInNanos() {
        return costInNanos;
    }

    public void setCostInNanos(long costInNanos) {
        this.costInNanos = costInNanos;
    }

}