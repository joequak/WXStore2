/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package accountManagement;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.xml.ws.WebServiceRef;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import wx.accMngmtWS.AdminAccMngmtWS_Service;
import wx.custAccMngmtWS.CustAccMngmtWS_Service;

/**
 *
 * @author ¿.¿.¿
 */
@ManagedBean (name = "commonInfraMB")
@SessionScoped
public class CommonInfraMB implements Serializable{
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/CustAccMngmtWS.wsdl")
    private CustAccMngmtWS_Service service_1;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/AdminAccMngmtWS.wsdl")
    private AdminAccMngmtWS_Service service;
    
    //For encryption
    private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,};
    
    //Check login Flag variables and ID input for Webservice
    private long logInAdmin;
    private long logInCust;
    
    //Input Varaibles to Web Services
    //Login
    private String emailAdd;
    private String password;    

    /**
     * Creates a new instance of CommonInfraMB
     */
    public CommonInfraMB() {
        this.logInAdmin = -1;
        this.logInCust = -1;
        
    }
    
    //WebService methods
    //Admin methods
    public void adminLogIn(ActionEvent actionEvent) throws IOException, GeneralSecurityException {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        wx.accMngmtWS.AdminAccMngmtWS port = service.getAdminAccMngmtWSPort();
        this.setLogInAdmin(port.logInAdmin(this.getEmailAdd(), encrypt(this.getPassword())));
        if (this.getLogInAdmin() == -1) {
            errorMsg("Login Failed");
        } else {
            infoMsg("Login Success");
            FacesContext.getCurrentInstance().getExternalContext().redirect("../AdminPortal/AdminHomePage.xhtml");
        }
    }

    //Customer methods
    public void memberLogIn(ActionEvent actionEvent) throws IOException, GeneralSecurityException {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        wx.custAccMngmtWS.CustAccMngmtWS port = service_1.getCustAccMngmtWSPort();
        this.setLogInCust(port.logInMember(this.getEmailAdd(), encrypt(this.getPassword())));
        if (this.getLogInCust() == -1) {
            errorMsg("Login Failed");
        } else {
            infoMsg("Login Success");
            FacesContext.getCurrentInstance().getExternalContext().redirect("../WineXpressStore/homePage.xhtml");
        }
    }

    //Non WS methods exposed and used in web pages
    public void checkAdminLogin() throws IOException {
        if (this.getLogInAdmin() == -1) {
            errorMsg("Please login before proceeding.");
            FacesContext.getCurrentInstance().getExternalContext().redirect("../errorAdmin.xhtml");
        }
    }
    
    public void checkCustLogin() throws IOException {
        if (this.getLogInCust() == -1) {
            errorMsg("Please login before proceeding.");
            FacesContext.getCurrentInstance().getExternalContext().redirect("../error.xhtml");
        }
        
    }
    
    //Logout Method
    public void adminLogout(ActionEvent actionEvent) throws IOException, Exception {
        System.out.println("CommonInfraMB: LOGOUT");
        long reset = -1;
        this.setLogInAdmin(reset);
        this.setLogInCust(reset);
        infoMsg("Logout Successful.");
        FacesContext.getCurrentInstance().getExternalContext().redirect("../AdminPortal/AdminLogIn.xhtml");
    }
    
    //Logout Method
    public void custLogout(ActionEvent actionEvent) throws IOException, Exception {
        System.out.println("CommonInfraMB: LOGOUT");
        this.setLogInAdmin(-1);
        this.setLogInCust(-1);
        infoMsg("Logout Successful.");
        FacesContext.getCurrentInstance().getExternalContext().redirect("../WineXpressStore/homePage.xhtml");
    }
    
    public void errorMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
    }

    public void infoMsg(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, "");
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, msg);
        context.getExternalContext().getFlash().setKeepMessages(true);
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

    /**
     * @return the logInAdmin
     */
    public long getLogInAdmin() {
        return logInAdmin;
    }

    /**
     * @param logInAdmin the logInAdmin to set
     */
    public void setLogInAdmin(long logInAdmin) {
        this.logInAdmin = logInAdmin;
    }

    /**
     * @return the logInCust
     */
    public long getLogInCust() {
        return logInCust;
    }

    /**
     * @param logInCust the logInCust to set
     */
    public void setLogInCust(long logInCust) {
        this.logInCust = logInCust;
    }

    /**
     * @return the emailAdd
     */
    public String getEmailAdd() {
        return emailAdd;
    }

    /**
     * @param emailAdd the emailAdd to set
     */
    public void setEmailAdd(String emailAdd) {
        this.emailAdd = emailAdd;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
