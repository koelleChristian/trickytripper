package de.koelle.christian.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectUtils {

    /**
     * Generates a deep clone of the object.
     * 
     * @param <T>
     *            type of the object to be cloned
     * 
     * @param objectToBeCloned
     *            The object to be cloned.
     * 
     * @return the object The cloned object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T cloneDeep(T objectToBeCloned) {

        Object obj = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectInputStream in = null;

        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(objectToBeCloned);
            out.flush();

            in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Object cannot be cloned.", e);
        } finally {
            try {
                bos.close();
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException e) {
                throw new RuntimeException("Object streams cannot be closed.", e);
            }
        }
        return (T) obj;
    }

}
