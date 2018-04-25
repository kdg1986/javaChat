package chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

public class chatServer {
	
	private String addressIp = "localhost";	
	private ServerSocket server;
	private Socket socket;	
	private HashMap<String, PrintWriter> hm = new HashMap<String, PrintWriter>();
	
	public chatServer() {		
		
			try {
				server = new ServerSocket(8900);
				System.out.println("클라이언트 접속 대기중...");
				while(true){								
					socket	= server.accept();
					new Thread( new chatNameChk() ).start();										
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public static void main(String[] args) {
		new chatServer();
	}
	public class chatNameChk  implements Runnable {
	
		BufferedReader read;
		BufferedReader in;
		PrintWriter out;
		String user_id;
		
		public chatNameChk() {			
			try {
				in =  new BufferedReader( new InputStreamReader(socket.getInputStream()) );
				out = new PrintWriter( socket.getOutputStream() );				
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		
		@Override
		public void run() {									
			try{
				while(true){
					if( null !=  (user_id = in.readLine()) ){
							 if( hm.containsKey(user_id) ){
								 out.println("이미 존재하는 대화명 입니다. code:[idChange]");
								 out.flush();
							 }else{
								 synchronized (hm) { hm.put(user_id, out); }								 
								 out.println("접속 되었습니다.");
								 out.flush();
								 new Thread( new chatServerUserset(in,out,user_id) ).start();								 
								 break;
							 }
					}
					
				}				
			}catch(Exception e){
				System.out.println("nameChk Thread Error");
				e.printStackTrace();
			}
		}
	}
	
	public class chatServerUserset implements Runnable {		
		BufferedReader read;
		BufferedReader in;
		PrintWriter out;
		String user_id;
		String data;
		
		public chatServerUserset(BufferedReader in,PrintWriter out,String user_id) {			
				this.in =  in;
				this.out = out;
				this.user_id = user_id;
				notice(user_id);
		}
	
		@Override
		public void run() {
				try {
					while(true){
						data = in.readLine();
						if( null != data && !data.equals("null") ){
							if( data.contains("/q") ){
								synchronized (hm) { hm.remove(user_id); } 
								broadCast(user_id+"님이 퇴장하셨습니다.");
							}else if( data.contains("/r") ){
								
							}else{
								broadCast(user_id+"===> "+data);
							}
														
						}						
					}	
				} catch (Exception e) {
					System.out.println(user_id+"접속 끊김");
				}finally {
					synchronized (hm) { hm.remove(user_id); } 
					broadCast(user_id+"님이 퇴장하셨습니다.");
				}
		}
	}
	
	public void notice(String user_id){
		String key;
		PrintWriter out;
		Iterator it = hm.keySet().iterator();
		
		while( it.hasNext() ){
			key = (String) it.next();
			if( key.equals(user_id) ) continue;						
			out = hm.get(key);
			out.println(user_id + "님이 접속하셨습니다.(IP=>"+socket.getPort()+")");
			out.println("현재 접속 인원은 "+ hm.size() +" 명 입니다.");
			out.flush();			
		}		
		
	}
	
	public void broadCast(String msg){
		String key;
		PrintWriter out;
		Iterator it = hm.keySet().iterator();
		
		while( it.hasNext() ){
			key = (String) it.next();
			out = hm.get(key);
			out.println(msg);
			out.flush();			
		}	
	}
}
