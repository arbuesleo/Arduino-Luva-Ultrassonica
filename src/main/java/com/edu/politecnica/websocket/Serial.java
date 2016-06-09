package com.edu.politecnica.websocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class Serial implements SerialPortEventListener {
	private BufferedReader input;

	@Override
	public void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				WebSocket.enviarMensagemClientes(this.input.readLine());
			} catch (Exception e) {
				System.err.println("Erro ao ler dados do arduino. "
						+ e.getMessage());
				WebSocket.enviarMensagemClientes("s:Desconectado");
			}
		}
	}

	private static final int RATE = 9600;
	
	private SerialPort serialPort;

	// private static final String porta = "COM9";
	
	public void fecharPortaSerial() {
		if (serialPort != null) {
			serialPort.close();
			serialPort = null;
			System.out.println("Portal Serial Fechada.");
		}
	}

	public void abrirPortaSerial(String porta) {
		if (serialPort != null) {
			fecharPortaSerial();
			System.out.println("Fechando portal serial.");
			
		}
		System.out.println("Abrindo portal serial.");
		
		try {
			WebSocket.enviarMensagemClientes("s:Conectando...");
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(porta);
			serialPort = (SerialPort) portId.open(this.getClass().getName(), 2000);
			serialPort.setSerialPortParams(RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.enableReceiveTimeout(1000);
			serialPort.enableReceiveThreshold(0);
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			System.out.println("Porta " + porta + " aberta");
			WebSocket.enviarMensagemClientes("s:Conectado");
		}catch(Exception e){
			System.out.println("Erro ao abrir: " + porta);
			WebSocket.enviarMensagemClientes("s:Erro ao Abrir Porta "+porta+" " + e + e.getMessage());
		}
	}

	private OutputStream output;

	public void enviarMensagemArduino(String message) {
		try {
			this.output.write(message.getBytes());
		} catch (Exception e) {
			System.err.println("Erro ao enviar mensagem para " + "o Arduino. "
					+ e.getMessage());
		}
	}

}
