package com.meal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BeanUtil {

    /**
     * Clone Object
     * @param obj
     * @return
     * @throws Exception
     */
    public static Object cloneObject(Object obj) throws Exception{
    	
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); 
        
        ObjectOutputStream out = new ObjectOutputStream(byteOut); 
        
        out.writeObject(obj); 
        
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray()); 
        
        ObjectInputStream in =new ObjectInputStream(byteIn);
        
        return in.readObject();
    }
	
}
