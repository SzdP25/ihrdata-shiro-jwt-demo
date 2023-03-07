package com.ihrdata.demo.mybatis;

import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 通用Mapper
 *
 * @author wangwz
 * @param <T>
 *            自定义类型
 * @date 2020/11/23
 */
public interface TkMapper<T> extends Mapper<T>, IdsMapper<T>, MySqlMapper<T> {
}
