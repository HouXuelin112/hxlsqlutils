package it.hxl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �������������ݿ�������ͬ����ϴ�ע��
 * ע����Ӧ������
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * ��Ӧ������
     * @return
     */
    String column();
}
