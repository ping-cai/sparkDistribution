package cn.edu.sicau.pfdistribution.entity.correct;

import java.util.Objects;

public abstract class AbstractSectionCapacity {
    protected String inId;
    protected String outId;
    protected String inTime;
    protected String outTime;

    public String getInId() {
        return inId;
    }

    public String getOutId() {
        return outId;
    }

    public String getInTime() {
        return inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public AbstractSectionCapacity() {
    }

    public AbstractSectionCapacity(String inId, String outId, String inTime, String outTime) {
        this.inId = inId;
        this.outId = outId;
        this.inTime = inTime;
        this.outTime = outTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractSectionCapacity that = (AbstractSectionCapacity) o;
        return Objects.equals(inId, that.inId) &&
                Objects.equals(outId, that.outId) &&
                Objects.equals(inTime, that.inTime) &&
                Objects.equals(outTime, that.outTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inId, outId, inTime, outTime);
    }

    public void setInId(String inId) {
        this.inId = inId;
    }

    public void setOutId(String outId) {
        this.outId = outId;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }
}
