package at.ac.tuwien.mnsa.ue3.smsapp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.mnsa.ue3.properties.PropertiesServiceFactory;
import at.ac.tuwien.mnsa.ue3.properties.SMSPropertiesService;

public class SMSAppTest {

	private static final Logger log = LoggerFactory.getLogger(SMSAppTest.class);

	private static Properties prop;

	private static SMSApp smsApp;

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
//		log.debug(" ");
//		log.debug("=======================");
//		log.debug("Setting up UnitTests...");
//		log.debug("=======================");
//
//		sms = SMSApp.getInstance();
//
//		prop = PropertiesServiceFactory.getPropertiesService().getProperties();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
//		log.debug(" ");
//		log.debug("====================");
//		log.debug("Starting teardown...");
//		log.debug("====================");
//
//		sms.close();
//
//		log.debug("Teardown done. Shutting down now...");
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void sendPduSMS() {
//	}
//
//	@Test
//	public void sendTextSMS() {
//		String answer[] = { "", "" };
//
//		log.debug("\n");
//		log.debug("====================");
//		log.debug("Send SMS in TextMode");
//		log.debug("====================");
//
//		// Is there a valid RECIPIENT_KEY available in the properties file?
//		if ((prop.getProperty(SMSPropertiesService.RECIPIENT_KEY)) != null
//				&& (prop.getProperty(SMSPropertiesService.RECIPIENT_KEY)
//						.length() > 0)) {
//
//			log.debug("Enabling TextMode...");
//			answer = sms.sendATCommand("AT+CMGF=1", SMSApp.DELAY_DEFAULT);
//
//			log.debug("Return-Code: {}", answer[1]);
//			log.debug("Telephone sent: {}", answer[0]);
//
//			log.debug("Sending SMS to {}",
//					prop.getProperty(SMSPropertiesService.RECIPIENT_KEY));
//			sms.sendATCommand(
//					"AT+CMGS=\""
//							+ prop.getProperty(SMSPropertiesService.RECIPIENT_KEY)
//							+ "\"\r\n", SMSApp.DELAY_DEFAULT);
//
//			answer = sms.sendATCommand("these go to 11" + '\032',
//					SMSApp.DELAY_SMS);
//
//			log.debug("Return-Code: {}", answer[1]);
//			log.debug("Telephone sent: {}", answer[0]);
//
//		} else
//			log.error("No recipient telephone number specified!");
//
//		assertTrue(answer[1].equalsIgnoreCase("ok"));
//	}
//
//	@Test
//	public void getModemInformation() {
//		String answer[];
//
//		log.debug(" ");
//		log.debug("=================");
//		log.debug("Modem Information");
//		log.debug("=================");
//
//		answer = sms.sendATCommand("ATI7", SMSApp.DELAY_DEFAULT);
//
//		log.debug("Return-Code: {}", answer[1]);
//		log.debug("Telephone sent: {}", answer[0]);
//
//		assertTrue(answer[1].equalsIgnoreCase("ok"));
//	}
//
//	@Test
//	public void testCall() {
//		String answer[] = { "", "" };
//
//		log.debug(" ");
//		log.debug("==============");
//		log.debug("Telephone-Call");
//		log.debug("==============");
//
//		// Is there a valid RECIPIENT_KEY available in the properties file?
//		if ((prop.getProperty(SMSPropertiesService.RECIPIENT_KEY)) != null
//				&& (prop.getProperty(SMSPropertiesService.RECIPIENT_KEY)
//						.length() > 0)) {
//			log.debug("Sending \"ATD"
//					+ prop.getProperty(SMSPropertiesService.RECIPIENT_KEY)
//					+ ";\"");
//
//			answer = sms
//					.sendATCommand(
//							"ATD+"
//									+ prop.getProperty(SMSPropertiesService.RECIPIENT_KEY)
//									+ ";", SMSApp.DELAY_CALL);
//
//			log.debug("Return-Code: {}", answer[1]);
//			log.debug("Telephone sent: {}", answer[0]);
//
//			// LOG.debug("Hangup telephone...");
//			// sms.sendATCommand("ATH", SMSApp.DELAY_DEFAULT);
//
//		} else
//			log.error("No recipient telephone number specified!");
//
//		assertTrue(answer[1].equalsIgnoreCase("ok")
//				|| answer[1].equalsIgnoreCase("no carrier"));
//	}
}