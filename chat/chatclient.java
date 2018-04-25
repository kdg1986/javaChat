package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class chatclient {
	private Socket client;
	private BufferedReader read;
	private BufferedReader in;
	private PrintWriter out;
	private String data;
	
	public chatclient(String address,String id) {
		
		try {
			
			client = new Socket(address, 8900);
			
			read= new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader( new InputStreamReader( client.getInputStream() ) );
            out = new PrintWriter( client.getOutputStream() );
            
            out.println(id);
            out.flush();
            new Thread( new clientReceiver(client,in,out) ).start();            
            
            while(true){            	
            	data = read.readLine();
                out.println( data );
                out.flush();                
                if(data.equals( "/q") ) {
                    break;
                }
            }
            System.out.print("클라이언트의 접속을 종료합니다. ");
            System.exit( 0 );
		
		} catch (UnknownHostException e) {		
			e.printStackTrace();
		} catch (IOException e) {		
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new chatclient(args[0],args[1]);
	}
	
	public class clientReceiver implements Runnable {	
		
		Socket client;
		BufferedReader in;
		PrintWriter out;
		String data;
		
		public clientReceiver(Socket client,BufferedReader in,PrintWriter out) {
			this.client = client;
			this.in	= in;
			this.out= out;
		}		
		@Override
		public void run() {		
			try{				
				while( true ){					
					if( null != ( data = in.readLine() ) ){ 					
						if( data.contains("code:[idChange]") ){
							System.out.println("===대화명 중복입니다. 다시입력해주세요.===");						
						}else{
							System.out.println(data);
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				try {
					this.in.close();
					this.out.close();
					this.client.close();
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
		}		
	}
	
	
	
	
}


