Message Sender Tool (AKA Black Box)

Pre-requisites:

1 - Java (JRE6.0) installed locally and on the path

USAGE

1 - Copy the BlackBox folder to a location on your desktop
2 - Run the batch file "run.bat"
3 - Click on "Open File" to select the file contents you want to send to a JMS queue
4 - After file is open, click on Send button

FEATURES

1 - Changing the JMS destination - defined on the "messagesender.properties" file
    To change the JMS destination the message will be sent to, just rename the "queue.name" property value to
    the desired queue address. e.g. To send to the "ClearingQueue", have the following line

queue.name=jms.LLB.ClearingQueue.Inbound

Changes are applied after you save the "messagesender.properties" file. Next time you send a message, it will go to the new destination.

2 - Changing the Weblogic JMS server
    To change the Weblogic JMS server to connect to, modify the "jndi.properties" file accordingly, by providing the connection details to the new server.