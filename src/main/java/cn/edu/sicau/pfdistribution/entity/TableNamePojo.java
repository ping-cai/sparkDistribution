package cn.edu.sicau.pfdistribution.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
public class TableNamePojo {
    private Map<String, String> theStatic;
    private Map<String, String> theDynamic;

    public TableNamePojo(String year) {
        HashMap<String, String> staticTable = new HashMap<>();
        staticTable.put("oneSectionTable", String.format("SCOTT.ONE_SC_%s", year));
        staticTable.put("oneStationTable", String.format("SCOTT.ONE_ST_%s", year));
        staticTable.put("oneTransferTable", String.format("SCOTT.ONE_TS_%s", year));
        staticTable.put("halfSectionTable", String.format("SCOTT.HALF_SC_%s", year));
        staticTable.put("halfStationTable", String.format("SCOTT.HALF_ST_%s", year));
        staticTable.put("halfTransferTable", String.format("SCOTT.HALF_TS_%s", year));
        staticTable.put("quarterSectionTable", String.format("SCOTT.QUA_SC_%s", year));
        staticTable.put("quarterStationTable", String.format("SCOTT.QUA_ST_%s", year));
        staticTable.put("quarterTransferTable", String.format("SCOTT.QUA_TS_%s", year));
        this.theStatic = staticTable;
        HashMap<String, String> dynamicTable = new HashMap<>();
        dynamicTable.put("oneSectionTable", String.format("SCOTT.DN_ONE_SC_%s", year));
        dynamicTable.put("oneStationTable", String.format("SCOTT.DN_ONE_ST_%s", year));
        dynamicTable.put("oneTransferTable", String.format("SCOTT.DN_ONE_TS_%s", year));
        dynamicTable.put("halfSectionTable", String.format("SCOTT.DN_HALF_SC_%s", year));
        dynamicTable.put("halfStationTable", String.format("SCOTT.DN_HALF_ST_%s", year));
        dynamicTable.put("halfTransferTable", String.format("SCOTT.DN_HALF_TS_%s", year));
        dynamicTable.put("quarterSectionTable", String.format("SCOTT.DN_QUA_SC_%s", year));
        dynamicTable.put("quarterStationTable", String.format("SCOTT.DN_QUA_ST_%s", year));
        dynamicTable.put("quarterTransferTable", String.format("SCOTT.DN_QUA_TS_%s", year));
        this.theDynamic = dynamicTable;
    }
}
