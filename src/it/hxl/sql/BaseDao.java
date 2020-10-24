package it.hxl.sql;

import it.hxl.annotation.*;
import it.hxl.util.MyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Setter
public class BaseDao<T> {
    private final static List<Connection> cons = new ArrayList<>();
    @NonNull
    private String driverClass;
    @NonNull
    private String url;
    @NonNull
    private String username;
    @NonNull
    private String password;

    public BaseDao(JDBCConfig jdbcConfig) {
        this.driverClass = jdbcConfig.getDriverClass();
        this.url = jdbcConfig.getUrl();
        this.username = jdbcConfig.getUsername();
        this.password = jdbcConfig.getPassword();
    }
    private BaseDao(){}

    @SneakyThrows
    protected synchronized Connection getConnection() {
        if (cons.size() == 0) {
            synchronized (BaseDao.class) {
                if (cons.size() == 0) {
                    Class.forName(driverClass);
                    for (int i = 0; i < 10; i ++) {
                        Connection connection = DriverManager.getConnection(url, username, password);
                        cons.add(connection);
                    }
                }
            }
        }
        log.info("cons的个数 ---> " + cons.size());
        // 关闭多余的连接
        while (cons.size() > 10) {
            Connection connection = cons.remove(0);
            connection.close();
        }
        return cons.remove(0);
    }

    /**
     * 回收连接
     * @param con 不使用的连接
     */
    @SneakyThrows
    protected synchronized void close(Connection con) {
        if (cons.size() >= 10) {
            // 如果连接大于十个则直接关闭
            con.close();
        } else {
            // 否则回收
            cons.add(con);
        }
    }

    @SneakyThrows
    protected void close(Connection con, ResultSet resultSet, PreparedStatement preparedStatement) {
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        close(con);
    }

