package cn.edu.sicau.pfdistribution.dao.tonghaoGet;

import cn.edu.sicau.pfdistribution.entity.jiaoda.GetSourceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author LiYongPing
 * @version 1.2.0
 * 车站换乘量数据数据库交互层（交大客流接口定义）
 */
@Repository
public class GetTransferData implements Serializable {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    /**
     * @return 获得OD_INFO表的所有测试原始数据
     */
    public List<GetSourceData> getSourceDataList() {
        String sql = "SELECT\n" +
                "\tTO_DATE(REPLACE(DATATIME, DATATIME,SUBSTR(DATATIME,1,4)||'-'||SUBSTR(DATATIME, 5, 2)||'-'||SUBSTR(DATATIME, 7, 2)), 'yyyy-mm-dd')  \"dataDT\",\n" +
                "\tTIME \"time\",\n" +
                "\tIN_STATIONID \"inStationId\",\n" +
                "\tIN_STATION \"inStation\",\n" +
                "\tOUT_STATIONID \"outStationId\",\n" +
                "\tOUT_STATION \"outStation\",\n" +
                "\tPASSENGERS \"passengers\" \n" +
                "FROM\n" +
                "\tSCOTT.OD_INFO \n" +
                "WHERE\n" +
                "\tROWNUM < 100";
        RowMapper<GetSourceData> rowMapper = new BeanPropertyRowMapper<>(GetSourceData.class);
        List<GetSourceData> getSource = jdbcTemplate.query(sql, rowMapper);
        return getSource;
    }

    /**
     * @param fromNode 换入线路的区间起点
     * @param toNode   换入线路的区间终点
     * @return 返回数据库中取得的换入换出线路
     */
    public String getLine(String fromNode, String toNode) {
        String getLineSql = "SELECT QJ_LJM line FROM SCOTT.\"dic_section\" WHERE CZ1_ID=? AND CZ2_ID=?";
        SqlRowSet Line = jdbcTemplate.queryForRowSet(getLineSql, fromNode, toNode);
        String LineString = "";
        if (Line.next()) {
            LineString = Line.getString("line");
        }
        return LineString;
    }

    /**
     * 数据库通过伪Id取换乘站线路名和车站名
     *
     * @param transferId 换乘站的伪Id
     * @return 返回真实的车站名和线路
     */
    public String[] getTransfer(String transferId) {
        String getTransfer = "SELECT CZ1_NAME station,QJ_LJM line FROM SCOTT.\"dic_section\" WHERE CZ1_ID=? AND ROWNUM<2";
        SqlRowSet transferIdAndLine = jdbcTemplate.queryForRowSet(getTransfer, transferId);
        String[] stationAndLine = new String[2];
        while (transferIdAndLine.next()) {
            stationAndLine[0] = transferIdAndLine.getString("station");
            stationAndLine[1] = transferIdAndLine.getString("line");
        }
        return stationAndLine;
    }

    /**
     * 通过真实存在的表转换伪id为真实id
     * 换乘站名以及线路名可以确定唯一id,即真实id
     *
     * @param transferId 换乘站的伪id
     * @return 换乘站的真实id
     */
    public String getTransferId(String transferId) {
        String transfer = "";
        String getTransferId = "SELECT\n" +
                "\tDISTINCT STATION_ID.STATION_ID id\n" +
                "FROM\n" +
                "\tSCOTT.STATION_ID\n" +
                "\tLEFT JOIN (\n" +
                "SELECT DISTINCT\n" +
                "\t\"dic_section\".CZ1_NAME station_name,\n" +
                "\t\"dic_section\".QJ_LJM line_qj_name\n" +
                "FROM\n" +
                "\tSCOTT.\"dic_section\"\n" +
                "\tLEFT JOIN SCOTT.STATION_ID ON STATION_ID.STAION = \"dic_section\".CZ1_NAME \n" +
                "\t) ON station_name = STATION_ID.STAION AND line_qj_name=STATION_ID.LINE WHERE STATION_ID.STAION=? AND STATION_ID.LINE=?";
        SqlRowSet id = jdbcTemplate.queryForRowSet(getTransferId, (Object[]) getTransfer(transferId));
        if (id.next()) {
            transfer = id.getString("id");
        }
        return transfer;
    }

    /**
     * 插入已分配数据到交大定义的接口表中
     *
     * @param date             数据日期
     * @param transfer         换乘点
     * @param inLineId         换入线路
     * @param outLineId        换出线路
     * @param upInUpOutNum     上进上出换乘量
     * @param upInDownOutNum   上进下出换乘量
     * @param downInUpOutNum   下进上出换乘量
     * @param downInDownOutNum 下进下出换乘量
     * @return 返回影响行数用于判断是否插入成功，复合主键
     */
    public int insertData(Date date, String transfer, String inLineId, String outLineId,
                          Integer upInUpOutNum, Integer upInDownOutNum, Integer downInUpOutNum, Integer downInDownOutNum) {
        String insertData = "INSERT into SCOTT.STATION_TRANSFER_VOLUME VALUES(?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(insertData, date, transfer, inLineId, outLineId, upInUpOutNum, upInDownOutNum, downInUpOutNum, downInDownOutNum);
    }

    /**
     * 更新已分配数据
     *
     * @param upInUpOutNum     上进上出换乘量
     * @param upInDownOutNum   上进下出换乘量
     * @param downInUpOutNum   下进上出换乘量
     * @param downInDownOutNum 下进下出换乘量
     * @param date             数据日期
     * @param transfer         换乘点
     * @param inLineId         换入线路
     * @param outLineId        换出线路
     * @return 返回更新影响行数，暂无具体作用
     */
    public int updateData(Integer upInUpOutNum, Integer upInDownOutNum, Integer downInUpOutNum, Integer downInDownOutNum, Date date, String transfer, String inLineId, String outLineId) {
        String updateDate = "UPDATE SCOTT.STATION_TRANSFER_VOLUME set TRAF_SIN_SOUT=TRAF_SIN_SOUT+?,TRAF_SIN_XOUT=TRAF_SIN_XOUT+?,TRAF_XIN_SOUT=TRAF_XIN_SOUT+?,TRAF_XIN_XOUT=TRAF_XIN_XOUT+?\n" +
                "WHERE XFER_STATION_ID=? AND TRAF_IN_LINE_ID=? AND TRAF_OUT_LINE_ID=? AND DATA_DT=?";
        return jdbcTemplate.update(updateDate, upInUpOutNum, upInDownOutNum, downInUpOutNum, downInDownOutNum, date, transfer, inLineId, outLineId);
    }
}

