/**
 * 初始化数据库
 */
var _table_name = "t8_doc_manage",
	rows = [{'id':1,'folder_name':'根目录','description':'根目录','port_level':'0','parent_id':'0','is_directory':'1','upd_username':'','upd_date':'','upd_time':'','crt_username':'','crt_date':'','crt_time':''},
	        {'id':2,'folder_name':'123','description':'','port_level':'1','parent_id':'1','is_directory':'1','upd_username':'semitree','upd_date':'20171228','upd_time':'014011','crt_username':'semitree','crt_date':'20171227','crt_time':'020223'},
	        {'id':4,'folder_name':'系统修改意见.xlsx','description':'','port_level':'1','parent_id':'2','is_directory':'0','upd_username':'semitree','upd_date':'20171227','upd_time':'020305','crt_username':'semitree','crt_date':'20171227','crt_time':'020242'},
	        {'id':7,'folder_name':'新文件夹','description':'','port_level':'1','parent_id':'2','is_directory':'1','upd_username':'','upd_date':'','upd_time':'','crt_username':'semitree','crt_date':'20171227','crt_time':'020541'},
	        {'id':8,'folder_name':'科目.docx','description':'','port_level':'1','parent_id':'7','is_directory':'0','upd_username':'','upd_date':'','upd_time':'','crt_username':'semitree','crt_date':'20171227','crt_time':'020550'},
	        {'id':10,'folder_name':'新文件夹[2]','description':'','port_level':'1','parent_id':'1','is_directory':'1','upd_username':'','upd_date':'','upd_time':'','crt_username':'semitree','crt_date':'20171228','crt_time':'013522'},
	        {'id':11,'folder_name':'新文件夹[3]','description':'','port_level':'1','parent_id':'1','is_directory':'1','upd_username':'','upd_date':'','upd_time':'','crt_username':'semitree','crt_date':'20171228','crt_time':'013524'},
	        {'id':12,'folder_name':'案例.xlsx','description':'','port_level':'1','parent_id':'1','is_directory':'0','upd_username':'semitree','upd_date':'20171228','upd_time':'014242','crt_username':'semitree','crt_date':'20171228','crt_time':'014229'},
	        {'id':13,'folder_name':'新文件夹','description':'','port_level':'1','parent_id':'1','is_directory':'1','upd_username':'','upd_date':'','upd_time':'','crt_username':'semitree','crt_date':'20180101','crt_time':'215822'},
	        {'id':14,'folder_name':'新文件夹[1]','description':'','port_level':'1','parent_id':'1','is_directory':'1','upd_username':'','upd_date':'','upd_time':'','crt_username':'semitree','crt_date':'20180101','crt_time':'215824'},
	        {'id':15,'folder_name':'功能.txt','description':'','port_level':'1','parent_id':'1','is_directory':'0','upd_username':'','upd_date':'','upd_time':'','crt_username':'semitree','crt_date':'20180101','crt_time':'215935'},
	        {'id':16,'folder_name':'新文件夹','description':'','port_level':'1','parent_id':'13','is_directory':'1','upd_username':'','upd_date':'','upd_time':'','crt_username':'semitree','crt_date':'20180101','crt_time':'220039'}],
	column_names = ['id','folder_name','description','port_level','parent_id','is_directory','upd_username','upd_date','upd_time','crt_username','crt_date','crt_time'];

/**
 * sql模板配置
 * M8610EQ005:查询文档在服务器上存储的目录
 */
var sql_execute = {"M8610EQ005":"M8610Q005",
				   "M8610EQ006":"M8610Q006",
				   "M8610EU001":"M8610U001",
				   "M8610EQ004":"M8610Q004",
				   "M8610ED001":"M8610D001",
				   "M8610ES001":"M8610S001",
				   "M8610EQ008":"M8610Q008"},
	sql_info = {"M8610Q005":"select folder_name,parent_id,is_directory from t8_doc_manage where id = ${id}",
				"M8610Q006":"select id,folder_name,is_directory from t8_doc_manage where parent_id = ${id} order by is_directory desc,folder_name",
				"M8610U001":"update t8_doc_manage set description = ${description},folder_name = ${folder_name},upd_username = ${username},upd_date = ${date},upd_time = ${time} where id = ${id}",
				"M8610Q004":"select id from t8_doc_manage where parent_id = ${id}",
				"M8610D001":"delete from t8_doc_manage where id = ${id}",
				"M8610S001":"insert into t8_doc_manage(id, folder_name, description, port_level, parent_id, is_directory,crt_username, crt_date, crt_time, upd_username, upd_date, upd_time) values($AUTOID,${folder_name},${description},${port_level},${parent_id},${is_directory},${username},${date},${time},,,)",
				"M8610Q008":"SELECT folder_name,is_directory,crt_username,upd_username,crt_date,crt_time,upd_date,upd_time FROM t8_doc_manage WHERE id = ${id}"};

