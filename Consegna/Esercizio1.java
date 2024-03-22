/**
 * Nome: Niccolò
 * Cognome: Bellucci
 * Matricola: 998755
 * Email: niccolo.bellucci2@studio.unibo.it
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Esercizio1 {

    private static ArrayList<Library>[] libraries = new ArrayList[26];
    private static HashMap<Integer, String> ciuToPr = new HashMap<>();
    private static int A_ASCII = 97;
    public static void main(String[] args) {
        try {
            parseLibraries("InputEs1.txt");
            parseCiuToPr(args);
        } catch (IOException e){
            System.out.println("Errore nei file");
            System.exit(1);
        }
        Scanner scanner = new Scanner(System.in);
        int menuChoice;

        do {
            System.out.println("1. Ricerca per nome");
            System.out.println("2. Stampare tutti gli iscritti");
            System.out.println("3. Stampare gli iscritti della stessa città");
            System.out.println("4. Cancellazione di un iscritto");
            System.out.println("5. Inserimento di un nuovo iscritto");
            System.out.println("6. Verifica una libreria tramite CIU");
            System.out.println("7. Stampare PR tramite CIU");
            System.out.println("0. Esci");

            System.out.print("Inserisci la tua scelta: ");
            menuChoice = scanner.nextInt();
            System.out.println();
            switch (menuChoice) {
                case 1:
                    System.out.println("Scrivi il nome della libreria da cercare: ");
                    scanner.nextLine();
                    String nameToSearch = scanner.nextLine();
                    System.out.println("\n-------------Output-------------");
                    System.out.println(searchByAName(nameToSearch));
                    System.out.println("--------------------------------\n");
                    break;
                case 2:
                    System.out.println("\n-------------Output-------------");
                    printAll();
                    System.out.println("--------------------------------\n");
                    break;
                case 3:
                    System.out.println("Scrivi la città della libreria da cercare: ");
                    scanner.nextLine();
                    String cityToSearch = scanner.nextLine();
                    System.out.println("\n-------------Output-------------");
                    printByCity(cityToSearch);
                    System.out.println("--------------------------------\n");
                    break;
                case 4:
                    System.out.println("Scrivi il nome della libreria da rimuovere: ");
                    scanner.nextLine();
                    String libraryToRemove = scanner.nextLine();
                    System.out.println("\n-------------Output-------------");
                    if (removeLibrary(libraryToRemove))
                        System.out.println("Libreria rimossa con successo!!!");
                    else
                        System.out.println("Errore rimozione libreria");
                    System.out.println("--------------------------------\n");
                    break;
                case 5:
                    Library libraryToInsert = null;
                    System.out.println("Compila i dati della libreria da inserire: ");
                    scanner.nextLine();
                    System.out.print("Nome: ");
                    String name = scanner.nextLine();
                    System.out.print("Città: ");
                    String city = scanner.nextLine();
                    System.out.print("Indirizzo (facoltativo): ");
                    String address = scanner.nextLine();
                    System.out.print("Anno: ");
                    int year = scanner.nextInt();
                    System.out.print("Link: ");
                    scanner.nextLine();
                    String link = scanner.nextLine();
                    System.out.print("Codice: ");
                    int code = scanner.nextInt();
                    System.out.println("\n-------------Output-------------");
                    libraryToInsert = new Library(name, city, address, year, link, code);
                    if (insertLibrary(libraryToInsert)) {
                        System.out.println("Libreria inserita con successo");
                        System.out.println(libraryToInsert);
                    } else
                        System.out.println("Errore inserimento libreria");
                    System.out.println("--------------------------------\n");

                    break;
                case 6:
                    System.out.println("Scrivi il CIU per controllare la presenza della libreria.");
                    scanner.nextLine();
                    System.out.print("CIU: ");
                    int ciu = scanner.nextInt();
                    System.out.println("\n-------------Output-------------");
                    if (checkCIU(ciu)) {
                        System.out.println("Libreria presente");
                    } else {
                        System.out.println("Libreria non presente");
                    }
                    System.out.println("--------------------------------\n");
                    break;
                case 7:
                    System.out.println("Scrivi il CIU per stampare il PR.");
                    scanner.nextLine();
                    System.out.print("CIU: ");
                    ciu = scanner.nextInt();
                    System.out.println("\n-------------Output-------------");
                    if (checkCIU(ciu)) {
                        System.out.println("Codice PR: " + ciuToPr.get(ciu));
                    } else {
                        System.out.println("PR non presente");
                    }
                    System.out.println("--------------------------------\n");
                    break;
                case 0:
                    System.out.println("Programma terminato.");
                    break;
                default:
                    System.out.println("Scelta non valida. Riprova.");
            }
        } while (menuChoice != 0);

//        System.out.println(Arrays.deepToString(libraries));
    }

    public static void parseLibraries(String inputfile) throws IOException {

        for (int i = 0; i < 26; i++) {
            libraries[i] = new ArrayList<Library>();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(inputfile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(" ");
                String name = data[0];
                String city = data[1];
                int year = Integer.parseInt(data[2]);
                String link = data[3];
                int code = Integer.parseInt(data[4]);

                int position = (int)name.toLowerCase().charAt(0) - A_ASCII;
                if (!isLibraryAlreadyPresent(libraries[position], name)) {
                    libraries[position].add(new Library(name, city, "", year, link, code));
                }

            }
        }
        for (int i = 0; i < 26; i++) {
            libraries[i].sort(Comparator.comparing(Library::getName));
        }
    }

    private static boolean isLibraryAlreadyPresent(ArrayList<Library> libraryList, String name) {
        for (Library library : libraryList) {
            if (library.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * La complessità di questo metodo è O(n), dove n è il numero di librerie nella corrispondente ArrayList.
     * Il metodo esegue l'iterazione attraverso l'ArrayList per trovare una libreria con un nome corrispondente.
     * @param nameToSearch
     * @return
     */
    private static Library searchByAName(String nameToSearch) {
        int index = getIndex(nameToSearch);

        for (int i = 0; i < libraries[index].size(); i++) {
            Library library = libraries[index].get(i);
            if (library.getName().equalsIgnoreCase(nameToSearch)) {
//                System.out.println(libraries[index].get(i));
                return libraries[index].get(i);
            }
        }
//        System.out.println("Libreria non presente");
        return null;
    }

    private static Library searchByCode(int code) {
        for (ArrayList<Library> libraryArrayList : libraries) {
            for (Library library : libraryArrayList) {
                if (library.getCode() == code)
                    return library;
            }
        }
        return null;
    }

    /**
     * La complessità di qeusto metodo è O(n), dove n è il numero totale delle librerie.
     * Il metodo scorre attraverso tutto l' ArrayList stampando ogni libreria.
     */
    private static void printAll() {
        for (int index = 0; index < 26; index++) {
            for (int i = 0; i < libraries[index].size(); i++) {
                System.out.println(libraries[index].get(i));
            }
        }
    }

    /**
     * La complessità di questo metodo è O(26 * n), dove 26 è il numero di liste di librerie e n il numero di librerie totali.
     * Essendo 26 una costante possiamo definire la complessità come O(n).
     * @param cityToSearch
     */
    private static void printByCity(String cityToSearch) {
        boolean found = false;
        for (int index = 0; index < 26; index++) {
            int size = libraries[index].size();
            for (int i = 0; i < size; i++) {
                Library library = libraries[index].get(i);
                if (library.getCity().equalsIgnoreCase(cityToSearch.toLowerCase())) {
                    System.out.println(libraries[index].get(i));
                    found = true;
                }
            }
        }
        if (!found)
            System.out.println("Non ci sono librerie in questa città");
    }

    /**
     * La complessità di questo metodo è O(n), dove n è il numero di librerie totali.
     * @param nameLibraryToRemove
     * @return
     */
    private static boolean removeLibrary(String nameLibraryToRemove) {
        ArrayList<Library> librariesList = libraries[nameLibraryToRemove.toLowerCase().charAt(0) - A_ASCII];
        return librariesList.remove(searchByAName(nameLibraryToRemove));
    }

    /**
     * La complessità di questo metodo è O(n * log(n)), dove n è il numero di librerie in un ArrayList,
     * questa complessità è dovuta alla chiamata della funzione sort() = O(n * log(n)).
     * @param libraryToInsert
     * @return
     */
    private static boolean insertLibrary(Library libraryToInsert) {
        int index = getIndex(libraryToInsert);
        if (libraries[index].add(libraryToInsert)) {
            libraries[index].sort(Comparator.comparing(Library::getName));
            return true;
        }
        return false;

    }

    private static int getIndex(String name) {
        return (int)name.toLowerCase().charAt(0) - A_ASCII;
    }

    private static int getIndex(Library library) {
        return (int)library.getName().toLowerCase().charAt(0) - A_ASCII;
    }

    /**
     * La complessità di questo metodo è O(k), dove k è il numero di coppie CIU-PR nel file di input.
     * @param args
     * @throws IOException
     */
    private static void parseCiuToPr(String[] args) throws IOException{
        if (args.length != 1) {
            System.out.println("Usage: java Esercizio1 <CiuPr_file>");
            System.exit(1);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(" ");
                int ciu = Integer.parseInt(data[0]);
                String pr = data[1];
                ciuToPr.put(ciu, pr);
            }
        }
    }

    /**
     * La complessità di questo metodo è O(m * n), dove m è il numero di librerie totali e n è il numero di librerie nella corrispondente ArrayList.
     * Questa complessità è ereditata dal metodo chiamato searchByCode(code).
     * @param code
     * @return
     */
    private static boolean checkCIU(int code) {
        return searchByCode(code) != null;
    }
}

class Library {
    private String name;
    private String city;
    private String address;
    private int year;
    private String link;
    private int code;

    public Library(String name, String city, String address,int year, String link, int code) {
        this.name = name;
        this.city = city;
        this.address = address;
        this.year = year;
        this.link = link;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return this.name + " " + this.city + " " + this.year + " " + link + " " + this.code;
    }

}