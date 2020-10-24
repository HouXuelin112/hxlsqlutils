package it.hxl.sql;

import it.hxl.annotation.ForeignKey;
import it.hxl.annotation.IgnoreField;
import it.hxl.annotation.PrimaryKey;
import it.hxl.annotation.Table;
import it.hxl.util.MyUtils;
import lombok.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class SimpleQuery {
    private Class<?> aClass;
    private List<BeanInfo> psBeanInfos;

    public SimpleQuery(Class<?> aClass) {
        this.aClass = aClass;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    final static class BeanInfo {
        private String column;
        private Object value;
        private boolean isPk;
    }

    @SneakyThrows
    public void setPreparedStatement(PreparedStatement ps) {
        assert psBeanInfos != null;
        int i = 1;
        for (BeanInfo beanInfo: psBeanInfos) {
            ps.setObject(i++, beanInfo.getValue());
        }
    }

    /**
     * 获取更新sql
     * @param e
     * @param <E>
     * @return
     */
    public <E> String getPreparedUpdateSql(E e) {
        List<BeanInfo> beanInfos = getNoNullBeanInfos(e);
        BeanInfo beanInfo = beanInfos.stream().filter(info -> info.isPk()).findFirst().orElse(null);
        assert beanInfo != null; // 主键不为空
        psBeanInfos = new ArrayList<>();
        List<String> updateStrings = new ArrayList<>();
        String sql = "update " + this.getTableName() + " set %s " + " where " + beanInfo.getColumn() + "=?";
        for (BeanInfo info: beanInfos) {
            if (info.isPk()) {
                continue;
            }
            updateStrings.add(info.getColumn() + "=?");
            psBeanInfos.add(info);
        }
        psBeanInfos.add(beanInfo);
        return String.format(sql, String.join(",", updateStrings));
    }

    /**
     * 获取preparedSql
     * @param e
     * @param <E>
     * @return
     */
    public <E> String getPreparedInsertSql(E e) {
        List<BeanInfo> beanInfos = getNoNullBeanInfos(e);
        psBeanInfos = new ArrayList<>();
        String sql = "insert into " + this.getTableName() + "(%s) values(%s)";
        List<String> keys = new ArrayList<>();
        List<String> strings = new ArrayList<>();
        for (BeanInfo beanInfo: beanInfos) {
            if (beanInfo.isPk()) {
                continue;
            }
            keys.add(beanInfo.getColumn());
            strings.add("?");
            psBeanInfos.add(beanInfo);
        }
        return String.format(sql, String.join(",", keys), String.join(",", strings));
    }

    /**
     * 获取实例的非空字段
     * @param e
     * @param <E>
     * @return
     */
    @SneakyThrows
    private   <E> List<BeanInfo> getNoNullBeanInfos(E e) {
        List<BeanInfo> noNullBeanInfos = new ArrayList<>();
        Class<?> aClass = e.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        // 判断为空的field
        boolean b = false;
        for (Field field: declaredFields) {
            field.setAccessible(true);
            if (MyUtils.hasAnnotation(field, IgnoreField.class) || isNullField(field, e)) {
                continue;
            }
            Object o = field.get(e);
            BeanInfo beanInfo = new BeanInfo();
            if (MyUtils.hasAnnotation(field, PrimaryKey.class)) {
                beanInfo.setPk(true);
                b = true;
            }
            beanInfo.setColumn(MyUtils.getColumnName(field));
            if (MyUtils.hasAnnotation(field, ForeignKey.class)) {
                beanInfo.setValue(getPkValue(field, e));
            } else {
                beanInfo.setValue(field.get(e));
            }
            noNullBeanInfos.add(beanInfo);
        }
        if (!b) {
            noNullBeanInfos.forEach(info -> {
                if (info.getColumn().equals("id")) {
                    info.setPk(true);
                }
            });
        }
        return noNullBeanInfos;
    }

    @SneakyThrows
    private Object getPkValue(Field field, Object e) {
        Class<?> type = field.getType();
        Field[] declaredFields = type.getDeclaredFields();
        Object pkValue = null;
        for (Field field1: declaredFields) {
            field.setAccessible(true);
            if (MyUtils.hasAnnotation(field1, PrimaryKey.class)) {
                pkValue = field1.get(field.get(e));
            }
        }
        if (pkValue == null) {
            pkValue = type.getField("id").get(field.get(e));
        }
        return pkValue;
    }

    @SneakyThrows
    private Boolean isNullField(Field field, Object o) {
        return field.get(o) == null;
    }

    /**
     * 根据class获取表名
     * @param aClass
     * @return
     */
    private String getTableName() {
        String tableName = null;
        Annotation annotation = this.aClass.getAnnotation(Table.class);
        if (annotation != null) {
            tableName = ((Table) annotation).value();
        } else {
            tableName = MyUtils.formatClassNameOrFieldName(this.aClass.getSimpleName());
        }
        return tableName;
    }
}
