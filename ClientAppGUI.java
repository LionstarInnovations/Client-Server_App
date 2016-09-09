package comp2449.cwk2;

/************************************
*									*
*	Marcus McFarlane				*
*	Networking and IT Management	*
*	University of Leeds				*
*									*
*************************************/


// Imports
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.awt.*;


/**
 * GUI class that is used to to start the Client Application GUI.
 * */
public class ClientAppGUI extends JFrame implements Runnable{

	// Fields
	private JButton uploadBtn;
	private JTextArea fileListText;
	private JButton downloadBtn;
	private JTextField uploadText;
	private JButton fileListBtn;
	private JButton uploadFileBtn;
	private JButton downloadFileBtn;
	private JTextField downloadText;

	
	private JButton connectBtn;
	private JTextField ipText;
	private JTextField portText;
	private String localhost;
	
	private String ip;
	private int port;
	
	
	/**
	 * Constructor method
	 * 
	 * */
	public ClientAppGUI(){
	}
	
	public ClientAppGUI(String ipInput, int portInput){
		
		ip = ipInput;
		
		port = portInput;
	}
	
	/**
	 * Class that is used as an action listener, that provides the 
	 * to start a client socket and connect to a server.
	 * 
	 * */
	private class ConnectListen implements ActionListener {
		public void actionPerformed( ActionEvent event){
				    	
	    	ip = ipText.getText();
	    	
	    	String portString = portText.getText();

	    	port = Integer.parseInt(portString);		    	
		}	
	}
	
	
	/**
	 * Class that is used as an action listener which uploads an image to the server.
	 * 
	 * */
	private class UploadListen implements ActionListener {
		public void actionPerformed( ActionEvent event){
	    	
	    	String uploadFileText = uploadText.getText();
			
			Client cli = new Client(ip, port, uploadFileText);
			
			cli.uploadImageToServer();   
		}	
	}
	
	
	/**
	 * Class that is used as an action listener, which downloads an images from the server.
	 * 
	 * */
	private class DownloadListen implements ActionListener {
		public void actionPerformed( ActionEvent event){
		
	    	String downloadFileText = downloadText.getText();

	    	Client cli = new Client(ip, port, downloadFileText);
			
			cli.downloadImageFromServer();			
		}	
	}
	
	
	/**
	 * Select an image to upload
	 * 
	 * */
	private class UpFileSelectListen implements ActionListener {
		public void actionPerformed( ActionEvent event){
			
			JFileChooser fileChooser = new JFileChooser();
						
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			
			int result = fileChooser.showOpenDialog(null);
			
			if (result == JFileChooser.APPROVE_OPTION) {
			    File selectedFile = fileChooser.getSelectedFile();
			    
			    // assign the below code to the relevant textBox
			    
			    System.out.println("Selected file: " + selectedFile.getPath());
			    
			    String fileUploadPath = selectedFile.getAbsolutePath();
			    
			    uploadText.setText(fileUploadPath);		    
			}			
		}	
	}

	
	/**
	 * Select an image to download
	 * 
	 * */
	private class DownFileSelectListen implements ActionListener {
		public void actionPerformed( ActionEvent event){

			JFileChooser fileChooser = new JFileChooser();
						
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			
			int result = fileChooser.showOpenDialog(null);
			
			if (result == JFileChooser.APPROVE_OPTION) {
			    File selectedFile = fileChooser.getSelectedFile();
			    			    
			    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			    
			    String fileDownloadPath = selectedFile.getAbsolutePath();
			    
			    downloadText.setText(fileDownloadPath);    
			}
		}	
	}
	
	
	/**
	 * List files stored in the server folder
	 * 
	 * */
	private class FileListListen implements ActionListener {

