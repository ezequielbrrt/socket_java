package edu.escom.cliente;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class Cliente extends SwingWorker<Long, Void> { //se extiende de SwingWorker para la barra de porcentaje

    private Socket socket;
    private DataOutputStream dos;
    private List<File> files;
    private JTextArea textArea;

    private JProgressBar progreso;
    private JButton jButton1, jButton2;
    private JLabel estado;

    //constructor de la clase
    public Cliente(JTextArea textArea, JButton jButton1, JButton jButton2, JLabel estado,JProgressBar progreso) {
        files = new ArrayList<>();
        this.textArea = textArea;
        this.jButton1 = jButton1;
        this.jButton2 = jButton2;
        this.estado = estado;
        this.progreso = progreso;
    }

    //método para conectar el socket del cliente, creando un socket
    //y el flujo de este, imprimiendo
    public void conectar() throws IOException {
        socket = new Socket(InetAddress.getByName("127.0.0.1"), 4000);
        dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        print("Se establecio la conexión: " + socket.getInetAddress() + "\n");
    }

    @Override
    protected Long doInBackground() throws Exception {
        blockButtons();
        //Tiempo de inicio
        Calendar start = Calendar.getInstance();
        //Número de archivos
        dos.writeInt(files.size());
        dos.flush();
        //Enviar los archivos
        for (File file : files) {
            print("Enviando archivos: " + file.getName());
            //progreso.setValue(25);
            //progreso.setStringPainted(true);
            print ("Tamaño del archivo: "+file.length()+" bytes");
            //progreso.setValue(70);
            enviar(file);
            print("El archivo "+file.getName() + "  Se ha enviado correctamente. \n");
            //progreso.setValue(100);
        }
        //Limpiar canal
        dos.flush();
        //Tiempo final
        Calendar fin = Calendar.getInstance();
        print("\nTiempo del envío: " + (fin.getTimeInMillis() - start.getTimeInMillis()) / 1000 + " segundos");
        unlockButtons();
        estado.setText("Transferencia completa");
        estado.setForeground(Color.GREEN);
        return (fin.getTimeInMillis() - start.getTimeInMillis()) / (60 * 1000);
    }
    
    public void process(List<Void> chunks) {
        //progreso.setValue(chunks.get(0));
        progreso.setString(chunks.get(0) + "%");
    }
    
    private void enviar(File file) throws IOException {

        //Variables locales
        byte[] buf = new byte[1024]; // Buffer de escritura
        FileInputStream fis; // Stream de salida
        int n = 0; // Cuenta los bits que se han mandado
           long porcentaje = 0; // Porcentaje del estado de transferencia
        long porcentajeOld = 0;
        long leidos; // Bytes leidos
        long lenght; // Tamaño que se ira reduciendo dependiendo de los bytes que se hayan transferido
        long lenghInt; // Tamaño en bytes original del archivo

        dos.writeUTF(file.getName());
        dos.writeLong(file.length());

        fis = new FileInputStream(file);
        leidos = 0;
        lenght = file.length();
                lenghInt = lenght;
    progreso.setStringPainted(true);
        //Escribir al servidor
        while ((n = fis.read(buf)) != -1) {
            dos.write(buf, 0, n);
            leidos += n;
                    porcentajeOld = porcentaje;
                    porcentaje = (leidos * 100) / lenghInt;
                    
                    if (porcentaje != porcentajeOld ) {

                        progreso.setValue(n);
                    }
         
          
            dos.flush();

        }
                      lenght -= n;
    }

    public void cerrarConexion() {
        try {
            socket.close();
            print("Conexión terminada con el servidor");
        } catch (IOException ex) {
            System.err.println("Error: " + ex);
        }
    }

    public void setFiles(File[] selectedFiles) {
        files.addAll(Arrays.asList(selectedFiles));
    }

    private void print(String text) {
        textArea.setText(textArea.getText() + "\n" + text);
    }

    private void blockButtons() {
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
    }

    private void unlockButtons() {
        jButton1.setEnabled(true);
        jButton2.setEnabled(true);
    }

}
