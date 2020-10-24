# hxlsqlutils
 jdbc工具

### 1. 说明

> src下为源码，打包后的jar为hxlsqlutils.jar

### 2. 使用

> 导入jar包，继承BaseDao

```java
package com.hxl1.dao;

import it.hxl.entity.Student;
import it.hxl.sql.BaseDao;
import it.hxl.sql.JDBCConfig;

public class StudentDao extends BaseDao<Student> {
    public StudentDao(JDBCConfig jdbcConfig) {
        super(jdbcConfig);
    }
}

```

```java
// 测试
package com.hxl1.test;

import com.hxl1.dao.StudentDao;
import it.hxl.entity.Student;
import it.hxl.sql.JDBCConfig;
import org.junit.Test;

public class MyTest {

    @Test
    public void testJAr(){
        JDBCConfig build = JDBCConfig.builder().driverClass("com.mysql.cj.jdbc.Driver").url("jdbc:mysql://localhost:3306/order?serverTimezone=Asia/Shanghai&&autoReconnect=true").username("root").password("qwe123").build();
        StudentDao dao = new StudentDao(build);
        dao.insert(Student.builder().name("dd").age(23).no("123131").gender(1).build());
        System.out.println(dao.findAll());

    }

}
// 运行结果
/*
[INFO][main][2020-10-24 11:12:21][it.hxl.sql.BaseDao.insert(BaseDao.java:243)] - insert into students(stu_name,stu_no,stu_gender,stu_age) values(?,?,?,?)
[INFO][main][2020-10-24 11:12:22][it.hxl.sql.BaseDao.getConnection(BaseDao.java:56)] - cons的个数 ---> 10
[INFO][main][2020-10-24 11:12:22][it.hxl.sql.BaseDao.getConnection(BaseDao.java:56)] - cons的个数 ---> 10
[Student(id=2, name=hxl, no=201706051405, gender=21, age=2, other=null), Student(id=3, name=a, no=s, gender=21, age=1, other=null), Student(id=4, name=hxl, no=null, gender=12, age=1, other=null), Student(id=5, name=hxl, no=null, gender=12, age=1, other=null), Student(id=6, name=hxl, no=2221231, gender=12, age=1, other=null), Student(id=7, name=hxl112, no=2221231, gender=12, age=1, other=null), Student(id=8, name=hxl, no=123, gender=1, age=12, other=null), Student(id=9, name=hxl, no=123, gender=1, age=12, other=null), Student(id=10, name=hxl112, no=2221231, gender=12, age=1, other=null), Student(id=11, name=hxl112, no=2221231, gender=12, age=1, other=null), Student(id=12, name=hxl112, no=2221231, gender=12, age=1, other=null), Student(id=13, name=hxl, no=123, gender=1, age=12, other=null), Student(id=14, name=dd, no=123131, gender=1, age=23, other=null), Student(id=15, name=dd, no=123131, gender=1, age=23, other=null)]
*/
```

