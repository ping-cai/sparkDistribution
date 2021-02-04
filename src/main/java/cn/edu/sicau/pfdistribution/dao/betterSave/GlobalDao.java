package cn.edu.sicau.pfdistribution.dao.betterSave;

import cn.edu.sicau.pfdistribution.Utils.Constants;
import cn.edu.sicau.pfdistribution.entity.jiaoda.GlobalTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;

@Repository
@Slf4j
public class GlobalDao {
    @Autowired
    @Qualifier("oracleJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public void createGlobal() {
        Class<GlobalTransaction> globalTransactionClass = GlobalTransaction.class;
        String className = globalTransactionClass.getSimpleName();
        Field[] fields = globalTransactionClass.getFields();
        String sql = "create table SCOTT.%s (%s)";
        StringBuilder nameAndType = new StringBuilder();
        int length = fields.length;
        for (int i = 0; i < length - 1; i++) {
            nameAndType.append(fields[i].getName())
                    .append(" ")
                    .append(Constants.javaToOracle.get(fields[i].getType().getName()))
                    .append(",");
        }
        nameAndType.append
                (fields[length - 1].getName())
                .append(" ")
                .append(Constants.javaToOracle.get(fields[length - 1].getType().getName()));
        String createTableSql = String.format(sql, className, nameAndType);
        try {
            jdbcTemplate.execute(createTableSql);
            log.info("创建表成功！表名为{}", className);
        } catch (Exception e) {
            log.warn("创建表失败，原因是{}", e, e);
        }

    }

}
