package it.hxl;

import it.hxl.entity.Student;
import it.hxl.sql.BaseDao;
import it.hxl.sql.JDBCConfig;
import org.junit.Test;

import java.util.List;

public class MyTest {
    @Test
    public void testV(){
        JDBCConfig build = JDBCConfig.builder()
                .driverClass("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/order?serverTimezone=Asia/Shanghai&&autoReconnect=true")
                .username("root")
                .password("qwe123").build();
//		new Thread(() -> {
//			System.out.println(new BaseDao<Student>(build).findByPk(1, Student.class));
//		}).start();
//		int i = new BaseDaoImpl(build).update(Student.builder().age(21).gender(1).id(1).no("201706051405").build());
//		System.out.println(new BaseDao<Student>(build).findByPk(2, Student.class))
        List<Student> all = new BaseDaoImpl(build).findAll();
        System.out.println(all);
    }
    public static class BaseDaoImpl extends BaseDao<Student> {
        public BaseDaoImpl(JDBCConfig jdbcConfig) {
            super(jdbcConfig);
        }
    }
}