/**
 * 自定义trim函数
 */
function trim(str){
	return str.replace(/(^\s*)|(\s*$)/g, "");
}

/**
 * @author semitree
 * @param sqlExecute
 * @param params
 * 模拟insert语句
 */
function insert(sqlExecute,params){
	var ori_sql = sql_info[sql_execute[sqlExecute]],
		sql = generateSQL(ori_sql,params).toLowerCase(),
		reg_tablename = new RegExp("^insert\\s+into\\s+\\w+\\(","ig"),
		result_tablename = sql.match(reg_tablename),
		table_name = trim(result_tablename.toString().replace("insert","").replace("into","").replace("(","")),
		reg_columnname = new RegExp("\\(.+\\)\\s+values","ig"),
		result_columnname = sql.match(reg_columnname),
		column_name = trim(result_columnname.toString().replace("(","").replace(")","").replace("values","")),
		columnname_array = column_name.split(","),
		reg_columnvalue = new RegExp("values\\s*\\(\\S+\\)$","ig"),
		result_columnvalue = sql.match(reg_columnvalue),
		column_value = trim(result_columnvalue.toString().replace("values","").replace("(","").replace(")","")),
		columnvalue_array = column_value.split(","),
		flag = true,
		row = {};
	if(trim(table_name) == _table_name){
		$.each(columnname_array,function(index,value){
			var columnname = trim(value),
				columnvalue = trim(columnvalue_array[index]);
			if($.inArray(columnname,column_names) != -1){
				if(columnvalue.toUpperCase() == "$AUTOID"){
					var maxid = 0;
					$.each(rows,function(index,value){
						var _row = value,
							rowid = parseInt(_row.id);
						maxid = rowid > maxid ? rowid : maxid;
					})
					row[columnname] = maxid + 1;
				}else{
					row[columnname] = columnvalue;
				}				
			}else{
				flag = false;
			}
		});
		if(flag){
			rows.push(row);
		}
	}
	console.log(rows);
}

/**
 * @author semitree
 * @param sqlExecute
 * @param params
 * 模拟update语句
 */
function update(sqlExecute,params){
	var ori_sql = sql_info[sql_execute[sqlExecute]],
		sql = generateSQL(ori_sql,params).toLowerCase(),
		reg_tablename = new RegExp("^update\\s+\\w+\\s+set","ig"),
		result_tablename = sql.match(reg_tablename),
		table_name = trim(result_tablename.toString().replace("update","").replace("set","")),
		reg_setvalues = new RegExp("set\\s+.+where","ig"),
		result_setvalues = sql.match(reg_setvalues),
		set_values = trim(result_setvalues.toString().replace("set","").replace("where","")),
		setvalue_array = set_values.split(","),
		reg_conditions = new RegExp("where\\s+.+$","ig"),
		result_conditions = sql.match(reg_conditions),
		conditions = trim(result_conditions.toString().replace("where","")),
		conditions_array = conditions.toLowerCase().split("and");
	if(trim(table_name) == _table_name){
		var condition_json = {},
			setvalue_json = {},
			flag = true;
		$.each(conditions_array,function(index,value){
			var condition = value,
				column_name = trim(condition.substring(0,condition.indexOf("="))),
				column_value = trim(condition.substring(condition.indexOf("=")+1));
			if($.inArray(column_name,column_names) != -1){
				condition_json[column_name] = column_value;
			}else{
				flag = false;
			}			
		});
		$.each(setvalue_array,function(index,value){
			var setvalue = value,
				setvalue_name = trim(setvalue.substring(0,setvalue.indexOf("="))),
				setvalue_value = trim(setvalue.substring(setvalue.indexOf("=")+1));
			if($.inArray(setvalue_name,column_names) != -1){
				setvalue_json[setvalue_name] = setvalue_value;
			}else{
				flag = false;
			}			
		});
		if(flag){
			$.each(rows,function(index,value){
				var row = value,
					flag2 = true;
				for(var condition_name in condition_json){
					if(row[condition_name] != condition_json[condition_name]){
						flag2 = false;
						break;
					}
				}
				if(flag2){
					for(var setvalue_name in setvalue_json){
						row[setvalue_name] = setvalue_json[setvalue_name];
					}
				}
			});
		}
	}
	console.log(rows);
}

