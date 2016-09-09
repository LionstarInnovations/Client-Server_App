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
import java.net.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * Client class that is used to start the client application.
 * 
 * */
public class Client implements Runnable{

	// Fields
	private static String line;
	private String[] array;
	private String uploadFileText;
	private String downloadFileText;
	private String ip;
	private int port;
	
	
	/**
	 * Constructor, used to pass the relevant strings from the file chooser path to the
	 * relevant methods for upload and download.
	 * */	
	public Client(String host, int por, String text){
		
		ip = host;
		
		port = por;
		
		uploadFileText = text;
		
		downloadFileText = text;		
	}

	
	/**
	 * Method getter for getting the array that contains the list of files stored on
	 * the server
	 * */
	public String[] getArray() 
	{
		return array;
	}

	
	/**
	 * 
	 * Method that communicates with the server and uploads an image 
	 * to the server.
	 * 
	 * */
    public void uploadImageToServer(){
    	           
		try {
			
			System.out.println("Client: Cleint socket starting... ");
			
			Socket soc;
		    BufferedImage img = null;
		    soc=new Socket(ip,port);
		    
		    System.out.println("Client: Client socket is running. ");
		  
		    // Signalling to the server to perform a method
		    
		    System.out.println("Client: Signalling server for upload command");
    		
    		OutputStream out = soc.getOutputStream(); 
		    DataOutputStream dos = new DataOutputStream(out);
		    		
			dos.writeUTF("upload");
	    	
			dos.flush();
		    
		    // Uploading image to the server
		  		    
		    System.out.println("Client: Reading image from client folder... ");
		    
		    img = ImageIO.read(new File(uploadFileText));
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 
		    ImageIO.write(img, "jpg", baos);
		    baos.flush();
		 
		    byte[] bytes = baos.toByteArray();
		    baos.close();
		 
		    System.out.println("Client: Read image from the client folder. ");
		    
		    System.out.println("Client: Sending image to server... ");
		 
		    dos.writeInt(bytes.length);
		    dos.write(bytes, 0, bytes.length);
		 
		    System.out.println("Client: Image sent to server. ");
		
		    dos.close();
		    out.close();
		    soc.close();
		    
		    System.out.println("Client: Client socket closed. ");
		}
		catch (Exception e) {
			
		    System.out.println("Exception: " + e.getMessage());
		}
	}

    
    /**
     * 
     * Method that communicates with the server and downloads an image from
     * the server.
     * 
     * */
    public void downloadImageFromServer(){
        
	    try{
	    	
	    	System.out.println("Client: Starting client socket... ");
	    	
		    Socket soc;
		    soc=new Socket(ip,port);
		    
		    System.out.println("Client: Client socket is running. ");
		   
		    // Signalling to the server to perform a method
		    
		    System.out.println("Client: Signalling server for download command");
    		
    		OutputStream out = soc.getOutputStream(); 
		    DataOutputStream dos = new DataOutputStream(out);
		    		
			dos.writeUTF("download");
	    	
			dos.flush();
		    
			// Signalling to the server to send a variable containing the image filepath.
		    
		    System.out.println("Client: Signalling server for list command");
    		
    		out = soc.getOutputStream(); 
		    dos = new DataOutputStream(out);
		    		
			dos.writeUTF(downloadFileText);
	    	
			dos.flush();		
		    
		    //------------------ Downloading image
		    
		    System.out.println("Client: Downloading image from the server... ");
		    
		    InputStream in = soc.getInputStream();
	        DataInputStream dis = new DataInputStream(in);  
	        
	        System.out.println(in);
	      
	        int len = dis.readInt();
	        System.out.println("Image Size: " + len/1024 + "KB");
	      
	        byte[] data = new byte[len];
	        dis.readFully(data);

	        InputStream ian = new ByteArrayInputStream(data);
	        BufferedImage bImage = ImageIO.read(ian);
	  
	        System.out.println("Client: Image downloaded from the server. ");
	        
	        dis.close();
	        in.close();
	        
	        // Writing the image to the client folder
	        
	        System.out.println("Client: Writing image to the client folder... ");
	        
	        
	        ImageIO.write(bImage, "jpg", new File("clientFolder/images/newClientImage.jpg"));	        

	        System.out.println("Client: Image written to the client folder. ");
	        
	        // Test if the correct image has been downloaded, displaying the image.
	       
	        System.out.println("Client: Displaying downloaded image. ");
	        
	        JFrame f = new JFrame("Client Folder: Downloaded Image");
	        ImageIcon icon = new ImageIcon(bImage);
	        JLabel l = new JLabel();
	       
	        l.setIcon(icon);
	        f.add(l);
	        f.pack();
	        f.setVisible(true);
	       
	        soc.close();
    	    
	        System.out.println("Client: Client socket closed. ");
	    }
	    catch (Exception e) {
        System.out.println("Exception: " + e.getMessage());
	    }	   
    }
	   
    
    /**
     * Method that communicates with the server and downloads and
     * lists all of the files that are stored in the server folder.
     * 
     * */
    public void listFilesClient(){
    	
    	try {   		
    		
    		System.out.println("Client: Starting client socket... ");
    		
    		Socket soc;
		    soc=new Socket(ip,port);
		    
		    System.out.println("Client: Client socket is running. ");
		    
		    // Signalling to the server to perform a method
		    
		    System.out.println("Client: Signalling server for list command");
    		
    		OutputStream out = soc.getOutputStream(); 
		    DataOutputStream dos = new DataOutputStream(out);
		    
			dos.writeUTF("list");
	    	
			dos.flush();		
	
		    // Reading the file list from the server
		    
		    System.out.println("Client: Reading the file list from the Server... ");
		    
	        DataInputStream dataInputStream = new DataInputStream(soc.getInputStream());
	        
	        // Makes the client wait for a response from the server.
	        // for a specified period of time if there is no input through
	        // the input stream.
	        
	        int attempts = 0;
	        while(dataInputStream.available() == 0 && attempts < 5000)
	        {
	            attempts++;
	            Thread.sleep(10);
	        }
	      	                
		    BufferedReader in = new BufferedReader(new
	        InputStreamReader(soc.getInputStream()));                   
	        
		    System.out.println("Client: File list read from the server. ");
		    
		    System.out.println("Client: Displaying file list from the server folder... ");
		    
		    // Splitting input into an array
		    
		    line = in.readLine();
		    
		    System.out.println(line);
		    
		    array = line.split(",");
		    
		    for (int i = 0; i < array.length; i++) {
				
				System.out.println(array[i]);
		    }
		    
		    System.out.println("Client: Done");
		    
	        in.close();
	        soc.close(); 
	         
	        System.out.println("Client: Client socket closed. "); 
		}
    	catch (Exception e) {
		}	
    }
    
  
    /**
    * Method run that is executed immediately when the program starts.
    * 
    * */  
    @Override
	public void run() {	    
	}
}