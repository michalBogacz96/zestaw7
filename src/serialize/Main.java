package serialize;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, ClassCastException, InterruptedException, ClassNotFoundException {

        Circular<String> queue = new Circular<>(3);


        queue.put("Piotr");
        queue.put("Adam");
        queue.put("Kuba");
        queue.poll();
        queue.poll();
        queue.put("Wlodek");

        //serializacja
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("serialized_data.ser"));
        queue.writeObject(outputStream);
        outputStream = new ObjectOutputStream(new FileOutputStream(("serialized_data.ser")));
        queue.put("Szymon");

        System.out.println("\nPrzed serializacja: \n");
        for(String s: queue)
            System.out.println(s);

        queue.writeObject(outputStream);
        outputStream.close();


        //deserializacja
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("serialized_data.ser"));
        queue.readObject(objectInputStream);
        objectInputStream.close();

        System.out.println("\n Po deserializacji\n");

        for(String s: queue)
            System.out.println(s);

        System.out.println(queue.toString());
    }

}