/**
 * @author semitree
 * @param sqlExecute
 * @param params
 * 模拟delete语句
 */
function del(sqlExecute,params){
	var ori_sql = sql_info[sql_execute[sqlExecute]],
		sql = generateSQL(ori_sql,params).toLowerCase(),
		reg_tablename = new RegExp("^delete\\s+from\\s+\\w+\\s+where","ig"),
		result_tablename = sql.match(reg_tablename),
		table_name = trim(result_tablename.toString().replace("delete","").replace("from","").replace("where","")),
		reg_conditions = new RegExp("where\\s+.+$","ig"),
		result_conditions = sql.match(reg_conditions),
		conditions = trim(result_conditions.toString().replace("where","")),
		conditions_array = conditions.split("and"),
		rows_copy = deepClone(rows);
	if(trim(table_name) == _table_name){
		var condition_json = {},
			flag = true;
		$.each(conditions_array,function(index,value){
			var condition = value,
				column_name = trim(condition.substring(0,condition.indexOf("="))),
				column_value = trim(condition.substring(condition.indexOf("=")+1));
			if($.inArray(column_name,column_names) != -1){
				condition_json[column_name] = column_value;
			}else{
				flag = false;
			}			
		});
		if(flag){
			var num = -1;
			$.each(rows_copy,function(index,value){
				var row = value,
					flag2 = true;
				for(var condition_name in condition_json){
					if(row[condition_name] != condition_json[condition_name]){
						flag2 = false;
						break;
					}
				}
				if(flag2){
					rows.splice(index-++num,1);
				}
			});
		}
	}
	console.log(rows);
}

/**
 * @author semitree
 * @param sqlExecute
 * @param params
 * @returns
 * 模拟select语句
 */
function select(sqlExecute,params){
	var ori_sql = sql_info[sql_execute[sqlExecute]],
		sql = generateSQL(ori_sql,params).toLowerCase(),
		reg_tablename = new RegExp("from\\s+\\w+\\s+where","ig"),
		result_tablename = sql.match(reg_tablename),
		table_name = trim(result_tablename.toString().replace("from","").replace("where","")),
		reg_selectcolumn = new RegExp("^select\\s+.+\\s+from","ig"),
		result_selectcolumn = sql.match(reg_selectcolumn),
		select_column = trim(result_selectcolumn.toString().replace("select","").replace("from","")),
		selectcolumn_array = select_column.split(","),
		reg_conditions = new RegExp("where\\s+.+$","ig"),
		result_conditions_with_order = sql.match(reg_conditions).toString(),
		index_order = result_conditions_with_order.indexOf("order"),
		result_conditions = index_order != -1 ? result_conditions_with_order.substring(0,index_order) : result_conditions_with_order,
		conditions = trim(result_conditions.toString().replace("where","")),
		conditions_array = conditions.split("and"),
		reg_orderby = new RegExp("order\\s+by\\s+.+$","ig"),
		result_orderby = sql.match(reg_orderby) == null ? "" : sql.match(reg_orderby),
		orderbys = trim(result_orderby.toString().replace("order","").replace("by","")),
		orderby_array = orderbys.split(","),
		select_rows = [];	
	if(trim(table_name) == _table_name){
		var condition_json = {},			
			flag = true;
		$.each(conditions_array,function(index,value){
			var condition = value,
				column_name = trim(condition.substring(0,condition.indexOf("="))),
				column_value = trim(condition.substring(condition.indexOf("=")+1));
			if($.inArray(column_name,column_names) != -1){
				condition_json[column_name] = column_value;
			}else{
				flag = false;
			}			
		});
		if(flag){
			$.each(rows,function(index,value){
				var row = value,
					flag2 = true;
				for(var condition_name in condition_json){
					if(row[condition_name] != condition_json[condition_name]){
						flag2 = false;
						break;
					}
				}
				if(flag2){
					var select_row = {};
					$.each(selectcolumn_array,function(index,value){
						var column_name = trim(value);
						select_row[column_name] = getType(row[column_name]) == "undefined" ? "" : row[column_name];
					});
					select_rows.push(select_row);
				}
			});
			var sequence = function(row1,row2){
				var row1_copy = deepClone(row1),
					row2_copy = deepClone(row2),
					orderby_index = getType(row1_copy.orderby_index) != "undefined" ? row1_copy.orderby_index : 0,
					orderby = trim(orderby_array[orderby_index].toString());
				if(orderby.length != 0){
					orderby = orderby.indexOf(" ") != -1 ? orderby : orderby+" asc";
					var orderby_column = orderby.substring(0,orderby.indexOf(" ")),
						orderby_direction = orderby.substring(orderby.lastIndexOf(" ")+1);
					if(row1_copy[orderby_column].toString() > row2_copy[orderby_column].toString()){
						return orderby_direction == "desc" ? -1 : 1;
					}else if(row1_copy[orderby_column].toString() < row2_copy[orderby_column].toString()){
						return orderby_direction == "desc" ? 1 : -1;
					}else{
						if(orderby_array.length > orderby_index+1){
							row1_copy.orderby_index = orderby_index+1;
							return sequence(row1_copy,row2_copy);
						}else{
							return 0;
						}							
					}
				}else{
					return 0;
				}
			};
			select_rows.sort(sequence);
		}
	}
	return select_rows;
}

