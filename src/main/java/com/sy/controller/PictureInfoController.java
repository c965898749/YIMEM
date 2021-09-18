//package com.sy.controller;
//
//import com.sy.entity.PictureInfo;
//import com.sy.service.PictureInfoService;
//import com.sy.vo.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * (PictureInfo)表控制层
// *
// * @author
// * @since 2021-09-18 13:46:01
// */
///**
// * @Description: picture_info
// * @Author: jeecg-boot
// * @Date:   2021-06-21
// * @Version: V1.0
// */
//@RestController
//@RequestMapping("/pictureInfo")
//@Slf4j
//public class PictureInfoController  {
//    @Autowired
//    private PictureInfoService pictureInfoService;
//
//    /**
//     * 分页列表查询
//     *
//     * @return
//     */
//    @GetMapping(value = "/list")
//    public Result<?> queryPageList(@RequestParam String  fromTable, @RequestParam String tableId) {
//
////        List<PictureInfo> pageList = pictureInfoService.list(
////                new QueryWrapper<PictureInfo>().lambda()
////                        .eq(PictureInfo::getFromTable,fromTable)
////                        .eq(PictureInfo::getTableId,tableId));
////        return Result.ok(pageList);
//    }
//
//    @GetMapping(value = "/listByType")
//    public Result<?> queryListByType(@RequestParam String  fromTable, @RequestParam String tableId, @RequestParam String picType) {
//
////        List<PictureInfo> pageList = pictureInfoService.list(
////                new QueryWrapper<PictureInfo>().lambda()
////                        .eq(PictureInfo::getFromTable,fromTable)
////                        .eq(PictureInfo::getTableId,tableId)
////                        .eq(PictureInfo::getPicType,picType));
////        return Result.ok(pageList);
//    }
//
//    /**
//     *   添加
//     *
//     * @param pictureInfo
//     * @return
//     */
//
//    @PostMapping(value = "/add")
//    public Result<?> add(@RequestBody PictureInfo pictureInfo) {
////        pictureInfoService.save(pictureInfo);
////        return Result.ok("添加成功！");
//    }
//
//    /**
//     *  编辑
//     *
//     * @param pictureInfo
//     * @return
//     */
//
//    @PutMapping(value = "/edit")
//    public Result<?> edit(@RequestBody PictureInfo pictureInfo) {
////        pictureInfoService.updateById(pictureInfo);
////        return Result.ok("编辑成功!");
//    }
//
//    /**
//     *   通过id删除
//     *
//     * @param id
//     * @return
//     */
//
//    @DeleteMapping(value = "/delete")
//    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
////        pictureInfoService.removeById(id);
////        return Result.ok("删除成功!");
//}
//
//    /**
//     *  批量删除
//     *
//     * @param ids
//     * @return
//     */
//
//    @DeleteMapping(value = "/deleteBatch")
//    public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
////        this.pictureInfoService.removeByIds(Arrays.asList(ids.split(",")));
////        return Result.ok("批量删除成功!");
//    }
//
//    /**
//     * 通过id查询
//     *
//     * @param id
//     * @return
//     */
//
//    @GetMapping(value = "/queryById")
//    public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
////        PictureInfo pictureInfo = pictureInfoService.getById(id);
////        if(pictureInfo==null) {
////            return Result.error("未找到对应数据");
////        }
////        return Result.ok(pictureInfo);
//    }
//
//    /**
//     * 导出excel
//     *
//     * @param request
//     * @param pictureInfo
//     */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(HttpServletRequest request, PictureInfo pictureInfo) {
////        return super.exportXls(request, pictureInfo, PictureInfo.class, "picture_info");
//    }
//
//    /**
//     * 通过excel导入数据
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
//    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
////        return super.importExcel(request, response, PictureInfo.class);
//    }
//
//}
//
