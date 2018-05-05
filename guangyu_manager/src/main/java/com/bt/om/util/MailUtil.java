package com.bt.om.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.http.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.bt.om.vo.common.MailVo;

public class MailUtil {

	private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);

	private static final String DEFAULT_ENCODE = Consts.UTF_8.name();

	private static Properties props = new Properties();

	static {
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.timeout", "25000");
		props.put("mail.smtp.starttls.enable", "true");
		//阿里云不开放25端口，所以这里改成465端口 ，不配置默认用25端口
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.port", "465");
		
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	}

	/**
	 * send email
	 */
	public static void sendEmail(MailVo vo) {
		try {
			JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
			senderImpl.setHost(vo.getHost());
			MimeMessage mailMessage = senderImpl.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, DEFAULT_ENCODE);
			messageHelper.setFrom(vo.getFrom());
			messageHelper.setTo(vo.getTo());
			messageHelper.setSubject(vo.getSubject());
			messageHelper.setText(vo.getContent(), true);
			MyAuthenticator auth = new MyAuthenticator(vo.getAccount(), vo.getPassword());
			Session session = Session.getDefaultInstance(props, auth);
			senderImpl.setSession(session);
			senderImpl.send(mailMessage);
			logger.info(MessageFormat.format("Send monitor email success, email text:{0},", vo));
		} catch (MessagingException e) {
			logger.error(MessageFormat.format("Send monitor email failure, email text:{0},", vo), e);
		}
	}

	public static void sendEmail(String title, String content, List<String> tos) {
		MailVo mailVo = new MailVo();
		mailVo.setFrom(ConfigUtil.getString("monitor.email.from"));
		mailVo.setTo(listToString(tos, ';').split(";"));
		mailVo.setHost(ConfigUtil.getString("monitor.email.from.host"));
		mailVo.setAccount(ConfigUtil.getString("monitor.email.from.account"));
		mailVo.setPassword(ConfigUtil.getString("monitor.email.from.password"));
		mailVo.setSubject(title);
		mailVo.setContent(content);
		MailUtil.sendEmail(mailVo);
	}

	public static String listToString(List<String> list, char separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i)).append(separator);
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}
	
	public static void main(String []args){
		if ("on".equals(ConfigUtil.getString("monitor.email.send.status"))) {
			List<String> tos = new ArrayList<>();
			String mailToStr = ConfigUtil.getString("monitor.email.to");
			String[] mailTos = mailToStr.split(";");
			for (int i = 0; i < mailTos.length; i++) {
				tos.add(mailTos[i]);
			}
			MailUtil.sendEmail("逛鱼返利", "用户发起提现申请，请及时处理", tos);
		}
	}
}