		public void actionPerformed( ActionEvent event){

	    	Client cli = new Client(ip, port, localhost);
	    	
	    	cli.listFilesClient();
	    	
	    	String[] ar = cli.getArray();
			
			fileListText.append("Server Folder Files: \n");
	 
			for (int i = 0; i < ar.length; i++) {
				
				fileListText.append(ar[i]);
				fileListText.append("\n");		   
			}		
		}
	}
	
		
	//---------------------GUI Objects----------------//
	/**
	 * Method containing the GUI components.
	 * 
	 * */
	public void guiObjects(JPanel panel){
			
		panel.setLayout(null);

	    Font font = new Font("Serif", Font.BOLD, 15);
		
		// (col, row, col-span, row-span)
	    	    	    
	    // JButton
	 	uploadBtn = new JButton("Upload");
	 	uploadBtn.setBounds(10, 30, 110, 25);
	 		
	 	//- Adding Action Listener to button.
	 	uploadBtn.addActionListener(new UploadListen());
	 		
	 	uploadBtn.setFont(font);
	 	uploadBtn.setBackground(Color.GRAY);		
	 	panel.add(uploadBtn);

		// JTextField
		uploadText = new JTextField(20);
		uploadText.setBounds(150, 30, 250, 25);
		panel.add(uploadText);
		
		// JButton
		uploadFileBtn = new JButton("Upload Path");
		uploadFileBtn.setBounds(10, 80, 150, 25);
		
		//- Adding Action Listener to button.
		uploadFileBtn.addActionListener(new UpFileSelectListen());
		
		uploadFileBtn.setFont(font);
		uploadFileBtn.setBackground(Color.GRAY);
		panel.add(uploadFileBtn);	
		
		// JButton
		fileListBtn = new JButton("List Files");
		fileListBtn.setBounds(10, 130, 110, 25);
		
		//- Adding Action Listener to button.
		fileListBtn.addActionListener(new FileListListen());
		
		fileListBtn.setFont(font);
		fileListBtn.setBackground(Color.GRAY);
		panel.add(fileListBtn);
		
		// JTextArea
		fileListText = new JTextArea(20, 20);
		fileListText.setBounds(150, 130, 250, 200);
		panel.add(fileListText);
			
		// JButton
		downloadBtn = new JButton("Download");
		downloadBtn.setBounds(10, 350, 110, 25);
		
		//- Adding Action Listener to button.
		downloadBtn.addActionListener(new DownloadListen());
		
		downloadBtn.setFont(font);
		downloadBtn.setBackground(Color.GRAY);
		panel.add(downloadBtn);	
		
		downloadText = new JTextField(20);
		downloadText.setBounds(150, 350, 250, 25);
		panel.add(downloadText);
		
		// JButton
		downloadFileBtn = new JButton("Download Path");
		downloadFileBtn.setBounds(10, 400, 150, 25);
		
		//- Adding Action Listener to button.
		downloadFileBtn.addActionListener(new DownFileSelectListen());
		
		downloadFileBtn.setFont(font);
		downloadFileBtn.setBackground(Color.GRAY);
		panel.add(downloadFileBtn);	
		
		// JButton
		connectBtn = new JButton("Connect");
		connectBtn.setBounds(10, 450, 110, 25);
		
		//- Adding Action Listener to button.
		connectBtn.addActionListener(new ConnectListen());
		
		connectBtn.setFont(font);
		connectBtn.setBackground(Color.GRAY);
		panel.add(connectBtn);	
		
		// JTextField
		ipText = new JTextField(20);
		ipText.setBounds(150, 450, 110, 25);
		panel.add(ipText); 
		
		// JTextField
		portText = new JTextField(20);
		portText.setBounds(290, 450, 110, 25);
		panel.add(portText); 
	}
	
	
	/**
	 * Run method
	 * 
	 * */
	public void run(){
	
		try {
			
			JFrame frame = new JFrame("Client Application");
			frame.setSize(500, 600);
			
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			// Creating JPanel
			JPanel panel = new JPanel();
			frame.add(panel);
			
			guiObjects(panel);
			
			panel.setBackground(Color.LIGHT_GRAY);
			
			frame.setVisible(true);				
		} 
		catch (Exception e) {
		}
	}
	
	
	/**
	 * Main method 
	 * */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new ClientAppGUI() );
	}
}