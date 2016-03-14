package edu.escom.servidor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;


public class Servidor extends SwingWorker<Void, Long> {

    private ServerSocket serverSocket;
    private DataInputStream dis;
    private FileOutputStream fos;
    private Socket socketCliente;

    private JProgressBar progreso;
    private JTextArea textArea;

    //constructor de la clase
    public Servidor(JProgressBar progreso, JTextArea textArea) {
        this.progreso = progreso;
        this.textArea = textArea;
    }

  
   @Override
    protected void process(List<Long> chunks) {
        progreso.setValue(chunks.get(0).intValue());
        progreso.setString(chunks.get(0) + "%");
    }

    @Override
    protected Void doInBackground() throws Exception {

        byte[] buf = new byte[1024]; // Buffer para transferencia de datos
        long porcentaje = 0; // Porcentaje del estado de transferencia
        long porcentajeOld = 0;
        int n = 0; // Bytes transferidos
        long leidos; // Bytes leidos
        long lenght; // Tamaño que se ira reduciendo dependiendo de los bytes que se hayan transferido
        long lenghInt; // Tamaño en bytes original del archivo
        File file; // Archivo donde se guardara lo recibido

        serverSocket = new ServerSocket(4000);
        print("El servidor ha iniciado en el puerto 4000 \n");
        print("En Espera de Archivos \n");

        while (true) {
            socketCliente = serverSocket.accept();
            print("Cliente conectado desde: " + socketCliente.getInetAddress() + ":" + socketCliente.getPort() + "\n");

            dis = new DataInputStream(new BufferedInputStream(socketCliente.getInputStream()));

            //Número de archivos que enviará el cliente
            int archivosNo = dis.readInt();
            print("El cliente enviará " + archivosNo + " archivos \n");

            // Loop para cada archivo
            for (int i = 0; i < archivosNo; i++) {
                //Reiniciar valores para cada archivo
                leidos = 0;
                porcentaje = 0;
                //Cabecera del archivo
                file = new File(dis.readUTF());
                print("Recibiendo el archivo: " + file.getName());

                lenght = dis.readLong();
                lenghInt = lenght;
                print("Tamaño del archivo: " + lenght + " bytes \n");

                //Ruta donde se guardaran los archivos
                fos = new FileOutputStream("C:\\" + file.getName());

                //Leer el archivo
                while (lenght > 0 && (n = dis.read(buf, 0, (int) Math.min(buf.length, lenght))) != -1) {
                    fos.write(buf, 0, n);
                    leidos += n;
                    porcentajeOld = porcentaje;
                    porcentaje = (leidos * 100) / lenghInt;
                    if (porcentaje != porcentajeOld ) {
                        System.out.println("Buffer: " + n + " Porcentaje: " + porcentaje);
                        publish(porcentaje);
                    }
                    lenght -= n;
                }
                fos.close();
                Thread.sleep(500);
            } //END loop archivos
            publish(Long.parseLong("100"));
            print("\nLos Archivos Se Recibieron Correctamente");
        }
    }

    private void print(String text) {
        textArea.setText(textArea.getText() + "\n" + text);
    }

}
