package com.blackbox.ui;

import com.blackbox.jms.MessageSender;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Message Sender panel that allows a user to open a text file and send the file contents
 * to a pre-defined JMS destination.
 */
public class MessageSenderPanel extends JPanel
	implements ActionListener
{
	JButton openButton, sendButton;
	JTextArea    fileContents;
	JFileChooser fc;
	File         selectedFile;
	JFrame       container;

	public MessageSenderPanel(JFrame container)
	{
		super( new BorderLayout() );

		this.container = container;

		//Create the fileContents first, because the action listeners
		//need to refer to it.
		fileContents = new JTextArea( 5, 20 );
		fileContents.setMargin( new Insets( 5, 5, 5, 5 ) );
		fileContents.setEditable( false );
		JScrollPane logScrollPane = new JScrollPane( fileContents );

		//Create a file chooser
		fc = new JFileChooser();

		openButton = new JButton( "Open a File...",
								  createImageIcon("/images/middle.gif") );
		openButton.addActionListener( this );

		sendButton = new JButton( "Send",
								  createImageIcon("/images/right.gif") );
		sendButton.setToolTipText( "Please open a file before pressing Send" );
		sendButton.setEnabled( false );
		sendButton.addActionListener( this );

		//For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); //use FlowLayout
		buttonPanel.add( openButton );
		buttonPanel.add( sendButton );

		//Add the buttons and the fileContents to this panel.
		add( buttonPanel, BorderLayout.PAGE_START );
		add( logScrollPane, BorderLayout.CENTER );
	}

	public void actionPerformed( ActionEvent e )
	{

		//Handle open button action.
		if ( e.getSource() == openButton )
		{
			int returnVal = fc.showOpenDialog( MessageSenderPanel.this );

			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				selectedFile = fc.getSelectedFile();
				//This is where a real application would open the file.
				showFileContents( selectedFile, fileContents );
				// enable send button after loading the file
				sendButton.setEnabled( true );
			}
			fileContents.setCaretPosition( fileContents.getDocument().getLength() );

			//Handle save button action.
		}
		else if ( e.getSource() == sendButton )
		{

			if ( selectedFile == null )
			{
				JOptionPane.showMessageDialog( this,
											   "Please select a file before pressing Send.",
											   "Error",
											   JOptionPane.ERROR_MESSAGE );
			}
			else
			{
				// send loaded file to JMS destination
				String toSend = fileContents.getText();
				try
				{
					MessageSender.sendMessageToQueue( toSend );
					JOptionPane.showMessageDialog( this,
												   "Message sent succesfully!!",
												   "Success",
												   JOptionPane.INFORMATION_MESSAGE );
				}
				catch ( Exception exc )
				{
					// report exception to user
					JOptionPane.showMessageDialog( this,
												   "Not possible to send message. Error: " + exc.getMessage(),
												   "Error",
												   JOptionPane.ERROR_MESSAGE );

					exc.printStackTrace();
				}
			}
		}
	}

	private void showFileContents( File selectedFile, JTextArea fileContents )
	{
		fileContents.setText( "" );
		FileReader fr = null;
		try
		{
			fr = new FileReader(selectedFile);
			LineIterator lineIterator = IOUtils.lineIterator( fr );
			int lineCount = 0;
			int maxLength = 0;
			while (lineIterator.hasNext()) {
				String line = lineIterator.next();
				fileContents.append( line );
				if (lineIterator.hasNext()) {
					fileContents.append( "\r\n" );
				}
				lineCount++;
				maxLength = maxLength < line.length() ? line.length() : maxLength;
			}

			// 600px per 36 lines
			// 820px per 141 chars length

			if (lineCount > 0 && maxLength > 0) {
				this.container.getContentPane().setPreferredSize( new Dimension( 6 * maxLength, 17 * lineCount + 100 ));
				this.container.pack();
			}

		}
		catch ( IOException ioexc )
		{
			ioexc.printStackTrace();
			fileContents.append( ioexc.getMessage() );
		}
		finally
		{
			if ( fr != null )
			{
				try
				{
					fr.close();
				}
				catch ( IOException e1 )
				{
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 */
	protected static ImageIcon createImageIcon( String path )
	{
		java.net.URL imgURL = MessageSenderPanel.class.getResource( path );
		if ( imgURL != null )
		{
			return new ImageIcon( imgURL );
		}
		else
		{
			System.err.println( "Couldn't find file: " + path );
			return null;
		}
	}

}
