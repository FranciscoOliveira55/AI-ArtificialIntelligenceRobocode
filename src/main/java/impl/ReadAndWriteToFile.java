package impl;

import maps.Maps;
import robocode.AdvancedRobot;
import robocode.RobocodeFileOutputStream;

import java.io.*;

public class ReadAndWriteToFile extends AdvancedRobot {

    public static void main(String args[]) throws InterruptedException, Exception {



    }


    public static byte[] toByteArray(Object obj) throws IOException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Object fromByteArray(byte[] byteArray) throws IOException, ClassNotFoundException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
