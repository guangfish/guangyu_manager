package com.bt.om.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.common.SysConst;
import com.bt.om.common.web.PageConst;
import com.bt.om.entity.DrawCash;
import com.bt.om.entity.UserOrder;
import com.bt.om.entity.vo.JqueryDataTable;
import com.bt.om.enums.CsbtConstants;
import com.bt.om.enums.ResultCode;
import com.bt.om.service.IDrawCashService;
import com.bt.om.service.IUserOrderService;
import com.bt.om.vo.web.Result;
import com.bt.om.vo.web.ResultVo;
import com.bt.om.vo.web.SearchDataVo;
import com.bt.om.web.BasicController;
import com.bt.om.web.util.SearchUtil;

/**
 * 订单管理类
 * @author yuhao
 *
 */
@Controller
public class OrderManagerController extends BasicController{

	@Autowired
	private IUserOrderService userOrderService;
	
	@Autowired
	private IDrawCashService drawCacheServie;
	
	
	/**
	 * 跳转到订单查询
	 * @return
	 */
	@RequestMapping(value="/order/list", method=RequestMethod.GET)
	public String toOrderList() {
		
		return PageConst.ORDER_LIST;
	}
	
	/**
	 * 订单管理列表
	 * @return
	 */
	@RequestMapping(value="/load/order/list", method=RequestMethod.GET)
	@ResponseBody
	public JqueryDataTable getOrderList(
			@RequestParam(value="mobile", required=false) String mobile,
			@RequestParam(value="startDate", required=false) String startDate,
			@RequestParam(value="endDate", required=false) String endDate,
			@RequestParam(value="orderStatus", required=false) Integer orderStatus,
			@RequestParam(value="putForwardStatus", required=false) Integer putForwardStatus,
			@RequestParam(value="paymentStatus", required=false) Integer paymentStatus) {
		
		SearchDataVo vo = SearchUtil.getVoForJqueryTab();
		if(paymentStatus != null) {
			vo.putSearchParam("paymentStatus", paymentStatus+"", paymentStatus);
		}
		if(putForwardStatus != null) {
			vo.putSearchParam("putForwardStatus", putForwardStatus+"", putForwardStatus);
		}
		if(orderStatus != null) {
			vo.putSearchParam("orderStatus", orderStatus+"", orderStatus);
		}
		if(StringUtils.isNotBlank(startDate)) {
			vo.putSearchParam("startDate", startDate, startDate);
		}
		if(StringUtils.isNotBlank(endDate)) {
			vo.putSearchParam("endDate", endDate, endDate);
		}
		if(StringUtils.isNotBlank(mobile)) {
			vo.putSearchParam("mobile", mobile, mobile);
		}
        int listCount = userOrderService.getAllListCount(vo.getSearchMap());
		if (listCount > 0) {
            List<Map<String, Object>> userOrderList = userOrderService.getAllList(vo);
            vo.setList(userOrderList);
        } else {
            vo.setList(new ArrayList<Map<String, Object>>());
        }
		
		return new JqueryDataTable(vo.getList(), listCount, listCount);
	}

	@RequestMapping(value="/order/status/upt", method=RequestMethod.GET)
    @ResponseBody
	public Model updateOrderStatus ( Model model,
			@RequestParam(value="id", required=false) Integer id,
			@RequestParam(value="orderStatus", required=false) Integer orderStatus,
			@RequestParam(value="putForwardStatus", required=false) Integer putForwardStatus,
			@RequestParam(value="paymentStatus", required=false) Integer paymentStatus) {
		
		ResultVo<String> resultVo = new ResultVo<String>();
		int flag = userOrderService.updateOrderStatus(id, orderStatus, putForwardStatus, paymentStatus);
		if (flag == 0) {
			resultVo.setCode(ResultCode.RESULT_FAILURE.getCode());
			resultVo.setResultDes("修改失败");
			model.addAttribute(SysConst.RESULT_KEY, resultVo);
			return model;
		}
		
		resultVo.setCode(ResultCode.RESULT_SUCCESS.getCode());
		resultVo.setResultDes("成功");
		model.addAttribute(SysConst.RESULT_KEY, resultVo);
		return model;
	}

