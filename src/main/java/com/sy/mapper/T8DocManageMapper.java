package com.sy.mapper;

import com.sy.model.T8DocManage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface T8DocManageMapper {


    int insert(T8DocManage record);


    T8DocManage selectByPrimaryKey(Integer id);

    List<T8DocManage> selectByparentId(Integer id);

    int updateByPrimaryKeySelective(T8DocManage record);

    List<Integer> selectidByparentId(Integer id);

    int deleteByPrimaryKey(Integer id);

    int insertSelective(T8DocManage record);

    List<T8DocManage> M8610EQ006(T8DocManage record);
    List<T8DocManage> M8610EQ007(T8DocManage record);
    List<T8DocManage> M8610EQ005(T8DocManage record);
    List<T8DocManage> M8610EQ009();
    Integer M8610EU001(T8DocManage record);
    Integer M8610EQ004(T8DocManage record);
    Integer M8610ED001(T8DocManage record);
    Integer M8610ES001(T8DocManage record);
    Integer updatereadcount(T8DocManage record);
    T8DocManage M8610EQ008(T8DocManage record);


    int updateByPrimaryKey(T8DocManage record);
}
