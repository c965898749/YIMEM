package com.sy.mapper;


import com.sy.model.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface RoleMapper {


	/**
	 * 查询所有角色
	 * @return
	 * @throws Exception
	 */
	public List<Role> getRoleList() throws Exception;

	/**
	 * 根据Id查询单个角色
	 * @param role
	 * @return
	 */
	public Role getRole(Role role) throws Exception;
	/**
	 * 根据角色名查询单个角色
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public Role getRoleR(Role role) throws Exception;

	/**
	 * 新增一条角色
	 * @param role
	 * @return
	 */
	public int addRole(Role role) throws Exception;

	/**
	 * 修改一条角色
	 * @param role
	 * @return
	 */
	public int modifyRole(Role role) throws Exception;

	/**
	 * 删除一个角色
	 * @param role
	 * @return
	 */
	public int deleteRole(Role role) throws Exception;

	/**
	 * 查询已启用的角色
	 * @return
	 */
	public List<Role> getRoleIdAndNameList() throws Exception;
	/**
	 * 修改角色状态
	 * @param status
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Integer madifyRoleStatus(@Param("status") Integer status, @Param("id") Integer id) throws Exception;

}
