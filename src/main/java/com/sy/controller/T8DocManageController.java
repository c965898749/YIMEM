package com.sy.controller;

import com.sy.model.T8DocManage;
import com.sy.model.resp.BaseResp;
import com.sy.service.T8DocManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author CZX
 * @version 1.0
 * @date 2021/2/22 0022 23:48
 */
@RestController
public class T8DocManageController {
    @Autowired
    private T8DocManageService t8DocManageService;
//    T8DocManage selectByPrimaryKey(Integer id);
//
//    List<T8DocManage> selectByparentId(Integer id);
//
//    int updateByPrimaryKeySelective(T8DocManage record);
//
//    List<Integer> selectidByparentId(Integer id);
//
//    int deleteByPrimaryKey(Integer id);
//
//    int insertSelective(T8DocManage record);
    @RequestMapping(value = "selectT8DocManageByPrimaryKey")
    public T8DocManage selectT8DocManageByPrimaryKey(Integer id) {
        return t8DocManageService.selectByPrimaryKey(id);
    }

    @RequestMapping(value = "selectT8DocManageByparentId")
    public  List<T8DocManage>  selectT8DocManageByparentId(Integer id) {
        return t8DocManageService.selectByparentId(id);
    }

    @RequestMapping(value = "updateT8DocManageByPrimaryKeySelective")
    public  int  updateT8DocManageByPrimaryKeySelective(T8DocManage record) {
        return t8DocManageService.updateByPrimaryKeySelective(record);
    }


    @RequestMapping(value = "selectT8DocManageidByparentId")
    public  List<Integer>  selectT8DocManageidByparentId(Integer id) {
        return t8DocManageService.selectidByparentId(id);
    }

    @RequestMapping(value = "deleteT8DocManageByPrimaryKey")
    public  Integer  deleteT8DocManageByPrimaryKey(Integer id) {
        return t8DocManageService.deleteByPrimaryKey(id);
    }

    @RequestMapping(value = "insertSelective")
    public  Integer  insertSelective(T8DocManage record) {
        return t8DocManageService.insertSelective(record);
    }
}
