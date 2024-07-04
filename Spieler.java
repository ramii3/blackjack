import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class Spieler {
    private int kapital;
    private int[] hand;
    private int einsatz;

    public Spieler(int startkapital) {
        this.kapital = startkapital;
        this.hand = new int[0];
        this.einsatz = 0;
    }

    public void sendeNachricht(String empfaengerHost, int empfaengerPort, String nachricht) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(empfaengerHost);
        byte[] sendData = nachricht.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, empfaengerPort);
        socket.send(sendPacket);
        socket.close();
    }

    public String empfangeNachricht(int port) throws Exception {
        DatagramSocket socket = new DatagramSocket(port);
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        socket.close();
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }

    public int berechneEinsatz() {
        // Beispiel: Einsatz auf Basis einer einfachen Strategie
        this.einsatz = Math.min(this.kapital, 100);  // Setze maximal 100 oder das verfügbare Kapital
        return this.einsatz;
    }

    public void spiele() throws Exception {
        // Initiale Nachrichten an Croupier und Kartenzähler senden
        sendeNachricht("localhost", 23456, "{\"aktion\":\"starte_spiel\"}");
        sendeNachricht("localhost", 34567, "{\"aktion\":\"frage_empfehlung\"}");

        while (true) {
            String nachricht = empfangeNachricht(12345);
            if (nachricht.contains("\"aktion\":\"empfehlung\"")) {
                // Empfehlung des Kartenzählers erhalten und verarbeiten
                System.out.println("Empfehlung erhalten: " + nachricht);
            } else if (nachricht.contains("\"aktion\":\"karten_verteilen\"")) {
                // Karten vom Croupier erhalten und verarbeiten
                System.out.println("Karten erhalten: " + nachricht);
            } else if (nachricht.contains("\"aktion\":\"spiel_ende\"")) {
                // Spielende und Ergebnis verarbeiten
                System.out.println("Spielende: " + nachricht);
                break;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Spieler spieler = new Spieler(1000);
        spieler.spiele();
    }
}
