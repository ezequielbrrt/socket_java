package edu.escom.cliente.gui;

import javax.swing.JFileChooser;

public class Seleccion {
	

    public static void main(String[] args) {
        //se crea el objeto JfileChooser para buscar los 
        //archivos a enviar
        JFileChooser fileChooser = new JFileChooser();
        //se activa la seleccion multiple de archivos
        fileChooser.setMultiSelectionEnabled(true);
        
        //se muestra la ventana de dialogo
        int resultado = fileChooser.showOpenDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
        	//se crea el objeto de ClienteWindow y se le envia los archivos seleccionados
            new ClienteWindow(fileChooser.getSelectedFiles()).setVisible(true);
        }
    }

}
