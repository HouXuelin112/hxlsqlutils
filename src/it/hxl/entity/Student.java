package it.hxl.entity;

import it.hxl.annotation.Column;
import it.hxl.annotation.IgnoreField;
import it.hxl.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Integer id;
    @Column(column = "stu_name")
    private String name;
    @Column(column = "stu_no")
    private String no;
    @Column(column = "stu_gender")
    private Integer gender;
    @Column(column = "stu_age")
    private Integer age;

    @IgnoreField
    private String other;
}