/**
 * @author semitree
 * @param ori_sql
 * @param params
 * @returns
 * 根据SQL模板和提交的参数生成对应可执行的SQL
 */
function generateSQL(ori_sql,params){
	for(var param in params){
		ori_sql = ori_sql.replace("${"+param+"}",params[param]);
	}
	return ori_sql;
}

/**
 * @author semitree
 * @param obj
 * @returns
 * 获取变量类型
 */
function getType(obj){
    //tostring会返回对应不同的标签的构造函数
    var toString = Object.prototype.toString;
    var map = {
       '[object Boolean]'  : 'boolean', 
       '[object Number]'   : 'number', 
       '[object String]'   : 'string', 
       '[object Function]' : 'function', 
       '[object Array]'    : 'array', 
       '[object Date]'     : 'date', 
       '[object RegExp]'   : 'regExp', 
       '[object Undefined]': 'undefined',
       '[object Null]'     : 'null', 
       '[object Object]'   : 'object'
   };
   if(obj instanceof Element) {
        return 'element';
   }
   return map[toString.call(obj)];
}

/**
 * @author semitree
 * @param data
 * @returns
 * 利用递归实现深拷贝
 */
function deepClone(data){
    var type = getType(data);
    var obj;
    if(type === 'array'){
        obj = [];
    } else if(type === 'object'){
        obj = {};
    } else {
        //不再具有下一层次
        return data;
    }
    if(type === 'array'){
        for(var i = 0, len = data.length; i < len; i++){
            obj.push(deepClone(data[i]));
        }
    } else if(type === 'object'){
        for(var key in data){
            obj[key] = deepClone(data[key]);
        }
    }
    return obj;
}

/**
 * 日期格式化函数
 */
Date.prototype.Format = function(formatStr){   
    var str = formatStr;   
    var Week = ['日','一','二','三','四','五','六'];  
  
    str=str.replace(/yyyy|YYYY/,this.getFullYear());   
    str=str.replace(/yy|YY/,(this.getYear() % 100)>9?(this.getYear() % 100).toString():'0' + (this.getYear() % 100));   
  
    str=str.replace(/MM/,this.getMonth()+1>9?(this.getMonth()+1).toString():'0' + (this.getMonth()+1));   
    str=str.replace(/M/g,this.getMonth());   
  
    str=str.replace(/w|W/g,Week[this.getDay()]);   
  
    str=str.replace(/dd|DD/,this.getDate()>9?this.getDate().toString():'0' + this.getDate());   
    str=str.replace(/d|D/g,this.getDate());   
  
    str=str.replace(/hh|HH/,this.getHours()>9?this.getHours().toString():'0' + this.getHours());   
    str=str.replace(/h|H/g,this.getHours());   
    str=str.replace(/mm/,this.getMinutes()>9?this.getMinutes().toString():'0' + this.getMinutes());   
    str=str.replace(/m/g,this.getMinutes());   
  
    str=str.replace(/ss|SS/,this.getSeconds()>9?this.getSeconds().toString():'0' + this.getSeconds());   
    str=str.replace(/s|S/g,this.getSeconds());   
  
    return str;   
}   









