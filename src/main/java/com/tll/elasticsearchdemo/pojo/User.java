package com.tll.elasticsearchdemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author tll
 * @create 2020/6/21 13:47
 * 创建实体类
 * @Component 实例化到spring容器中
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class User {
    private String name;
    private int age;
}
