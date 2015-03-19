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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.xml.ws.WebServiceRef;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import ws.CustSupportWS_Service;
import wx.accMngmtWS.AdminAccMngmtWS_Service;
import wx.accMngmtWS.AdminUsr;
import wx.custAccMngmtWS.CustAccMngmtWS_Service;
import wx.custAccMngmtWS.Customer;

/**
 *
 * @author ¿.¿.¿
 */
@ManagedBean (name = "accRegistMB")
@ViewScoped
public class AccRegistMB implements Serializable{
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/CustSupportWS.wsdl")
    private CustSupportWS_Service service_2;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/CustAccMngmtWS.wsdl")
    private CustAccMngmtWS_Service service_1;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8080/WineXpressWebService-war/AdminAccMngmtWS.wsdl")
    private AdminAccMngmtWS_Service service;
    
    //for encryption
    private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
    private static final byte[] SALT = {
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
        (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,};
    
    //Input Varaibles to Web Services;
    //Create Admin
    private AdminUsr toCreate;
    private String toCreatePassword;
    //Register Member
    private Customer toRegist;
    private String toRegistPassword;
    private String emailToActivate;

    /**
     * Creates a new instance of AccRegistMB
     */
    public AccRegistMB() {
        toRegist = new Customer();
        toRegistPassword = "";
        emailToActivate = "";
        toCreate = new AdminUsr();
        toCreatePassword = "";
    }

    //Register Member Methods
    public void activateAccount() throws IOException {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        wx.custAccMngmtWS.CustAccMngmtWS port = service_1.getCustAccMngmtWSPort();
        this.setEmailToActivate(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("email"));
        System.out.println("email to activate is " + this.getEmailToActivate());
        if (port.activateAccount(this.getEmailToActivate())) {
            infoMsg("Member Activated");
            FacesContext.getCurrentInstance().getExternalContext().redirect("../WineXpressStore/VisitorActivateAccountSuccess.xhtml");
        } else {
            errorMsg("Member Activation Fail");
            FacesContext.getCurrentInstance().getExternalContext().redirect("../WineXpressStore/VisitorActivateAccountFail.xhtml");
        }
    }

    public void registerAsMember (ActionEvent actionEvent) throws IOException, GeneralSecurityException {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        wx.custAccMngmtWS.CustAccMngmtWS port = service_1.getCustAccMngmtWSPort();
        this.getToRegist().setPassword(encrypt(this.getToRegistPassword()));
        if (port.registerAsMember(this.getToRegist())) {
            ws.CustSupportWS port_2 = service_2.getCustSupportWSPort();
            String emailSubject = "WineXpress Member Registration";
            String emailContent = "Hi " + this.getToRegist().getFistName() + ",\n\n Welcome to WineXpress.com. \n\n Please click the following link to activate your account."
                    + "\n\n http://localhost:8080/WXStore/faces/WineXpressStore/VisitorActivateAccount.xhtml?email=" + this.getToRegist().getEmail() + "\n\nThank you for joining us."
                    + "\n\n From. \n WineXpress CX Team.";
            port_2.sendEmail(this.getToRegist().getEmail(), emailSubject, emailContent);
            infoMsg("Member Registered.");
            FacesContext.getCurrentInstance().getExternalContext().redirect("../WineXpressStore/VisitorRegisterSuccess.xhtml");
        } else {
            errorMsg("Member Registration Fail. Email address has been taken.");
        }
    }
    
    //Create Admin Account Methods
    public void createAdmin(ActionEvent actionEvent) throws IOException, GeneralSecurityException {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        wx.accMngmtWS.AdminAccMngmtWS port = service.getAdminAccMngmtWSPort();
        this.getToCreate().setPassword(encrypt(this.getToCreatePassword()));
        if (port.createAdmin(this.getToCreate())) {
            infoMsg("Admin Account Created");
            FacesContext.getCurrentInstance().getExternalContext().redirect("../AdminPortal/AdminHomePage.xhtml");
        } else {
            errorMsg("Admin Account Creation Fail");
        }
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
     * @return the toRegist
     */
    public Customer getToRegist() {
        return toRegist;
    }

    /**
     * @param toRegist the toRegist to set
     */
    public void setToRegist(Customer toRegist) {
        this.toRegist = toRegist;
    }

    /**
     * @return the emailToActivate
     */
    public String getEmailToActivate() {
        return emailToActivate;
    }

    /**
     * @param emailToActivate the emailToActivate to set
     */
    public void setEmailToActivate(String emailToActivate) {
        this.emailToActivate = emailToActivate;
    }

    /**
     * @return the toCreate
     */
    public AdminUsr getToCreate() {
        return toCreate;
    }

    /**
     * @param toCreate the toCreate to set
     */
    public void setToCreate(AdminUsr toCreate) {
        this.toCreate = toCreate;
    }

    /**
     * @return the toCreatePassword
     */
    public String getToCreatePassword() {
        return toCreatePassword;
    }

    /**
     * @param toCreatePassword the toCreatePassword to set
     */
    public void setToCreatePassword(String toCreatePassword) {
        this.toCreatePassword = toCreatePassword;
    }

    /**
     * @return the toRegistPassword
     */
    public String getToRegistPassword() {
        return toRegistPassword;
    }

    /**
     * @param toRegistPassword the toRegistPassword to set
     */
    public void setToRegistPassword(String toRegistPassword) {
        this.toRegistPassword = toRegistPassword;
    }
   
}
