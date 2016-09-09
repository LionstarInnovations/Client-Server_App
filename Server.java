package comp2449.cwk2;

/************************************
*									*
*	Marcus McFarlane				*
*	Student Id: 200912969			*
*	Coursework 2					*
*	Networking and IT Management	*
*	University of Leeds				*
*									*
*************************************/

// Imports
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Server {
	
	// Fields
    private static Server server; 
    private ServerSocket serverSocket;    
    private BufferedImage img;

	
    /**
     * This executor service has 10 threads. 
     * So it means your server can process max 10 concurrent requests.
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(10);        

    
    /**
     * Method Run that initiates the server socket to start communicating with the client.
     * 
     * */
    private void runServer() {        
    	
        int serverPort = 4000;
        
        try {
            System.out.println("Starting Server... ");
            serverSocket = new ServerSocket(serverPort); 

            while(true) {
                System.out.println("Waiting for request from client.. ");
                try {
                    Socket s = serverSocket.accept();
                    System.out.println("Processing request");
                    executorService.submit(new ServiceRequest(s));
                } catch(IOException ioe) {
                    System.out.println("Error accepting connection");
                    ioe.printStackTrace();
                }
            }
        }catch(IOException e) {
            System.out.println("Error starting Server on "+serverPort);
            e.printStackTrace();
        }
    }

    
    /**
     * Method that is used to stop the server.
     * */
    private void stopServer() {
        //Stop the executor service.
        executorService.shutdownNow();
        try {
            //Stop accepting requests.
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error in server shutdown");
            e.printStackTrace();
        }
        System.exit(0);
    }

    
    /**
     * Class that contains the methods to perform the functions 'upload'. 'download', and 'listFiles'.
     * */
    public class ServiceRequest implements Runnable {

        private Socket socket;

        public ServiceRequest(Socket connection) {
            this.socket = connection;
        }
        
        
        /**
         * Method that is used to download an image that has been uploaded by the client.
         * 
         * */
        public void downloadImageFromClient(){
    		
    		try{
    	         	      
    	        System.out.println("Server: Server reading an image from the client... ");
    	        
    	        InputStream in = socket.getInputStream();
    	        DataInputStream dis = new DataInputStream(in);     
    	      
    	        int len = dis.readInt();
    	        System.out.println("Image Size: " + len/1024 + "KB");
    	      
    	        byte[] data = new byte[len];
    	        
    	        dis.readFully(data);
    	            	      
    	        InputStream ian = new ByteArrayInputStream(data);
    	        BufferedImage bImage = ImageIO.read(ian);
    	        
    	        System.out.println("Server: Server read image from the client. ");
    	        
    		    System.out.println("Server: Writing image to the server folder... ");
    	        
    	        ImageIO.write(bImage, "jpg", new File("serverFolder/images/newServerImage.jpg"));	 
    	      
    	        System.out.println("Server: Image written to the server folder. ");
    	        
    	        // Test if the correct image has been obtained by displaying the image.
    	      
    	        System.out.println("Server: Displaying uploaded image. ");
    	        
    	        JFrame f = new JFrame("Server Folder: Uploaded Image ");
    	        ImageIcon icon = new ImageIcon(bImage);
    	        JLabel l = new JLabel();
    	      
    	        l.setIcon(icon);
    	        f.add(l);
    	        f.pack();
    	        f.setVisible(true);
    			
    	        System.out.println("Server: Server socket closed. ");
    	    }
    	    catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
    	}


    	/**
    	 * Method used to upload an image to the client.
    	 * 
    	 * */
    	public void uploadImageToClient(){
    		 		
    		try{
    		         
    	        System.out.println("Server: Server retrieving file from server folder");
    	        	        
    	        System.out.println("Server: Reading image from the server folder... ");
    	        
    	        DataInputStream dis = new DataInputStream(socket.getInputStream());

			    String downloadFileText = dis.readUTF();
			    
				System.out.println(downloadFileText);
    	        
    		    img = ImageIO.read(new File(downloadFileText));
    		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		 
    		    System.out.println("Server: Image read from the server folder. ");
    		    
    		    //-------------- Writing the file to the Client
    		    
    		    System.out.println("Server: Sending the image file to the client... ");
    		    
    		    ImageIO.write(img, "jpg", baos);
    		    baos.flush();
    		 
    		    byte[] bytes = baos.toByteArray();
    		    baos.close();
    		 		 
    		    OutputStream out = socket.getOutputStream(); 
    		    DataOutputStream dos = new DataOutputStream(out);
    		 
    		    dos.writeInt(bytes.length);
    		    dos.write(bytes, 0, bytes.length);
    		 
    		    System.out.println("Server: Image sent to the client. ");
    		
    		    dos.close();
    		    out.close();
    		    	        
    	        System.out.println("Server: Server socket closed. ");       
    	    }
    		catch (Exception e) {
    	        System.out.println("Exception: " + e.getMessage());
    	    }
    	}	

        
        /**
    	 * Method used to retrieve the server folder file list and send the file 
    	 * list to the client
    	 * */
        public void listFilesServer(){
        	
        	try {
    	        
    	        System.out.println("Server: Server reading file list from the server folder... ");
        		
        		File file = new File("serverFolder/images");

                // Reading directory contents
                File[] files = file.listFiles();
    	        
    	        System.out.println("Server: Server read file list from the server folder. ");
    	        	        
    	        System.out.println("Server: Server sending the file list to the client... ");
    	        	        
    	        System.out.println(files[1]);
    	        
    	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    	        out.print(java.util.Arrays.toString(files));
            
    		    System.out.println("Server: Server sent the file list to the client. ");
    		    
    		    out.close();
    		    //server.close();
     	
    		    System.out.println("Server: Server socket closed. ");
    		} 
        	catch (Exception e) {
    		}
        }
 
        
        /**
         * Method log used to log the client requests permanently in a text file.
         * 
         * @param ip
         * @param request
         */
        public void log(InetAddress ip, String request){
    		
        	BufferedWriter writer = null;
   	        
        	try {
   	        	
   	            File logFile = new File("Client Log");

   	            System.out.println(logFile.getCanonicalPath());
   	            
   	            // Appending to an existing text file
   	            writer = new BufferedWriter(new FileWriter(logFile, true));
   	         
   	            // Creating the date.	   	            
   	            
   	            Date date = new Date();

   	            System.out.println(date.toString());

   	            String dateString = date.toString();
   	                        
   	            // Converting INET address to a string.
            	   	            
   	            String host = ip.toString();
   	            
   	            // Writing (appending) to the text file 

   	            writer.write("\nClient Request: \n\n");
   	            
   	            writer.write(dateString);
   	            
   	            writer.write(host);
   	            
   	            writer.write(request);	
            	   	            
   	        } catch (Exception e) {
   	            e.printStackTrace();
   	        } finally {
   	            try {
   	                writer.close();
   	            } catch (Exception e) {
   	            }
   	        }
        }

        
        /**
         * Method run that is executed immediately when the program starts.
         * */
        public void run() {

			try {
				
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				
			    String line = dis.readUTF();
			    
				InetAddress inet = socket.getInetAddress();
				
				boolean s = true;
				
				while(s){
				
					if(line.equals("list"))
		    		{				    	
				    	listFilesServer();

				    	log(inet, "List Files");
				    	
				    	s = false;
		    		}
					else if(line.equals("upload")){
						
						downloadImageFromClient();
						
				    	log(inet, "Upload Image To Server");
						
				    	s = false;
					}
					else if(line.equals("download")){

						uploadImageToClient();
						
				    	log(inet, "Download Image From Server");
						
				    	s = false;
					}
				}					
			} 
			catch (IOException e1) {
				e1.printStackTrace();
			}
            try {
                socket.close();   
            }
            catch(IOException ioe) {
                System.out.println("Error closing client connection");
            }
        }        
    }
    
    
    /**
     * Main method
     * */
    public static void main(String[] args) throws IOException {
        server = new Server();
        server.runServer();
    }
}