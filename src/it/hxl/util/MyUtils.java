package it.hxl.util;

import it.hxl.annotation.Column;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class MyUtils {

    /**
     * 格式化类名或属性名
     * 与数据库表名和字段名对应
     * @param name 类名或属性明
     * @return 表名或字段名
     */
    public static String formatClassNameOrFieldName(String name) {
        String s = name.replaceAll("[A-Z]", "_$0").toLowerCase();
        if (s.indexOf("_") == 0) {
            s = s.substring(1);
        }
        return s;
    }

    /**
     * 判断是否有注解
     * @param field
     * @param aClass
     * @return
     */
    public static Boolean hasAnnotation(Field field, Class<? extends Annotation> aClass) {
        Annotation annotation = field.getAnnotation(aClass);
        return annotation != null;
    }

    /**
     * 获取field对应的列名
     * @param field
     * @return
     */
    public static String getColumnName(Field field) {
        Column annotation = field.getAnnotation(Column.class);
        String columnName = null;
        if (annotation != null) {
            columnName = annotation.column();
        } else {
            columnName = MyUtils.formatClassNameOrFieldName(field.getName());
        }
        return columnName;
    }
}
