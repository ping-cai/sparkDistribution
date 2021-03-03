package cn.edu.sicau.pfdistribution.entity.correct;

public class SectionCapacity extends AbstractSectionCapacity {

    public SectionCapacity() {
    }

    public SectionCapacity(String inId, String outId, String inTime, String outTime) {
        super(inId, outId, inTime, outTime);
    }
    @Override
    public String getInId() {
        return super.getInId();
    }

    @Override
    public String getOutId() {
        return super.getOutId();
    }

    @Override
    public String getInTime() {
        return super.getInTime();
    }

    @Override
    public String getOutTime() {
        return super.getOutTime();
    }

    @Override
    public void setInId(String inId) {
        super.setInId(inId);
    }

    @Override
    public void setOutId(String outId) {
        super.setOutId(outId);
    }

    @Override
    public void setInTime(String inTime) {
        super.setInTime(inTime);
    }

    @Override
    public void setOutTime(String outTime) {
        super.setOutTime(outTime);
    }
}
