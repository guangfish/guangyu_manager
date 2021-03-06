package com.bt.om.web.controller.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.om.cache.JedisPool;
import com.bt.om.entity.TkInfoTask;
import com.bt.om.util.GsonUtil;
import com.bt.om.web.BasicController;
import com.bt.om.web.controller.app.task.Queue;
import com.bt.om.web.controller.app.task.WebQueue;
import com.bt.om.web.controller.app.vo.AppCrawlBean;
import com.bt.om.web.controller.app.vo.AppCrawlTaskBean;

/**
 * APP端爬虫接口
 */
@Controller
@RequestMapping(value = "/app/api")
public class AppCawalTaskController extends BasicController {
	private static final Logger logger = Logger.getLogger(AppCawalTaskController.class);
	@Autowired
	private JedisPool jedisPool;

	 
	@RequestMapping(value = "/getTask", method = RequestMethod.POST)
	@ResponseBody
	public AppCrawlTaskBean getTask(Model model, HttpServletRequest request, HttpServletResponse response) {
		logger.info("收到app任务获取请求");
		AppCrawlTaskBean appCrawlTaskBean = null;
		TkInfoTask tkInfoTask = null;
		Object object = Queue.get();
		appCrawlTaskBean = new AppCrawlTaskBean();
		if (object != null) {
			tkInfoTask = (TkInfoTask) object;
			appCrawlTaskBean.setStatus("1");
			appCrawlTaskBean.setSign(tkInfoTask.getSign());
			appCrawlTaskBean.setTklStr(tkInfoTask.getProductUrl());
			logger.info("队列有任务返回");
		} else {
			appCrawlTaskBean.setSign("");
			appCrawlTaskBean.setStatus("0");
			appCrawlTaskBean.setTklStr("");
			logger.info("队列中无任务");
		}
		return appCrawlTaskBean;
	}

	@RequestMapping(value = "/pushData", method = RequestMethod.POST)
	@ResponseBody
	public Model pushData(Model model, HttpServletRequest request, HttpServletResponse response) {
		String data = request.getParameter("data");
		logger.info("收到APP端任务结果数据推送");
		logger.info(data);
		AppCrawlBean appCrawlBean = GsonUtil.GsonToBean(data, AppCrawlBean.class);

		String sign = appCrawlBean.getSign();
		jedisPool.putInCache("", sign, data, 60);

		return model;
	}
	
	//京东爬虫任务获取请求
	@RequestMapping(value = "/getJdTask", method = RequestMethod.POST)
	@ResponseBody
	public AppCrawlTaskBean getJdTask(Model model, HttpServletRequest request, HttpServletResponse response) {
		logger.info("收到JD任务获取请求");
		AppCrawlTaskBean appCrawlTaskBean = null;
		TkInfoTask tkInfoTask = null;
		Object object = WebQueue.get();
		appCrawlTaskBean = new AppCrawlTaskBean();
		if (object != null) {
			tkInfoTask = (TkInfoTask) object;
			appCrawlTaskBean.setStatus("1");
			appCrawlTaskBean.setSign(tkInfoTask.getSign());
			appCrawlTaskBean.setTklStr(tkInfoTask.getProductUrl());
			logger.info("队列有任务返回");
		} else {
			appCrawlTaskBean.setSign("");
			appCrawlTaskBean.setStatus("0");
			appCrawlTaskBean.setTklStr("");
			logger.info("队列中无任务");
		}
		return appCrawlTaskBean;
	}
	
	@RequestMapping(value = "/pushJdData", method = RequestMethod.POST)
	@ResponseBody
	public Model pushJdData(Model model, HttpServletRequest request, HttpServletResponse response) {
		String data = request.getParameter("data");
		logger.info("收到JD端任务结果数据推送");
		logger.info(data);
		TkInfoTask tkInfoTask = GsonUtil.GsonToBean(data, TkInfoTask.class);

		String sign = tkInfoTask.getSign();
		jedisPool.putInCache("", sign, tkInfoTask, 60);

		return model;
	}
}