    /**
     * 根据主键查询数据
     * @param pk 主键值
     * @param aClass 返回封装类
     */
    @SneakyThrows
    @SuppressWarnings("all")
    public <A> A findByPk(Object pk, Class<A> aClass) {
        // 获取表名
        // 1. 查看是否有Table注解
        // 2. 如果有则tableName为Table注解的value属性 否则为类名驼峰转换下划线连接
        String tableName = null;
        Table annotationTable = (Table) aClass.getAnnotation(Table.class);
        if (annotationTable != null) {
            tableName = annotationTable.value();
        } else {
            tableName = MyUtils.formatClassNameOrFieldName(aClass.getSimpleName());
        }
        /*
            获取主键名字：
            遍历属性，如果属性有@PrimaryKey注解即为主键
            否则主键为默认id
         */
        String primaryKey = null;
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field: declaredFields) {
            PrimaryKey annotation = field.getAnnotation(PrimaryKey.class);
            if (annotation != null) {
                Column annotationColumn = field.getAnnotation(Column.class);
                // 如果有Column注解则为其column属性值 否则为属性名格式化
                if (annotationColumn != null) {
                    primaryKey = annotationColumn.column();
                } else {
                    primaryKey = MyUtils.formatClassNameOrFieldName(field.getName());
                }
                break;
            }
        }
        if (primaryKey == null) {
            primaryKey = "id";
        }
        // 组查询sql
        String sql = "select * from " + tableName + " where " + primaryKey + "=?";
        log.info(sql);
        // 开始查询
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, pk);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            A a = aClass.getConstructor().newInstance();
            for (Field field: declaredFields) {
                field.setAccessible(true);
                // 处理结果集
                // 判断是否应该忽略
                IgnoreField annotationIgnoreField = field.getAnnotation(IgnoreField.class);
                if (annotationIgnoreField != null) {
                    // 如果该属性有被忽略的注解则不做处理
                    continue;
                }
                String colName = null; // 列名
                Column annotationColumn = field.getAnnotation(Column.class);
                if (annotationColumn != null) {
                    colName = annotationColumn.column();
                } else {
                    colName = MyUtils.formatClassNameOrFieldName(field.getName());
                }
                // 判断是否为外键
                ForeignKey annotationForeignKey = field.getAnnotation(ForeignKey.class);
                if (annotationForeignKey != null) {
                    field.set(a, this.findByPk(resultSet.getObject(colName), field.getType()));
                } else {
                    field.set(a, resultSet.getObject(colName));
                }

            }
            close(connection, resultSet, preparedStatement);
            return a;
        }
        close(connection, resultSet, preparedStatement);
        return null;
    }

    /**
     * 查询全部数据返回
     * @return
     */
    @SneakyThrows
    public List<T> findAll() {
        List<T> objs = new ArrayList<>();
        String sql = "select * from " + getTableName();
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        Class<?> tClass = getTClass();
        Field[] declaredFields = tClass.getDeclaredFields();
        while (resultSet.next()) {
            Constructor<?> constructor = tClass.getConstructor();
            Object o = constructor.newInstance();
            for (Field field: declaredFields) {
                field.setAccessible(true);
                // 如果此属性为忽略属性
                if (MyUtils.hasAnnotation(field, IgnoreField.class)) {
                    continue;
                }
                String columnName = MyUtils.getColumnName(field);
                if (MyUtils.hasAnnotation(field, ForeignKey.class)) {
                    field.set(o, this.findByPk(resultSet.getObject(columnName), field.getType()));
                } else {
                    field.set(o, resultSet.getObject(columnName));
                }
            }
            objs.add((T)o);
        }
        return objs;
    }

    @SneakyThrows
    public int deleteByPk(Object pk) {
        // 获取主键名字
        // 1.获取泛型类
        Class<?> tClass = getTClass();
        Field[] declaredFields = tClass.getDeclaredFields();
        Field field1 = Stream.of(declaredFields).filter(field -> field.getAnnotation(PrimaryKey.class) != null).findFirst().orElse(null);
        String pkName = null;
        if (field1 == null) {
            pkName = "id";
        } else {
            Column annotation = field1.getAnnotation(Column.class);
            if (annotation == null) {
                pkName = MyUtils.formatClassNameOrFieldName(field1.getName());
            } else {
                pkName = annotation.column();
            }
        }
        String sql = "delete from " + this.getTableName() + " where " + pkName + "=?";
        log.info(sql);
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, pk);
        int i = 0;
        synchronized (BaseDao.class) {
            i = preparedStatement.executeUpdate();
        }
        close(connection, null, preparedStatement);
        return i;
    }

    /**
     * 新增
     * @param t
     */
    public int insert(T t) {
        SimpleQuery simpleQuery = new SimpleQuery(t.getClass());
        String preparedInsertSql = simpleQuery.getPreparedInsertSql(t);
        log.info(preparedInsertSql);
        return executeUpdateSql(preparedInsertSql, simpleQuery);
    }

    /**
     * 根据主键修改
     * @param t
     * @return
     */
    public int update(T t) {
        SimpleQuery simpleQuery = new SimpleQuery(t.getClass());
        String preparedUpdateSql = simpleQuery.getPreparedUpdateSql(t);
        log.info(preparedUpdateSql);
        return executeUpdateSql(preparedUpdateSql, simpleQuery);
    }

    /**
     * 执行更新操作的sql，新增和删除
     * @param sql
     * @param simpleQuery
     * @return
     */
    @SneakyThrows
    private int executeUpdateSql(String sql, SimpleQuery simpleQuery) {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        simpleQuery.setPreparedStatement(preparedStatement);
        int i = 0;
        synchronized (BaseDao.class) {
            i = preparedStatement.executeUpdate();
        }
        close(connection, null, preparedStatement);
        return i;
    }

    /**
     * 获取泛型对应的类
     * @return
     */
    private Class<?> getTClass() {
        Type actualTypeArgument = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return (Class<?>) actualTypeArgument;
    }

    /**
     * 获取本类对应的表名
     * @return
     */
    private String getTableName() {
        Class<?> tClass = getTClass();
        return getTableName(tClass);
    }

    /**
     * 根据class获取表名
     * @param aClass
     * @return
     */
    private String getTableName(Class aClass) {
        String tableName = null;
        Annotation annotation = aClass.getAnnotation(Table.class);
        if (annotation != null) {
            tableName = ((Table) annotation).value();
        } else {
            tableName = MyUtils.formatClassNameOrFieldName(aClass.getSimpleName());
        }
        return tableName;
    }
}
