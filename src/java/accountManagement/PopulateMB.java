/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accountManagement;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.xml.ws.WebServiceRef;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import wx.accMngmtWS.AdminAccMngmtWS_Service;
import wx.custAccMngmtWS.CustAccMngmtWS_Service;

/**
 *
 * @author ¿.¿.¿
 */
@ManagedBean (name = "popMB")
@ViewScoped
public class PopulateMB {
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/CustAccMngmtWS.wsdl")
    private CustAccMngmtWS_Service service_1;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/AdminAccMngmtWS.wsdl")
    private AdminAccMngmtWS_Service service;

    //for encryption
    private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,};
    
    /**
     * Creates a new instance of PopulateMB
     */
    public PopulateMB() {
    }
    
    public void populate() throws GeneralSecurityException, UnsupportedEncodingException {
        //Populate Admin
        wx.accMngmtWS.AdminAccMngmtWS port = service.getAdminAccMngmtWSPort();
        wx.accMngmtWS.AdminUsr admin1 = new wx.accMngmtWS.AdminUsr();
        admin1.setEmail("joequak@mail.com");
        admin1.setFirstName("Joe");
        admin1.setLastName("Quak");
        admin1.setPassword(encrypt("password"));
        if (port.createAdmin(admin1)) {
            System.out.println("Populate Admin 1 success");
        }
        wx.accMngmtWS.AdminUsr admin2 = new wx.accMngmtWS.AdminUsr();
        admin2.setEmail("joycetan@mail.com");
        admin2.setFirstName("Joyce");
        admin2.setLastName("Tan");
        admin2.setPassword(encrypt("P@ssword"));
        if (port.createAdmin(admin2)) {
            System.out.println("Populate Admin 2 success");
        }
        
        //Populate Customer
        wx.custAccMngmtWS.CustAccMngmtWS port_1 = service_1.getCustAccMngmtWSPort();
        wx.custAccMngmtWS.Customer cust1 = new wx.custAccMngmtWS.Customer();
        cust1.setEmail("joe.zaxx89@gmail.com");
        cust1.setFistName("Joe");
        cust1.setLastName("ZaXx");
        cust1.setPassword(encrypt("cust1"));
        if (port_1.registerAsMember(cust1)) {
            System.out.println("Populate Cust 1 Registered");
        }
        if (port_1.activateAccount(cust1.getEmail())) {
            System.out.println("Populate Cust 1 Activated");
        }
        wx.custAccMngmtWS.Customer cust2 = new wx.custAccMngmtWS.Customer();
        cust2.setEmail("joeboiboi@gmail.com");
        cust2.setFistName("Boi");
        cust2.setLastName("Boi");
        cust2.setPassword(encrypt("cust2"));
        if (port_1.registerAsMember(cust2)) {
            System.out.println("Populate Cust 2 Registered");
        }
        if (port_1.activateAccount(cust2.getEmail())) {
            System.out.println("Populate Cust 2 Activated");
        }
    }
    
    private static String encrypt(String property) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }

    private static String base64Encode(byte[] bytes) {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Encoder().encode(bytes);
    }

    private static String decrypt(String property) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    private static byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal, and you probably should use another impl
        return new BASE64Decoder().decodeBuffer(property);
    }
    
    
}
