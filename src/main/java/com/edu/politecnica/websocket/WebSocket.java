package com.edu.politecnica.websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket")
public class WebSocket {

	private static final Set<Session> sessoes = Collections.synchronizedSet(new HashSet<Session>());
	private static final Serial serial = new Serial();

	@OnOpen
	public void sessaoAberta(Session sessao) {
		System.out.println("sessao aberta");
		sessoes.add(sessao);
	}

	@OnClose
	public void sessaoFechada(Session sessao) {
		System.out.println("sessao fechada");
		sessoes.remove(sessao);
		serial.fecharPortaSerial();
		

	}

	@OnMessage
	public void abrirSerial(String mensagem) {
		
		int index = mensagem.indexOf(":");
		String key = mensagem.substring(0, index + 1);
		String value = mensagem.substring(index + 1, mensagem.length());

		if (key.toLowerCase().compareTo("o:") == 0) {
			serial.abrirPortaSerial(value);
			
		}else if (key.toLowerCase().compareTo("fs:") == 0){
			serial.fecharPortaSerial();
			enviarMensagemClientes("s:Desconectado");
		}
	}

	public static void enviarMensagemClientes(String mensagem) {
		for (Session session : sessoes) {
			try {
				session.getBasicRemote().sendText(mensagem);
			} catch (Exception e) {
			
				
			}
		}
	}
}