	@RequestMapping(value="/delete/order", method=RequestMethod.GET)
    @ResponseBody
	public Result deleteOrder (
			@RequestParam(value="id", required=false) Integer id) {
		Result result = new Result();
		userOrderService.deleteOrder(id);
		result.setCode(CsbtConstants.RESULT_CODE_SUCCESS);
		result.setMsg("成功");
		return result;
	}

	
	/**
	 * 跳转到返利查询
	 * @return
	 */
	@RequestMapping(value="/drawcache/list", method=RequestMethod.GET)
	public String toPayForwardList() {
		
		return PageConst.DRAW_CACHE_LIST;
	}
	
	/**
	 * 返利查询列表
	 * @return
	 */
	@RequestMapping(value="/load/drawcache/list", method=RequestMethod.GET)
	@ResponseBody
	public JqueryDataTable getDrawCachList(
			@RequestParam(value="mobile", required=false) String mobile,
			@RequestParam(value="applyTimeStart", required=false) String applyTimeStart,
			@RequestParam(value="applyTimeEnd", required=false) String applyTimeEnd,
			@RequestParam(value="payTimeStart", required=false) String payTimeStart,
			@RequestParam(value="payTimeEnd", required=false) String payTimeEnd,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="alipayAccount", required=false) String alipayAccount) {
		
		SearchDataVo vo = SearchUtil.getVoForJqueryTab();
		if(StringUtils.isNotBlank(applyTimeStart)) {
			vo.putSearchParam("applyTimeStart", applyTimeStart, applyTimeStart);
		}
		if(StringUtils.isNotBlank(applyTimeEnd)) {
			vo.putSearchParam("applyTimeEnd", applyTimeEnd, applyTimeEnd);
		}
		if(StringUtils.isNotBlank(payTimeStart)) {
			vo.putSearchParam("payTimeStart", payTimeStart, payTimeStart);
		}
		if(StringUtils.isNotBlank(payTimeEnd)) {
			vo.putSearchParam("payTimeEnd", payTimeEnd, payTimeEnd);
		}
		if(StringUtils.isNotBlank(mobile)) {
			vo.putSearchParam("mobile", mobile, mobile);
		}
		if(StringUtils.isNotBlank(status)) {
			vo.putSearchParam("status", status, status);
		}
		if(StringUtils.isNotBlank(alipayAccount)) {
			vo.putSearchParam("alipayAccount", alipayAccount, alipayAccount);
		}
        int listCount = drawCacheServie.getDrawListCount(vo.getSearchMap());
		if (listCount > 0) {
            List<DrawCash> drawCacheList = drawCacheServie.getDrawList(vo);
            vo.setList(drawCacheList);
        } else {
            vo.setList(new ArrayList<DrawCash>());
        }
		
		return new JqueryDataTable(vo.getList(), listCount, listCount);
	}
	
	/**
	 * 查看返利订单
	 * @return
	 */
	@RequestMapping(value="/load/draworder/list", method=RequestMethod.GET)
	@ResponseBody
	public JqueryDataTable getUserOrderByDrawId(@RequestParam(value="id", required=false) Integer id) {
		SearchDataVo vo = SearchUtil.getVoForJqueryTab();
		
        int listCount = drawCacheServie.getUserOrderCountByDrawId(id);
		if (listCount > 0) {
            List<Map<String, Object>> drawCacheList = drawCacheServie.getUserOrderByDrawId(vo, id);
            vo.setList(drawCacheList);
        } else {
            vo.setList(new ArrayList<Map<String, Object>>());
        }
		
		return new JqueryDataTable(vo.getList(), listCount, listCount);
	}
	

	/**
	 * 删除提现申请相关订单
	 */
	@RequestMapping(value="/delete/drwaorder", method=RequestMethod.GET)
    @ResponseBody
	public Result deleteDrawOrder (
			@RequestParam(value="id", required=false) Integer id) {
		Result result = new Result();
		drawCacheServie.deleteByPrimaryKey(id);
		result.setCode(CsbtConstants.RESULT_CODE_SUCCESS);
		result.setMsg("成功");
		return result;
	}

	/**
	 * 确认打款
	 */
	@RequestMapping(value="/payment/confirm", method=RequestMethod.GET)
    @ResponseBody
	public Result confirmPayMent ( Model model,
			@RequestParam(value="id", required=false) Integer id) {
		Result result = new Result();
		try {
			drawCacheServie.confimPayment(id);
			result.setCode(CsbtConstants.RESULT_CODE_SUCCESS);
			result.setMsg("成功");
		} catch (Exception e) {
			result.setCode(CsbtConstants.RESULT_CODE_FAIL);
			result.setMsg("失败");
			e.printStackTrace();
		}
		return result;
	}
}
