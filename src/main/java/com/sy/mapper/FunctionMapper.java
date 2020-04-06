package com.sy.mapper;

import com.sy.model.Authority;
import com.sy.model.Function;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * FunctionMapper
 *
 */
public interface FunctionMapper {


	/**
	 * 根据RoldId查询子菜单
	 * @param function
	 * @return
	 * @throws Exception
	 */
	public List<Function> getSubFunctionList(Function function) throws Exception;

	/**
	 * 简单查询子菜单
	 * @param function
	 * @return
	 * @throws Exception
	 */
	public List<Function> getSubFuncList(Function function) throws Exception;
	/**
	 *	根据RoleId查询主菜单
	 * @return
	 * @throws Exception
	 */
	public List<Function> getMainFunctionList(Authority authority) throws Exception;

	/**
	 * 根据Id查询
	 * @return
	 * @throws Exception
	 */
	public Function getFunctionById(Function function) throws Exception;
	/**
	 *	查询操作
	 * @param sqlInString
	 * @return
	 * @throws Exception
	 */
	public List<Function> getFunctionListByIn(@Param(value = "sqlInString") String sqlInString) throws Exception;
	/**
	 * 根据RoleId查询除了主菜单之外所有操作
	 * @param
	 * @return
	 * @throws Exception
	 */
	public List<Function> getFunctionListByRoId(Authority authority) throws Exception;

}
