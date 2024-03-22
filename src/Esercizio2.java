/**
 * Nome: Niccolò
 * Cognome: Bellucci
 * Matricola: 998755
 * Email: niccolo.bellucci2@studio.unibo.it
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Esercizio2 {
    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.out.println("Usage: java Esercizio2 <input_file>");
                System.exit(1);
            }

            List<Double> weights = new ArrayList<>();
            double maxWeight = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
                maxWeight = Double.parseDouble(br.readLine());
                int n = Integer.parseInt(br.readLine());

                for (int i = 0; i < n; i++) {
                    weights.add(Double.parseDouble(br.readLine()));
                }
            }

            // La complessità dell' algoritmo di assegnazione degli oggetti è O(m * n) dove m è il numero di container e n il numero di contenitori
            // Questa perché ogni peso potrebbe richiedere una scelta attraverso tutti i container esistenti.
            List<Container> containers = new ArrayList<>();
            containers.add(new Container(maxWeight, 0));

            for (double weight : weights) {
                boolean isPlaced = false;

                for (Container container : containers) {
                    if (container.currentWeight + weight <= container.maxWeight) {
                        container.currentWeight += weight;
                        container.objectIds.add(weights.indexOf(weight));
                        isPlaced = true;
                        break;
                    }
                }

                if (!isPlaced) {
                    Container tmp = new Container(maxWeight, weight);
                    tmp.objectIds.add(weights.indexOf(weight));
                    containers.add(tmp);
                }
            }

            System.out.println(containers.size());

            // La stampa dei gli oggetti in ogni container impiega O(m * n), dove m il numero dei container e n il numero degli oggetti.

            for (Container container : containers) {
                System.out.print(container.currentWeight + ", ");
                for (int i = 0; i < container.objectIds.size(); i++) {
                    if (i < container.objectIds.size() - 1)
                        System.out.print(container.objectIds.get(i) + ", ");
                    else
                        System.out.print(container.objectIds.get(i));
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.out.println("Errore nella lettura del file.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

}

class Container {
    double maxWeight;
    double currentWeight;
    List<Integer> objectIds;

    Container(double maxWeight, double currentWeight) {
        this.maxWeight = maxWeight;
        this.currentWeight = currentWeight;
        this.objectIds = new ArrayList<>();
    }
}