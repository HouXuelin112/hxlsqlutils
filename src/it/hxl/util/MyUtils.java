package it.hxl.util;

import it.hxl.annotation.Column;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class MyUtils {

    /**
     * ��ʽ��������������
     * �����ݿ�������ֶ�����Ӧ
     * @param name ������������
     * @return �������ֶ���
     */
    public static String formatClassNameOrFieldName(String name) {
        String s = name.replaceAll("[A-Z]", "_$0").toLowerCase();
        if (s.indexOf("_") == 0) {
            s = s.substring(1);
        }
        return s;
    }

    /**
     * �ж��Ƿ���ע��
     * @param field
     * @param aClass
     * @return
     */
    public static Boolean hasAnnotation(Field field, Class<? extends Annotation> aClass) {
        Annotation annotation = field.getAnnotation(aClass);
        return annotation != null;
    }

    /**
     * ��ȡfield��Ӧ������
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
