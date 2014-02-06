package edu.cmu.master.server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

import android.util.Log;

public class CVMasterMessage implements Serializable {

	private static final long serialVersionUID = 18641L;
	public static final int servicePort = 18641;
	public static final String serverHostName = "54.205.10.142";

	public static final int GET_STUDENT_INFO_FLAG = (0x01 << 1);
	public static final int REGISTER_STUDENT_FLAG = (0x01 << 2);
	public static final int NEW_REGISTERED_COURSE_FLAG = (0x01 << 3);

	private int flag;
	private boolean status;
	private Object payload;

	public CVMasterMessage(int flag, boolean status, Object payload) {
		this.flag = flag;
		this.status = status;
		this.payload = payload;
	}
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}
	
	public boolean send(Socket socket) {
		OutputStream output = null;
		ObjectOutputStream objectOutput = null;
		try {
			output = socket.getOutputStream();
		} catch (Exception ex) {
			//System.err.println("failed to get output stream");
			//System.err.println(ex.getMessage());
			Log.e("e", "send 1");
			return false;
		}
		try {
			objectOutput = new ObjectOutputStream(output);
		} catch (Exception ex) {
			//System.err.println("failed to get the output stream");
			//System.err.println(ex.getMessage());
			Log.e("e", "send 2");

			if (objectOutput != null) {
				try {
					objectOutput.close();
				} catch (Exception nestedEx) {
					//System.err.println("failed to close object output stream");
					//System.err.println(nestedEx.getMessage());
					Log.e("e", "send 3");

				}
			}
			return false;
		}
		
		try {
			objectOutput.writeObject(this);
		} catch (Exception ex) {
			System.err.println("failed to send message");
			System.err.println(ex.getMessage());
			if (objectOutput != null) {
				try {
					objectOutput.close();
				} catch (Exception nestedEx) {
					System.err.println("failed to close object output stream");
					System.err.println(nestedEx.getMessage());
				}
			}
			return false;
		}
		return true;

	}
	
	public static CVMasterMessage receive(Socket socket) {
		InputStream input = null;
		ObjectInputStream objectInput = null;
		try {
			input = socket.getInputStream();
		} catch (Exception ex) {
			System.err.println("failed to get input stream");
			System.err.println(ex.getMessage());
			return null;
		}
		try {
			objectInput = new ObjectInputStream(input);
		} catch (Exception ex) {
			System.err.println("failed to get object input stream");
			System.err.println(ex.getMessage());
			if (objectInput != null) {
				try {
					objectInput.close();
				} catch (Exception nestedEx) {
					System.err.println("failed to close object input stream");
					System.err.println(nestedEx.getMessage());
				}
			}
			return null;
		}
		CVMasterMessage incomingMessage = null;
		try {
			incomingMessage = (CVMasterMessage) objectInput.readObject();
		} catch (Exception ex) {
			System.err.println("failed to get incoming message");
			System.err.println(ex.getMessage());
			if (objectInput != null) {
				try {
					objectInput.close();
				} catch (Exception nestedEx) {
					System.err.println("failed to close object input stream");
					System.err.println(nestedEx.getMessage());
				}
			}
			return null;
		}
		return incomingMessage;
	}
	
	public CVMasterMessage sendReceiveRound(Socket socket) {
		if (!send(socket)) {
			return null;
		}
		CVMasterMessage reply = CVMasterMessage.receive(socket);
		if (reply == null) {
			return null;
		}

		/*
		 * check flag and status, if not correct, discard the reply and return
		 * null
		 */
		if (reply.getFlag() != flag) {
			System.err.println("invalid reply flag");
			return null;
		}
		if (!reply.getStatus()) {
			System.err.println((String) reply.getPayload());
			return null;
		}
		return reply;
	}
}
