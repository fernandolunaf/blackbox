package com.blackbox.jms;

import java.io.*;
import java.util.Properties;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Sends message to a queue using the JNDI properties configured in "messagesender.properties" file
 */
public class MessageSender
{
	private static Properties loadEnvironment() throws IOException
	{
		return loadProperties("jndi.properties");
	}

	private static Properties loadProperties( String propertiesFileName ) throws IOException
	{
		Properties properties = new Properties();
		InputStream is ;
		File fileFromLocalFolder = new File( propertiesFileName );
		if (!fileFromLocalFolder.exists()) {
			is = MessageSender.class.getResourceAsStream( "/" + propertiesFileName );
		} else {
			// not found in classpath, try from local folder
			is = new FileInputStream( fileFromLocalFolder );
		}
		properties.load( is );
		return properties;
	}

	private static Properties loadDestination() throws IOException
	{
		return loadProperties("messagesender.properties");
	}

	public static void sendMessageToQueue(String payload ) throws JMSException, NamingException, IOException
	{
		Properties env = loadEnvironment();
		Properties dst = loadDestination();
		Context ctx = new InitialContext(env);

		ConnectionFactory cf = (ConnectionFactory) ctx.lookup((String) dst.get( "factory.name" ));
		Destination destination = (Destination) ctx.lookup((String) dst.get( "queue.name" ));

		Connection connection = cf.createConnection();
		Session session = connection.createSession(true, -1);
		MessageProducer producer = session.createProducer(destination);
		TextMessage textMessage = session.createTextMessage(payload);
		producer.send(textMessage);
		producer.close();
		session.commit();
		session.close();
		connection.close();
		ctx.close();
	}
}
